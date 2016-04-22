<%--
  User: HZC
  Date: 2016/4/5
  note: 主题配置页面引入文件
  1、常量
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.city.common.pojo.Constant" %>
<script>
    <%--
        配置主题内容类型
    --%>
    var CONTENT_TYPE = {
//        综合表
        report:<%=Constant.THEME_CONTENT_TYPE.RPT_SYNTHESIZE%>,
//        分析报表
        research:<%=Constant.THEME_CONTENT_TYPE.RPT_CUSTOM%>,
//        图表
        chart:<%=Constant.THEME_CONTENT_TYPE.CHART%>,
//        地图
        map:<%=Constant.THEME_CONTENT_TYPE.MAP%>,
//        分析文字
        textDesc:<%=Constant.THEME_CONTENT_TYPE.TEXT_DESC%>,
//        文件
        file:<%=Constant.THEME_CONTENT_TYPE.FILE%>,
//        目录
        menu:<%=Constant.THEME_CONTENT_TYPE.MENU%>,
//        页面
        page:<%=Constant.THEME_CONTENT_TYPE.PAGE%>,
//        数据集
        dataSet:<%=Constant.THEME_CONTENT_TYPE.DATA_SET%>,
//        分析主题
        textTheme:<%=Constant.THEME_CONTENT_TYPE.TEXT_THEME%>
    };

    //    报表期度
    var REPORT_PERIOD = {
//        年报
        year:<%=Constant.PeriodType.YEAR%>,
//        半年报
        halfYear:<%=Constant.PeriodType.HALF%>,
//        季报
        quarter:<%=Constant.PeriodType.QUARTER%>,
//        月报
        month:<%=Constant.PeriodType.MONTH%>
    }
</script>
