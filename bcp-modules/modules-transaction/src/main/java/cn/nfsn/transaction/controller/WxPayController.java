package cn.nfsn.transaction.controller;

import cn.nfsn.common.core.domain.R;
import cn.nfsn.transaction.bridge.PayBridge;
import cn.nfsn.transaction.factory.PayFactory;
import cn.nfsn.transaction.model.dto.ProductDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

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
        Map<String, Object> result = null;

        // 如果支付方式实例创建成功，调用其创建订单的方法
        if (wxPayNative != null) {
            result = wxPayNative.createOrder(productDTO);
        }

        //返回支付二维码连接和订单号
        return R.ok(result);
    }

}