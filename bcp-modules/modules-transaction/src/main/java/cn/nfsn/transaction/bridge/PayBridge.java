package cn.nfsn.transaction.bridge;

import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.ResponseWxPayNotifyDTO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @InterfaceName: PayStrategy
 * @Description: 支付策略
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/8 0008 13:22
 **/
public abstract class PayBridge {

    protected IPayMode payMode;

    public PayBridge(IPayMode payMode) {
        this.payMode = payMode;
    }

    /**
     * 创建订单，调用Native支付接口
     *
     * @param productDTO 商品信息
     * @return 包含微信支付的订单二维码链接或者支付宝支付的HTML表单和订单号的Map
     * @throws Exception 抛出异常
     */
    public abstract Object createOrder(ProductDTO productDTO) throws Exception;

    /**
     * 处理订单
     *
     * @param bodyMap 请求体Map
     * @throws GeneralSecurityException 抛出安全异常
     */
    @Transactional(rollbackFor = Exception.class)
    public abstract void processOrder(Map<String, Object> bodyMap, OrderStatus successStatus) throws GeneralSecurityException;

    /**
     * 处理支付通知，验证请求的有效性，并进行订单处理.
     *
     * @param request HttpServletRequest 对象，表示一个 HTTP 请求
     * @return ResponseWxPayNotifyDTO 响应对象，包含响应码和信息
     * @throws IOException              如果读取请求数据时出错
     * @throws GeneralSecurityException 如果在验证签名过程中出现安全异常
     */
    public ResponseWxPayNotifyDTO handlePaymentNotification(HttpServletRequest request, OrderStatus successStatus) throws IOException, GeneralSecurityException {
        return payMode.handlePaymentNotification(request, successStatus);
    }

    /**
     * 取消订单
     *
     * @param orderNo    订单号
     * @throws Exception 抛出异常
     */
    public abstract void cancelOrder(String orderNo) throws Exception;

    /**
     * 根据订单号和退款原因进行退款操作
     *
     * @param orderNo 订单编号，不能为空
     * @param reason  退款原因，不能为空
     * @throws Exception 若退款过程中发生错误，则抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public abstract void refund(String orderNo, String reason) throws Exception;

    /**
     * 处理退款单
     *
     * @param bodyMap 请求体Map，包含了退款信息
     * @throws Exception 抛出异常，包括但不限于解密错误、数据库操作失败等
     */
    public abstract void processRefund(Map<String, Object> bodyMap, OrderStatus successStatus) throws Exception;
}