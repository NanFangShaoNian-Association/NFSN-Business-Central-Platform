package cn.nfsn.common.redis.constant;

/**
 * @Author: gaojianjie
 * @Description redis缓存常量
 * @date 2023/7/10 20:54
 */
public class CacheConstants {
    /**
     * 冒号字符，常用于键的构造
     */
    public static final String MQ_IDEMPOTENCY = "MQIdempotency:";

    /**
     * 冒号字符，常用于键的构造
     */
    public static final String REQ_IDEMPOTENCY = "ReqIdempotency:";

    /**
     * 默认等待时长 1 毫秒
     */
    public static final long DEF_WAIT_TIME = 1L;

    /**
     * 默认过期时长 10 秒
     */
    public static final long DEF_EXPIRE_TIME = 1000 * 10L;

    /**
     * 默认重试次数
     */
    public static final Integer NO_TRY_COUNT = 0;

    /**
     * 默认重试休眠时长
     */
    public static final Long NO_TRY_SLEEP_TIME = 0L;
}
