package cn.nfsn.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class SecurityRedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
        lettuceConnectionFactory.setHostName("39.108.67.16");
        lettuceConnectionFactory.setPort(6379);
        lettuceConnectionFactory.setPassword("nfsn-redis-6379");
        lettuceConnectionFactory.setDatabase(0);
        lettuceConnectionFactory.setTimeout(10000); // 10 seconds
//        lettuceConnectionFactory.getPoolConfiguration().setMinIdle(0);
//        lettuceConnectionFactory.getPoolConfiguration().setMaxIdle(8);
//        lettuceConnectionFactory.getPoolConfiguration().setMaxTotal(8);
//        lettuceConnectionFactory.getPoolConfiguration().setMaxWaitMillis(-1);
        return lettuceConnectionFactory;
    }
}
