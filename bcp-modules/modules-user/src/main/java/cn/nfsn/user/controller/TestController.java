package cn.nfsn.user.controller;

import cn.nfsn.api.article.TestRemoteService;
import cn.nfsn.api.system.RemoteMsgRecordService;
import cn.nfsn.common.core.constant.Constants;
import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.rocketmq.constant.RocketMQConstants;
import cn.nfsn.common.rocketmq.service.MQProducerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/10 16:29
 */
@Api("测试案例")
@RestController
public class TestController {
    @Autowired
    private TestRemoteService testRemoteService;
    @Autowired
    private MQProducerService mqProducerService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RemoteMsgRecordService remoteMsgRecordService;

    @Value("${swagger.title}")
    private String title;
    @Value("${redis.password}")
    private String host;

    @ApiOperation("Feign测试接口")
    @GetMapping("/test")
    public void test(){
        System.out.println(remoteMsgRecordService.queryFileStateMsgRecord().getData().toString());

//        return testRemoteService.testRemoteService();
    }

    @RequestMapping("/config/get")
    public String get() {
        return title+" "+host;
    }

    @GetMapping("/testLocalMsgRecord")
    @Transactional
    public void testLocalMsgRecord(){
        LocalMessageRecord messageRecord = mqProducerService.getMsgRecord(RocketMQConstants.DELAY_TOPIC, RocketMQConstants.LOGOUT_DELAY_TAG,"i am body", Constants.USER_SERVICE, Constants.DELAY_LOGOUT);
        remoteMsgRecordService.saveMsgRecord(messageRecord);
        System.out.println("something todo....");
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rocketMQTemplate.asyncSend(messageRecord.getTopic() + ":"+messageRecord.getTags(), MessageBuilder.withPayload(messageRecord.getBody()).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        remoteMsgRecordService.updateMsgRecord(mqProducerService.asyncMsgRecordOnSuccessHandler(messageRecord,sendResult));
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        //log.error
                        remoteMsgRecordService.updateMsgRecord(mqProducerService.asyncMsgRecordOnFailHandler(messageRecord));
                    }
                });
            }
        });
    }
}
