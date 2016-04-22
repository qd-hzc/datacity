package com.city.resourcecategory.themes.pojo;

/**
 * Created by HZC on 2016/3/25.
 */
public class ReportVO {
    //        综合表id，综合表报告期id
    private Integer id;
    //        审核状态：
    //        Constant.RPT_STATUS
    //        public static int ALL = 0;//全部
    //        public static int WAITING_FILL = 1;//待填报
    //        public static int DRAFT = 2;//草稿
    //        public static int WAITING_PASS = 3;//待审
    //        public static int PASS = 4;//已审
    //        public static int REJECT = 5;//已驳回
    private String status = "4";
    //        是否填充数据：0：不填充数据，1：填充数据
    private Integer hasData = 1;
    //        是否带表格宽度和高度：0：不带，1：带样式
    private Integer hasStyle = 1;
    //        报告期年
    private Integer year;
    //        报告期期度：年，半年，季，月
    private Integer m;
    //        频度：1：年报，2：半年报，3：季报，4：月报
    private Integer frequency;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getHasData() {
        return hasData;
    }

    public void setHasData(Integer hasData) {
        this.hasData = hasData;
    }

    public Integer getHasStyle() {
        return hasStyle;
    }

    public void setHasStyle(Integer hasStyle) {
        this.hasStyle = hasStyle;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "ReportVO{" +
                "id=" + id +
                ", status=" + status +
                ", hasData=" + hasData +
                ", hasStyle=" + hasStyle +
                '}';
    }
}
