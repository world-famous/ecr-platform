package com.tianwen.springcloud.microservice.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NavigationTree implements Serializable{
    private String label;
    private DictItem dictItem;
    private List<NavigationTree> children;

    public NavigationTree(){
        children = new ArrayList<>();
    }

    public DictItem getDictItem() {
        return dictItem;
    }

    public void setDictItem(DictItem dictItem) {
        this.dictItem = dictItem;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<NavigationTree> getChildren() {
        return children;
    }

    public void setChildren(List<NavigationTree> children) {
        this.children = children;
    }
}
