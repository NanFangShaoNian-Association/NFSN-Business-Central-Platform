package cn.nfsn.user.service.impl;

import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.common.core.domain.dto.BindingCertificateDTO;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.UserOperateException;
import cn.nfsn.common.redis.constant.CacheConstants;
import cn.nfsn.common.redis.service.RedisCache;
import cn.nfsn.user.mapper.AuthCredentialsMapper;
import cn.nfsn.user.service.AuthCredentialsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

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
    @Autowired
    private RedisCache redisCache;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindingPhoneById(BindingCertificateDTO bindingCertificateDTO) {
        String certificate = bindingCertificateDTO.getCertificate();
        if(!Objects.isNull(queryAuthCredentialsByPhone(certificate))){
            throw new UserOperateException(ResultCode.ACCOUNT_EXIST);
        }
        String code = redisCache.getCacheObject(CacheConstants.BINDING_CODE_KEY + CacheConstants.PHONE_KEY + bindingCertificateDTO.getCertificate());
        checkVerifyCodeCode(code,bindingCertificateDTO.getVerifyCode());
        Integer credentialsId = bindingCertificateDTO.getCredentialsId();
        LambdaUpdateWrapper<AuthCredentials> updateWrapper = new LambdaUpdateWrapper<AuthCredentials>().eq(AuthCredentials::getCredentialsId, credentialsId).set(AuthCredentials::getPhoneNumber, certificate);
        this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindingEmailById(BindingCertificateDTO bindingCertificateDTO) {
        String certificate = bindingCertificateDTO.getCertificate();
        if(!Objects.isNull(queryAuthCredentialsByEmail(certificate))){
            throw new UserOperateException(ResultCode.ACCOUNT_EXIST);
        }
        String code = redisCache.getCacheObject(CacheConstants.BINDING_CODE_KEY + CacheConstants.EMAIL_KEY + bindingCertificateDTO.getCertificate());
        checkVerifyCodeCode(code,bindingCertificateDTO.getVerifyCode());
        Integer credentialsId = bindingCertificateDTO.getCredentialsId();
        LambdaUpdateWrapper<AuthCredentials> updateWrapper = new LambdaUpdateWrapper<AuthCredentials>().eq(AuthCredentials::getCredentialsId, credentialsId).set(AuthCredentials::getEmail, certificate);
        this.update(updateWrapper);
    }

    private void checkVerifyCodeCode(String code,String verifyCode){
        if(!StringUtils.hasText(code)||!verifyCode.equals(code)){
            throw new UserOperateException(ResultCode.USER_VERIFY_ERROR);
        }
    }
}




