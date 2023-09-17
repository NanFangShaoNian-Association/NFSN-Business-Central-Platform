package cn.nfsn.common.core.exception;

import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.base.BaseException;

/**
 * @ClassName: AliPayException
 * @Description: 支付宝支付异常类，继承自BaseException
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-11 15:16
 **/
public class AliPayException extends BaseException {
    /**
     * 构造方法
     *
     * @param resultCode 异常对应的结果代码
     */
    public AliPayException(ResultCode resultCode) {
        super(resultCode);
    }
}