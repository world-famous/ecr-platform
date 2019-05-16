package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "t_e_score_rule")
public class ScoreRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ruleid")
    @ApiModelProperty("ruleid")
    private String ruleid;

    @Column(name = "scoretype")
    @ApiModelProperty(required = true)
    private String scoretype;

    @Column(name = "bussinesstype")
    @ApiModelProperty(required = true)
    private String bussinesstype;

    @Column(name = "contenttype")
    @ApiModelProperty()
    private String contenttype;

    @Column(name = "score")
    @ApiModelProperty()
    private Integer score;

    @Column(name = "sharerange")
    @ApiModelProperty()
    private String sharerange;

    @Column(name = "remark")
    @ApiModelProperty()
    private String remark;

    @Column(name = "forobject")
    @ApiModelProperty()
    private String forobject;

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

    public String getScoretype() {
        return scoretype;
    }

    public void setScoretype(String scoretype) {
        this.scoretype = scoretype;
    }

    public String getBussinesstype() {
        return bussinesstype;
    }

    public void setBussinesstype(String bussinesstype) {
        this.bussinesstype = bussinesstype;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSharerange() {
        return sharerange;
    }

    public void setSharerange(String sharerange) {
        this.sharerange = sharerange;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getForobject() {
        return forobject;
    }

    public void setForobject(String forobject) {
        this.forobject = forobject;
    }
}
