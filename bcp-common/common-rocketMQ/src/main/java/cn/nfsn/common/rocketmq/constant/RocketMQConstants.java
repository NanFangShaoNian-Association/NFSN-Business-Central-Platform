package cn.nfsn.common.rocketmq.constant;

/**
 * @Author: gaojianjie
 * @Description TODO
 * @date 2023/8/22 20:26
 */
public class RocketMQConstants {
    /**
     * 以服务、消息的类型来划分topic，再细化的服务用tag来划分
     */
    public static final String DELAY_TOPIC = "delay_topic";

    //账号注销延迟tag
    public static final String LOGOUT_DELAY_TAG = "logout_delay_tag";
}
