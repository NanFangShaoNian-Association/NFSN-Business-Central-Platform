package cn.nfsn.system.factory;

import cn.nfsn.common.core.enums.CertificateMethodEnum;
import cn.nfsn.system.strategy.CertificateStrategy;
import cn.nfsn.system.strategy.impl.EmailCertificateStrategy;
import cn.nfsn.system.strategy.impl.PhoneCertificateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: gaojianjie
 * @Description
 * @date 2023/9/11 08:29
 */
@Component
public class CertificateStrategyFactory {
    @Autowired
    private PhoneCertificateStrategy phoneCertificateStrategy;
    @Autowired
    private EmailCertificateStrategy emailCertificateStrategy;

    public CertificateStrategy getStrategy(CertificateMethodEnum certificateMethodEnum) {
        return createCodeHandlerStrategy(certificateMethodEnum);
    }
    private CertificateStrategy createCodeHandlerStrategy(CertificateMethodEnum certificateMethodEnum) {

        switch (certificateMethodEnum) {
            case EMAIL_CODE:
                return emailCertificateStrategy;
            case PHONE_CODE:
                return phoneCertificateStrategy;
            default:
                throw new IllegalArgumentException("Unsupported account type: " + certificateMethodEnum);
        }
    }
}
