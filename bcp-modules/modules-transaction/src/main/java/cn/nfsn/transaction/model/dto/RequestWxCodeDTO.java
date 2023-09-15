package cn.nfsn.transaction.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName: RequestWxCodeDTO
 * @Description: 用于封装微信支付请求所需的各类参数的数据传输对象
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-14 12:23
 **/
@Data
@Builder
public class RequestWxCodeDTO {

    /**
     * 微信公众账号ID
     */
    private String appid;

    /**
     * 微信商户号
     */
    private String mchid;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 通知地址，这是微信支付成功后微信会回调的地址
     */
    private String notify_url;

    /**
     * 订单金额信息，包括总金额和货币类型等
     */
    private AmountDTO amount;
}
