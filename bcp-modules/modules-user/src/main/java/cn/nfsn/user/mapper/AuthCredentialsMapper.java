package cn.nfsn.user.mapper;

import cn.nfsn.common.core.domain.AuthCredentials;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author gaojianjie
* @description 针对表【auth_credentials】的数据库操作Mapper
* @createDate 2023-09-09 19:46:40
* @Entity generator.domain.AuthCredentials
*/
@Mapper
public interface AuthCredentialsMapper extends BaseMapper<AuthCredentials> {
}




