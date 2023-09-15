package cn.nfsn.user.controller;

import cn.nfsn.api.user.RemoteUserCredentialsService;
import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.common.core.domain.R;
import cn.nfsn.user.service.AuthCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: gaojianjie
 * @Description 用户凭证控制层
 * @date 2023/9/10 14:47
 */
@RestController
@RequestMapping("/user")
public class UserCredentialsController implements RemoteUserCredentialsService {
    @Autowired
    private AuthCredentialsService authCredentialsService;

    @RequestMapping(method = RequestMethod.GET,value = "/email/{email}")
    public R<AuthCredentials> getAuthCredentialsByEmail(@PathVariable(value = "email") String email){
        AuthCredentials authCredentials = authCredentialsService.queryAuthCredentialsByEmail(email);
        return R.ok(authCredentials);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/phone/{phone}")
    public R<AuthCredentials> getAuthCredentialsByPhone(@PathVariable(value = "phone") String phone){
        AuthCredentials authCredentials = authCredentialsService.queryAuthCredentialsByPhone(phone);
        return R.ok(authCredentials);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/phone")
    public R createUserCredentialsByPhone(@RequestBody String certificate){
        authCredentialsService.createUserCredentialsByPhone(certificate);
        return R.ok();
    }

    @RequestMapping(method = RequestMethod.POST,value = "/email")
    public R createUserCredentialsByEmail(@RequestBody String certificate){
        authCredentialsService.createUserCredentialsByEmail(certificate);
        return R.ok();
    }
}
