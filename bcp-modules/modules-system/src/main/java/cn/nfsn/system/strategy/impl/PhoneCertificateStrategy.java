package cn.nfsn.system.strategy.impl;

import cn.nfsn.common.redis.constant.CacheConstants;
import cn.nfsn.common.redis.service.RedisCache;
import cn.nfsn.system.strategy.CertificateStrategy;
import cn.nfsn.system.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: gaojianjie
 * @date 2023/9/11 08:26
 */
@Component
public class PhoneCertificateStrategy implements CertificateStrategy {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void sendCodeHandler(String account, String code,String subject) {
        //todo 线程池异步发送
        smsUtil.sendMsg(account,code,subject);

    }

    @Override
    public void saveVerificationCodeToRedis(String account, String code) {
        redisCache.setCacheObject(CacheConstants.LOGIN_PHONE_CODE_KEY+account,code,CacheConstants.MESSAGE_CODE_TIME_OUT, TimeUnit.MINUTES);
    }

}
