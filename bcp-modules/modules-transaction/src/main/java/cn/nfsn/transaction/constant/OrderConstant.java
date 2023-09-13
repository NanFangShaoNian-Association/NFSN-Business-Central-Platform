package cn.nfsn.transaction.constant;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: OrderConstant
 * @Description: 包含了一些和订单处理相关的常量
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/12 0012 11:08
 **/
public class OrderConstant {
    /**
     * 订单锁名前缀
     */
    public static final String ORDER_LOCK_PREFIX = "ORDER_LOCK_";

    /**
     * 获取锁的最大等待时间
     */
    public static final long MAX_WAIT_TIME = 3L;

    /**
     * 锁自动释放时间
     */
    public static final long EXPIRE_TIME = 5L;

    /**
     * 时间单位
     */
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * order_info表的AppID字段名
     */
    public static final String APP_ID = "app_id";

    /**
     * order_info表的订单状态字段名
     */
    public static final String ORDER_STATUS = "order_status";

    /**
     * order_info表的用户ID字段名
     */
    public static final String USER_ID = "user_id";

    /**
     * order_info表的支付类型字段名
     */
    public static final String PAYMENT_TYPE = "payment_type";

    /**
     * order_info表的订单编号字段名
     */
    public static final String ORDER_NO = "order_no";

    /**
     * order_info表的二维码url字段名
     */
    public static final String CODE_URL = "code_url";

    /**
     * 订单号前缀
     */
    public static final String ORDER_PREFIX = "ORDER_";

    /**
     * 退款单号前缀
     */
    public static final String REFUND_PREFIX = "REFUND_";
}
