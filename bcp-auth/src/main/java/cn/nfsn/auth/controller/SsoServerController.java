package cn.nfsn.auth.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sign.SaSignUtil;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.nfsn.auth.service.SsoService;
import cn.nfsn.common.core.domain.dto.LoginRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

/**
 * SSO Server端 Controller
 */
@Api("认证模块")
@RestController
@RequestMapping("/sso")
public class SsoServerController {
    @Autowired
    private SsoService ssoService;

    @ApiIgnore
    @RequestMapping("/*")
    public Object ssoRequest() {
        return SaSsoProcessor.instance.serverDister();
    }

    @ApiOperation("单点登陆接口")
    @RequestMapping(method = RequestMethod.POST,value = "/doLogin")
    public SaResult ssoLogin(@Validated @RequestBody LoginRequestDTO loginRequestDTO) throws IOException {
        ssoService.SsoLoginByCodeHandler(loginRequestDTO);
        return SaResult.ok().setData(StpUtil.getTokenValue());
    }

    // 示例：获取数据接口（用于在模式三下，为 client 端开放拉取数据的接口）
    @ApiOperation("根据密钥解析的ID获取用户信息接口")
    @RequestMapping(value = "/getData",method = RequestMethod.GET)
    public SaResult getData(@ApiParam("密钥解析的ID")String loginId, @ApiParam("应用ID")String appCode) {
        // 校验签名：只有拥有正确秘钥发起的请求才能通过校验
        SaSignUtil.checkRequest(SaHolder.getRequest());
        return SaResult.ok().setData(ssoService.queryUserInfoByCredentialsId(loginId,appCode));
    }

    // SSO-Server：单点注销
    @ApiOperation("请求头携带密钥进行单点注销接口")
    @RequestMapping(value = "/signout",method = RequestMethod.GET)
    public Object ssoSignout() {
        return SaSsoProcessor.instance.ssoSignout();
    }

    // SSO-Server：校验ticket 获取账号id
    @ApiIgnore
    @RequestMapping("/checkTicket")
    public Object ssoCheckTicket() {
        return SaSsoProcessor.instance.ssoCheckTicket();
    }

    // SSO-Server：统一认证地址
    @ApiIgnore
    @RequestMapping("/auth")
    public Object ssoAuth() {
        return SaSsoProcessor.instance.ssoAuth();
    }

    // 当前是否登录
    @RequestMapping(method = RequestMethod.GET,value = "/isLogin")
    public Object isLogin() {
        return SaResult.data(StpUtil.isLogin());
    }

}
