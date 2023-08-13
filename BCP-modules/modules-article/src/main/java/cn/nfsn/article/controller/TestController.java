package cn.nfsn.article.controller;

import cn.nfsn.api.article.TestRemoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 17:08
 */
@RestController
public class TestController implements TestRemoteService {
    @GetMapping("/testRemoteService")
    public String testRemoteService(){
        return "SUCCESS";
    }
}
