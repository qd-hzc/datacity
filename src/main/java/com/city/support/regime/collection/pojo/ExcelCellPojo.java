package com.city.support.regime.collection.pojo;

import java.util.List;

/**
 * Created by wgx on 2016/2/19.
 */
public class ExcelCellPojo {
    public static int real =0;//实节点
    public static int virtual = 1;//虚节点
    //单元格值
    private String cellValue;
    //单元格起始行
    private int beginRowCell;
    //单元格结束行
    private int endRowCell;
    //单元格起始列
    private int beginColCell;
    //单元格结束列
    private int endColCell;
    // 是否实节点
    private int nodeType;
    // excel单元格类型
    private Integer cellType;
    public ExcelCellPojo() {
    }

    public ExcelCellPojo(int beginRowCell, int endRowCell, int beginColCell, int endColCell) {
        this.beginRowCell = beginRowCell;
        this.endRowCell = endRowCell;
        this.beginColCell = beginColCell;
        this.endColCell = endColCell;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public int getBeginRowCell() {
        return beginRowCell;
    }

    public void setBeginRowCell(int beginRowCell) {
        this.beginRowCell = beginRowCell;
    }

    public int getEndRowCell() {
        return endRowCell;
    }

    public void setEndRowCell(int endRowCell) {
        this.endRowCell = endRowCell;
    }

    public int getBeginColCell() {
        return beginColCell;
    }

    public void setBeginColCell(int beginColCell) {
        this.beginColCell = beginColCell;
    }

    public int getEndColCell() {
        return endColCell;
    }

    public void setEndColCell(int endColCell) {
        this.endColCell = endColCell;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getCellType() {
        return cellType;
    }

    public void setCellType(Integer cellType) {
        this.cellType = cellType;
    }

    // 处理此行占多行的情况
    public int[] getColCell(int beginColCell,int endColCell,List<ExcelCellPojo> excelExtraCellList){
        int[] colCel = new int[2];
        colCel[0]=beginColCell;
        colCel[1]=endColCell;
        if(excelExtraCellList.size()>0){
            for(ExcelCellPojo excelCell:excelExtraCellList){
                int begin = excelCell.getBeginColCell();
                int end =excelCell.getEndColCell();
                int length = end-begin+1;
                if(beginColCell>=begin){
                    beginColCell+=length;
                    endColCell+=length;
                }
            }
            colCel[0]=beginColCell;
            colCel[1]=endColCell;
            return colCel;

        }else{
            return colCel;
        }
    }

    @Override
    public String toString() {
        return "ExcelCellPojo{" +
                "cellValue='" + cellValue + '\'' +
                ", beginRowCell=" + beginRowCell +
                ", endRowCell=" + endRowCell +
                ", beginColCell=" + beginColCell +
                ", endColCell=" + endColCell +
                '}';
    }
}
