/**
 * Created by wys on 2016/3/17.
 */
//条件pojo
function EsiCondition(dataType, dataValue, dataInfo1, dataInfo2, dataInfo3) {
    this.dataType = dataType;
    this.dataValue = dataValue;
    if (dataInfo1)this.dataInfo1 = dataInfo1;
    if (dataInfo2)this.dataInfo2 = dataInfo2;
    if (dataInfo3)this.dataInfo3 = dataInfo3;
}
//时间pojo
function EsiTimeRangePojo(frequency, year, period, type, periodsSpan) {
    if (frequency)this.frequency = frequency;
    if (year)this.year = year;
    if (period)this.period = period;
    if (type)this.type = type;
    if (periodsSpan)this.periodsSpan = periodsSpan;
}

//操作echarts option类
function EsiHighchartsOption(esiChart) {
    this.chartOption = null;
    this.esiChart = esiChart;
    this.plug = esiChart.plug;
    this.esiDataHandler = esiChart.esiDataHandler;

}
//创建分类轴
EsiHighchartsOption.prototype.createCategory = function (categoryList, seriesList) {
    var result = [];
    var type;
    var tmpAxis;

    //TODO 现在只有一个x轴，后期扩展多个x轴修改此方法
    $.each(seriesList, function (i, tmpSeries) {
        type = tmpSeries.info.chartType;
        if (type == ANALYSISCHART_INFO.CHART_CURVE || type == ANALYSISCHART_INFO.CHART_COLUMN || type == ANALYSISCHART_INFO.CHART_LINE) {
            var axis = {};
            axis.categories = [];
            for (var j = 0; categoryList && j < categoryList.length; j++) {
                tmpAxis = categoryList[j];
                axis.categories.push(tmpAxis.info.name);
            }
            result.push(axis);
            return false;
        }
    });
    return result;
};
//创建序列
EsiHighchartsOption.prototype.createSeries = function (seriesList, categoryList) {
    var result = [];
    var seriesDatas = [];
    var tmpSeries;
    var tmpseriesData;
    var series;
    var isFind = false;
    //处理散点图
    for (var i = 0; seriesList && i < seriesList.length; i++) {

        tmpSeries = seriesList[i];
        if (tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_PIE) {
            this.hasPie = true
        }
        if (tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_MAP) {
            this.hasMap = true
        }
        if (tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
            this.hasScatter = true;
            for (var j = 0; seriesDatas && j < seriesDatas.length; j++) {
                if (seriesDatas[j].name == tmpSeries.info.group) {
                    seriesDatas[j].datas.push(tmpSeries);
                    seriesDatas[j].units.push(tmpSeries.unit ? tmpSeries.unit : "");
                    isFind = true;
                    break;
                }
            }
        }
        if (!isFind) {
            tmpseriesData = {
                name: tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_SCATTER ? tmpSeries.info.group : tmpSeries.info.name,
                stack: tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_SCATTER ? undefined : tmpSeries.info.group,
                axis: tmpSeries.info.axis,
                isShow: tmpSeries.info.isShow,
                //time:tmpSeries.esiTime.year,
                units: [tmpSeries.unit ? tmpSeries.unit : ""],
                type: ANALYSISCHART_INFO.getEN(tmpSeries.info.chartType, 'echarts'),
                datas: [tmpSeries]
            };
            seriesDatas.push(tmpseriesData);
        }
        isFind = false;
    }
    //生成序列
    for (var i = 0; i < seriesDatas.length; i++) {
        series = {
            name: seriesDatas[i].name,
            type: seriesDatas[i].type,
            //stack: seriesDatas[i].stack,
            isShow: seriesDatas[i].isShow,
            z: i,
            unit: this.hasScatter ? seriesDatas[i].units : seriesDatas[i].units[0],
            yAxisIndex: seriesDatas[i].axis,
            //time:tmpSeries.esiTime.year,
            data: this.pGetData(categoryList, seriesDatas[i].datas, seriesDatas[i].type)
        };
        if (series.type == ANALYSISCHART_INFO.CHART_MAP_EN) {
            series.mapType = seriesDatas[i].stack;
            series.selectedMode = 'single';
        } else {
            series.stack = seriesDatas[i].stack;
        }
        result.push(series);
    }
    //返回生成的序列
    return result;
};
//内部调用根据条件组成序列的data结构
EsiHighchartsOption.prototype.pGetData = function (categoryList, seriesList) {
    var result = [];
    var tmpSeries = null;
    var tmpCategory = null;
    var tmpValue = null;
    for (var j = 0; categoryList && j < categoryList.length; j++) {
        tmpCategory = categoryList[j];
        tmpValue = [];
        for (var i = 0; seriesList && i < seriesList.length; i++) {
            tmpSeries = seriesList[i];
            if (tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
                tmpValue.push(this.esiDataHandler.getData(tmpSeries, tmpCategory));
                if (this.chartOption.xAxis && this.chartOption.xAxis.length < 1) {
                    tmpXaxis = {
                        type: 'value',
                        axisLine: {
                            onZero: false
                        }
                    };
                    this.chartOption.xAxis.push(tmpXaxis);
                }
            } else {
                tmpValue = {};
                tmpValue.name = tmpCategory.info.name;
                tmpValue.value = this.esiDataHandler.getData(tmpSeries, tmpCategory);
                if (tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_MAP) {
                    tmpValue.timeStr = tmpCategory.esiTime?tmpCategory.esiTime.time:"";
                    tmpValue.info = tmpCategory.info.info;
                    tmpValue.selected = false;
                }
            }
        }
        result.push(tmpValue);
    }
    return result;
};

