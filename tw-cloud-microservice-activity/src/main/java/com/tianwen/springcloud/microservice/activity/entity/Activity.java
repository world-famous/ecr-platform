package com.tianwen.springcloud.microservice.activity.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.ArrayUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Table(name = "t_e_collection_activity")
public class Activity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_activityid_t_e_collection_activity')")
    @Column(name = "activityid")
    @ApiModelProperty("活动Id")
    private String activityid;

    @Column(name = "activityname")
    @ApiModelProperty(value = "活动名称", required = true)
    private String activityname;

    @Column(name = "starttime")
    @ApiModelProperty(value = "启始日期", required = true)
    private Timestamp starttime;

    @Column(name = "endtime")
    @ApiModelProperty(value = "结束日期", required = true)
    private Timestamp endtime;

    @Column(name = "requirement")
    @ApiModelProperty("活动要求")
    private String requirement;

    @Column(name = "format")
    @ApiModelProperty("文件格式")
    private String format;

    @Column(name = "description")
    @ApiModelProperty("活动介绍")
    private String description;

    @Column(name = "bonusnote")
    @ApiModelProperty("奖励说明")
    private String bonusnote;

    @Column(name = "bonuspoints")
    @ApiModelProperty(value = "奖励积分", required = true)
    private Integer bonuspoints;

    @Column(name = "according")
    @ApiModelProperty("活动文件")
    private String according;

    @Column(name = "activitytype")
    @ApiModelProperty(value = "活动类型", required = true)
    private String activitytype;

    @Column(name = "activitytemplate")
    @ApiModelProperty(value = "活动模块", required = true)
    private String activitytemplate;

    @Column(name = "creator")
    @ApiModelProperty(value = "创建人", required = true)
    private String creator;

    @Column(name = "createtime")
    @ApiModelProperty(value = "创建时间", required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "最后修改时间", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "contenttype")
    @ApiModelProperty(value = "资源类型", required = true)
    private String contenttype;

    @Column(name = "logopath")
    @ApiModelProperty(value = "主题图片")
    private String logopath;

    @Column(name = "status")
    @ApiModelProperty("活动状态")
    private String status;

    @Column(name = "votestarttime")
    @ApiModelProperty("")
    private Timestamp votestarttime;

    @Column(name = "voteendtime")
    @ApiModelProperty("")
    private Timestamp voteendtime;

    @Column(name = "enddescription")
    @ApiModelProperty("")
    private String enddescription;

    @Column(name = "descriptiontext")
    @ApiModelProperty("descriptiontext")
    private String descriptiontext;

    @Column(name = "requirementtext")
    @ApiModelProperty("requirementtext")
    private String requirementtext;

    @Column(name = "bonusnotetext")
    @ApiModelProperty("bonusnotetext")
    private String bonusnotetext;

    @Column(name="recommand")
    @ApiModelProperty("recommand")
    private String recommand;

    @Column(name = "isanonymity")
    @ApiModelProperty(value = "isanonymity", required = true)
    private String isanonymity;

    @ApiModelProperty(value = "answeruserid", required = true)
    private String answeruserid;

    private String[] formats;

    private Long viewtimes;
    private Long downtimes;
    private Long joinercount;
    private Long answercount;
    private String creatorname;

    @Transient
    private List<String> accordings;

    public List<String> getAccordings() {
        return accordings;
    }

    public void setAccordings(List<String> accordings) {
        this.accordings = accordings;
    }

    public String getDescriptiontext() {
        return descriptiontext;
    }

    public void setDescriptiontext(String descriptiontext) {
        this.descriptiontext = descriptiontext;
    }

    public String getRequirementtext() {
        return requirementtext;
    }

    public void setRequirementtext(String requirementtext) {
        this.requirementtext = requirementtext;
    }

    public String getBonusnotetext() {
        return bonusnotetext;
    }

    public void setBonusnotetext(String bonusnotetext) {
        this.bonusnotetext = bonusnotetext;
    }

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public Timestamp getStarttime() {
        if (null == starttime) return null;
        return (Timestamp) starttime.clone();
    }

    public void setStarttime(Timestamp starttime) {
        if (starttime != null)
            this.starttime = (Timestamp) starttime.clone();
    }

    public Timestamp getEndtime() {
        if (null == endtime) return null;
        return (Timestamp) endtime.clone();
    }

    public void setEndtime(Timestamp endtime) {
        if (endtime != null)
            this.endtime = (Timestamp) endtime.clone();
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBonusnote() {
        return bonusnote;
    }

    public void setBonusnote(String bonusnote) {
        this.bonusnote = bonusnote;
    }

    public Integer getBonuspoints() {
        return bonuspoints;
    }

    public void setBonuspoints(Integer bonuspoints) {
        this.bonuspoints = bonuspoints;
    }

    public String getAccording() {
        return according;
    }

    public void setAccording(String according) {
        this.according = according;
    }

    public String getActivitytype() {
        return activitytype;
    }

    public void setActivitytype(String activitytype) {
        this.activitytype = activitytype;
    }

    public String getActivitytemplate() {
        return activitytemplate;
    }

    public void setActivitytemplate(String activitytemplate) {
        this.activitytemplate = activitytemplate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Timestamp getCreatetime() {
        if (null == createtime) return null;
        return (Timestamp) createtime.clone();
    }

    public void setCreatetime(Timestamp createtime) {
        if (createtime != null)
            this.createtime = (Timestamp) createtime.clone();
    }

    public Timestamp getLastmodifytime() {
        if (null == lastmodifytime) return null;
        return (Timestamp) lastmodifytime.clone();
    }

    public void setLastmodifytime(Timestamp lastmodifytime) {
        if (lastmodifytime != null)
            this.lastmodifytime = (Timestamp) lastmodifytime.clone();
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getLogopath() {
        return logopath;
    }

    public void setLogopath(String logopath) {
        this.logopath = logopath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getFormats() {
        if (ArrayUtils.isNotEmpty(formats)) {
            return formats.clone();
        } else {
            return null;
        }
    }

    public void setFormats(String[] formats) {
        if (ArrayUtils.isNotEmpty(formats)) {
            this.formats = formats.clone();
        }
    }

    public String getCreatorname() {
        return creatorname;
    }

    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }


    public Long getViewtimes() {
        return viewtimes;
    }

    public void setViewtimes(Long viewtimes) {
        this.viewtimes = viewtimes;
    }

    public Long getDowntimes() {
        return downtimes;
    }

    public void setDowntimes(Long downtimes) {
        this.downtimes = downtimes;
    }

    public Long getJoinercount() {
        return joinercount;
    }

    public void setJoinercount(Long joinercount) {
        this.joinercount = joinercount;
    }

    public Timestamp getVotestarttime() {
        if (null == votestarttime) return null;
        return (Timestamp) votestarttime.clone();
    }

    public void setVotestarttime(Timestamp votestarttime) {
        if (null != votestarttime)
            this.votestarttime = (Timestamp) votestarttime.clone();
    }

    public Timestamp getVoteendtime() {
        if (null == voteendtime) return null;
        return (Timestamp) voteendtime.clone();
    }

    public void setVoteendtime(Timestamp voteendtime) {
        if (null != voteendtime)
            this.voteendtime = (Timestamp) voteendtime.clone();
    }

    public String getEnddescription() {
        return enddescription;
    }

    public void setEnddescription(String enddescription) {
        this.enddescription = enddescription;
    }

    public String getRecommand() {
        return recommand;
    }

    public void setRecommand(String recommand) {
        this.recommand = recommand;
    }

    public Long getAnswercount() {
        return answercount;
    }

    public void setAnswercount(Long answercount) {
        this.answercount = answercount;
    }

    public String getIsanonymity() {
        return isanonymity;
    }

    public void setIsanonymity(String isanonymity) {
        this.isanonymity = isanonymity;
    }

    public String getAnsweruserid() {
        return answeruserid;
    }

    public void setAnsweruserid(String answeruserid) {
        this.answeruserid = answeruserid;
    }
}

