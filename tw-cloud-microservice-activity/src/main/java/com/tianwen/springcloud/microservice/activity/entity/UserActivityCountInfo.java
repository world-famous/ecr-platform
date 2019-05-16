package com.tianwen.springcloud.microservice.activity.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

public class UserActivityCountInfo extends BaseEntity{

    private String userid;
    private Long collectionjoincount;
    private Long rewardjoincount;
    private Long estimatejoincount;
    private Long rewardcreatecount;
    private Long goodansweredcount;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getCollectionjoincount() {
        return collectionjoincount;
    }

    public void setCollectionjoincount(Long collectionjoincount) {
        this.collectionjoincount = collectionjoincount;
    }

    public Long getRewardjoincount() {
        return rewardjoincount;
    }

    public void setRewardjoincount(Long rewardjoincount) {
        this.rewardjoincount = rewardjoincount;
    }

    public Long getEstimatejoincount() {
        return estimatejoincount;
    }

    public void setEstimatejoincount(Long estimatejoincount) {
        this.estimatejoincount = estimatejoincount;
    }

    public Long getRewardcreatecount() {
        return rewardcreatecount;
    }

    public void setRewardcreatecount(Long rewardcreatecount) {
        this.rewardcreatecount = rewardcreatecount;
    }

    public Long getGoodansweredcount() {
        return goodansweredcount;
    }

    public void setGoodansweredcount(Long goodansweredcount) {
        this.goodansweredcount = goodansweredcount;
    }
}
