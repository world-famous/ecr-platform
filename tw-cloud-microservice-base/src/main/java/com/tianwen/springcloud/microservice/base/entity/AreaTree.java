package com.tianwen.springcloud.microservice.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AreaTree implements Serializable{
    private String label;
    private String areaId;
    private List<AreaTree> children;

    public AreaTree(){
        children = new ArrayList<>();
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<AreaTree> getChildren() {
        return children;
    }

    public void setChildren(List<AreaTree> children) {
        this.children = children;
    }

}
