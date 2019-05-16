package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 用户账号信息实体类(对应t_e_user_logininfo表)
 * 
 * @author wangbin
 * @version [版本号, 2017年4月25日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Table(name = "t_e_book")
public class Book extends BaseEntity
{

    /**
     * 用户Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_bookid_t_e_book')")
    @Column(name = "bookid")
    @ApiModelProperty("")
    private String bookid;

    @Column(name = "naviid")
    @ApiModelProperty(value = "", required = true)
    private String naviid;

    @Column(name = "gradeid")
    @ApiModelProperty(value = "", required = true)
    private String gradeid;

    private String grade;

    @Column(name = "bookname")
    @ApiModelProperty(value = "", required = true)
    private String bookname;

    @Column(name = "bookscore")
    @ApiModelProperty("")
    private int bookscore;

    @Column(name = "thumbnail")
    @ApiModelProperty("")
    private String thumbnail;

    @Column(name = "description")
    @ApiModelProperty("")
    private String description;

    @Column(name = "creator")
    @ApiModelProperty("")
    private String creator;

    @Column(name = "type")
    @ApiModelProperty("")
    private String type;

    @Column(name = "createtime")
    @ApiModelProperty("")
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty("")
    private Timestamp lastmodifytime;

    private String schoolsectionid;
    private String subjectid;
    private String bookmodelid;
    private String editiontypeid;
    private String schoolsection;
    private String subject;
    private String bookmodel;
    private String editiontype;

    public String getNaviid() {
        return naviid;
    }

    public void setNaviid(String naviid) {
        this.naviid = naviid;
    }

    public String getSchoolsectionid() {
        return schoolsectionid;
    }

    public void setSchoolsectionid(String schoolsectionid) {
        this.schoolsectionid = schoolsectionid;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
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

    public String getBookmodel() {
        return bookmodel;
    }

    public void setBookmodel(String bookmodel) {
        this.bookmodel = bookmodel;
    }

    public String getEditiontype() {
        return editiontype;
    }

    public void setEditiontype(String editiontype) {
        this.editiontype = editiontype;
    }

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

    public int getBookscore() {
        return bookscore;
    }

    public void setBookscore(int bookscore) {
        this.bookscore = bookscore;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Book [bookid=" + bookid + ", bookname=" + bookname + ", bookscore=" + bookscore
            + ", thumbnail=" + thumbnail + ", description=" + description + ", creator=" + creator
            + ", createtime=" + createtime + ", lastmodifytime=" + lastmodifytime + "]";
    }
}
