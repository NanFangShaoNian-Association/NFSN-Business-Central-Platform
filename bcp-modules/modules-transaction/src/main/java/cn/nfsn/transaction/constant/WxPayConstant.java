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

    /**
     * Native下单
     */
    public static final String NATIVE_ORDER = "Native下单";

    /**
     * 关闭订单
     */
    public static final String CLOSE_ORDER = "关闭订单";

    /**
     * UTF-8编码
     */
    public static final String UTF8 = "utf-8";

    /**
     * application/json内容类型
     */
    public static final String APP_JSON = "application/json";

    /**
     * Accept
     */
    public static final String ACCEPT = "Accept";
}