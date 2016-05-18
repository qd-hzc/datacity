<%--
  Created by IntelliJ IDEA.
  User: HZC
  Date: 2016/3/7
  Time: 14:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
%>
<html>
<head>
    <title>自定义查询预览</title>
    <jsp:include page="../../../common/imp.jsp"/>
    <jsp:include page="../../../common/metaDataImp.jsp"/>
    <jsp:include page="../../../common/sysConstant.jsp"/>
    <link rel="stylesheet" href="<%=contextPath%>/Plugins/ueditor/themes/iframe.css">
    <style>
        body {
            font-family: sans-serif;
            font-size: 16px;
        }

        table {
            border-collapse: collapse;
            margin: 0 auto;
        }

        .esi td, th {
            border: 1px solid black;
            padding: 5px 10px;
        }

        .esi-thead tr:first-child td {
            border-top: 1px solid;
        }
    </style>
</head>
<body>
<script src="<%=request.getContextPath()%>/City/support/regime/report/design/addAllPeriodsWin.js"></script>
<script>
    var contextPath = '<%=contextPath%>';
    var MARGIN_4_8 = '8 8';
    //  自定义查询
    var research = ${research};
    //    表格
    var table = '${table}';
    //    时间范围
    var timeRange = ${timeRange};
    Ext.onReady(function () {
        //    时间
        var periods = ${periods};

        /**
         * 返回年store
         * @param obj 集合：[{year：2015}]
         * @returns {Array} [{code:2015,name:'2015年'}]
         */
        function genYearStore(obj) {
            var list = [];
            if (obj && obj.length > 0) {
                for (var i = 0; i < obj.length; i++) {
                    var time = obj[i];
                    var year = genYear(time.year);
                    list.push(year);
                }
            }
            function genYear(n) {
                return {code: n, name: n + '年'}
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
         * @returns [{id:3,name:'3月/1季度/上半年'}]
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
                                re.push({id: period, name: period == 6 ? '上半年' : '下半年'});
                                break;
                            case 3:
                                re.push({
                                    id: period,
                                    name: period == 3 ? '1季度' : (period == 6 ? '2季度' : (period == 9 ? '3季度' : '4季度'))
                                });
                                break;
                            case 4:
                                re.push({id: period, name: period + '月'});
                                break;
                        }
                    }
                }
            }
            return re;
        }

        var time = periods.length > 0 ? periods[0] : null;

        var yearStore = Ext.create('Ext.data.Store', {
            fields: ['code', 'name']
        });
        var periodStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name']
        });
        var yearCombobox = Ext.create('Ext.form.ComboBox', {
            name: 'year',
            store: yearStore,
            valueField: 'code',
            displayField: 'name',
            queryMode: 'local',
            width: 100,
            margin: MARGIN_4_8,
            listeners: {
                select: function (_this, record) {
                    var newValue = record.get('code');
                    try {
                        var store = genPeriodStore(newValue, periods);
                        periodStore.loadData(store);
                        periodCombobox.setValue(store[0].id);
                    } catch (e) {
                    }
                    Ext.Ajax.request({
                        url: contextPath + '/resourcecategory/analysis/report/designCustomResearch/getPeriodCustomResearch',
                        params: {
                            researchId: research.id,
                            year: newValue,
                            period: periodCombobox.getValue()
                        },
                        success: function (response, opts) {
                            var obj = Ext.decode(response.responseText);
                            if (obj.success) {
                                researchHtml.update(obj.datas);
                            } else {
                                Ext.Msg.alert('提示', '系统繁忙，稍后再试');
                            }
                        },
                        failure: function (response, opts) {
                            Ext.Msg.alert('提示', '系统繁忙，稍后再试');
                        }
                    });
                },
                afterrender: function (_this) {
                    var years = genYearStore(periods);
                    yearStore.loadData(years);
                }
            }
        });
        var periodCombobox = Ext.create('Ext.form.ComboBox', {
            name: 'period',
            store: periodStore,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            width: 100,
            margin: MARGIN_4_8,
            listeners: {
                select: function (_this, record) {
                    var newValue = record.get('id');
                    Ext.Ajax.request({
                        url: contextPath + '/resourcecategory/analysis/report/designCustomResearch/getPeriodCustomResearch',
                        params: {
                            researchId: research.id,
                            year: yearCombobox.getValue(),
                            period: newValue
                        },
                        success: function (response, opts) {
                            var obj = Ext.decode(response.responseText);
                            if (obj.success) {
                                researchHtml.update(obj.datas);
                            } else {
                                Ext.Msg.alert('提示', '系统繁忙，稍后再试');
                            }
                        },
                        failure: function (response, opts) {
                            Ext.Msg.alert('提示', '系统繁忙，稍后再试');
                        }
                    });
                }
            }
        });
        var formExport = Ext.create('Ext.Button', {
            text: '报表导出',
            margin: MARGIN_4_8,
            handler: function () {
                Ext.MessageBox.confirm("提示", "是否导出该表？", function (btn) {
                    if (btn == 'yes') {
                        var list = [];
                        list.push(research);
                        var url = GLOBAL_PATH + '/resourcecategory/analysis/report/designCustomResearch/checkExportToExcel?research='
                                + JSON.stringify(list);

                        var yearValue = yearCombobox.getValue();
                        var periodValue = periodCombobox.getValue();
                        var periodRawValue = periodCombobox.getRawValue();
                        var year = '';
                        var month = '';
//                        判断时间选择器是否有值
                        if (null != yearValue && '' != yearValue) {
                            year = yearValue;
                        }
                        if (null != periodValue && '' != periodValue) {
                            month = periodValue;
                        }
                        var obj = {
                            year: year,
                            month: month,
                            name: periodRawValue
                        }
                        if (year != '') {
                            var yearArray = new Array();//年
                            yearArray.push(obj);
                            url += '&yearArray=' + JSON.stringify(yearArray);
                        }
                        window.location.href = url;
                    }
                })
            }
        });
        var batchExport = Ext.create('Ext.Button', {
            text: '批量导出',
            margin: MARGIN_4_8,
            handler: function () {
                var rsch = [];
                rsch.push(research);
                    var lists = [];
                    for (var i = 0; i < periods.length; i++) {
                        var year = periods[i].year;
                        var period = genPeriodStore(year, periods);
                        if (period.length == 0) {
                            period = [{id: '', name: '全年'}]
                        }
                        var obj = {'year': year, 'period': period};
                        lists.push(obj)
                    }
                    Ext.addAllPeriodsWin.init(rsch, lists, function (rec) {
                    });
            }
        });
