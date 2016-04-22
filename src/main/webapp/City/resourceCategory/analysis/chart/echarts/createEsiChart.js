/**
 * Created by wgx on 2016/3/7.
 */
function EsiChart(config) {
    if (config) {
        this.chartId = config.chart.id
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
            this.createHighChartsOption();
            break;
        default://默认echarts
            return this.createEchartsOption();

    }
    //..
}
// 根据插件与传入数据添加数据
/**
 *
 * @param dynamicMetadata          动态元数据
 * @param isSaave                  是否将动态元数据保留
 * @param datas
 */
EsiChart.prototype.addDynamicOption = function (dynamicMetadata, isSaave, datas) {
    var _plug = this.plug;
    var _dynamicMetadata = this.copyDynamicMetadata(dynamicMetadata);
    if (this.dynamicMetadata) {
        _dynamicMetadata = addDynamicMetadata(_dynamicMetadata, this.dynamicMetadata);
    }
    if (isSaave) {
        this.dynamicMetadata = addDynamicMetadata(this.dynamicMetadata, dynamicMetadata);
    }
    switch (_plug) {
        case 'highcharts':
            return this.updateEchartsOption(_dynamicMetadata, datas);
            //this.updateHighChartsOption(_dynamicMetadata, datas);
            break;
        default://默认echarts
            return this.updateEchartsOption(_dynamicMetadata, datas);

    }
    //..
}
EsiChart.prototype.updateEchartsOption = function (dynamicMetadata, datas) {
    var option = this.option;
    //加载动态指标数据
    this.loadedData = false;
    this.loadData(datas, dynamicMetadata, true)
    if (this.loadedData) {
        //初始序列
        var series = option.series;
        //console.log(series);
        //添加序列
        var dynamicSeries = this.createSeries(null, dynamicMetadata, false);
        //console.log(dynamicSeries);
        option.series = dynamicSeries;
        option.legend = this.createLegend(dynamicMetadata);
        if (this.esiSeries[0].info.chartType != ANALYSISCHART_INFO.CHART_PIE && this.esiSeries[0].info.chartType != ANALYSISCHART_INFO.CHART_MAP&& this.esiSeries[0].info.chartType != ANALYSISCHART_INFO.CHART_SCATTER) {//判断是否为饼图或地图
            //创建分类轴
            option.xAxis = this.createCategory(dynamicMetadata);
        }
        //console.log(dynamicSeries);
        //console.log(series)
        //alert(12)
        //for (var i = 0; i < dynamicSeries.datas.length; i++) {
        //   series.datas.push(dynamicSeries.datas[i]);
        // }
    }
    return option;

};
EsiChart.prototype.createEchartsOption = function () {
    var result = null;
    var title = {
        text: this.title ? this.title + "" : '',
        subtext: this.subTitle ? this.subTitle : '',
        x: (this.chartStyle && this.chartStyle.dataZoom) ? this.chartStyle.title : 'center',

    };
    var tooltip = (this.chartStyle && this.chartStyle.tooltip) ? this.chartStyle.tooltip : {
        trigger: 'axis'
    };
    var toolbox = (this.chartStyle && this.chartStyle.toolbox) ? this.charStyle.toolbox : {
        show: true,
        feature: {
            dataView: {show: true, readOnly: false},
            restore: {show: true},
            saveAsImage: {show: true}
        }
    };
    var dataZoom = (this.chartStyle && this.chartStyle.dataZoom) ? this.charStyle.dataZoom : {
        show: true,
        start: 0,
        end: 100
    };
    var yAxis = [];
    var xAxis = [];
    var series = [];
    var legend = [];
    if (this.chartType == 0) {
        if (this.loadedData) {
            legend = this.createLegend();

            //创建序列
            series = this.createSeries();
            if (this.esiSeries[0].info.chartType != ANALYSISCHART_INFO.CHART_PIE && this.esiSeries[0].info.chartType != ANALYSISCHART_INFO.CHART_MAP && this.esiSeries[0].info.chartType != ANALYSISCHART_INFO.CHART_SCATTER) {//判断是否为饼图
                //创建分类轴
                xAxis = this.createCategory();
                // 创建y轴（需用到数据，在创建序列时记录）
                yAxis = this.createYAxis();
            } else if (this.esiSeries[0].info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
                var _result = this.createScatterAxis();
                // 创建x轴（需用到数据，在创建序列时记录）
                xAxis = _result.xAxis;
                // 创建y轴（需用到数据，在创建序列时记录）
                yAxis = _result.yAxis;
                tooltip.formatter = function (params) {
                    if (params.value.length > 1) {
                        return params.seriesName + ' :<br/>'
                            + _result.nameX +' : '+params.value[0] + "<br/>"
                            + _result.nameY +' : '+params.value[1];
                    }
                    else {
                        return params.seriesName + ' :<br/>'
                            + params.name + ' : '
                            + params.value + yAxis[0].name;
                    }
                }

            } else {
                xAxis = null;
                yAxis = null;
            }

        }
    }
    //TODO ..
    result = {
        title: title,
        tooltip: tooltip,
        toolbox: toolbox,
        legend: legend,
        //yAxis: yAxis,
        //xAxis: xAxis,
        series: series
    }
    if (xAxis) {
        result.xAxis = xAxis;
    }
    if (yAxis) {
        result.yAxis = yAxis;
    }
    this.option = result;
    return result;

}
/**
 *
 * @param category                    分类轴/序列
 * @param dynamicMetadata             动态指标
 * @returns {{dynamicDatas: *, dynamicType: (number|*|dataType)}}
 */
function getInfoDynamic(category, dynamicMetadata) {
    var dynamicDatas;
    var isDynamicInfo = false;
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
        if (dynamicMetadata.dynamicItem) {
            dynamicDatas = dynamicMetadata.dynamicItem;
            isDynamicInfo = true;
        }
    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
        if (dynamicMetadata.dynamicItemGroup) {
            dynamicDatas = dynamicMetadata.dynamicItemGroup;
            isDynamicInfo = true;
        }
    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
        if (dynamicMetadata.dynamicSurobj) {
            dynamicDatas = dynamicMetadata.dynamicSurobj;
            isDynamicInfo = true;
        }
    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
        if (dynamicMetadata.dynamicTimeframe) {
            dynamicDatas = dynamicMetadata.dynamicTimeframe;
            isDynamicInfo = true;
        }
    }
    return {
        dynamicDatas: dynamicDatas,
        isDynamicInfo: isDynamicInfo
    };
}
/**
 *
 * @param category                    分类轴/序列
 * @param dynamicMetadata             动态指标
 * @returns {{dynamicDatas: *, dynamicType: (number|*|dataType)}}
 */
function getDynamicData(category, dynamicMetadata) {
    var dynamicDatas;
    if (category.metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
        if (dynamicMetadata.dynamicItem) {
            dynamicDatas = dynamicMetadata.dynamicItem;
        }
    }
    if (category.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
        if (dynamicMetadata.dynamicItemGroup) {
            dynamicDatas = dynamicMetadata.dynamicItemGroup;
        }
    }
    if (category.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
        if (dynamicMetadata.dynamicSurobj) {
            dynamicDatas = dynamicMetadata.dynamicSurobj;
        }
    }
    if (category.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
        if (dynamicMetadata.dynamicTimeframe) {
            dynamicDatas = dynamicMetadata.dynamicTimeframe;
        }
    }
    return dynamicDatas;
}
/**
 /**
 * 根据分类轴的条件获取数据集
 * @param category                                       分类轴
 * @param cateReportDataId                               联合主键
 * @param periodType                                     报送频率
 * @param cateDataSets                                   数据集
 * @param isDynamicOnly                                  是否只加载动态数据
 * @param dynamicMetadata                                动态数据对象
 * {
 *     dynamicItem：metadata       动态指标
 *     dynamicItemGroup：metadata  动态分组
 *     dynamicSurobj：metadata     动态统计对象
 *     dynamicTimeframe：metadata  动态时间框架
 *  }
 *  metadata：元数据
 *     [{
 *         dataValue：   元数据id，
 *         dataName：    元数据名称
 *         dataType      元数据类型
 *         dataInfo1：   若是统计对象类型,则dataInfo1表示统计对象类型
 *                       若是指标类型,则dataInfo1表示指标口径
 *         dataInfo2：   若是统计对象类型,则dataInfo2表示统计地区
 *                       若是指标类型,则dataInfo2表示部门
 *         dataInfo3：   若是指标类型,则dataInfo3表示所属报表
 *     },...]
 *
 * @returns {{periodType: *, cateStructureId: *}}        返回报送频率和时间类型id
 */
