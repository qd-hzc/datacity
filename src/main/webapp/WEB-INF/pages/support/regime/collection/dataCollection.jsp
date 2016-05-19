<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/2/2
  Time: 9:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>${rptTmpName}</title>
    <jsp:include page="../../../common/imp.jsp"/>
    <jsp:include page="../../../common/reportInfoImp.jsp"/>
    <jsp:include page="../../../common/metaDataImp.jsp"/>
    <jsp:include page="../../../common/sysConstant.jsp"/>
    <script src="<%=request.getContextPath()%>/City/support/regime/collection/importExcelWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/support/regime/collection/exportExcelWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/support/regime/collection/rejectReportWin.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/ueditor/themes/iframe.css">
    <style>
        body {
            font-family: sans-serif;
            font-size: 16px;
            min-width: 1000px;
        }

        .x-viewport {
            overflow-x: auto;
        }

        table {
            border-collapse: collapse;
            width: 100%;
            margin: 0 auto;
        }

        .esi td, th {
            border: 1px solid #666;
            padding: 5px 10px;
        }

        .esi-thead tr:first-child td {
            border-top: 1px solid #666;
        }

        .esi-thead tr.firstRow:first-child td {
            border-top: 0;
            border-bottom: 1px solid #666;;
        }

        table.esi tr:last-child td {
            border-bottom: 1px solid #666;
        }

        /*table中的tfoot样式*/
        .esi-tfoot tr:last-child td {
            border-color: #666;
        }

        table.esi input {
            width: 100%;
        }

        /*table.esi {
            width: 100%;
            padding: 20px;

        }
        .esi-thead tr:first-child td {
            border-color: #000;
            font-size: 20px;
            font-weight: 600;
            padding-bottom: 15px;
        }
        table.esi td.real[esi-type="second"] {
            border-top: 0 none;
        }
        table.esi td {
            border: 1px solid #666;
            border-bottom: 0 none;
            border-left: 0 none;
        }
        table.esi thead td {
            border-top: 0 none;
        }
        table.esi td:last-child {
            border-right: 0 none;
        }
        input {
            border: medium none;
            width: 100%;
            height: 100%;
            text-align: right;
            padding: 0px 4%;
            outline: medium none;
        }
        table.esi:last-child tr {
            border-bottom: 1px solid;
        }
        table.esi tr:last-child td {
            border-bottom: 1px solid;
        }*/
    </style>
