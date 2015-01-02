package org.whut.platform.business.user.entity;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sunhui
 * Date: 14-5-12
 * Time: 上午9:40
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private long id;
    private String name;
    private String password;
    private String sex;
    private String role;
    private String email;
    private Date createTime;
    private String image;
    private Integer status;

    private Long appId;
    private String appName;

    private String activateCode;
    private Date activateCreateTime;
    private String retrievePwdCode;
    private Date retrievePwdCreateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getActivateCode() {
        return activateCode;
    }

    public void setActivateCode(String activateCode) {
        this.activateCode = activateCode;
    }

    public Date getActivateCreateTime() {
        return activateCreateTime;
    }

    public void setActivateCreateTime(Date activateCreateTime) {
        this.activateCreateTime = activateCreateTime;
    }

    public String getRetrievePwdCode() {
        return retrievePwdCode;
    }

    public void setRetrievePwdCode(String retrievePwdCode) {
        this.retrievePwdCode = retrievePwdCode;
    }

    public Date getRetrievePwdCreateTime() {
        return retrievePwdCreateTime;
    }

    public void setRetrievePwdCreateTime(Date retrievePwdCreateTime) {
        this.retrievePwdCreateTime = retrievePwdCreateTime;
    }
}
