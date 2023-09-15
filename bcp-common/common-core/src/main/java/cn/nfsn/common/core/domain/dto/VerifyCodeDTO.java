package cn.nfsn.common.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("接收验证码的对象以及类型")
public class VerifyCodeDTO {
    @NotNull(message = "账号不能为空")
    @NotBlank(message = "账号不能为空")
    @ApiModelProperty("邮箱或手机号")
    private String account;


    @ApiModelProperty("验证码主题(应用名等)")
    private String subject;

    @NotNull(message = "账号类型不能为空")
    @ApiModelProperty("1-手机号+验证码 2-邮箱+验证码")
    private Integer identity;

    public VerifyCodeDTO() {
    }

    public VerifyCodeDTO(String account, String subject, Integer identity) {
        this.account = account;
        this.subject = subject;
        this.identity = identity;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }
}