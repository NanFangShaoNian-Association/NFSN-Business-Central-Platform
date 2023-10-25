package cn.nfsn;

import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("cn.nfsn.article.mapper")
public class ArticlePlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticlePlatformApplication.class,args);
    }
}
