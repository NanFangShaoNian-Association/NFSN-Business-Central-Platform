package cn.nfsn.auth.factory;

import cn.nfsn.common.core.enums.CertificateMethodEnum;
import cn.nfsn.auth.strategy.CertificateStrategy;
import cn.nfsn.auth.strategy.impl.EmailCertificateStrategy;
import cn.nfsn.auth.strategy.impl.PhoneCertificateStrategy;
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
    private PhoneCertificateStrategy phoneLoginStrategy;
    @Autowired
    private EmailCertificateStrategy emailLoginStrategy;

    public CertificateStrategy getLoginStrategy(CertificateMethodEnum certificateMethodEnum) {
        return createLoginStrategy(certificateMethodEnum);
    }
    private CertificateStrategy createLoginStrategy(CertificateMethodEnum certificateMethodEnum) {

        switch (certificateMethodEnum) {
            case EMAIL_CODE:
                return emailLoginStrategy;
            case PHONE_CODE:
                return phoneLoginStrategy;
            default:
                throw new IllegalArgumentException("Unsupported login method: " + certificateMethodEnum);
        }
    }
}
