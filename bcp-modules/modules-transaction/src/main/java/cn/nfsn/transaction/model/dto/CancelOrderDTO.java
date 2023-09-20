package cn.nfsn.transaction.model.dto;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: CancelOrderDTO
 * @Description: 取消订单数据传输对象，包含订单编号一个字段
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-18 21:36
 **/
@Data
public class CancelOrderDTO {

    /**
     * 订单编号，不能为空
     */
    @NotBlank(message = "订单编号不能为空")
    @ApiModelProperty(value = "订单编号", required = true)
    private String orderNo;
}

