<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/4/1
  Time: 15:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <title>数据地图</title>
    <style>
        .mapDataTop {
            padding: 10px;
            padding-bottom: 0;
        }

        .mapTitle {
            margin-left: 20px;

            color: #000;
            font-size: 16px;
            font-weight: 700;
        }

        .mapDiv {
            height: 200px;
            background-color: #fff;
            border: 1px solid #e5e5e5;
        }
    </style>
</head>

<div class="mapcontainer">
    <script data-id="mapDataMenuTemp" data-uuid="${uuid}" type="text/template">
        <li role="presentation" style="cursor: pointer;" data-uuid="${uuid}" data-function="changeTab">
            <a data-id="{id}" data-index="{index}"
               aria-controls="mapData" role="tab" data-toggle="tab">{name}</a>
        </li>

    </script>
    <script data-id="mapDataContentTemp" data-uuid="${uuid}" type="text/template">
        <div data-id="mapDataContent{id}" data-uuid="${uuid}" role="tabpanel" class=""></div>
    </script>
    <script data-id="mapDataRankTemp" data-uuid="${uuid}" type="text/template">
        <li role="presentation" style="cursor: pointer;" data-uuid="${uuid}" area-name="{name}"
            series-name="{seriesname}" area-info="{info}" unit="{unit}">
            <span style="display:table-cell;width:50%;text-align:left;color:#666666;font-size:14px">{name}</span>
            <span style="display:table-cell;width:25%;text-align:right;color:#666666">{value}</span>
            <span style="display:table-cell;width:25%;text-align:right;color:#666666"></span>
        </li>

    </script>
    <div class="container-fluid">
        <div class="row">
            <div class="col-lg-2">
                <ul data-uuid="${uuid}" data-id="mapdatadiv1" class="nav" role="tablist"
                    style="margin-top:20px;width: 200px"></ul>
            </div>
            <div class="col-lg-8">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="mapDataTop"><span data-uuid="${uuid}" data-id="mapTitle"
                                                      class="mapTitle"><strong></strong></span></div>
                        <div data-uuid="${uuid}" data-id="mapdatadiv2" class="tab-content">
                            <div data-uuid="${uuid}" data-id='mapdiv2' style="height: 500px;overflow: auto;">
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="mapDataTop"><span data-uuid="${uuid}" data-id="overviewTitle"
                                                      class="mapTitle"><strong>区域简介</strong></span>
                        </div>
                        <div data-uuid="${uuid}" data-id="overviewDiv" class="mapDiv"/>
                    </div>
                    <div class="col-lg-6">
                        <div class="mapDataTop"><span data-uuid="${uuid}" data-id="chartTitle"
                                                      class="mapTitle"><strong>地区</strong></span></div>
                        <div data-uuid="${uuid}" data-id="chartDiv" class="mapDiv"/>
                    </div>
                </div>
            </div>
            <div class="col-lg-2">
                <div class="mapDataTop"><span data-uuid="${uuid}" data-id="rankTitle"
                                              class="mapTitle"><strong>指标数据</strong></span></div>
                <ul data-uuid="${uuid}" data-id="rankDiv" class="nav" role="tablist"
                    style="margin-top:20px;width: 100%"></ul>
            </div>
        </div>
    </div>
</div>

<!-- Tab panes -->

