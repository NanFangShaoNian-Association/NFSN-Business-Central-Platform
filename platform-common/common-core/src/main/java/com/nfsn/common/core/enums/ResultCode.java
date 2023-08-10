package com.nfsn.common.core.enums;

import org.springframework.http.HttpStatus;

/**
 * 状态码枚举，用于http响应中的状态码
 */
public enum ResultCode {
    /*成功状态码*/
    SUCCESS(200,"成功", HttpStatus.OK),
    /*参数错误：1001-1999*/
    PARAM_IS_INVALID(1001,"参数无效", HttpStatus.BAD_REQUEST),
    PARAM_IS_BLANK(1002,"参数为空", HttpStatus.BAD_REQUEST),
    PARAM_TYPE_BIND_ERROR(1003,"参数类型错误", HttpStatus.BAD_REQUEST),
    PARAM_NOT_COMPLETE(1004,"参数缺失", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_ERROR(1005,"两次密码输入不匹配", HttpStatus.BAD_REQUEST),
    /*用户错误：2001-2999*/
    USER_NOT_LOGGED_IN(2001,"用户未登录，访问的路径需要验证，请登录", HttpStatus.UNAUTHORIZED),
    USER_LOGIN_ERROR(2002,"账号不存在或密码错误", HttpStatus.UNAUTHORIZED),
    USER_ACCOUNT_FORBIDDEN(2003,"账号已被禁用", HttpStatus.UNAUTHORIZED),
    USER_HAS_EXISTED(2005,"用户已存在", HttpStatus.CONFLICT),
    USER_VERIFY_ERROR(2008,"验证码校验失败，请重新获取", HttpStatus.UNAUTHORIZED),
    USER_INSERT_ERROR(2010,"用户数据插入异常",HttpStatus.CONFLICT),
    USER_NOT_EXIST_BY_CODE(2012,"账号不存在,请注册", HttpStatus.NOT_FOUND),
    USER_REGISTRATION_TIME_OUT(2014,"用户注册超时，请重新注册",HttpStatus.UNPROCESSABLE_ENTITY),
    USER_ACCOUNT_INCONSISTENT(2015,"注册账号与发送验证码账号不一致",HttpStatus.BAD_REQUEST),
    USER_UPDATE_ERROR(2010,"用户数据更新异常",HttpStatus.CONFLICT),
    USER_LOGOUT_FAIL(2017,"用户注销失败",HttpStatus.CONFLICT),
    HTTP_METHOD_ERROR(3001, "Http请求方法错误", HttpStatus.BAD_REQUEST),
    PHONE_CODE_OOT(3002,"当天手机号发送验证码次数以达到上限，请24小时后重试",HttpStatus.TOO_MANY_REQUESTS),
    SMS_INTERRUPTED_OR_EXECUTION_ERROR(4001,"阿里云服务被中断或执行错误",HttpStatus.BAD_REQUEST),

    /*服务器内部错误*/
    INTERNAL_ERROR(5001,"服务器内部错误，请联系开发人员", HttpStatus.INTERNAL_SERVER_ERROR);

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 接口调用提示信息
     */
    private final String message;

    private final HttpStatus status;

    ResultCode(Integer code, String message, HttpStatus status){
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() { return status; }
}