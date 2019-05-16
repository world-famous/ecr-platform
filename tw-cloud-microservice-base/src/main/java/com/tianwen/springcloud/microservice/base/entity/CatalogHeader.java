package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;

import javax.persistence.Transient;

public class CatalogHeader extends BaseEntity {
    @Transient
    private String bookid;

    @Transient
    private String bookname;

    @Transient
    private String schoolsectionid;

    @Transient
    private String schoolsection;

    @Transient
    private String dictsection;

    @Transient
    private String subjectid;

    @Transient
    private String subject;

    @Transient
    private String dictsubject;

    @Transient
    private String gradeid;

    @Transient
    private String grade;

    @Transient
    private String dictgrade;

    @Transient
    private String editiontypeid;

    @Transient
    private String editiontype;

    @Transient
    private String dictedition;

    @Transient
    private String bookmodelid;

    @Transient
    private String bookmodel;

    @Transient
    private String dictmodel;

    @Transient
    private String catalogids;

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
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

    public String getDictsection() {
        return dictsection;
    }

    public void setDictsection(String dictsection) {
        this.dictsection = dictsection;
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

    public String getDictsubject() {
        return dictsubject;
    }

    public void setDictsubject(String dictsubject) {
        this.dictsubject = dictsubject;
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

    public String getDictgrade() {
        return dictgrade;
    }

    public void setDictgrade(String dictgrade) {
        this.dictgrade = dictgrade;
    }

    public String getEditiontypeid() {
        return editiontypeid;
    }

    public void setEditiontypeid(String editiontypeid) {
        this.editiontypeid = editiontypeid;
    }

    public String getEditiontype() {
        return editiontype;
    }

    public void setEditiontype(String editiontype) {
        this.editiontype = editiontype;
    }

    public String getDictedition() {
        return dictedition;
    }

    public void setDictedition(String dictedition) {
        this.dictedition = dictedition;
    }

    public String getBookmodelid() {
        return bookmodelid;
    }

    public void setBookmodelid(String bookmodelid) {
        this.bookmodelid = bookmodelid;
    }

    public String getBookmodel() {
        return bookmodel;
    }

    public void setBookmodel(String bookmodel) {
        this.bookmodel = bookmodel;
    }

    public String getDictmodel() {
        return dictmodel;
    }

    public void setDictmodel(String dictmodel) {
        this.dictmodel = dictmodel;
    }

    public String getCatalogids() {
        return catalogids;
    }

    public void setCatalogids(String catalogids) {
        this.catalogids = catalogids;
    }
}