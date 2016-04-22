package com.city.resourcecategory.themes.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxl on 2016/3/10.
 * 模块内容
 */
public class EsiThemeModuleContent implements Serializable {
    private String containerId;//dom id
    private String containerName;//dom 显示名,便于配置内容
    private List<Integer> contentTypes;//内容可选类型

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public List<Integer> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<Integer> contentTypes) {
        this.contentTypes = contentTypes;
    }
}