EsiHighchartsOption.prototype.getOption = function (categoryList, seriesList) {
    var _this = this;
    _this.chartOption = {};
    _this.chartOption.title = {
        text: _this.esiChart.title ? _this.esiChart.title + "" : '',
        subtext: _this.esiChart.subTitle ? _this.esiChart.subTitle : ''
    };
    _this.chartOption.tooltip = {
        //trigger: 'axis'
    };
    _this.chartOption.yAxis = null;
    _this.chartOption.xAxis = null;
    _this.chartOption.series = [];
    _this.chartOption.legend = {
        selected: {},
        data: []
    };

    var series = _this.createSeries(seriesList, categoryList);
    // 散点图的x轴和y轴
    var _tmpXaxis = null;
    var _tmpXaxisList = [];
    var _tmpYaxis = null;
    var _tmpYaxisList = [];
    var tmpYaxis = null;
    var tmpYaxisList = [];
    var hasAxis = false;
    $.each(series, function (i, tmpSeries) {
        _this.chartOption.legend.data.push(tmpSeries.name);
        // tmpSeries.isShow==0时不显示
        _this.chartOption.legend.selected[tmpSeries.name] = (tmpSeries && tmpSeries.isShow) ? true : false;
        if (tmpSeries.type != ANALYSISCHART_INFO.CHART_PIE_EN && tmpSeries.type != ANALYSISCHART_INFO.CHART_MAP_EN) {
            hasAxis = true;
        }
        if (!tmpYaxisList[tmpSeries.yAxisIndex]) {
            if (_this.hasScatter) {
                _tmpXaxis = {
                    name: (tmpSeries.unit && tmpSeries.unit.length > 1) ? tmpSeries.unit[0] : "",
                    type: 'value',
                    scale: true,
                    axisLine: {
                        onZero: false
                    }
                };
                _tmpYaxis = {
                    name: (tmpSeries.unit && tmpSeries.unit.length > 1) ? tmpSeries.unit[1] : "",
                    type: 'value',
                    scale: true,
                    axisLine: {
                        onZero: false
                    }
                };
                _tmpXaxisList.push(_tmpXaxis);
                _tmpYaxisList.push(_tmpYaxis);
            } else {
                tmpYaxis = {
                    name: tmpSeries.unit ? tmpSeries.unit : "",
                    type: 'value',
                    scale: true,
                    axisLine: {
                        onZero: false
                    }
                };
                if (tmpSeries.yAxisIndex > 0)
                    tmpYaxis.splitLine = {
                        show: false
                    };
                if (tmpSeries.type == ANALYSISCHART_INFO.CHART_LINE_EN) {
                    tmpYaxis.boundaryGap = [1, 5];
                }
                tmpYaxisList[tmpSeries.yAxisIndex] = tmpYaxis;
            }
        }
    });
    if (hasAxis && !_this.hasScatter) {
        _this.chartOption.yAxis = tmpYaxisList;
    }
    _this.chartOption.series = series;

    var category = _this.createCategory(categoryList, seriesList);
    var tmpXaxisList = [];
    var tmpXaxis = null;
    $.each(category, function (i, tmpAxis) {
        tmpXaxis = {
            type: 'category',
            data: tmpAxis.categories,
            axisLine: {
                onZero: false
            }
        };
        tmpXaxisList.push(tmpXaxis)
    });
    if (!_this.hasMap && !_this.hasPie && !_this.hasScatter) {
        _this.chartOption.xAxis = tmpXaxisList;
    }
    if (_this.hasScatter) {
        _this.chartOption.yAxis = _tmpYaxisList;
        _this.chartOption.xAxis = _tmpXaxisList
    }
    if (_this.hasMap&&_this.chartOption&&_this.chartOption.series.length) {
        var mapDatas = _this.chartOption.series[0].data;
        var values = [];
        $.each(mapDatas,function(i,mapData){
            values.push(parseFloat(mapData.value));
        })
        if(values) {
            _this.chartOption.dataRange = {
                min: Math.min.apply(null, values),
                max: Math.max.apply(null, values),
                formatter: function (s) {
                    return s.substring(0, s.indexOf(".") + 3);
                },
                x: '5%',
                y: '50%',
                tdext: ['High', 'Low'],
                calculable: true,
                itemHeight: 15,
                color: ['orangered', 'yellow', 'lightskyblue']
            }
        }
    }

    return _this.chartOption;
};

//数据操作类
function EsiDataHandler() {

    this.data = [];
    this.dataMap = {};
}
//根据序列和分类获取数据
EsiDataHandler.prototype.getData = function (series, category) {
    var tmpData = this.dataMap[series.info.id] ? this.dataMap[series.info.id] : this.data;
    var result = '-';
    var time = series.esiTime ? series.esiTime : (category.esiTime ? category.esiTime : null);
    var conditions = [];
    $.merge(conditions, category.condition);
    $.merge(conditions, series.condition);
    var menuList = [];
    var menu = '0';
    $.each(conditions, function (i, condition) {
        if (condition.metaType == METADATA_TYPE.ITEM_MENU) {
            menuList.push(condition.metaId);
        }
    });
    menuList.sort();
    if (menuList.length > 0) {
        menu = menuList.join(",");
    }
    var metaExt = null;
    if (tmpData) {
        $.each(tmpData, function (i, dataObj) {
            var yesItIs = true;
            if (time) {
                if (dataObj.time.period == time.period && dataObj.time.year == time.year) {
                    $.each(dataObj.datas, function (j, data) {
                        yesItIs = true;
                        $.each(conditions, function (k, condition) {
                            metaExt = $.parseJSON(condition.metaExt);
                            switch (condition.metaType) {
                                case METADATA_TYPE.ITEM:
                                    if (!metaExt.caliber) {
                                        metaExt.caliber = 0;
                                    }
                                    if (condition.metaId != data.reportDataId.item || metaExt.caliber != data.reportDataId.itemCaliber || metaExt.dep != data.reportDataId.depId) {
                                        yesItIs = false;
                                    }
                                    if(metaExt.rptTmp&&metaExt.rptTmp!=data.rptTmpId){
                                        yesItIs = false;
                                   }
                                    break;
                                case METADATA_TYPE.TIME_FRAME:
                                    if (condition.metaId != data.reportDataId.timeFrame) {
                                        yesItIs = false;
                                    }
                                    break;
                                case METADATA_TYPE.RESEARCH_OBJ:
                                    if (condition.metaId != data.reportDataId.surobj || metaExt.objType != data.reportDataId.surobjType) {
                                        yesItIs = false;
                                    }
                                    break;
                                case METADATA_TYPE.ITEM_MENU:
                                    if (menu != data.reportDataId.itemDict) {
                                        yesItIs = false;
                                    }
                                    break;
                            }
                            if (!yesItIs) {
                                return false;
                            }
                        });

                        if (yesItIs) {
                            if (data.itemValue)
                                result = parseFloat(data.itemValue);
                            return false;
                        }
                    });
                    if (yesItIs) {
                        return false;
                    }
                }
            }
        });
    }
    return result;
};


EsiDataHandler.prototype.loadData = function (datas, isAdd) {
    if (isAdd) {
        this.data = this.data.concat(datas);
    } else {
        this.data = datas;
    }

};
// 异步加载图表数据
/**
 *
 * @param fn                       回调函数
 * @param seriesParam              动态序列
 * @param categoryParam            动态分类轴
 */
EsiChart.prototype.init = function (fn,time, seriesParam, categoryParam) {
    this.dynSeries = [];
    this.dynCategory = [];
    if(this.chartType) {
        var addDynamicSeries = [];
        var addDynamicCategory = [];
        if (seriesParam && seriesParam.dynamicType) {
            addDynamicSeries = this.getDynamicSeries(seriesParam)
        } else if (seriesParam && seriesParam.length) {
            addDynamicSeries = this.getDynamicSeriesList(seriesParam, this);
        }
        if (addDynamicSeries && addDynamicSeries.length) {
            $.extend(this.dynSeries, addDynamicSeries);
        }
        if (categoryParam && categoryParam.dynamicType) {
            addDynamicCategory = this.getDynamicCategory(categoryParam);
        } else if (categoryParam && categoryParam.length) {
            addDynamicCategory = this.getDynamicCategoryList(categoryParam, this);
        }
        if (addDynamicCategory && addDynamicCategory.length) {
            $.extend(this.dynCategory, addDynamicCategory);
        }
        var series = [];
        $.extend(series, this.esiSeriesList)
        var categories = [];
        $.extend(categories, this.esiCategoryList)
        this.esiDataHandler.load(series.concat(this.dynSeries), categories.concat(this.dynCategory), time, false, true, fn, this);
    }else{
        this.esiDataHandler.load(this.esiSeriesList, this.esiCategoryList, time, false, true, fn, this);
    }
}
//根据序列加载数据
/**
 *
 * @param seriesList               序列
 * @param categoryList             分类轴
 * @param isAdd                    是否为添加数据
 * @param isAsync                  是否异步加载
 * @param fn                       回调函数
 */