//        时间选择器
        var timeRangeContainer = Ext.create('Ext.panel.Panel', {
            height: '40',
//            region: 'north',
            border: false,
            layout: 'vbox',
            columnWidth: .2,
            items: [
                {
                    xtype: 'panel',
                    layout: 'hbox',
                    border: false,
                    margin: '0 10 10 10',
                    items: [{
                        xtype: 'displayfield',
                        value: '请选择',
                        margin: MARGIN_4_8,
                    },
                        yearCombobox,
                        periodCombobox,
                    ]
                },

            ],
            listeners: {
                afterrender: function (_this, eOpts) {
                    if (timeRange.type != 3 || null == time) {
                        _this.hide();
                        batchExport.hide();
                        return;
                    }
                    var period = time.periods[0];
                    var year = time.year;
                    switch (research.period) {
                        case 1:
                            periodCombobox.hide();
                            yearCombobox.setValue(year);
                            break;
                        default:
                            periodStore.loadData(genPeriodStore(year, periods));
                            periodCombobox.setValue(period);
                            yearCombobox.setValue(year);
                            break;
                    }
                }
            }
        });
        var exportContainer = Ext.create('Ext.panel.Panel', {
            height: '40',
//            region: 'south',
            border: false,
            layout: 'vbox',
            columnWidth: .2,
            hidden: table == '无数据',
            items: [{
                xtype: 'panel',
                layout: 'hbox',
                border: false,
                margin: '0 10 10 10',
                items: [
                    {
                        xtype: 'displayfield',
                        border: false,
//                            columnWidth: .52
                    },
                    formExport,
                    batchExport]
            }]
        });
        var topContainer = Ext.create('Ext.panel.Panel', {
            height: '40',
            region: 'north',
            border: false,
            layout: 'column',
            items: [
                timeRangeContainer,
//                {
//                    xtype: 'displayfield',
//                    border: false,
//                            columnWidth: .6
//                },

                exportContainer
            ]
        });
        var researchHtml = Ext.create('Ext.panel.Panel', {
            region: 'center',
            scrollable: true,
            listeners: {
                afterrender: function () {
                    this.update(table);
                }
            }
        })
//        主容器
        var container = Ext.create('Ext.panel.Panel', {
            height: '100%',
            renderTo: Ext.getBody(),
            layout: 'border',
            items: [
                topContainer,
                researchHtml
            ]
        });
    });

</script>
</body>
</html>
