package cn.nfsn.user.service;

import cn.nfsn.common.core.domain.AuthCredentials;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gaojianjie
* @description 针对表【auth_credentials】的数据库操作Service
* @createDate 2023-09-09 19:46:40
*/
public interface AuthCredentialsService extends IService<AuthCredentials> {
    AuthCredentials checkPhoneNumbExit(String phoneNumber);

    AuthCredentials queryAuthCredentialsByEmail(String email);

    AuthCredentials queryAuthCredentialsByPhone(String phone);

    void createUserCredentialsByPhone(String certificate);

    void createUserCredentialsByEmail(String certificate);
}
