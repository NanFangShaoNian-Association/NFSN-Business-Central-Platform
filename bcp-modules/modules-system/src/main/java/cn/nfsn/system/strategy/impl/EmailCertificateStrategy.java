package cn.nfsn.system.strategy.impl;

import cn.nfsn.common.redis.constant.CacheConstants;
import cn.nfsn.common.redis.service.RedisCache;
import cn.nfsn.system.strategy.CertificateStrategy;
import cn.nfsn.system.utils.SendEmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: gaojianjie
 * @date 2023/9/11 08:26
 */
@Component
public class EmailCertificateStrategy implements CertificateStrategy {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SendEmailUtil sendEmailUtil;

    @Override
    public void sendCodeHandler(String account, String code,String subject) {
        sendEmailUtil.sendSimpleMail(account,subject,code);
    }

    @Override
    public void saveVerificationCodeToRedis(String account, String code) {
        redisCache.setCacheObject(CacheConstants.LOGIN_EMAIL_CODE_KEY+account,code,CacheConstants.MESSAGE_CODE_TIME_OUT, TimeUnit.MINUTES);
    }

}