</head>
<body style="overflow: hidden">
<script>
    var rptInfoId = ${rptInfoId};            // 当前报表id
    var rptStatus = ${rptStatus};            // 当前报表状态
    var rptTmpId = ${rptTmpId};                // 当前报表模板id
    var rptStatusList = ${rptStatusList};   // 报表状态
    var isReview = ${isReview};              //是否为审核状态
    var isReadOnly = ${isReadOnly};          //是否为查看状态
    var year = ${year};                       //当前报表年份
    var month = ${month};                     //当前报表月份
    var isWrite = ${isWrite};                //是否有修改权限
    var isApproval = ${isApproval};         //是否有审核权限
    var rptInfos = ${rptInfos};              //所有报表
    var years = ${years};                    // 所有生成报表的年份
    var period = ${period};                  // 报送频率
    var hasFres = false;
    var fres = null;

    if (isReview) {// 审核状态下当前报表驳回状态时刷新不显示当前报表
        if (rptInfos.length) {
            console.log(rptInfos[0])
            year = rptInfos[0].year;
            month = rptInfos[0].month;
            rptStatus = rptInfos[0].status;
            rptInfoId = rptInfos[0].id;
        } else {
            years = {
                text: year + "年",
                value: year
            }
            fres = {
                text: FREQUENCY_TYPE.getString(period, month),
                value: month
            }
        }
    }
    fres = fres?fres:getFres(year, period);       //生成报告期月、季
    // 获取当前年的报送频率
    function getFres(year, period) {
        var list = new Array();
        $.each(rptInfos, function (i, rptInfo) {
            if (rptInfo.year == year) {
                var isRepeated = false;
                $.each(list, function (i, item) {
                    if (item.value == rptInfo.month) {
                        isRepeated = true;
                        return false;
                    }
                })
                if (!isRepeated) {
                    var item = {
                        text: FREQUENCY_TYPE.getString(period, rptInfo.month),
                        value: rptInfo.month
                    }
                    list.push(item);
                }
            }
        });
        return list;
    }
    // 重新加载月份、季等信息
    function reloadFres(year) {
        fres = getFres(year, period);
        monthField.getStore().loadData(fres);
        var oldValue = monthField.getValue();
        if (fres.length) {
            monthField.setValue(fres[0].value);
            if (oldValue == fres[0].value) {
                return getRptId(year, oldValue);
            }
        } else {
            monthField.clearValue();
            hideAllButton();
        }
        return 0;
    }
    // 获取当前展示报表id
    function getRptId(year, month) {
        var id = null;
        $.each(rptInfos, function (i, rptInfo) {
            if (rptInfo.year == year && rptInfo.month == month) {
                id = rptInfo.id;
                rptStatus = rptInfo.status;
                statusLabel.setText(RPT_STATUS.getString(rptStatus))
                return false;
            }
        })
        return id

    }
    // 保存、提交审核按钮
    function saveOrSubmitRptData(rptStatus, fn, rptId) {
        var inputList = Ext.query('.esi input');
        var ispass = true;
        for (var i = 0; i < inputList.length; i++) {

            if (rptStatus == RPT_STATUS.WAITING_PASS) {//提交审核时验证

            }
            if (inputList[i].value != "") {
                inputList[i].setAttribute("value", $.trim(inputList[i].value).trim());
            } else {
                inputList[i].setAttribute("value", "");
            }
        }
        if (Ext.query('.esi').length) {
            var tableHtml = Ext.query('.esi')[0].outerHTML;
            if (ispass) {
                Ext.Ajax.request({
                    url: GLOBAL_PATH + '/support/regime/collection/saveOrSubmitRptData',
                    waitTitle: '提示',
                    waitMsg: '正在操作...',
                    method: 'POST',
                    timeOut: 15 * 3600,
                    params: {
                        tableHtml: tableHtml,
                        reportId: rptId,
                        rptStatus: rptStatus,
                        collectionType: COLLECTION_TYPE.FILL
                    },
                    success: function (response, opts) {
                        var result = Ext.decode(response.responseText);
                        Ext.Msg.alert("成功", result.msg);
                        if (fn)
                            fn();
                    },
                    failure: function (response, opts) {
                        var result = Ext.decode(response.responseText);
                        Ext.Msg.alert("失败", result.msg);
                    }
                });
            } else {
                Ext.Msg.alert("失败", "审核不通过！");
            }
        } else {
            Ext.Msg.alert("失败", "未获取报表表样！");
        }
    }
    // 审核驳回
    function updateRptStatus(status, fn, rptId, info) {
        info = info ? info : "";
        Ext.Ajax.request({
            url: GLOBAL_PATH + '/support/regime/review/rptReview',
            waitTitle: '提示',
            waitMsg: '正在操作...',
            method: 'POST',
            timeOut: 15 * 3600,
            params: {
                ids: rptId,
                rptStatus: status,
                info: info
            },
            success: function (response, opts) {
                var result = Ext.decode(response.responseText);
                Ext.Msg.alert("成功", result.msg);
                if (fn)
                    fn();
            },
            failure: function (response, opts) {
                var result = Ext.decode(response.responseText);
                Ext.Msg.alert("失败", result.msg);
            }
        });
    }
    var yearField;
    var monthField;
    var statusLabel;
    Ext.onReady(function () {

        var tableForm = new Ext.form.Panel({
            id: 'tableForm',
            width: '100%',
            autoScroll: true,
            padding: 0,
            bodyStyle: 'background:#fff;padding:20px',
            //height: 100,
            flex: 1,
            region: 'center',
            frame: true,
            loader: {
                url: GLOBAL_PATH + '/support/regime/collection/getRptInfoHtml?rptInfoId=' + rptInfoId,
                autoLoad: true,
                loadMask: '正在加载...',
                nocache: true,
                closeAction: "destroy",
                autoScroll: true,
                scripts: true,
                border: false,
                renderer: function (loader, response, active) {
                    var table = Ext.decode(response.responseText).table;
                    loader.getTarget().update(table, true, null);
                    return true;
                },
                nocache: true
            }
        });

        function loadNewRptInfo(id) {
            var url = GLOBAL_PATH + '/support/regime/collection/getRptInfoHtml';
            if (id) {
                url += '?rptInfoId=' + id
                showAllButton();
                hideButton();
            } else {
                hideAllButton();
            }
            Ext.getCmp('tableForm').getLoader().load({
                url: url,
                autoLoad: true,
                loadMask: '正在加载...',
                nocache: true,
                closeAction: "destroy",
                autoScroll: true,
                scripts: true,
                renderer: function (loader, response, active) {
                    var table = Ext.decode(response.responseText).table;
                    if (id) {
                        loader.getTarget().update(table, true, null);
                    } else {
                        loader.getTarget().update("此状态下没有报表！", true, null);
                    }
                    return true;
                },
                nocache: true
            });
        }

        yearField = new Ext.form.ComboBox({
            xtype: 'combobox',
            fieldLabel: '',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: years
            }),
            queryMode: 'local',
            editable: false,
            displayField: 'text',
            valueField: 'value',
            value: year,
            width: 100,
            labelWidth: 30,
            margin: '10 0 10 20',
            listeners: {
                change: function (_this, n, o) {
                    if (n) {
                        var id = reloadFres(n);
                        if (id) {
                            loadNewRptInfo(id);
                            showAllButton();
                            hideButton();
                        }
                    }
                }
            }
        });
        var monthStore = new Ext.data.Store({
            fields: ['text', 'value'],
            data: fres
        });
        monthField = new Ext.form.ComboBox({
            xtype: 'combobox',
            fieldLabel: '',
            store: monthStore,
            queryMode: 'local',
            editable: false,
            displayField: 'text',
            valueField: 'value',
            value: month,
            width: 100,
            labelWidth: 30,
            margin: '10 0 10 20',
            listeners: {
                change: function (_this, n, o) {
                    var year = yearField.getValue()
                    if (year) {
                        var id = getRptId(year, n);
                        loadNewRptInfo(id);
                        showAllButton();
                        hideButton();
                    }
                }
            }
        });

        statusLabel = Ext.create('Ext.form.Label', {
            xtype: 'label',
            width: 60,
            margin: '12 0 10 0',
            text: '待填报'
        });
        statusLabel.setText(RPT_STATUS.getString(rptStatus))
        var viewport = new Ext.container.Viewport({
            width: '100%',
            height: '100%',
            renderTo: 'dataCollectionContainer',
            layout: "border",
            minWidth: 1000,
            items: [{
                xtype: "panel",
                region: 'north',
                layout: 'column',
                align: 'middle',
                border: false,
                items: [yearField, monthField, {
                    xtype: 'label',
                    width: 40,
                    margin: '12 0 10 20',
                    text: '状态: '
                }, statusLabel, {
                    xtype: 'button',
                    name: 'export',
                    width: 80,
                    margin: '10 20 10 20',
                    text: '导出',
                    style: 'float:right',
                    handler: function () {
                        Ext.Msg.show({
                            title: '提示',
                            message: '是否导出当前报表？',
                            buttons: Ext.Msg.YESNOCANCEL,
                            buttonText: {yes: '确定', no: '批量导出', cancel: '取消'},
                            icon: Ext.Msg.QUESTION,
                            fn: function (btn) {
                                if (btn === 'yes') {
                                    var year = yearField.getValue();
                                    var month = monthField.getValue();
                                    if (year && month) {
                                        var rptId = getRptId(year, month);
                                        if (rptId) {
                                            exportToExcel(rptId, true);
                                        } else {
                                            Ext.Msg.alert("提示", "没有可以导出的报表！")
                                        }
                                    } else {
                                        Ext.Msg.alert("提示", "没有可以导出的报表！")
                                    }
                                } else if (btn === 'no') {
                                    if (rptTmpId) {
                                        var win = Ext.exportExcelWin.init(rptInfos, years, period, function (data) {
                                            if (data && data.length) {
                                                batchExportToExcel(data, true);
                                                win.close();
                                            } else {
                                                Ext.Msg.alert("提示", "没有可以导出的报表！")
                                            }
                                        });
                                    } else {
                                        Ext.Msg.alert("提示", "没有可以导出的报表！")
                                    }
                                }
                            }
                        });
                    }
                }, {
                    xtype: 'button',
                    name: 'import',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '导入',
                    style: 'float:right',
                    handler: function () {
                        var year = yearField.getValue();
                        var month = monthField.getValue();
                        if (year && month) {
                            var rptId = getRptId(year, month);
                            if (rptId) {
                                var win = Ext.importExcelWin.init(null, function () {
                                    $.each(rptInfos, function (i, rptInfo) {
                                        if (rptInfo.id == rptId) {
                                            if (rptInfo.status == RPT_STATUS.WAITING_FILL) {
                                                rptInfo.status = RPT_STATUS.DRAFT;
                                                statusLabel.setText(RPT_STATUS.getString(rptInfo.status));
                                                showAllButton();
                                                hideButton(rptInfo.status);
                                            }
                                            return false;
                                        }
                                    });
                                    loadNewRptInfo(rptId);
                                    win.close();
                                }, rptId);
                            } else {
                                Ext.Msg.alert("提示", "没有需要导入的报表！")
                            }
                        } else {
                            Ext.Msg.alert("提示", "没有需要导入的报表！")
                        }
                    }
                }, {
                    xtype: 'button',
                    name: 'reject',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '驳回',
                    style: 'float:right',
                    iconCls: 'Cross',
                    handler: function () {
                        var year = yearField.getValue();
                        var month = monthField.getValue();
                        if (year && month) {
                            var rptId = getRptId(year, month);
                            if (rptId) {
                                console.log(rptId);
                                Ext.rejectReportWin.init(null, function (rec) {
                                    updateRptStatus(RPT_STATUS.REGECT, function () {
                                        $.each(rptInfos, function (i, rptInfo) {
                                            if (rptInfo.id == rptId) {
                                                rptInfo.status = RPT_STATUS.REGECT;
                                                statusLabel.setText(RPT_STATUS.getString(rptInfo.status));
                                                showAllButton();
                                                hideButton(rptInfo.status);
                                                return false;
                                            }
                                        });
                                    }, rptId, rec);
                                });
                            } else {
                                Ext.Msg.alert("提示", "没有可以驳回的报表！")
                            }
                        } else {
                            Ext.Msg.alert("提示", "没有可以驳回的报表！")
                        }
                    }
                }, {
                    xtype: 'button',
                    name: 'pass',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '通过',
                    style: 'float:right',
                    iconCls: 'Tick',
                    handler: function () {
                        var year = yearField.getValue();
                        var month = monthField.getValue();
                        if (year && month) {
                            var rptId = getRptId(year, month);
                            if (rptId) {
                                updateRptStatus(RPT_STATUS.PASS, function () {
                                    $.each(rptInfos, function (i, rptInfo) {
                                        if (rptInfo.id == rptId) {
                                            rptInfo.status = RPT_STATUS.PASS;
                                            statusLabel.setText(RPT_STATUS.getString(rptInfo.status));
                                            showAllButton();
                                            hideButton(rptInfo.status);
                                            return false;
                                        }
                                    });

                                }, rptId);
                            } else {
                                Ext.Msg.alert("提示", "没有可以通过的报表！")
                            }
                        } else {
                            Ext.Msg.alert("提示", "没有可以通过的报表！")
                        }
                    }
                }, {
                    xtype: 'button',
                    name: 'submit',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '提交审核',
                    style: 'float:right',
                    handler: function () {
                        var year = yearField.getValue();
                        var month = monthField.getValue();
                        if (year && month) {
                            var rptId = getRptId(year, month);
                            if (rptId) {
                                saveOrSubmitRptData(RPT_STATUS.WAITING_PASS, function () {
                                    $.each(rptInfos, function (i, rptInfo) {
                                        if (rptInfo.id == rptId) {
                                            rptInfo.status = RPT_STATUS.WAITING_PASS;
                                            statusLabel.setText(RPT_STATUS.getString(rptInfo.status));
                                            showAllButton();
                                            hideButton(rptInfo.status);
                                            return false;
                                        }
                                    });
                                }, rptId);
                            } else {
                                Ext.Msg.alert("提示", "没有可以提交审核的报表！")
                            }
                        } else {
                            Ext.Msg.alert("提示", "没有可以提交审核的报表！")
                        }
                    }
                }, {
                    xtype: 'button',
                    name: 'save',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '保存',
                    style: 'float:right',
                    handler: function () {
                        var year = yearField.getValue();
                        var month = monthField.getValue();
                        if (year && month) {
                            var rptId = getRptId(year, month);
                            if (rptId) {
                                if (rptStatus == RPT_STATUS.REGECT) {
                                    saveOrSubmitRptData(rptStatus, function () {
                                    }, rptId);
                                } else {
                                    saveOrSubmitRptData(RPT_STATUS.DRAFT, function () {
                                        $.each(rptInfos, function (i, rptInfo) {
                                            if (rptInfo.id == rptId) {
                                                rptInfo.status = RPT_STATUS.DRAFT;
                                                statusLabel.setText(RPT_STATUS.getString(rptInfo.status));
                                                showAllButton();
                                                hideButton(rptInfo.status);
                                                return false;
                                            }
                                        });
                                    }, rptId);
                                }
                            } else {
                                Ext.Msg.alert("提示", "没有可以保存的报表！")
                            }
                        } else {
                            Ext.Msg.alert("提示", "没有可以保存的报表！")
                        }
                    }
                }]
            }, tableForm]
        });
        hideButton();
        function hideButton(status) {
            if (status) {
                rptStatus = status;
            }
            if (isReadOnly) {
                viewport.query('*[name=save]')[0].hide();
                viewport.query('*[name=submit]')[0].hide();
                viewport.query('*[name=pass]')[0].hide();
                viewport.query('*[name=reject]')[0].hide();
                viewport.query('*[name=import]')[0].hide();
            } else if (isReview) {
                viewport.query('*[name=save]')[0].hide();
                viewport.query('*[name=submit]')[0].hide();
                viewport.query('*[name=import]')[0].hide();
                if (rptStatus == RPT_STATUS.PASS) {
                    viewport.query('*[name=pass]')[0].hide();
                } else if (rptStatus == RPT_STATUS.REGECT) {
                    viewport.query('*[name=reject]')[0].hide();
                }

            } else {
                if (rptStatus == RPT_STATUS.PASS || rptStatus == RPT_STATUS.WAITING_PASS) {
                    viewport.query('*[name=save]')[0].hide();
                    viewport.query('*[name=submit]')[0].hide();
                    viewport.query('*[name=import]')[0].hide();
                }
                viewport.query('*[name=pass]')[0].hide();
                viewport.query('*[name=reject]')[0].hide();
            }

            if (!isWrite) {
                viewport.query('*[name=save]')[0].hide();
                viewport.query('*[name=submit]')[0].hide();
                viewport.query('*[name=import]')[0].hide();
            }
            if (!isApproval) {
                viewport.query('*[name=pass]')[0].hide();
                viewport.query('*[name=reject]')[0].hide();
            }
        }

        function hideAllButton() {
            viewport.query('*[name=save]')[0].hide();
            viewport.query('*[name=submit]')[0].hide();
            viewport.query('*[name=pass]')[0].hide();
            viewport.query('*[name=reject]')[0].hide();
            viewport.query('*[name=import]')[0].hide();
            viewport.query('*[name=export]')[0].hide();
        }

        function showAllButton() {
            viewport.query('*[name=save]')[0].show();
            viewport.query('*[name=submit]')[0].show();
            viewport.query('*[name=pass]')[0].show();
            viewport.query('*[name=reject]')[0].show();
            viewport.query('*[name=import]')[0].show();
            viewport.query('*[name=export]')[0].show();
        }

    })
    function batchExportToExcel(rptInfoIds, hasData) {
        var obj = {
            rptTmpIds: rptTmpId,
            rptInfoIds: rptInfoIds.join(",")
        }
        var reportList = [];
        reportList.push(obj);
        //发送请求,下载文件
        window.location.href = GLOBAL_PATH + '/support/regime/collection/excel/batchExportToExcel?isOne=' + false + '&hasData=' + hasData + '&reportList=' + JSON.stringify(reportList);
    }
    function exportToExcel(rptId, hasData) {
        var rptInfoIds = [];
        var obj = {
            rptTmpIds: rptTmpId,
            rptInfoIds: rptId
        }
        var reportList = [];
        reportList.push(obj);
        //发送请求,下载文件
        window.location.href = GLOBAL_PATH + '/support/regime/collection/excel/batchExportToExcel?isOne=' + false + '&hasData=' + hasData + '&reportList=' + JSON.stringify(reportList);
    }
</script>
<div id="dataCollectionContainer" style="width:100%;height: 100%;overflow:hidden"></div>
</body>
</html>
