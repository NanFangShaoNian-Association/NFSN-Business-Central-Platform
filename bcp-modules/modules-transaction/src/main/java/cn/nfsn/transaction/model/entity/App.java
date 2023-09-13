package cn.nfsn.transaction.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName app
 */
@TableName(value ="app")
@Data
public class App implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 应用名
     */
    private String appName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 支付结果的回调地址
     */
    private String paymentNotifyUrl;

    /**
     * 退款结果的回调地址
     */
    private String refundNotifyUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}