package cn.nfsn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.nfsn.common.core.domain.UserInfo;

/**
* @author gaojianjie
* @description 针对表【user_info】的数据库操作Mapper
* @createDate 2023-08-11 22:29:18
* @Entity generator.domain.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    public void recoverDefaultInfo(Integer userId);

    UserInfo queryUserInfoByEmail(String email,String appCode);

    UserInfo queryUserInfoByPhone(String phone,String appCode);
}




