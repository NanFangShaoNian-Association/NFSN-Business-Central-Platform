package cn.nfsn.system.strategy.impl;

import cn.nfsn.common.core.domain.dto.VerifyCodeDTO;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.UserOperateException;
import cn.nfsn.common.core.utils.ValidatorUtil;
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
        checkCertificate(account);
        //todo 线程池异步发送
        smsUtil.sendMsg(account,code,subject);
    }

    @Override
    public void saveVerificationCodeToRedis(VerifyCodeDTO verifyCodeDTO,String code) {
        String prefix = CertificateStrategy.getPrefixByType(verifyCodeDTO.getFunctionType());
        checkCertificate(verifyCodeDTO.getAccount());
        redisCache.setCacheObject(prefix+CacheConstants.PHONE_KEY+verifyCodeDTO.getAccount(),code,CacheConstants.MESSAGE_CODE_TIME_OUT, TimeUnit.MINUTES);
    }

    private void checkCertificate(String certificate){
        if(!ValidatorUtil.isMobile(certificate)){
            throw new UserOperateException(ResultCode.PHONE_NUM_NON_COMPLIANCE);
        }
    }

}
