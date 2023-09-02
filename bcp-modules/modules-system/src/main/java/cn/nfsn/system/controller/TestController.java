package cn.nfsn.system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String info;
    @Value("${redis.host}")
    private String host;

    @RequestMapping("/config/get")
    public String get() {
        return info +" "+host;
    }

}
