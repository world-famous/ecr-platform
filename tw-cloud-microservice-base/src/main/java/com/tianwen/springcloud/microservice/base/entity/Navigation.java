package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "t_e_subjectnavi")
public class Navigation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,  generator = "SELECT pg_nextval('seq_naviid_t_e_subjectnavi')")
    @Column(name = "naviid")
    @ApiModelProperty("naviid")
    private String naviid;

    @Column(name = "subjectid")
    @ApiModelProperty(required = true)
    private String subjectid;

    @Column(name = "schoolsection")
    @ApiModelProperty(required = true)
    private String schoolsectionid;

    @Transient
    private String gradeid;

    @Column(name = "bookmodel")
    @ApiModelProperty()
    private String bookmodelid;

    @Column(name = "editiontypeid")
    @ApiModelProperty()
    private String editiontypeid;

    private String schoolsection;
    private String subject;
    private String grade;
    private String editiontype;
    private String bookmodel;
    private String chapter;
    private String section;
    private String lesson;
    private String bookid;
    private String bookname;
    @Column(name = "sequence")
    @ApiModelProperty("sequence")
    private int sequence;

    @Column(name = "status")
    @ApiModelProperty(required = true)
    private String status;

    @Column(name = "producttype")
    @ApiModelProperty(required = true)
    private String producttype;

    @Transient
    private String catalogid;

    @Transient
    private String catalogname;

    @Transient
    private String parentcatalogid;

    @Transient
    private String lang;

    @Transient
    private String searchkey;

    private Integer level;

    public String getNaviid() {
        return naviid;
    }

    public void setNaviid(String naviid) {
        this.naviid = naviid;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getSchoolsectionid() {
        return schoolsectionid;
    }

    public void setSchoolsectionid(String schoolsectionid) {
        this.schoolsectionid = schoolsectionid;
    }

    public void setBookname(String bookname){
        this.bookname = bookname;
    }
    public String getBookname(){
        return bookname;
    }

    public String getGradeid() {
        return gradeid;
    }

    public void setGradeid(String gradeid) {
        this.gradeid = gradeid;
    }

    public String getBookmodelid() {
        return bookmodelid;
    }

    public void setBookmodelid(String bookmodelid) {
        this.bookmodelid = bookmodelid;
    }

    public String getEditiontypeid() {
        return editiontypeid;
    }

    public void setEditiontypeid(String editiontypeid) {
        this.editiontypeid = editiontypeid;
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

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCatalogid() {
        return catalogid;
    }

    public void setCatalogid(String catalogid) {
        this.catalogid = catalogid;
    }

    public String getCatalogname() {
        return catalogname;
    }

    public void setCatalogname(String catalogname) {
        this.catalogname = catalogname;
    }

    public String getParentcatalogid() {
        return parentcatalogid;
    }

    public void setParentcatalogid(String parentcatalogid) {
        this.parentcatalogid = parentcatalogid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public String getSearchkey() {
        return searchkey;
    }

    public void setSearchkey(String searchkey) {
        this.searchkey = searchkey;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setBookid(String bookid){
        this.bookid = bookid;
    }
    public String getBookid(){
        return bookid;
    }
}
