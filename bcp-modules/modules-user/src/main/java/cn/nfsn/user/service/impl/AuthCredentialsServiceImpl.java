package cn.nfsn.user.service.impl;

import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.user.mapper.AuthCredentialsMapper;
import cn.nfsn.user.service.AuthCredentialsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author gaojianjie
* @description 针对表【auth_credentials】的数据库操作Service实现
* @createDate 2023-09-09 19:46:40
*/
@Service
public class AuthCredentialsServiceImpl extends ServiceImpl<AuthCredentialsMapper, AuthCredentials>
    implements AuthCredentialsService {
    @Autowired
    private AuthCredentialsMapper authCredentialsMapper;

    @Override
    public AuthCredentials checkPhoneNumbExit(String phoneNumber) {
        LambdaQueryWrapper<AuthCredentials> queryWrapper = new LambdaQueryWrapper<AuthCredentials>().eq(AuthCredentials::getPhoneNumber, phoneNumber);
        return this.getOne(queryWrapper);
    }

    @Override
    public AuthCredentials queryAuthCredentialsByEmail(String email) {
        LambdaQueryWrapper<AuthCredentials> queryWrapper = new LambdaQueryWrapper<AuthCredentials>().eq(AuthCredentials::getEmail, email);
        return this.getOne(queryWrapper);
    }

    @Override
    public AuthCredentials queryAuthCredentialsByPhone(String phone) {
        LambdaQueryWrapper<AuthCredentials> queryWrapper = new LambdaQueryWrapper<AuthCredentials>().eq(AuthCredentials::getPhoneNumber, phone);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserCredentialsByPhone(String certificate) {
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setPhoneNumber(certificate);
        this.save(authCredentials);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserCredentialsByEmail(String certificate) {
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setEmail(certificate);
        this.save(authCredentials);
    }
}




