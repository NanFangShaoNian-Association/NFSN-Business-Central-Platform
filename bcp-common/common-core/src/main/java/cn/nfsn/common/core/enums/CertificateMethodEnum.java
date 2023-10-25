package cn.nfsn.common.core.enums;

/**
 * @author gaojianjie
 * @Description 用户登陆方式枚举
 */
public enum CertificateMethodEnum {
    //通过手机号和验证码登陆
    PHONE_CODE(1),
    //通过邮箱和验证码登陆
    EMAIL_CODE(2);

    private final int code;
    CertificateMethodEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CertificateMethodEnum fromCode(int code) {
        for (CertificateMethodEnum method : CertificateMethodEnum.values()) {
            if (method.getCode() == code) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid login method code: " + code);
    }
}
