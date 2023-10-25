package cn.nfsn.transaction.service;

import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName: OrderInfoService
 * @Description: 订单信息服务接口，定义了创建订单和存储订单二维码等方法
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-07 14:15
 **/
public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 根据商品id创建订单
     *
     * @param productDTO 商品信息
     * @param paymentType 支付类型
     * @return 返回新建的或者已存在但未支付的订单
     */
    OrderInfo createOrderByProductId(ProductDTO productDTO, String paymentType) throws InterruptedException;

    /**
     * 存储订单二维码
     *
     * @param orderNo 订单号
     * @param codeUrl 二维码地址
     */
    void saveCodeUrl(String orderNo, String codeUrl);

    /**
     * 根据订单号获取订单状态
     *
     * @param orderNo 订单号，不能为空
     * @return 返回该订单号对应的订单状态，如果订单不存在，则返回null
     */
    String getOrderStatus(String orderNo);

    /**
     * 根据订单编号更新订单状态
     *
     * @param orderNo     需要更新的订单的订单编号
     * @param orderStatus 更新后的订单状态
     */
    void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);

    /**
     * 根据订单号获取订单
     *
     * @param orderNo 订单号
     * @return 返回查询到的订单
     */
    OrderInfo getOrderByOrderNo(String orderNo);
}
