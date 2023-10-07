package cn.nfsn.transaction.bridge;

import cn.nfsn.common.core.utils.ImprovedHttpClientUtil;
import cn.nfsn.transaction.config.WxPayConfig;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.enums.PayType;
import cn.nfsn.transaction.enums.WxApiType;
import cn.nfsn.transaction.enums.WxNotifyType;
import cn.nfsn.transaction.factory.PayFactory;
import cn.nfsn.transaction.model.dto.AmountDTO;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.RequestWxCodeDTO;
import cn.nfsn.transaction.model.dto.ResponsePayNotifyDTO;
import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.model.entity.RefundInfo;
import cn.nfsn.transaction.service.OrderInfoService;
import cn.nfsn.transaction.service.PaymentInfoService;
import cn.nfsn.transaction.service.RefundInfoService;
import cn.nfsn.transaction.utils.WechatPay2ValidatorForRequest;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static cn.nfsn.transaction.constant.WxPayConstant.*;
import static cn.nfsn.transaction.utils.WxPayUtils.sendRequest;

/**
 * @ClassName: WxPayStrategy
 * @Description: 微信支付策略
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/8 0008 13:33
 **/
@Slf4j
@Component
public class WxPayNative implements IPayMode {

    /**
     * 重入锁，用于处理并发问题
     */
    private final ReentrantLock lock = new ReentrantLock();
    @Resource
    private OrderInfoService orderInfoService;
    @Resource
    private WxPayConfig wxPayConfig;
    @Resource
    private CloseableHttpClient wxPayClient;
    @Resource
    private PaymentInfoService paymentInfoService;
    @Resource
    private Verifier verifier;
    @Resource
    private PayFactory payFactory;
    @Resource
    private RefundInfoService refundInfoService;

    /**
     * 创建订单，调用Native支付接口
     *
     * @param productDTO 商品信息
     * @return 包含 code_url 和订单号的Map
     * @throws Exception 抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object createOrder(ProductDTO productDTO) throws Exception {
        //根据商品ID和支付类型创建订单
        OrderInfo orderInfo = orderInfoService.createOrderByProductId(productDTO, PayType.WXPAY.getType());

        //获取订单二维码URL
        String codeUrl = orderInfo.getPaymentData();

        //检查订单是否存在且二维码URL是否已保存
        if (orderInfo != null && !StringUtils.isEmpty(codeUrl)) {
            // 添加订单号到日志
            log.info("订单：{} 已存在，二维码已保存", orderInfo.getOrderNo());

            //返回二维码和订单号
            return createResultMap(codeUrl, orderInfo.getOrderNo());
        }

        //打印日志，开始调用统一下单API
        log.info("订单：{} 开始调用统一下单API", orderInfo.getOrderNo());

        //获取统一下单API的URL
        String url = wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType());

        //设置金额参数
        AmountDTO amountDTO = AmountDTO.builder()
                .total(orderInfo.getTotalFee())
                .currency("CNY")
                .build();

        //设置请求参数
        RequestWxCodeDTO requestWxCodeDTO = RequestWxCodeDTO.builder()
                .appid(wxPayConfig.getAppid())
                .mchid(wxPayConfig.getMchId())
                .description(orderInfo.getTitle())
                .out_trade_no(orderInfo.getOrderNo())
                .notify_url(wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()))
                .amount(amountDTO)
                .build();

        Map<String, String> resultMap = sendRequest(wxPayClient, url, requestWxCodeDTO, "Native下单");

        //从结果中获取二维码URL
        codeUrl = resultMap.get("code_url");

        //保存二维码URL
        String orderNo = orderInfo.getOrderNo();
        orderInfoService.saveCodeUrl(orderNo, codeUrl);

        //返回二维码和订单号
        return createResultMap(codeUrl, orderInfo.getOrderNo());
    }

    /**
     * 创建结果Map
     *
     * @param codeUrl 订单二维码链接
     * @param orderNo 订单号
     * @return 包含订单二维码链接和订单号的Map
     */
    private Map<String, Object> createResultMap(String codeUrl, String orderNo) {
        // 创建一个新的HashMap
        Map<String, Object> map = new HashMap<>();

        // 向map中插入订单二维码链接
        map.put("codeUrl", codeUrl);

        // 向map中插入订单号
        map.put("orderNo", orderNo);

        // 返回填充了数据的map
        return map;
    }

