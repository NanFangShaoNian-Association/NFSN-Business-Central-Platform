package com.nfsn.controller;

import com.nfsn.common.core.constant.Constants;
import com.nfsn.common.core.domain.R;
import com.nfsn.common.core.domain.UserInfo;
import com.nfsn.common.core.enums.ResultCode;
import com.nfsn.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/{userId}")
    public R deRegistration(@PathVariable(value = "userId") String userId){
        if (userInfoService.deRegistration(userId)) {
            return R.ok(Constants.SUCCESS_OPERA);
        }else {
            return R.fail(ResultCode.INTERNAL_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public R registration(@RequestBody UserInfo userInfo){
        userInfoService.registration(userInfo);
        return R.ok(Constants.SUCCESS_OPERA);
    }

}
