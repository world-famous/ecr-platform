package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by gems on 2018.12.19.
 */
@Table(name = "t_e_theme_information")
public class Theme extends BaseEntity {
    /**
     * 用户Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_themeid_t_e_theme_information')")
    @Column(name = "themeid")
    @ApiModelProperty("专题id")
    private String themeid;

    @Column(name = "description")
    @ApiModelProperty(value = "专题描述", required = true)
    private String description;

    @Column(name = "schoolsection")
    @ApiModelProperty(value = "", required = true)
    private String schoolsection;

    @Column(name = "grade")
    @ApiModelProperty(value = "年级", required = true)
    private String grade;

    @Column(name = "term")
    @ApiModelProperty(value = "学期")
    private String term;

    @Column(name = "subjectid")
    @ApiModelProperty(value = "科目", required = true)
    private String subjectid;

    @Column(name = "logopath")
    @ApiModelProperty(value = "", required = true)
    private String logopath;

    @Column(name = "themetype")
    @ApiModelProperty(value = "", required = true, allowableValues = "1,2" )
    private String themetype;

    @Column(name = "subthemetype")
    @ApiModelProperty(value = "", required = true)
    private String subthemetype;

    @Column(name = "parentthemeid")
    @ApiModelProperty(value = "", required = true)
    private String parentthemeid;

    @Column(name = "copyright")
    @ApiModelProperty(value = "")
    private String copyright;

    @Column(name = "isallowdownload")
    @ApiModelProperty(value = "", required = true, allowableValues = "0,1")
    private String isallowdownload;

    @Column(name = "orderno")
    @ApiModelProperty(value = "")
    private long orderno;

    @Column(name = "status")
    @ApiModelProperty(value = "", required = true, allowableValues = "0,1")
    private String status;

    @Column(name = "creator")
    @ApiModelProperty(value = "", required = true)
    private String creator;

    @Column(name = "createtime")
    @ApiModelProperty(value = "")
    private Date createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "")
    private Date lastmodifytime;

    @Column(name = "themename")
    @ApiModelProperty(value = "")
    private String themename;

    private Long goodscount;

    private int childCount = 0;

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public String getThemeid() {
        return themeid;
    }

    public void setThemeid(String themeid) {
        this.themeid = themeid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchoolsection() {
        return schoolsection;
    }

    public void setSchoolsection(String schoolsection) {
        this.schoolsection = schoolsection;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getLogopath() {
        return logopath;
    }

    public void setLogopath(String logopath) {
        this.logopath = logopath;
    }

    public String getThemetype() {
        return themetype;
    }

    public void setThemetype(String themetype) {
        this.themetype = themetype;
    }

    public String getSubthemetype() {
        return subthemetype;
    }

    public void setSubthemetype(String subthemetype) {
        this.subthemetype = subthemetype;
    }

    public String getParentthemeid() {
        return parentthemeid;
    }

    public void setParentthemeid(String parentthemeid) {
        this.parentthemeid = parentthemeid;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getIsallowdownload() {
        return isallowdownload;
    }

    public void setIsallowdownload(String isallowdownload) {
        this.isallowdownload = isallowdownload;
    }

    public long getOrderno() {
        return orderno;
    }

    public void setOrderno(long orderno) {
        this.orderno = orderno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreatetime() {
        if (createtime == null)
            return null;
        return new Timestamp(createtime.getTime());
    }

    public void setCreatetime(Date createtime) {
        if (createtime == null)
            this.createtime = new Timestamp(System.currentTimeMillis());
        else
            this.createtime = new Timestamp(createtime.getTime());
    }

    public Date getLastmodifytime() {
        if (lastmodifytime == null)
            return null;
        return new Timestamp(lastmodifytime.getTime());
    }

    public void setLastmodifytime(Date lastmodifytime) {
        if (lastmodifytime == null)
            this.lastmodifytime = new Timestamp(System.currentTimeMillis());
        else
            this.lastmodifytime = new Timestamp(lastmodifytime.getTime());
    }

    public String getThemename() {
        return themename;
    }

    public void setThemename(String themename) {
        this.themename = themename;
    }

    public Long getGoodscount() {
        return goodscount;
    }

    public void setGoodscount(Long goodscount) {
        this.goodscount = goodscount;
    }
}
