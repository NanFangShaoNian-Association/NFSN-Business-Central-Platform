package cn.nfsn.common.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: gaojianjie
 * @Description 凭证绑定类
 * @date 2023/9/20 10:22
 */
@Data
public class BindingCertificateDTO {
    @NotNull(message = "账号ID不能为空")
    private Integer credentialsId;

    @NotBlank(message = "绑定账号不能为空")
    private String certificate;

    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
