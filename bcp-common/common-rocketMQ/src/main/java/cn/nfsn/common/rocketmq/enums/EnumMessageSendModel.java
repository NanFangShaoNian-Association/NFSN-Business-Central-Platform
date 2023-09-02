package cn.nfsn.common.rocketmq.enums;

public enum EnumMessageSendModel {

    SYNC("SYNC", "同步发送，发送消息后当前线程需要等待mq的结果"),
    ASYNC("ASYNC", "异步发送，发送消息后当前线程结束，在异步线程接受broker的结果"),
    ONEWAY("ONEWAY", "单向发送，发送消息后不需要等broker的结果"),
    ORDER("ORDER", "顺序发送，发送消息后当前线程结束，在异步线程接受broker的结果");


    private final String code;
    private final String name;

    EnumMessageSendModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}