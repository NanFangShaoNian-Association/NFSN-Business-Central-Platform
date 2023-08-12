package com.nfsn.controller;

import com.nfsn.api.SysFileRemoteService;
import com.nfsn.api.TestRemoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 16:29
 */
@Api("测试案例")
@RestController
public class TestController {
    @Autowired
    private TestRemoteService testRemoteService;
    @Autowired
    private SysFileRemoteService sysFileRemoteService;

    @ApiOperation("Feign测试接口")
    @GetMapping("/test")
    public String test(){
        return testRemoteService.testRemoteService();
    }

    @PostMapping("/upload")
    public String upLoad(MultipartFile file){
        return sysFileRemoteService.upload(file).getData();
    }
}
