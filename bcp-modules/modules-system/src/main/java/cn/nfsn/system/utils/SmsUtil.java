package cn.nfsn.system.utils;

import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class SmsUtil {

    @Value("${sms.signName}")
    private String signName;

    @Value("${sms.templateCode}")
    private String templateCode;

    @Autowired
    private AsyncClient client;

    public void sendMsg(String phone, String code,String subject) {
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(signName)
                .templateCode(templateCode)
                .phoneNumbers(phone)
                .templateParam(subject+"{  \"code\":\"" + code + "\"}")
                .build();

        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        try {
            System.out.println(response.get());
            //log.info("阿里云短信响应信息：" + response.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new SystemServiceException(ResultCode.ACCOUNT_LOGOUT);
        }
    }
}
