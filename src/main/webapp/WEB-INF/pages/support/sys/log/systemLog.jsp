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
        //公共参数
        var commonParams = {
            themeParams: {
                name: "",
                startDate: "",
                endDate: ""
            }
        };
//        设置查询参数
        function setCommonParams() {
            var sDate = '';
            var sTime = '';
            var eDate = '';
            var eTime = '';
            if (searchStartDate.value) {
                sDate = searchStartDate.value.toDateString();
            }
            if (searchStartTime.value) {
                sTime = searchStartTime.value.toTimeString();
                if (null == searchStartDate.value) {
                    Ext.Msg.alert('提示', '请选择日期');
                    return;
                }
            }
            if (searchEndDate.value) {
                eDate = searchEndDate.value.toDateString();
            }
            if (searchEndTime.value) {
                eTime = searchEndTime.value.toTimeString();
                if (null == searchEndDate.value) {
                    Ext.Msg.alert('提示', '请选择日期');
                    return;
                }
            }
            commonParams.themeParams.startDate = sDate + " " + sTime;
            commonParams.themeParams.endDate = eDate + " " + eTime;
            commonParams.themeParams.name = searchInfoField.value;
            return commonParams.themeParams;
        };
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
                extraParams: commonParams.themeParams,
                api: {
//                    read: contextPath + '/support/sys/log/getSystemLogByOrder'
                    read: contextPath + '/support/sys/log/getSystemLogByCondition'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    totalProperty: 'total',
                    idProperty: 'id',
                }
            },
            autoLoad: true
        });
        // 重新加载参数
        systemLogStore.on('beforeload', function (s) {
            s.getProxy().extraParams = commonParams.themeParams;
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
                renderer: function (value) {
                    return Ext.util.Format.date(new Date(value), 'Y年n月j日 G:i:s')
                }
            }, {
                text: 'IP地址',
                dataIndex: 'ip',
                flex: 1
            }]
        });
//        模糊搜索文本框
        var searchInfoField = Ext.create('Ext.form.field.Text', {
            hideLabel: true,
            width: 200,
            emptyText: '输入用户名或文字信息描述',
            triggerCls: 'x-form-clear-trigger',
            onTriggerClick: function () {
                this.reset();
                commonParams.themeParams.name = '';
                systemLogStore.reload({params: commonParams.themeParams});
            },
//            listeners: {
//                specialkey: function (field, e) {
//                    if (e.getKey() == e.ENTER || e.getKey() == e.TAB) {
//                        commonParams.themeParams.name = searchInfoField.value;
//                        systemLogStore.reload({params: commonParams.themeParams});
//                    }
//                }
//            }
        });

        var searchStartDate = Ext.create('Ext.form.field.Date', {
            name: 'to_date',
            format: 'Y年m月d日'
        });
        var searchEndDate = Ext.create('Ext.form.field.Date', {
            name: 'from_date',
            format: 'Y年m月d日'
        });
        var searchStartTime = Ext.create('Ext.form.field.Time', {
            name: 'in',
            increment: 30,
            format: "G时i分",
            emptyText: '0时00分'
        });
        var searchEndTime = Ext.create('Ext.form.field.Time', {
            name: 'out',
            increment: 30,
            format: "G时i分",
            emptyText: '0时00分'

        });
        var clearButton = Ext.create('Ext.Button', {
            text: '重置',
            handler: function () {
                searchInfoField.reset();
                searchStartDate.reset();
                searchStartTime.reset();
                searchEndDate.reset();
                searchEndTime.reset();
                commonParams.themeParams.name = '';
                commonParams.themeParams.startDate = '';
                commonParams.themeParams.endDate = '';
                systemLogStore.reload({params: commonParams.themeParams});
            }
        });

//        搜索按钮
        var searchButton = Ext.create('Ext.Button', {
            text: '搜索',
            handler: function () {
                systemLogStore.reload({params: setCommonParams()});
            }
        });

//        导出Excel按钮
        var exportButton = Ext.create('Ext.Button', {
            text: '导出Excel',
            handler: function () {
//              获取表头的Text
                var gridColumn = systemLogGrid.initialConfig.columns || []();
                var headText = [];
                Ext.Array.forEach(gridColumn, function (item) {
                    if (item.dataIndex != '') {
                        headText.push(item.text);
                    }
                });
//                获取搜索条件
                var comParas=systemLogStore.getProxy().extraParams;
                Ext.MessageBox.confirm("提示", "是否按搜索条件导出系统日志？", function (btn) {
                    if (btn == 'yes') {
                        window.location.href = contextPath + '/support/sys/log/expertExcel?text=' + headText + '&name='
                                + comParas.name + '&startDate=' + comParas.startDate + '&endDate=' + comParas.endDate
                    }
                })

            }

        });
//        主容器
        var container = Ext.create('Ext.panel.Panel', {
            height: '100%',
            renderTo: 'systemLog',
            layout: 'vbox',
            border: false,
            items: [systemLogGrid],
            tbar: ['-', searchInfoField, '-', '开始时间', searchStartDate, searchStartTime,
                '-', '结束时间', searchEndDate, searchEndTime,'-', searchButton, '-', clearButton, '->', exportButton],
            bbar: ['->', new Ext.PagingToolbar({
                store: systemLogStore,
                border: false,
                displayInfo: true,
                displayMsg: '显示第{0}条 到{1}条记录，一共{2}条',
                emptyMsg: "没有数据"
            })],
            listeners: {
                render: function () {
                    if (indexPanel) {
                        var tabPanel = indexPanel.down('#tabCenter');
                        var myTab = tabPanel.getActiveTab();
                        if (myTab)
                            myTab.myPanel = this;
                    }
                    if (this.hasListener('reDR'))
                        this.un('reDR');
                    this.on('reDR', function (Obj) {
                        if (Obj) {
                            Obj.height = Obj.height - 40;
                            this.updateBox(Obj)
                        }
                    });
                }
            }
        });
    })
</script>
<div id="systemLog" style="width: 100%;height: 100%"></div>
</body>
</html>
