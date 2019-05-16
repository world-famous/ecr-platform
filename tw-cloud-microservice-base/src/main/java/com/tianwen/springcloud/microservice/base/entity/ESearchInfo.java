package com.tianwen.springcloud.microservice.base.entity;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ESearchInfo implements Serializable{
    @Transient
    private String schoolsection;

    @Transient
    private String subject;

    @Transient
    private String grade;

    @Transient
    private String editiontype;

    @Transient
    private String bookmodel;

    @Transient
    private String catalogids;

    @Transient
    private String knowledgepoint;

    @Transient
    private String contenttype;

    @Transient
    private String areaid;

    @Transient
    private String area;

    @Transient
    private String orgid;

    @Transient
    private String orgname;

    @Transient
    private String creator;

    @Transient
    private String onelabelname;

    @Transient
    private String twolabelname;

    @Transient
    private String threelabelname;

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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEditiontype() {
        return editiontype;
    }

    public void setEditiontype(String editiontype) {
        this.editiontype = editiontype;
    }

    public String getBookmodel() {
        return bookmodel;
    }

    public void setBookmodel(String bookmodel) {
        this.bookmodel = bookmodel;
    }

    public String getCatalogids() {
        return catalogids;
    }

    public void setCatalogids(String catalogids) {
        this.catalogids = catalogids;
    }

    public String getKnowledgepoint() {
        return knowledgepoint;
    }

    public void setKnowledgepoint(String knowledgepoint) {
        this.knowledgepoint = knowledgepoint;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOnelabelname() {
        return onelabelname;
    }

    public void setOnelabelname(String onelabelname) {
        this.onelabelname = onelabelname;
    }

    public String getTwolabelname() {
        return twolabelname;
    }

    public void setTwolabelname(String twolabelname) {
        this.twolabelname = twolabelname;
    }

    public String getThreelabelname() {
        return threelabelname;
    }

    public void setThreelabelname(String threelabelname) {
        this.threelabelname = threelabelname;
    }
}
