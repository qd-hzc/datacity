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
    this.title = {
        text: esiChart.title ? esiChart.title + "" : '',
        subtext: esiChart.subTitle ? esiChart.subTitle : '',
        //x: (esiChart.chartStyle && esiChart.chartStyle.dataZoom) ? esiChart.chartStyle.title : 'center'
    };
    this.yAxis = [{
        title: {}
    }, {
        title: {},
        opposite: true
    }];
    this.subtitle = {
        text: esiChart.subTitle ? esiChart.subTitle : '',
        x: (esiChart.chartStyle && esiChart.chartStyle.dataZoom) ? esiChart.chartStyle.title : 'center'
    };
    this.xAxis = [];
    this.series = [];
    this.plotOptions = {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true
            },
            showInLegend: true
        }
    };
    this.plug = esiChart.plug;
    this.esiDataHandler = esiChart.esiDataHandler;

}
//创建分类轴
EsiHighchartsOption.prototype.createCategory = function (categoryList, seriesList) {
    var result = [];
    var type;
    var tmpAxis;
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
        if (tmpSeries.info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
            for (var j = 0; seriesDatas && j < seriesDatas.length; j++) {
                if (seriesDatas[j].name == tmpSeries.info.group) {
                    seriesDatas[j].datas.push(tmpSeries);
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
                type: ANALYSISCHART_INFO.getEN(tmpSeries.info.chartType, this.plug),
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
            stack:seriesDatas[i].stack,
            yAxis: seriesDatas[i].axis,
            data: this.pGetData(categoryList, seriesDatas[i].datas, seriesDatas[i].type)
        };
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
                if (this.xAxis.length < 1) {
                    this.xAxis.push({});
                }
            } else {
                tmpValue = {};
                tmpValue.name = tmpCategory.info.name;
                tmpValue.y = this.esiDataHandler.getData(tmpSeries, tmpCategory);
            }
        }
        result.push(tmpValue);
    }
    return result;
};
//数据操作类
function EsiDataHandler() {

    this.data = [];
    this.dataMap = {};
}
//根据序列和分类获取数据
EsiDataHandler.prototype.getData = function (series, category) {
    var tmpData = this.dataMap[series.info.id] ? this.dataMap[series.info.id] : this.data;
    var result = null;
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
                                    break;
                                case METADATA_TYPE.TIME_FRAME:
                                    if (condition.metaId != data.reportDataId.timeFrame) {
                                        yesItIs = false;
                                    }
                                    break;
                                case METADATA_TYPE.RESEARCH_OBJ:
                                    if (condition.metaId != data.reportDataId.surobj || metaExt.objType != data.surobjType) {
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
//根据序列加载数据
EsiDataHandler.prototype.load = function (seriesList, categoryList, isAdd) {
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
        kkk=[];
        for (var j = 0; categoryList && j < categoryList.length; j++) {
            aaa = conditions.concat(categoryList[j].condition);
            handleResult = _this.handleConditions(aaa);
            kkk=kkk.concat(handleResult.conditionList);

        }
        esiConditionLists.push(kkk);
        esiTimeRangePojoLists.push(handleResult.timeList);

    }
    $.ajax({
        type: 'post',
        async: false,
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
            for (var i = 0; seriesList && i < seriesList.length; i++) {
                if (!_this.dataMap[seriesList[i].info.id]) {
                    _this.dataMap[seriesList[i].info.id] = (data[i] ? data[i] : []);
                } else {
                    _this.dataMap[seriesList[i].info.id] = _this.dataMap[seriesList[i].info.id].concat((data[i] ? data[i] : []));
                }
                if (data[i])
                    _this.loadData(data[i], true);
            }
        }
    });

};
//处理请求数据的条件
EsiDataHandler.prototype.handleConditions = function (conditions) {
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
        switch (tmpCondition.metaType) {
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
                            result.timeList.push(new EsiTimeRangePojo(metaExt.periodType, null, null, METADATA_TYPE.BAOGAOQI, metaExt.timeRange[j].dataValue));
                            break;
                    }

                }
        }
    }
    itemMenu.sort();
    result.conditionList.push(new EsiCondition(METADATA_TYPE.ITEM_MENU, itemMenu.join(',')));
    return result;
};

function EsiChart(config) {
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
        this.loadedData = false;
        this.esiDataHandler = new EsiDataHandler(this);
        this.esiChartOptionHandler = new EsiHighchartsOption(this);
        this.initChartStructure();
        this.esiDataHandler.load(this.esiSeriesList, this.esiCategoryList);
        this.dynamicMetadata = null;
    }
}

