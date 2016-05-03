/**
 * Created by wxl on 2016/4/11.
 * 根据类型和id获取数据
 */
function EsiData(dataType, dataValue, year, period) {
    this.dataType = dataType;
    this.dataValue = dataValue;
    this.year = year;
    this.period = period;
}

/**
 * 根据传的dom获取
 * @param renderDom dom选择器
 */
EsiData.prototype.init = function (renderDom,fn) {
    switch (this.dataType) {
        case DATA_DICT_TYPE.RPT_SYNTHESIZE://综合表
            this.getSynthesizeData(renderDom,fn);
            break;
        case DATA_DICT_TYPE.RPT_CUSTOM://分析报表
            this.getCustomData(renderDom,fn);
            break;
        case DATA_DICT_TYPE.CHART://图表
            this.getChartData(renderDom,fn);
            break;
        case DATA_DICT_TYPE.TEXT_THEME://分析主题
            this.getThemeData(renderDom,fn);
            break;
        case DATA_DICT_TYPE.TEXT_DESC://文字分析
            this.getTextData(renderDom,fn);
            break;
    }
};
/**
 * 获取图表数据
 */
EsiData.prototype.getChartData = function (renderDom,fn) {
    //请求图表
    EsiData.ajaxData('/support/resourcecategory/analysis/chart/queryAnalysisChartInfoByChartId', {id: this.dataValue}, function (data) {
        this.datas = data;
        var gg = new EsiChart(data, null, -1);
        gg.init(function (option) {
            var myChart = echarts.init($(renderDom).get(0));
            if (myChart && option) {
                option.title = '';
                option.tooltip.trigger = 'axis';
                /*option.tooltip.position=function(pos){
                	pos[0] = pos[0]-50
                	return pos;
                };*/
                option.dataZoom = [{
                    type: 'slider',
                    show: true,
                    xAxisIndex: [0],
                    start: 50,
                    end: 100
                },
		        {
		            type: 'inside',
		            show: true,
		            xAxisIndex: [0],
                    start: 50,
                    end: 100
		        }];
				for(var i=0;option.series&&i<option.series.length;i++){
		        	option.series[i].showAllSymbol = true;
		        };
                myChart.setOption(option);
                if(fn){
                	fn(data);
                }
            }
        });
    });
};
/**
 * 获取综合表数据
 */
EsiData.prototype.getSynthesizeData = function (renderDom,fn) {
    var params = {
        id: this.dataValue
    };
    var url;
    if (this.year) {
        params.year = this.year;
        params.m = this.period;
        url = '/resourcecategory/themes/commonController/getOnlyReportByTime';
    } else {
        url = '/resourcecategory/themes/commonController/getOnlyReportById';
    }
    EsiData.ajaxData(url, params, function (data) {
        this.datas = data;
        if (data) {
            //$(renderDom).html(data.datas);
            $(renderDom).html("<div class='esitablediv'><div class='esimaintablediv'>"+data.datas+"</div><div class='esiclonetablediv'></div></div>")
        }
        if(fn){
        	fn(data);
        }
    });
};
/**
 * 获取分析报表数据
 */
EsiData.prototype.getCustomData = function (renderDom,fn) {
    var params = {
        id: this.dataValue
    };
    var url;
    if (this.year) {
    	params.researchId = this.dataValue;
        params.year = this.year;
        params.period = this.period;
        url = '/resourcecategory/analysis/report/designCustomResearch/getPeriodCustomResearch';
    } else {
        url = '/resourcecategory/themes/commonController/getResearch';
    }
    EsiData.ajaxData(url, params, function (data) {
        this.datas = data;
        if (data) {
            //$(renderDom).html(data.datas);
            $(renderDom).html("<div class='esitablediv'><div class='esimaintablediv'>"+data.datas+"</div><div class='esiclonetablediv'></div></div>")
        }
        if(fn){
        	fn(data);
        }
    });
};
/**
 * 获取分析主题数据
 */
EsiData.prototype.getThemeData = function (renderDom,fn) {
    if (this.year) {
        var params = {
            themeId: this.dataValue,
            year: this.year,
            period: this.period
        };
        EsiData.ajaxData("/support/resourceCategory/analysis/text/queryContentByTime", params, function (data) {
            this.datas = data;
            if (data && data.length) {
                $(renderDom).html(data[0].content);
            }
            if(fn){
            	fn(data);
            }
        });
    }
};
/**
 * 获取文字分析数据
 */
EsiData.prototype.getTextData = function (renderDom,fn) {
    EsiData.ajaxData("/support/resourceCategory/analysis/text/queryContentById", {contentId: this.dataValue}, function (data) {
        this.datas = data;
        if (data) {
            $(renderDom).html(data.content);
        }
        if(fn){
        	fn(data);
        }
    });
};
/**
 * 请求数据
 * @param url 请求路径
 * @param params 参数
 * @param success 成功回调
 */
EsiData.ajaxData = function (url, params, success) {
    //请求表格
    $.ajax({
        url: GLOBAL_PATH + url,
        type: 'GET',
        data: params,
        dataType: 'json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader('x-app', 1);
        },
        success: function (data) {
            success(data);
        },
        error: function (jqXHR) {
            switch (jqXHR.status) {
                case(500):
                    //alert("服务器系统内部错误");
                    break;
                case(401):
                    alert("未登录");
                    break;
                case(403):
                    alert("无权限执行此操作");
                    break;
                case(408):
                    alert("请求超时");
                    break;
                default:
                    //alert("未知错误");
            }
        }
    });
};
