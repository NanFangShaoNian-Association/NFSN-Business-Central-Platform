package cn.nfsn.common.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {

    /**
     * Redisson客户端注册
     * 单机模式
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient createRedissonClient() throws IOException {

        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://39.108.67.16:6379");
        singleServerConfig.setPassword("nfsn-redis-6379");
        singleServerConfig.setTimeout(3000);
        return Redisson.create(config);
    }
}