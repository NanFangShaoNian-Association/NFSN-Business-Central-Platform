package cn.nfsn.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName local_message_record
 */
@TableName(value ="local_message_record")
public class LocalMessageRecord implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 服务名称 eg:订单服务
     */
    private String service;

    /**
     * 业务名称 eg:创建订单
     */
    private String business;

    /**
     * 消息模式：SYNC，ASYNC，ONEWAY
     */
    private String model;

    /**
     * topic
     */
    private String topic;

    /**
     * tag
     */
    private String tags;

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息key
     */
    private String msgKey;

    /**
     * 消息体
     */
    private String body;

    /**
     * 发送状态  0:发送中  1:重试中  2:发送失败  3:发送成功
     */
    private Integer status;

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;

    /**
     * 当前重试次数
     */
    private Integer currentRetryTimes;

    /**
     * 发送成功时间
     */
    private Date sendSuccessTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 消息触发时间
     */
    private Date scheduledTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 服务名称 eg:订单服务
     */
    public String getService() {
        return service;
    }

    /**
     * 服务名称 eg:订单服务
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * 业务名称 eg:创建订单
     */
    public String getBusiness() {
        return business;
    }

    /**
     * 业务名称 eg:创建订单
     */
    public void setBusiness(String business) {
        this.business = business;
    }

    /**
     * 消息模式：SYNC，ASYNC，ONEWAY
     */
    public String getModel() {
        return model;
    }

    /**
     * 消息模式：SYNC，ASYNC，ONEWAY
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * tag
     */
    public String getTags() {
        return tags;
    }

    /**
     * tag
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * 消息id
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * 消息id
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * 消息key
     */
    public String getMsgKey() {
        return msgKey;
    }

    /**
     * 消息key
     */
    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    /**
     * 消息体
     */
    public String getBody() {
        return body;
    }

    /**
     * 消息体
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 发送状态  0:发送中  1:重试中  2:发送失败  3:发送成功
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 发送状态  0:发送中  1:重试中  2:发送失败  3:发送成功
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 最大重试次数
     */
    public Integer getMaxRetryTimes() {
        return maxRetryTimes;
    }

    /**
     * 最大重试次数
     */
    public void setMaxRetryTimes(Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    /**
     * 当前重试次数
     */
    public Integer getCurrentRetryTimes() {
        return currentRetryTimes;
    }

    /**
     * 当前重试次数
     */
    public void setCurrentRetryTimes(Integer currentRetryTimes) {
        this.currentRetryTimes = currentRetryTimes;
    }

    /**
     * 发送成功时间
     */
    public Date getSendSuccessTime() {
        return sendSuccessTime;
    }

    /**
     * 发送成功时间
     */
    public void setSendSuccessTime(Date sendSuccessTime) {
        this.sendSuccessTime = sendSuccessTime;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 消息触发时间
     */
    public Date getScheduledTime() {
        return scheduledTime;
    }

    /**
     * 消息触发时间
     */
    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LocalMessageRecord other = (LocalMessageRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getService() == null ? other.getService() == null : this.getService().equals(other.getService()))
                && (this.getBusiness() == null ? other.getBusiness() == null : this.getBusiness().equals(other.getBusiness()))
                && (this.getModel() == null ? other.getModel() == null : this.getModel().equals(other.getModel()))
                && (this.getTopic() == null ? other.getTopic() == null : this.getTopic().equals(other.getTopic()))
                && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
                && (this.getMsgId() == null ? other.getMsgId() == null : this.getMsgId().equals(other.getMsgId()))
                && (this.getMsgKey() == null ? other.getMsgKey() == null : this.getMsgKey().equals(other.getMsgKey()))
                && (this.getBody() == null ? other.getBody() == null : this.getBody().equals(other.getBody()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getMaxRetryTimes() == null ? other.getMaxRetryTimes() == null : this.getMaxRetryTimes().equals(other.getMaxRetryTimes()))
                && (this.getCurrentRetryTimes() == null ? other.getCurrentRetryTimes() == null : this.getCurrentRetryTimes().equals(other.getCurrentRetryTimes()))
                && (this.getSendSuccessTime() == null ? other.getSendSuccessTime() == null : this.getSendSuccessTime().equals(other.getSendSuccessTime()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getScheduledTime() == null ? other.getScheduledTime() == null : this.getScheduledTime().equals(other.getScheduledTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getService() == null) ? 0 : getService().hashCode());
        result = prime * result + ((getBusiness() == null) ? 0 : getBusiness().hashCode());
        result = prime * result + ((getModel() == null) ? 0 : getModel().hashCode());
        result = prime * result + ((getTopic() == null) ? 0 : getTopic().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getMsgId() == null) ? 0 : getMsgId().hashCode());
        result = prime * result + ((getMsgKey() == null) ? 0 : getMsgKey().hashCode());
        result = prime * result + ((getBody() == null) ? 0 : getBody().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getMaxRetryTimes() == null) ? 0 : getMaxRetryTimes().hashCode());
        result = prime * result + ((getCurrentRetryTimes() == null) ? 0 : getCurrentRetryTimes().hashCode());
        result = prime * result + ((getSendSuccessTime() == null) ? 0 : getSendSuccessTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getScheduledTime() == null) ? 0 : getScheduledTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", service=").append(service);
        sb.append(", business=").append(business);
        sb.append(", model=").append(model);
        sb.append(", topic=").append(topic);
        sb.append(", tags=").append(tags);
        sb.append(", msgId=").append(msgId);
        sb.append(", msgKey=").append(msgKey);
        sb.append(", body=").append(body);
        sb.append(", status=").append(status);
        sb.append(", maxRetryTimes=").append(maxRetryTimes);
        sb.append(", currentRetryTimes=").append(currentRetryTimes);
        sb.append(", sendSuccessTime=").append(sendSuccessTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", scheduledTime=").append(scheduledTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}