EsiDataHandler.prototype.load = function (seriesList, categoryList,time, isAdd, isAsync, fn, esichart) {
    var _isAsync = isAsync ? isAsync : false;
    var esiConditionLists = [];
    var esiTimeRangePojoLists = [];
    var tmpSeries = null;
    var conditions = [];
    var handleResult = null;
    var _this = this;
    //var _isAdd = isAdd;
    var aaa = null;
    var kkk = null;
    for (var i = 0; seriesList && i < seriesList.length; i++) {
        tmpSeries = seriesList[i];
        conditions = tmpSeries.condition;
        kkk = [];
        for (var j = 0; categoryList && j < categoryList.length; j++) {
            aaa = conditions.concat(categoryList[j].condition);
            handleResult = _this.handleConditions(aaa,time);
            kkk = kkk.concat(handleResult.conditionList);

        }
        if (kkk.length > 0)
            esiConditionLists.push(kkk);
        if (handleResult)
            esiTimeRangePojoLists.push(handleResult.timeList);

    }
    if (esiTimeRangePojoLists.length > 0) {
        $.ajax({
            type: 'post',
            async: _isAsync,
            url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryChartDatasByCondition',
            data: {
                timeListStr: JSON.stringify(esiTimeRangePojoLists),
                dataSets: JSON.stringify(esiConditionLists)
            },
            dataType: 'json',
            success: function (data) {
                if (!isAdd) {
                    _this.dataMap = {};
                    _this.data = [];
                }
                var isScatter = false;
                for (var i = 0; seriesList && i < seriesList.length; i++) {
                    if (seriesList[i].info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
                        isScatter = true;
                    }
                    if (!_this.dataMap[seriesList[i].info.id]) {
                        _this.dataMap[seriesList[i].info.id] = (data[i] ? data[i] : []);
                    } else {
                        _this.dataMap[seriesList[i].info.id] = _this.dataMap[seriesList[i].info.id].concat((data[i] ? data[i] : []));
                    }
                    if (data[i])
                        _this.loadData(data[i], true);
                }
                if (fn) {
                    var option = null;
                    if (esichart) {
                        option = esichart.createOption();
                    }
                    fn(option, esichart);
                }
            }
        });
    }

};
//处理请求数据的条件
EsiDataHandler.prototype.handleConditions = function (conditions,time) {
    var result = {};//{conditionList:[],timeList:[]}
    result.conditionList = [];
    result.timeList = [];
    var tmpCondition = null;
    var metaExt = null;
    var itemMenu = [];
    var lianxu = {};
    for (var i = 0; conditions && i < conditions.length; i++) {
        lianxu = {};
        tmpCondition = conditions[i];
        switch (tmpCondition && tmpCondition.metaType) {
            case METADATA_TYPE.ITEM:
                metaExt = $.parseJSON(tmpCondition.metaExt);
                result.conditionList.push(new EsiCondition(METADATA_TYPE.ITEM, tmpCondition.metaId, metaExt.caliber, metaExt.dep, metaExt.rptTmp));
                break;
            case METADATA_TYPE.TIME_FRAME:
                result.conditionList.push(new EsiCondition(METADATA_TYPE.TIME_FRAME, tmpCondition.metaId));
                break;
            case METADATA_TYPE.RESEARCH_OBJ:
                metaExt = $.parseJSON(tmpCondition.metaExt);
                result.conditionList.push(new EsiCondition(METADATA_TYPE.RESEARCH_OBJ, tmpCondition.metaId, metaExt.objType, metaExt.areaId));
                break;
            case METADATA_TYPE.ITEM_MENU:
                itemMenu.push(tmpCondition.metaId);
                break;
            case METADATA_TYPE.TIME:
                metaExt = $.parseJSON(tmpCondition.metaExt);
                for (var j = 0; metaExt.timeRange && j < metaExt.timeRange.length; j++) {
                    switch (metaExt.timeRange[j].type) {
                        case METADATA_TYPE.XUANZE:
                            if (metaExt.timeRange[j].dataType == METADATA_TYPE.DATA_YEAR) {
                                for (var k = 0; k < metaExt.timeRange.length; k++) {
                                    if (metaExt.timeRange[k].dataType == METADATA_TYPE.DATA_PERIOD) {
                                        result.timeList.push(new EsiTimeRangePojo(metaExt.periodType, metaExt.timeRange[j].dataValue, metaExt.timeRange[k].dataValue, METADATA_TYPE.XUANZE))
                                    }
                                }
                            }
                            break;
                        case METADATA_TYPE.LIANXU:
                            switch (metaExt.timeRange[j].dataType) {
                                case METADATA_TYPE.DATA_BEGIN_YEAR:
                                    lianxu[METADATA_TYPE.DATA_BEGIN_YEAR] = metaExt.timeRange[j].dataValue;
                                    break;
                                case METADATA_TYPE.DATA_END_YEAR:
                                    lianxu[METADATA_TYPE.DATA_END_YEAR] = metaExt.timeRange[j].dataValue;
                                    break;
                                case METADATA_TYPE.DATA_BEGIN_PERIOD:
                                    lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD] = metaExt.timeRange[j].dataValue;
                                    break;
                                case METADATA_TYPE.DATA_END_PERIOD:
                                    lianxu[METADATA_TYPE.DATA_END_PERIOD] = metaExt.timeRange[j].dataValue;
                                    break;
                            }
                            if (lianxu[METADATA_TYPE.DATA_BEGIN_YEAR] && lianxu[METADATA_TYPE.DATA_END_YEAR] && lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD] && lianxu[METADATA_TYPE.DATA_END_PERIOD]) {
                                result.timeList.push(new EsiTimeRangePojo(metaExt.periodType, lianxu[METADATA_TYPE.DATA_BEGIN_YEAR], lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD], METADATA_TYPE.LIANXU));
                                result.timeList.push(new EsiTimeRangePojo(metaExt.periodType, lianxu[METADATA_TYPE.DATA_END_YEAR], lianxu[METADATA_TYPE.DATA_END_PERIOD], METADATA_TYPE.LIANXU));
                            }
                            break;
                        case METADATA_TYPE.BAOGAOQI:
                            var year = null;
                            var month = null;
                            /*                    timeRange.push({
                             dataType: METADATA_TYPE.DATA_YEAR,
                             dataValue: dataInfo2,
                             type: METADATA_TYPE.XUANZE
                             });
                             timeRange.push({
                             dataType: METADATA_TYPE.DATA_PERIOD,
                             dataValue: dataInfo3,
                             type: METADATA_TYPE.XUANZE
                             });
                             obj = {
                             timeRange: timeRange,
                             periodType: _this.period
                             };*/
                            if(time&&time.metaExt){
                                $.each($.parseJSON(time.metaExt).timeRange,function(i,timeRange){
                                    switch(timeRange.dataType) {
                                        case METADATA_TYPE.DATA_YEAR:
                                            year = timeRange.dataValue;
                                            break;
                                        case METADATA_TYPE.DATA_PERIOD:
                                            month = timeRange.dataValue;
                                            break;
                                    }
                                })
                            }
                            result.timeList.push(new EsiTimeRangePojo(metaExt.periodType, year, month, METADATA_TYPE.BAOGAOQI, metaExt.timeRange[j].dataValue));
                            break;
                    }

                }
        }
    }
    itemMenu.sort();
    if (itemMenu.length < 1) {
        itemMenu.push("0")
    }
    result.conditionList.push(new EsiCondition(METADATA_TYPE.ITEM_MENU, itemMenu.join(',')));
    return result;
};

