package cn.nfsn.transaction.bridge;

import cn.nfsn.transaction.model.dto.ProductDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: WeChat
 * @Description: 微信支付类，继承自Pay类，实现了具体的支付逻辑
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-13 17:57
 **/
@Component
public class WxPay extends PayBridge {

   /**
    * 构造函数，初始化支付模式
    * @param payMode 支付模式接口
    */
   public WxPay(IPayMode payMode) {
      super(payMode);
   }

   /**
    * 创建订单，调用Native支付接口
    *
    * @param productDTO 商品信息
    * @return 包含code_url 和 订单号的Map
    * @throws Exception 抛出异常
    */
   @Override
   public Map<String, Object> createOrder(ProductDTO productDTO) throws Exception {
      return payMode.createOrder(productDTO);
   }
}