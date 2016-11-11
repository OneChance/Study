package com.logic.mes.entity.base;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Table("user")
public class User implements Serializable{
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private Long id;
    private String empCode;
    private String empName;
    private String userName;
    private String sex;
    private String password;
    private String mobile;
    private String weixin;
    private String email;
    private String tel;
    private String orgid_hr;
    private Long orgid_mes;
    private Integer isDel;
    private Integer isChanged;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOrgid_hr() {
        return orgid_hr;
    }

    public void setOrgid_hr(String orgid_hr) {
        this.orgid_hr = orgid_hr;
    }

    public Long getOrgid_mes() {
        return orgid_mes;
    }

    public void setOrgid_mes(Long orgid_mes) {
        this.orgid_mes = orgid_mes;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getIsChanged() {
        return isChanged;
    }

    public void setIsChanged(Integer isChanged) {
        this.isChanged = isChanged;
    }
}
