package com.nfsn.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nfsn.common.core.constant.UserConstants;
import com.nfsn.common.core.domain.UserInfo;
import com.nfsn.mapper.UserInfoMapper;
import com.nfsn.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
* @author gaojianjie
* @description 针对表【user_info】的数据库操作Service实现
* @createDate 2023-08-11 22:29:18
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {
    @Override
    public UserInfo queryUserInfo(String userId) {
        return this.getById(userId);
    }

    @Override
    public boolean deRegistration(String userId) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<UserInfo>().eq(UserInfo::getUserId, userId).set(UserInfo::getUserStatus, UserConstants.DEREGISTRATION);
        return this.update(updateWrapper);
    }

    @Override
    public void registration(UserInfo userInfo) {

    }
}




