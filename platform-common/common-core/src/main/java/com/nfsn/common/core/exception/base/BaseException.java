package com.nfsn.common.core.exception.base;

import com.nfsn.common.core.enums.ResultCode;

/**
 * 基础异常
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -1L;
    public ResultCode resultCode;

    public BaseException() {
    }

    public BaseException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}