function getCategoryConditions(category, cateReportDataId, periodType, cateDataSets, isDynamicOnly, dynamicMetadata) {
    var cateStructureId;// 分类轴时间id
    var isDynamic = dynamicMetadata ? true : false;
    if (category.info.metaType == METADATA_TYPE.TIME) {
        cateReportDataId.isTime = true;
    } else {
        cateReportDataId.itemName = category.info.name;
    }
    var cateConditions = category.condition;
    var itemMenu = [];
    for (var j = 0; j < cateConditions.length; j++) {

        var obj = {
            dataType: cateConditions[j].metaType,
            dataValue: cateConditions[j].metaId
        };
        if (cateConditions[j].metaType == METADATA_TYPE.ITEM) {
            cateReportDataId.itemId = cateConditions[j].metaId;//指标
            var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
            if (metxExtObj.caliber) {
                obj.dataInfo1 = metxExtObj.caliber;
                cateReportDataId.itemCaliber = metxExtObj.caliber;//口径
            }
            obj.dataInfo2 = metxExtObj.dep;
            cateReportDataId.depId = metxExtObj.dep;// 部门
            if (metxExtObj.rptTmp) {
                obj.dataInfo3 = metxExtObj.rptTmp;
                cateReportDataId.rptTmp = metxExtObj.rptTmp;//模板
            }

        }
        if (cateConditions[j].metaType == METADATA_TYPE.ITEM_MENU) {
            //cateReportDataId.itemDict = cateConditions[j].metaId;//指标分组目录
            itemMenu.push(cateConditions[j].metaId)
        }
        if (cateConditions[j].metaType == METADATA_TYPE.TIME_FRAME) {
            cateReportDataId.timeFrame = cateConditions[j].metaId;//时间框架
        }
        if (cateConditions[j].metaType == METADATA_TYPE.RESEARCH_OBJ) {
            var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
            cateReportDataId.surobj = cateConditions[j].metaId;//调查对象
            cateReportDataId.surobjType = metxExtObj.objType;//调查对象类型
            obj.dataInfo1 = cateReportDataId.surobjType;
            if (metxExtObj.objType == 1) {
                cateReportDataId.areaId = cateConditions[j].metaId;//地区类型调查对象
                obj.dataInfo2 = cateReportDataId.areaId;
            } else {
                if (metxExtObj.areaId) {
                    cateReportDataId.areaId = metxExtObj.areaId;
                    obj.dataInfo2 = cateReportDataId.areaId;
                }
            }
        }
        // 有时间的话需要传报送频率
        if (cateConditions[j].metaType == METADATA_TYPE.TIME) {
            var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
            periodType = metxExtObj.periodType;
            cateStructureId = cateConditions[j].id;
            /**
             * 序列的时间只可能是一个报告期，需要记录下此时的年份和月份
             */
            var timeRange = metxExtObj.timeRange;
            if (!METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).isNewPeriod && !cateReportDataId.isTime) {
                cateReportDataId.year = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).year;
                cateReportDataId.month = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).month;
            }
        } else {
            if (!category.isDynamic == 1 && !(isDynamicOnly && isDynamic) && cateConditions[j].metaType != METADATA_TYPE.ITEM_MENU) {//保存静态数据集(在动态元数据存在且只加载动态数据时不加载静态数据)
                cateDataSets.push(obj)
                //console.log(obj);
            }
            if (category.isDynamic == 1 && isDynamic) {//保存动态数据集
                //动态元数据处理
                if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
                    if (dynamicMetadata.dynamicItem) {
                        var dynamicItem = dynamicMetadata.dynamicItem;
                        for (var k = 0; k < dynamicItem.length; k++) {
                            obj.dataType = dynamicItem[k].dataType;
                            obj.dataValue = dynamicItem[k].dataValue;//指标id
                            cateReportDataId.itemId = dynamicItem[k].dataValue;//指标id
                            obj.dataInfo1 = dynamicItem[k].dataInfo1;
                            cateReportDataId.itemCaliber = dynamicItem[k].dataInfo1;//口径
                            obj.dataInfo2 = dynamicItem[k].dataInfo2;
                            cateReportDataId.depId = dynamicItem[k].dataInfo2;// 部门
                            if (dynamicItem[k].dataInfo3) {
                                obj.dataInfo3 = dynamicItem[k].dataInfo3;
                                cateReportDataId.rptTmp = dynamicItem[k].dataInfo3;//模板
                            }
                            cateDataSets.push(obj);
                        }
                    }

                }
                if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
                    if (dynamicMetadata.dynamicItemGroup) {
                        var dynamicItemGroup = dynamicMetadata.dynamicItemGroup;
                        for (var k = 0; k < dynamicItemGroup.length; k++) {
                            obj.dataType = dynamicItemGroup[k].dataType;
                            obj.dataValue = dynamicItemGroup[k].dataValue;//指标id
                            cateReportDataId.itemDict = dynamicItemGroup[k].dataValue;//指标分组目录
                            //cateDataSets.push(obj);
                            itemMenu.push(dynamicItemGroup[k].dataValue);
                        }
                    }

                }
                if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
                    if (dynamicMetadata.dynamicSurobj) {
                        var dynamicSurobj = dynamicMetadata.dynamicSurobj;
                        for (var k = 0; k < dynamicSurobj.length; k++) {
                            obj.dataType = dynamicSurobj[k].dataType;
                            obj.dataValue = dynamicSurobj[k].dataValue;
                            cateReportDataId.surobj = obj.dataValue;//调查对象
                            obj.dataInfo1 = dynamicSurobj[k].dataInfo1;
                            cateReportDataId.surobjType = obj.dataInfo1;//调查对象类型
                            obj.dataInfo2 = dynamicSurobj[k].dataInfo2;
                            cateReportDataId.areaId = dynamicSurobj[k].dataInfo2;
                            cateDataSets.push(obj);
                            //console.log(obj);
                        }
                    }
                }
                if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
                    if (dynamicMetadata.dynamicTimeframe) {
                        var dynamicTimeframe = dynamicMetadata.dynamicTimeframe;
                        for (var k = 0; k < dynamicTimeframe.length; k++) {
                            obj.dataType = dynamicTimeframe[k].dataType;
                            obj.dataValue = dynamicTimeframe[k].dataValue;
                            cateReportDataId.timeFrame = dynamicTimeframe[k].dataValue;//时间框架
                            cateDataSets.push(obj);

                        }

                    }

                }
            }
        }
    }
    if (itemMenu.length) {
        itemMenu.sort();
        var obj = {
            dataType: METADATA_TYPE.ITEM_MENU,
            dataValue: itemMenu.join(",")
        }
        cateDataSets.push(obj);
    }
    return {
        periodType: periodType,
        cateStructureId: cateStructureId,
    };
}
/**
 * 根据序列条件获取数据集
 * @param seriesConditions                               序列条件
 * @param reportDataId                                   联合主键
 * @param periodType                                     报送频率
 * @param structureId                                    序列时间id
 * @param dataSets                                       数据集
 * @param isDynamicOnly                                  是否只加载动态数据
 * @param dynamicMetadata                                动态数据对象
 *  * {
 *     dynamicItem：metadata       动态指标
 *     dynamicItemGroup：metadata  动态分组
 *     dynamicSurobj：metadata     动态统计对象
 *     dynamicTimeframe：metadata  动态时间框架
 *  }
 *  metadata：元数据
 *     [{
 *         dataValue：   元数据id，
 *         dataName：    元数据名称
 *         dataType      元数据类型
 *         dataInfo1：   若是统计对象类型,则dataInfo1表示统计对象类型
 *                       若是指标类型,则dataInfo1表示指标口径
 *         dataInfo2：   若是统计对象类型,则dataInfo2表示统计地区
 *                       若是指标类型,则dataInfo2表示部门
 *         dataInfo3：   若是指标类型,则dataInfo3表示所属报表
 *     },...]
 * @returns {{periodType: *, structureId: *}}
 */
