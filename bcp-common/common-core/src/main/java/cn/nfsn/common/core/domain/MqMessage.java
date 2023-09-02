package cn.nfsn.common.core.domain;

import java.io.Serializable;

public class MqMessage implements Serializable {

    private String messageKey;
    private String messageBody;
    private static final long serialVersionUID = 1L;

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public MqMessage(String messageKey, String messageBody) {
        this.messageKey = messageKey;
        this.messageBody = messageBody;
    }

    public MqMessage() {
    }

    @Override
    public String toString() {
        return "MqMessage{" +
                "messageKey='" + messageKey + '\'' +
                ", messageBody='" + messageBody + '\'' +
                '}';
    }
}
