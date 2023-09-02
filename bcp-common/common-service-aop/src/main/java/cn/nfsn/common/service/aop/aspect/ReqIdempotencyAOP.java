package cn.nfsn.common.service.aop.aspect;

import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import cn.nfsn.common.redis.constant.CacheConstants;
import cn.nfsn.common.redis.service.RedisCache;
import cn.nfsn.common.redis.service.RedisLock;
import cn.nfsn.common.service.aop.anno.ReqIdempotency;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: gaojianjie
 * @Description 请求接口幂等性切面
 * @date 2023/8/30
 */
@Aspect
@Component
public class ReqIdempotencyAOP {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(cn.nfsn.common.service.aop.anno.ReqIdempotency)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request =((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestId = request.getHeader("requestId");
        if(StringUtils.hasText(requestId)){
            throw new SystemServiceException(ResultCode.REQUEST_ID_NULL);
        }
        // 这里使用分布锁保证其线程安全（这里很关键）
        String lockKey = "Lock-" + requestId;
        RLock lock = RedisLock.getLock(redissonClient, lockKey);
        boolean res = RedisLock.lock(lock,3,300);
        if(!res) {
            throw new SystemServiceException(ResultCode.SERVER_BUSY);
        }
        Optional<String> cacheValue = Optional.ofNullable(redisCache.getCacheObject(CacheConstants.REQ_IDEMPOTENCY+requestId));
        if (!cacheValue.isPresent()) {
            try {
                Object o = pjp.proceed();
                MethodSignature signature = (MethodSignature) pjp.getSignature();
                ReqIdempotency  idempotency = signature.getMethod().getAnnotation(ReqIdempotency .class);
                // 默认300000毫秒内视为重复提交
                redisCache.setCacheObject(CacheConstants.REQ_IDEMPOTENCY+requestId, "待定", idempotency.expire(), TimeUnit.MILLISECONDS);
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
}