function getSeriesConditions(series, reportDataId, periodType, structureId, dataSets, isDynamicOnly, dynamicMetadata) {
    var seriesConditions = series.condition;
    var isDynamic = dynamicMetadata ? true : false;
    var itemMenu = [];
    for (var j = 0; j < seriesConditions.length; j++) {
        var obj = {
            dataType: seriesConditions[j].metaType,
            dataValue: seriesConditions[j].metaId
        }
        if (seriesConditions[j].metaType == METADATA_TYPE.ITEM) {
            reportDataId.itemId = seriesConditions[j].metaId;//指标
            var metxExtObj = $.parseJSON(seriesConditions[j].metaExt);
            if (metxExtObj.caliber) {
                obj.dataInfo1 = metxExtObj.caliber;
                reportDataId.itemCaliber = metxExtObj.caliber;//口径
            }
            obj.dataInfo2 = metxExtObj.dep;
            reportDataId.depId = metxExtObj.dep;// 部门
            if (metxExtObj.rptTmp) {
                obj.dataInfo3 = metxExtObj.rptTmp;
                reportDataId.rptTmp = metxExtObj.rptTmp;//模板
            }

        }
        if (seriesConditions[j].metaType == METADATA_TYPE.ITEM_MENU) {
            //reportDataId.itemDict = seriesConditions[j].metaId;//指标分组目录
            itemMenu.push(seriesConditions[j].metaId);
        }
        if (seriesConditions[j].metaType == METADATA_TYPE.TIME_FRAME) {
            reportDataId.timeFrame = seriesConditions[j].metaId;//时间框架
        }
        if (seriesConditions[j].metaType == METADATA_TYPE.RESEARCH_OBJ) {
            var metxExtObj = $.parseJSON(seriesConditions[j].metaExt);
            reportDataId.surobj = seriesConditions[j].metaId;//调查对象
            reportDataId.surobjType = metxExtObj.objType;//调查对象类型
            obj.dataInfo1 = reportDataId.surobjType;
            if (metxExtObj.objType == 1) {
                reportDataId.areaId = seriesConditions[j].metaId;//地区类型调查对象
                obj.dataInfo2 = reportDataId.areaId;
            } else {
                if (metxExtObj.areaId) {
                    reportDataId.areaId = metxExtObj.areaId;
                    obj.dataInfo2 = reportDataId.areaId;
                }
            }
        }
        // 有时间的话需要传报送频率
        if (seriesConditions[j].metaType == METADATA_TYPE.TIME) {
            var metxExtObj = $.parseJSON(seriesConditions[j].metaExt);
            periodType = metxExtObj.periodType;
            structureId = seriesConditions[j].id;
            /**
             * 序列的时间只可能是一个报告期，需要记录下此时的年份和月份
             */
            var timeRange = metxExtObj.timeRange;
            if (!METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).isNewPeriod) {
                reportDataId.year = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).year;
                reportDataId.month = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).month;
            }
        } else {
            if (!series.isDynamic == 1 && !(isDynamicOnly && isDynamic) && seriesConditions[j].metaType != METADATA_TYPE.ITEM_MENU) {//保存静态数据集(在动态元数据存在且只加载动态数据时不加载静态数据)
                dataSets.push(obj);
            }
            if (series.isDynamic == 1 && isDynamic) {//保存动态数据集
                //动态元数据处理
                var isPush = false;
                if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
                    if (dynamicMetadata.dynamicItem) {
                        var dynamicItem = dynamicMetadata.dynamicItem;
                        for (var k = 0; k < dynamicItem.length; k++) {
                            reportDataId.itemName = dynamicItem[k].dataName;
                            obj.dataType = dynamicItem[k].dataType;
                            obj.dataValue = dynamicItem[k].dataValue;//指标id
                            reportDataId.itemId = dynamicItem[k].dataValue;//指标id
                            obj.dataInfo1 = dynamicItem[k].dataInfo1;
                            reportDataId.itemCaliber = dynamicItem[k].dataInfo1;//口径
                            obj.dataInfo2 = dynamicItem[k].dataInfo2;
                            reportDataId.depId = dynamicItem[k].dataInfo2;// 部门
                            if (dynamicItem[k].dataInfo3) {
                                obj.dataInfo3 = dynamicItem[k].dataInfo3;
                                reportDataId.rptTmp = dynamicItem[k].dataInfo3;//模板
                            }
                            dataSets.push(obj);
                            isPush = true;
                        }
                    }

                }
                if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
                    if (dynamicMetadata.dynamicItemGroup) {
                        var dynamicItemGroup = dynamicMetadata.dynamicItemGroup;
                        for (var k = 0; k < dynamicItemGroup.length; k++) {
                            reportDataId.itemName = dynamicItemGroup[k].dataName;
                            obj.dataType = dynamicItemGroup[k].dataType;
                            obj.dataValue = dynamicItemGroup[k].dataValue;//指标id
                            reportDataId.itemDict = dynamicItemGroup[k].dataValue;//指标分组目录
                            //dataSets.push(obj);
                            itemMenu.push(dynamicItemGroup[k].dataValue);
                            isPush = true;
                        }
                    }

                }
                if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
                    if (dynamicMetadata.dynamicSurobj) {
                        var dynamicSurobj = dynamicMetadata.dynamicSurobj;
                        for (var k = 0; k < dynamicSurobj.length; k++) {
                            reportDataId.itemName = dynamicSurobj[k].dataName;
                            obj.dataType = dynamicSurobj[k].dataType;
                            obj.dataValue = dynamicSurobj[k].dataValue;
                            reportDataId.surobj = obj.dataValue;//调查对象
                            obj.dataInfo1 = dynamicSurobj[k].dataInfo1;
                            reportDataId.surobjType = obj.dataInfo1;//调查对象类型
                            obj.dataInfo2 = dynamicSurobj[k].dataInfo2;
                            reportDataId.areaId = dynamicSurobj[k].dataInfo2;
                            dataSets.push(obj);
                            isPush = true;
                        }
                    }
                }
                if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
                    if (dynamicMetadata.dynamicTimeframe) {
                        var dynamicTimeframe = dynamicMetadata.dynamicTimeframe;
                        for (var k = 0; k < dynamicTimeframe.length; k++) {
                            reportDataId.itemName = dynamicTimeframe[k].dataName;
                            obj.dataType = dynamicTimeframe[k].dataType;
                            obj.dataValue = dynamicTimeframe[k].dataValue;
                            reportDataId.timeFrame = dynamicTimeframe[k].dataValue;//时间框架
                            dataSets.push(obj);
                            isPush = true;
                        }

                    }

                }
                if (!isPush && seriesConditions[j].metaType != METADATA_TYPE.ITEM_MENU) {
                    dataSets.push(obj)
                }
            }
        }
    }
    if (itemMenu.length) {
        itemMenu.sort();
        var obj = {
            dataType: METADATA_TYPE.ITEM_MENU,
            dataValue: itemMenu.join(",")
        }
        dataSets.push(obj);
    }
    return {
        periodType: periodType,
        structureId: structureId
    };
}
EsiChart.prototype.loadData = function (datas, dynamicMetadata, _isDynamicOnly) {
    if (datas) {
        this.datas = datas;
    } else {
        var isDynamicOnly = _isDynamicOnly ? _isDynamicOnly : false;
        // 查询所有序列的数据
        var datas;
        var periodType = null;// 报送频率
        var dataInfosList = [];//总的数据集（根据序列分）
        var structureIds = [];//时间图表结构id（分类轴和序列不会同时有时间）
        var cateDataSets = [];//分类轴数据集
        var cateStructureId;// 分类轴时间id
        // 遍历分类轴条件
        var cateReportDataId = this.initRportDataId();
        var esiCategoryArray = this.esiCategory;
        var hasCateDynamic = false;//分类轴存在动态序列
        var hasSeriesDynamic = false;//序列存在动态序列
        var esiSeriesArray = this.esiSeries;
        for (var i = 0; i < esiSeriesArray.length; i++) {
            if (esiSeriesArray[i].isDynamic == 1) {
                hasSeriesDynamic = true;
            }
        }
        for (var i = 0; i < esiCategoryArray.length; i++) {
            if (esiCategoryArray[i].isDynamic == 1) {
                hasCateDynamic = true;
            }
            var category = esiCategoryArray[i];
            if (!esiCategoryArray[i].isDynamic == 1 && !(!hasSeriesDynamic && isDynamicOnly)) {
                var __ret = getCategoryConditions(category, cateReportDataId, periodType, cateDataSets);
                periodType = __ret.periodType;
                cateStructureId = __ret.cateStructureId;
            } else if (esiCategoryArray[i].isDynamic == 1) {
                var __ret = getCategoryConditions(category, cateReportDataId, periodType, cateDataSets, isDynamicOnly, dynamicMetadata);
                periodType = __ret.periodType;
                cateStructureId = __ret.cateStructureId;
            }

        }


        for (var i = 0; i < esiSeriesArray.length; i++) {

            var reportDataId = this.copyRportDataId(cateReportDataId);
            var structureId = cateStructureId;// 分类轴的时间id
            var series = esiSeriesArray[i];
            var dataSets = [];
            if (!esiSeriesArray[i].isDynamic == 1 && !(!hasCateDynamic && isDynamicOnly)) {// 静态序列在分类轴微动态或者全部加载是读取数据
                for (var k = 0; k < cateDataSets.length; k++) {
                    dataSets.push(cateDataSets[k])// 接着分类轴的数据集添加
                }
                var __ret = getSeriesConditions(series, reportDataId, periodType, structureId, dataSets);
                periodType = __ret.periodType;
                structureId = __ret.structureId;
            } else if (esiSeriesArray[i].isDynamic == 1) {
                for (var k = 0; k < cateDataSets.length; k++) {
                    dataSets.push(cateDataSets[k])// 接着分类轴的数据集添加
                }
                var __ret = getSeriesConditions(series, reportDataId, periodType, structureId, dataSets, isDynamicOnly, dynamicMetadata);
                periodType = __ret.periodType;
                structureId = __ret.structureId;
            }

            structureIds.push(structureId)
            dataInfosList.push(dataSets);
            this.esiSeries[i].reportDataId = reportDataId;
        }
        $.ajax({
            type: 'post',
            async: false,
            url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryDatasByCondition',
            data: {
                structureIds: JSON.stringify(structureIds),
                chartType: 2,//图表类型
                periodType: periodType,
                dataSets: JSON.stringify(dataInfosList)
            },

            dataType: 'json',
            success: function (data) {
                datas = data
            }
        });
        this.loadedData = true;
        if (this.datas) {
            //console.log(this.datas)
            if (datas) {
                for (var i = 0; i < datas.length; i++) {
                    this.datas.push(datas[i]);
                }
            }
        } else {
            this.datas = datas;
        }
        /*


         if(series.info.chartType!= ANALYSISCHART_INFO.CHART_PIE){
         this.setX(xAxis)
         }*/


    }


}
EsiChart.prototype.createYAxis = function () {
    var yAxis = [
        {
            type: 'value'
        },
        {
            type: 'value'
        }

        /*{
         type: 'value',
         scale: true,
         name: '价格',
         max: 20,
         min: 0,
         boundaryGap: [0.2, 0.2]
         },
         {
         type: 'value',
         scale: true,
         name: '预购量',
         max: 1200,
         min: 0,
         boundaryGap: [0.2, 0.2]
         }*/
    ];
    // 设置单位
    for (var i = 0; i < this.esiSeries.length; i++) {
        var unit = this.esiSeries[i].unit ? this.esiSeries[i].unit : ""
        if (this.esiSeries[i].info.axis) {
            yAxis[this.esiSeries[i].info.axis].name = unit;
        } else if (this.esiSeries[i].info.axis == 0) {
            yAxis[0].name = unit;
        }
        // 设置最大最小值
    }
    return yAxis;
};
//设置散点图坐标
EsiChart.prototype.createScatterAxis = function () {
    var unitX ="";
    var unitY ="";
    var nameX="";
    var nameY="";
    if(this.esiSeries.length>1){
        unitX = this.esiSeries[0].unit ? this.esiSeries[0].unit : ""
        unitY = this.esiSeries[1].unit ? this.esiSeries[1].unit : ""
        var nameX=this.esiSeries[0].info.name;
        var nameY=this.esiSeries[1].info.name;
    }

    var xAxis = [
        {
            name : unitX,
            type : 'value',
            scale:true
        }
    ];
    var yAxis = [
        {
            name : unitY,
            type : 'value',
            scale:true
        }
    ];
    return {xAxis:xAxis,yAxis:yAxis,nameX:nameX,nameY:nameY};
};

