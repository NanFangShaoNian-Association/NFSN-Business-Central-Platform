package cn.nfsn.transaction.utils;

import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;

import static com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders.*;

/**
 * @ClassName: WechatPay2ValidatorForRequest
 * @Description: 该类主要用于微信支付请求的验证，包括参数校验、过期时间校验和签名校验
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-01 15:18:43
 **/
public class WechatPay2ValidatorForRequest {

    /**
     * 日志记录器对象
     */
    protected static final Logger log = LoggerFactory.getLogger(WechatPay2ValidatorForRequest.class);
    
    /**
     * 应答超时时间，单位为分钟
     */
    protected static final long RESPONSE_EXPIRED_MINUTES = 5;
    
    /**
     * 验证器对象
     */
    protected final Verifier verifier;
    
    /**
     * 请求id字符串
     */
    protected final String requestId;
    
    /**
     * 请求体内容
     */
    protected final String body;

    /**
     * 构造函数
     *
     * @param verifier  验证器对象
     * @param requestId 请求id字符串
     * @param body      请求体内容
     */
    public WechatPay2ValidatorForRequest(Verifier verifier, String requestId, String body) {
        this.verifier = verifier;
        this.requestId = requestId;
        this.body = body;
    }

    /**
     * 参数错误异常处理方法
     *
     * @param message 异常提示信息
     * @param args    变长参数
     * @return IllegalArgumentException 对象
     */
    protected static IllegalArgumentException parameterError(String message, Object... args) {
        message = String.format(message, args);
        return new IllegalArgumentException("parameter error: " + message);
    }

    /**
     * 验证失败异常处理方法
     *
     * @param message 异常提示信息
     * @param args    变长参数
     * @return IllegalArgumentException 对象
     */
    protected static IllegalArgumentException verifyFail(String message, Object... args) {
        message = String.format(message, args);
        return new IllegalArgumentException("signature verify fail: " + message);
    }

    /**
     * 验证请求方法
     *
     * @param request HttpServletRequest对象
     * @return boolean 验证是否通过
     * @throws IOException IO异常
     */
    public final boolean validate(HttpServletRequest request) throws IOException {
        try {
            // 处理请求参数
            validateParameters(request);

            // 构造验签名串
            String message = buildMessage(request);

            String serial = request.getHeader(WECHAT_PAY_SERIAL);
            String signature = request.getHeader(WECHAT_PAY_SIGNATURE);

            // 验签
            if (!verifier.verify(serial, message.getBytes(StandardCharsets.UTF_8), signature)) {
                throw verifyFail("serial=[%s] message=[%s] sign=[%s], request-id=[%s]",
                        serial, message, signature, requestId);
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 校验请求参数方法
     *
     * @param request HttpServletRequest对象
     */
    protected final void validateParameters(HttpServletRequest request) {

        // NOTE: ensure HEADER_WECHAT_PAY_TIMESTAMP at last
        String[] headers = {WECHAT_PAY_SERIAL, WECHAT_PAY_SIGNATURE, WECHAT_PAY_NONCE, WECHAT_PAY_TIMESTAMP};

        String header = null;
        for (String headerName : headers) {
            header = request.getHeader(headerName);
            if (header == null) {
                throw parameterError("empty [%s], request-id=[%s]", headerName, requestId);
            }
        }

        // 判断请求是否过期
        String timestampStr = header;
        try {
            Instant responseTime = Instant.ofEpochSecond(Long.parseLong(timestampStr));
            // 拒绝过期请求
            if (Duration.between(responseTime, Instant.now()).abs().toMinutes() >= RESPONSE_EXPIRED_MINUTES) {
                throw parameterError("timestamp=[%s] expires, request-id=[%s]", timestampStr, requestId);
            }
        } catch (DateTimeException | NumberFormatException e) {
            throw parameterError("invalid timestamp=[%s], request-id=[%s]", timestampStr, requestId);
        }
    }

    /**
     * 构建验签名串方法
     *
     * @param request HttpServletRequest对象
     * @return String 验签名串
     * @throws IOException IO异常
     */
    protected final String buildMessage(HttpServletRequest request) throws IOException {
        String timestamp = request.getHeader(WECHAT_PAY_TIMESTAMP);
        String nonce = request.getHeader(WECHAT_PAY_NONCE);
        return timestamp + "\n"
                + nonce + "\n"
                + body + "\n";
    }

    /**
     * 获取响应体内容
     *
     * @param response CloseableHttpResponse对象
     * @return String 响应体内容
     * @throws IOException IO异常
     */
    protected final String getResponseBody(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return (entity != null && entity.isRepeatable()) ? EntityUtils.toString(entity) : "";
    }

}
