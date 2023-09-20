package cn.nfsn.transaction.controller;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.transaction.bridge.PayBridge;
import cn.nfsn.transaction.enums.OrderStatus;
import cn.nfsn.transaction.factory.PayFactory;
import cn.nfsn.transaction.model.dto.ProductDTO;
import cn.nfsn.transaction.model.dto.ResponsePayNotifyDTO;
import cn.nfsn.transaction.service.OrderInfoService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static cn.nfsn.common.core.enums.ResultCode.ORDER_PAID;
import static cn.nfsn.common.core.enums.ResultCode.ORDER_PAYING;
import static cn.nfsn.transaction.constant.WxPayConstant.*;

/**
 * @ClassName: WxPayController
 * @Description: 微信支付控制器，包含微信支付的关键操作如下单、支付通知、取消订单、查询订单、申请退款、查询退款、退款结果通知和账单相关操作。
 * @Author: atnibamaitay
 * @CreateTime: 2023-08-31 22:25:58
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags = "网站微信支付APIv3")
@Slf4j
public class WxPayController {

    @Resource
    private PayFactory payFactory;

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * Native下单，调用统一下单API，生成支付二维码
     *
     * @param productDTO 商品信息
     * @return 返回响应实体R，携带支付二维码连接和订单号
     * @throws Exception 抛出异常
     */
    @ApiOperation("调用统一下单API，生成支付二维码")
    @PostMapping("/native")
    public R nativePay(@RequestBody ProductDTO productDTO) throws Exception {
        log.info("发起支付请求 v3");

        // 使用工厂方法创建具体的支付方式实例
        PayBridge wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        // 保存创建订单的结果
        Object result = null;

        // 如果支付方式实例创建成功，调用其创建订单的方法
        if (wxPayNative != null) {
            result = wxPayNative.createOrder(productDTO);
        }

        // 判断返回的结果类型并进行相应的处理
        if (result instanceof Map) {
            //返回支付二维码连接和订单号
            return R.ok((Map<String, Object>) result);
        } else {
            throw new RuntimeException("Unexpected result type: " + result.getClass().getName());
        }
    }

    /**
     * 查询本地订单状态。根据传入的订单号，查询对应的订单状态，然后返回相应的结果。
     *
     * @param orderNo 订单编号
     * @return 返回一个封装了查询结果的R对象，如果订单支付成功，则返回的R对象中的message为"支付成功"，否则为"支付中"
     */
    @ApiOperation("查询本地订单状态")
    @GetMapping("/query-order-status/{orderNo}")
    public R queryOrderStatus(@PathVariable String orderNo){

        // 从orderInfoService服务中获取指定订单号的订单状态
        String orderStatus = orderInfoService.getOrderStatus(orderNo);

        // 判断获取到的订单状态是否为“成功”
        if(OrderStatus.SUCCESS.getType().equals(orderStatus)){
            return R.ok(ORDER_PAID);
        }

        // 如果订单状态不是“成功”，则返回一个R.ok()对象，其中的message为“支付中”
        return R.ok(ORDER_PAYING);
    }

    /**
     * 支付通知的 API 接口.
     * 微信支付通过支付通知接口将用户支付成功消息通知给商户
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 返回响应消息的 JSON 字符串
     */
    @ApiOperation("支付通知")
    @PostMapping("/native/notify")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response) {
        // 通过工厂方法创建一个具体的支付方式实例，这里为微信原生支付
        PayBridge wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        Gson gson = new Gson();
        try {
            // 调用拿到的实例进行处理
            ResponsePayNotifyDTO result = wxPayNative.handlePaymentNotification(request, OrderStatus.SUCCESS);

            // 根据返回结果设置响应状态，并返回相应消息
            if (SUCCESS_CODE.equals(result.getCode())) {
                response.setStatus(200);
            } else {
                response.setStatus(500);
            }
            return gson.toJson(result);

        } catch (Exception e) {
            // 如果处理过程中出现异常，打印异常堆栈，设置响应状态码为 500，并返回错误消息
            e.printStackTrace();
            response.setStatus(500);
            return gson.toJson(new ResponsePayNotifyDTO(ERROR_CODE, ERROR_MSG));
        }

    }

    /**
     * 用户取消订单
     *
     * @param orderNo 订单编号
     * @return R对象，包含操作结果信息
     * @throws Exception 可能出现的异常
     */
    @ApiOperation("用户取消订单")
    @PostMapping("/cancel/{orderNo}")
    public R cancel(@PathVariable String orderNo) throws Exception {

        // 通过工厂方法创建一个具体的支付方式实例，这里为微信原生支付
        PayBridge wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        // 记录日志，标记正在进行的操作为"取消订单"
        log.info("取消订单");

        // 调用支付服务，进行订单取消操作
        wxPayNative.cancelOrder(orderNo);

        // 返回成功响应，附带消息为"订单已取消"
        return R.ok("订单已取消");
    }

    /**
     * 申请退款操作
     *
     * @param orderNo 订单编号
     * @param reason 退款原因
     * @return R 返回类型为通用返回对象R，包含了处理的结果信息
     * @throws Exception 抛出异常
     */
    @ApiOperation("申请退款")
    @PostMapping("/refunds/{orderNo}/{reason}")
    public R refunds(@PathVariable String orderNo, @PathVariable String reason) throws Exception {
        // 通过工厂方法创建一个具体的支付方式实例，这里为微信原生支付
        PayBridge wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        // 记录日志信息，表示开始进行退款操作
        log.info("申请退款");

        // 调用微信支付服务，进行退款操作，参数为订单编号和退款原因
        wxPayNative.refund(orderNo, reason);

        // 返回成功的通用返回对象
        return R.ok();
    }

    /**
     * 退款结果通知接口
     * 当退款状态发生改变后，微信会把相关退款结果发送给商户。商户需要实现此接口以获取这些通知。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 返回响应消息的 JSON 字符串
     */
    @ApiOperation("退款结果通知")
    @PostMapping("/refunds/notify")
    public String refundsNotify(HttpServletRequest request, HttpServletResponse response) {
        // 记录日志：开始执行退款通知处理
        log.info("退款通知执行");

        // 通过工厂方法创建一个具体的支付方式实例，这里为微信原生支付
        PayBridge wxPayNative = payFactory.createPay(PayFactory.WX_PAY_NATIVE);

        try {
            // 调用拿到的实例进行处理
            ResponsePayNotifyDTO result = wxPayNative.handlePaymentNotification(request, OrderStatus.REFUND_SUCCESS);

            // 判断返回结果
            if (result.getCode().equals(SUCCESS_CODE)) {
                // 成功处理后返回成功信息
                response.setStatus(200);
            } else {
                // 验签失败，记录日志并返回错误信息
                response.setStatus(500);
            }

            return new Gson().toJson(result);

        } catch (Exception e) {
            // 出现异常时打印错误堆栈，并返回错误信息
            e.printStackTrace();
            response.setStatus(500);
            return new Gson().toJson(new ResponsePayNotifyDTO("ERROR", "失败"));
        }
    }

}