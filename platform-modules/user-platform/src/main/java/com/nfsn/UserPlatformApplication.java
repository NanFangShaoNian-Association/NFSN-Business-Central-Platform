package com.nfsn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 16:09
 */
@SpringBootApplication
@EnableFeignClients
@EnableOpenApi
public class UserPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserPlatformApplication.class,args);
    }
}
