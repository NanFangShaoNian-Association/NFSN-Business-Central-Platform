package cn.nfsn.common.core.exception;

import java.io.IOException;
import org.apache.http.HttpResponse;

/**
 * @ClassName: UnexpectedHttpStatusException
 * @Description: 当HTTP响应状态出现意外时抛出的异常类，它继承自IOException。
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-04 00:00
 */
public class UnexpectedHttpStatusException extends IOException {
    /**
     * HTTP响应对象
     */
    private final HttpResponse response;

    /**
     * 构造方法初始化异常信息和HTTP响应对象
     *
     * @param message  异常信息
     * @param response HTTP响应对象
     */
    public UnexpectedHttpStatusException(String message, HttpResponse response) {
        super(message);
        this.response = response;
    }

    /**
     * 获取HTTP响应对象
     *
     * @return HTTP响应对象
     */
    public HttpResponse getResponse() {
        return response;
    }
}