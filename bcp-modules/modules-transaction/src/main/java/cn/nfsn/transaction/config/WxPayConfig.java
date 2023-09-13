package cn.nfsn.transaction.config;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

import static cn.nfsn.transaction.constant.WxPayConstant.PRIVATE_KEY_PATH;

/**
 * @ClassName: WxPayConfig
 * @Description: 微信支付相关配置类，包含了微信支付所需的基本参数及相应的获取方法
 * @Author: atnibamaitay
 * @CreateTime: 2023-08-31 16:49
 **/
@Configuration
@ConfigurationProperties(prefix="wxpay")
@Data
@Slf4j
public class WxPayConfig {

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户API证书序列号
     */
    private String mchSerialNo;

    /**
     * 商户私钥文件路径
     */
    private String privateKeyPath;

    /**
     * APIv3密钥
     */
    private String apiV3Key;

    /**
     * APPID
     */
    private String appid;

    /**
     * 微信服务器地址
     */
    private String domain;

    /**
     * 接收结果通知地址
     */
    private String notifyDomain;

    /**
     * 根据提供的私钥文件路径获取商户的私钥对象
     *
     * @param filePath 私钥文件路径
     * @return 返回PrivateKey对象
     */
    public PrivateKey getPrivateKey(String filePath){
        try {
            return PemUtil.loadPrivateKey(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("私钥文件不存在", e);
        }
    }

    /**
     * 创建并返回一个定时更新的签名验证器
     *
     * @return ScheduledUpdateCertificatesVerifier对象
     */
    @Bean
    public ScheduledUpdateCertificatesVerifier getVerifier(){

        log.info("获取签名验证器");

        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(PRIVATE_KEY_PATH);

        //私钥签名对象
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(mchSerialNo, privateKey);

        //身份认证对象
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(mchId, privateKeySigner);

        // 使用定时更新的签名验证器，不需要传入证书
        ScheduledUpdateCertificatesVerifier verifier = new ScheduledUpdateCertificatesVerifier(
                wechatPay2Credentials,
                apiV3Key.getBytes(StandardCharsets.UTF_8));

        return verifier;
    }


    /**
     * 创建并返回一个http请求对象
     *
     * @param verifier 签名验证器
     * @return CloseableHttpClient对象
     */
    @Bean(name = "wxPayClient")
    public CloseableHttpClient getWxPayClient(ScheduledUpdateCertificatesVerifier verifier){

        log.info("获取 HttpClient");

        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(PRIVATE_KEY_PATH);

        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(mchId, mchSerialNo, privateKey)
                .withValidator(new WechatPay2Validator(verifier));

        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        CloseableHttpClient httpClient = builder.build();

        return httpClient;
    }

    /**
     * 创建并返回一个跳过验签流程的http请求对象
     *
     * @return CloseableHttpClient对象
     */
    @Bean(name = "wxPayNoSignClient")
    public CloseableHttpClient getWxPayNoSignClient(){

        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(PRIVATE_KEY_PATH);

        //用于构造HttpClient
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                //设置商户信息
                .withMerchant(mchId, mchSerialNo, privateKey)
                //无需进行签名验证、通过withValidator((response) -> true)实现
                .withValidator((response) -> true);

        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        CloseableHttpClient httpClient = builder.build();

        log.info("== getWxPayNoSignClient END ==");

        return httpClient;
    }

}
