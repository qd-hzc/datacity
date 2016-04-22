<%--
  User: HZC
  Date: 2016/4/5
  报表页面：分析报表，综合表
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
%>

<link href="<%=contextPath%>/Plugins/ueditor/themes/iframe.css" rel="stylesheet">

<style>
    table.esi td {
        border: 1px solid black;
        width: auto !important;
    }

    table.esi input {
        width: 50px !important;
    }

    table.esi input {
        border: medium none;
        text-align: right;
        margin-right: 1%;
    }
</style>

<div data-id="reportdiv1" data-uuid="${uuid}" style="width: 100%;height: 100%;overflow: auto;">

    <div data-id="option" data-uuid="${uuid}" style="height: 40px;width:100%;display: none;">
        <span>请选择：</span>
        <select data-id="select" data-uuid="${uuid}"></select>
        <select data-id="period" data-uuid="${uuid}" style="display: none;"></select>
    </div>

</div>

<script data-id="temp" data-uuid="${uuid}" type="text/html">
    <option value="{id}">{time}</option>
</script>

<script>
    (function () {
        var mine = {};

        mine.uuid = '${uuid}';
        mine.page = ${page};
        mine.contents = mine.page.contents;
        mine.esiTheme = new EsiTheme();
        mine.esiReport = new EsiReport();
        mine.container = $('[data-id=reportdiv1][data-uuid=' + mine.uuid + ']');

//        初始化
        mine.init = function () {

            if (mine.contents && mine.contents.length > 0) {

                var content = mine.contents[0];

                mine.esiTheme.getData(content, function (n) {
                    if (n.success) {

                        if (content.contentType == CONTENT_TYPE.report) {
                            mine.setReport(n.datas);
                        } else if (content.contentType == CONTENT_TYPE.research) {
                            mine.setResearch(n.datas);
                        }

                    } else {
                        $.showError(n.msg, '提示')
                    }
                });
            } else {
                $.showError('页面无数据', '提示');
            }
        };
//        设置综合表
        mine.setReport = function (n) {
            if (n) {

                mine.container.append(n.table);

                var infos = n.reportInfos;
                if (infos && infos.length > 0) {

                    var htmlStr = '';
                    var temp = $('[data-id=temp][data-uuid=' + mine.uuid + ']').html();

                    for (var i = 0; i < infos.length; i++) {
                        var info = infos[i];
                        htmlStr += $.render(temp, info);
                    }
                    $('[data-id=select][data-uuid=' + mine.uuid + ']').html(htmlStr);

                    $('[data-id=option][data-uuid=' + mine.uuid + ']').show();

                    mine.select();
                }
            } else {
                $.showError('综合表无数据', '提示');
            }
        };
//        设置分析报表
        mine.setResearch = function (n) {
            if (n) {

                mine.container.append(n.table);

                var timeRange = n.timeRange.type;
                if (timeRange == 3) {
//                分析报表时间范围：1、连续报告期；2、选择报告期；3、报告期数
//                分析报表期度
//                "periods":[{//频度 frequency:1,//年 year:2015,
//                分析报表期度：年：12，半年：6、12，季：3、6、9、12，月：1、2、3、4、5、6、7、8、9、10、11、12
//                period:List<Integer>}]}
                    mine.periods = n.periods;
//                分析报表
                    mine.research = n.research;

                    if (mine.periods && mine.periods.length > 0) {

                        var years = genYearStore(mine.periods);
                        var temp = $('[data-id=temp][data-uuid=' + mine.uuid + ']').html();
                        var htmlStr = '';

                        for (var j = 0; j < years.length; j++) {
                            var year = years[j];
                            htmlStr += $.render(temp, year);
                        }
                        $('[data-id=select][data-uuid=' + mine.uuid + ']').html(htmlStr);

                        if (mine.research.period != REPORT_PERIOD.year) {

                            var htmlPeriod = '';
                            var ps = genPeriodStore(years[0].id, mine.periods);

                            for (var m = 0; m < ps.length; m++) {
                                var p = ps[m];
                                htmlPeriod += $.render(temp, p);
                            }
                            var periodContainer = $('[data-id=period][data-uuid=' + mine.uuid + ']');
                            periodContainer.html(htmlPeriod);

                            periodContainer.show();

                            mine.selectPeriod();
                        }

                        $('[data-id=option][data-uuid=' + mine.uuid + ']').show();

                        mine.select();
                    }
                }
            } else {
                $.showError('分析报表无数据', '提示');
            }
        };

//        报告期选择变化
        mine.select = function () {

            $('[data-id=select][data-uuid=' + mine.uuid + ']').change(function () {

                var val = $(this).val();
                var content = mine.contents[0];

                mine.container.children('table').remove();

                if (content.contentType == CONTENT_TYPE.report) {

                    mine.esiReport.getReportByPeriod(val, function (n) {

                        if (n.success) {
                            if (n.datas) {
                                mine.container.append(n.datas)
                            } else {
                                $.showError('综合表无数据', '提示');
                            }
                        } else {
                            $.showError(n.msg, '提示');
                        }

                    });

                } else if (content.contentType == CONTENT_TYPE.research) {

                    var pcontainer = $('[data-id=period][data-uuid=' + mine.uuid + ']');

                    if (mine.research.period != REPORT_PERIOD.year) {

                        var temp = $('[data-id=temp][data-uuid=' + mine.uuid + ']').html();
                        var htmlPeriod = '';
                        var ps = genPeriodStore(val, mine.periods);

                        for (var m = 0; m < ps.length; m++) {
                            var p = ps[m];
                            htmlPeriod += $.render(temp, p);
                        }
                        pcontainer.html(htmlPeriod);

                    }

                    var obj = {
                        researchId: mine.research.id,
                        year: val,
                        period: pcontainer.val()
                    };

                    mine.esiReport.getResearchByPeriod(obj, function (n) {

                        if (n.success) {
                            if (n.datas) {
                                mine.container.append(n.datas)
                            } else {
                                $.showError('分析报表无数据', '提示');
                            }
                        } else {
                            $.showError(n.msg, '提示');
                        }

                    });
                }
            });
        };

//        分析报表期度选择变化
        mine.selectPeriod = function () {

            $('[data-id=period][data-uuid=' + mine.uuid + ']').change(function () {

                mine.container.children('table').remove();

                var obj = {
                    researchId: mine.research.id,
                    year: $('[data-id=select][data-uuid=' + mine.uuid + ']').val(),
                    period: $(this).val()
                };

                mine.esiReport.getResearchByPeriod(obj, function (n) {

                    if (n.success) {
                        if (n.datas) {
                            mine.container.append(n.datas)
                        } else {
                            $.showError('分析报表无数据', '提示');
                        }
                    } else {
                        $.showError(n.msg, '提示');
                    }

                });
            });
        }

        $(function () {

            mine.init();

        });

        /**
         * 返回年store
         * @param obj 集合：[{year：2015}]
         * @returns {Array} [{id:2015,time:'2015年'}]
         */
        function genYearStore(obj) {
            var list = [];

            if (obj && obj.length > 0) {

                for (var i = 0; i < obj.length; i++) {

                    var year = genYear(obj[i].year);
                    list.push(year);
                }

            }

            function genYear(n) {
                return {id: n, time: n + '年'}
            }

            return list;
        }

        /**
         * 返回期度store
         * <pre>
         *      根据传入的年，返回该年拥有的期度
         * </pre>
         * @param year 2015
         * @param obj [{frequency:3,year:2015,periods:[1,3,5]}]
         * @returns [{id:3,time:'3月/1季度/上半年'}]
         */
        function genPeriodStore(year, obj) {

            var r, re = [];

            if (obj && obj.length > 0) {

                for (var i = 0; i < obj.length; i++) {
                    var time = obj[i];
                    if (time.year == year) {
                        r = time;
                    }
                }

            }

            if (r) {

                if (r.periods.length > 0) {

                    for (var m = 0; m < r.periods.length; m++) {

                        var period = r.periods[m];

                        switch (r.frequency) {
                            case 1:
                                break;
                            case 2:
                                re.push({id: period, time: period == 6 ? '上半年' : '下半年'});
                                break;
                            case 3:
                                re.push({
                                    id: period,
                                    time: period == 3 ? '1季度' : (period == 6 ? '2季度' : (period == 9 ? '3季度' : '4季度'))
                                });
                                break;
                            case 4:
                                re.push({id: period, time: period + '月'});
                                break;
                        }
                    }
                }
            }

            return re;
        }
    })()
</script>

