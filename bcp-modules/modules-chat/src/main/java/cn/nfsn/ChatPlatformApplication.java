package cn.nfsn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 16:09
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChatPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatPlatformApplication.class,args);
    }
}
