package com.tianwen.springcloud.microservice.base.entity;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "t_e_class")
public class ClassInfo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_classid_t_e_class')")
    @Column(name = "classid")
    @ApiModelProperty("")
    private String classid;

    @Column(name = "name")
    @ApiModelProperty(value = "", required = true)
    private String name;

    @Column(name = "schoolsectionid")
    @ApiModelProperty(value = "", required = true)
    private String schoolsectionid;

    @Transient
    private String schoolsection;

    @Column(name = "gradeid")
    @ApiModelProperty(value = "", required = true)
    private String gradeid;

    @Transient
    private String grade;

    @Column(name = "adviser")
    @ApiModelProperty("")
    private String adviser;

    @Column(name = "description")
    @ApiModelProperty("")
    private String description;

    @Column(name = "lastnamemodifytime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp lastnamemodifytime;

    @Column(name = "createtime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp createtime;

    @Column(name = "creator")
    @ApiModelProperty(value = "", required = true)
    private String creator;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "orgid")
    @ApiModelProperty(value = "", required = true)
    private String orgid;

    @Transient
    private String orgname;

    @Column(name = "schoolstartdate")
    @ApiModelProperty(value = "", required = true)
    private Timestamp schoolstartdate;

    @Column(name = "status")
    @ApiModelProperty(value = "", required = true)
    private String status;

    @Column(name = "teacherid")
    @ApiModelProperty(value = "", required = true)
    private String teacherid;

    @Column(name = "extinfo")
    @ApiModelProperty(value = "", required = true)
    private JSONObject extinfo;

    @Transient
    private String subjectid;

    @Transient
    private String subject;

    public ClassInfo() {}

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolsectionid() {
        return schoolsectionid;
    }

    public void setSchoolsectionid(String schoolsectionid) {
        this.schoolsectionid = schoolsectionid;
    }

    public String getSchoolsection() {
        return schoolsection;
    }

    public void setSchoolsection(String schoolsection) {
        this.schoolsection = schoolsection;
    }

    public String getGradeid() {
        return gradeid;
    }

    public void setGradeid(String gradeid) {
        this.gradeid = gradeid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getAdviser() {
        return adviser;
    }

    public void setAdviser(String adviser) {
        this.adviser = adviser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getLastnamemodifytime() {
        if (lastnamemodifytime == null)
            return null;
        return new Timestamp(lastnamemodifytime.getTime());
    }

    public void setLastnamemodifytime(Timestamp lastnamemodifytime) {
        if (lastnamemodifytime == null)
            this.lastnamemodifytime = new Timestamp(System.currentTimeMillis());
        else
            this.lastnamemodifytime = new Timestamp(lastnamemodifytime.getTime());
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public Timestamp getSchoolstartdate() {
        return new Timestamp(schoolstartdate.getTime());
    }

    public void setSchoolstartdate(Timestamp schoolstartdate) {
        this.schoolstartdate = new Timestamp(schoolstartdate.getTime());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public JSONObject getExtinfo() {
        return extinfo;
    }

    public void setExtinfo(JSONObject extinfo) {
        this.extinfo = extinfo;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
