package com.nfsn.controller;

import com.nfsn.common.core.domain.R;
import com.nfsn.common.core.domain.UserInfo;
import com.nfsn.common.core.enums.ResultCode;
import com.nfsn.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public R deRegistration(@PathVariable(value = "userId") String userId){
        if (userInfoService.deRegistration(userId)) {
            return R.ok();
        }else {
            return R.fail(ResultCode.INTERNAL_ERROR);
        }
    }

}
