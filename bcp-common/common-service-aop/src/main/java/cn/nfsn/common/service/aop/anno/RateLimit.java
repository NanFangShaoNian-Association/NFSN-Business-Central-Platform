package cn.nfsn.common.service.aop.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RateLimit
 * @Description: 用于方法级别的速率限制注解，可以控制接口的请求频率。
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-21 21:24
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    /**
     * 表示资源名称，用于标识不同的资源
     *
     * @return String 资源名称
     */
    String resourceName();

    /**
     * 表示令牌桶的初始容量，即最初令牌桶中的令牌数量
     *
     * @return int 初始容量
     */
    int initialCapacity();

    /**
     * 表示令牌桶单位时间内的填充速率，即每个单位时间内向令牌桶中添加的令牌数
     *
     * @return int 填充速率
     */
    int refillRate();

    /**
     * 表示令牌桶填充的时间单位，用于定义填充速率的时间范围
     *
     * @return TimeUnit 时间单位
     */
    TimeUnit refillTimeUnit();
}
