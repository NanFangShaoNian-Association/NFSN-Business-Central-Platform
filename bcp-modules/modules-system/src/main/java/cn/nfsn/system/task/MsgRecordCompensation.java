package cn.nfsn.system.task;

import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.rocketmq.enums.EnumMessageStatus;
import cn.nfsn.common.rocketmq.service.MQProducerService;
import cn.nfsn.system.mapper.LocalMessageRecordMapper;
import cn.nfsn.system.service.LocalMessageRecordService;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @Author: gaojianjie
 * @Description 本地消息表补偿任务
 * @date 2023/8/30 21:12
 */
@Component
public class MsgRecordCompensation {
    @Autowired
    private LocalMessageRecordMapper localMessageRecordMapper;
    @Autowired
    private LocalMessageRecordService localMessageRecordService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private MQProducerService mqProducerService;
    @Scheduled(cron="${time.cron}")   //每5秒执行一次
    @Async
    public void retrySendFailMsg(){
        List<LocalMessageRecord> retryMessageRecords = localMessageRecordMapper.queryFailStateMsgRecord();

        for (LocalMessageRecord message : retryMessageRecords) {
            if (message.getCurrentRetryTimes() < message.getMaxRetryTimes()) {
                // 重新发送消息
                rocketMQTemplate.asyncSend(message.getTopic() + ":"+message.getTags(), MessageBuilder.withPayload(message.getBody()).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        localMessageRecordService.updateMsgRecordByMsgKey(mqProducerService.asyncMsgRecordOnSuccessHandler(message,sendResult));
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        //log.error
                        System.out.println(throwable.getMessage());
                        localMessageRecordService.updateMsgRecordByMsgKey(mqProducerService.asyncMsgRecordOnFailHandler(message));
                    }
                });
            } else {
                // 超过最大重试次数，标记为发送失败
                message.setStatus(EnumMessageStatus.FAILED.getCode());
                localMessageRecordService.updateMsgRecordByMsgKey(message);
            }
        }
    }

}
