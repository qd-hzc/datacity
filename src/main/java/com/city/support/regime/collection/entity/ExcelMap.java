package com.city.support.regime.collection.entity;

import com.city.support.sys.user.entity.Role;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by wys on 2016/2/3.
 */
@Entity
@Table(name = "SPT_RGM_RPT_EXCELMAP")
public class ExcelMap {
    private Integer id;
    private String excelName;
    private String sheetName;
    private Integer excelRow;
    private Integer excelCol;
    private Integer tmpId;
    private String tmpName;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "importRuleGen", sequenceName = "SPT_RGM_RPT_IMPORTRULE_SEQ")
    @GeneratedValue(generator = "importRuleGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "EXCELCOL")
    public Integer getExcelCol() {
        return excelCol;
    }

    public void setExcelCol(Integer excelCol) {
        this.excelCol = excelCol;
    }

    @Column(name = "EXCELNAME")
    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    @Column(name = "EXCELROW")
    public Integer getExcelRow() {
        return excelRow;
    }

    public void setExcelRow(Integer excelRow) {
        this.excelRow = excelRow;
    }

    @Column(name = "SHEETNAME")
    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    @Column(name = "TMP_ID")
    public Integer getTmpId() {
        return tmpId;
    }

    public void setTmpId(Integer tmpId) {
        this.tmpId = tmpId;
    }

    @Column(name = "TMPNAME")
    public String getTmpName() {
        return tmpName;
    }

    public void setTmpName(String tmpName) {
        this.tmpName = tmpName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExcelMap excelMap = (ExcelMap) o;

        return !(id != null ? !id.equals(excelMap.id) : excelMap.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
