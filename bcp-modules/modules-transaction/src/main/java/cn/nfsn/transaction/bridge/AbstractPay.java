package cn.nfsn.transaction.bridge;

import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.ResponsePayNotifyDTO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @InterfaceName: AbstractPay
 * @Description: 支付方式的抽象类，定义了支付操作相关的诸多方法
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/8 0008 13:22
 **/
public abstract class AbstractPay {

    protected IPayMode payMode;

    public AbstractPay(IPayMode payMode) {
        this.payMode = payMode;
    }

    /**
     * 创建订单
     *
     * @param productDTO 商品信息
     * @return           包含微信支付的订单二维码链接和订单号或者支付宝支付的HTML表单代码的Map
     * @throws Exception 抛出异常
     */
    public abstract Object createOrder(ProductDTO productDTO) throws Exception;

    /**
     * 处理来自支付系统方的支付通知
     *
     * @param request                    HttpServletRequest 对象，表示一个 HTTP 请求
     * @param successStatus              订单状态
     * @return ResponsePayNotifyDTO      响应对象，包含响应码和信息
     * @throws IOException               如果读取请求数据时出错
     * @throws GeneralSecurityException  如果在验证签名过程中出现安全异常
     */
    public abstract ResponsePayNotifyDTO paymentNotificationHandler(HttpServletRequest request, OrderStatus successStatus) throws IOException, GeneralSecurityException;

    /**
     * 取消订单
     *
     * @param orderNo    订单号
     * @throws Exception 抛出异常
     */
    public abstract void cancelOrder(String orderNo) throws Exception;

    /**
     * 退款
     *
     * @param orderNo    订单编号，不能为空
     * @param reason     退款原因，不能为空
     * @throws Exception 若退款过程中发生错误，则抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public abstract void refund(String orderNo, String reason) throws Exception;
}