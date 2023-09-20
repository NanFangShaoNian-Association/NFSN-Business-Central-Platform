package cn.nfsn.transaction.service.impl;

import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.utils.OrderNoUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nfsn.transaction.model.entity.RefundInfo;
import cn.nfsn.transaction.service.RefundInfoService;
import cn.nfsn.transaction.mapper.RefundInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Atnibam Aitay
* @description 针对表【refund_info】的数据库操作Service实现
* @createDate 2023-09-07 14:15:09
*/
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo>
    implements RefundInfoService{

    @Resource
    private OrderInfoServiceImpl orderInfoService;

    @Resource
    private RefundInfoMapper refundInfoMapper;

    /**
     * 根据订单号和退款原因创建阿里支付的退款订单
     *
     * @param orderNo 订单编号
     * @param reason 退款原因
     * @return 返回创建的退款订单信息
     */
    @Override
    public RefundInfo createRefundByOrderNo(String orderNo, String reason) {

        // 根据订单号获取订单信息
        OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);

        // 初始化退款订单信息
        RefundInfo refundInfo = new RefundInfo();

        // 设置订单编号
        refundInfo.setOrderNo(orderNo);

        // 设置退款单编号，通过工具类生成
        refundInfo.setRefundNo(OrderNoUtils.getRefundNo());

        // 设置原订单金额(单位：分)
        refundInfo.setTotalFee(orderInfo.getTotalFee());

        // 设置退款金额(单位：分)，默认为原订单金额
        refundInfo.setRefund(orderInfo.getTotalFee());

        // 设置退款原因
        refundInfo.setReason(reason);

        // 保存退款订单到数据库
        refundInfoMapper.insert(refundInfo);

        return refundInfo;
    }
}