// 根据插件配置空图表
EsiChart.prototype.createOption = function (plug) {
    var _plug = null;
    if (plug) {
        _plug = plug;
    } else {
        _plug = this.plug;
    }
    switch (_plug) {
        case 'highcharts':
            if (!this.esiChartOptionHandler)
                this.esiChartOptionHandler = new EsiHighchartsOption(this);
            var cc = this.handleStruct(this.esiCategoryList);
            var ss = this.handleStruct(this.esiSeriesList);
            var c = this.esiChartOptionHandler.createCategory(cc, ss);
            this.esiChartOptionHandler.xAxis = c;
            var d = this.esiChartOptionHandler.createSeries(ss, cc);
            this.esiChartOptionHandler.series = d;
            break;
        default://默认echarts
            return this.createEchartsOption();
            /*if (!this.esiChartOptionHandler)
                this.esiChartOptionHandler = new EsiHighchartsOption(this);
            var cc = this.handleStruct(this.esiCategoryList);
            var ss = this.handleStruct(this.esiSeriesList);
            var c = this.esiChartOptionHandler.createCategory(cc, ss);
            this.esiChartOptionHandler.xAxis = c;
            var d = this.esiChartOptionHandler.createSeries(ss, cc);
            this.esiChartOptionHandler.series = d;*/

    }
    return this.esiChartOptionHandler;
    //..
};
EsiChart.prototype.addParam = function (item) {
    var dynSeries = this.dyList[i];//原始的动态序列
    var tmpSeries = {};//根据原始动态序列接收条件生成的新的序列；
    $.extend(tmpSeries,dynSeries);
    tmpSeries.condition.push(item);
    return tmpSeries;
};
EsiChart.prototype.show = function (item,isAdd) {
    var k = this.addParam(item);//获取接收参数后的序列
    this.esiDataHandler.load([k], this.esiCategoryList,true);//加载序列的数据
    var ss = this.handleStruct([k]);//处理序列的时间问题
    var cc = this.handleStruct(this.esiCategoryList);//处理分类的时间问题
    var okSeries = this.esiChartOptionHandler.createSeries(ss,cc);
    okSeries.isD = true;
    //先移除掉所有动态的
    this.esiChartOptionHandler.series.push(okSeries);

    return this.esiChartOptionHandler;

}
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
//处理structure结构需要在加载完数据后使用，主要是处理时间问题structList可以是series或category
EsiChart.prototype.handleStruct = function (structList) {
    var result = [];
    var _this = this;
    var tmpEsiStruct = null;
    var lianxu = {};
    var isAdd = false;
    $.each(structList, function (index, tmpStruct) {
        $.each(tmpStruct.condition, function (i, tmpCondition) {
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
                                            period: metaExt.timeRange[k].dataValue
                                        };
                                        //tmpEsiStruct.condition = tmpStruct.condition;
                                        //tmpEsiStruct.info = tmpStruct.info;
                                        $.extend(true, tmpEsiStruct, tmpStruct);
                                        if (tmpStruct.info.name == "时间")
                                            tmpEsiStruct.info.name.replace("时间", tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period));
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
                                    if (tmpYear == lianxu[METADATA_TYPE.DATA_BEGIN_YEAR]) {
                                        for (var tmpM = lianxu[METADATA_TYPE.DATA_BEGIN_PERIOD]; tmpM <= 12;) {
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: tmpYear,
                                                period: tmpM
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间")
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            else
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            console.log(tmpEsiStruct.info.name)
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
                                                period: tmpM
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间")
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            else
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            console.log(tmpEsiStruct.info.name)
                                            result.push(tmpEsiStruct);
                                            isAdd = true;
                                        }

                                    } else {
                                        for (var tmpM = 0; tmpM < 12;) {
                                            tmpM += span;
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: tmpYear,
                                                period: tmpM
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间")
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            else
                                                tmpEsiStruct.info.name += tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                            console.log(tmpEsiStruct.info.name)
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
                                            period: _this.esiDataHandler.dataMap[tmpStruct.info.id][l].time.period
                                        };
                                        $.extend(true, tmpEsiStruct, tmpStruct);
                                        if (tmpStruct.info.name == "时间")
                                            tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
                                        result.push(tmpEsiStruct);
                                        isAdd = true;
                                    }
                                } else {
                                    $.each(_this.esiDataHandler.dataMap, function (a) {
                                        for (l = 0; l < metaExt.timeRange[j].dataValue && l < _this.esiDataHandler.dataMap[a].length; l++) {
                                            tmpEsiStruct = {};
                                            tmpEsiStruct.esiTime = {
                                                year: _this.esiDataHandler.dataMap[a][l].time.year,
                                                period: _this.esiDataHandler.dataMap[a][l].time.period
                                            };
                                            $.extend(true, tmpEsiStruct, tmpStruct);
                                            if (tmpStruct.info.name == "时间")
                                                tmpEsiStruct.info.name = tmpEsiStruct.esiTime.year + "年" + FREQUENCY_TYPE.getString(metaExt.periodType, tmpEsiStruct.esiTime.period);
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
    console.log(result)
    return result;
};