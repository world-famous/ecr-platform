package com.tianwen.springcloud.microservice.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeTree implements Serializable{
    private String label;
    private Knowledge knowledge;
    private List<KnowledgeTree> children;

    public KnowledgeTree(){
        children = new ArrayList<>();
    }

    public Knowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<KnowledgeTree> getChildren() {
        return children;
    }

    public void setChildren(List<KnowledgeTree> children) {
        this.children = children;
    }

}
