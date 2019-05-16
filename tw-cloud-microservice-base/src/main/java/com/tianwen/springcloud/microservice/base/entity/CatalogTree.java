package com.tianwen.springcloud.microservice.base.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CatalogTree implements Serializable {
    private String label;
    private String id;
    private String parentcatalogid;
    private List<String> catalogids;
    private List<CatalogTree> children;
    private Integer childcount;
    private List<String> typeNames;
    private List<Long> typeCnts;
    private JSONObject extInfo;

    public CatalogTree() {
        catalogids = new ArrayList<>();
        children = new ArrayList<>();
        childcount = 0;
    }

    public CatalogTree(String label, String dictvalue) {
        this.label = label;
        this.id = dictvalue;
        catalogids = new ArrayList<>();
        children = new ArrayList<>();
        childcount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public List<Long> getTypeCnts() {
        return typeCnts;
    }

    public void setTypeCnts(List<Long> typeCnts) {
        this.typeCnts = typeCnts;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getCatalogids() {
        return catalogids;
    }

    public void setCatalogids(List<String> catalogs) {
        catalogids.clear();

        for(String catalog : catalogs)
            if (catalogids.indexOf(catalog) == -1)
                catalogids.add(catalog);
    }

    public List<CatalogTree> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogTree> children) {
        this.children = children;
    }

    public void addChildren(CatalogTree child) {
        if (child.getCatalogids().size() == 1 && child.getCatalogids().get(0) == null)
            return;
        children.add(child);
        childcount = children.size();
    }

    public void addCatalogIds(List<String> catalogids) {
        for (String catid : catalogids)
            this.catalogids.add(catid);
    }

    public void addCatalogid(String catalogid)
    {
        if (catalogids.indexOf(catalogid) == -1)
            catalogids.add(catalogid);
    }

    public Integer getChildcount() {
        return childcount;
    }

    public void setChildcount(Integer childcount) {
        this.childcount = childcount;
    }

    public String getParentcatalogid() {
        return parentcatalogid;
    }

    public void setParentcatalogid(String parentcatalogid) {
        this.parentcatalogid = parentcatalogid;
    }

    public JSONObject getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(JSONObject extInfo) {
        this.extInfo = extInfo;
    }
}