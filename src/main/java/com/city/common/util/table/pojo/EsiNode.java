package com.city.common.util.table.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxl on 2016/1/26 0026.
 */
public class EsiNode implements Serializable {
    private String dataName;
    private Integer dataType;
    private boolean isRealNode;
    private int depth;
    private Integer span;//如果是主栏,则代表rowspan,colspan使用depth计算,宾栏的反之
    private boolean leaf;
    private List<EsiProperty> properties;
    private List<EsiNode> children;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public boolean isRealNode() {
        return isRealNode;
    }

    public void setRealNode(boolean realNode) {
        isRealNode = realNode;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Integer getSpan() {
        return span;
    }

    public void setSpan(Integer span) {
        this.span = span;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<EsiProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<EsiProperty> properties) {
        this.properties = properties;
    }

    public List<EsiNode> getChildren() {
        return children;
    }

    public void setChildren(List<EsiNode> children) {
        this.children = children;
    }
}
