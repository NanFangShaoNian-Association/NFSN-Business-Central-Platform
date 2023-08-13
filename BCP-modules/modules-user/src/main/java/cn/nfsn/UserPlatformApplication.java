package cn.nfsn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 16:09
 */
@MapperScan("cn.nfsn.user.mapper")
@SpringBootApplication
@EnableOpenApi
@EnableDiscoveryClient
@EnableFeignClients
public class UserPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserPlatformApplication.class,args);
    }
}
