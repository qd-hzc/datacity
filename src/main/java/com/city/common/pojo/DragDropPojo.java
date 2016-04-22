package com.city.common.pojo;

/**
 * Created by Administrator on 2016/1/7 0007.
 * 树节点拖拽排序的实体
 */
public class DragDropPojo {

    /**
     * 要移动的节点
     */
    private Integer moveId;
    /**
     * 目标节点
     */
    private Integer overId;
    /**
     * 目标节点的父节点
     */
    private Integer overParentId;
    /**
     * 移动的位置,[append,before,after]
     */
    private String dropPosition;

    public Integer getMoveId() {
        return moveId;
    }

    public void setMoveId(Integer moveId) {
        this.moveId = moveId;
    }

    public Integer getOverId() {
        return overId;
    }

    public void setOverId(Integer overId) {
        this.overId = overId;
    }

    public Integer getOverParentId() {
        return overParentId;
    }

    public void setOverParentId(Integer overParentId) {
        this.overParentId = overParentId;
    }

    public String getDropPosition() {
        return dropPosition;
    }

    public void setDropPosition(String dropPosition) {
        this.dropPosition = dropPosition;
    }
}
