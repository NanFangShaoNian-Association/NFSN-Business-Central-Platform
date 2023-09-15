package cn.nfsn.system.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: SMSConfig
 * @description: 定义SMS短信信息的配置类
 */
@Configuration
public class SMSConfig {

    @Value("${sms.accessKeyId}")
    private String accessKeyId;

    @Value("${sms.secret}")
    private String secret;

    @Value("${sms.regionId}")
    private String regionId;   // 短信服务器区域

    @Bean
    public StaticCredentialProvider staticCredentialProvider() {
        return StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(secret)
                .build());
    }

    @Bean
    public AsyncClient asyncClient(StaticCredentialProvider provider) {
        return AsyncClient.builder()
                .region(regionId)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();
    }

}
