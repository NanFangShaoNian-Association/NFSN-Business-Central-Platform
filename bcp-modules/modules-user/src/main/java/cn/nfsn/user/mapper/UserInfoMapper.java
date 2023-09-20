package cn.nfsn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.nfsn.common.core.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author gaojianjie
* @description 针对表【user_info】的数据库操作Mapper
* @createDate 2023-08-11 22:29:18
* @Entity generator.domain.UserInfo
*/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    void recoverDefaultInfo(Integer userId);

    UserInfo queryUserInfoByEmail(String email,String appCode);

    UserInfo queryUserInfoByPhone(String phone,String appCode);
}




