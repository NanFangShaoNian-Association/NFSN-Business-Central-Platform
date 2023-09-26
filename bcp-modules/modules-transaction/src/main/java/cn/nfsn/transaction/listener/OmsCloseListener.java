package cn.nfsn.transaction.listener;

import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.nfsn.transaction.constant.RabbitConstant.*;

import java.io.IOException;

/**
 * @ClassName: OmsCloseListener
 * @Description: 订单超时未支付取消监听器，监听延迟队列和关闭订单队列的消息。
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-20 16:32:04
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class OmsCloseListener {

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * 监听延迟队列的消息。如果在指定时间内未被消费，则转发到死信交换机。
     *
     * @param orderSn 订单编号
     * @param message 消息对象
     * @param channel 通道
     * @throws IOException 如果操作失败，会抛出IOException
     */
    @RabbitListener(bindings =
            {
                    @QueueBinding(
                            value = @Queue(value = ORDER_CLOSE_DELAY_DEAD_QUEUE,
                                    arguments =
                                            {
                                                    @Argument(name = "x-dead-letter-exchange", value = ORDER_DLX_EXCHANGE),
                                                    @Argument(name = "x-dead-letter-routing-key", value = ORDER_CLOSE_ROUTING_KEY),
                                                    @Argument(name = "x-message-ttl", value = "" + ORDER_EXPIRE_TIME, type = "java.lang.Long")
                                            }),
                            exchange = @Exchange(value = ORDER_EXCHANGE),
                            key = {ORDER_CLOSE_DELAY_ROUTING_KEY}
                    )
            }, ackMode = "MANUAL"
    )
    public void handleOrderCloseDelay(String orderSn, Message message, Channel channel) throws IOException {
        log.info("订单({})延时队列，10s内如果未支付将路由到关单队列", orderSn);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
    }

    /**
     * 监听关闭订单队列的消息，并处理超时未支付的订单。
     *
     * @param orderSn 订单编号
     * @param message 消息对象
     * @param channel 通道
     * @throws IOException 如果操作失败，会抛出IOException
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = ORDER_CLOSE_PROCESS_QUEUE, durable = "true"),
                    exchange = @Exchange(value = ORDER_DLX_EXCHANGE),
                    key = {ORDER_CLOSE_ROUTING_KEY}
            )
    }, ackMode = "MANUAL"
    )
    public void handleOrderClose(String orderSn, Message message, Channel channel) throws IOException {
        log.info("OmsCloseListener - 订单({})超时未支付，系统自动关闭订单", orderSn);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 在更新订单状态之前先检查当前订单的状态，如果已经支付成功，则不做任何操作；
            if(orderInfoService.getOrderStatus(orderSn).equals(OrderStatus.SUCCESS.getType())) {
                log.info("订单({})已支付，不做任何操作", orderSn);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 如果订单状态没有变成OrderStatus.SUCCESS，则将订单状态设置为OrderStatus.CLOSED
            orderInfoService.updateStatusByOrderNo(orderSn, OrderStatus.CLOSED);

            // TODO:释放库存

            // TODO:需要考虑网络抖动或者消费端处理能力不足导致的消息重复消费问题。可以通过业务层面的幂等性控制来解决。
            // 确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("关闭订单({})失败，原因：{}", orderSn, e.getMessage());

            // TODO:如果更新订单状态失败，这里选择了拒绝消息并将其放回队列。
            //  这样做可能会导致消息被无限次的重新消费，如果更新订单状态一直失败，那么这个消息可能永远也不会被消费掉，对资源造成浪费。
            //  需要对此进行优化，比如设置最大重试次数，超过则丢弃消息或者转发到其他队列进行处理。
            // 拒绝消息，并将其放回队列
            channel.basicReject(deliveryTag, true);
        }
    }
}