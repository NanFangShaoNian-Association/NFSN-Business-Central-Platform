package com.nfsn.common.core.exception;

import com.nfsn.common.core.enums.ResultCode;
import com.nfsn.common.core.exception.base.BaseException;

public class SystemServiceException extends BaseException {
    public SystemServiceException(ResultCode resultCode) {
        super(resultCode);
    }
}