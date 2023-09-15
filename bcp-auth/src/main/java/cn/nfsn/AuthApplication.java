package cn.nfsn;

import cn.nfsn.common.security.config.SecurityRedisConfig;
import cn.nfsn.common.security.handler.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @Author: gaojianjie
 * @Description 权限认证启动类
 * @date 2023/9/6 21:50
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Import({SecurityRedisConfig.class, GlobalExceptionHandler.class})
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class,args);
    }
}
