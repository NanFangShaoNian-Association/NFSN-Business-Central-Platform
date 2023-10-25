package cn.nfsn.transaction.service.impl;

import cn.nfsn.transaction.enums.WxRefundStatus;
import cn.nfsn.transaction.model.entity.OrderInfo;
import cn.nfsn.transaction.utils.OrderNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nfsn.transaction.model.entity.RefundInfo;
import cn.nfsn.transaction.service.RefundInfoService;
import cn.nfsn.transaction.mapper.RefundInfoMapper;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.nfsn.transaction.constant.AliPayConstant.REFUND_NO;

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

    /**
     * 获取申请退款超过指定分钟数但还未成功的退款订单
     *
     * @param minutes 指定的分钟数
     * @return 未成功的退款订单列表
     */
    public List<RefundInfo> getNoRefundOrderByDuration(int minutes) {
        //计算出minutes分钟之前的时间点
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));

        //构建查询条件
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("refund_status", WxRefundStatus.PROCESSING.getType());
        queryWrapper.le("create_time", instant);

        //查询并返回结果
        return refundInfoMapper.selectList(queryWrapper);
    }

    /**
     * 根据提供的JSON内容更新对应的退款记录
     *
     * @param content JSON格式的退款记录信息
     */
    @Override
    public void updateRefund(String content) {
        //将json字符串转换成Map
        Gson gson = new Gson();
        Map<String, String> resultMap = gson.fromJson(content, HashMap.class);
        System.out.println(resultMap);

        //构建查询条件
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("refund_no", resultMap.get("out_refund_no"));

        //创建并初始化要更新的退款记录信息
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setRefundId(resultMap.get("refund_id"));

        //查询退款和申请退款中的返回参数
        if (resultMap.get("status") != null) {
            refundInfo.setRefundStatus(resultMap.get("status"));
            refundInfo.setContentReturn(content);
        }
        //退款回调中的回调参数
        if (resultMap.get("refund_status") != null) {
            refundInfo.setRefundStatus(resultMap.get("refund_status"));
            refundInfo.setContentNotify(content);
        }

        //更新数据库中的退款记录
        refundInfoMapper.update(refundInfo, queryWrapper);
    }

    /**
     * 更新退款记录
     *
     * @param refundNo     退款单编号
     * @param content      响应结果内容
     * @param refundStatus 退款状态
     */
    @Override
    public void updateRefundForAliPay(String refundNo, String content, String refundStatus) {

        // 根据退款单编号构造查询条件
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(REFUND_NO, refundNo);

        // 构造要修改的退款信息对象，并设置退款状态与响应结果内容
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setRefundStatus(refundStatus);
        refundInfo.setContentReturn(content);

        // TODO: 还需要记录退款单ID


        // 使用查询条件和待更新的退款信息，进行退款记录的更新操作
        refundInfoMapper.update(refundInfo, queryWrapper);
    }

}




