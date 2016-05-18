<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/4/1
  Time: 16:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>地图</title>
</head>
<link href="<%=request.getContextPath()%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/jquery/jquery.esi.1.0.js"></script>
<script src="<%=request.getContextPath()%>/City/resourceCategory/themes/EsiTheme.js"></script>
<script type="text/javascript"
        src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/echarts/EsiChart.js"></script>
<jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
<jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>
<script src="<%=request.getContextPath()%>/Plugins/eharts/echarts-all.js"></script>
<div data-uuid="${uuid}" data-id='mapdiv1' style="height: 200px;overflow: auto;"></div>
<script>
    (function () {
        var mapId = '${mapId}';
        //console.log(mapId);
        var Map = {};
        Map.uuid = '${uuid}';
        Map.page = ${page};

        Map.contents = Map.page.contents;
        Map.esi = new EsiTheme();
        //    初始化
        Map.init = function () {
            Map.esi.getData(Map.contents[0], function (data) {

                if (data.success) {
                    var result = data.datas;
                    if (result) {
                        //console.log(result)
                        var option;
                        var gg = new EsiChart(result);
                        option = gg.createOption();
                        var myChart = echarts.init($('[data-uuid=' + Map.uuid + '][data-id=mapdiv1]').get(0));
                        if (myChart) {
                            myChart.setOption(option);
                        }
                    }
                }
            });
        }
        $(function () {
            Map.init();
        });
    })()
</script>
