package cn.nfsn.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.nfsn.api.user.RemoteUserInfoService;
import cn.nfsn.auth.factory.CertificateStrategyFactory;
import cn.nfsn.auth.strategy.CertificateStrategy;
import cn.nfsn.common.core.constant.UserConstants;
import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.common.core.domain.UserInfo;
import cn.nfsn.common.core.domain.dto.LoginRequestDTO;
import cn.nfsn.common.core.domain.vo.UserInfoVO;
import cn.nfsn.common.core.enums.CertificateMethodEnum;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import cn.nfsn.common.core.exception.UserOperateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author: gaojianjie
 * @date 2023/9/7 08:27
 */
@Service
public class SsoService {
    @Autowired
    private RemoteUserInfoService remoteUserInfoService;
    @Autowired
    private CertificateStrategyFactory certificateStrategyFactory;
    public void SsoLoginByCodeHandler(LoginRequestDTO loginRequestDTO) throws IOException {
        CertificateStrategy certificateStrategy = certificateStrategyFactory.getLoginStrategy(CertificateMethodEnum.fromCode(loginRequestDTO.getLoginMethod()));

        String code = certificateStrategy.getCodeFromRedis(loginRequestDTO.getCertificate());
        if(!loginRequestDTO.getVerifyCode().equals(code)){
            //验证码校验失败
            throw new UserOperateException(ResultCode.USER_VERIFY_ERROR);
        }
        //redisCache.deleteObject(loginRequestDTO.getCertificate());
        // 1.如未注册进行注册 2.如注销状态取消注销
        checkUserLogoutStatus(registrationHandler(certificateStrategy, loginRequestDTO));
        UserInfo userInfo = certificateStrategy.getUserInfoByCertificate(loginRequestDTO);
        if(Objects.isNull(userInfo)){
            //log.error
            throw new SystemServiceException(ResultCode.SERVER_BUSY);
        }
        StpUtil.login(userInfo.getCredentialsId());
    }

    /**
     * 检查账户是否注册，没有则进行账户创建以及对应应用信息的默认初始化
     * 如账户存在而对应应用信息不存在则进行应用信息的默认初始化
     */
    private UserInfo registrationHandler(CertificateStrategy certificateStrategy, LoginRequestDTO loginRequestDTO) throws IOException {
        AuthCredentials credentials = certificateStrategy.getAuthCredentialsByCertificate(loginRequestDTO.getCertificate());
        if(Objects.isNull(credentials)){
            certificateStrategy.createCredentialsByCertificate(loginRequestDTO.getCertificate());
            UserInfo userInfo = new UserInfo();
            userInfo.setCredentialsId(certificateStrategy.getAuthCredentialsByCertificate(loginRequestDTO.getCertificate()).getCredentialsId());
            userInfo.setAppCode(loginRequestDTO.getAppCode());
            remoteUserInfoService.registration(userInfo);
            return null;
        }
        UserInfo userInfo = certificateStrategy.getUserInfoByCertificate(loginRequestDTO);
        if(Objects.isNull(userInfo)){
            userInfo = new UserInfo();
            userInfo.setCredentialsId(credentials.getCredentialsId());
            userInfo.setAppCode(loginRequestDTO.getAppCode());
            remoteUserInfoService.registration(userInfo);
            return null;
        }
        return userInfo;
    }

    private void checkUserLogoutStatus(UserInfo userInfo){
        //取消注销操作
        if (!Objects.isNull(userInfo)&&UserConstants.LOGOUT.equals(userInfo.getLogoutStatus().toString())) {
            userInfo.setLogoutStatus(Integer.valueOf(UserConstants.CANCEL_LOGOUT));
            remoteUserInfoService.updateUserInfo(userInfo);
        }
    }

    public UserInfoVO queryUserInfoByCredentialsId(String credentialsId, String appCode) {
        UserInfo userInfo = remoteUserInfoService.getUserInfoByCredentialsId(appCode, credentialsId).getData();
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfo,userInfoVO);
        return userInfoVO;
    }
}
