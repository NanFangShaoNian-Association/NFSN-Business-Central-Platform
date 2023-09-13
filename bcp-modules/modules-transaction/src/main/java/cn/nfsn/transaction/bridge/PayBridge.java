package cn.nfsn.transaction.bridge;

import cn.nfsn.transaction.model.dto.ProductDTO;

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
     * @return 包含code_url 和 订单号的Map
     * @throws Exception 抛出异常
     */
    public abstract Map<String, Object> createOrder(ProductDTO productDTO) throws Exception;
}