package cn.nfsn.api.article;

import cn.nfsn.api.article.interceptor.ArticleFeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 17:14
 */
@FeignClient(value = "modules-article",configuration = ArticleFeignInterceptor.class)
public interface TestRemoteService {
    @GetMapping("/testRemoteService")
    public String testRemoteService();
}
