package cn.nfsn.common.redis.constant;

/**
 * @Author: gaojianjie
 * @Description redis缓存常量
 * @date 2023/7/10 20:54
 */
public class CacheConstants {
    public static final String MQ_IDEMPOTENCY = "MQIdempotency:";
    public static final String REQ_IDEMPOTENCY = "ReqIdempotency:";
    public static final String LOGIN_EMAIL_CODE_KEY = "login:code:email:";
    public static final String LOGIN_PHONE_CODE_KEY = "login:code:phone:";
    //验证码失效时间15分钟
    public static final Integer MESSAGE_CODE_TIME_OUT = 999999;

}
