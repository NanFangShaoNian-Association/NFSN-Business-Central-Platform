package cn.nfsn.common.core.exception;

import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.base.BaseException;

public class UserOperateException extends BaseException {
    public UserOperateException(ResultCode resultCode) {
        super(resultCode);
    }
}
