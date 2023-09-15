package cn.nfsn.common.core.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Base64FileReader {

    /**
     * 从文本文件中读取 Base64 字符串。
     *
     * @param filePath 文本文件的路径
     * @return 读取的 Base64 字符串，如果读取失败则返回 null
     */
    public static String readBase64FromFile(String filePath) {
        try {
            // 创建 FileReader 对象
            FileReader fileReader = new FileReader(filePath);

            // 创建 BufferedReader 对象以便逐行读取文件内容
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // 用于存储读取的文本内容
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // 逐行读取文本文件内容
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // 关闭文件读取器
            bufferedReader.close();

            // 返回读取的内容作为 Base64 字符串
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 读取失败时返回 null
        }
    }
}