function getMetaDatas(category, dynamicMetadata) {
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
        return dynamicMetadata.dynamicItem
    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
        return dynamicMetadata.dynamicItemGroup;

    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
        return dynamicMetadata.dynamicSurobj;
    }

    if (category.info.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
        return dynamicMetadata.dynamicTimeframe;
    }
}
function isDynamic(category) {
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
        return true;
    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
        return true;

    }
    if (category.info.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
        return true;
    }

    if (category.info.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
        return true;
    }
    return false;
}
EsiChart.prototype.createLegend = function (dynamicMetadata) {
    var legend = {
        y: 30
    }
    var legendData = [];
    var selected = [];
    for (var i = 0; i < this.esiSeries.length; i++) {
        if (this.esiSeries[i].info.chartType == ANALYSISCHART_INFO.CHART_PIE) {
            for (var j = 0; j < this.esiCategory.length; j++) {
                if (dynamicMetadata) {
                    var metadatas = getMetaDatas(this.esiCategory[j], dynamicMetadata);
                    if (metadatas) {
                        for (var k = 0; k < metadatas.length; k++) {
                            legendData.push(metadatas[k].dataName);
                            var name = metadatas[k].dataName;
                            if (this.esiSeries[i].info.isShow == 0) {
                                var obj = {name: false};
                                selected.push(obj);
                            }
                        }
                    } else {
                        var name = this.esiCategory[j].info.name
                        legendData.push(name);
                        if (this.esiSeries[i].info.isShow == 0) {
                            var obj = {name: false};
                            selected.push(obj);
                        }

                    }
                } else {
                    if (!isDynamic(this.esiCategory[j])) {
                        var name = this.esiCategory[j].info.name
                        legendData.push(name);
                        if (this.esiSeries[i].info.isShow == 0) {
                            var obj = {name: false};
                            selected.push(obj);
                        }
                    }

                }

            }
            legend.data = legendData;
        } else if (this.esiSeries[i].info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
            var name = this.esiSeries[i].info.group
            var isExist = false;
            for(var k=0;k<legendData.length;k++){
                if(legendData[k]==name){
                    isExist = true;
                    break;
                }
            }
            if(!isExist){
                legendData.push(name);
            }

            if (this.esiSeries[i].info.isShow == 0) {
                var obj = {};
                obj[name] = false
                selected = obj/*JSON.stringify(obj).substring(1,JSON.stringify(obj).length-1)*/;
            }
            legend.data = legendData;
        } else {
            if (!this.esiSeries[i].isDynamic == 1) {
                var name = this.esiSeries[i].info.name
                legendData.push(name);
                if (this.esiSeries[i].info.isShow == 0) {
                    var obj = {};
                    obj[name] = false
                    selected = obj/*JSON.stringify(obj).substring(1,JSON.stringify(obj).length-1)*/;
                }
            } else {
                if (dynamicMetadata) {
                    var metadatas = getMetaDatas(this.esiSeries[i], dynamicMetadata);
                    if (metadatas) {
                        for (var k = 0; k < metadatas.length; k++) {
                            legendData.push(metadatas[k].dataName);
                            var name = metadatas[k].dataName;
                            if (this.esiSeries[i].info.isShow == 0) {
                                var obj = {name: false};
                                selected.push(obj);
                            }
                        }
                    } else {
                        legendData.push(this.esiSeries[i].info.name);
                        var name = this.esiSeries[i].info.name;
                        if (this.esiSeries[i].info.isShow == 0) {
                            var obj = {name: false};
                            selected.push(name, obj);
                        }
                    }
                }

            }
            legend.data = legendData;
        }

    }
    if (selected) {
        legend.selected = selected;
        /*legend.selected ={
         '居民储蓄存款':false
         };*/
        //console.log(selected)
        //console.log(legend.selected)
    }

    return legend;
};

