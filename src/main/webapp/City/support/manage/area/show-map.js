/**
 * 地区显示地图
 * Created by Paul on 2016/1/8.
 */
Ext.define('Ext.showMapWin', {
    extend: 'Ext.window.Window',
    height: 640,
    width: 880,
    modal: true,
    html: '<div id="mainMap" style="width:100%;height:600px;"></div>'
});
Ext.showMapWin.show = function (mapPath, mapType) {
    var mapWin = Ext.create('Ext.showMapWin', {
        title: '地图',
        frame: false,
        border: false,
        layout: 'border',
        listeners: {
            afterrender: function () {
                MINE.showJsonMap(mapPath,mapType);

            }
        }
    });
    mapWin.show();
}
var MINE = {

    /**
     * 显示json地图
     * @param jsonSvg
     */
    showJsonMap: function (jsonSvg,mapType) {
// --- 地图 ---
        var myChart = echarts.init(document.getElementById('mainMap'));
        if (mapType == 'json') {
            echarts.util.mapData.params.params.beijing = {
                getGeoJson: function (callback) {
                    $.getJSON(contextPath + jsonSvg, function (data) {
                        // 压缩后的地图数据必须使用 decode 函数转换
                        callback(echarts.util.mapData.params.decode(data));
                    });
                }
            };
        } else {
            echarts.util.mapData.params.params.beijing = {
                getGeoJson: function (callback) {
                    $.ajax({
                        url: contextPath + jsonSvg,
                        dataType: 'xml',
                        success: function (xml) {
                            callback(xml)
                        }
                    });
                }
            };
        }
        myChart.setOption({
            backgroundColor: 'white',
            tooltip: {
                trigger: 'item',
                formatter: '{b}'
            },
            series: [
                {
                    name: '中国',
                    type: 'map',
                    mapType: 'beijing',
                    selectedMode: 'multiple',
                    itemStyle: {
                        normal: {label: {show: true}},
                        emphasis: {label: {show: true}}
                    }
                    ,
                    data: [
                        {
                            name: '崂山区',
                            value: Math.round(Math.random() * 1000),
                            itemStyle: {
                                normal: {
                                    color: '#32cd32',
                                    label: {
                                        show: true,
                                        textStyle: {
                                            color: '#fff',
                                            fontSize: 15
                                        }
                                    }
                                },
                                emphasis: {                 // 也是选中样式
                                    borderWidth: 5,
                                    borderColor: 'yellow',
                                    color: '#cd5c5c',
                                    label: {
                                        show: false,
                                        textStyle: {
                                            color: 'blue'
                                        }
                                    }
                                }
                            }
                        }
                    ]
                }
            ]
        });
    }
}