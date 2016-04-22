<%--
  User: CRX
  Date: 2016/4/5
  分析图表
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div data-id="chartdiv1" data-uuid="${uuid}" style="width: 500px;height:500px;overflow: auto;"></div>

<script>
    (function () {
        var esiChart = {};

        esiChart.uuid = '${uuid}';
        esiChart.page = ${page};
        esiChart.contents = esiChart.page.contents;
        esiChart.esi = new EsiTheme();

        //    初始化
        esiChart.init = function () {
            var dom = $('[data-uuid=' + esiChart.uuid + '][data-id=chartdiv1]');

            dom.height(dom.parent().height());
            dom.width(dom.parent().width());

            if (esiChart.contents && esiChart.contents.length > 0) {
                var content = esiChart.contents[0];
                if (content.contentType == CONTENT_TYPE.chart) {
                    esiChart.setChart(content);
                }
            }
        }

        /**
         * 设置图表信息
         * @param content
         */
        esiChart.setChart = function (content) {
            esiChart.esi.getData(content, function (data) {
                if (data.success) {
                    var result = data.datas;
                    if (result) {
                        var gg = new EsiChart(result);
                        var option = gg.createOption();
                        var myChart = echarts.init($('[data-uuid=' + esiChart.uuid + '][data-id=chartdiv1]').get(0));
                        if (myChart) {
                            myChart.setOption(option);
                        }
                    }
                } else {
                    $.showError('暂无数据', '提示');
                }
            });
        }

        $(function () {
            esiChart.init();
        });
    })()
</script>

