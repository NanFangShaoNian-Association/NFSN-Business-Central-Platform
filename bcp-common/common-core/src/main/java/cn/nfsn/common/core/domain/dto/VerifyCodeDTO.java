package cn.nfsn.common.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("接收发送验证码的对象以及类型")
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

    @NotNull(message = "功能类型不能为空")
    @ApiModelProperty("1-登陆注册验证码 2-绑定账号验证码")
    private Integer functionType;

}