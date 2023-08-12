package com.nfsn.api;

import com.nfsn.api.interceptor.FeignInterceptor;
import com.nfsn.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/12 20:30
 */
@FeignClient(value = "file-service",configuration = FeignInterceptor.class)
public interface SysFileRemoteService {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<String> upload(@RequestPart(value = "file")MultipartFile file);
}