function EsiChart(config, esiData, dataType) {
    if (config) {
        this.chartId = config.chart.id;
        this.title = config.chart.title;                //标题
        this.subTitle = config.chart.subTitle;           //副标题
        this.chartType = config.chart.chartType;        //图表类型
        this.period = config.chart.periodType;          //周期
        this.timeline = config.chart.timeline;          //时间轴(暂不用)
        this.plug = config.chart.plug;                  //插件
        this.chartStyle = config.chart.charStyle;      //样式
        this.dataSet = config.chart.dataSet;          //数据集
        this.esiCategory = config.category;                //分类轴
        this.esiSeries = config.series;                    //序列
        this.esiDataHandler = new EsiDataHandler(this);
        this.esiChartOptionHandler = new EsiHighchartsOption(this);
        this.initChartStructure();
        if (!esiData && !dataType) {
            this.esiDataHandler.load(this.esiSeriesList, this.esiCategoryList);
        } else {
            switch (dataType) {
                case 1:// 纯数据
                    break;
                case 2:// 格式化后的数据
                    this.esiDataHandler = esiData;
                    this.esiChartOptionHandler.esiDataHandler = esiData;
                    break;
                default:

            }

        }
        this.dynamicMetadata = null;
        this.dynSeries = [];
        this.dynCategory = [];
        this.isMap = false;
    }
}

// 根据插件配置空图表
EsiChart.prototype.createOption = function (plug) {
    if (!this.esiChartOptionHandler)
        this.esiChartOptionHandler = new EsiHighchartsOption();
    var ss = this.handleStruct(this.esiSeriesList.concat(this.dynSeries), this);
    var cc = this.handleStruct(this.esiCategoryList.concat(this.dynCategory), this);
    return this.esiChartOptionHandler.getOption(cc, ss);
};
function EsiDynamic(metaId, metaName, metaType, dataInfo1, dataInfo2, dataInfo3, endYear, endPeriod){
    var gg = new EsiChart();
    return gg.createMetadata(metaId, metaName, metaType, dataInfo1, dataInfo2, dataInfo3, endYear, endPeriod)
}
function EsiDynamicTime(year,month){
    var gg = new EsiChart();
    return gg.createDynamicTime(year,month);
}
/**
 * 插入元数据，替换动态元数据
 */
EsiChart.prototype.createMetadata = function (metadata) {
    if (metadata) {
        return this.createDynamicMetadata(metadata.id, metadata.dataName, metadata.dataType, metadata.dataInfo1, metadata.dataInfo2, metadata.dataInfo3);
    }
    return null;
}
/**
 * 插入单个时间
 * @param year
 * @param month
 * @returns {{dynamicType: number, metaId: *, metaName: *, metaType: *, metaExt: *}}
 */
EsiChart.prototype.createDynamicTime = function (year, month) {
    return this.createDynamicMetadata(0, null, METADATA_TYPE.TIME, null, year, month)
}
/**
 * 根据时间类型插入时间
 * @param type
 * @param year
 * @param month
 * @param endYear
 * @param endPeriod
 * @returns {{dynamicType: number, metaId: *, metaName: *, metaType: *, metaExt: *}}
 */
EsiChart.prototype.createDynamicTimeByType = function (type, year, month, endYear, endPeriod) {
    return this.createDynamicMetadata(0, null, METADATA_TYPE.TIME, type, year, month, endYear, endPeriod)
}

/**
 /**
 * 生成动态元数据
 * @param metaId              元数据id
 * @param metaName            元数据名称
 * @param metaType            元数据类型
 * @param dataInfo1           若是统计对象类型,则dataInfo1表示统计对象类型
 *                             若是指标类型,则dataInfo1表示指标口径
 *                             若是时间类型，则dataInfo1表示时间类型
 * @param dataInfo2           若是统计对象类型,则dataInfo1表示统计地区
 *                             若是指标类型,则dataInfo2表示部门
 *                             若是时间类型，则dataInfo2表示年、开始年或期度
 * @param dataInfo3           若是指标类型,则dataInfo3表示所属报表
 *                             若是时间类型，则dataInfo3表示月、开始月
 * @param endYear             结束年
 * @param endPeriod           结束月
 * @returns {{dynamicType: number, metaId: *, metaName: *, metaType: *, metaExt: *}}
 */
