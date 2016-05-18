<%@ taglib prefix="v-on" uri="http://www.springframework.org/tags/form" %>
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
    <script src="<%=request.getContextPath()%>/Plugins/highcharts/highcharts.js "></script>
    <script src="<%=request.getContextPath()%>/Plugins/vue/vue.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/echarts/VueChart.js"></script>
</head>
<body>
<div id="buttonDiv" style="display:none">
    <button class="button1">2010</button>
    <button class="button1">2011</button>
    <button class="button1">2012</button>
    <button class="button1">2013</button>
    <button class="button1">2014</button>
</div>
<div id="buttonDiv2" style="display:none">
    <button class="menu1">测试1</button>
    <button class="menu1">测试2</button>
    <button class="timeFrame1">增速1</button>
    <button class="timeFrame1">增速2</button>
</div>
<div id="main" style="height:95%;border:1px solid #ccc;padding:10px;">
    <div>
        <esi-chart :esi-id="'echart-'+chartId" width="100%" height="100%"
                   :esi-chart-id="chartId" :esi-charttime="esiCharttime" v-ref:chart>
        </esi-chart>
    </div>
    <div>
    </div>
</div>
<script>
    var chartId =${chartId};
    var time =null; //(2015,10);
    var series = EsiDynamic(103, "zengsu", 12);
    Ext.onReady(function () {
        var navV = new Vue({
            el: '#main',
            data: {
                chartId: chartId,
                esiCharttime:time
            }
        });
        // 访问子组件
        var child = navV.$children[0].$refs.select
        /*$("#buttonDiv2").show();
        $(".timeFrame1").on("click", function () {
            //var timeFrame = this.gg.createDynamicMetadata(103, item, 12);
            navV.$refs.chart.addDynamic(timeFrame)
        })*/
        /*        Ext.Ajax.request({
         url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryAnalysisChartInfoByChartId',
         method: 'POST',
         params: {
         id: chartId
         },
         success: function (response, opts) {
         /!*                var config = Ext.JSON.decode(response.responseText);
         var gg = new EsiChart(config,null,-1);
         console.log(gg)
         //'itemdict':'77,78','item':202,'timeframe':121.0,'depid':'121'
         var itemMenu = gg.createDynamicMetadata(87,"测试动态目录1",3)
         var timeFrame1 = gg.createDynamicMetadata(103,"增速1",12);
         var timeFrame2 = gg.createDynamicMetadata(103,"增速2",12);
         var time = gg.createDynamicMetadata(0,null,METADATA_TYPE.TIME,null,2013,12)
         gg.init(function(option){
         gg.configMap("svg1.2.svg");
         option.series[0].mapLocation = {
         x: '15%',
         y: '15%',
         width: '100%',
         height: '80%'
         };
         var myChart = echarts.init(document.getElementById('main'));
         if(myChart&&option){
         myChart.setOption(option);
         }
         if(chartId == 3790){
         $("#buttonDiv").show();
         $("#buttonDiv2").show();
         //$("#buttonDiv3").show();
         }
         $(".button1").on("click",function(){
         //var time = gg.createDynamicMetadata(0,null,METADATA_TYPE.TIME,null,2014,12)
         var time = gg.createDynamicTime($(this).text(),12);
         gg.initDynamicMetadata(null,time,function(_option){
         var myChart = echarts.init(document.getElementById('main'));
         if(myChart&&_option){
         myChart.setOption(_option);
         }else {
         alert("暂无数据")
         }
         },false)
         })
         $(".menu1").on("click",function(){
         //var time = gg.createDynamicMetadata(0,null,METADATA_TYPE.TIME,null,2014,12)
         var menu = gg.createDynamicMetadata(87,$(this).text(),3);
         gg.initDynamicMetadata(null,menu,function(_option){
         var myChart = echarts.init(document.getElementById('main'));
         if(myChart&&_option){
         myChart.setOption(_option);
         }else {
         alert("暂无数据")
         }
         },true)
         })
         $(".timeFrame1").on("click",function(){
         //var time = gg.createDynamicMetadata(0,null,METADATA_TYPE.TIME,null,2014,12)
         var timeFrame = gg.createDynamicMetadata(103,$(this).text(),12);
         var menu = gg.createDynamicMetadata(87,$(this).text(),3);
         gg.initDynamicMetadata(timeFrame,[],function(_option){
         var myChart = echarts.init(document.getElementById('main'));
         if(myChart&&_option){
         myChart.setOption(_option);
         }else {
         alert("暂无数据")
         }
         },true)
         })
         },[/!*timeFrame1,timeFrame2*!/],time);*!/


         }
         });*/
    })
</script>

</body>
</html>
