package cn.nfsn.system.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.nfsn.common.core.domain.dto.VerifyCodeDTO;
import cn.nfsn.common.core.enums.CertificateMethodEnum;
import cn.nfsn.system.factory.CertificateStrategyFactory;
import cn.nfsn.system.service.CodeService;
import cn.nfsn.system.strategy.CertificateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static cn.nfsn.common.core.constant.Constants.RANDOM_LENGTH;

/**
 * @Author: gaojianjie
 * @Description 短信服务
 * @date 2023/9/12 19:24
 */
@Service
public class CodeServiceImpl implements CodeService {
    @Autowired
    private CertificateStrategyFactory certificateStrategyFactory;
    @Override
    public void sendCode(VerifyCodeDTO verifyCodeDTO) {
        //生成验证码
        String code = RandomUtil.randomNumbers(RANDOM_LENGTH);
        //提取发送目标
        String account = verifyCodeDTO.getAccount();
        //提取主题信息
        String subject = verifyCodeDTO.getSubject();
        //发送验证码
        CertificateStrategy strategy = certificateStrategyFactory.getStrategy(CertificateMethodEnum.fromCode(verifyCodeDTO.getIdentity()));
        strategy.sendCodeHandler(account,code,subject);
        strategy.saveVerificationCodeToRedis(verifyCodeDTO,code);
    }
}
