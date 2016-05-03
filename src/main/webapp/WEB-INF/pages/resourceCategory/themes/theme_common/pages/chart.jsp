<%--
  User: CRX
  Date: 2016/3/21
  content:6个框的内容页
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    String contextPath = request.getContextPath();
%>
<style>
    .esi-btn {
        border-radius: 20px;
        font-size: 12px;
        line-height: 1.5;
        padding: 5px 20px;
        border: none;
    }

    .esi-chart-row {
        margin-top: 13px;
        margin-left: -14px;
        margin-right: -14px;
    }

    .esi-chart-title-row {
        height: 50px;
        background-color: #F6F9FE;
        margin-left: 1px;
        margin-right: 1px;
        margin-top: -27px;
        padding-top: 15px;
        border-top-left-radius: 4px;
        border-top-right-radius: 4px;
        border-bottom: 1px solid #E8EEF6;
    }

    .esi-chart-title {
        padding-left: 20px;
        font-size: 1.3rem;
        font-weight: 600;
    }

    .esi-chart-time {
        font-size: 1.3rem;
        text-align: right;
        padding-right: 20px;
    }

    .esi-bottom {
        margin-bottom: 30px;
    }

    .esi-bottom > div {
        border-top: 1px solid #E8EEF6;
        border-right: 1px solid #E8EEF6;
    }
</style>
<script type="text/html" data-uuid="${uuid}" data-id="temp">
    <div class=" col-xs-4" style='color: {color};height: 73px;'>
        <p class="text-center" style="font-size: x-large;margin-top: 1rem">{value}</p>

        <p class="text-center" style="color:#a8a297;margin-top: -1rem">{name}</p>
    </div>
