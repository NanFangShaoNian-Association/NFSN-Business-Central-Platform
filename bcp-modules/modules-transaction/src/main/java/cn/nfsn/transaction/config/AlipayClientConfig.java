package cn.nfsn.transaction.config;

import com.alipay.api.*;
import lombok.Setter;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @ClassName: AlipayClientConfig
 * @Description: 支付宝客户端配置类，用于加载支付宝沙箱环境的配置文件并初始化支付宝客户端
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-02 15:29
 **/
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="alipay")
public class AlipayClientConfig {

    /**
     * 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
     */
    private String appId;

    /**
     * 商户PID,卖家支付宝账号ID
     */
    private String sellerId;

    /**
     * 支付宝网关
     */
    private String gatewayUrl;

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 接口内容加密秘钥，对称秘钥
     */
    private String contentKey;

    /**
     * 页面跳转同步通知页面路径
     */
    private String returnUrl;

    /**
     * 服务器异步通知页面路径
     */
    private String notifyUrl;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        // 创建支付宝配置对象
        AlipayConfig alipayConfig = new AlipayConfig();

        // 设置网关地址
        alipayConfig.setServerUrl(gatewayUrl);
        // 设置应用Id
        alipayConfig.setAppId(appId);
        // 设置应用私钥
        alipayConfig.setPrivateKey(merchantPrivateKey);
        // 设置请求格式，固定值json
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
        // 设置字符集
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
        // 设置支付宝公钥
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        // 设置签名类型
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);

        System.out.println(alipayConfig);

        // 根据支付宝配置对象构造支付宝客户端对象并返回
        return new DefaultAlipayClient(alipayConfig);
    }
}