EsiChart.prototype.createCategory = function (dynamicMetadata) {
    var result = [];
    var esiCateArray = this.esiCategory;
    var datas = this.getData();
    //var tmpCate = null;
    result = {
        type: 'category',
        boundaryGap: true,
        axisLine:{
            onZero:false
        }
}
    var cateData = [];
    for (var i = 0; i < esiCateArray.length; i++) {
        //console.log(esiCateArray[i].info);
        if (!esiCateArray[i].isDynamic == 1) {
            if (esiCateArray[i].info.metaType == METADATA_TYPE.TIME) {
                for (var i = 0; i < datas.length; i++) {
                    if (datas[i]) {
                        for (var j = 0; j < datas[i].length; j++) {
                            cateData.push(this.getTimeName(datas[i][j].time));
                        }
                        break;
                    }
                }

            } else {
                cateData.push(esiCateArray[i].info.name)
            }
        } else {
            if (dynamicMetadata) {
                var metadatas = getMetaDatas(esiCateArray[i], dynamicMetadata);
                if (metadatas) {
                    for (var k = 0; k < metadatas.length; k++) {
                        cateData.push(metadatas[k].dataName);
                    }
                } else {
                    cateData.push(esiCateArray[i].info.name);
                }
            }
        }
    }
    result.data = cateData;
    /**
     设置其他样式
     result.sss = esiCateArray[i].info.style.sss;

     */
    return result;
}
EsiChart.prototype.createSeries = function (initSeries, dynamicMetadata, _isDynamicOnly) {
    var result = [];
    if (initSeries) {
        result = initSeries;
    }
    var isDynamic = dynamicMetadata ? true : false;
    var isDynamicOnly = _isDynamicOnly ? _isDynamicOnly : false;
    var esiSeriesArray = this.esiSeries;
    var tmpSeries = null;
    var series = null;
    var isScatter = false;
    for (var i = 0; i < esiSeriesArray.length; i++) {
        series = esiSeriesArray[i];
        if (series.info.chartType == ANALYSISCHART_INFO.CHART_SCATTER) {
            isScatter = true;
        }
        //console.log(series)
        if (!esiSeriesArray[i].isDynamic == 1 && !(isDynamicOnly && isDynamic)) {
            tmpSeries = {
                name: series.info.name,
                type: ANALYSISCHART_INFO.getEN(series.info.chartType, this.plug),
                yAxisIndex: series.info.axis ? series.info.axis : 0,
                data: this.createSeriesData([series], null, dynamicMetadata)
            };
            if (series.info.group) {
                // 分组
                tmpSeries.stack = series.info.group;
            }
            //tmpSeries.sss = esiSeriesArray[i].info.style.sss;
            result.push(tmpSeries);

        } else {
            //动态元数据处理
            //console.log("asdfas")
            if (isDynamic) {
                //console.log("asdfas1")
                if (series.info.metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
                    if (dynamicMetadata.dynamicItem) {
                        var dynamicItem = dynamicMetadata.dynamicItem;
                        for (var k = 0; k < dynamicItem.length; k++) {
                            tmpSeries = {
                                name: dynamicItem[k].dataName,
                                type: ANALYSISCHART_INFO.getEN(series.info.chartType, this.plug),
                                yAxisIndex: series.info.axis ? series.info.axis : 0,
                                data: this.createSeriesData([series], dynamicItem[k], dynamicMetadata)
                            };
                            result.push(tmpSeries);
                        }
                    }

                }
                if (series.info.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
                    if (dynamicMetadata.dynamicItemGroup) {
                        var dynamicItemGroup = dynamicMetadata.dynamicItemGroup;
                        for (var k = 0; k < dynamicItemGroup.length; k++) {
                            tmpSeries = {
                                name: dynamicItemGroup[k].dataName,
                                type: ANALYSISCHART_INFO.getEN(series.info.chartType, this.plug),
                                yAxisIndex: series.info.axis ? series.info.axis : 0,
                                data: this.createSeriesData([series], dynamicItemGroup[k], dynamicMetadata)
                            };
                            result.push(tmpSeries);
                        }
                    }

                }
                //console.log(dynamicMetadata);
                //console.log(series.info);
                if (series.info.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
                    if (dynamicMetadata.dynamicSurobj) {
                        var dynamicSurobj = dynamicMetadata.dynamicSurobj;
                        //console.log(dynamicSurobj)
                        for (var k = 0; k < dynamicSurobj.length; k++) {
                            tmpSeries = {
                                name: dynamicSurobj[k].dataName,
                                type: ANALYSISCHART_INFO.getEN(series.info.chartType, this.plug),
                                yAxisIndex: series.info.axis ? series.info.axis : 0,
                                data: this.createSeriesData([series], dynamicSurobj[k], dynamicMetadata)
                            };
                            result.push(tmpSeries);
                        }
                    }
                }
                if (series.info.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
                    if (dynamicMetadata.dynamicTimeframe) {
                        var dynamicTimeframe = dynamicMetadata.dynamicTimeframe;
                        for (var k = 0; k < dynamicTimeframe.length; k++) {
                            tmpSeries = {
                                name: dynamicTimeframe[k].dataName,
                                type: ANALYSISCHART_INFO.getEN(series.info.chartType, this.plug),
                                yAxisIndex: series.info.axis ? series.info.axis : 0,
                                data: this.createSeriesData([series], dynamicTimeframe[k], dynamicMetadata)
                            };
                            result.push(tmpSeries);
                        }

                    }

                } else {
                    tmpSeries = {
                        name: series.info.name,
                        type: ANALYSISCHART_INFO.getEN(series.info.chartType, this.plug),
                        yAxisIndex: series.info.axis ? series.info.axis : 0,
                        data: this.createSeriesData([series], null, dynamicMetadata)
                    };
                    result.push(tmpSeries);
                }
            }

        }
    }
    var scatterSeriesDatas = [];//散点图序列数据
    if (isScatter) {
        for (var i = 0; i < result.length; i++) {
            var isExist = false;
            for (var j = 0; j < scatterSeriesDatas.length; j++) {
                if (scatterSeriesDatas[j].name == result[i].stack) {
                    for (var k = 0; k < scatterSeriesDatas[j].data.length; k++)
                        scatterSeriesDatas[j].data[k].push(result[i].data[k].value);
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                tmpSeries = {
                    name: result[i].stack,
                    type: result[i].type,
                };
                tmpSeries.data = [];
                for (var k = 0; k < result[i].data.length; k++) {
                    tmpSeries.data.push([result[i].data[k].value]);
                }
                scatterSeriesDatas.push(tmpSeries);
            }

        }
        return scatterSeriesDatas;
    }
    return result;

}
/**
 * 将时间转换为文字
 * @param time
 * @returns {*}
 */
EsiChart.prototype.getTimeName = function (time) {
    var fre = this.period;
    var year = time.year
    var period = time.period
    console.log(fre);
    console.log(time);
    return FREQUENCY_TYPE.getTime(fre, period, year);

}

function setReportDataId(esiSeries, dynamicData, dynamicMetadata) {
    var reportDataId = this.initRportDataId();

    for (var i = 0; i < this.esiCategory.length; i++) {
        if (this.esiCategory[i].info.metaType == METADATA_TYPE.TIME) {
            reportDataId.isTime = true;
            /**
             * 分类周为时间时，只能有时间一条分类轴
             */
            var cateConditions = this.esiCategory[i].condition;
            var itemMenu = [];
            for (var j = 0; j < cateConditions.length; j++) {
                if (cateConditions[j].metaType == METADATA_TYPE.ITEM) {
                    reportDataId.itemId = cateConditions[j].metaId;//指标
                    var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
                    if (metxExtObj.caliber)
                        reportDataId.itemCaliber = metxExtObj.caliber;//口径
                    reportDataId.depId = metxExtObj.dep;// 部门
                    if (metxExtObj.rptTmp) {
                        reportDataId.rptTmp = metxExtObj.rptTmp;//模板
                    }
                }
                if (cateConditions[j].metaType == METADATA_TYPE.ITEM_MENU) {
                    //reportDataId.itemDict = cateConditions[j].metaId;//指标分组目录
                    itemMenu.push(cateConditions[j].metaId);
                }
                if (cateConditions[j].metaType == METADATA_TYPE.TIME_FRAME) {
                    reportDataId.timeFrame = cateConditions[j].metaId;//时间框架
                }
                if (cateConditions[j].metaType == METADATA_TYPE.RESEARCH_OBJ) {
                    var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
                    reportDataId.surobj = cateConditions[j].metaId;//调查对象
                    reportDataId.surobjType = metxExtObj.objType;//调查对象类型
                    if (metxExtObj.objType == 1) {
                        reportDataId.areaId = cateConditions[j].metaId;//地区类型调查对象
                    } else {
                        if (metxExtObj.areaId) {
                            reportDataId.areaId = metxExtObj.areaId;
                        }
                    }
                }
                // 时间为叶子节点不需要传报送频率
                // 处理动态元数据
                var dynamicCates = getDynamicData(this.esiCategory[i], dynamicMetadata)
                if (dynamicCates) {
                    var dynamicCate = dynamicCates[0];
                    //动态元数据处理
                    if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
                        reportDataId.itemName = dynamicCate.dataName;
                        reportDataId.itemId = dynamicCate.dataValue;//指标id
                        reportDataId.itemCaliber = dynamicCate.dataInfo1;//口径
                        reportDataId.depId = dynamicCate.dataInfo2;// 部门
                        if (dynamicCate.dataInfo3) {
                            reportDataId.rptTmp = dynamicCate.dataInfo3;//模板
                        }
                    }
                    if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
                        reportDataId.itemName = dynamicCate.dataName;
                        //reportDataId.itemDict = dynamicCate.dataValue;//指标分组目录
                        itemMenu.push(dynamicCate.dataValue);
                    }
                    if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
                        reportDataId.itemName = dynamicCate.dataName;
                        reportDataId.surobj = dynamicCate.dataValue;//调查对象
                        reportDataId.surobjType = dynamicCate.dataInfo1;//调查对象类型
                        reportDataId.areaId = dynamicCate.dataInfo2;
                    }
                    if (cateConditions[j].metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
                        reportDataId.itemName = dynamicCate.dataName;
                        reportDataId.timeFrame = dynamicCate.dataValue;//时间框架
                    }
                }
            }
            if (itemMenu.length) {
                itemMenu.sort();
                reportDataId.itemDict = itemMenu.join(",");
            }
        } else {
            reportDataId.itemName = this.esiCategory[i].info.name;
        }
    }

    var seriesConditions = esiSeries.condition;
    var itemMenu = [];
    for (var j = 0; j < seriesConditions.length; j++) {
        if (seriesConditions[j].metaType == METADATA_TYPE.ITEM) {
            reportDataId.itemId = seriesConditions[j].metaId;//指标
            var metxExtObj = $.parseJSON(seriesConditions[j].metaExt);
            if (metxExtObj.caliber)
                reportDataId.itemCaliber = metxExtObj.caliber;//口径
            reportDataId.depId = metxExtObj.dep;// 部门
            if (metxExtObj.rptTmp) {
                reportDataId.rptTmp = metxExtObj.rptTmp;//模板
            }
        }
        if (seriesConditions[j].metaType == METADATA_TYPE.ITEM_MENU) {
            //reportDataId.itemDict = seriesConditions[j].metaId;//指标分组目录
            itemMenu.push(seriesConditions[j].metaId);
        }
        if (seriesConditions[j].metaType == METADATA_TYPE.TIME_FRAME) {
            reportDataId.timeFrame = seriesConditions[j].metaId;//时间框架
        }
        if (seriesConditions[j].metaType == METADATA_TYPE.RESEARCH_OBJ) {
            var metxExtObj = $.parseJSON(seriesConditions[j].metaExt);
            reportDataId.surobj = seriesConditions[j].metaId;//调查对象
            reportDataId.surobjType = metxExtObj.objType;//调查对象类型
            if (metxExtObj.objType == 1) {
                reportDataId.areaId = seriesConditions[j].metaId;//地区类型调查对象
            } else {
                if (metxExtObj.areaId) {
                    reportDataId.areaId = metxExtObj.areaId;
                }
            }
        }
        // 有时间的话需要传报送频率
        if (seriesConditions[j].metaType == METADATA_TYPE.TIME) {
            var metxExtObj = $.parseJSON(seriesConditions[j].metaExt);
            periodType = metxExtObj.periodType;
            structureId = seriesConditions[j].id;
            /**
             * 序列的时间只可能是一个报告期，需要记录下此时的年份和月份
             */
            var timeRange = metxExtObj.timeRange;
            if (!METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).isNewPeriod) {
                reportDataId.year = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).year;
                reportDataId.month = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).month;
            }
        }
        if (dynamicData) {
            //动态元数据处理
            if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
                reportDataId.itemName = dynamicData.dataName;
                reportDataId.itemId = dynamicData.dataValue;//指标id
                reportDataId.itemCaliber = dynamicData.dataInfo1;//口径
                reportDataId.depId = dynamicData.dataInfo2;// 部门
                if (dynamicData.dataInfo3) {
                    reportDataId.rptTmp = dynamicData.dataInfo3;//模板
                }
            }
            if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
                reportDataId.itemName = dynamicData.dataName;
                //reportDataId.itemDict = dynamicData.dataValue;//指标分组目录
                itemMenu.push(dynamicData.dataValue);
            }
            if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
                reportDataId.itemName = dynamicData.dataName;
                reportDataId.surobj = dynamicData.dataValue;//调查对象
                reportDataId.surobjType = dynamicData.dataInfo1;//调查对象类型
                reportDataId.areaId = dynamicData.dataInfo2;
            }
            if (seriesConditions[j].metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
                reportDataId.itemName = dynamicData.dataName;
                reportDataId.timeFrame = dynamicData.dataValue;//时间框架
            }
        }
    }
    if (itemMenu.length) {
        itemMenu.sort();
        reportDataId.itemDict = itemMenu.join(",");
    }
    return reportDataId;
}
//序列List如果list大于1的话封装散点图的datas格式[[a,c],[a,b,c]]，如果序列list等于1的话封装二位图表的datas格式[{name:'',vlaue:''}]

