package cn.nfsn.transaction.service.impl;

import cn.nfsn.transaction.enums.PayType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nfsn.transaction.model.entity.PaymentInfo;
import cn.nfsn.transaction.service.PaymentInfoService;
import cn.nfsn.transaction.mapper.PaymentInfoMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
* @author Atnibam Aitay
* @description 针对表【payment_info】的数据库操作Service实现
* @createDate 2023-09-07 14:15:09
*/
@Slf4j
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

    /**
     * 记录微信支付信息
     * 使用 Gson 将传入的 plainText 转换为 HashMap 来获取订单号、业务编号、支付类型、交易状态以及用户实际支付金额等信息，
     * 然后创建一个 PaymentInfo 对象，将这些信息设置到此对象中，并存入数据库。
     *
     * @param plainText Json格式的字符串，包含需要记录的支付信息
     */
    @Override
    public void createPaymentInfo(String plainText) {

        log.info("开始记录微信支付日志");

        // 创建 Gson 对象
        Gson gson = new Gson();
        // 将传入的 Json 字符串转化为 HashMap 对象
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);

        // 从 HashMap 中获取订单号
        String orderNo = (String)plainTextMap.get("out_trade_no");
        // 从 HashMap 中获取业务编号
        String transactionId = (String)plainTextMap.get("transaction_id");
        // 从 HashMap 中获取支付类型
        String tradeType = (String)plainTextMap.get("trade_type");
        // 从 HashMap 中获取交易状态
        String tradeState = (String)plainTextMap.get("trade_state");
        // 从 HashMap 中获取用户实际支付金额（单位：分）
        Map<String, Object> amount = (Map)plainTextMap.get("amount");
        Integer payerTotal = ((Double) amount.get("payer_total")).intValue();

        // 创建 PaymentInfo 对象，用于保存支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        // 设置订单号
        paymentInfo.setOrderNo(orderNo);
        // 设置支付方式为微信支付
        paymentInfo.setPaymentType(PayType.WXPAY.getType());
        // 设置业务编号
        paymentInfo.setTransactionId(transactionId);
        // 设置支付类型
        paymentInfo.setTradeType(tradeType);
        // 设置交易状态
        paymentInfo.setTradeState(tradeState);
        // 设置用户实际支付金额
        paymentInfo.setPayerTotal(payerTotal);
        // 设置原始支付信息内容
        paymentInfo.setContent(plainText);

        // 插入支付信息到数据库
        baseMapper.insert(paymentInfo);

        log.info("微信支付日志记录结束");
    }

    /**
     * 记录支付日志：支付宝
     *
     * @param params 支付相关参数，包括订单号、业务编号、交易状态、交易金额等信息
     */
    @Override
    public void createPaymentInfoForAliPay(Map<String, String> params) {

        log.info("记录支付日志");

        // 获取订单号
        String orderNo = params.get("out_trade_no");

        // 业务编号
        String transactionId = params.get("trade_no");

        // 交易状态
        String tradeStatus = params.get("trade_status");

        // 交易金额
        String totalAmount = params.get("total_amount");
        // 将交易金额从字符串转换为整数，并乘以100（单位由元转换为分）
        int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();

        // 初始化支付信息对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(orderNo);
        paymentInfo.setPaymentType(PayType.ALIPAY.getType());
        paymentInfo.setTransactionId(transactionId);
        paymentInfo.setTradeType("电脑网站支付");
        paymentInfo.setTradeState(tradeStatus);
        paymentInfo.setPayerTotal(totalAmountInt);

        // 使用Gson将支付参数Map转化为json格式
        Gson gson = new Gson();
        String json = gson.toJson(params, HashMap.class);
        // 设置支付信息内容
        paymentInfo.setContent(json);

        // 插入支付信息到数据库
        baseMapper.insert(paymentInfo);
    }

}




