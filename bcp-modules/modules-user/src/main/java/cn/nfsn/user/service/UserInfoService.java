package cn.nfsn.user.service;

import cn.nfsn.common.core.domain.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
* @author gaojianjie
* @description 针对表【user_info】的数据库操作Service
* @createDate 2023-08-11 22:29:18
*/
public interface UserInfoService extends IService<UserInfo> {
    UserInfo queryUserInfo(String userId);

    void logout(String userId);

    void registration(UserInfo userInfo) throws IOException;

    void updateUserInfo(UserInfo userInfo);

    UserInfo queryUserInfoByEmail(String email,String appCode);

    UserInfo queryUserInfoByPhone(String phone,String appCode);

    UserInfo queryUserInfoByCredentialsId(String credentialsId, String appCode);
}