EsiChart.prototype.createSeriesData = function (seriesList, dynamicData, dynamicMetadata) {
    if (seriesList.length == 1) {//普通格式
        var isDynamic = (dynamicMetadata) ? true : false;
        var esiSeries = seriesList[0];
        var datas = this.datas;
        //console.log(this.datas)
        var seriesReportDataId = setReportDataId.call(this, esiSeries, dynamicData);
        //console.log(seriesReportDataId);
        //var seriesReportDataId = esiSeries.reportDataId;
        var seriesDatas = [];
        if (seriesReportDataId.isTime) {
            setSeriesData.call(this, datas, seriesReportDataId, esiSeries, seriesDatas, seriesReportDataId.isTime);
        } else {
            // 遍历分类轴条件
            var esiCategoryArray = this.esiCategory;
            var reportDataId = this.copyRportDataId(seriesReportDataId);
            for (var i = 0; i < esiCategoryArray.length; i++) {
                var dynamicReportDataIds = [];
                var itemMenu = [];
                if (esiCategoryArray[i].isDynamic == 1 && isDynamic) {//保存动态数据集
                    //动态元数据处理
                    if (esiCategoryArray[i].info.metaType == METADATA_TYPE.DYNAMIC_ITEM) {//动态指标
                        if (dynamicMetadata.dynamicItem) {
                            var dynamicItem = dynamicMetadata.dynamicItem;
                            for (var k = 0; k < dynamicItem.length; k++) {
                                var dynamicReportDataId = this.copyRportDataId(reportDataId);
                                dynamicReportDataId.itemName = dynamicItem[k].dataName;
                                dynamicReportDataId.itemId = dynamicItem[k].dataValue;//指标id
                                dynamicReportDataId.itemCaliber = dynamicItem[k].dataInfo1;//口径
                                dynamicReportDataId.depId = dynamicItem[k].dataInfo2;// 部门
                                if (dynamicItem[k].dataInfo3) {
                                    dynamicReportDataId.rptTmp = dynamicItem[k].dataInfo3;//模板
                                }
                                dynamicReportDataIds.push(dynamicReportDataId)
                            }
                        }

                    }
                    if (esiCategoryArray[i].info.metaType == METADATA_TYPE.DYNAMIC_ITEMGROUP) {//动态指标分组目录
                        if (dynamicMetadata.dynamicItemGroup) {
                            var dynamicItemGroup = dynamicMetadata.dynamicItemGroup;
                            for (var k = 0; k < dynamicItemGroup.length; k++) {
                                var dynamicReportDataId = this.copyRportDataId(reportDataId);
                                dynamicReportDataId.itemName = dynamicItemGroup[k].dataName;
                                dynamicReportDataId.itemDict = dynamicItemGroup[k].dataValue;//指标分组目录
                                dynamicReportDataIds.push(dynamicReportDataId)
                            }
                        }

                    }
                    if (esiCategoryArray[i].info.metaType == METADATA_TYPE.DYNAMIC_SUROBJ) {//动态调查对象
                        if (dynamicMetadata.dynamicSurobj) {
                            var dynamicSurobj = dynamicMetadata.dynamicSurobj;
                            for (var k = 0; k < dynamicSurobj.length; k++) {
                                var dynamicReportDataId = this.copyRportDataId(reportDataId);
                                dynamicReportDataId.itemName = dynamicSurobj[k].dataName;
                                dynamicReportDataId.surobj = dynamicSurobj[k].dataValue;//调查对象
                                dynamicReportDataId.surobjType = dynamicSurobj[k].dataInfo1;//调查对象类型
                                dynamicReportDataId.areaId = dynamicSurobj[k].dataInfo2;
                                dynamicReportDataIds.push(dynamicReportDataId)
                            }
                        }
                    }
                    if (esiCategoryArray[i].info.metaType == METADATA_TYPE.DYNAMIC_TIMEFRAME) {//动态时间框架
                        if (dynamicMetadata.dynamicTimeframe) {
                            var dynamicTimeframe = dynamicMetadata.dynamicTimeframe;
                            for (var k = 0; k < dynamicTimeframe.length; k++) {
                                var dynamicReportDataId = this.copyRportDataId(reportDataId);
                                dynamicReportDataId.itemName = dynamicTimeframe[k].dataName;
                                dynamicReportDataId.timeFrame = dynamicTimeframe[k].dataValue;//时间框架
                                dynamicReportDataIds.push(dynamicReportDataId)
                            }
                        }
                    }
                }
                if (dynamicReportDataIds.length == 0 && !esiCategoryArray[i].isDynamic == 1) {
                    dynamicReportDataIds.push(reportDataId);
                }
                for (var k = 0; k < dynamicReportDataIds.length; k++) {
                    var _reportDataId = this.copyRportDataId(dynamicReportDataIds[k]);
                    if (_reportDataId.itemDict && !0 == _reportDataId.itemDict) {
                        itemMenu.push(_reportDataId.itemDict);
                    }
                    if (!esiCategoryArray[i].isDynamic == 1) {
                        _reportDataId.itemName = esiCategoryArray[i].info.name;
                    } else {
                        //叶子节点不是动态指标的情况？？？
                        var _info = getInfoDynamic(esiCategoryArray[i], dynamicMetadata)
                        if (!_info.isDynamicInfo) {
                            _reportDataId.itemName = esiCategoryArray[i].info.name;
                        }
                    }

                    var cateConditions = esiCategoryArray[i].condition;
                    for (var j = 0; j < cateConditions.length; j++) {
                        if (cateConditions[j].metaType == METADATA_TYPE.ITEM) {
                            _reportDataId.itemId = cateConditions[j].metaId;//指标
                            var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
                            if (metxExtObj.caliber)
                                _reportDataId.itemCaliber = metxExtObj.caliber;//口径
                            _reportDataId.depId = metxExtObj.dep;// 部门
                            if (metxExtObj.rptTmp) {
                                _reportDataId.rptTmp = metxExtObj.rptTmp;//模板
                            }

                        }
                        if (cateConditions[j].metaType == METADATA_TYPE.ITEM_MENU) {
                            //_reportDataId.itemDict = cateConditions[j].metaId;//指标分组目录
                            itemMenu.push(cateConditions[j].metaId);
                        }
                        if (cateConditions[j].metaType == METADATA_TYPE.TIME_FRAME) {
                            _reportDataId.timeFrame = cateConditions[j].metaId;//时间框架
                        }
                        if (cateConditions[j].metaType == METADATA_TYPE.RESEARCH_OBJ) {
                            var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
                            _reportDataId.surobj = cateConditions[j].metaId;//调查对象
                            _reportDataId.surobjType = metxExtObj.objType;//调查对象类型
                            if (metxExtObj.objType == 1) {
                                _reportDataId.areaId = cateConditions[j].metaId;//地区类型调查对象
                            } else {
                                if (metxExtObj.areaId) {
                                    _reportDataId.areaId = metxExtObj.areaId;
                                }
                            }
                        }
                        // 有时间的话需要传报送频率
                        if (cateConditions[j].metaType == METADATA_TYPE.TIME) {
                            var metxExtObj = $.parseJSON(cateConditions[j].metaExt);
                            periodType = metxExtObj.periodType;
                            cateStructureId = cateConditions[j].id;
                            /**
                             * 序列的时间只可能是一个报告期，需要记录下此时的年份和月份
                             */
                            var timeRange = metxExtObj.timeRange;
                            if (!METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).isNewPeriod) {
                                _reportDataId.year = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).year;
                                _reportDataId.month = METADATA_TYPE.getSingleTime(timeRange[0].type, periodType, timeRange).month;
                            }
                        }
                    }
                    if (itemMenu.length) {
                        itemMenu.sort();
                        _reportDataId.itemDict = itemMenu.join(",");
                    }
                    console.log(_reportDataId);
                    setSeriesData.call(this, datas, _reportDataId, esiSeries, seriesDatas, seriesReportDataId.isTime);
                }


            }
        }
    }
    else {
        /**
         *      //如果是散点图的话
         var seriesData = [];
         var cateData = [];//cate 是不是时间直接把datas的时间
         for(item in category){
     for(ser in seriesList){
     cateData.push(item.conditions+ser.conditions get data from this.datas);
     }
     seriesData.push(cateData);
     cateData = [];
     }
         */
    }
    return seriesDatas;
}

