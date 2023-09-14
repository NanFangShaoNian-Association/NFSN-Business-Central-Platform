package cn.nfsn.transaction.bridge;

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
    * @return 返回一个包含订单信息的Map对象
    * @throws Exception 如果在创建订单过程中出现问题，将抛出异常
    */
   Map<String, Object> createOrder(ProductDTO productDTO) throws Exception;

   /**
    * 处理微信支付通知，验证请求的有效性，并进行订单处理.
    *
    * @param request HttpServletRequest 对象，表示一个 HTTP 请求
    * @return ResponseWxPayNotifyDTO 响应对象，包含响应码和信息
    * @throws IOException              如果读取请求数据时出错
    * @throws GeneralSecurityException 如果在验证签名过程中出现安全异常
    */
   ResponseWxPayNotifyDTO handlePaymentNotification(HttpServletRequest request) throws IOException, GeneralSecurityException;

   /**
    * 处理订单
    *
    * @param bodyMap 请求体Map
    * @throws GeneralSecurityException 抛出安全异常
    */
   @Transactional(rollbackFor = Exception.class)
   void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException;
}
