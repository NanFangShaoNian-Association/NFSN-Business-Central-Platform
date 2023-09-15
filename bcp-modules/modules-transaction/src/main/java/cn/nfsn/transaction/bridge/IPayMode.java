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
 * @ClassName: IPayMode
 * @Description: 支付方式的接口，定义了创建订单的方法
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-13 22:15
 **/
public interface IPayMode {

   /**
    * 创建订单的方法
    * @param productDTO 商品数据传输对象,包含商品相关信息
    * @return           返回一个包含订单信息的Map对象
    * @throws Exception 如果在创建订单过程中出现问题，将抛出异常
    */
   Object createOrder(ProductDTO productDTO) throws Exception;

   /**
    * 处理微信支付通知，验证请求的有效性，并进行订单处理.
    *
    * @param request HttpServletRequest 对象，表示一个 HTTP 请求
    * @return ResponseWxPayNotifyDTO    响应对象，包含响应码和信息
    * @throws IOException               如果读取请求数据时出错
    * @throws GeneralSecurityException  如果在验证签名过程中出现安全异常
    */
   ResponseWxPayNotifyDTO handlePaymentNotification(HttpServletRequest request, OrderStatus successStatus) throws IOException, GeneralSecurityException;

   /**
    * 处理订单
    *
    * @param bodyMap                   请求体Map
    * @throws GeneralSecurityException 抛出安全异常
    */
   @Transactional(rollbackFor = Exception.class)
   void processOrder(Map<String, Object> bodyMap, OrderStatus successStatus) throws GeneralSecurityException;

   /**
    * 取消订单
    *
    * @param orderNo    订单号
    * @throws Exception 抛出异常
    */
    void cancelOrder(String orderNo) throws Exception;

    /**
     * 根据订单号和退款原因进行退款操作
     *
     * @param orderNo 订单编号，不能为空
     * @param reason  退款原因，不能为空
     * @throws Exception 若退款过程中发生错误，则抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    void refund(String orderNo, String reason) throws Exception;

    /**
     * 处理退款单
     *
     * @param bodyMap 请求体Map，包含了微信通知的退款信息
     * @successStatus 成功状态
     * @throws Exception 抛出异常，包括但不限于解密错误、数据库操作失败等
     */
    @Transactional(rollbackFor = Exception.class)
    void processRefund(Map<String, Object> bodyMap, OrderStatus successStatus) throws Exception;
}
