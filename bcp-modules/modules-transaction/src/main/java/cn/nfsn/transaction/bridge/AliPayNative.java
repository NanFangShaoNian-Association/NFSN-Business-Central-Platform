package cn.nfsn.transaction.bridge;

import cn.hutool.json.JSONObject;
import cn.nfsn.transaction.config.AlipayClientConfig;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.enums.PayType;
import cn.nfsn.transaction.model.dto.AlipayBizContentDTO;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.ResponseWxPayNotifyDTO;
import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.service.OrderInfoService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Map;

import static cn.nfsn.transaction.constant.AliPayConstant.PRODUCT_CODE;

/**
 * @ClassName: AliPayNative
 * @Description:
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/14 0014 18:21
 **/
@Slf4j
@Component
public class AliPayNative implements IPayMode {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private AlipayClientConfig config;

    @Resource
    private AlipayClient alipayClient;

    /**
     * 创建订单的方法
     *
     * @param productDTO 商品数据传输对象,包含商品相关信息
     * @return 返回一个包含订单信息的Map对象
     * @throws Exception 如果在创建订单过程中出现问题，将抛出异常
     */
    @Override
    public Object createOrder(ProductDTO productDTO) throws Exception {
        try {
            // 生成订单
            log.info("生成订单");
            OrderInfo orderInfo = orderInfoService.createOrderByProductId(productDTO, PayType.ALIPAY.getType());

            // 调用支付宝接口
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

            // 配置需要的公共请求参数
            // 支付完成后，支付宝向应用发起异步通知的地址
            request.setNotifyUrl(config.getNotifyUrl());

            // 支付完成后，我们想让页面跳转回应用的页面，配置returnUrl
            request.setReturnUrl(config.getReturnUrl());

            // 组装当前业务方法的请求参数
            // 使用建造者模式创建 AlipayBizContentDTO 对象
            AlipayBizContentDTO alipayBizContentDTO = AlipayBizContentDTO.builder()
                    .outTradeNo(orderInfo.getOrderNo())
                    .totalAmount(new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal(100)))
                    .subject(orderInfo.getTitle())
                    .productCode(PRODUCT_CODE)
                    .build();

            JSONObject bizContent = new JSONObject(alipayBizContentDTO);
            request.setBizContent(bizContent.toString());

            // 执行请求，调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

            if(response.isSuccess()){
                log.info("调用成功，返回结果 ===> " + response.getBody());
                return response.getBody();
            } else {
                log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
                throw new RuntimeException("创建支付交易失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException("创建支付交易失败");
        }
    }

    /**
     * 处理微信支付通知，验证请求的有效性，并进行订单处理.
     *
     * @param request HttpServletRequest 对象，表示一个 HTTP 请求
     * @return ResponseWxPayNotifyDTO 响应对象，包含响应码和信息
     * @throws IOException              如果读取请求数据时出错
     * @throws GeneralSecurityException 如果在验证签名过程中出现安全异常
     */
    @Override
    public ResponseWxPayNotifyDTO handlePaymentNotification(HttpServletRequest request, OrderStatus successStatus) throws IOException, GeneralSecurityException {
        return null;
    }

    /**
     * 处理订单
     *
     * @param bodyMap 请求体Map
     * @throws GeneralSecurityException 抛出安全异常
     */
    @Override
    public void processOrder(Map<String, Object> bodyMap, OrderStatus successStatus) throws GeneralSecurityException {

    }

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @throws Exception 抛出异常
     */
    @Override
    public void cancelOrder(String orderNo) throws Exception {

    }

    /**
     * 根据订单号和退款原因进行退款操作
     *
     * @param orderNo 订单编号，不能为空
     * @param reason  退款原因，不能为空
     * @throws Exception 若退款过程中发生错误，则抛出异常
     */
    @Override
    public void refund(String orderNo, String reason) throws Exception {

    }

    /**
     * 处理退款单
     *
     * @param bodyMap 请求体Map，包含了微信通知的退款信息
     * @throws Exception 抛出异常，包括但不限于解密错误、数据库操作失败等
     */
    @Override
    public void processRefund(Map<String, Object> bodyMap, OrderStatus successStatus) throws Exception {

    }
}