<script>
    var areaName = "";
    var seriesName = "";
    var chartConfig = null;
    var esiData = null;
    (function () {
        var MapData = {};
        MapData.uuid = '${uuid}';
        MapData.page = ${page};
        //页面返回的目录
        MapData.result = '';
        MapData.contents = MapData.page.contents;
        MapData.esi = new EsiTheme();
        /*        $.each(MapData.contents, function (i, content) {
         if (content.contentType == 7) {
         MapData.content = content;
         } else if (content.contentType == 8) {
         MapData.mapHtml = content;
         }
         })*/
        MapData.content = MapData.contents[0];
        console.log(MapData.content)
        var mapId = 0;
        //    初始化
        MapData.init = function () {
            MapData.esi.getData(MapData.content, function (d) {
                if (d.success) {
                    var pages = d.datas;
                    if (pages && pages.length > 0) {
                        MapData.result = pages;
                        var tempMenuHtml = $('[data-id=mapDataMenuTemp][data-uuid=' + MapData.uuid + ']').html();
                        var tempContentHtml = $('[data-id=navigationContentTemp][data-uuid=' + MapData.uuid + ']').html();
                        var menuHtml = '';
                        var contentHtml = '';
                        $.each(pages, function (i, li) {
                            li.index = i;
                            console.log(li)
                            menuHtml += $.render(tempMenuHtml, li);
                        });
                        var menuContainer = $('[data-id=mapdatadiv1][data-uuid=' + MapData.uuid + ']');
                        menuContainer.html(menuHtml);
                        MapData.loadData(pages[0].contents[0]);
                        var menua = menuContainer.find('li:first').addClass('active').find('a');
                        //MapData.loadMenuData(menua);
                        //$('[data-uuid=' + MapData.uuid + '][data-id=mapdatadiv2]').html(contentHtml);
                        MapData.changeTab();
                    }
                }
            });
            //菜单点击切换
            MapData.changeTab = function () {
                $('[data-function=changeTab][data-uuid=' + MapData.uuid + ']').delegate('a', 'click', function () {
                    var self = $(this);
                    //console.log(self)
                    var index = self.data('index');
                    $('[data-uuid=' + MapData.uuid + '][data-id=navigationdiv2]').children('div').removeClass('active').eq(index).addClass('active');
                    var page = MapData.result[index];
                    MapData.loadData(page.contents[0]);
                    $("div.mapDataTop span[data-id=mapTitle][data-uuid=" + MapData.uuid + "] strong").text(page.name);
                });
            }
            function configHandler(chartConfig, itemName) {
                var config = {};
                config.chart = chartConfig.chart;
                config.series = chartConfig.series;
                config.category = [];
                $.each(config.series, function (i, series) {
                    if (series.info.infoSort == 1) {
                        series.info.chartType = 2;
                    } else {
                        series.info.chartType = 1;
                    }


                })
                var configCategory = {};
                var isExist = false;
                $.each(chartConfig.category, function (i, category) {
                    if (category.info.name == itemName) {
                        configCategory.condition = [];
                        $.each(category.condition, function (j, condition) {
                            if (condition.metaName == "时间") {
                                condition.leaf = true;
                                configCategory.condition.push(condition);
                            } else {
                                condition.leaf = false;
                                configCategory.condition.push(condition);
                            }
                        });
                        configCategory.info = [];//category.info;
                        configCategory.info.name = "时间"
                        configCategory.info.metaType = 666;
                        configCategory.isDynamic = 0;
                        isExist = true;
                        //console.log("configCategory")
                        //console.log(configCategory)
                        return false;
                    }
                });
                //console.log(configCategory)
                if (isExist) {
                    config.category.push(configCategory);
                }
                //console.log("chartConfig");
                //console.log(chartConfig);
                return config
            }

            function selectedCallBack(param) {
                //alert(param.target);
            }

            function hoverCallBack(param) {
                if (areaName != param.name) {
                    areaName = param.name;
                    $("div.mapDataTop span[data-id=overviewTitle][data-uuid=" + MapData.uuid + "] strong").text(param.name);
                    $('[data-id=overviewDiv][data-uuid=' + MapData.uuid + ']').text(param.data.info ? param.data.info : "")
                    $("div.mapDataTop span[data-id=chartTitle][data-uuid=" + MapData.uuid + "] strong").text(param.seriesName + "(" + param.name + ")");
                    var option;
                    var config = configHandler($.extend({}, chartConfig), areaName)
                    //console.log(esiData)
                    var gg = new EsiChart(config, esiData, 2);

                    //console.log(esiData);
                    //console.log(gg.esiDataHandler)
                    //console.log(gg)
                    option = gg.createOption();
                    console.log(option)
                    $.each(option.legend.data, function (i, selected) {
                        option.legend.selected[selected] = true;
                    })
                    option.title = "";
                    //console.log(option)
                    $('[data-id=chartDiv][data-uuid=' + MapData.uuid + ']').html("");
                    var myChart = echarts.init($('[data-id=chartDiv][data-uuid=' + MapData.uuid + ']').get(0));
                    if (myChart) {
                        myChart.setOption(option);
                    }
                }
                /*                    if(k!=param.name){
                 k = param.name;
                 console.log(k)
                 //$("div.mapTop #map3Title strong").text(param.seriesName+"("+k+")");// 标题
                 //createChart('map3Div',formatMapJson(d,param.name),true);
                 //loadChart(k);//鼠标移上去加载选中的区域信息
                 }*/
            }

            function setRank(series) {
                var tempRankHtml = $('[data-id=mapDataRankTemp][data-uuid=' + MapData.uuid + ']').html();
                var rankHtml = '';
                if (series.length && series[0].data)
                    $.each(series[0].data, function (i, li) {
                        li.seriesname = series[0].name;
                        li.unit = series[0].unit
                        rankHtml += $.render(tempRankHtml, li);
                    });
                var rankContainer = $('[data-id=rankDiv][data-uuid=' + MapData.uuid + ']');
                rankContainer.html(rankHtml);
                $('ul[data-id=rankDiv][data-uuid=' + MapData.uuid + '] li').click(function () {
                    var param = [];
                    var $this = $(this);
                    param.name = $this.attr('area-name');//点击时加载选中的区域信息
                    param.seriesName = $this.attr('series-name');
                    param.data = [];
                    param.data.info = $this.attr('area-info')
                    //console.log(param)
                    hoverCallBack(param)
                });
            }

            // 加载当前地图
            MapData.loadData = function (content) {
                MapData.esi.getData(content, function (data) {
                    console.log(content)
                    if (data.success) {
                        var result = data.datas;
                        if (result) {
                            chartConfig = result;
                            var option;
                            var gg = new EsiChart(result);
                            esiData = gg.esiDataHandler;
                            option = gg.createOption();


                            option.title = "";
                            option.series[0].mapLocation = {
                                x: '15%',
                                y: '15%',
                                width: '100%',
                                height: '80%'
                            };
                            var selectedData = [];
                            $.each(option.legend.data, function (i, selected) {
                                if (option.legend.selected[selected]) {
                                    selectedData.push(selected)
                                }
                            })
                            option.legend.data = selectedData;
                            if(content.contentValue==4141) {
                                var seriesByTime = [];
                                $.each(option.series, function (i, series) {
                                    var dataByTime = [];
                                    $.each(series.data, function (j, data) {

                                        if (data.timeStr == '2013年4季度') {
                                            console.log(data.timeStr)
                                            dataByTime.push(data);
                                        }
                                    })
                                    var tmpSeries = $.extend(true, {}, series)
                                    tmpSeries.data = dataByTime;
                                    seriesByTime.push(tmpSeries)
                                })
                                option.series = seriesByTime;
                                var values = [];
                                $.each(option.series[0].data, function (i, mapData) {
                                    values.push(parseFloat(mapData.value));
                                })
                                if (values) {
                                    option.dataRange.min = Math.min.apply(null, values);
                                    option.dataRange.max = Math.max.apply(null, values);
                                }
                            }
                            setRank(option.series);
                            console.log(option)
                            var myChart = echarts.init($('[data-uuid=' + MapData.uuid + '][data-id=mapdiv2]').get(0));
                            gg.configMap("svg1.2.svg");
                            if (myChart) {
                                myChart.setOption(option);
                                myChart.on(echarts.config.EVENT.HOVER, hoverCallBack);
                                myChart.on(echarts.config.EVENT.MAP_SELECTED, selectedCallBack);
                            }
                        }
                    }
                });
                /*                var url = MapData.esi.getPageHtml(self.id,function(){
                 console.log(111)
                 });
                 console.log(url)*/
                //$('#mapdatadiv').attr('src', url);
                //var index = self.data('index');
                // $('[data-uuid=' + NAVIGATION.uuid + '][data-id=navigationdiv2]').children('div').removeClass('active').eq(index).addClass('active');
                // var page = NAVIGATION.result[index];
                //var con = $('[data-id=navigationContent' + page.id + '][data-uuid=' + NAVIGATION.uuid + ']');
                //if (!con.html()) {
                /*                MapData.esi.getPageHtml(self.themePageId, function (data) {
                 if (data.success == false)return;
                 console.log(data)
                 //$(con).html(data);
                 })*/
                //}
            }
            /*            MapData.esi.getData(MapData.mapHtml, function (d) {
             if (d.success) {
             var pages = d.datas;
             var tempContentHtml = $('[data-id=navigationContentTemp][data-uuid=' + MapData.uuid + ']').html();
             var contentHtml = '';
             console.log(pages)
             /!*                    $.each(pages, function (i, li) {
             li.index = i;
             menuHtml += $.render(tempMenuHtml, li);
             });
             var menuContainer = $('[data-id=mapdatadiv1][data-uuid=' + MapData.uuid + ']');
             menuContainer.html(menuHtml);*!/
             //$('[data-uuid=' + MapData.uuid + '][data-id=mapdatadiv2]').html(contentHtml);
             }
             });*/

            /*            MapData.esi.getData(MapData.map, function (data) {
             if (data.success) {
             var result = data.datas;
             if (result) {
             var table = result.table;
             $('[data-uuid=' + MapData.uuid + '][data-id=mapdatadiv2]').html(table);
             }
             }
             });*/
        }
        $(function () {
            MapData.init();
        });
    })()
</script>
