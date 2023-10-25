package cn.nfsn.transaction.model.dto;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: RefundDTO
 * @Description: 退款数据传输对象，包含订单编号和退款原因两个字段
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-18 21:36
 **/
@Data
public class RefundDTO {

    /**
     * 订单编号，不能为空
     */
    @NotBlank(message = "订单编号不能为空")
    @ApiModelProperty(value = "订单编号", required = true)
    private String orderNo;

    /**
     * 退款原因，不能为空
     */
    @NotBlank(message = "退款原因不能为空")
    @ApiModelProperty(value = "退款原因", required = true)
    private String reason;
}

