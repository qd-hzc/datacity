package com.city.support.manage.pojo;

/**
 * extjs中树的拖放pojo
 * Created by HZC on 2016/1/6.
 */
public class DragAndDropVO {

    //    当前移动的节点的id
    private String moveId;
    //    当前停留之上的节点的id
    private String overId;
    //    moveId的父id
    private String moveParentId;
    //    overId的父id
    private String overParentId;
    //    拖放位置,[before,after,append]
    private String dropPosition;

    /**
     * Getter for property 'moveId'.
     *
     * @return Value for property 'moveId'.
     */
    public String getMoveId() {
        return moveId;
    }

    /**
     * Setter for property 'moveId'.
     *
     * @param moveId Value to set for property 'moveId'.
     */
    public void setMoveId(String moveId) {
        this.moveId = moveId;
    }

    /**
     * Getter for property 'overId'.
     *
     * @return Value for property 'overId'.
     */
    public String getOverId() {
        return overId;
    }

    /**
     * Setter for property 'overId'.
     *
     * @param overId Value to set for property 'overId'.
     */
    public void setOverId(String overId) {
        this.overId = overId;
    }

    /**
     * Getter for property 'moveParentId'.
     *
     * @return Value for property 'moveParentId'.
     */
    public String getMoveParentId() {
        return moveParentId;
    }

    /**
     * Setter for property 'moveParentId'.
     *
     * @param moveParentId Value to set for property 'moveParentId'.
     */
    public void setMoveParentId(String moveParentId) {
        this.moveParentId = moveParentId;
    }

    /**
     * Getter for property 'overParentId'.
     *
     * @return Value for property 'overParentId'.
     */
    public String getOverParentId() {
        return overParentId;
    }

    /**
     * Setter for property 'overParentId'.
     *
     * @param overParentId Value to set for property 'overParentId'.
     */
    public void setOverParentId(String overParentId) {
        this.overParentId = overParentId;
    }

    /**
     * Getter for property 'dropPosition'.
     *
     * @return Value for property 'dropPosition'.
     */
    public String getDropPosition() {
        return dropPosition;
    }

    /**
     * Setter for property 'dropPosition'.
     *
     * @param dropPosition Value to set for property 'dropPosition'.
     */
    public void setDropPosition(String dropPosition) {
        this.dropPosition = dropPosition;
    }
}
