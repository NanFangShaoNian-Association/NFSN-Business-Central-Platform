package cn.nfsn.common.minio.service;

import cn.nfsn.common.minio.domain.ObjectItem;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MinioSysFileService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String address;
    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 判断bucket是否存在，不存在则创建
     *
     * @param name 存储bucket名称
     */
    public void existBucket(String name) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建存储bucket
     *
     * @param bucketName 存储bucket名称
     * @return Boolean 是否创建成功
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储bucket
     *
     * @param bucketName 存储bucket名称
     * @return Boolean   是否删除成功
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param file 待上传的文件对象
     * @return 返回上传后的文件访问地址，如果上传失败则返回"Upload failed."
     */
    public String upload(MultipartFile file) {
        // 获取原始文件名
        String fileName = file.getOriginalFilename();

        // 将原始文件名分割成文件名和扩展名
        String[] split = fileName.split("\\.");

        // 在原始文件名后添加当前时间戳以防止重名
        if (split.length > 1) {
            // 如果文件有扩展名，则在文件名和扩展名之间添加时间戳
            fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
        } else {
            // 如果文件没有扩展名，则直接在文件名后添加时间戳
            fileName = fileName + System.currentTimeMillis();
        }

        InputStream in = null;
        try {
            // 获取文件输入流
            in = file.getInputStream();

            // 使用Minio客户端将文件上传到指定的存储桶中
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(in, in.available(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        } catch (Exception e) {
            // 如果在上传过程中发生异常，则打印堆栈轨迹并返回失败信息
            e.printStackTrace();
            return "Upload failed.";
        } finally {
            // 关闭文件输入流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 返回上传后的文件访问地址
        return address + "/" + bucketName + "/" + fileName;
    }

    /**
     * 下载文件
     *
     * @param fileName 需要下载的文件名
     * @return 返回包含文件内容的ResponseEntity对象，如果文件不存在或读取失败则返回null
     */
    public ResponseEntity<byte[]> download(String fileName) {
        // 初始化响应实体对象
        ResponseEntity<byte[]> responseEntity = null;

        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            // 通过Minio客户端获取指定存储桶中的文件输入流
            in = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());

            // 创建字节输出流
            out = new ByteArrayOutputStream();

            // 将文件输入流的内容复制到字节输出流中
            IOUtils.copy(in, out);

            // 获取字节输出流中的数据
            byte[] bytes = out.toByteArray();

            // 创建HTTP头信息
            HttpHeaders headers = new HttpHeaders();
            try {
                // 添加内容处理方式为附件，并设置附件的文件名（进行URL编码防止特殊字符影响）
                headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // 如果在URL编码过程中发生异常，则打印堆栈轨迹
                e.printStackTrace();
            }

            // 设置内容长度为字节数组的长度
            headers.setContentLength(bytes.length);

            // 设置内容类型为二进制流
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 设置可公开访问的头部字段
            headers.setAccessControlExposeHeaders(Arrays.asList("*"));

            // 封装响应实体，包括文件的字节数据、HTTP头信息和HTTP状态码
            responseEntity = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // 如果在获取文件或创建响应实体过程中发生异常，则打印堆栈轨迹
            e.printStackTrace();
        } finally {
            try {
                // 关闭输入流和输出流
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 返回响应实体对象
        return responseEntity;
    }

    /**
     * 查看文件对象
     *
     * @param bucketName 存储bucket名称
     * @return 存储bucket内文件对象信息
     */
    public List<ObjectItem> listObjects(String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<ObjectItem> objectItems = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                ObjectItem objectItem = new ObjectItem();
                objectItem.setObjectName(item.objectName());
                objectItem.setSize(item.size());
                objectItems.add(objectItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectItems;
    }

    /**
     * 批量删除文件对象
     *
     * @param bucketName 存储bucket名称
     * @param objects    对象名称集合
     */
    public Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> objects) {
        List<DeleteObject> dos = objects.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
        return results;
    }


}

