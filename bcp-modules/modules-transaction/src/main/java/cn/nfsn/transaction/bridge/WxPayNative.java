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
import cn.nfsn.transaction.model.dto.ResponseWxPayNotifyDTO;
import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.service.OrderInfoService;
import cn.nfsn.transaction.service.PaymentInfoService;
import cn.nfsn.transaction.utils.WechatPay2ValidatorForRequest;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
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

/**
 * @ClassName: WxPayStrategy
 * @Description: 微信支付策略
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/8 0008 13:33
 **/
@Slf4j
@Component
public class WxPayNative implements IPayMode {
    private final Gson gson = new Gson();

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

    /**
     * 重入锁，用于处理并发问题
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 创建订单，调用Native支付接口
     *
     * @param productDTO 商品信息
     * @return 包含code_url 和 订单号的Map
     * @throws Exception 抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object createOrder(ProductDTO productDTO) throws Exception {
        // 打印日志，开始生成订单
        log.info("开始生成订单");

        //根据商品ID和支付类型创建订单
        OrderInfo orderInfo = orderInfoService.createOrderByProductId(productDTO, PayType.WXPAY.getType());

        //获取订单二维码URL
        String codeUrl = orderInfo.getCodeUrl();

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

        Map<String, String> resultMap = sendRequest(url, requestWxCodeDTO, "Native下单");

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
     * 发送请求并获取响应结果
     *
     * @param url         请求的URL
     * @param requestData 请求参数集合
     * @param logInfo     日志信息
     * @return 响应结果映射
     * @throws IOException 如果请求失败，抛出异常
     */
    private Map<String, String> sendRequest(String url, Object requestData, String logInfo) throws IOException {

        // 创建POST请求
        HttpPost httpPost = new HttpPost(url);

        // 将请求参数转换为JSON格式
        String jsonParams = gson.toJson(requestData);

        // 打印请求参数信息
        log.info("请求参数 ===> {}", jsonParams);

        // 创建字符串实体，设置编码格式和内容类型
        StringEntity entity = new StringEntity(jsonParams, "utf-8");
        entity.setContentType("application/json");

        // 设置请求实体和头部信息
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        // 发送请求并获取响应
        try (CloseableHttpResponse response = wxPayClient.execute(httpPost)) {
            // 获取响应内容和状态码
            // 响应体
            String bodyAsString = EntityUtils.toString(response.getEntity());
            // 响应状态码
            int statusCode = response.getStatusLine().getStatusCode();

            // 根据状态码处理响应结果
            if (statusCode == 200) {
                log.info("成功, 返回结果 = {}", bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                log.info("{}失败,响应码 = {},返回结果 = {}", logInfo, statusCode, bodyAsString);
                throw new IOException("request failed");
            }

            // 解析响应结果并返回
            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);

            return resultMap;
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
    public ResponseWxPayNotifyDTO handlePaymentNotification(HttpServletRequest request) throws IOException, GeneralSecurityException {

        // 实例化 Gson 对象，用于 JSON 数据解析
        Gson gson = new Gson();

        // 从请求中读取数据并转换为字符串格式
        String body = ImprovedHttpClientUtil.readData(request);

        // 将字符串数据转化为 Map 对象
        Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);

        // 从 Map 中获取支付通知 ID 并记录
        String requestId = (String) bodyMap.get("id");
        log.info("支付通知的id ===> {}", requestId);

        // 创建签名验证器对象
        WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest =
                new WechatPay2ValidatorForRequest(verifier, requestId, body);

        // 进行签名验证
        if (!wechatPay2ValidatorForRequest.validate(request)) {
            // 若验证失败，记录错误信息并返回错误消息
            log.error("通知验签失败");
            return new ResponseWxPayNotifyDTO(ERROR_CODE, ERROR_VALIDATION_FAILED_MSG);
        }

        // 验证成功，记录相关信息
        log.info("通知验签成功");

        // 创建支付桥接对象，用于处理具体的支付逻辑
        PayBridge wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        // 使用桥接对象处理订单
        wxPayNative.processOrder(bodyMap);

        // 处理完成，返回成功消息
        return new ResponseWxPayNotifyDTO(SUCCESS_CODE, SUCCESS_MSG);
    }


    /**
     * 处理订单
     *
     * @param bodyMap 请求体Map
     * @throws GeneralSecurityException 抛出安全异常
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("处理订单");

        // 解密报文
        String plainText = decryptFromResource(bodyMap);

        // 将明文转换成map
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String)plainTextMap.get("out_trade_no");

        // 在对业务数据进行状态检查和处理之前，
        // 要采用数据锁进行并发控制，
        // 以避免函数重入造成的数据混乱
        if(lock.tryLock()){
            try {
                // 处理重复的通知
                // 接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                if(!OrderStatus.NOTPAY.getType().equals(orderStatus)){
                    return;
                }

                // 更新订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);

                // 记录支付日志
                paymentInfoService.createPaymentInfo(plainText);
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

}