function setSeriesData(datas, seriesReportDataId, esiSeries, seriesDatas, isTime) {
    var hasSaved = false;
    for (var i = 0; i < datas.length; i++) {// 分序列的数据(包含所有时间)
        if (hasSaved) {
            break;
        }
        /* 按时间存储的序列 包含：
         data[i].datas 数据（可能多条）
         data[i].time 时间*/
        var data = datas[i];
        var isExist = false;
        if (data) {
            for (var k = 0; k < data[0].datas.length; k++) {
                if (this.compareRportDataId(seriesReportDataId, data[0].datas[k].reportDataId, data[0].datas[k].rptTmp)) {
                    isExist = true;// 数据存在
                    if (!isTime) {// 如果不是时间，只有一个报告期的数据；如果是时间的话需要遍历当前数组
                        var seriesData = null;
                        //console.log(data[0]);
                        var unit = data[0].datas[k].unit ? data[0].datas[k].unit : "";
                        var name = seriesReportDataId.itemName;
                        esiSeries.unit = unit;// 单位存到序列上，设置y轴需要
                        seriesData = {
                            name: name,
                            value: data[0].datas[k].itemValue
                        };
                        if (seriesData) {
                            seriesDatas.push(seriesData);
                            hasSaved = true;
                        }
                    }
                    break;
                } else {
                }
            }
            if (isExist && isTime) {
                for (var j = 0; j < data.length; j++) {
                    var seriesData = null;
                    for (var k = 0; k < data[j].datas.length; k++) {
                        if (this.compareRportDataId(seriesReportDataId, data[j].datas[k].reportDataId, data[j].datas[k].rptTmp)) {
                            var unit = data[j].datas[k].unit ? data[j].datas[k].unit : "";
                            var name = this.getTimeName(data[j].time);
                            esiSeries.unit = unit;// 单位存到序列上，设置y轴需要
                            seriesData = {
                                name: name,
                                value: data[j].datas[k].itemValue
                            };
                            break;
                        }
                    }
                    if (seriesData) {
                        seriesDatas.push(seriesData);
                    }
                }
                break;
            }
        }
    }
    if (!hasSaved && !isTime) {
        seriesData = {
            name: name,
            value: "-"
        };
        seriesDatas.push(seriesData);
    }
}
EsiChart.prototype.getData = function (data) {
    return this.datas;
}
EsiChart.prototype.setX = function (data) {
    this.xAxis = data;
}
EsiChart.prototype.getData1 = function () {
    return this.esiSeries;
}
EsiChart.prototype.compareRportDataId = function (seriesRptDataId, rptDataId, rptTmp) {
    if (seriesRptDataId.areaId ? seriesRptDataId.areaId != rptDataId.areaId : !seriesRptDataId.areaId)//地区id（系统默认）
        return false;
    if (seriesRptDataId.itemId ? seriesRptDataId.itemId != rptDataId.item : !seriesRptDataId.itemId)//指标（必需）'
        return false;
    if (seriesRptDataId.depId ? seriesRptDataId.depId != rptDataId.depId : !seriesRptDataId.depId)//部门id（必需）
        return false;
    if (seriesRptDataId.itemCaliber ? seriesRptDataId.itemCaliber != rptDataId.itemCaliber : !seriesRptDataId.itemCaliber)// 口径（必需）
        return false;
    if (seriesRptDataId.rptTmp ? seriesRptDataId.rptTmp != rptTmp : seriesRptDataId.rptTmp)//模板id（默认为空，非联合主键）
        return false;
    if ((seriesRptDataId.itemDict || seriesRptDataId.itemDict == 0) ? seriesRptDataId.itemDict != rptDataId.itemDict : !seriesRptDataId.itemDict)//指标分组目录（系统默认）
        return false;
    if (seriesRptDataId.month ? seriesRptDataId.month != rptDataId.month : seriesRptDataId.month)//月（序列存在时间时需要,期度不进行比较）
        return false;
    if (seriesRptDataId.year ? seriesRptDataId.year != rptDataId.year : seriesRptDataId.year)//年（序列存在时间时需要,期度不进行比较）
        return false;
    if (seriesRptDataId.surobj ? seriesRptDataId.surobj != rptDataId.surobj : !seriesRptDataId.surobj)//调查对象（系统默认）
        return false;
    if ((seriesRptDataId.surobjType || seriesRptDataId.surobjType == 0) ? seriesRptDataId.surobjType != rptDataId.surobjType : !seriesRptDataId.surobjType)//调查对象类型（系统默认）
        return false;
    if (seriesRptDataId.timeFrame ? seriesRptDataId.timeFrame != rptDataId.timeFrame : !seriesRptDataId.timeFrame)//时间框架（必需）
        return false;
    return true;
}
EsiChart.prototype.copyRportDataId = function (rptDataId) {
    var reportDataId = new Object();
    reportDataId.itemName = rptDataId.itemName;//指标名称，用于序列值名称，不参与比较
    reportDataId.isTime = rptDataId.isTime;//分类轴是否时间，不参与比较
    reportDataId.areaId = rptDataId.areaId;//地区id（系统默认）
    reportDataId.itemId = rptDataId.itemId;//指标（必需）
    reportDataId.depId = rptDataId.depId;//部门id（必需）
    reportDataId.itemCaliber = rptDataId.itemCaliber;// 口径（必需）
    reportDataId.rptTmp = rptDataId.rptTmp;//模板id（默认为空，非联合主键）
    //reportDataId.rptId;
    reportDataId.itemDict = rptDataId.itemDict;//指标分组目录（系统默认）
    reportDataId.month = rptDataId.month;//月（必需）
    reportDataId.year = rptDataId.year;//年（必需）
    reportDataId.surobj = rptDataId.surobj;//调查对象（系统默认）
    reportDataId.surobjType = rptDataId.surobjType;//调查对象类型（系统默认）
    reportDataId.timeFrame = rptDataId.timeFrame;//时间框架（必需）
    return reportDataId;
}
EsiChart.prototype.initRportDataId = function () {
    var reportDataId = new Object();
    reportDataId.itemName = "";//指标名称，用于序列值名称，不参与比较
    reportDataId.isTime = false;//分类轴是否时间，不参与比较
    reportDataId.areaId = surObjId;//地区id（系统默认）
    reportDataId.itemId = "";//指标（必需）
    reportDataId.depId = "";//部门id（必需）
    reportDataId.itemCaliber = "0";// 口径（必需,默认为0）
    reportDataId.rptTmp = "";//模板id（默认为空，非联合主键）
    //reportDataId.rptId;
    reportDataId.itemDict = "0";//指标分组目录（系统默认）
    reportDataId.month = "";//月（必需）
    reportDataId.year = "";//年（必需）
    reportDataId.surobj = surObjId;//调查对象（系统默认）
    reportDataId.surobjType = "0";//调查对象类型（系统默认）
    reportDataId.timeFrame = "";//时间框架（必需）
    return reportDataId;
}
EsiChart.prototype.copyDynamicMetadata = function (dynamicMetadata) {
    var _dynamicMetadata = new Object();
    _dynamicMetadata.dynamicItem = [];
    _dynamicMetadata.dynamicItemGroup = [];
    _dynamicMetadata.dynamicSurobj = [];
    _dynamicMetadata.dynamicTimeframe = [];
    if (dynamicMetadata.dynamicItem) {
        for (var i = 0; i < dynamicMetadata.dynamicItem.length; i++) {
            _dynamicMetadata.dynamicItem.push(dynamicMetadata.dynamicItem[i]);
        }
    }
    if (dynamicMetadata.dynamicItemGroup) {
        for (var i = 0; i < dynamicMetadata.dynamicItemGroup.length; i++) {
            _dynamicMetadata.dynamicItemGroup.push(dynamicMetadata.dynamicItemGroup[i]);
        }
    }
    if (dynamicMetadata.dynamicSurobj) {
        for (var i = 0; i < dynamicMetadata.dynamicSurobj.length; i++) {
            _dynamicMetadata.dynamicSurobj.push(dynamicMetadata.dynamicSurobj[i]);
        }
    }
    if (dynamicMetadata.dynamicTimeframe) {
        for (var i = 0; i < dynamicMetadata.dynamicTimeframe.length; i++) {
            _dynamicMetadata.dynamicTimeframe.push(dynamicMetadata.dynamicTimeframe[i]);
        }
    }
    return _dynamicMetadata;
}
/**
 *
 * @param _dynamicMetadata
 * @param dynamicMetadata
 */
