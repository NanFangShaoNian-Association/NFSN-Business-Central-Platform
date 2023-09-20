package cn.nfsn.transaction.controller;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.transaction.bridge.PayBridge;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.factory.PayFactory;
import cn.nfsn.transaction.model.dto.CancelOrderDTO;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.RefundDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @ClassName: AliPayController
 * @Description: 支付宝控制器，包含支付宝的关键操作如下单、支付通知、取消订单、查询订单、申请退款、查询退款、退款结果通知和账单相关操作。
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/14 0014 23:14
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/ali-pay")
@Api(tags = "网站支付宝")
@Slf4j
public class AliPayController {

    @Resource
    private PayFactory payFactory;

    private PayBridge aliPayNative;

    /**
     * 使用@PostConstruct注解来初始化aliPayNative
     */
    @PostConstruct
    public void init() {
        this.aliPayNative = payFactory.createPay(PayFactory.ALI_PAY_NATIVE);
    }

    /**
     * 统一收单下单并支付页面
     *
     * @param productDTO 商品信息
     * @return 返回结果
     */
    @ApiOperation("统一收单下单并支付页面")
    @PostMapping("/trade/page/pay")
    public R tradePagePay(@RequestBody @Valid ProductDTO productDTO) throws Exception {

        log.info("统一收单下单并支付页面接口的被调用");

        Object result = aliPayNative.createOrder(productDTO);

        if (result instanceof String) {
            return R.ok((String) result);
        } else {
            throw new RuntimeException("Unexpected result type: " + result.getClass().getName());
        }
    }

    /**
     * 处理支付通知接口，用于处理支付宝的异步通知
     *
     * @param request 请求参数，包含支付宝异步通知中的全部信息
     * @return 处理结果，成功返回"success"，失败返回"failure"
     */
    @ApiOperation("支付通知处理")
    @PostMapping("/trade/notify")
    public String tradeNotify(HttpServletRequest request){

        log.info("支付通知接口被调用");

        try {
            aliPayNative.handlePaymentNotification(request, OrderStatus.NOTPAY);
            return "success";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "failure";
        }
    }

    /**
     * 取消订单
     *
     * @param cancelOrderDTO 请求参数，包含订单编号
     * @return R 返回结果
     */
    @ApiOperation("取消订单")
    @PostMapping("/trade/close")
    public R cancel(@RequestBody @Valid CancelOrderDTO cancelOrderDTO) throws Exception {

        log.info("取消订单接口被调用");

        aliPayNative.cancelOrder(cancelOrderDTO.getOrderNo());
        return R.ok();
    }

    /**
     * 退款
     *
     * @param refundDTO 请求参数，包含订单编号和退款原因
     * @return R 返回结果
     */
    @ApiOperation("退款")
    @PostMapping("/trade/refund")
    public R refunds(@RequestBody @Valid RefundDTO refundDTO) throws Exception {
        log.info("退款接口被调用");

        aliPayNative.refund(refundDTO.getOrderNo(), refundDTO.getReason());
        return R.ok();
    }

}