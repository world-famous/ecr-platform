package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Table(name = "t_e_school")
public class School extends BaseEntity
{
    @Id
    @Column(name = "orgid")
    @ApiModelProperty(value = "", required = true)
    private String orgid;

    @Transient
    private String orgname;

    @Column(name = "classnamingrule")
    @ApiModelProperty(value = "")
    private String classnamingrule;

    @Column(name = "creatorid")
    @ApiModelProperty(value = "", required = true)
    private String creatorid;

    @Column(name = "createtime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "isnamed")
    @ApiModelProperty(value = "")
    private String isnamed;

    @Column(name = "photopath")
    @ApiModelProperty(value = "")
    private String photopath;

    @Column(name = "description")
    @ApiModelProperty(value = "")
    private String description;

    @Column(name = "orderno")
    @ApiModelProperty(value = "", required = true)
    private Integer orderno;

    @Transient
    private String schooltypeid;

    @Transient
    private String schooltypename;

    @Transient
    private String areaid;

    @Transient
    private String areaname;

    @Transient
    private String parentid;

    @Transient
    private String orgcode;

    @Transient
    private String parentorgid;

    @Transient
    private String orgtype;

    @Transient
    private String orgaddr;

    @Transient
    private String status;

    @Transient
    private String lastmodifierid;

    @Transient
    private String tenantid;

    @Transient
    private Integer goodscount;

    public Integer getOrderno() {
        return orderno;
    }

    public Integer getGoodscount() {
        return goodscount;
    }

    public void setGoodscount(Integer goodscount) {
        this.goodscount = goodscount;
    }

    public void setOrderno(Integer orderno) {
        this.orderno = orderno;
    }

    public String getParentorgid() {
        return parentorgid;
    }

    public void setParentorgid(String parentorgid) {
        this.parentorgid = parentorgid;
    }

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

    public String getSchooltypename() {
        return schooltypename;
    }

    public void setSchooltypename(String schooltypename) {
        this.schooltypename = schooltypename;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getSchooltypeid() {
        return schooltypeid;
    }

    public void setSchooltypeid(String schooltypeid) {
        this.schooltypeid = schooltypeid;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getClassnamingrule() {
        return classnamingrule;
    }

    public void setClassnamingrule(String classnamingrule) {
        this.classnamingrule = classnamingrule;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
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

    public String getIsnamed() {
        return isnamed;
    }

    public void setIsnamed(String isnamed) {
        this.isnamed = isnamed;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifierid() {
        return lastmodifierid;
    }

    public void setLastmodifierid(String lastmodifierid) {
        this.lastmodifierid = lastmodifierid;
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }

    public void setFromSchool(School school){
        orgid = school.getOrgid();
        classnamingrule = school.getClassnamingrule();
        creatorid = school.getCreatorid();
        createtime = school.getCreatetime();
        lastmodifytime = school.getLastmodifytime();
        isnamed = school.getIsnamed();
    }

    public void setFromOrg(Organization organization){
        orgid = organization.getOrgid();
        creatorid = organization.getCreatorid();
        createtime = organization.getCreatetime();
        lastmodifytime = organization.getLastmodifytime();
    }

    //good status
    @Transient
    private String area1;

    @Transient
    private String area2;

    @Transient
    private String area3;

    @Transient
    private String contypename;

    @Transient
    private String contypevalue;

    public String getArea1() {
        return area1;
    }

    public void setArea1(String area1) {
        this.area1 = area1;
    }

    public String getArea2() {
        return area2;
    }

    public void setArea2(String area2) {
        this.area2 = area2;
    }

    public String getArea3() {
        return area3;
    }

    public void setArea3(String area3) {
        this.area3 = area3;
    }

    public String getContypename() {
        return contypename;
    }

    public void setContypename(String contypename) {
        this.contypename = contypename;
    }

    public String getContypevalue() {
        return contypevalue;
    }

    public void setContypevalue(String contypevalue) {
        this.contypevalue = contypevalue;
    }
}
