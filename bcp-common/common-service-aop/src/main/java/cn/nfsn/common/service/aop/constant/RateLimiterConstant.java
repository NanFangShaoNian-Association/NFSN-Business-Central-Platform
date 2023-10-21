package cn.nfsn.common.service.aop.constant;

/**
 * @ClassName: RateLimiterConstant
 * @Description: 提供用于令牌桶限流的常量和参数配置
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-21 21:29
 **/
public class RateLimiterConstant {

    /**
     * 定义用于在 Redis 存储令牌数量的 Key 的前缀
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 定义用于在 Redis 存储上次填充令牌的时间的 Key 的前缀
     */
    public static final String LAST_REFILL_TIME_KEY = "last_refill_time:";

    /**
     * 定义令牌桶的最大容量
     */
    public static final long REFILL_DURATION = 10000L;

    /**
     * 令牌桶在 Redis 缓存中的过期时间（单位：分钟）
     */
    public static final Integer TOKEN_BUCKET_EXPIRE_TIME_MINUTES = 60;

    /**
     * 资源在 Redis 缓存中的过期时间（单位：秒）
     */
    public static final long RESOURCE_EXPIRE_TIME_SECONDS = 3600L;
}
