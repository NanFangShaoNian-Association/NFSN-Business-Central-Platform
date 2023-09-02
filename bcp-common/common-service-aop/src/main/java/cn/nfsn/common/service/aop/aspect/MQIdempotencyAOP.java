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

    @Around("@annotation(cn.nfsn.common.service.aop.anno.MQIdempotency)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        MqMessage mqMessage = findMqMessageParameter(args);
        if(Objects.isNull(mqMessage)){
            //log 消息投递失败
            return null;
        }
        // 这里使用分布锁保证其线程安全（这里很关键）
        String lockKey = "Lock-" + mqMessage.getMessageKey();
        RLock lock = RedisLock.getLock(redissonClient, lockKey);
        boolean res = RedisLock.lock(lock,3,300);
        if(!res) {
            throw new SystemServiceException(ResultCode.SERVER_BUSY);
        }
        Optional<String> cacheValue = Optional.ofNullable(redisCache.getCacheObject(CacheConstants.MQ_IDEMPOTENCY+mqMessage.getMessageKey()));
        if (!cacheValue.isPresent()) {
            try {
                Object o = pjp.proceed();
                MethodSignature signature = (MethodSignature) pjp.getSignature();
                MQIdempotency  idempotency = signature.getMethod().getAnnotation(MQIdempotency.class);
                // 默认300000毫秒内视为重复提交
                redisCache.setCacheObject(CacheConstants.MQ_IDEMPOTENCY+mqMessage.getMessageKey(), "待定", idempotency.expire(), TimeUnit.MILLISECONDS);
                return o;
            }catch (Exception e){
                throw new SystemServiceException(ResultCode.INTERNAL_ERROR);
            } finally {
                RedisLock.unlock(lock);
            }
        } else {
            RedisLock.unlock(lock);
            throw new SystemServiceException(ResultCode.IDEMPOTENCY_ERROR);
        }
    }
    private MqMessage findMqMessageParameter(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof MqMessage) {
                return (MqMessage) arg;
            }
        }
        return null;
    }
}
