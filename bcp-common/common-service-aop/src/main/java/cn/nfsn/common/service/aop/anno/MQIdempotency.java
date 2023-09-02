package cn.nfsn.common.service.aop.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: gaojianjie
 * @Description MQ消息幂等性
 * @date 2023/9/1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MQIdempotency {
    /**
     * 过期时长（毫秒）
     * 5分钟
     */
    int expire() default 300000;
}