</script>
<div class="row esi-chart-row">
    <div class="row esi-chart-title-row esi-chart-row">
        <div class="col-xs-6 esi-chart-title" data-uuid="${uuid}" data-id="chartTitle"></div>
        <div class="col-xs-6 esi-chart-time" data-uuid="${uuid}" data-id="chartTime"></div>
    </div>
    <div data-uuid="${uuid}" data-id='chartdiv1' style="height: 200px;" class="col-md-12">
    </div>
    <div class="col-md-12">
        <div data-uuid="${uuid}" data-id="zhibiao" style="height: 73px;background-color: white;"
             class="row esi-bottom esi-chart-row">
            <div class="col-xs-4 text-center pull-right"
                 style="padding-top:2.5rem;height: 73px;border-right: none;display: none;">
                <button data-uuid="${uuid}" data-id="xiangxi"
                        type="button" class="btn btn-default btn-sm esi-btn"
                        style="background-color: #007AE1;color:white;">
                    详情
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    (function () {
        var Chart = {};
        Chart.contextPath = '<%=contextPath%>';
        Chart.uuid = '${uuid}';
        Chart.page = ${page};
        Chart.contents = Chart.page.contents;
        Chart.esi = new EsiTheme();

        //    初始化
        Chart.init = function () {

            if (Chart.contents && Chart.contents.length > 0) {

                $.each(Chart.contents, function (i, n) {
                    var content = n;

                    if (content.contentType == CONTENT_TYPE.chart) {
                        Chart.setChart(content);
                    } else if (content.contentType == CONTENT_TYPE.dataSet) {
                        Chart.setItem(content);
                    }

                });
            }
        }

        /**
         * 显示详细信息
         */
        Chart.showDetail = function () {
            $('[data-uuid=' + Chart.uuid + '][data-id=xiangxi]').click(function () {
//                Chart.esi.loadSubPage(Chart.page.id, Chart.page);
                var data = JSON.stringify(Chart.page);
                var height = $(window).height() - 63;
                var title = Chart.page.name;
                var html =
                        '<div class="modal-header" style="background-color: #F6F9FE">' +
                        '   <button type="button" class="close" style="margin-right:10px;" data-dismiss="modal" aria-label="Close">' +
                        '       <span aria-hidden="true" class="glyphicon glyphicon-remove"></span>' +
                        '   </button>' +
                        '   <span aria-hidden="true" class="glyphicon glyphicon-star-empty close" style="margin-right: 10px;"></span>' +
                        '   <span aria-hidden="true" class="glyphicon glyphicon-share-alt close" style="margin-right:10px;"></span>' +

                        '   <h4 class="modal-title">' + title + '</h4>' +

                        '</div>' +

                        '<iframe id="iframe-esi" style="width: 100%;height:' + height + 'px;background-color:#E7EBEE;" frameborder="0"></iframe>' +

                        '<script> ' +

                        '   var esi = new EsiTheme(); ' +
                        '   $("#iframe-esi").attr("src",\'' +
                        Chart.contextPath + '/resourcecategory/themes/commonController/loadSubPage?id=' + Chart.page.id + '&data=' + data + '\')' +

                        '<\/script>';

                Chart.esi.detailDialog(html);
            });
        }

        /**
         * 设置图表信息
         * @param content
         */
        Chart.setChart = function (content) {
            Chart.esi.getData(content, function (data) {
                        if (data.success) {
                            var result = data.datas;
                            if (result) {
                                Chart.gg = new EsiChart(result, null, -1);
                                var date = new Date().toLocaleString().split("/");
                                var time = Chart.gg.createDynamicTime(date[0], date[1]);

                                Chart.gg.init(function (option) {
                                    var myChart = echarts.init($('[data-uuid=' + Chart.uuid + '][data-id=' + content.containerId + ']').get(0));
                                    if (myChart && option) {
                                        $('[data-uuid=' + Chart.uuid + '][data-id=chartTitle]').html(option.title.text);
                                        $('[data-uuid=' + Chart.uuid + '][data-id=chartTime]').html(date[0] + '-' + date[1]);
                                        option.legend.show = false;
                                        option.title.show = false;

                                        option.grid = {
                                            borderWidth: 0,
                                            x: '3%',
                                            y: '15%',
                                            x2: '3%',
                                            y2: '5%'
                                        };
                                        if (option.xAxis) {
                                            option.xAxis[0] && ( option.xAxis[0].show = false);
                                            option.xAxis[1] && ( option.xAxis[1].show = false);
                                            option.yAxis[0] && ( option.yAxis[0].show = false);
                                            option.yAxis[1] && ( option.yAxis[1].show = false);
                                        }
                                        myChart.setOption(option);
                                    }
                                }, time);
                            }
                        }
                    }
            )
        }

        /**
         * 设置时间框架信息
         * 仅支持2个时间框架，1个带加减号(±)的时间框架
         * @param content
         */
        Chart.setItem = function (content) {
            Chart.esi.getTimeFrames(content.contentValue, function (data) {
                if (data.success) {
                    var datas = data.datas;
                    if (datas && datas.length > 0) {

                        var htmlStr = '';
                        var temp = $('[data-id=temp][data-uuid=' + Chart.uuid + ']').html();

                        for (var j = 0; j < datas.length; j++) {
//                          只支持显示2个时间框架值
                            if (j > 1)break;
                            var d = datas[j];
                            d.color = 'turquoise';
                            var name = d.name;
//                          时间框架数值设置增长，减少颜色标示
                            if (name.indexOf('±') > -1) {
                                if (d.value.indexOf('-') > -1) {
                                    d.color = 'red';
                                } else {
                                    d.color = 'green';
                                }
                            }
                            d.value = d.value ? d.value : '-';
                            htmlStr += $.render(temp, d);
                        }
                        $('[data-id=zhibiao][data-uuid=' + Chart.uuid + ']').prepend(htmlStr).children(':last').show();
                    }
                }
            });
        }

        $(function () {
            Chart.init();
            Chart.showDetail();
            datetimepicker.on('changeDate', function (ev) {
                var now = ev.date.toLocaleString();
                var date = now.split("/");
                var time = Chart.gg.createDynamicTime(date[0], date[1]);
                Chart.gg.initDynamicMetadata(null, time, time, function (option) {
                    var myChart = echarts.init($('[data-uuid=' + Chart.uuid + '][data-id=chartdiv1]').get(0));
                    if (myChart && option) {
                        $('[data-uuid=' + Chart.uuid + '][data-id=chartTitle]').html(option.title.text);
                        $('[data-uuid=' + Chart.uuid + '][data-id=chartTime]').html(date[0] + '-' + date[1]);

                        option.legend.show = false;
                        option.title.show = false;

                        option.grid = {
                            borderWidth: 0,
                            x: '3%',
                            y: '15%',
                            x2: '3%',
                            y2: '5%'
                        };
                        if (option.xAxis) {
                            option.xAxis[0] && ( option.xAxis[0].show = false);
                            option.xAxis[1] && ( option.xAxis[1].show = false);
                            option.yAxis[0] && ( option.yAxis[0].show = false);
                            option.yAxis[1] && ( option.yAxis[1].show = false);
                        }
                        myChart.setOption(option);
                    }
                }, false)
            });
        });
    })();
</script>
