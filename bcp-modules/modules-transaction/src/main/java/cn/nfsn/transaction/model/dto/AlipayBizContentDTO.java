package cn.nfsn.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName: AlipayBizContentDTO
 * @Description: 对应支付宝业务参数的数据传输对象，包括商户订单号、订单总金额、订单标题和产品码等信息。
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-14 23:38
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlipayBizContentDTO {

    /**
     * 商户网站唯一订单号
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * 订单总金额，单位为元，精确到小数点后两位
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 销售产品码，支付宝平台上架的销售产品对应的编码
     */
    @JsonProperty("product_code")
    private String productCode;
}
