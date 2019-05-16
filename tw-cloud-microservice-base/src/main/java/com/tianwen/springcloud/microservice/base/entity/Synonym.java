package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "t_e_synonym")
public class Synonym extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_synid_t_e_synonym')")
    @Column(name = "synid")
    @ApiModelProperty("")
    private String synid;

    @Column(name = "original")
    @ApiModelProperty(value = "", required = true)
    private String original;

    @Column(name = "synonym")
    @ApiModelProperty(value = "", required = true)
    private String synonym;

    @Column(name = "searchkey")
    @ApiModelProperty(value = "", required = true)
    private String searchkey;

    public String getSynid() {
        return synid;
    }

    public void setSynid(String synid) {
        this.synid = synid;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getSearchkey() {
        return searchkey;
    }

    public void setSearchkey(String searchkey) {
        this.searchkey = searchkey;
    }
}
