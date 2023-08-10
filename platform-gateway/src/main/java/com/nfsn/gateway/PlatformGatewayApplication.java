package com.nfsn.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 15:39
 */
@EnableDiscoveryClient
@SpringBootApplication
public class PlatformGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformGatewayApplication.class,args);
    }
}
