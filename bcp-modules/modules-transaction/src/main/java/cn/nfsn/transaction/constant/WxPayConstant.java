package cn.nfsn.transaction.constant;

/**
 * @ClassName: WxPayConstant
 * @Description: 微信支付常量
 * @Author: atnibamaitay
 * @CreateTime: 2023/9/8 0008 16:53
 **/
public class WxPayConstant {

    /**
     * 秘钥路径
     */
    public static final String PRIVATE_KEY_PATH = "bcp-modules/modules-transaction/src/main/resources/apiclient_key.pem";

    /**
     * 错误码
     */
    public static final String ERROR_CODE = "ERROR";

    /**
     * 成功码
     */
    public static final String SUCCESS_CODE = "SUCCESS";

    /**
     * 失败信息
     */
    public static final String ERROR_MSG = "失败";

    /**
     * 成功信息
     */
    public static final String SUCCESS_MSG = "成功";

    /**
     * 通知验签失败信息
     */
    public static final String ERROR_VALIDATION_FAILED_MSG = "通知验签失败";
}