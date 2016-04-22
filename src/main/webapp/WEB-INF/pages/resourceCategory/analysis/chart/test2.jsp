<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/3/7
  Time: 14:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图表预览</title>
    <jsp:include page="/WEB-INF/pages/common/imp.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/echarts/EsiChart.js"></script>
    <script src="<%=request.getContextPath()%>/Plugins/eharts/echarts-all.js"></script>
</head>
<body>
<div id="main" style="height:95%;border:1px solid #ccc;padding:10px;width: 100%"></div>
<script>
    var chartId =${chartId};
    $(function () {
        Ext.Ajax.request({
            url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryAnalysisChartInfoByChartId',
            method: 'POST',
            params: {
                id:chartId
            },
            success: function (response, opts) {
                var config = Ext.JSON.decode(response.responseText);
                var gg = new EsiChart(config);
                console.log(gg)
                gg.addParam(item);
                var option = gg.createOption();
                gg.dynSeries.shift();
                gg.addCategory(item);
                gg.dynCategory.shift();
                gg.addSeriesParam(otherItem);
                option = gg.createOption();
                console.log(option)
                var myChart = echarts.init(document.getElementById('main'));
                if(myChart)
                    myChart.setOption(option);





               // $('#main').highcharts(option);

            }
        });
    })
</script>

</body>
</html>
