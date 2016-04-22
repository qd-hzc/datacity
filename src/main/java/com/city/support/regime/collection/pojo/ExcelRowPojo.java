package com.city.support.regime.collection.pojo;

import java.util.List;

/**
 * Created by wgx on 2016/2/19.
 */
public class ExcelRowPojo {
    // 单元格内容
    List<ExcelCellPojo> excelCellList;
    // 占两行及以上的的单元格
    List<ExcelCellPojo> excelExtraCellList;

    public List<ExcelCellPojo> getExcelCellList() {
        return excelCellList;
    }

    public void setExcelCellList(List<ExcelCellPojo> excelCellList) {
        this.excelCellList = excelCellList;
    }

    public List<ExcelCellPojo> getExcelExtraCellList() {
        return excelExtraCellList;
    }

    public void setExcelExtraCellList(List<ExcelCellPojo> excelExtraCellList) {
        this.excelExtraCellList = excelExtraCellList;
    }

    @Override
    public String toString() {
        return "ExcelRowPojo{" +
                "excelCellList=" + excelCellList +
                ", excelExtraCellList=" + excelExtraCellList+
                '}';
    }
}
