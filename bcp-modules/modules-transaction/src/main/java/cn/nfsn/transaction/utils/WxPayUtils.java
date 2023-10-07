package cn.nfsn.transaction.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static cn.nfsn.transaction.constant.WxPayConstant.*;

/**
 * @ClassName: WxPayUtils
 * @Description: 微信支付工具类，主要负责发送请求并处理返回结果
 * @Author: atnibamaitay
 * @CreateTime: 2023-10-07 11:54
 **/
@Slf4j
public class WxPayUtils {

    /**
     * Gson实例，用于将对象转化为Json格式的字符串，或将Json格式的字符串转化为对象
     */
    private static final Gson gson = new Gson();

    /**
     * 发送请求到指定的URL，并处理返回的响应结果
     *
     * @param wxPayClient 微信支付客户端
     * @param url         请求的URL
     * @param requestData 请求数据
     * @param logInfo     日志信息
     * @return 以Map形式返回的响应结果
     * @throws IOException 抛出可能发生的IO异常
     */
    public static Map<String, String> sendRequest(CloseableHttpClient wxPayClient, String url, Object requestData, String logInfo) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        String jsonParams = gson.toJson(requestData);

        // 记录请求参数日志
        log.info("请求参数 ===> {}", jsonParams);

        StringEntity entity = new StringEntity(jsonParams, UTF8);
        entity.setContentType(APP_JSON);

        httpPost.setEntity(entity);
        httpPost.setHeader(ACCEPT, APP_JSON);

        try (CloseableHttpResponse response = wxPayClient.execute(httpPost)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String bodyAsString = "";

            if (entity != null && !logInfo.equals("关闭订单")) {
                bodyAsString = EntityUtils.toString(response.getEntity());
            }

            // 根据状态码进行不同的处理
            if (statusCode == 200) {
                log.info("成功, 返回结果 = {}", bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                log.info("{}失败,响应码 = {},返回结果 = {}", logInfo, statusCode, bodyAsString);
                throw new IOException("request failed");
            }

            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);

            return resultMap;
        }
    }
}
