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
    public static final String ORDER_CLOSE_DELAY_DEAD_QUEUE = "order.close.delay.dead.queue";

    /**
     * 延迟队列路由键
     */
    public static final String ORDER_CLOSE_DELAY_ROUTING_KEY = "order.close.delay";

    /**
     * 订单延迟交换机前缀
     */
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";

    /**
     * 订单延迟路由键前缀
     */
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.routingKey";

    /**
     * 交换机名称
     */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /**
     * 死信队列名称
     */
    public static final String ORDER_CLOSE_PROCESS_QUEUE = "order.close.process.queue";

    /**
     * 死信交换机名称
     */
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";

    /**
     * 关闭订单路由键
     */
    public static final String ORDER_CLOSE_ROUTING_KEY = "order.close";

    /**
     * 订单超时时间：30分钟
     */
    public static final long ORDER_EXPIRE_TIME = 30 * 60 * 1000;
}