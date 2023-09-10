package cn.nfsn.common.rocketmq.service;

import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.core.utils.DateUtils;
import cn.nfsn.common.rocketmq.enums.EnumMessageSendModel;
import cn.nfsn.common.rocketmq.enums.EnumMessageStatus;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MQProducerService {

    /**
     * 消息超时时间
     */
    @Value("${rocketmq.producer.send-message-timeout}")
    private Integer messageTimeOut;

    /**
     * RocketMQ模板，用于发送消息到broker服务器
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送普通消息
     *
     * @param object 要发送的消息对象，可以是任意类型（可以发送个对象，也可以是字符串等）
     * @param topic 目标主题
     * @param tag 消息标签
     */
    public void send(Object object, String topic, String tag) {
        rocketMQTemplate.convertAndSend(topic + ":"+tag, object);
    }

    /**
     * 发送同步消息
     * 阻塞当前线程，等待broker响应发送结果，这样不太容易丢失消息
     *
     * @param msgBody 消息内容，也可以是对象
     * @param topic 目标主题
     * @return SendResult 返回的发送结果
     */
    public SendResult sendMsg(String msgBody,String topic) {
        return rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build());
    }

    /**
     * 发送异步消息
     * 通过线程池执行发送到broker的消息任务，执行完后回调：在SendCallback中可处理相关成功失败时的逻辑
     * 适合对响应时间敏感的业务场景
     *
     * @param msgBody 消息内容，也可以是对象
     * @param topic 目标主题
     * @param tag 消息标签
     */
    public void sendAsyncMsg(String msgBody,String topic,String tag) {
        rocketMQTemplate.asyncSend(topic + ":"+tag, MessageBuilder.withPayload(msgBody).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }
            @Override
            public void onException(Throwable throwable) {
            }
        });
    }

    /**
     * 发送延时消息
     * 上面的发送同步消息，delayLevel的值就为0，因为不延时
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 15d
     * 时间单位支持：s、m、h、d，分别表示秒、分、时、天
     *
     * @param msgBody 消息内容，也可以是对象
     * @param delayLevel 延迟等级
     * @param topic 目标主题
     * @param tag 消息标签
     */
    public void sendDelayMsg(String msgBody, int delayLevel,String topic,String tag) {
        rocketMQTemplate.syncSend(topic + ":"+tag, MessageBuilder.withPayload(msgBody).build(), messageTimeOut, delayLevel);
    }

    /**
     * 发送单向消息
     * 只负责发送消息，不等待应答，不关心发送结果，如日志
     *
     * @param msgBody 消息内容，也可以是对象
     * @param topic 目标主题
     */
    public void sendOneWayMsg(String msgBody,String topic) {
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(msgBody).build());
    }

    /**
     * 发送带tag的消息
     * 在topic后面加上":tag"
     *
     * @param msgBody 消息内容，也可以是对象
     * @param topic 目标主题
     * @param tag 消息标签
     * @return SendResult 返回的发送结果
     */
    public SendResult sendTagMsg(String msgBody,String topic,String tag) {
        return rocketMQTemplate.syncSend(topic + ":"+tag, MessageBuilder.withPayload(msgBody).build());
    }


    /**
     * 创建相关消息记录对象
     *
     * @param topic 消息主题
     * @param tag 消息标签
     * @param value 消息内容
     * @param serviceName 服务名称
     * @param businessName 业务名称
     * @return LocalMessageRecord 创建的消息记录对象
     */
    public LocalMessageRecord getMsgRecord(String topic, String tag,Object value, String serviceName, String businessName) {
        // 创建 LocalMessageRecord 对象
        LocalMessageRecord msgRecord = new LocalMessageRecord();

        // 设置消息主题
        msgRecord.setTopic(topic);

        // 设置消息标签
        msgRecord.setTags(tag);

        // 将消息内容转为 JSON 格式并设置
        msgRecord.setBody(JSONObject.toJSONString(value));

        // 设置服务名称
        msgRecord.setService(serviceName);

        // 设置业务名称
        msgRecord.setBusiness(businessName);

        // 设置消息发送模型为异步
        msgRecord.setModel(EnumMessageSendModel.ASYNC.getCode());

        // 设置创建时间为当前时间
        msgRecord.setCreateTime(DateUtils.getNowDate());

        // 设置消息状态为发送中
        msgRecord.setStatus(EnumMessageStatus.SENDING.getCode());

        // 设置重试次数为 0
        msgRecord.setCurrentRetryTimes(0);

        // 生成一个随机UUID作为消息键
        msgRecord.setMsgKey(UUID.randomUUID().toString());

        return msgRecord;
    }

    /**
     * MQ发送成功时，刷新消息记录为成功，并填充msgId
     *
     * @param messageRecord 消息记录
     * @param sendResult 发送结果
     * @return LocalMessageRecord 更新后的消息记录对象
     */
    public LocalMessageRecord asyncMsgRecordOnSuccessHandler(LocalMessageRecord messageRecord,SendResult sendResult){
        messageRecord.setMsgId(sendResult.getMsgId());
        messageRecord.setStatus(EnumMessageStatus.SUCCESS.getCode());
        messageRecord.setSendSuccessTime(DateUtils.getNowDate());
        return messageRecord;
    }

    /**
     * MQ发送失败时，刷新消息记录为测试中，MQ自动重试，重试次数++
     *
     * @param messageRecord 消息记录
     * @return LocalMessageRecord 更新后的消息记录对象
     */
    public LocalMessageRecord asyncMsgRecordOnFailHandler(LocalMessageRecord messageRecord){
        messageRecord.setStatus(EnumMessageStatus.RETRYING.getCode());
        messageRecord.setCurrentRetryTimes(messageRecord.getCurrentRetryTimes()+1);
        return messageRecord;
    }
}
