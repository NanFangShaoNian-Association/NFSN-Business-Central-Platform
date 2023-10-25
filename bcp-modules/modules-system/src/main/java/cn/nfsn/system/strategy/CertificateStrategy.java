package cn.nfsn.system.strategy;

import cn.nfsn.common.core.constant.UserConstants;
import cn.nfsn.common.core.domain.dto.VerifyCodeDTO;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import cn.nfsn.common.redis.constant.CacheConstants;

/**
 * @Author: gaojianjie
 * @date 2023/9/11 08:23
 */
public interface CertificateStrategy {
    void sendCodeHandler(String account,String code,String subject);

    void saveVerificationCodeToRedis(VerifyCodeDTO verifyCodeDTO,String code);

     static String getPrefixByType(Integer integer) {
         switch (integer) {
             case UserConstants.PREFIX_TYPE_1:
                 return CacheConstants.LOGIN_CODE_KEY;
             case UserConstants.PREFIX_TYPE_2:
                 return CacheConstants.BINDING_CODE_KEY;
             default:
                 throw new SystemServiceException(ResultCode.PARAM_IS_INVALID);
         }
     }
}