    /**
     * 处理微信支付通知，验证请求的有效性，并进行订单处理.
     *
     * @param request HttpServletRequest 对象，表示一个 HTTP 请求
     * @return ResponsePayNotifyDTO 响应对象，包含响应码和信息
     * @throws IOException              如果读取请求数据时出错
     * @throws GeneralSecurityException 如果在验证签名过程中出现安全异常
     */
    @Override
    public ResponsePayNotifyDTO paymentNotificationHandler(HttpServletRequest request, OrderStatus successStatus) throws IOException, GeneralSecurityException {

        // 实例化 Gson 对象，用于 JSON 数据解析
        Gson gson = new Gson();

        // 从请求中读取数据并转换为字符串格式
        String body = ImprovedHttpClientUtil.readData(request);

        // 将字符串数据转化为 Map 对象
        Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);

        // 从 Map 中获取通知 ID 并记录
        String requestId = (String) bodyMap.get("id");
        log.info("通知的id ===> {}", requestId);

        // 创建签名验证器对象
        WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest =
                new WechatPay2ValidatorForRequest(verifier, requestId, body);

        // 进行签名验证
        if (!wechatPay2ValidatorForRequest.validate(request)) {
            // 若验证失败，记录错误信息并返回错误消息
            log.error("通知验签失败");
            return new ResponsePayNotifyDTO(ERROR_CODE, ERROR_VALIDATION_FAILED_MSG);
        }

        // 验证成功，记录相关信息
        log.info("通知验签成功");

        // 创建支付桥接对象，用于处理具体的支付逻辑
        AbstractPay wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        // 使用桥接对象处理订单
        processOrder(bodyMap, successStatus);

