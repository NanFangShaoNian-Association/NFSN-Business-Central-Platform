package cn.nfsn.transaction.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static cn.nfsn.transaction.constant.RabbitConstant.*;

/**
 * @ClassName: OrderRabbitConfig
 * @Description: 订单相关的RabbitMQ配置类，包含了创建交换机、队列、绑定关系和消息转换器等操作。
 * @Author: atnibamaitay
 * @CreateTime: 2023/09/20 15:50
 **/
@Component
@Slf4j
public class OrderRabbitConfig {

    /**
     * 创建一个名为ORDER_EXCHANGE的持久化且不自动删除的交换机。
     * 交换机是RabbitMQ的核心概念，用于接收生产者发送的消息并将它们路由到相应的队列。
     *
     * @return Exchange
     */
    @Bean
    public Exchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * 创建一个名为ORDER_DLX_EXCHANGE的持久化且不自动删除的死信交换机。
     * 死信交换机用于处理无法被正常消费的消息（例如：延时队列中的消息超时）。
     *
     * @return Exchange
     */
    @Bean
    public Exchange orderDlxExchange() {
        return new DirectExchange(ORDER_DLX_EXCHANGE, true, false);
    }

    /**
     * 创建一个带有过期时间、死信交换机和死信路由键的持久化延时队列ORDER_QUEUE。
     * 当队列中的消息超过设定的过期时间后，这些消息会被发送到指定的死信交换机并使用设定的死信路由键进行路由。
     *
     * @return Queue
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        // 设置死信交换机
        args.put("x-dead-letter-exchange", ORDER_DLX_EXCHANGE);
        // 设置死信路由Key
        args.put("x-dead-letter-routing-key", ORDER_DLQ_ROUTING_KEY);
        // 设置消息过期时间
        args.put("x-message-ttl", ORDER_EXPIRE_TIME);
        log.info("延时队列(order.delay.queue)创建");
        return new Queue(ORDER_QUEUE, true, false, false, args);
    }

    /**
     * 创建一个将延时队列与交换机按照指定路由键进行绑定的Binding。
     * 通过这种绑定关系，交换机可以知道如何根据路由键将消息路由到正确的队列。
     *
     * @return Binding
     */
    @Bean
    public Binding orderDelayQueueBinding() {
        return new Binding(ORDER_QUEUE, Binding.DestinationType.QUEUE, ORDER_EXCHANGE,
                ORDER_QUEUE_ROUTING_KEY, null);
    }

    /**
     * 创建一个名为ORDER_DLQ_QUEUE的持久化死信队列，用于接收延时队列的过期消息。
     * 当消息在延时队列中过期后，会被转发到此死信队列进行处理。
     *
     * @return Queue
     */
    @Bean
    public Queue orderCloseQueue() {
        log.info("死信队列(order.dlq.queue)创建");
        return new Queue(ORDER_DLQ_QUEUE, true, false, false);
    }

    /**
     * 创建一个将死信队列与死信交换机按照指定路由键进行绑定的Binding。
     * 通过这种绑定关系，死信交换机可以知道如何将过期消息路由到正确的死信队列。
     *
     * @return Binding
     */
    @Bean
    public Binding orderCloseQueueBinding() {
        return new Binding(ORDER_DLQ_QUEUE, Binding.DestinationType.QUEUE, ORDER_DLX_EXCHANGE,
                ORDER_DLQ_ROUTING_KEY, null);
    }

    /**
     * 创建一个消息转换器，用于在生产者发送消息和消费者接收消息时进行消息的序列化和反序列化操作。
     * 这里使用的是Jackson2JsonMessageConverter，它会将Java对象转换为JSON格式的消息。
     *
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
