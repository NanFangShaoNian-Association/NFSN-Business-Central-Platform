package cn.nfsn.common.rocketmq.enums;

/**
 * 消息状态枚举
 */
public enum EnumMessageStatus {

    SENDING(0, "发送中"),
    RETRYING(1, "重试中"),
    FAILED(2, "发送失败"),
    SUCCESS(3, "发送成功"),
    ;

    private final Integer code;
    private final String name;

    EnumMessageStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}