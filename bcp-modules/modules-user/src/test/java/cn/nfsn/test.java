package cn.nfsn;

import cn.nfsn.api.system.RemoteMsgRecordService;
import cn.nfsn.common.core.constant.Constants;
import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.rocketmq.constant.RocketMQConstants;
import cn.nfsn.common.rocketmq.service.MQProducerService;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class test {
    @Autowired
    private RemoteMsgRecordService remoteMsgRecordService;
    @Autowired
    private MQProducerService mqProducerService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    @Transactional
    public void testLocalMsgRecord(){
        LocalMessageRecord messageRecord = mqProducerService.getMsgRecord(RocketMQConstants.LOGOUT_DELAY_TAG, RocketMQConstants.DELAY_TOPIC, Constants.USER_SERVICE, Constants.DELAY_LOGOUT);
        remoteMsgRecordService.saveMsgRecord(messageRecord);
        System.out.println("something todo....");
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rocketMQTemplate.asyncSend(messageRecord.getTopic() + ":"+messageRecord.getTags(), MessageBuilder.withPayload(messageRecord.getBody()).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        //MQ发送成功刷新消息记录为成功 并填充msgId
                        remoteMsgRecordService.updateMsgRecord(mqProducerService.asyncMsgRecordOnSuccessHandler(messageRecord,sendResult));
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        //log.error
                        //MQ发送失败刷新消息记录为测试中  MQ自动重试 重试次数++
                        remoteMsgRecordService.updateMsgRecord(mqProducerService.asyncMsgRecordOnFailHandler(messageRecord));
                    }
                });
            }
        });
    }

}