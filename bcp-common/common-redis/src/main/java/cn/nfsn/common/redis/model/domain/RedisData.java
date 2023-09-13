package cn.nfsn.common.redis.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: RedisData
 * @Description: 包含Redis数据的一些关键信息，过期时间和数据本身。
 * @Author: atnibamaitay
 * @CreateTime: 2023-05-23 21:46
 **/
@Data
public class RedisData {

    /**
     * 这个数据应该在何时过期。
     * 这个时间是一个 LocalDateTime 对象。
     * 如果时间到了，数据就应该被视为过期。
     */
    private LocalDateTime expireTime;

    /**
     * 这是存储在 Redis 中的实际数据。
     * 它被设置为对象类型，这样就可以存储任何类型的数据。
     */
    private Object data;
}
