package cn.nfsn.transaction.bridge;

import cn.nfsn.transaction.model.dto.ProductDTO;
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
}
