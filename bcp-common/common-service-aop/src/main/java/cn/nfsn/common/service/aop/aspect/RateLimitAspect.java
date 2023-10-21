package cn.nfsn.common.service.aop.aspect;

import cn.nfsn.common.redis.service.RedisCache;
import cn.nfsn.common.service.aop.anno.RateLimit;
import cn.nfsn.common.service.aop.service.DistributedRateLimiterNew;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static cn.nfsn.common.core.enums.ResultCode.SERVER_LIMIT;
import static cn.nfsn.common.service.aop.constant.RateLimiterConstant.REFILL_DURATION;

/**
 * @ClassName: RateLimitAspect
 * @Description: 基于 AOP 和 Redis 实现的限流切面类
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-21 21:24
 **/
@Aspect
@Component
public class RateLimitAspect {

    /**
     * 使用 RedisCache 工具类操作 Redis
     */
    @Resource
    private RedisCache redisCache;

    /**
     * 对使用了 RateLimit 注解的方法进行拦截，并处理其限流逻辑
     *
     * @param joinPoint 切入点
     * @param rateLimit 自定义注解，包含了限流相关的参数
     * @return 方法的返回值
     * @throws Throwable 在执行方法过程中可能会抛出的异常
     */
    @Around("@annotation(rateLimit)")
    public Object aroundRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        // 获取注解内的参数
        String resourceName = rateLimit.resourceName();
        int initialCapacity = rateLimit.initialCapacity();
        int refillRate = rateLimit.refillRate();
        TimeUnit refillTimeUnit = rateLimit.refillTimeUnit();

        // 创建并初始化一个分布式限流器
        DistributedRateLimiterNew distributedRateLimiterNew = new DistributedRateLimiterNew(redisCache, resourceName, initialCapacity, refillRate, REFILL_DURATION);

        // 如果当前请求不能被接受（即超过了设定的频率），则抛出异常
        if (!distributedRateLimiterNew.allowRequest()) {
            throw new RuntimeException(SERVER_LIMIT.getMessage());
        }

        // 否则正常执行被代理的方法，并返回其结果
        return joinPoint.proceed();
    }
}