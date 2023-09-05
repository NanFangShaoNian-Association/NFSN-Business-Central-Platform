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

	@Value("${rocketmq.producer.send-message-timeout}")
    public Integer messageTimeOut;
    
	// 直接注入使用，用于发送消息到broker服务器
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

	/**
     * 普通发送（这里的参数对象Object可以随意定义，可以发送个对象，也可以是字符串等）
     */
    public void send(Object object,String topic,String tag) {
        rocketMQTemplate.convertAndSend(topic + ":"+tag, object);
//        rocketMQTemplate.send(topic + ":tag1", MessageBuilder.withPayload(user).build()); // 等价于上面一行
    }

    /**
     * 发送同步消息（阻塞当前线程，等待broker响应发送结果，这样不太容易丢失消息）
     * （msgBody也可以是对象，sendResult为返回的发送结果）
     */
    public SendResult sendMsg(String msgBody,String topic) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build());
//        log.info("【sendMsg】sendResult={}", JSON.toJSONString(sendResult));
        return sendResult;
    }

	/**
     * 发送异步消息（通过线程池执行发送到broker的消息任务，执行完后回调：在SendCallback中可处理相关成功失败时的逻辑）
     * （适合对响应时间敏感的业务场景）
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
     * 发送延时消息（上面的发送同步消息，delayLevel的值就为0，因为不延时）
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 15d
     * 时间单位支持：s、m、h、d，分别表示秒、分、时、天
     * 默认值就是上面声明的，可手工调整
     */
    public void sendDelayMsg(String msgBody, int delayLevel,String topic,String tag) {
        rocketMQTemplate.syncSend(topic + ":"+tag, MessageBuilder.withPayload(msgBody).build(), messageTimeOut, delayLevel);
    }

    /**
     * 发送单向消息（只负责发送消息，不等待应答，不关心发送结果，如日志）
     */
    public void sendOneWayMsg(String msgBody,String topic) {
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(msgBody).build());
    }
    
	/**
     * 发送带tag的消息，直接在topic后面加上":tag"
     */
    public SendResult sendTagMsg(String msgBody,String topic,String tag) {
        return rocketMQTemplate.syncSend(topic + ":"+tag, MessageBuilder.withPayload(msgBody).build());
    }


    /**
     *返回创建的相关消息记录对象
     */
    public LocalMessageRecord getMsgRecord(String topic, String tag,Object value, String serviceName, String businessName) {
        LocalMessageRecord msgRecord = new LocalMessageRecord();
        msgRecord.setTopic(topic);
        msgRecord.setTags(tag);
        msgRecord.setBody(JSONObject.toJSONString(value));
        msgRecord.setService(serviceName);
        msgRecord.setBusiness(businessName);
        msgRecord.setModel(EnumMessageSendModel.ASYNC.getCode());
        msgRecord.setCreateTime(DateUtils.getNowDate());
        msgRecord.setStatus(EnumMessageStatus.SENDING.getCode());
        msgRecord.setCurrentRetryTimes(0);
        msgRecord.setMsgKey(UUID.randomUUID().toString());
        return msgRecord;
    }

    /**
     * MQ发送成功刷新消息记录为成功 并填充msgId
     */
    public LocalMessageRecord asyncMsgRecordOnSuccessHandler(LocalMessageRecord messageRecord,SendResult sendResult){
        messageRecord.setMsgId(sendResult.getMsgId());
        messageRecord.setStatus(EnumMessageStatus.SUCCESS.getCode());
        messageRecord.setSendSuccessTime(DateUtils.getNowDate());
        return messageRecord;
    }

    /**
     *MQ发送失败刷新消息记录为测试中  MQ自动重试 重试次数++
     */
    public LocalMessageRecord asyncMsgRecordOnFailHandler(LocalMessageRecord messageRecord){
        messageRecord.setStatus(EnumMessageStatus.RETRYING.getCode());
        messageRecord.setCurrentRetryTimes(messageRecord.getCurrentRetryTimes()+1);
        return messageRecord;
    }
    
}
