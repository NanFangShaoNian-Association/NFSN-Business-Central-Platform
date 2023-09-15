package cn.nfsn.common.core.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: gaojianjie
 * @Description 获取的用户信息封装
 * @date 2023/9/15 14:07
 */
@ApiModel("用户信息展示类")
public class UserInfoVO implements Serializable {
    /**
     * 用户ID-主键
     */
    @ApiModelProperty("用户信息ID")
    private Integer userId;

    /**
     * 用户名-默认值(应该是随机生成)
     */
    @ApiModelProperty("用户名")
    private String userName;

    /**
     * 用户角色-0:平台管理员;1:机构管理员;2:老师;3:学生;(默认为0)
     */
    @ApiModelProperty("用户角色-0:平台管理员;1:机构管理员;2:老师;3:学生;(默认为0)")
    private Integer userRole;

    /**
     * 头像-默认（固定默认头像）
     */
    @ApiModelProperty("头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty("用户简介")
    private String userIntroduction;

    /**
     * 省份
     */
    @ApiModelProperty("省份")
    private String userLocationProvince;

    /**
     * 城市
     */
    @ApiModelProperty("城市")
    private String userLocationCity;

    /**
     * 区域
     */
    @ApiModelProperty("区域")
    private String userLocationRegion;

    /**
     * 经度
     */
    @ApiModelProperty("经度")
    private Double longitude;

    /**
     * 纬度
     */
    @ApiModelProperty("纬度")
    private Double latitude;

    /**
     * wx_openID
     */
    @ApiModelProperty("wx_openID")
    private String wxOpenid;

    /**
     * qq_openID
     */
    @ApiModelProperty("qq_openID")
    private String qqOpenid;

    /**
     * 最后一次上线时间
     */
    @ApiModelProperty("最后一次上线时间")
    private Date loginLastTime;

    /**
     * 最后一次下线时间
     */
    @ApiModelProperty("最后一次下线时间")
    private Date offLineLastTime;

    /**
     * 最后一次登录ip
     */
    @ApiModelProperty("最后一次登录ip")
    private String loginLastTimeIp;

    /**
     * 微信unionid
     */
    @ApiModelProperty("微信unionid")
    private String wxUnionid;

    /**
     * QQunionid
     */
    @ApiModelProperty("QQunionid")
    private String qqUnionid;

    /**
     * 账号注册时间-非空
     */
    @ApiModelProperty("账号注册时间-非空")
    private Date userRegistTime;

    /**
     * 更新日期
     */
    @ApiModelProperty("更新日期")
    private Date updateTime;

    /**
     * 用户表外键
     */
    @ApiModelProperty("用户表外键")
    private Integer credentialsId;

    private static final long serialVersionUID = 1L;

    public UserInfoVO() {
    }

    public UserInfoVO(Integer userId, String userName, Integer userRole, String userAvatar, String userIntroduction, String userLocationProvince, String userLocationCity, String userLocationRegion, Double longitude, Double latitude, String wxOpenid, String qqOpenid, Date loginLastTime, Date offLineLastTime, String loginLastTimeIp, String wxUnionid, String qqUnionid, Date userRegistTime, Date updateTime, Integer credentialsId) {
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.userAvatar = userAvatar;
        this.userIntroduction = userIntroduction;
        this.userLocationProvince = userLocationProvince;
        this.userLocationCity = userLocationCity;
        this.userLocationRegion = userLocationRegion;
        this.longitude = longitude;
        this.latitude = latitude;
        this.wxOpenid = wxOpenid;
        this.qqOpenid = qqOpenid;
        this.loginLastTime = loginLastTime;
        this.offLineLastTime = offLineLastTime;
        this.loginLastTimeIp = loginLastTimeIp;
        this.wxUnionid = wxUnionid;
        this.qqUnionid = qqUnionid;
        this.userRegistTime = userRegistTime;
        this.updateTime = updateTime;
        this.credentialsId = credentialsId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserIntroduction() {
        return userIntroduction;
    }

    public void setUserIntroduction(String userIntroduction) {
        this.userIntroduction = userIntroduction;
    }

    public String getUserLocationProvince() {
        return userLocationProvince;
    }

    public void setUserLocationProvince(String userLocationProvince) {
        this.userLocationProvince = userLocationProvince;
    }

    public String getUserLocationCity() {
        return userLocationCity;
    }

    public void setUserLocationCity(String userLocationCity) {
        this.userLocationCity = userLocationCity;
    }

    public String getUserLocationRegion() {
        return userLocationRegion;
    }

    public void setUserLocationRegion(String userLocationRegion) {
        this.userLocationRegion = userLocationRegion;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getWxOpenid() {
        return wxOpenid;
    }

    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }

    public String getQqOpenid() {
        return qqOpenid;
    }

    public void setQqOpenid(String qqOpenid) {
        this.qqOpenid = qqOpenid;
    }

    public Date getLoginLastTime() {
        return loginLastTime;
    }

    public void setLoginLastTime(Date loginLastTime) {
        this.loginLastTime = loginLastTime;
    }

    public Date getOffLineLastTime() {
        return offLineLastTime;
    }

    public void setOffLineLastTime(Date offLineLastTime) {
        this.offLineLastTime = offLineLastTime;
    }

    public String getLoginLastTimeIp() {
        return loginLastTimeIp;
    }

    public void setLoginLastTimeIp(String loginLastTimeIp) {
        this.loginLastTimeIp = loginLastTimeIp;
    }

    public String getWxUnionid() {
        return wxUnionid;
    }

    public void setWxUnionid(String wxUnionid) {
        this.wxUnionid = wxUnionid;
    }

    public String getQqUnionid() {
        return qqUnionid;
    }

    public void setQqUnionid(String qqUnionid) {
        this.qqUnionid = qqUnionid;
    }

    public Date getUserRegistTime() {
        return userRegistTime;
    }

    public void setUserRegistTime(Date userRegistTime) {
        this.userRegistTime = userRegistTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(Integer credentialsId) {
        this.credentialsId = credentialsId;
    }
}
