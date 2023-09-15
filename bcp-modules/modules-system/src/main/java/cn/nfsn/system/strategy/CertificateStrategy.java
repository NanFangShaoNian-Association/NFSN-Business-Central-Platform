package cn.nfsn.system.strategy;

/**
 * @Author: gaojianjie
 * @date 2023/9/11 08:23
 */
public interface CertificateStrategy {
    void sendCodeHandler(String account,String code,String subject);

    void saveVerificationCodeToRedis(String account,String code);

}
