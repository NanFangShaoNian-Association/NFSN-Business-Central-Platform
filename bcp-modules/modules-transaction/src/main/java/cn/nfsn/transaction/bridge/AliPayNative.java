package cn.nfsn.transaction.bridge;

import cn.hutool.core.net.URLDecoder;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.AliPayException;
import cn.nfsn.transaction.config.AlipayClientConfig;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.enums.PayType;
import cn.nfsn.transaction.model.dto.AlipayBizContentDTO;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.ResponseWxPayNotifyDTO;
import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.service.OrderInfoService;
import cn.nfsn.transaction.service.PaymentInfoService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private PaymentInfoService paymentInfoService;

    /**
     * 重入锁，用于处理并发问题
     */
    private final ReentrantLock lock = new ReentrantLock();

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

            //获取订单二维码URL
            String codeUrl = orderInfo.getCodeUrl();

            //检查订单是否存在且二维码URL是否已保存
            if (orderInfo != null && !StringUtils.isEmpty(codeUrl)) {
                // 添加订单号到日志
                log.info("订单：{} 已存在，二维码已保存", orderInfo.getOrderNo());

                //返回二维码和订单号
                return codeUrl;
            }

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

            ObjectMapper objectMapper = new ObjectMapper();
            // 将Java对象转换为JSON格式的字符串
            String jsonString = objectMapper.writeValueAsString(alipayBizContentDTO);
            System.out.println("==================TEST: " + jsonString);
            request.setBizContent(jsonString.toString());

            // 执行请求，调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

            if(response.isSuccess()){
                log.info("调用成功，返回结果 ===> " + response.getBody());
                //保存结果
                codeUrl = response.getBody();
                String orderNo = orderInfo.getOrderNo();
                orderInfoService.saveCodeUrl(orderNo, codeUrl);

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
     * 处理支付宝支付通知，验证请求的有效性，并进行订单处理.
     *
     * @param request HttpServletRequest 对象，表示一个 HTTP 请求
     * @param successStatus 成功状态
     * @return ResponseWxPayNotifyDTO   响应对象，包含响应码和信息
     * @throws IOException              如果读取请求数据时出错
     * @throws GeneralSecurityException 如果在验证签名过程中出现安全异常
     */
    @Override
    public ResponseWxPayNotifyDTO handlePaymentNotification(HttpServletRequest request, OrderStatus successStatus) throws IOException, GeneralSecurityException {
        Gson gson = new Gson();

        // 从请求URL中获取参数并转换为Map
        Map<String, Object> params = Arrays.stream(request.getQueryString().split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        // 解析业务内容
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> bizContent = gson.fromJson(params.get("biz_content").toString(), type);
        // 将业务内容合并到主参数
        params.putAll(bizContent);
        log.info("支付宝支付通知 ===> " + params);

        // 验证签名
        boolean signVerified;
        try {
            signVerified = AlipaySignature.rsaCheckV1(
                    convertParamsToStringKey(params),
                    config.getAlipayPublicKey(),
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        if (!signVerified) {
            throw new AliPayException(ResultCode.CREATE_ORDER_CONTRAST_LOCK_FAIL);
        }

        // 获取并验证订单信息
        String outTradeNo = checkAndGetStringFromParams(params, "out_trade_no");
        OrderInfo order = orderInfoService.getOrderByOrderNo(outTradeNo);
        if(order == null){
            throw new AliPayException(ResultCode.ORDER_NOT_EXIST);
        }

        // 校验订单金额
        String totalAmount = checkAndGetStringFromParams(params, "total_amount");
        int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();
        int totalFeeInt = order.getTotalFee().intValue();
        if(totalAmountInt != totalFeeInt){
            throw new AliPayException(ResultCode.CREATE_ORDER_FAIL);
        }

        // 校验卖家ID
        String sellerId = checkAndGetStringFromParams(params, "seller_id");
        if(!sellerId.equals(config.getSellerId())){
            throw new AliPayException(ResultCode.INSERT_ORDER_FAIL);
        }

        // 验证应用ID
        String appId = checkAndGetStringFromParams(params, "app_id");
        if(!appId.equals(config.getAppId())){
            throw new AliPayException(ResultCode.PRODUCT_OR_PAY_TYPE_NULL);
        }

        // 验证交易状态
        String tradeStatus = checkAndGetStringFromParams(params, "trade_status");
        if(!"TRADE_SUCCESS".equals(tradeStatus)){
            throw new AliPayException(ResultCode.ORDER_PAYING);
        }

        // 所有校验通过，处理相关业务，例如修改订单状态，记录支付日志等
        processOrder(params, successStatus);

        return null;
    }

    /**
     * 将查询参数字符串分割为键值对.
     *
     * @param it 查询参数字符串
     * @return 键值对
     */
    private AbstractMap.SimpleEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleEntry<>(
                URLDecoder.decode(key, StandardCharsets.UTF_8),
                URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }

    /**
     * 检查并从参数中获取字符串.
     *
     * @param params 参数
     * @param key 键
     * @return 值
     * @throws RuntimeException 如果键对应的值不是字符串类型
     */
    private String checkAndGetStringFromParams(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (!(value instanceof String)) {
            throw new RuntimeException("Unexpected type for '" + key + "': " + value.getClass());
        }
        return (String) value;
    }

    /**
     * 将Map的值转化为字符串类型.
     *
     * @param params 参数
     * @return 转换后的Map
     */
    private Map<String, String> convertParamsToStringKey(Map<String, Object> params) {
        return params.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue() != null ? e.getValue().toString() : ""
                ));
    }

    /**
     * 处理订单
     *
     * @param bodyMap 请求体Map
     * @throws GeneralSecurityException 抛出安全异常
     */
    @Override
    public void processOrder(Map<String, Object> bodyMap, OrderStatus successStatus) throws GeneralSecurityException {
        log.info("开始处理订单");

        Object orderNoObj = bodyMap.get("out_trade_no");
        if (!(orderNoObj instanceof String)) {
            throw new RuntimeException("Unexpected type for 'out_trade_no': " + (orderNoObj == null ? "null" : orderNoObj.getClass()));
        }

        // 从参数中获取订单号
        String orderNo = bodyMap.get("out_trade_no").toString();

        /*
         * 尝试获取锁：
         * 如果成功获取则立即返回true，获取失败则立即返回false。
         * 不必一直等待锁的释放
         */
        if(lock.tryLock()) {
            try {

                /*
                 * 处理重复通知
                 * 接口调用的幂等性：无论接口被调用多少次，以下业务只执行一次
                 */
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
                    return;
                }

                // 更新订单状态为成功
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);

                Map<String, String> bodyMapString = convertParamsToStringKey(bodyMap);

                // 记录支付日志
                paymentInfoService.createPaymentInfoForAliPay(bodyMapString);

            } finally {
                // 处理完毕后，需要主动释放锁
                lock.unlock();
            }
        }
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
     * @param bodyMap 请求体Map，包含了支付宝通知的退款信息
     * @throws Exception 抛出异常，包括但不限于解密错误、数据库操作失败等
     */
    @Override
    public void processRefund(Map<String, Object> bodyMap, OrderStatus successStatus) throws Exception {

    }
}