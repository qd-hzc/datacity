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
          src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/createEsiChart.js"></script>
  <script src="<%=request.getContextPath()%>/Plugins/eharts/echarts-all.js"></script>
  <script src="<%=request.getContextPath()%>/Plugins/highcharts/highcharts.js "></script>
</head>
<body>
<div id="main" style="height:95%;border:1px solid #ccc;padding:10px;"></div>
<script>
  var chartId =${chartId};
  Ext.onReady(function () {
    Ext.Ajax.request({
      url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart//queryAnalysisChartInfoByChartId',
      method: 'POST',
      params: {
        id: chartId
      },
      success: function (response, opts) {
        var config = Ext.JSON.decode(response.responseText);
        var esichart = new EsiChart(config);

        esichart.loadData();
        //console.log(esichart.getData());
        //esichart.loadData([2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3])
        var option = esichart.createOption();
        /*                console.log(esichart.getData())
         console.log(option)
         //console.log(esichart.createOption());
         var obj = {
         dataValue: 1237,
         dataName: "海安县",
         dataType: 1,
         dataInfo1: 1,
         dataInfo2: 1237
         }
         var obj2 = {
         dataValue: 11,
         dataName: "增速",
         dataType: 12,
         }
         var meta = new Object();
         meta.dynamicSurobj = [obj];
         meta.dynamicTimeframe = [obj2];
         //option = esichart.addDynamicOption(meta,true);
         var meta2 = new Object();
         var obj3 = {
         dataValue: 1241,
         dataName: "通州区",
         dataType: 1,
         dataInfo1: 1,
         dataInfo2: 1241
         }
         meta2.dynamicSurobj = [obj3];*/
        //option = esichart.addDynamicOption(meta2,true);
        //console.log(esichart.getDynamicMetadata());
        //console.log(option);
        //console.log(esichart.getData())
        esichart.setOption($('#main'),option);
        //$('#main').highcharts(option);
      }
    });
  })
</script>

</body>
</html>