function addDynamicMetadata(_dynamicMetadata, dynamicMetadata) {
    if (!_dynamicMetadata) {
        _dynamicMetadata = new Object();
        _dynamicMetadata.dynamicItem = [];
        _dynamicMetadata.dynamicItemGroup = [];
        _dynamicMetadata.dynamicSurobj = [];
        _dynamicMetadata.dynamicTimeframe = [];
    } else {
        if (!_dynamicMetadata.dynamicItem) {
            _dynamicMetadata.dynamicItem = [];
        }
        if (!_dynamicMetadata.dynamicItemGroup) {
            _dynamicMetadata.dynamicItemGroup = [];
        }
        if (!_dynamicMetadata.dynamicSurobj) {
            _dynamicMetadata.dynamicSurobj = [];
        }
        if (!_dynamicMetadata.dynamicTimeframe) {
            _dynamicMetadata.dynamicTimeframe = [];
        }
    }
    if (dynamicMetadata.dynamicItem) {
        for (var i = 0; i < dynamicMetadata.dynamicItem.length; i++) {
            _dynamicMetadata.dynamicItem.push(dynamicMetadata.dynamicItem[i]);
        }
    }
    if (dynamicMetadata.dynamicItemGroup) {
        for (var i = 0; i < dynamicMetadata.dynamicItemGroup.length; i++) {
            _dynamicMetadata.dynamicItemGroup.push(dynamicMetadata.dynamicItemGroup[i]);
        }
    }
    if (dynamicMetadata.dynamicSurobj) {
        for (var i = 0; i < dynamicMetadata.dynamicSurobj.length; i++) {
            _dynamicMetadata.dynamicSurobj.push(dynamicMetadata.dynamicSurobj[i]);
        }
    }
    if (dynamicMetadata.dynamicTimeframe) {
        for (var i = 0; i < dynamicMetadata.dynamicTimeframe.length; i++) {
            _dynamicMetadata.dynamicTimeframe.push(dynamicMetadata.dynamicTimeframe[i]);
        }
    }
    return _dynamicMetadata;
};
EsiChart.prototype.getDynamicMetadata = function () {
    return this.dynamicMetadata;
}