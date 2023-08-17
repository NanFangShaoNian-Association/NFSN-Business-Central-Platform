package cn.nfsn.common.core.domain;

import cn.nfsn.common.core.utils.ValidatorUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName user_info
 */
@TableName(value ="user_info")
public class UserInfo implements Serializable {
    /**
     * 用户ID-主键
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名-默认值(应该是随机生成)
     */
    private String userName;

    /**
     * 用户角色-0:平台管理员;1:机构管理员;2:老师;3:学生;(默认为0)
     */
    private Integer userRole;

    /**
     * 头像-默认（固定默认头像）
     */
    @NotNull(message = "头像获取失败")
    @NotBlank(message = "头像获取失败")
    private String userAvatar;

    /**
     * 邮箱
     */
//    @Pattern(regexp = ValidatorUtil.REGEX_EMAIL,message = "请输入正确邮箱")
    private String email;

    /**
     * 手机号-唯一
     */
    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = ValidatorUtil.REGEX_MOBILE,message = "请输入正确手机号")
    private String phoneNumber;

    /**
     * 用户简介
     */
    private String userIntroduction;

    /**
     * 省份
     */
    private String userLocationProvince;

    /**
     * 城市
     */
    private String userLocationCity;

    /**
     * 区域
     */
    private String userLocationRegion;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * wx_openID
     */
    private String wxOpenid;

    /**
     * qq_openID
     */
    private String qqOpenid;

    /**
     * 最后一次上线时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date loginLastTime;

    /**
     * 最后一次下线时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date offLineLastTime;

    /**
     * 最后一次登录ip
     */
