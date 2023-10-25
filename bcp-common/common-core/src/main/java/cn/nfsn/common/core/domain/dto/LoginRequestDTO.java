package cn.nfsn.common.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("登陆注册请求类")
public class LoginRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 凭证-手机号登录凭证为手机号码，微信和QQ则为对应的凭据
     */
    @ApiModelProperty("邮箱或手机账号")
    @NotNull(message = "账号不能为空")
    @NotBlank(message = "账号不能为空")
    private String certificate;

    /**
     * 验证码-
     */
    @ApiModelProperty("验证码")
    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    /**
     * 登录方式 1-手机号+验证码 2-邮箱+验证码
     */
    @ApiModelProperty("登录方式 1-手机号+验证码 2-邮箱+验证码")
    @NotNull(message = "登录方式不能为空")
//    @ApiModelProperty("1-手机号+验证码 2-邮箱+验证码 ")
    private Integer loginMethod = 0;

    /**
     * 应用码-
     */
    @ApiModelProperty("应用ID 1 STEAM课堂 2 北极星宠 3万象课堂")
    @NotNull(message = "应用码不能为空")
    @NotBlank(message = "应用码不能为空")
    private String appCode;

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public Integer getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(Integer loginMethod) {
        this.loginMethod = loginMethod;
    }

    public LoginRequestDTO() {
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Override
    public String toString() {
        return "LoginRequestDTO{" +
                "certificate='" + certificate + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", loginMethod=" + loginMethod +
                ", appCode='" + appCode + '\'' +
                '}';
    }
}