package cn.nfsn.api.user;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.common.core.domain.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * @Author: gaojianjie
 * @Description 用户信息远程调用接口
 * @date 2023/9/12 08:31
 */
@FeignClient(value = "modules-user",contextId = "userInfo")
public interface RemoteUserInfoService {
    @RequestMapping(method = RequestMethod.GET,value = "/userInfo/{userId}")
    public R<UserInfo> queryUserInfo(@PathVariable(value = "userId") String userId);

    @RequestMapping(method = RequestMethod.DELETE,value = "/userInfo/{userId}")
    public R logout(@PathVariable(value = "userId") String userId);

    @RequestMapping(method = RequestMethod.POST,value = "/userInfo")
    public R<String> registration(@Validated @RequestBody UserInfo userInfo) throws IOException;

    @RequestMapping(method = RequestMethod.PUT,value = "/userInfo")
    public R updateUserInfo(@Validated @RequestBody UserInfo userInfo);

    @RequestMapping(method = RequestMethod.GET,value = "/userInfo/phone-numbers/{phoneNumber}/exists")
    public R<Boolean> checkPhoneNumbExit(@PathVariable(value = "phoneNumber") String phoneNumber);

    @RequestMapping(method = RequestMethod.GET,value = "/userInfo/email/{email}/appcode/{appCode}")
    public R<UserInfo> getUserInfoByEmail(@PathVariable(value = "email") String email, @PathVariable(value = "appCode") String appCode);

    @RequestMapping(method = RequestMethod.GET,value = "/userInfo/phone/{phone}/appcode/{appCode}")
    public R<UserInfo> getUserInfoByPhone(@PathVariable(value = "phone") String phone, @PathVariable(value = "appCode") String appCode);

    @RequestMapping(method = RequestMethod.GET,value = ("/userInfo/credentialsId/{credentialsId}/appcode/{appCode}"))
    public R<UserInfo> getUserInfoByCredentialsId(@PathVariable(value = "appCode") String appCode, @PathVariable(value = "credentialsId") String credentialsId);
}
