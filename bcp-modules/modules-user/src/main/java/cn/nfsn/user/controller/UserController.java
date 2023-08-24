package cn.nfsn.user.controller;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.common.core.domain.UserInfo;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.UserOperateException;
import cn.nfsn.common.core.utils.StringUtils;
import cn.nfsn.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/11 22:37
 */
//demo
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/{userId}")
    public R<UserInfo> queryUserInfo(@PathVariable(value = "userId") String userId){
        return R.ok(userInfoService.queryUserInfo(userId));
    }

    @DeleteMapping("/{userId}")
    public R logout(@PathVariable(value = "userId") String userId){
        userInfoService.logout(userId);
        return R.ok();
    }

    @PostMapping
    public R<String> registration(@Validated @RequestBody UserInfo userInfo){
        userInfoService.registration(userInfo);
        return R.ok();
    }

    @PutMapping
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

    @GetMapping("/phone-numbers/{phoneNumber}/exists")
    public R<Boolean> checkPhoneNumbExit(@PathVariable String phoneNumber){
        UserInfo result = userInfoService.checkPhoneNumbExit(phoneNumber);
        boolean bool = result != null;
        return R.ok(bool);
    }
}
