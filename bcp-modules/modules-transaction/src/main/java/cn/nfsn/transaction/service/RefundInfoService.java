package cn.nfsn.transaction.service;

import cn.nfsn.transaction.model.entity.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Atnibam Aitay
* @description 针对表【refund_info】的数据库操作Service
* @createDate 2023-09-07 14:15:09
*/
public interface RefundInfoService extends IService<RefundInfo> {

    /**
     * 根据订单号和退款原因创建阿里支付的退款订单
     *
     * @param orderNo 订单编号
     * @param reason 退款原因
     * @return 返回创建的退款订单信息
     */
    RefundInfo createRefundByOrderNo(String orderNo, String reason);

    /**
     * 根据提供的JSON内容更新对应的退款记录
     *
     * @param content JSON格式的退款记录信息
     */
    void updateRefund(String content);

    /**
     * 更新退款记录
     *
     * @param refundNo     退款单编号
     * @param content      响应结果内容
     * @param refundStatus 退款状态
     */
    void updateRefundForAliPay(String refundNo, String content, String refundStatus);
}
