package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;


@Table(name = "t_e_label")
public class Label extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_labelid_t_e_label')")
    @Column(name = "labelid")
    @ApiModelProperty("labelid")
    private String labelid;

    @Column(name = "labelname")
    @ApiModelProperty(required = true)
    private String labelname;

    @Column(name = "parentlabelid")
    @ApiModelProperty(required = true)
    private String parentlabelid;

    @Column(name = "businesstype")
    @ApiModelProperty(required = true)
    private String businesstype;

    @Column(name = "labeltype")
    @ApiModelProperty(required = true)
    private String labeltype;

    @Column(name = "sequence")
    @ApiModelProperty(required = true)
    private String sequence;

    @Column(name = "localpath")
    @ApiModelProperty()
    private String localpath;

    @Column(name = "status")
    @ApiModelProperty(required = true)
    private String status;

    @Column(name = "property")
    @ApiModelProperty(required = true)
    private String property;

    @Column(name = "sortmethod")
    @ApiModelProperty(required = true)
    private String sortmethod;

    @Column(name = "schoolsectionid")
    @ApiModelProperty()
    private String schoolsectionid;

    @Transient
    private String schoolsection;

    @Column(name = "subjectid")
    @ApiModelProperty()
    private String subjectid;

    @Transient
    private String subject;

    @Column(name = "gradeid")
    @ApiModelProperty()
    private String gradeid;

    @Transient
    private String grade;

    @Column(name = "editiontypeid")
    @ApiModelProperty()
    private String editiontypeid;

    @Transient
    private String editiontype;

    @Column(name = "bookmodelid")
    @ApiModelProperty()
    private String bookmodelid;

    @Transient
    private String bookmodel;

    @Column(name = "creator")
    @ApiModelProperty()
    private String creator;

    @Transient
    private String creatorname;

    @Column(name = "schoolid")
    @ApiModelProperty()
    private String schoolid;

    @Transient
    private String schoolname;

    @Column(name = "createtime")
    @ApiModelProperty(required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(required = true)
    private Timestamp lastmodifytime;

    //author:han
    @Column(name = "score")
    @ApiModelProperty()
    private Integer score;
    //end:han

    @Transient
    private String onelabel;

    @Transient
    private String  twolabel;

    @Transient
    private String threelabel;

    @Transient
    private long goodscount;

    @Transient
    private String lang;

    @Transient
    private Integer childcount;

    //author:han
    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }
    //end:han

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public long getGoodscount() {
        return goodscount;
    }

    public void setGoodscount(long goodscount) {
        this.goodscount = goodscount;
    }

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid;
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname;
    }

    public String getParentlabelid() {
        return parentlabelid;
    }

    public void setParentlabelid(String parentlabelid) {
        this.parentlabelid = parentlabelid;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }

    public String getLabeltype() {
        return labeltype;
    }

    public void setLabeltype(String labeltype) {
        this.labeltype = labeltype;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnelabel() {
        return onelabel;
    }

    public void setOnelabel(String onelabel) {
        this.onelabel = onelabel;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public String getTwolabel() {
        return twolabel;
    }

    public void setTwolabel(String twolabel) {
        this.twolabel = twolabel;
    }

    public String getThreelabel() {
        return threelabel;
    }

    public void setThreelabel(String threelabel) {
        this.threelabel = threelabel;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSortmethod() {
        return sortmethod;
    }

    public void setSortmethod(String sortmethod) {
        this.sortmethod = sortmethod;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorname() {
        return creatorname;
    }

    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
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

    public Integer getChildcount() {
        return childcount;
    }

    public void setChildcount(Integer childcount) {
        this.childcount = childcount;
    }
}