EsiChart.prototype.createDynamicMetadata = function (metaId, metaName, metaType, dataInfo1, dataInfo2, dataInfo3, endYear, endPeriod) {
    var metaExt = getMeatExtJson(this, metaType, dataInfo1, dataInfo2, dataInfo3, endYear, endPeriod)
    var dynamicType = 0;
    switch (metaType) {
        case METADATA_TYPE.TIME:
            metaId = 0;
            metaName = METADATA_TYPE.TIME_CH;
            dynamicType = METADATA_TYPE.DYNAMIC_TIME;
            break;
        case    METADATA_TYPE.ITEM:
            dynamicType = METADATA_TYPE.DYNAMIC_ITEM;
            break;
        case    METADATA_TYPE.RESEARCH_OBJ:
            dynamicType = METADATA_TYPE.DYNAMIC_SUROBJ;
            break;
        case    METADATA_TYPE.TIME_FRAME:
            dynamicType = METADATA_TYPE.DYNAMIC_TIMEFRAME;
            break;
        case   METADATA_TYPE.ITEM_MENU:
            dynamicType = METADATA_TYPE.DYNAMIC_ITEMGROUP;
            break;
    }
    var obj = {
        dynamicType: dynamicType ? dynamicType : 0,
        metaId: metaId ? metaId : 0,
        metaName: metaName ? metaName : "",
        metaType: metaType ? metaType : 0,
        metaExt: metaExt ? metaExt : null
    }
    return obj
};
function getMeatExtJson(_this, metaType, dataInfo1, dataInfo2, dataInfo3, endYear, endPeriod) {
    switch (metaType) {
        case METADATA_TYPE.TIME:
            var timeRange = []
            var obj;
            switch (dataInfo1) {
                case METADATA_TYPE.LIANXU:
                    if (!dataInfo2 || !dataInfo3) {
                        return null;
                    }
                    timeRange = [{
                        dataType: METADATA_TYPE.DATA_BEGIN_YEAR,
                        dataValue: dataInfo2,
                        type: METADATA_TYPE.LIANXU
                    },
                        {dataType: METADATA_TYPE.DATA_BEGIN_PERIOD, dataValue: dataInfo3, type: METADATA_TYPE.LIANXU},
                        {
                            dataType: METADATA_TYPE.DATA_END_YEAR,
                            dataValue: endYear ? endYear : dataInfo2,
                            type: METADATA_TYPE.LIANXU
                        },
                        {
                            dataType: METADATA_TYPE.DATA_END_PERIOD,
                            dataValue: endPeriod ? endPeriod : dataInfo3,
                            type: METADATA_TYPE.LIANXU
                        }];
                    obj = {
                        timeRange: timeRange,
                        periodType: _this.period
                    }
                    return JSON.stringify(obj);
                case METADATA_TYPE.XUANZE:
                    if (!dataInfo2 || !dataInfo3) {
                        return null;
                    }
                    if (dataInfo2.length) {
                        //年
                        for (var i = 0; i < dataInfo2.length; i++) {
                            timeRange.push({
                                dataType: METADATA_TYPE.DATA_YEAR,
                                dataValue: dataInfo2[i],
                                type: METADATA_TYPE.XUANZE
                            });
                        }
                    } else {
                        timeRange.push({
                            dataType: METADATA_TYPE.DATA_YEAR,
                            dataValue: dataInfo2,
                            type: METADATA_TYPE.XUANZE
                        });
                    }
                    if (dataInfo3.length) {
                        //报告期
                        for (var i = 0; i < dataInfo3.length; i++) {
                            timeRange.push({
                                dataType: METADATA_TYPE.DATA_PERIOD,
                                dataValue: dataInfo3[i],
                                type: METADATA_TYPE.XUANZE
                            });
                        }
                    } else {
                        timeRange.push({
                            dataType: METADATA_TYPE.DATA_PERIOD,
                            dataValue: dataInfo3,
                            type: METADATA_TYPE.XUANZE
                        });
                    }
                    obj = {
                        timeRange: timeRange,
                        periodType: _this.period
                    };
                    return JSON.stringify(obj);
                case METADATA_TYPE.BAOGAOQI:
                    if (!dataInfo2) {
                        return null;
                    }
                    obj = {
                        timeRange: [{
                            dataType: METADATA_TYPE.DATA_NUMBER,
                            dataValue: dataInfo2,
                            type: METADATA_TYPE.BAOGAOQI
                        }],
                        periodType: _this.period
                    }
                    return JSON.stringify(obj);
                default:// 默认为添加单个报告期
                    if (!dataInfo2 || !dataInfo3) {
                        return null;
                    }
                    timeRange.push({
                        dataType: METADATA_TYPE.DATA_YEAR,
                        dataValue: dataInfo2,
                        type: METADATA_TYPE.XUANZE
                    });
                    timeRange.push({
                        dataType: METADATA_TYPE.DATA_PERIOD,
                        dataValue: dataInfo3,
                        type: METADATA_TYPE.XUANZE
                    });
                    obj = {
                        timeRange: timeRange,
                        periodType: _this.period
                    };
                    return JSON.stringify(obj);
            }
            return null;
        case    METADATA_TYPE.ITEM:
            if (!dataInfo2) {
                return null;
            }
            var obj = {
                caliber: dataInfo1 ? dataInfo1 : "",
                dep: dataInfo2,
                rptTmp: dataInfo3 ? dataInfo3 : "",
            };
            return JSON.stringify(obj);
        case    METADATA_TYPE.RESEARCH_OBJ:
            if (!dataInfo1) {
                return null;
            }
            var obj = {
                objType: dataInfo1
            }
            if (dataInfo1 != SUROBJ_TYPE.AREA) {
                obj.areaId = dataInfo2;
            }
            return JSON.stringify(obj);
        default:
            return null;
    }
}
/**
 * 添加动态元数据
 * @param param  动态元数据
 * @param type   1序列 2分类轴
 * @param fn     回调函数
 */
/*EsiChart.prototype.addParam = function (param, type, isAdd) {
 switch (type) {
 case 1:
 this.addSeriesParam(param, isAdd);
 break;
 case 2:
 this.addCategoryParam(param, isAdd);
 break;
 default:

 }
 }*/
/**
 * 转换动态序列
 * @param item
 * @returns {Array}
 */
EsiChart.prototype.getDynamicSeries = function (item) {
    var addDynamic = [];
    var dynamicEsiSeriesList = $.extend(true, {}, this.dynamicEsiSeriesList)
    $.each(dynamicEsiSeriesList, function (i, tmpDynSeries) {
        var addSeries = {};
        $.each(tmpDynSeries.condition, function (j, condition) {
            // 动态分组目录添加有问题，暂时不添加
            if (condition.metaType == item.dynamicType) {
                /*                if (condition.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {
                 $.each(tmpDynCategory.condition, function (jj, tmpCondition) {
                 if (tmpCondition.dynamicType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {
                 tmpDynCategory.condition.splice(jj, 1);
                 }
                 });
                 }*/
                $.extend(addSeries, tmpDynSeries);
                addSeries.condition.push(item);
                addSeries.isDynamic = 0;
                if (condition.leaf) {
                    addSeries.info.metaType = item.metaType;
                    addSeries.info.name = item.metaName;
                }
                addDynamic.push(addSeries);
                return false;
            }

        });
    });
    return addDynamic;
}
/**
 * 添加动态序列
 * @param item
 * @param isAdd
 */
/*EsiChart.prototype.addSeriesParam = function (item, isAdd, isAsync, fn) {
 var _isAsync = isAsync ? isAsync : false;
 var addDynamic = [];
 if (!isAdd) {
 this.dynSeries = [];
 }
 addDynamic = this.getDynamicSeries(item);
 if (addDynamic.length > 0) {
 $.extend(this.dynSeries, addDynamic);
 var categories = [];
 $.extend(categories, this.esiCategoryList);
 this.esiDataHandler.load(addDynamic, categories.concat(this.dynCategory), true, _isAsync, fn);
 }
 };*/
/**
 * 判断分类轴上是否有未传值的动态元数据
 * @param tmpDynCategory
 * @returns {boolean}
 */
