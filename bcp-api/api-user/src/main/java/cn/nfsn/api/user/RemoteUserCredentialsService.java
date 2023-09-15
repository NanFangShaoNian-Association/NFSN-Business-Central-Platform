package cn.nfsn.api.user;

import cn.nfsn.common.core.domain.AuthCredentials;
import cn.nfsn.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author: gaojianjie
 * @Description 用户凭证接口远程调用
 * @date 2023/9/10 15:04
 */
@FeignClient(value = "modules-user",contextId = "userCredentials")
public interface RemoteUserCredentialsService {
    @RequestMapping(method = RequestMethod.GET,value = "/user/email/{email}")
    public R<AuthCredentials> getAuthCredentialsByEmail(@PathVariable(value = "email") String email);

    @RequestMapping(method = RequestMethod.GET,value = "/user/phone/{phone}")
    public R<AuthCredentials> getAuthCredentialsByPhone(@PathVariable(value = "phone") String phone);

    @RequestMapping(method = RequestMethod.POST,value = "/user/phone")
    public R createUserCredentialsByPhone(@RequestBody String certificate);

    @RequestMapping(method = RequestMethod.POST,value = "/user/email")
    public R createUserCredentialsByEmail(@RequestBody String certificate);
}