//    @Pattern(regexp = ValidatorUtil.REGEX_IP_ADDR,message = "IP获取错误")
    private String loginLastTimeIp;

    /**
     * 微信unionid
     */
    private String wxUnionid;

    /**
     * QQunionid
     */
    private String qqUnionid;

    /**
     * 账号注册时间-非空
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date userRegistTime;

    /**
     * 用户状态码-0:未注销;1:已注销;2:暂时被冻结;(默认为0)
     */
    private Integer userStatus;

    /**
     * 更新日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID-主键
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 用户ID-主键
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 用户名-默认值(应该是随机生成)
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 用户名-默认值(应该是随机生成)
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 用户角色-0:平台管理员;1:机构管理员;2:老师;3:学生;(默认为0)
     */
    public Integer getUserRole() {
        return userRole;
    }

    /**
     * 用户角色-0:平台管理员;1:机构管理员;2:老师;3:学生;(默认为0)
     */
    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    /**
     * 头像-默认（固定默认头像）
     */
    public String getUserAvatar() {
        return userAvatar;
    }

    /**
     * 头像-默认（固定默认头像）
     */
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    /**
     * 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 手机号-唯一
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 手机号-唯一
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 用户简介
     */
    public String getUserIntroduction() {
        return userIntroduction;
    }

    /**
     * 用户简介
     */
    public void setUserIntroduction(String userIntroduction) {
        this.userIntroduction = userIntroduction;
    }

    /**
     * 省份
     */
    public String getUserLocationProvince() {
        return userLocationProvince;
    }

    /**
     * 省份
     */
    public void setUserLocationProvince(String userLocationProvince) {
        this.userLocationProvince = userLocationProvince;
    }

    /**
     * 城市
     */
    public String getUserLocationCity() {
        return userLocationCity;
    }

    /**
     * 城市
     */
    public void setUserLocationCity(String userLocationCity) {
        this.userLocationCity = userLocationCity;
    }

    /**
     * 区域
     */
    public String getUserLocationRegion() {
        return userLocationRegion;
    }

    /**
     * 区域
     */
    public void setUserLocationRegion(String userLocationRegion) {
        this.userLocationRegion = userLocationRegion;
    }

    /**
     * 经度
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 经度
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * 纬度
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 纬度
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * wx_openID
     */
    public String getWxOpenid() {
        return wxOpenid;
    }

    /**
     * wx_openID
     */
    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }

    /**
     * qq_openID
     */
    public String getQqOpenid() {
        return qqOpenid;
    }

    /**
     * qq_openID
     */
    public void setQqOpenid(String qqOpenid) {
        this.qqOpenid = qqOpenid;
    }

    /**
     * 最后一次上线时间
     */
    public Date getLoginLastTime() {
        return loginLastTime;
    }

    /**
     * 最后一次上线时间
     */
    public void setLoginLastTime(Date loginLastTime) {
        this.loginLastTime = loginLastTime;
    }

    /**
     * 最后一次下线时间
     */
    public Date getOffLineLastTime() {
        return offLineLastTime;
    }

    /**
     * 最后一次下线时间
     */
    public void setOffLineLastTime(Date offLineLastTime) {
        this.offLineLastTime = offLineLastTime;
    }

    /**
     * 最后一次登录ip
     */
    public String getLoginLastTimeIp() {
        return loginLastTimeIp;
    }

    /**
     * 最后一次登录ip
     */
    public void setLoginLastTimeIp(String loginLastTimeIp) {
        this.loginLastTimeIp = loginLastTimeIp;
    }

    /**
     * 微信unionid
     */
    public String getWxUnionid() {
        return wxUnionid;
    }

    /**
     * 微信unionid
     */
    public void setWxUnionid(String wxUnionid) {
        this.wxUnionid = wxUnionid;
    }

    /**
     * QQunionid
     */
    public String getQqUnionid() {
        return qqUnionid;
    }

    /**
     * QQunionid
     */
    public void setQqUnionid(String qqUnionid) {
        this.qqUnionid = qqUnionid;
    }

    /**
     * 账号注册时间-非空
     */
    public Date getUserRegistTime() {
        return userRegistTime;
    }

    /**
     * 账号注册时间-非空
     */
    public void setUserRegistTime(Date userRegistTime) {
        this.userRegistTime = userRegistTime;
    }

    /**
     * 用户状态码-0:未注销;1:已注销;2:暂时被冻结;(默认为0)
     */
    public Integer getUserStatus() {
        return userStatus;
    }

    /**
     * 用户状态码-0:未注销;1:已注销;2:暂时被冻结;(默认为0)
     */
    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * 更新日期
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新日期
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 用户注销标记：0-未注销，1-确认注销，2-取消注销
     */
    private Integer logoutStatus;

    /**
     * 用户注销标记：0-未注销，1-确认注销，2-取消注销
     */
    public Integer getLogoutStatus() {
        return logoutStatus;
    }

    /**
     * 用户注销标记：0-未注销，1-确认注销，2-取消注销
     */
    public void setLogoutStatus(Integer logoutStatus) {
        this.logoutStatus = logoutStatus;
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
        UserInfo other = (UserInfo) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getUserRole() == null ? other.getUserRole() == null : this.getUserRole().equals(other.getUserRole()))
                && (this.getUserAvatar() == null ? other.getUserAvatar() == null : this.getUserAvatar().equals(other.getUserAvatar()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getPhoneNumber() == null ? other.getPhoneNumber() == null : this.getPhoneNumber().equals(other.getPhoneNumber()))
                && (this.getUserIntroduction() == null ? other.getUserIntroduction() == null : this.getUserIntroduction().equals(other.getUserIntroduction()))
                && (this.getUserLocationProvince() == null ? other.getUserLocationProvince() == null : this.getUserLocationProvince().equals(other.getUserLocationProvince()))
                && (this.getUserLocationCity() == null ? other.getUserLocationCity() == null : this.getUserLocationCity().equals(other.getUserLocationCity()))
                && (this.getUserLocationRegion() == null ? other.getUserLocationRegion() == null : this.getUserLocationRegion().equals(other.getUserLocationRegion()))
                && (this.getLongitude() == null ? other.getLongitude() == null : this.getLongitude().equals(other.getLongitude()))
                && (this.getLatitude() == null ? other.getLatitude() == null : this.getLatitude().equals(other.getLatitude()))
                && (this.getWxOpenid() == null ? other.getWxOpenid() == null : this.getWxOpenid().equals(other.getWxOpenid()))
                && (this.getQqOpenid() == null ? other.getQqOpenid() == null : this.getQqOpenid().equals(other.getQqOpenid()))
                && (this.getLoginLastTime() == null ? other.getLoginLastTime() == null : this.getLoginLastTime().equals(other.getLoginLastTime()))
                && (this.getOffLineLastTime() == null ? other.getOffLineLastTime() == null : this.getOffLineLastTime().equals(other.getOffLineLastTime()))
                && (this.getLoginLastTimeIp() == null ? other.getLoginLastTimeIp() == null : this.getLoginLastTimeIp().equals(other.getLoginLastTimeIp()))
                && (this.getWxUnionid() == null ? other.getWxUnionid() == null : this.getWxUnionid().equals(other.getWxUnionid()))
                && (this.getQqUnionid() == null ? other.getQqUnionid() == null : this.getQqUnionid().equals(other.getQqUnionid()))
                && (this.getUserRegistTime() == null ? other.getUserRegistTime() == null : this.getUserRegistTime().equals(other.getUserRegistTime()))
                && (this.getUserStatus() == null ? other.getUserStatus() == null : this.getUserStatus().equals(other.getUserStatus()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getLogoutStatus() == null ? other.getLogoutStatus() == null : this.getLogoutStatus().equals(other.getLogoutStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getUserRole() == null) ? 0 : getUserRole().hashCode());
        result = prime * result + ((getUserAvatar() == null) ? 0 : getUserAvatar().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getPhoneNumber() == null) ? 0 : getPhoneNumber().hashCode());
        result = prime * result + ((getUserIntroduction() == null) ? 0 : getUserIntroduction().hashCode());
        result = prime * result + ((getUserLocationProvince() == null) ? 0 : getUserLocationProvince().hashCode());
        result = prime * result + ((getUserLocationCity() == null) ? 0 : getUserLocationCity().hashCode());
        result = prime * result + ((getUserLocationRegion() == null) ? 0 : getUserLocationRegion().hashCode());
        result = prime * result + ((getLongitude() == null) ? 0 : getLongitude().hashCode());
        result = prime * result + ((getLatitude() == null) ? 0 : getLatitude().hashCode());
        result = prime * result + ((getWxOpenid() == null) ? 0 : getWxOpenid().hashCode());
        result = prime * result + ((getQqOpenid() == null) ? 0 : getQqOpenid().hashCode());
        result = prime * result + ((getLoginLastTime() == null) ? 0 : getLoginLastTime().hashCode());
        result = prime * result + ((getOffLineLastTime() == null) ? 0 : getOffLineLastTime().hashCode());
        result = prime * result + ((getLoginLastTimeIp() == null) ? 0 : getLoginLastTimeIp().hashCode());
        result = prime * result + ((getWxUnionid() == null) ? 0 : getWxUnionid().hashCode());
        result = prime * result + ((getQqUnionid() == null) ? 0 : getQqUnionid().hashCode());
        result = prime * result + ((getUserRegistTime() == null) ? 0 : getUserRegistTime().hashCode());
        result = prime * result + ((getUserStatus() == null) ? 0 : getUserStatus().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getLogoutStatus() == null) ? 0 : getLogoutStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", userName=").append(userName);
        sb.append(", userRole=").append(userRole);
        sb.append(", userAvatar=").append(userAvatar);
        sb.append(", email=").append(email);
        sb.append(", phoneNumber=").append(phoneNumber);
        sb.append(", userIntroduction=").append(userIntroduction);
        sb.append(", userLocationProvince=").append(userLocationProvince);
        sb.append(", userLocationCity=").append(userLocationCity);
        sb.append(", userLocationRegion=").append(userLocationRegion);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", wxOpenid=").append(wxOpenid);
        sb.append(", qqOpenid=").append(qqOpenid);
        sb.append(", loginLastTime=").append(loginLastTime);
        sb.append(", offLineLastTime=").append(offLineLastTime);
        sb.append(", loginLastTimeIp=").append(loginLastTimeIp);
        sb.append(", wxUnionid=").append(wxUnionid);
        sb.append(", qqUnionid=").append(qqUnionid);
        sb.append(", userRegistTime=").append(userRegistTime);
        sb.append(", userStatus=").append(userStatus);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", logoutStatus=").append(logoutStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}