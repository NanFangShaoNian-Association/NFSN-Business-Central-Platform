package cn.nfsn.gateway;

import cn.dev33.satoken.SaManager;
import cn.nfsn.common.security.config.SecurityRedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @Author: gaojianjie
 * @Description 网关启动类
 * @date 2023/8/10 15:39
 */
@EnableDiscoveryClient
@SpringBootApplication
@Import(SecurityRedisConfig.class)
public class PlatformGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformGatewayApplication.class,args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }
}
