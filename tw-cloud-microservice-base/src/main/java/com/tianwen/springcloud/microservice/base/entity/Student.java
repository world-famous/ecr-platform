package com.tianwen.springcloud.microservice.base.entity;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Table(name = "t_e_student")
public class Student extends BaseEntity
{
    @Id
    @Column(name = "userid")
    @ApiModelProperty(value = "", required = true)
    private String userid;

    @Column(name = "classid")
    @ApiModelProperty(value = "", required = true)
    private String classid;

    @Transient
    private String classname;

    @Column(name = "createtime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "extinfo")
    @ApiModelProperty("")
    private JSONObject extinfo;

    @Column(name = "parentid")
    @ApiModelProperty("")
    private String parentid;

    @Column(name = "studentcode")
    @ApiModelProperty("")
    private String studentcode;

    @Transient
    private UserLoginInfo userinfo;

    @Transient
    private String orgid;

    @Transient
    private String orgname;

    @Transient
    private String areaid;

    @Transient
    private String areaname;

    private String loginname;
    private String realname;
    private String sex;
    private String status;

    public Student(){}

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserLoginInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserLoginInfo userinfo) {
        this.userinfo = userinfo;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public Timestamp getCreatetime() {
        if (createtime == null)
            return null;
        return new Timestamp(createtime.getTime());
    }

    public void setCreatetime(Timestamp createtime) {
        if (createtime == null)
            this.createtime = new Timestamp(System.currentTimeMillis());
        else
            this.createtime = new Timestamp(createtime.getTime());
    }

    public Timestamp getLastmodifytime() {
        if (lastmodifytime == null)
            return null;
        return new Timestamp(lastmodifytime.getTime());
    }

    public void setLastmodifytime(Timestamp lastmodifytime) {
        if (lastmodifytime == null)
            this.lastmodifytime = new Timestamp(System.currentTimeMillis());
        else
            this.lastmodifytime = new Timestamp(lastmodifytime.getTime());
    }

    public JSONObject getExtinfo() {
        return extinfo;
    }

    public void setExtinfo(JSONObject extinfo) {
        this.extinfo = extinfo;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getStudentcode() {
        return studentcode;
    }

    public void setStudentcode(String studentcode) {
        this.studentcode = studentcode;
    }
}
