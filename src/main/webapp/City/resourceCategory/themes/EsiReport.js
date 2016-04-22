/**
 * 获取综合表、自定义表方法
 * Created by HZC on 2016/3/23.
 */
function EsiReport() {
    this._path = '/' + window.location.pathname.split('/')[1];
}
EsiReport.prototype._ajax = function (url, id, fn) {
    $.ajax({
        type: 'post',
        url: this._path + url,
        data: {id: id},
        dataType: 'json',
        success: function (d) {
            fn(d)
        },
        error: function (e) {
            var r = {success: false, code: 500, msg: "请求失败", datas: e};
            fn(r);
        }
    });
}
/**
 * 返回系统年份
 */
EsiReport.prototype.getYear = function (fn) {
    this._ajax('/resourcecategoryanalysis/report/designCustomResearch/getYearStore', fn);
}
/**
 * 返回半年
 */
EsiReport.prototype.getHalfYear = function (fn) {
    var r = [{'code': '6', 'name': '上半年'}, {'code': '12', 'name': '下半年'}];
    fn(r);
}
/**
 * 返回季度
 */
EsiReport.prototype.getQuarter = function (fn) {
    var r = [{'code': '3', 'name': '一季度'}, {'code': '6', 'name': '二季度'}, {'code': '9', 'name': '三季度'}, {
        'code': '12',
        'name': '四季度'
    }];
    fn(r);
}
/**
 * 返回月份
 */
EsiReport.prototype.getMonth = function (fn) {
    var r = [{'code': '1', 'name': '1月'}, {'code': '2', 'name': '2月'}, {'code': '3', 'name': '3月'}, {
        'code': '4',
        'name': '4月'
    }, {'code': '5', 'name': '5月'}, {'code': '6', 'name': '6月'}, {'code': '7', 'name': '7月'}, {
        'code': '8',
        'name': '8月'
    }, {'code': '9', 'name': '9月'}, {'code': '10', 'name': '10月'}, {'code': '11', 'name': '11月'}, {
        'code': '12',
        'name': '12月'
    }];
    fn(r);
}
/**
 * 返回综合表的报表和所有报告期
 * <pre>
 *    返回综合表的最新报告期报表和所有报告期
 *    返回结果：
 *   {success:true,code:200,msg:"请求成功",datas:datas}
 *   datas:{
 *      "table":"<table></table>",
 *      //报告期
 *      "reportInfos":[
 *          {
 *              id:143,//id：唯一主键
 *              name:'俩',
 *              time:'2016年3月',
 *              type:1,
 *              rptStatus:'4',
 *              submitStatus:0,
 *              dptId:121,
 *              rptStyleId:221,
 *              tmpId:221,
 *              submitDaysDelay:30
 *          }
 *      ]
 *  }
 * </pre>
 */
EsiReport.prototype.getReport = function (id, fn) {
    if (!id || parseInt(id) < 1)return {success: false, code: 500, msg: "请求失败", datas: '参数格式错误'};
    this._ajax('/resourcecategory/themes/commonController/returnReports', id, fn);
}
/**
 * 返回综合表一个报告期的报表
 * <pre>
 *   返回某一个报告期的报表
 *   返回结果：
 *   {success:true,code:200,msg:"请求成功",datas:datas}
 *   datas:<table></table>
 * </pre>
 */
EsiReport.prototype.getReportByPeriod = function (id, fn) {
    if (!id || parseInt(id) < 1)return {success: false, code: 500, msg: "请求失败", datas: '参数格式错误'};
    this._ajax('/resourcecategory/themes/commonController/returnReportByReportInfo', id, fn);
}
/**
 * 返回自定义表
 * <pre>
 *     1、非报告期数
 *     2、报告期数
 *     返回结果：{success:true,code:200,msg:"请求成功",datas:datas}
 *       datas:{
 *           //自定义查询bean信息
 *           "research":{
 *               "id":48,//id
 *               "researchGroupId":2,//自定义查询分组id
 *               "name":"aa",//名称
 *               "dataSet":{//数据源
 *                   "id":21,//数据源id
 *                   "name":"ceshi",//数据源名称
 *                   "comments":"test"//数据源说明
 *               },
 *               "type":1,//自定义查询类型
 *               "period":1//时间频度：年报，半年报，季报，月报
 *           },
 *           //时间范围：查询报表是时间范围是否为报告期数:1:连续时间范围，2：选择时间范围，3：报告期数
 *           "timeRange":{
 *               "id": 2039,//id
 *               "foreignId": 48,//自定义查询id或图表id
 *               "foreignType": 1,//关联类型：1：报表类型，2：图表类型
 *               "type": 3,//时间范围类型：1:连续时间范围，2：选择时间范围，3：报告期数
 *               "dataType": 1,//时间类型：开始年：1，开始期度：2，结束年：3，结束期度：4，年份：5，期度：6，报告期数：7
 *               "dataValue": 2010,//时间类型值
 *               "status": 1//状态
 *           },
 *           //自定义查询表
 *           "table":"<table></table>",
 *           //分析报表的有效报告期
 *           "periods":[
 *              {
 *                   //频度
 *                   frequency:3,
 *                   //年
 *                   year:2015,
 *                   // 分析报表期度：年：12，半年：6、12，季：3、6、9、12，月：1、2、3、4、5、6、7、8、9、10、11、12
 *                   period:List<Integer>
 *              }
 *           ]
 *       }
 * </pre>
 */
EsiReport.prototype.getResearch = function (id, fn) {
    if (!id || parseInt(id) < 1)return {success: false, code: 500, msg: "请求失败", datas: '参数格式错误'};
    this._ajax('/resourcecategory/themes/commonController/returnResearchs', id, fn);
}
/**
 * 返回自定义查询一个报告期的报表
 * <pre>
 *   返回某一个报告期的报表
 *   参数：{researchId:1,frequency:1,year:2016,period:1}
 *   返回结果：
 *   {success:true,code:200,msg:"请求成功",datas:datas}
 *   datas:<table></table>
 * </pre>
 */
EsiReport.prototype.getResearchByPeriod = function (options, fn) {
    if (typeof options === 'object') {
        var researchId = ('researchId' in options) ? options.researchId : null;
        var year = ('year'in options) ? options.year : null;
        var period = ('period' in options) ? options.period : null;
        $.ajax({
            type: 'post',
            url: this._path + '/resourcecategory/analysis/report/designCustomResearch/getPeriodCustomResearch',
            data: {
                researchId: researchId,
                year: year,
                period: period
            },
            dataType: 'json',
            success: function (d) {
                fn(d)
            },
            error: function (e) {
                var r = {success: false, code: 500, msg: "请求失败", datas: e};
                fn(r);
            }
        });
    } else {
        var r = {success: false, code: 500, msg: "请求失败", datas: '参数格式错误'};
        fn(r);
    }
}
