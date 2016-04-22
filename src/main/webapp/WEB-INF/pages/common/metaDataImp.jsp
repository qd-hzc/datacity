<%@ page import="com.city.common.pojo.Constant" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<script>
    //默认调查对象和默认地区id
    var surObjId = <%=Constant.systemConfigPojo.getDefaultAreaId()%>;
    //源数据类型
    var METADATA_TYPE = {
        ITEM: <%=Constant.MetadataType.ITEM%>,//指标
        DYNAMIC_ITEM: <%=Constant.MetadataType.DYNAMIC_ITEM%>,//动态指标
        ITEM_GROUP: <%=Constant.MetadataType.ITEM_GROUP%>,//指标体系
        ITEM_MENU: <%=Constant.MetadataType.ITEM_MENU%>,//指标分组目录
        DYNAMIC_ITEMGROUP: <%=Constant.MetadataType.DYNAMIC_ITEMGROUP%>,//动态指标分组目录
        RESEARCH_OBJ: <%=Constant.MetadataType.RESEARCH_OBJ%>,//调查对象
        DYNAMIC_SUROBJ: <%=Constant.MetadataType.DYNAMIC_SUROBJ%>,//动态调查对象
        RESEARCH_OBJ_GROUP: <%=Constant.MetadataType.RESEARCH_OBJ_GROUP%>,//调查对象分组
        TIME_FRAME: <%=Constant.MetadataType.TIME_FRAME%>,//时间框架
        DYNAMIC_TIMEFRAME: <%=Constant.MetadataType.DYNAMIC_TIMEFRAME%>,//动态时间框架
        TIME: <%=Constant.MetadataType.TIME%>,//时间类型
        TIME_CH: '<%=Constant.MetadataType.TIME_CH%>',//时间类型
        DYNAMIC_TIME: <%=Constant.MetadataType.DYNAMIC_TIME%>,//时间类型
        SYSTEM_DESCRIBE_TYPE: <%=Constant.MetadataType.SYSTEM_DESCRIBE_TYPE%>,//描述类型
        INFO_YEAR:<%=Constant.TIMERANGE.INFO_YEAR%>,//时间频度类型：年
        INFO_PERIOD:<%=Constant.TIMERANGE.INFO_PERIOD%>,//时间频度类型：半年
        DATA_BEGIN_YEAR:<%=Constant.TIMERANGE.DATA_BEGIN_YEAR%>,//连续：开始年
        DATA_BEGIN_PERIOD:<%=Constant.TIMERANGE.DATA_BEGIN_PERIOD%>,//连续：开始期度
        DATA_END_YEAR:<%=Constant.TIMERANGE.DATA_END_YEAR%>,//连续：结束年
        DATA_END_PERIOD:<%=Constant.TIMERANGE.DATA_END_PERIOD%>,//连续：结束期度
        DATA_YEAR:<%=Constant.TIMERANGE.DATA_YEAR%>,//选择：年份
        DATA_PERIOD:<%=Constant.TIMERANGE.DATA_PERIOD%>,//选择：期度
        DATA_NUMBER:<%=Constant.TIMERANGE.DATA_NUMBER%>,//报告期数
        LIANXU:<%=Constant.TIMERANGE.LIANXU%>,//          连续时间范围
        XUANZE:<%=Constant.TIMERANGE.XUANZE%>,//        选择时间范围
        BAOGAOQI:<%=Constant.TIMERANGE.BAOGAOQI%>,//        报告期数时间范围
        getTimeRange: function (type, periodType, data) {
            switch (type) {
                case this.LIANXU:
                    var beginPeriod = "";
                    if (FREQUENCY_TYPE.getString(periodType, data[1].dataValue)) {
                        beginPeriod = FREQUENCY_TYPE.getString(periodType, data[1].dataValue);
                    }
                    var endPeriod = "";
                    if (FREQUENCY_TYPE.getString(periodType, data[3].dataValue)) {
                        endPeriod = FREQUENCY_TYPE.getString(periodType, data[3].dataValue);
                    }
                    return data[0].dataValue + "年"
                            + beginPeriod
                            + "至" + data[2].dataValue + "年"
                            + endPeriod;
                case this.XUANZE :
                    var years = "";
                    var periods = "";
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].dataType == this.DATA_YEAR) {
                            years += data[i].dataValue + ","
                        } else {
                            if (FREQUENCY_TYPE.getString(periodType, data[i].dataValue)) {
                                periods += FREQUENCY_TYPE.getString(periodType, data[i].dataValue) + ",";
                            }

                        }
                    }
                    console.log(data);
                    console.log(years)
                    if (periods) {
                        return "年(" + years.substring(0, years.length - 1) + "),报告期(" + periods.substring(0, periods.length - 1) + ")";
                    } else {
                        return "年(" + years.substring(0, years.length - 1) + ")";
                    }
                case this.BAOGAOQI :
                    return "最新" + data[0].dataValue + "期";
            }
            return '';
        },
        getSingleTime: function (type, periodType, data) {
            var year = "";
            var month = "";
            var isNewPeriod = false;
            switch (type) {
                case this.LIANXU:
                    year = data[0].dataValue;
                    month = data[1].dataValue;
                    break;
                case this.XUANZE :
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].dataType == this.DATA_YEAR) {
                            year = data[i].dataValue;
                        } else {
                            month = data[i].dataValue;
                        }
                    }
                    break;
                case this.BAOGAOQI :
                    isNewPeriod =true;
                    break;
            }
            var obj = {
                isNewPeriod:isNewPeriod,
                year: year,
                month: month
            };
            return obj;
        }
    };
    //图表结构
    var STRUCTURE_TYPE = {
        CATEGORY: <%=Constant.STRUCTURE_TYPE.CATEGORY%>,//分类轴
        SERIES: <%=Constant.STRUCTURE_TYPE.SERIES%>//序列
    };
    //节点类型
    var NODE_TYPE = {
        EMPTY: <%=Constant.NODE_TYPE.EMPTY%>,//虚节点
        EMPTY_CH: '<%=Constant.NODE_TYPE.EMPTY_CH%>',//虚节点
        REAL: <%=Constant.NODE_TYPE.REAL%>,//实节点
        REAL_CH: '<%=Constant.NODE_TYPE.REAL_CH%>',//实节点
        /*        getNodeTypeCH: function (type) {
         switch (type) {
         case this.EMPTY:
         return this.EMPTY_CH;
         case this.REAL :
         return this.REAL_CH;
         }
         return '';
         },*/
    };
    //调查对象类型
    var SUROBJ_TYPE = {
        AREA: <%=Constant.SurObjType.AREA%>,//地区统计对象
        AREA_CH: '<%=Constant.SurObjType.AREA_CH%>',//地区统计对象
        OTHER: <%=Constant.SurObjType.OTHER%>,//其他统计对象
        OTHER_CH: '<%=Constant.SurObjType.OTHER_CH%>',//其他统计对象
        COMPANY: <%=Constant.SurObjType.COMPANY%>,//名录统计对象
        COMPANY_CH: '<%=Constant.SurObjType.COMPANY_CH%>'//名录统计对象
    };
    //报表设计类型
    var RPT_DESIGN_TYPE = {
        SYNTHESIS: <%=Constant.RptDesignType.SYNTHESIS%>,//原始表
        CUSTOM: <%=Constant.RptDesignType.CUSTOM%>,//自定义表
        TYPE_REPORT:<%=Constant.TIMERANGE.TYPE_REPORT%>,//报表分析类型
        TYPE_CHART:<%=Constant.TIMERANGE.TYPE_CHART%>//图表分析类型
    };
    var FREQUENCY_TYPE = {
        YEAR:<%=Constant.FrequencyType.YEAR%>,//年报
        YEAR_STRING: '<%=Constant.FrequencyType.YEAR_STRING%>',//年报
        HALF_UP:<%=Constant.FrequencyType.HALF_UP%>,//上半年
        HALF_UP_STRING: '<%=Constant.FrequencyType.HALF_UP_STRING%>',
        HALF_DOWN:<%=Constant.FrequencyType.HALF_DOWN%>,//下半年
        HALF_DOWN_STRING: '<%=Constant.FrequencyType.HALF_DOWN_STRING%>',
        QUARTER_1:<%=Constant.FrequencyType.QUARTER_1%>,//一季度
        QUARTER_1_STRING: '<%=Constant.FrequencyType.QUARTER_1_STRING%>',
        QUARTER_2:<%=Constant.FrequencyType.QUARTER_2%>,//二季度
        QUARTER_2_STRING: '<%=Constant.FrequencyType.QUARTER_2_STRING%>',
        QUARTER_3:<%=Constant.FrequencyType.QUARTER_3%>,//三季度
        QUARTER_3_STRING: '<%=Constant.FrequencyType.QUARTER_3_STRING%>',
        QUARTER_4:<%=Constant.FrequencyType.QUARTER_4%>,//四季度
        QUARTER_4_STRING: '<%=Constant.FrequencyType.QUARTER_4_STRING%>',
        MONTH_1:<%=Constant.FrequencyType.MONTH_1%>,
        MONTH_1_STRING: '<%=Constant.FrequencyType.MONTH_1_STRING%>',
        MONTH_2:<%=Constant.FrequencyType.MONTH_2%>,
        MONTH_2_STRING: '<%=Constant.FrequencyType.MONTH_2_STRING%>',
        MONTH_3:<%=Constant.FrequencyType.MONTH_3%>,
        MONTH_3_STRING: '<%=Constant.FrequencyType.MONTH_3_STRING%>',
        MONTH_4:<%=Constant.FrequencyType.MONTH_4%>,
        MONTH_4_STRING: '<%=Constant.FrequencyType.MONTH_4_STRING%>',
        MONTH_5:<%=Constant.FrequencyType.MONTH_5%>,
        MONTH_5_STRING: '<%=Constant.FrequencyType.MONTH_5_STRING%>',
        MONTH_6:<%=Constant.FrequencyType.MONTH_6%>,
        MONTH_6_STRING: '<%=Constant.FrequencyType.MONTH_6_STRING%>',
        MONTH_7:<%=Constant.FrequencyType.MONTH_7%>,
        MONTH_7_STRING: '<%=Constant.FrequencyType.MONTH_7_STRING%>',
        MONTH_8:<%=Constant.FrequencyType.MONTH_8%>,
        MONTH_8_STRING: '<%=Constant.FrequencyType.MONTH_8_STRING%>',
        MONTH_9:<%=Constant.FrequencyType.MONTH_9%>,
        MONTH_9_STRING: '<%=Constant.FrequencyType.MONTH_9_STRING%>',
        MONTH_10:<%=Constant.FrequencyType.MONTH_10%>,
        MONTH_10_STRING: '<%=Constant.FrequencyType.MONTH_10_STRING%>',
        MONTH_11:<%=Constant.FrequencyType.MONTH_11%>,
        MONTH_11_STRING: '<%=Constant.FrequencyType.MONTH_11_STRING%>',
        MONTH_12:<%=Constant.FrequencyType.MONTH_12%>,
        MONTH_12_STRING: '<%=Constant.FrequencyType.MONTH_12_STRING%>',
        /**
         * 根据类型返回对应id值得日期名称
         * @param type (int)类型：1：年报，2：半年，3：季，4：月
         * @param id (int)id值：1,2,3,4,5,6,7,8,9,10,11,12
         * @param year
         */
        getTime: function (type, id, year) {
            var period = this.getString(type, id);
            if (period) {
                return year + this.YEAR_STRING + period;
            } else {
                return year + this.YEAR_STRING
            }
        },
        /**
         * 根据类型返回对应id值得期度名称
         * @param type (int)类型：1：年报，2：半年，3：季，4：月
         * @param id (int)id值：1,2,3,4,5,6,7,8,9,10,11,12
         */
        getString: function (type, id) {
            var rs="";
            switch (type) {
                case 1://年报
                    break;
                case 2://半年报
                    switch (id) {
                        case 6:
                            rs = this.HALF_UP_STRING;
                            break;
                        case 12:
                            rs = this.HALF_DOWN_STRING;
                            break;
                    }
                    break;
                case 3://季
                    switch (id) {
                        case 3:
                            rs = this.QUARTER_1_STRING;
                            break;
                        case 6:
                            rs = this.QUARTER_2_STRING;
                            break;
                        case 9:
                            rs = this.QUARTER_3_STRING;
                            break;
                        case 12:
                            rs = this.QUARTER_4_STRING;
                            break;
                    }
                    break;
                case 4://月
                    switch (id) {
                        case 1:
                            rs = this.MONTH_1_STRING;
                            break;
                        case 2:
                            rs = this.MONTH_2_STRING;
                            break;
                        case 3:
                            rs = this.MONTH_3_STRING;
                            break;
                        case 4:
                            rs = this.MONTH_4_STRING;
                            break;
                        case 5:
                            rs = this.MONTH_5_STRING;
                            break;
                        case 6:
                            rs = this.MONTH_6_STRING;
                            break;
                        case 7:
                            rs = this.MONTH_7_STRING;
                            break;
                        case 8:
                            rs = this.MONTH_8_STRING;
                            break;
                        case 9:
                            rs = this.MONTH_9_STRING;
                            break;
                        case 10:
                            rs = this.MONTH_10_STRING;
                            break;
                        case 11:
                            rs = this.MONTH_11_STRING;
                            break;
                        case 12:
                            rs = this.MONTH_12_STRING;
                            break;
                    }
                    break;
            }
            return rs;
        }
    }


</script>
