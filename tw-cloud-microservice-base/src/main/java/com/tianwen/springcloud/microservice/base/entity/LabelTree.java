package com.tianwen.springcloud.microservice.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LabelTree implements Serializable {
    private String label;
    private Label labelData;
    private List<LabelTree> children;

    public LabelTree() {
        children = new ArrayList<>();
    }

    public LabelTree(String label) {
        this.label = label;
        children = new ArrayList<>();
    }

    public Label getLabelData() {
        return labelData;
    }

    public void setLabelData(Label labelData) {
        this.labelData = labelData;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<LabelTree> getChildren() {
        return children;
    }

    public void setChildren(List<LabelTree> children) {
        this.children = children;
    }

    public void addChildren(LabelTree child) {
        children.add(child);
    }
}