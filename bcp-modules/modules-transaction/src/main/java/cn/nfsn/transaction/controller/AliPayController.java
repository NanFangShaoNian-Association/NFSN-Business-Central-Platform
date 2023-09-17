package cn.nfsn.transaction.controller;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.transaction.bridge.PayBridge;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.factory.PayFactory;
import cn.nfsn.transaction.model.dto.ProductDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    /**
     * 统一收单下单并支付页面接口的调用
     *
     * @param productDTO 商品信息
     * @return 返回结果
     */
    @ApiOperation("统一收单下单并支付页面接口的调用")
    @PostMapping("/trade/page/pay")
    public R tradePagePay(@RequestBody ProductDTO productDTO) throws Exception {
        log.info("统一收单下单并支付页面接口的调用");
        // 使用工厂方法创建具体的支付方式实例
        PayBridge aliPayNative = payFactory.createPay(PayFactory.ALI_PAY_NATIVE);

        // 保存创建订单的结果
        Object result = null;

        // 如果支付方式实例创建成功，调用其创建订单的方法
        if (aliPayNative != null) {
            result = aliPayNative.createOrder(productDTO);
        }

        // 判断返回的结果类型并进行相应的处理
        if (result instanceof String) {
            //将form表单字符串返回给前端程序，之后前端将会调用自动提交脚本，进行表单的提交
            //此时，表单会自动提交到action属性所指向的支付宝开放平台中，从而为用户展示一个支付页面
            return R.ok((String) result);
        } else {
            throw new RuntimeException("Unexpected result type: " + result.getClass().getName());
        }
    }

    /**
     * 支付通知处理接口，用于处理支付宝的异步通知
     *
     * @param request 请求参数，包含支付宝异步通知中的全部信息
     * @return 处理结果，成功返回"success"，失败返回"failure"
     */
    @ApiOperation("支付通知")
    @PostMapping("/trade/notify")
    public String tradeNotify(@RequestParam HttpServletRequest request){
        // 使用工厂方法创建具体的支付方式实例
        PayBridge aliPayNative = payFactory.createPay(PayFactory.ALI_PAY_NATIVE);

        log.info("支付通知正在执行");

        try {
            aliPayNative.handlePaymentNotification(request, OrderStatus.NOTPAY);
            return "success";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "failure";
        }
    }

}