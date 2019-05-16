package com.tianwen.springcloud.microservice.base.entity;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Table(name = "t_e_teacher")
public class Teacher extends BaseEntity
{
    @Id
    @Column(name = "userid")
    @ApiModelProperty(value = "", required = true)
    private String userid;

    @Column(name = "job_status")
    @ApiModelProperty(value = "", required = true)
    private String job_status;

    @Column(name = "createtime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "extinfo")
    @ApiModelProperty("")
    private JSONObject extinfo;

    @Column(name = "personprofile")
    @ApiModelProperty("")
    private String personprofile;

    @Column(name = "isnamed")
    @ApiModelProperty("")
    private String isnamed;

    @Column(name = "orderno")
    @ApiModelProperty(value = "", required = true)
    private Integer orderno;

    @Column(name = "sharerange")
    @ApiModelProperty(value = "")
    private String sharerange;

    @Column(name = "photopath")
    @ApiModelProperty(value = "")
    private String photopath;

    @Column(name = "description")
    @ApiModelProperty(value = "")
    private String description;

    @Transient
    private String areaid;

    private String areaname;

    private ClassInfo classinfo;

    @Transient
    private String loginname;

    @Transient
    private String realname;

    @Transient
    private String sex;

    @Transient
    private String birthday;

    @Transient
    private String status;

    @Transient
    private String orgid;

    @Transient
    private String orgname;

    @Transient
    private Integer goodscount;

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
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

    public String getPersonprofile() {
        return personprofile;
    }

    public void setPersonprofile(String personalprofile) {
        this.personprofile = personalprofile;
    }

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

    public String getBirthday() {
        return  birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getOrderno() {
        return orderno;
    }

    public void setOrderno(Integer orderno) {
        this.orderno = orderno;
    }

    public String getIsnamed() {
        return isnamed;
    }

    public void setIsnamed(String isnamed) {
        this.isnamed = isnamed;
    }

    public String getSharerange() {
        return sharerange;
    }

    public void setSharerange(String sharerange) {
        this.sharerange = sharerange;
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

    public ClassInfo getClassinfo() {
        return classinfo;
    }

    public void setClassinfo(ClassInfo classinfo) {
        this.classinfo = classinfo;
    }

    public Integer getGoodscount() {
        return goodscount;
    }

    public void setGoodscount(Integer goodscount) {
        this.goodscount = goodscount;
    }
}
