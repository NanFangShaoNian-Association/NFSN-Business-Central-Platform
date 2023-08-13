package cn.nfsn.common.core.exception;

import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.base.BaseException;

public class SystemServiceException extends BaseException {
    public SystemServiceException(ResultCode resultCode) {
        super(resultCode);
    }
}