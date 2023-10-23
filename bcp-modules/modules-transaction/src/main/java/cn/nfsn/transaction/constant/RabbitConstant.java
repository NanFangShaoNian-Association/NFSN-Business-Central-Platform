package cn.nfsn.transaction.constant;

/**
 * @ClassName: RabbitConstant
 * @Description: 包含了一些和Rabbit相关的常量
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/20 0020 15:43
 **/
public class RabbitConstant {
    /**
     * 普通延迟队列名称
     */
    public static final String ORDER_QUEUE = "order.delay.queue";

    /**
     * 延迟队列路由键
     */
    public static final String ORDER_QUEUE_ROUTING_KEY = "order.queue.routingKey";

    /**
     * 交换机名称
     */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /**
     * 死信队列名称
     */
    public static final String ORDER_DLQ_QUEUE = "order.dlq.queue";

    /**
     * 死信交换机名称
     */
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";

    /**
     * 关闭订单路由键
     */
    public static final String ORDER_DLQ_ROUTING_KEY = "order.dlq.routingKey";

    /**
     * 订单超时时间：30分钟
     */
    public static final long ORDER_EXPIRE_TIME = 30 * 60 * 1000;
    //测试使用：10s
    //public static final long ORDER_EXPIRE_TIME = 10 * 1000;
}