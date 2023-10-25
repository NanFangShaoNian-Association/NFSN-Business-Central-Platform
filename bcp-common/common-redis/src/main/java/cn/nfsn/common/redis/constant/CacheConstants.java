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
    public static final String REDIS_SEPARATOR = ":";

    /**
     * MQ幂等前缀
     */
    public static final String MQ_IDEMPOTENCY = "MQIdempotency:";

    /**
     * 请求幂等前缀
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

    /**
     * 线程池大小
     */
    public static final int THREAD_POOL_SIZE = 10;

    /**
     * 缓存空值的存活时间，单位为分钟
     */
    public static final Long CACHE_NULL_TTL = 2L;

    /**
     * 锁前缀
     */
    public static final String LOCK_KEY = "lock:";

    /**
     * 登陆注册验证码前缀
     */
    public static final String LOGIN_CODE_KEY = "login:code:";


    /**
     * 绑定验证码前缀
     */
    public static final String BINDING_CODE_KEY = "binding:code:";

    /**
     * 邮箱号前缀
     */
    public static final String EMAIL_KEY = "email:";

    /**
     * 手机号前缀
     */
    public static final String PHONE_KEY = "phone:";

    //验证码失效时间15分钟
    public static final Integer MESSAGE_CODE_TIME_OUT = 999999;

}