function hasDynamic(tmpDynCategory) {
    var isDynamic = true;
    $.each(tmpDynCategory.condition, function (j, condition) {
        var infoName = "";
        if (condition.metaType == METADATA_TYPE.DYNAMIC_TIME) {
            $.each(tmpDynCategory.condition, function (index, tmpCondition) {
                if (tmpCondition.dynamicType == METADATA_TYPE.DYNAMIC_TIME) {
                    isDynamic = false;
                    if (tmpCondition.dynamicType == tmpDynCategory.info.metaType) {
                        tmpDynCategory.info.name = tmpCondition.metaName;
                    }
                    return false;
                }
            });
            return !isDynamic;
        } else if (condition.metaType == METADATA_TYPE.DYNAMIC_ITEM) {
            $.each(tmpDynCategory.condition, function (index, tmpCondition) {
                if (tmpCondition.dynamicType == METADATA_TYPE.DYNAMIC_ITEM) {
                    isDynamic = false;
                    if (tmpCondition.dynamicType == tmpDynCategory.info.metaType) {
                        tmpDynCategory.info.name = tmpCondition.metaName;
                    }
                    return false;
                }
            });
            return !isDynamic;
        } else if (condition.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {
            $.each(tmpDynCategory.condition, function (index, tmpCondition) {
                if (tmpCondition.dynamicType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {
                    isDynamic = false;
                    if (tmpCondition.dynamicType == tmpDynCategory.info.metaType) {
                        tmpDynCategory.info.name = tmpCondition.metaName;
                    }
                    return false;
                }
            });
            return !isDynamic;
        } else if (condition.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {
            $.each(tmpDynCategory.condition, function (index, tmpCondition) {
                if (tmpCondition.dynamicType == METADATA_TYPE.DYNAMIC_SUROBJ) {
                    isDynamic = false;
                    if (tmpCondition.dynamicType == tmpDynCategory.info.metaType) {
                        tmpDynCategory.info.name = tmpCondition.metaName;
                    }
                    return false;
                }
            });
            return !isDynamic;
        } else if (condition.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {
            $.each(tmpDynCategory.condition, function (index, tmpCondition) {
                if (tmpCondition.dynamicType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {
                    isDynamic = false;
                    if (tmpCondition.dynamicType == tmpDynCategory.info.metaType) {
                        tmpDynCategory.info.name = tmpCondition.metaName;
                    }
                    return false;
                }
            });
            return !isDynamic;
        } else {
            isDynamic = false;
        }
    })
    return isDynamic;
}
/**
 * 转换动态分类轴
 * @param item              动态指标
 * @param isAdd             是否保留之前的动态元数据，false清空所有同类元数据 true 只清空同类元数据中名称相同的元数据
 * @param isClear           是否清空所有动态元数据
 * @returns {Array}
 */
EsiChart.prototype.getDynamicCategory = function (item, isAdd, isClear) {
    var _this = this;
    var addDynamic = [];
    $.each(_this.dynamicEsiCategoryList, function (i, tmpDynCategory) {
        var addCategory = {};
        var tempCondition = [];
        if (isClear) {
            tempCondition = []
        } else {
            tempCondition = []
            $.each(tmpDynCategory.condition, function (j, condition) {
                if (condition.dynamicType != item.dynamicType) {
                    if (!isAdd || condition.name != item.metaName) {
                        tempCondition.push(condition)
                    }
                }
            });
        }
        tmpDynCategory.condition = [];
        $.each(tempCondition, function (j, condition) {
            tmpDynCategory.condition.push(condition);
        });
        $.each(tmpDynCategory.condition, function (j, condition) {
            if (condition.metaType == item.dynamicType) {
                tmpDynCategory.condition.push(item);
                addCategory = $.extend(true, {}, tmpDynCategory);
                addCategory.isDynamic = 0;
                if (condition.leaf) {
                    addCategory.info.metaType = item.metaType;
                    addCategory.info.name = item.metaName;
                }
                if (tmpDynCategory.info.metaType == METADATA_TYPE.DYNAMIC_TIME) {
                    $.each(_this.dynCategory, function (ii, tmpDynCategory) {
                        var hasNotDynamicTime = true;
                        $.each(tmpDynCategory.condition, function (jj, condition) {
                            if (condition.metaType == METADATA_TYPE.DYNAMIC_TIME) {
                                hasNotDynamicTime = false;
                                _this.dynCategory.splice(ii, 1);
                                return false;
                            }
                        });
                        return hasNotDynamicTime;
                    });
                }
                return false;
            }
        });
        var isDynamic = hasDynamic(tmpDynCategory);
        if (!isDynamic && addCategory.info) {
            addDynamic.push(addCategory)
        }
    });
    //console.log(addDynamic)
    return addDynamic;
}
/**
 * 添加动态分类轴
 *
 * @param item     动态元数据
 * @param isAdd    是否保留之前的动态元数据
 */
/*EsiChart.prototype.addCategoryParam = function (item, isAdd, isAsync, fn) {
 var _isAsync = isAsync ? isAsync : false;
 var addDynamic = [];
 if (!isAdd) {
 this.dynCategory = [];
 }
 addDynamic = this.getDynamicCategory(item);
 if (addDynamic.length > 0) {
 var category =[];
 $.extend(category, addDynamic);
 var series = [];
 $.extend(series, this.esiSeriesList);
 this.esiDataHandler.load(series.concat(this.dynSeries), category.concat(this.dynCategory), true, _isAsync, fn);
 }
 };*/
EsiChart.prototype.getDynamicSeriesList = function (seriesParamList, isAdd) {
    var _this = this;
    var dynamicSeriesList = [];
    $.each(seriesParamList, function (i, seriesParam) {
        if (seriesParam) {
            var dynamicSeries = _this.getDynamicSeries(seriesParam, isAdd);
            if (dynamicSeries.length) {
                dynamicSeriesList = dynamicSeriesList.concat(dynamicSeries);
            }
        }
    });
    return dynamicSeriesList;
}
EsiChart.prototype.getDynamicCategoryList = function (categoryParamList, isAdd) {
    var _this = this;
    var dynamicCategoryList = [];
    $.each(categoryParamList, function (i, categoryParam) {
        if (categoryParam) {
            var dynamicCategory = _this.getDynamicCategory(categoryParam, isAdd);
            if (dynamicCategory.length) {
                dynamicCategoryList = dynamicCategoryList.concat(dynamicCategory);
            }
        }
    });
    return dynamicCategoryList;
}
/**
 * 初始化动态序列
 * @param seriesParamList
 * @param fn
 * @param isAdd
 */
EsiChart.prototype.initDynamicSeries = function (seriesParamList,time, fn, isAdd, isClear) {
    var _this = this;
    if (isClear) {// 清空之前的动态序列
        this.dynSeries = [];
    } else {//保留之前的同类不同名的序列和和不同类的序列
        var dynSeries = [];
        if (seriesParamList && this.dynSeries) {
            if (seriesParamList.dynamicType) {
                $.each(this.dynSeries, function (j, series) {
                    var isDelete = true;
                    $.each(series.condition, function (jj, condition) {
                        if (seriesParamList.dynamicType == condition.dynamicType) {
                            if (isAdd && seriesParamList.metaName != series.info.name) {
                                isDelete = false;
                                return false;
                            }
                        }
                    });
                    if (!isDelete) {
                        dynSeries.push(series);
                    }
                })
                this.dynSeries = dynSeries;
            } else if (seriesParamList.length) {
                $.each(seriesParamList, function (i, seriesParam) {
                    $.each(this.dynSeries, function (j, series) {
                        var isDelete = true;
                        $.each(series.condition, function (jj, condition) {
                            if (seriesParam.dynamicType == condition.dynamicType) {
                                if (isAdd && seriesParam.metaName != series.info.name) {
                                    isDelete = false;
                                    return false;
                                }
                            }
                        });
                        if (!isDelete) {
                            dynSeries.push(series);
                        }
                    })
                })
                this.dynSeries = dynSeries;
            }
        }

    }
    var dynamicSeriesList = [];
    if (seriesParamList && seriesParamList.length) {
        dynamicSeriesList = this.getDynamicSeriesList(seriesParamList, isAdd);
    } else if (seriesParamList && seriesParamList.dynamicType) {// 单个传入
        dynamicSeriesList = this.getDynamicSeriesList([seriesParamList], isAdd);
    }
    if (dynamicSeriesList && dynamicSeriesList.length) {
        var series = [];
        //$.extend(this.dynSeries, dynamicSeriesList);
        if (this.dynSeries && this.dynSeries.length) {
            this.dynSeries = this.dynSeries.concat(dynamicSeriesList)
        } else {
            $.extend(this.dynSeries, dynamicSeriesList);
        }
        var categories = [];
        $.extend(categories, this.esiCategoryList);
        this.esiDataHandler.load(dynamicSeriesList, categories.concat(this.dynCategory),time, true, true, fn, _this);
    } else {
        fn(null, _this);
    }
}
/**
 * 初始化动态分类轴
 * @param categoryParamList
 * @param fn
 * @param isAdd
 */
EsiChart.prototype.initDynamicCategory = function (categoryParamList,time, fn, isAdd, isClear) {
    var _this = this;
    if (isClear) {
        _this.dynCategory = [];
    } else {//isAdd==true保留之前的同类不同名的分类轴和不同类的分类轴,否则全部删除同类元数据
        var dynCategory = [];
        if (categoryParamList && this.dynCategory) {
            if (categoryParamList.dynamicType) {
                $.each(_this.dynCategory, function (j, category) {
                    var isDelete = true;
                    $.each(category.condition, function (jj, condition) {
                        if (isAdd && condition.dynamicType) {
                            isDelete = false;
                        }
                        if (categoryParamList.dynamicType == condition.dynamicType) {
                            if (categoryParamList.metaName == category.info.name) {
                                isDelete = true;
                                return false;
                            }
                        }
                    });
                    if (!isDelete) {
                        dynCategory.push(category);
                    }
                })
                _this.dynCategory = dynCategory;
            } else if (categoryParamList.length) {
                $.each(categoryParamList, function (i, categoryParam) {
                    $.each(_this.dynCategory, function (j, category) {
                        var isDelete = true;
                        $.each(category.condition, function (jj, condition) {
                            if (categoryParam.dynamicType == condition.dynamicType) {
                                if (isAdd && categoryParam.metaName != category.info.name) {
                                    isDelete = false;
                                    return false;
                                }
                            }
                        });
                        if (!isDelete) {
                            dynCategory.push(category);
                        }
                    })
                })
                this.dynCategory = dynCategory;
            }

        }

    }

    var dynamicCategoryList = [];
    if (categoryParamList && categoryParamList.length) {
        dynamicCategoryList = this.getDynamicCategoryList(categoryParamList, isAdd);
    } else if (categoryParamList && categoryParamList.dynamicType) {// 单个传入
        dynamicCategoryList = this.getDynamicCategoryList([categoryParamList], isAdd);
    }
    if (dynamicCategoryList && dynamicCategoryList.length) {
        //$.extend(this.dynCategory,dynamicCategoryList);
        if (this.dynCategory && this.dynCategory.length) {
            this.dynCategory = this.dynCategory.concat(dynamicCategoryList)
        } else {
            $.extend(this.dynCategory, dynamicCategoryList);
        }
        var series = [];
        $.extend(series, this.esiSeriesList);
        this.esiDataHandler.load(series.concat(this.dynSeries), dynamicCategoryList,time, true, true, fn, _this);
    } else {
        fn(null, _this);
    }
}
/**
 * 初始化动态元数据，下载相应数据
 * @param seriesParamList
 * @param categoryParamList
 * @param fn
 * @param isAdd
 */
EsiChart.prototype.initDynamicMetadata = function (seriesParamList, categoryParamList,time, fn, isAdd, isClear) {
    if(this.chartType) {
        var _this = this;
        if (categoryParamList && (categoryParamList.length || categoryParamList.dynamicType)) {
            this.initDynamicSeries(seriesParamList, time, function (o, esichart) {
                _this.initDynamicCategory(categoryParamList, time, fn, isAdd, isClear);
            }, isAdd);
        } else {
            this.initDynamicCategory(categoryParamList, time, function (o, esichart) {
                _this.initDynamicSeries(seriesParamList, time, fn, isAdd, isClear);
            }, isAdd);
        }
    }else{
        this.init(fn,time);
    }
}


/*EsiChart.prototype.show = function (item, isAdd) {
 var k = this.addParam(item);//获取接收参数后的序列
 this.esiDataHandler.load([k], this.esiCategoryList, true);//加载序列的数据
 var ss = this.handleStruct([k], this);//处理序列的时间问题
 var cc = this.handleStruct(this.esiCategoryList, this);//处理分类的时间问题
 var okSeries = this.esiChartOptionHandler.createSeries(ss, cc);
 okSeries.isD = true;
 //先移除掉所有动态的
 this.esiChartOptionHandler.series.push(okSeries);

 return this.esiChartOptionHandler;

 };*/
//初始化
EsiChart.prototype.initChartStructure = function () {
    this.esiSeriesIdMap = {};
    this.esiSeriesList = [];
    this.dynamicEsiSeriesIdMap = {};
    this.dynamicEsiSeriesList = [];
    this.esiCategoryIdMap = {};
    this.esiCategoryList = [];
    this.dynamicEsiCategoryIdMap = {};
    this.dynamicEsiCategoryList = [];
    var _this = this;
    var tmpEsiSeries = null;
    for (var i = 0; this.esiSeries && i < this.esiSeries.length; i++) {
        tmpEsiSeries = this.esiSeries[i];
        if (tmpEsiSeries.isDynamic == 0) {
            this.esiSeriesIdMap[tmpEsiSeries.info.id] = tmpEsiSeries;
            this.esiSeriesList.push(tmpEsiSeries);
        } else {
            this.dynamicEsiSeriesIdMap[tmpEsiSeries.info.id] = tmpEsiSeries;
            this.dynamicEsiSeriesList.push(tmpEsiSeries);
        }
    }
    $.each(this.esiCategory, function (index, tmpEsiCategory) {
        if (tmpEsiCategory.isDynamic == 0) {
            _this.esiCategoryIdMap[tmpEsiCategory.info.id] = tmpEsiCategory;
            _this.esiCategoryList.push(tmpEsiCategory);
        } else {
            _this.dynamicEsiCategoryIdMap[tmpEsiCategory.info.id] = tmpEsiCategory;
            _this.dynamicEsiCategoryList.push(tmpEsiCategory);
        }
    })

};
/*EsiChart.prototype.isMap = function(){
 return this.isMap;
 }
 EsiChart.prototype.setMapStatus = function(){
 this.isMap = true;
 }*/
//处理structure结构需要在加载完数据后使用，主要是处理时间问题structList可以是series或category
EsiChart.prototype.handleStruct = function (structList, esiChart) {
    var result = [];
    var _this = this;
    var tmpEsiStruct = null;
    var lianxu = {};//{}
    var isAdd = false;
    $.each(structList, function (index, tmpStruct) {
        if (!tmpStruct) {
            return true;
        }
        tmpStruct.unit = _this.esiDataHandler.dataMap[tmpStruct.info.id] && _this.esiDataHandler.dataMap[tmpStruct.info.id][0] ? _this.esiDataHandler.dataMap[tmpStruct.info.id][0].datas[0].unit : ""
        if (tmpStruct.info.chartType == ANALYSISCHART_INFO.CHART_MAP) {
            esiChart.isMap = true;
        }
        $.each(tmpStruct.condition, function (i, tmpCondition) {
            lianxu = {};//{}
            if (tmpCondition.metaType == METADATA_TYPE.TIME) {
                var metaExt = $.parseJSON(tmpCondition.metaExt);
                for (var j = 0; metaExt.timeRange && j < metaExt.timeRange.length; j++) {
                    switch (metaExt.timeRange[j].type) {
                        case METADATA_TYPE.XUANZE:
                            if (metaExt.timeRange[j].dataType == METADATA_TYPE.DATA_YEAR) {
                                for (var k = 0; k < metaExt.timeRange.length; k++) {
                                    if (metaExt.timeRange[k].dataType == METADATA_TYPE.DATA_PERIOD) {
                                        tmpEsiStruct = {};
                                        tmpEsiStruct.esiTime = {
                                            year: metaExt.timeRange[j].dataValue,
                                            period: metaExt.timeRange[k].dataValue,
                                        };
                                        //tmpEsiStruct.condition = tmpStruct.condition;
                                        //tmpEsiStruct.info = tmpStruct.info;
                                        $.extend(true, tmpEsiStruct, tmpStruct);
                                        if (tmpStruct.info.name == "时间") {
                                            tmpEsiStruct.info.name.replace("时间", tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period));
                                        } else if (metaExt.timeRange.length > 2 && !esiChart.isMap) {
                                            tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                        }else if(esiChart.isMap){
                                            tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                        }
                                        result.push(tmpEsiStruct);
                                        isAdd = true;
                                        //result.timeList.push(new EsiTimeRangePojo(metaExt.periodType, metaExt.timeRange[j].dataValue, metaExt.timeRange[k].dataValue, METADATA_TYPE.XUANZE))
                                    }
                                }
                            }
                            break;
                        case METADATA_TYPE.LIANXU:
                            switch (metaExt.timeRange[j].dataType) {
                                case METADATA_TYPE.DATA_BEGIN_YEAR:
                                    lianxu[METADATA_TYPE.DATA_BEGIN_YEAR] = metaExt.timeRange[j].dataValue;
                                    break;
                                case METADATA_TYPE.DATA_END_YEAR:
                                    lianxu[METADATA_TYPE.DATA_END_YEAR] = metaExt.timeRange[j].dataValue;
                                    break;
                                case METADATA_TYPE.DATA_BEGIN_PERIOD:
                                    lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD] = metaExt.timeRange[j].dataValue;
                                    break;
                                case METADATA_TYPE.DATA_END_PERIOD:
                                    lianxu[METADATA_TYPE.DATA_END_PERIOD] = metaExt.timeRange[j].dataValue;
                                    break;
                            }
                            if (lianxu[METADATA_TYPE.DATA_BEGIN_YEAR] && lianxu[METADATA_TYPE.DATA_END_YEAR] && lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD] && lianxu[METADATA_TYPE.DATA_END_PERIOD]) {
                                var span = 1;
                                switch (metaExt.periodType) {
                                    case PERIOD_TYPE.MONTH:
                                        span = 1;
                                        break;
                                    case PERIOD_TYPE.HALF:
                                        span = 6;
                                        break;
                                    case PERIOD_TYPE.QUARTER:
                                        span = 3;
                                        break;
                                    case PERIOD_TYPE.YEAR:
                                        span = 12;
                                        break;
                                }
                                for (var tmpYear = lianxu[METADATA_TYPE.DATA_BEGIN_YEAR]; tmpYear <= lianxu[METADATA_TYPE.DATA_END_YEAR]; tmpYear++) {
                                    if (lianxu[METADATA_TYPE.DATA_BEGIN_YEAR] == lianxu[METADATA_TYPE.DATA_END_YEAR]) {
                                        for (var tmpM = lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD]; tmpM <= lianxu[METADATA_TYPE.DATA_END_PERIOD];) {
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: tmpYear,
                                                period: tmpM,
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间") {
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            } else if (lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD] != lianxu[METADATA_TYPE.DATA_END_PERIOD] && !esiChart.isMap) {
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }else if(esiChart.isMap){
                                                tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }
                                            result.push(tmpEsiStruct);
                                            isAdd = true;
                                            tmpM += span;
                                        }


                                    } else if (tmpYear == lianxu[METADATA_TYPE.DATA_BEGIN_YEAR]) {
                                        for (var tmpM = lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD]; tmpM <= 12;) {
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: tmpYear,
                                                period: tmpM,
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间") {
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            } else if (!esiChart.isMap) {
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }else if(esiChart.isMap){
                                                tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }
                                            result.push(tmpEsiStruct);
                                            isAdd = true;
                                            tmpM += span;
                                        }


                                    } else if (tmpYear == lianxu[METADATA_TYPE.DATA_END_YEAR]) {
                                        for (var tmpM = 0; tmpM < lianxu[METADATA_TYPE.DATA_END_PERIOD];) {
                                            tmpM += span;
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: tmpYear,
                                                period: tmpM,
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间") {
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            } else if (!esiChart.isMap) {
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }else if(esiChart.isMap){
                                                tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }
                                            result.push(tmpEsiStruct);
                                            isAdd = true;
                                        }

                                    } else {
                                        for (var tmpM = 0; tmpM < 12;) {
                                            tmpM += span;
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: tmpYear,
                                                period: tmpM,
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间") {
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            } else if (!esiChart.isMap) {
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }else if(esiChart.isMap){
                                                tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }
                                            result.push(tmpEsiStruct);
                                            isAdd = true;
                                        }
                                    }
                                }
                            }
                            break;
                        case METADATA_TYPE.BAOGAOQI:
                            if (_this.esiDataHandler && _this.esiDataHandler.dataMap) {
                                if (_this.esiDataHandler.dataMap[tmpStruct.info.id]) {
                                    for (l = 0; l < metaExt.timeRange[j].dataValue && l < _this.esiDataHandler.dataMap[tmpStruct.info.id].length; l++) {
                                        tmpEsiStruct = {};
                                        tmpEsiStruct.esiTime = {
                                            year: _this.esiDataHandler.dataMap[tmpStruct.info.id][l].time.year,
                                            period: _this.esiDataHandler.dataMap[tmpStruct.info.id][l].time.period,
                                        };
                                        $.extend(true, tmpEsiStruct, tmpStruct);
                                        if (tmpStruct.info.name == "时间") {
                                            tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                        } else if (metaExt.timeRange[j].dataValue > 1 && !esiChart.isMap) {
                                            tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                        }else if(esiChart.isMap){
                                            tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                        }
                                        result.push(tmpEsiStruct);
                                        isAdd = true;
                                    }
                                } else {
                                    $.each(_this.esiDataHandler.dataMap, function (a) {
                                        for (l = 0; l < metaExt.timeRange[j].dataValue && l < _this.esiDataHandler.dataMap[a].length; l++) {
                                            tmpEsiStruct = {};

                                            tmpEsiStruct.esiTime = {
                                                year: _this.esiDataHandler.dataMap[a][l].time.year,
                                                period: _this.esiDataHandler.dataMap[a][l].time.period,
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间") {
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            } else if (metaExt.timeRange[j].dataValue > 1 && !esiChart.isMap) {
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }else if(esiChart.isMap){
                                                tmpEsiStruct.esiTime.time = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            }

                                            result.push(tmpEsiStruct);
                                            isAdd = true;
                                        }
                                        return false;
                                    });

                                }
                            }
                            break;
                    }
                }
            }
        });
        if (!isAdd) {
            result.push(tmpStruct);
        }
        isAdd = false;
    });
    return result;
};
// ---------------------------配置地图-----------------------------------------------
EsiChart.prototype.configMap = function(mapPath) {
    echarts.util.mapData.params.params.jimo = {
        getGeoJson : function(callback) {
            $.ajax({
                async : false,
                url : GLOBAL_PATH+"/City/resourceCategory/analysis/chart/svg/"+mapPath,
                dataType : 'xml',
                success : function(xml) {
                    callback(xml);
                }
            });
        }
    };
}