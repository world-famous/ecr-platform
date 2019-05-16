package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.List;

@Table(name = "t_jc_sys_area")
public class Area extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_areaid_t_jc_sys_area')")
    @Column(name = "areaid")
    @ApiModelProperty("区域ID")
    private String areaid;

    @Column(name = "areaname")
    @ApiModelProperty(value = "区域名", required = true)
    private String areaname;

    @Column(name = "parentareaid")
    @ApiModelProperty(value = "父区域ID", required = true)
    private String parentareaid;

    private List<String> userids;

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getParentareaid() {
        return parentareaid;
    }

    public void setParentareaid(String parentareaid) {
        this.parentareaid = parentareaid;
    }

    public List<String> getUserids() {
        return userids;
    }

    public void setUserids(List<String> userids) {
        this.userids = userids;
    }
}
