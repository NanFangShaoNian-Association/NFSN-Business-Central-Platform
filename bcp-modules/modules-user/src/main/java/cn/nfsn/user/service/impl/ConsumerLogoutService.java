package cn.nfsn.user.service.impl;

import cn.nfsn.common.core.constant.UserConstants;
import cn.nfsn.common.core.domain.MqMessage;
import cn.nfsn.common.core.domain.UserInfo;
import cn.nfsn.common.rocketmq.constant.RocketMQConstants;
import cn.nfsn.common.service.aop.anno.MQIdempotency;
import cn.nfsn.user.service.UserInfoService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author: gaojianjie
 * @Description 延迟进行账号注销操作
 * @date 2023/8/21 15:48
 */
@Component
@RocketMQMessageListener(topic = RocketMQConstants.DELAY_TOPIC, selectorExpression = RocketMQConstants.LOGOUT_DELAY_TAG, consumerGroup = "Group1")
public class ConsumerLogoutService implements RocketMQListener<MqMessage> {
    @Autowired
    private UserInfoService userInfoService;
    @Override
    @MQIdempotency
    public void onMessage(MqMessage mqMessage) {
        UserInfo userInfo = userInfoService.queryUserInfo(mqMessage.getMessageBody());
        if(Objects.isNull(userInfo)){
            //todo log
            return;
        }
        //用户取消了注销
        if(UserConstants.NORMAL.equals(userInfo.getLogoutStatus().toString())||UserConstants.CANCEL_LOGOUT.equals(userInfo.getLogoutStatus().toString())){
            return;
        }
        userInfo.setUserStatus(Integer.valueOf(UserConstants.LOGOUT));
        userInfoService.updateById(userInfo);
    }
}
