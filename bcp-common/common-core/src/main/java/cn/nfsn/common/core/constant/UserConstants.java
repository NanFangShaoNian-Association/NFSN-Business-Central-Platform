package cn.nfsn.common.core.constant;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/11 23:04
 */
public class UserConstants {
    /**
     * 正常状态
     */
    public static final String NORMAL = "0";

    /**
     * 注销状态
     */
    public static final String LOGOUT = "1";

    /**
     * 取消注销
     */
    public static final String CANCEL_LOGOUT = "2";

    /**
     * 用户冻结状态
     */
    public static final String USER_DISABLE = "2";

    /**
     * 头像格式
     */
    public static final String AVATAR_FORMAT = "data:image/jpg;base64";

    /**
     * 默认头像
     */
    public static final String DEFAULT_HEAD_PICTURE = "classpath:static/text/head_picture_base64";

    /**
     * 登陆注册功能邮箱验证码前缀
     */
    public static final int PREFIX_TYPE_1  = 1;

    /**
     * 账号绑定功能邮箱验证码前缀
     */
    public static final int PREFIX_TYPE_2  = 2;

}
