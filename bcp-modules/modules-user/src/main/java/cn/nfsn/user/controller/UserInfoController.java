package cn.nfsn.user.controller;

import cn.nfsn.api.user.RemoteUserInfoService;
import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.common.core.domain.R;
import cn.nfsn.common.core.domain.UserInfo;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.UserOperateException;
import cn.nfsn.common.core.utils.StringUtils;
import cn.nfsn.user.service.AuthCredentialsService;
import cn.nfsn.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author: gaojianjie
 * @Description 用户基本信息控制层
 * @date 2023/8/11 22:37
 */
//demo
@RestController
@RequestMapping("/userInfo")
public class UserInfoController implements RemoteUserInfoService {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AuthCredentialsService authCredentialsService;

    @RequestMapping(method = RequestMethod.GET,value = "/{userId}")
    public R<UserInfo> queryUserInfo(@PathVariable(value = "userId") String userId){
        return R.ok(userInfoService.queryUserInfo(userId));
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{userId}")
    public R logout(@PathVariable(value = "userId") String userId){
        userInfoService.logout(userId);
        return R.ok();
    }

    @RequestMapping(method = RequestMethod.POST)
    public R<String> registration(@Validated @RequestBody UserInfo userInfo) throws IOException {
        userInfoService.registration(userInfo);
        return R.ok();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public R updateUserInfo(@Validated @RequestBody UserInfo userInfo){
        if(Objects.isNull(userInfo.getUserId())){
            //todo log
            throw new UserOperateException(ResultCode.PARAM_NOT_COMPLETE);
        }
        if(!StringUtils.hasText(userInfo.getUserName())){
            //todo log
            throw new UserOperateException(ResultCode.PARAM_NOT_COMPLETE);
        }
        userInfoService.updateUserInfo(userInfo);
        return R.ok();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/phone-numbers/{phoneNumber}/exists")
    public R<Boolean> checkPhoneNumbExit(@PathVariable(value = "phoneNumber") String phoneNumber){
        AuthCredentials result = authCredentialsService.checkPhoneNumbExit(phoneNumber);
        boolean bool = result != null;
        return R.ok(bool);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/email/{email}/appcode/{appCode}")
    public R<UserInfo> getUserInfoByEmail(@PathVariable(value = "email") String email, @PathVariable(value = "appCode") String appCode){
        UserInfo userInfo = userInfoService.queryUserInfoByEmail(email,appCode);
        return R.ok(userInfo);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/phone/{phone}/appcode/{appCode}")
    public R<UserInfo> getUserInfoByPhone(@PathVariable(value = "phone") String phone, @PathVariable(value = "appCode") String appCode){
        UserInfo userInfo = userInfoService.queryUserInfoByPhone(phone,appCode);
        return R.ok(userInfo);
    }

    @RequestMapping(method = RequestMethod.GET,value = ("/credentialsId/{credentialsId}/appcode/{appCode}"))
    public R<UserInfo> getUserInfoByCredentialsId(@PathVariable(value = "appCode") String appCode, @PathVariable(value = "credentialsId") String credentialsId){
        UserInfo userInfo = userInfoService.queryUserInfoByCredentialsId(credentialsId,appCode);
        return R.ok(userInfo);
    }
}
