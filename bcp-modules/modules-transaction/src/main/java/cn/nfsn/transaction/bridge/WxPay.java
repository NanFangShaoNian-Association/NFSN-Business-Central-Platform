package cn.nfsn.transaction.bridge;

import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.model.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @ClassName: WxPay
 * @Description: 微信支付类，继承自Pay类，实现了具体的支付逻辑
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-13 17:57
 **/
@Component("wxPay")
public class WxPay extends PayBridge {

   /**
    * 构造函数，初始化支付模式
    * @param payMode 支付模式接口
    */
   public WxPay(@Qualifier("wxPayNative") IPayMode payMode) {
      super(payMode);
   }

   /**
    * 创建订单，调用Native支付接口
    *
    * @param productDTO 商品信息
    * @return 包含 code_url 和订单号的Map
    * @throws Exception 抛出异常
    */
   @Override
   public Object createOrder(ProductDTO productDTO) throws Exception {
      return payMode.createOrder(productDTO);
   }

   /**
    * 处理订单
    *
    * @param bodyMap 请求体Map
    * @throws GeneralSecurityException 抛出安全异常
    */
   @Override
   public void processOrder(Map<String, Object> bodyMap, OrderStatus successStatus) throws GeneralSecurityException {
        payMode.processOrder(bodyMap, successStatus);
   }

   /**
    * 取消订单
    *
    * @param orderNo    订单号
    * @throws Exception 抛出异常
    */
   @Override
   public void cancelOrder(String orderNo) throws Exception {
      payMode.cancelOrder(orderNo);
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
      payMode.refund(orderNo, reason);
   }

   /**
    * 处理退款单
    *
    * @param bodyMap 请求体Map，包含了微信通知的退款信息
    * @throws Exception 抛出异常，包括但不限于解密错误、数据库操作失败等
    */
   @Override
   public void processRefund(Map<String, Object> bodyMap, OrderStatus successStatus) throws Exception {
        payMode.processRefund(bodyMap, successStatus);
   }
}