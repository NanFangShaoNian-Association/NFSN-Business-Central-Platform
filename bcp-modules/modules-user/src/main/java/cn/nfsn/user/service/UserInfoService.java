package cn.nfsn.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.nfsn.common.core.domain.UserInfo;

/**
* @author gaojianjie
* @description 针对表【user_info】的数据库操作Service
* @createDate 2023-08-11 22:29:18
*/
public interface UserInfoService extends IService<UserInfo> {
    UserInfo queryUserInfo(String userId);

    void logout(String userId);

    void registration(UserInfo userInfo);

    void updateUserInfo(UserInfo userInfo);

    UserInfo checkPhoneNumbExit(String phoneNumber);
}

