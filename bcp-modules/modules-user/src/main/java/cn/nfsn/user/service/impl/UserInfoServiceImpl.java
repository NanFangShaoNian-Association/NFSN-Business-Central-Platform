package cn.nfsn.user.service.impl;

import cn.nfsn.api.system.RemoteMsgRecordService;
import cn.nfsn.common.core.constant.Constants;
import cn.nfsn.common.core.constant.UserConstants;
import cn.nfsn.common.core.domain.LocalMessageRecord;
import cn.nfsn.common.core.domain.MqMessage;
import cn.nfsn.common.core.domain.UserInfo;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import cn.nfsn.common.core.exception.UserOperateException;
import cn.nfsn.common.core.utils.Base64ToMultipartFileUtils;
import cn.nfsn.common.core.utils.DateUtils;
import cn.nfsn.common.core.utils.RandomNameUtils;
import cn.nfsn.common.core.utils.StringUtils;
import cn.nfsn.common.minio.service.MinioSysFileService;
import cn.nfsn.common.rocketmq.constant.RocketMQConstants;
import cn.nfsn.common.rocketmq.service.MQProducerService;
import cn.nfsn.user.mapper.UserInfoMapper;
import cn.nfsn.user.service.UserInfoService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;
import java.util.UUID;

/**
* @author gaojianjie
* @description 针对表【user_info】的数据库操作Service实现
* @createDate 2023-08-11 22:29:18
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private MinioSysFileService minioSysFileService;

    @Autowired
    private MQProducerService mqProducerService;

    @Autowired
    private RemoteMsgRecordService remoteMsgRecordService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public UserInfo queryUserInfo(String userId) {
        UserInfo userInfo = this.getById(userId);
        checkUserStatus(userInfo.getUserStatus().toString());
        return userInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void logout(String userId) {
        //将注销状态标记为确认注销,实际上为注销
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<UserInfo>().eq(UserInfo::getUserId, userId).set(UserInfo::getLogoutStatus, UserConstants.LOGOUT);
        this.update(updateWrapper);
        //15天后进行注销逻辑
        MqMessage mqMessage = new MqMessage(UUID.randomUUID().toString(), userId);
        mqProducerService.sendDelayMsg(JSON.toJSONString(mqMessage), 5, RocketMQConstants.DELAY_TOPIC, RocketMQConstants.LOGOUT_DELAY_TAG);
        LocalMessageRecord messageRecord = mqProducerService.getMsgRecord(RocketMQConstants.DELAY_TOPIC, RocketMQConstants.LOGOUT_DELAY_TAG,mqMessage.getMessageBody(), Constants.USER_SERVICE, Constants.DELAY_LOGOUT);
        messageRecord.setScheduledTime(DateUtils.addDaysToDate(DateUtils.getNowDate(),15));
        remoteMsgRecordService.saveMsgRecord(messageRecord);
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

    @Override
    public UserInfo checkPhoneNumbExit(String phoneNumber) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhoneNumber, phoneNumber);
        return this.getOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserInfo(UserInfo userInfo) {
        checkUserStatus(userInfo.getUserStatus().toString());
        userInfo.setUserAvatar(setAvatarToRegistration(userInfo.getUserAvatar()));
        this.updateById(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registration(UserInfo userInfo) {
        Integer userId = checkSignupFromLogout(userInfo);
        userInfo.setUserName(setRandomName(userInfo.getUserName()));
        userInfo.setUserAvatar(setAvatarToRegistration(userInfo.getUserAvatar()));
        if (Objects.isNull(userId)) {
            this.save(userInfo);
        } else {
            userInfo.setUserId(userId);
            this.updateById(userInfo);
        }
    }


    private String setRandomName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        return RandomNameUtils.getRandomChineseCharacters();
    }

    public String setAvatarToRegistration(String avatarBase64) {
        final String[] base64Array = avatarBase64.split(",");
        String dataUir, data, url;
        if (base64Array.length > 1) {
            dataUir = base64Array[0];
            data = base64Array[1];
        } else {
            //根据base64代表的具体文件构建
            dataUir = UserConstants.AVATAR_FORMAT;
            data = base64Array[0];
        }
        Base64ToMultipartFileUtils multipartFile = new Base64ToMultipartFileUtils(data, dataUir);
        try {
            url = minioSysFileService.upload(multipartFile);
        } catch (Exception e) {
            log.error("MinIO获取URL服务异常");
            throw new SystemServiceException(ResultCode.INTERNAL_ERROR);
        }
        if (!org.springframework.util.StringUtils.hasText(url)) {
            log.error("MinIO获取URL服务异常");
            throw new SystemServiceException(ResultCode.INTERNAL_ERROR);
        }
        return url;
    }

    private void checkUserStatus(String status) {
        switch (status) {
            case UserConstants.USER_DISABLE:
                throw new SystemServiceException(ResultCode.ACCOUNT_FREEZE);
            case UserConstants.LOGOUT:
                throw new SystemServiceException(ResultCode.ACCOUNT_LOGOUT);
        }
    }

    /**
     * 校验注册账号是否是注销的账号
     */
    private Integer checkSignupFromLogout(UserInfo userInfo) {
        UserInfo exitUserInfo = checkPhoneNumbExit(userInfo.getPhoneNumber());
        if (Objects.isNull(exitUserInfo)) {
            return null;
        }
        if (!exitUserInfo.getUserStatus().toString().equals(UserConstants.LOGOUT)) {
            throw new UserOperateException(ResultCode.PHONE_NUM_REGISTERED);
        }
        //将已注销的用户信息恢复默认
        userInfoMapper.recoverDefaultInfo(userInfo.getUserId());
        return userInfo.getUserId();
    }
}