        // 处理完成，返回成功消息
        return new ResponsePayNotifyDTO(SUCCESS_CODE, SUCCESS_MSG);
    }


    /**
     * 处理订单
     *
     * @param bodyMap 请求体Map
     * @throws GeneralSecurityException 抛出安全异常
     * @successStatus 成功状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void processOrder(Map<String, Object> bodyMap, OrderStatus successStatus) throws GeneralSecurityException {
        log.info("处理订单");

        // 解密报文
        String plainText = decryptFromResource(bodyMap);

        // 将明文转换成map
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String) plainTextMap.get("out_trade_no");

        // 在对业务数据进行状态检查和处理之前，
        // 要采用数据锁进行并发控制，
        // 以避免函数重入造成的数据混乱
        if (lock.tryLock()) {
            try {
                // 处理重复的通知
                // 接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                if (!OrderStatus.NOTPAY.getType().equals(orderStatus) && !OrderStatus.REFUND_PROCESSING.getType().equals(orderStatus)) {
                    return;
                }

                // 更新订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, successStatus);

                if (OrderStatus.NOTPAY.getType().equals(orderStatus)) {
                    // 记录支付日志
                    paymentInfoService.createPaymentInfo(plainText);
                } else {
                    // 记录退款日志
                    refundInfoService.updateRefund(plainText);
                }
                log.info("订单状态更新成功");
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
    }

    /**
     * 对称解密
     *
     * @param bodyMap 请求体Map
     * @return 明文字符串
     * @throws GeneralSecurityException 抛出安全异常
     */
    private String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {

        log.info("密文解密");

        //通知数据
        Map<String, String> resourceMap = (Map) bodyMap.get("resource");
        //数据密文
        String ciphertext = resourceMap.get("ciphertext");
        //随机串
        String nonce = resourceMap.get("nonce");
        //附加数据
        String associatedData = resourceMap.get("associated_data");

        log.info("密文 ===> {}", ciphertext);
        AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        log.info("明文 ===> {}", plainText);

        return plainText;
    }

    /**
     * 用户取消订单
     *
     * @param orderNo 订单号
     * @throws Exception 抛出异常
     */
    @Override
    public void cancelOrder(String orderNo) throws Exception {
        // 调用微信支付的关单接口
        this.closeOrder(orderNo);
        // 更新商户端的订单状态
        orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
    }

    /**
     * 调用微信支付的关闭订单接口实现订单的关闭操作。
     *
     * @param orderNo 需要关闭的订单编号
     * @throws Exception 如果网络请求失败或者服务器返回了不成功的HTTP状态码，会抛出此异常
     */
    private void closeOrder(String orderNo) throws Exception {
        // 订单号合法性检查，为空时抛出异常
        if (orderNo == null || orderNo.isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }

        // 记录开始执行订单关闭操作的日志，记录信息包含订单号
        log.info("关单接口的调用，订单号 ===> {}", orderNo);

        // 构造一个调用微信支付关闭订单接口的URL, URL中需要包含待关闭的订单编号
        String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
        url = wxPayConfig.getDomain().concat(url);

        // 创建一个Map对象用于存储待发送的请求参数，这里我们只需要设置商户号mchid
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mchid", wxPayConfig.getMchId());

        try {
            // 使用sendRequest方法发送请求并获取结果
            sendRequest(wxPayClient, url, paramsMap, CLOSE_ORDER);
        } catch (IOException e) {
            throw new Exception("关闭订单失败，订单号：" + orderNo, e);
        }
    }

    /**
     * 根据订单号和退款原因进行退款操作
     *
     * @param orderNo 订单编号，不能为空
     * @param reason  退款原因，不能为空
     * @throws Exception 若退款过程中发生错误，则抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refund(String orderNo, String reason) throws Exception {

        // 创建并记录退款单信息
        log.info("创建退款单记录");
        RefundInfo refundsInfo = refundInfoService.createRefundByOrderNo(orderNo, reason);

        // 构造退款API的URL
        log.info("调用退款API");
        String url = wxPayConfig.getDomain().concat(WxApiType.DOMESTIC_REFUNDS.getType());

        /**
         * 创建一个Map对象paramsMap，用于存储请求的参数
         * 其中包含：
         * out_trade_no：订单编号
         * out_refund_no：退款单编号
         * reason：退款原因
         * notify_url：回调通知地址，由微信支付配置的域名和退款通知类型拼接得来
         */
        Map paramsMap = new HashMap();
        paramsMap.put("out_trade_no", orderNo);
        paramsMap.put("out_refund_no", refundsInfo.getRefundNo());
        paramsMap.put("reason", reason);
        paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.REFUND_NOTIFY.getType()));

        /**
         * 创建一个Map对象amountMap，用于存储退款金额相关的信息
         * 其中包含：
         * refund：退款金额
         * total：总金额
         * currency：货币种类，默认为"CNY"，即人民币
         *
         * 最后将amountMap作为一个整体，放入paramsMap中
         */
        Map amountMap = new HashMap();
        amountMap.put("refund", refundsInfo.getRefund());
        amountMap.put("total", refundsInfo.getTotalFee());
        amountMap.put("currency", "CNY");
        paramsMap.put("amount", amountMap);

        // 调用sendRequest方法发送请求并处理结果
        try {
            Map<String, String> responseResult = sendRequest(wxPayClient, url, paramsMap, "退款");
            // 更新订单状态为正在处理退款
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_PROCESSING);

            // 更新退款单信息
            Gson gson = new Gson();
            // 将Map转换为Json字符串
            String jsonContent = gson.toJson(responseResult);
            // 传入Json字符串
            refundInfoService.updateRefund(jsonContent);
        } catch (IOException e) {
            throw new RuntimeException("退款异常", e);
        }
    }
}