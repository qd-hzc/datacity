package com.city.support.dataSet.pojo;

/**
 * Created by wxl on 2016/2/24.
 * <p/>
 * 数据集的数据传输实体 统计对象的
 */
public class DataSetAreaInfoPojo extends DataSetInfoPojo {
    private Integer objType;//统计对象类型
    private Integer areaId;//统计地区

    public DataSetAreaInfoPojo(Integer id, String name, Integer objType, Integer areaId) {
        super(id, name);
        this.objType = objType;
        this.areaId = areaId;
    }

    public Integer getObjType() {
        return objType;
    }

    public void setObjType(Integer objType) {
        this.objType = objType;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
