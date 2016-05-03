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
    <title>数据采集</title>
    <jsp:include page="../../../common/imp.jsp"/>
    <jsp:include page="../../../common/reportInfoImp.jsp"/>
    <jsp:include page="../../../common/metaDataImp.jsp"/>
    <jsp:include page="../../../common/sysConstant.jsp"/>
    <script src="<%=request.getContextPath()%>/City/support/regime/collection/importExcelWin.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/ueditor/themes/iframe.css">
    <style>
        body {
            font-family: sans-serif;
            font-size: 16px;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin: 0 auto;
        }

        td, th {
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
            width:100%;
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
    var rptInfoId = ${rptInfoId};
    var rptStatus= ${rptStatus}
    var isReview = ${isReview};
    var year = ${year};
    var month = ${month};
    var isWrite =${isWrite};
    var isApproval =${isApproval};
    if(isReview){
        document.title = "报表审核"
    }else{
        document.title = "数据填报"
    }

    function saveOrSubmitRptData(rptStatus,fn) {
        var inputList = Ext.query('input');
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
        var tableHtml = Ext.query('.esi')[0].outerHTML;
        if(ispass){
            Ext.Ajax.request({
                url: GLOBAL_PATH + '/support/regime/collection/saveOrSubmitRptData',
                waitTitle: '提示',
                waitMsg: '正在操作...',
                method: 'POST',
                timeOut: 15 * 3600,
                params: {
                    tableHtml: tableHtml,
                    reportId: rptInfoId,
                    rptStatus: rptStatus,
                    collectionType:COLLECTION_TYPE.FILL
                },
                success: function (response, opts) {
                    var result = Ext.decode(response.responseText);
                    Ext.Msg.alert("成功", result.msg);
                    if(fn)
                    fn();
                },
                failure: function (response, opts) {
                    var result = Ext.decode(response.responseText);
                    Ext.Msg.alert("失败", result.msg);
                }
            });
        }else{
            Ext.Msg.alert("失败", "审核不通过！");
        }

    }
    function updateRptStatus(status,fn) {
        Ext.Ajax.request({
            url: GLOBAL_PATH + '/support/regime/review/rptReview',
            waitTitle: '提示',
            waitMsg: '正在操作...',
            method: 'POST',
            timeOut: 15 * 3600,
            params: {
                ids: rptInfoId,
                rptStatus: status,
                info:""
            },
            success: function (response, opts) {
                var result = Ext.decode(response.responseText);
                Ext.Msg.alert("成功", result.msg);
                if(fn)
                fn();
            },
            failure: function (response, opts) {
                var result = Ext.decode(response.responseText);
                Ext.Msg.alert("失败", result.msg);
            }
        });
    }
    Ext.onReady(function () {

        var tableForm = new Ext.form.Panel({
            id: 'tableForm',
            width: '100%',
            autoScroll: true,
            //padding:'20px',
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
                renderer: function (loader, response, active) {
                    var table = Ext.decode(response.responseText).table;
                    loader.getTarget().update(table, true, null);
                    return true;
                },
                nocache: true
            }
        });
        var viewport = new Ext.container.Viewport({
            width: '100%',
            height: '100%',
            renderTo: 'dataCollectionContainer',
            layout: "border",
            items: [{
                xtype: "panel",
                region: 'north',
                items: [{
                    xtype: 'button',
                    name:'save',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '保存',
                    handler: function () {
                        if(rptStatus == RPT_STATUS.REGECT){
                            saveOrSubmitRptData(rptStatus,function(){
                            });
                        }else{
                            saveOrSubmitRptData(RPT_STATUS.DRAFT,function(){
                            });
                        }
                    }
                }, {
                    xtype: 'button',
                    name:'submit',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '提交审核',
                    handler: function () {
                        saveOrSubmitRptData(RPT_STATUS.WAITING_PASS,function(){
                            window.close();
                        });

                    }
                },{
                    xtype: 'button',
                    name:'pass',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '通过',
                    iconCls:'Tick',
                    handler: function () {
                        updateRptStatus(RPT_STATUS.PASS,function(){
                            window.close();
                        });
                    }
                }, {
                    xtype: 'button',
                    name:'reject',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '驳回',
                    iconCls:'Cross',
                    handler: function () {
                        updateRptStatus(RPT_STATUS.REGECT,function(){
                            window.close();
                        });
                    }
                },{
                    xtype: 'button',
                    name:'import',
                    width: 80,
                    margin: '10 0 10 20',
                    text: '导入',
                    handler: function () {
                        var win = Ext.importExcelWin.init(null, function () {
                            Ext.getCmp('tableForm').getLoader().load({
                                url: GLOBAL_PATH + '/support/regime/collection/getRptInfoHtml?rptInfoId=' + rptInfoId,
                                autoLoad: true,
                                loadMask: '正在加载...',
                                nocache: true,
                                closeAction: "destroy",
                                autoScroll: true,
                                scripts: true,
                                renderer: function (loader, response, active) {
                                    var table = Ext.decode(response.responseText).table;
                                    loader.getTarget().update(table, true, null);
                                    return true;
                                },
                                nocache: true
                            });
                            win.close();
                        },rptInfoId);
                    }
                }]
            }, tableForm]
        });
        if(isReview==true){
            viewport.query('*[name=save]')[0].hide();
            viewport.query('*[name=submit]')[0].hide();
            viewport.query('*[name=import]')[0].hide();
        }else{
            viewport.query('*[name=pass]')[0].hide();
            viewport.query('*[name=reject]')[0].hide();
        }
        if(rptStatus == RPT_STATUS.PASS){
            viewport.query('*[name=save]')[0].hide();
            viewport.query('*[name=submit]')[0].hide();
            viewport.query('*[name=pass]')[0].hide();
            viewport.query('*[name=reject]')[0].hide();
            viewport.query('*[name=import]')[0].hide();
        }
        if(!isWrite){
            viewport.query('*[name=save]')[0].hide();
            viewport.query('*[name=submit]')[0].hide();
            viewport.query('*[name=import]')[0].hide();
        }
        if(!isApproval){
            viewport.query('*[name=pass]')[0].hide();
            viewport.query('*[name=reject]')[0].hide();
        }
    })
</script>
<div id="dataCollectionContainer" style="width:100%;height: 100%;overflow:hidden"></div>
</body>
</html>
