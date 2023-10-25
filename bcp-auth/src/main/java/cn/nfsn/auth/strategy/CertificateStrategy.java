package cn.nfsn.auth.strategy;

import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.common.core.domain.UserInfo;
import cn.nfsn.common.core.domain.dto.LoginRequestDTO;

/**
 * @Author: gaojianjie
 * @date 2023/9/11 08:23
 */
public interface CertificateStrategy {
    String getCodeFromRedis(String certificate);
    UserInfo getUserInfoByCertificate(LoginRequestDTO loginRequestDTO);
    AuthCredentials getAuthCredentialsByCertificate(String certificate);
    void createCredentialsByCertificate(String certificate);
}
