package com.nfsn.common.core.exception;

import com.nfsn.common.core.enums.ResultCode;
import com.nfsn.common.core.exception.base.BaseException;

public class UserOperateException extends BaseException {
    public UserOperateException(ResultCode resultCode) {
        super(resultCode);
    }
}
