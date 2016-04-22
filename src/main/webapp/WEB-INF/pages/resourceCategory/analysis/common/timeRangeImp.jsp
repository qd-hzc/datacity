<%@ page import="com.city.common.pojo.Constant" %><%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/1
  Time: 11:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<script>
    var TIME_RANGE = {
        LIANXU: <%=Constant.TIMERANGE.LIANXU%>,//时间范围类型:连续
        XUANZE: <%=Constant.TIMERANGE.XUANZE%>,//时间范围类型:选择
        BAOGAOQI: <%=Constant.TIMERANGE.BAOGAOQI%>,//时间范围类型:报告期
        TYPE_REPORT: <%=Constant.TIMERANGE.TYPE_REPORT%>,//分析报表类型
        TYPE_CHART: <%=Constant.TIMERANGE.TYPE_CHART%>,//分析图表类型
        DATA_BEGIN_YEAR: <%=Constant.TIMERANGE.DATA_BEGIN_YEAR%>,//连续:开始年
        DATA_BEGIN_PERIOD: <%=Constant.TIMERANGE.DATA_BEGIN_PERIOD%>,//连续:开始期度
        DATA_END_YEAR: <%=Constant.TIMERANGE.DATA_END_YEAR%>,//连续:结束年
        DATA_END_PERIOD: <%=Constant.TIMERANGE.DATA_END_PERIOD%>,//连续:结束期度
        DATA_YEAR: <%=Constant.TIMERANGE.DATA_YEAR%>,//选择:年份
        DATA_PERIOD: <%=Constant.TIMERANGE.DATA_PERIOD%>,//选择:期度
        DATA_NUMBER: <%=Constant.TIMERANGE.DATA_NUMBER%>//报告期:期数
    };
</script>
