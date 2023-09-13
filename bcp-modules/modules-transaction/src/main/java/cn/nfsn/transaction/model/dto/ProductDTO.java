package cn.nfsn.transaction.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName: ProductDTO
 * @Description: 商品信息的数据传输对象
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-08 15:30
 **/
@Data
@ApiModel(description = "商品信息")
public class ProductDTO {

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id")
    @NotNull(message = "商品id不能为空")
    private Long id;

    /**
     * 用户iD
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    @NotNull(message = "商品名称不能为空")
    private String title;

    /**
     * 价格（分）
     */
    @ApiModelProperty(value = "价格（分）")
    @NotNull(message = "价格不能为空")
    private Integer price;

    /**
     * AppID
     */
    @ApiModelProperty(value = "AppID")
    @NotNull(message = "AppID不能为空")
    private Integer appId;
}