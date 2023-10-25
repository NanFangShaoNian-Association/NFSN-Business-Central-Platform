package cn.nfsn.transaction.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName order_info
 */
@TableName(value ="order_info")
@Data
@Builder
public class OrderInfo implements Serializable {
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 平台的订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 支付产品id
     */
    private Long productId;

    /**
     * 订单金额(分)
     */
    private Integer totalFee;

    /**
     * 微信支付的订单二维码链接或者支付宝支付的HTML表单
     */
    private String paymentData;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 支付方式
     */
    private String paymentType;

    /**
     * AppID
     */
    private Integer appId;

    /**
     * 支付结果的回调地址
     */
    private String paymentCallBackUrl;

    /**
     * 退款结果的回调地址
     */
    private String refundCallBackUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}