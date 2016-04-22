/**
 * Created by wys on 2016/4/13.
 */
Vue.component('esi-chart', {
    template: '<div :id="esiId" :style="{width:width,height:height}"></div>',
    props: ['width', 'height', 'esiChartId', 'esiId', 'esiChartoption', "esiCharttime"],
    ready: function () {
        console.log('ready-begin');
        var _this = this, esiOption = null, chartContext = this._context;
        if (chartContext) {
            esiOption = chartContext[_this.esiId + '-option'];
        }
        console.log(this._context)
        this.createChart(esiOption);
        console.log('ready-end');
    },
    compiled: function () {
        console.log('compiled1');
    },
    beforeCompile: function () {
        console.log('beforeCompile1');
    },
    data: function () {
        return {
            item: '777'
        }
    },
    methods: {
        createChart: function (extOption) {
            var _this = this;
            if (this.esiChartId) {
                $.ajax(GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryAnalysisChartInfoByChartId', {
                    type: 'POST',
                    data: {
                        id: this.esiChartId
                    },
                    success: function (response, opts) {
                        var config = $.parseJSON(response);
                        console.log(document.getElementById(_this.esiId))
                        var gg = new EsiChart(config, null, -1);
                        _this.gg = gg;
                        var time = null;
                        if (_this.esiCharttime) {
                            time = _this.esiCharttime;
                        }
                        _this.time = time;
                        gg.init(function (option) {
                            myChart = echarts.init(document.getElementById(_this.esiId));
                            if (extOption) {
                                $.extend(true, option, extOption);
                            }
                            if (_this.esiChartoption) {
                                $.extend(true, option, _this.esiChartoption);
                            }
                            console.log(option);
                            if (option) {
                                myChart.setOption(option);
                            }
                        }, time, null, null);
                    }
                });
            }
        },
        addDynamic: function (seriesParamList, categoryParamList) {
            var _this = this;
            this.gg.initDynamicMetadata(seriesParamList, categoryParamList, _this.time, function (_option) {
                if (_option) {
                    var myChart = echarts.init(document.getElementById(_this.esiId));
                    if (myChart) {
                        myChart.setOption(_option);
                    }
                }
            }, true)
        },
    }/*,
    events: {
        'esi-item': function (item) {
            var _this = this;
            this.gg.initDynamicMetadata(item, [], function (_option) {
                var myChart = echarts.init(document.getElementById(_this.esiId));
                if (myChart && _option) {
                    myChart.setOption(_option);
                } else {
                    alert("暂无数据")
                }
            }, true)
        }
    }*/
});
/*
Vue.component('esi-select', {
    template: '<input v-model="item"><button v-on:click="notify">Dispatch Event</button>',
    data: {
        messages: []
    },
    methods: {
        notify: function () {
            if (this.item.trim()) {
                this.$dispatch('esi-item', this.item)
                this.item = ''
            }
        }
    }
});*/
