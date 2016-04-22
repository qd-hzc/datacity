<%--
  Created by IntelliJ IDEA.
  User: CRX
  Date: 2016/4/18
  Time: 9:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    String contextPath = request.getContextPath();
%>
<html>
<head>
    <title>日志管理</title>
</head>
<body>
<script>
    Ext.onReady(function () {

//系统日志model
        createModel('SystemLog', function () {
            Ext.define('SystemLog', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'operateDate',
                    type: 'int'
                }, {
                    name: 'operaType',
                    type: 'int'
                }, {
                    name: 'rptId',
                    type: 'int'
                }, {
                    name: 'type',
                    type: 'int'
                }, {
                    name: 'userId',
                    type: 'int'
                }, {
                    name: 'sourceId',
                    type: 'int'
                }, {
                    name: 'sourceDate',
                    type: 'int'
                }, {
                    name: 'userType',
                    type: 'int'
                }, {
                    name: 'contents',
                    type: 'string'
                }, {
                    name: 'ip',
                    type: 'string'
                }, {
                    name: 'userName',
                    type: 'string'
                }, {
                    name: 'method',
                    type: 'string'
                }, {
                    name: 'info',
                    type: 'string'
                }]
            });
        });
//        系统日志列表数据源
        var systemLogStore = Ext.create('Ext.data.Store', {
            model: 'SystemLog',
            pageSize: 15,
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/support/sys/log/getSystemLogByOrder'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    totalProperty: 'total',
                    idProperty: 'id'
                }
            },
            autoLoad: true

        });
        var systemLogGrid = Ext.create('Ext.grid.Panel', {
            width: '100%',
            flex: 1,
            border: false,
            store: systemLogStore,
            columns: [new Ext.grid.RowNumberer({
                text: '序号',
                align: 'center',
                flex: .2
            }), {
                text: '用户名',
                dataIndex: 'userName',
                flex: 1
            }, {
                text: '文字信息描述',
                dataIndex: 'info',
                flex: 1,
            }, {
                text: '日期',
                flex: 1,
                dataIndex: 'operateDate',
                renderer: function(value) {
                    return Ext.util.Format.date(new Date(value),'Y年n月j日 G:i:s')
                }
            }, {
                text: '操作方法',
                dataIndex: 'method',
                flex: 1
            }, {
                text: 'IP地址',
                dataIndex: 'ip',
                flex: 1
            }]
        });
        var container = Ext.create('Ext.panel.Panel', {
            height: '100%',
            renderTo: 'systemLog',
            layout: 'vbox',
            border: false,
            items: [systemLogGrid],
            tbar: ['<b>日志列表</b>'],
            bbar: ['->', new Ext.PagingToolbar({
                store: systemLogStore,
                border: false,
                displayInfo: true,
                displayMsg: '显示第{0}条 到{1}条记录，一共{2}条',
                emptyMsg: "没有数据"
            })],
        });
    })
</script>
<div id="systemLog" style="width: 100%;height: 100%"></div>
</body>
</html>
