package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "t_e_konwledage_information")
public class Knowledge extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_knowledgeid_t_e_knowledge_information')")
    @Column(name = "knowledgeid")
    @ApiModelProperty("")
    private String knowledgeid;

    @Column(name = "name")
    @ApiModelProperty(value = "", required = true)
    private String name;

    @Column(name = "description")
    @ApiModelProperty(value = "", required = true)
    private String description;

    @Column(name = "schoolsectionid")
    @ApiModelProperty(value = "", required = true)
    private String schoolsectionid;

    @Transient
    private String schoolsection;

    @Column(name = "subjectid")
    @ApiModelProperty(value = "", required = true)
    private String subjectid;

    @Transient
    private String subject;

    @Column(name = "parentknowledgeid")
    @ApiModelProperty(value = "", required = true)
    private String parentknowledgeid;

    @Column(name = "iseditable")
    @ApiModelProperty(value = "", required = true)
    private String iseditable;

    @Column(name = "status")
    @ApiModelProperty(value = "", required = true)
    private String status;

    @Column(name = "creator")
    @ApiModelProperty(value = "", required = true)
    private String creator;

    @Transient
    private String creatorname;

    @Column(name = "createtime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "sequence")
    @ApiModelProperty(value = "", required = true)
    private String sequence;

    @Transient
    private String lang;

    public String getKnowledgeid() {
        return knowledgeid;
    }

    public void setKnowledgeid(String knowledgeid) {
        this.knowledgeid = knowledgeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getParentknowledgeid() {
        return parentknowledgeid;
    }

    public void setParentknowledgeid(String parentknowledgeid) {
        this.parentknowledgeid = parentknowledgeid;
    }

    public String getIseditable() {
        return iseditable;
    }

    public void setIseditable(String iseditable) {
        this.iseditable = iseditable;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreatorname() {
        return creatorname;
    }

    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
