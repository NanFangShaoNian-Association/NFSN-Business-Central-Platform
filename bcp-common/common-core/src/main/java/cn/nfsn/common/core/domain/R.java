package cn.nfsn.common.core.domain;

import cn.nfsn.common.core.constant.Constants;
import cn.nfsn.common.core.enums.ResultCode;

import java.io.Serializable;

/**
 * @ClassName: R
 * @Description: 通用响应结果类，包含状态码、消息和数据信息
 * @Author: ruoyi
 * @CreateTime: 2023-09-08 14:04
 **/
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功的状态码，来自于常量类Constants
     */
    public static final int SUCCESS = Constants.SUCCESS;

    /**
     * 失败的状态码，来自于常量类Constants
     */
    public static final int FAIL = Constants.FAIL;

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 创建成功状态的响应结果，不包含数据
     */
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, Constants.SUCCESS_OPERA);
    }

    /**
     * 创建成功状态的响应结果，包含数据
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, Constants.SUCCESS_OPERA);
    }

    /**
     * 创建成功状态的响应结果，包含数据和自定义消息
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    /**
     * 创建失败状态的响应结果，不包含数据
     */
    public static <T> R<T> fail() {
        return restResult(null, FAIL, Constants.FAIL_OPERA);
    }

    /**
     * 创建失败状态的响应结果，包含自定义消息
     */
    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    /**
     * 创建失败状态的响应结果，包含数据
     */
    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, Constants.FAIL_OPERA);
    }

    /**
     * 创建失败状态的响应结果，包含数据和自定义消息
     */
    public static <T> R<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    /**
     * 创建失败状态的响应结果，包含自定义状态码和消息
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 创建响应结果
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    /**
     * 用于创建失败状态的响应结果，包含来自ResultCode的状态码和消息
     */
    public static R fail(ResultCode resultCode) {
        return restResult(null, resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 获取响应状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置响应状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置响应消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 检查响应结果是否为错误状态，通过isSuccess(ret)方法的返回值决定
     */
    public static <T> Boolean isError(R<T> ret) {
        return !isSuccess(ret);
    }

    /**
     * 检查响应结果是否为成功状态，判断依据是返回码是否等于SUCCESS
     */
    public static <T> Boolean isSuccess(R<T> ret) {
        return R.SUCCESS == ret.getCode();
    }
}