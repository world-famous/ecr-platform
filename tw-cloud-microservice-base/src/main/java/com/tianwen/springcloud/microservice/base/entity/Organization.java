package com.tianwen.springcloud.microservice.base.entity;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Table(name = "t_e_org_edu")
public class Organization extends BaseEntity
{
    @Id
    @Column(name = "orgid")
    @ApiModelProperty(value = "" )
    private String orgid;

    @Column(name = "orgcode")
    @ApiModelProperty(value = "")
    private String orgcode;

    @Column(name = "orgname")
    @ApiModelProperty(value = "")
    private String orgname;

    @Column(name = "parentorgid")
    @ApiModelProperty(value = "")
    private String parentorgid;

    @Column(name = "orgtype")
    @ApiModelProperty(value = "")
    private String orgtype;

    @Column(name = "orgaddr")
    @ApiModelProperty(value = "")
    private String orgaddr;

    @Column(name = "zipcode")
    @ApiModelProperty(value = "")
    private String zipcode;

    @Column(name = "orgmanager")
    @ApiModelProperty(value = "")
    private String orgmanager;

    @Column(name = "linkman")
    @ApiModelProperty(value = "")
    private String linkman;

    @Column(name = "linktel")
    @ApiModelProperty(value = "")
    private String linktel;

    @Column(name = "email")
    @ApiModelProperty(value = "")
    private String email;

    @Column(name = "weburl")
    @ApiModelProperty(value = "")
    private String weburl;

    @Column(name = "status")
    @ApiModelProperty(value = "" )
    private String status;

    @Column(name = "linenum")
    @ApiModelProperty(value = "" )
    private Integer linenum;

    @Column(name = "description")
    @ApiModelProperty(value = "" )
    private String description;

    @Column(name = "creatorid")
    @ApiModelProperty(value = "" )
    private String creatorid;

    @Column(name = "lastmodifierid")
    @ApiModelProperty(value = "" )
    private String lastmodifierid;

    @Column(name = "areaid")
    @ApiModelProperty(value = "" )
    private String areaid;

    @Column(name = "tenantid")
    @ApiModelProperty(value = "" )
    private String tenantid;

    @Column(name = "logofileid")
    @ApiModelProperty(value = "" )
    private String logofileid;

    @Column(name = "orgdomain")
    @ApiModelProperty(value = "" )
    private String orgdomain;

    @Column(name = "synurl")
    @ApiModelProperty(value = "" )
    private String synurl;

    @Column(name = "syncode")
    @ApiModelProperty(value = "" )
    private String syncode;

    @Column(name = "title")
    @ApiModelProperty(value = "" )
    private String title;

    @Column(name = "createtime")
    @ApiModelProperty(value = "" )
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "" )
    private Timestamp lastmodifytime;

    private JSONObject extinfo;

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getParentorgid() {
        return parentorgid;
    }

    public void setParentorgid(String parentorgid) {
        this.parentorgid = parentorgid;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    public String getOrgaddr() {
        return orgaddr;
    }

    public void setOrgaddr(String orgaddr) {
        this.orgaddr = orgaddr;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getOrgmanager() {
        return orgmanager;
    }

    public void setOrgmanager(String orgmanager) {
        this.orgmanager = orgmanager;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getLinktel() {
        return linktel;
    }

    public void setLinktel(String linktel) {
        this.linktel = linktel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLinenum() {
        return linenum;
    }

    public void setLinenum(Integer linenum) {
        this.linenum = linenum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getLastmodifierid() {
        return lastmodifierid;
    }

    public void setLastmodifierid(String lastmodifierid) {
        this.lastmodifierid = lastmodifierid;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }

    public String getLogofileid() {
        return logofileid;
    }

    public void setLogofileid(String logofileid) {
        this.logofileid = logofileid;
    }

    public String getOrgdomain() {
        return orgdomain;
    }

    public void setOrgdomain(String orgdomain) {
        this.orgdomain = orgdomain;
    }

    public String getSynurl() {
        return synurl;
    }

    public void setSynurl(String synurl) {
        this.synurl = synurl;
    }

    public String getSyncode() {
        return syncode;
    }

    public void setSyncode(String syncode) {
        this.syncode = syncode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setFromSchool(School school){
        orgid = school.getOrgid();
        orgcode = school.getOrgcode();
        orgname = school.getOrgname();
        orgtype = school.getOrgtype();
        orgaddr = school.getOrgaddr();
        status = school.getStatus();
        creatorid = school.getCreatorid();
        lastmodifierid = school.getLastmodifierid();
        tenantid = school.getTenantid();
        createtime = school.getCreatetime();
        lastmodifytime = school.getLastmodifytime();
        lastmodifierid = school.getCreatorid();
    }
}
