package com.nfsn.controller;

import com.nfsn.api.SysFileRemoteService;
import com.nfsn.common.core.domain.R;
import com.nfsn.service.MinioSysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件请求处理
 */
@RestController
public class SysFileController implements SysFileRemoteService {
    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private MinioSysFileService sysFileService;

    /**
     * 文件上传请求
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)
    {
        try
        {
            // 上传并返回访问地址
            String url = sysFileService.upload(file);
            return R.ok(url);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }
}