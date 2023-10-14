package cn.nfsn;

import cn.nfsn.chat.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @Author: gaojianjie
 * @Description 消息中心主启动程序
 * @date 2023/8/10 16:09
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableOpenApi
public class ChatPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatPlatformApplication.class, args);
        try {
            new NettyServer(12345).start();
            System.out.println("https://blog.csdn.net/moshowgame");
            System.out.println("http://127.0.0.1:6688/netty-websocket/index");
        }catch(Exception e) {
            System.out.println("NettyServerError:"+e.getMessage());
        }
    }
}