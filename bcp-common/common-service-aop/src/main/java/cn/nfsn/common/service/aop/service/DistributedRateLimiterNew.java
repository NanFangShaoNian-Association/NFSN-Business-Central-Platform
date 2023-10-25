package cn.nfsn.common.service.aop.service;

import cn.nfsn.common.redis.service.RedisCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.nfsn.common.service.aop.constant.RateLimiterConstant.*;

/**
 * @ClassName: DistributedRateLimiterNew
 * @Description: 基于 Redis 实现的分布式限流器，采用令牌桶算法
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-21 21:24
 **/
@Slf4j
public class DistributedRateLimiterNew {

    /**
     * 定义用于操作 Redis 的工具类实例
     */
    private final RedisCache redisCache;

    /**
     * 定义资源名称
     */
    private final String resourceName;

    /**
     * 定义令牌桶的最大容量
     */
    private final int maxTokens;

    /**
     * 定义令牌桶的填充速率
     */
    private final int refillRate;

    /**
     * 定义令牌桶的填充间隔（单位：ms）
     */
    private final long refillInterval;

    /**
     * 构造函数
     *
     * @param redisCache     Redis 工具类
     * @param resourceName   资源名称
     * @param maxTokens      令牌桶容量
     * @param refillRate     令牌桶单位时间填充速率
     * @param refillInterval 令牌桶填充时间间隔，单位ms
     */
    public DistributedRateLimiterNew(RedisCache redisCache, String resourceName, int maxTokens, int refillRate, long refillInterval) {
        this.redisCache = redisCache;
        this.resourceName = resourceName;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.refillInterval = refillInterval;

        // 初始化令牌桶
        initializeTokenBucket();
    }

    /**
     * 判断是否允许请求。即判断当前的请求频率是否超过了设定的限制
     *
     * @return 如果没有超过设定的限制则返回 true，否则返回 false
     */
    public boolean allowRequest() {
        String key = RATE_LIMIT_KEY + resourceName;
        long currentTime = System.currentTimeMillis();

        // 获取当前令牌数量
        Integer tokenCountCache = redisCache.getCacheObject(key);
        int tokenCount = Objects.isNull(tokenCountCache) ? 0 : tokenCountCache;

        // 补充令牌
        long lastRefillTime = redisCache.getCacheObject(LAST_REFILL_TIME_KEY + resourceName);
        long timePassed = currentTime - lastRefillTime;
        int newTokens = (int) (timePassed * refillRate / refillInterval);
        tokenCount = Math.min(tokenCount + newTokens, maxTokens);

        log.info("扣除之前tokenCount:{}", tokenCount);

        // 判断是否允许请求
        if (tokenCount > 0) {
            tokenCount--;
            log.info("扣除之后tokenCount:{}", tokenCount);
            // 保存令牌桶数量
            redisCache.setCacheObject(key, tokenCount, TOKEN_BUCKET_EXPIRE_TIME_MINUTES, TimeUnit.MINUTES);
            // 保存最近一次更新token的时间
            redisCache.setCacheObject(LAST_REFILL_TIME_KEY + resourceName, System.currentTimeMillis(), TOKEN_BUCKET_EXPIRE_TIME_MINUTES, TimeUnit.MINUTES);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 初始化令牌桶。即在 Redis 中为该资源创建对应的记录，并设置初始值
     */
    private void initializeTokenBucket() {
        // 当资源为空时，则进行新建
        if (redisCache.getCacheObject(RATE_LIMIT_KEY + resourceName) == null) {
            // 保存最近一次更新token的时间
            redisCache.setCacheObject(LAST_REFILL_TIME_KEY + resourceName, System.currentTimeMillis(), TOKEN_BUCKET_EXPIRE_TIME_MINUTES, TimeUnit.MINUTES);
            // 保存令牌桶数量
            redisCache.setCacheObject(RATE_LIMIT_KEY + resourceName, maxTokens, TOKEN_BUCKET_EXPIRE_TIME_MINUTES, TimeUnit.MINUTES);
            // 设置过期时间，当长期不用则自动删除
            redisCache.expire(resourceName, RESOURCE_EXPIRE_TIME_SECONDS);
        }
    }
}
