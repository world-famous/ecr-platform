package com.tianwen.springcloud.microservice.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gems on 2018.12.20.
 */
public class ThemeTree implements Serializable {
    private String themeName;
    private Theme themeData;
    private List<ThemeTree> children;

    public ThemeTree() {
        children = new ArrayList<>();
    }

    public ThemeTree(String label) {
        this.themeName = label;
        children = new ArrayList<>();
    }

    public void addChildren(ThemeTree child) {
        children.add(child);
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public Theme getThemeData() {
        return themeData;
    }

    public void setThemeData(Theme themeData) {
        this.themeData = themeData;
    }

    public List<ThemeTree> getChildren() {
        return children;
    }

    public void setChildren(List<ThemeTree> children) {
        this.children = children;
    }
}
