package com.nfsn.api;

import com.nfsn.api.interceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 17:14
 */
@FeignClient(value = "article-platform-service",configuration = FeignInterceptor.class)
public interface TestRemoteService {
    @GetMapping("/testRemoteService")
    public String testRemoteService();
}
