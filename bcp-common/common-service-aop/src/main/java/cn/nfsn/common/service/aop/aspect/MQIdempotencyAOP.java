package cn.nfsn.common.service.aop.aspect;

import cn.nfsn.common.core.domain.MqMessage;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import cn.nfsn.common.redis.constant.CacheConstants;
import cn.nfsn.common.redis.service.RedisCache;
import cn.nfsn.common.redis.service.RedisLock;
import cn.nfsn.common.service.aop.anno.MQIdempotency;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: gaojianjie
 * @Description MQ消费幂等性切面
 * @date 2023/8/30
 */
@Aspect
@Component
public class MQIdempotencyAOP {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 使用环绕通知处理带有@MQIdempotency注解的方法，保证其幂等性。
     *
     * @param pjp 连接点对象
     * @return 方法执行结果
     * @throws Throwable 抛出可预测的异常
     */
    @Around("@annotation(cn.nfsn.common.service.aop.anno.MQIdempotency)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 获取方法参数
        Object[] args = pjp.getArgs();

        // 查找MqMessage类型的参数
        MqMessage mqMessage = findMqMessageParameter(args);

        // 如果没有找到MqMessage参数，则记录日志并返回null
        if (Objects.isNull(mqMessage)) {
            // log 消息投递失败
            return null;
        }

        // 使用分布锁保证其线程安全
        // 生成锁的key
        String lockKey = "Lock-" + mqMessage.getMessageKey();

        // 从Redis中获取锁
        RLock lock = RedisLock.getLock(redissonClient, lockKey);

        // 尝试获取锁，最多等待3秒，锁有效期300秒
        boolean res = RedisLock.lock(lock, 3, 300);

        // 若未能获取到锁，抛出服务器繁忙的系统异常
        if (!res) {
            throw new SystemServiceException(ResultCode.SERVER_BUSY);
        }

        // 从缓存中获取幂等性标识
        Optional<String> cacheValue = Optional.ofNullable(
                redisCache.getCacheObject(CacheConstants.MQ_IDEMPOTENCY + mqMessage.getMessageKey()));

        // 若缓存中没有幂等性标识，说明是首次提交
        if (!cacheValue.isPresent()) {
            try {
                // 执行原方法
                Object o = pjp.proceed();

                // 获取方法签名
                MethodSignature signature = (MethodSignature) pjp.getSignature();

                // 获取当前方法上的@MQIdempotency注解
                MQIdempotency idempotency = signature.getMethod().getAnnotation(MQIdempotency.class);

                // 在缓存中设置幂等性标识，有效期为idempotency.expire()
                // 默认300000毫秒内视为重复提交
                redisCache.setCacheObject(
                        CacheConstants.MQ_IDEMPOTENCY + mqMessage.getMessageKey(),
                        "待定",
                        idempotency.expire(),
                        TimeUnit.MILLISECONDS);

                return o;
            } catch (Exception e) {
                // 若执行原方法过程中抛出异常，则抛出系统内部错误的系统异常
                throw new SystemServiceException(ResultCode.INTERNAL_ERROR);
            } finally {
                // 最终，无论如何都要释放锁
                RedisLock.unlock(lock);
            }
        } else {
            // 若缓存中已有幂等性标识，说明是重复提交，释放锁并抛出幂等性错误的系统异常
            RedisLock.unlock(lock);
            throw new SystemServiceException(ResultCode.IDEMPOTENCY_ERROR);
        }
    }

    /**
     * 在参数列表中查找MqMessage类型的参数。
     *
     * @param args 参数列表
     * @return MqMessage类型的参数，若未找到则返回null
     */
    private MqMessage findMqMessageParameter(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof MqMessage) {
                return (MqMessage) arg;
            }
        }
        return null;
    }

}
