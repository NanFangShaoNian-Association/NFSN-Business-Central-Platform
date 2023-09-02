package cn.nfsn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/28 16:20
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.nfsn.system.mapper")
@EnableScheduling
@EnableAsync
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }
}

