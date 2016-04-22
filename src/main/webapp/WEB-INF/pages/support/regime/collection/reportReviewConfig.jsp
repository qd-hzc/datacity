<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/2/23
  Time: 11:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>报表审核</title>
    <meta charset="UTF-8"/>
    <jsp:include page="../../../common/reportInfoImp.jsp"/>
    <script>

        //报表类型
        var rptTypes =${rptTypes};
        //报送周期
        var periods =${periods};
        //报表状态
        var rptStatus1 =${rptStatus};
        //报送状态
        var submitStatus =${submitStatus};
        //报送频率
        var yearFres =${yearFres};
        var halfFres =${halfFres};
        var quarterFres =${quarterFres};
        var monthFres =${monthFres};
    </script>
</head>
<body>
<div id="rptReviewContainer" style="width: 100%;height:100%"></div>
<script src="<%=request.getContextPath()%>/City/support/regime/collection/rejectReportWin.js"></script>
<script>
    /**
     * 获取报告期
     * @param data
     * @param year
     * @param month
     */
    function getSubmitDate(data, year, month) {
        var submitDate = "";
        if (month == 12) {
            submitDate = (year + 1) + "年1月"
        } else {
            submitDate = year + "年" + (month + 1) + "月"
        }
        if (data <= 1) {
            submitDate = submitDate + "1日";
        } else {
            submitDate = submitDate + "1日至" + Math.min(data, getLastDay(year, month + 1)) + "日";
        }
        return submitDate;
    }
    function getLastDay(year, month) {
        if (month == 13 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }

        }
    }

    function updateRptStatus1(sel, status, info,fn) {
        var ids = [];
        for (var i = 0; i < sel.length; i++) {
            ids.push(sel[i].id);
        }
        Ext.Ajax.request({
            url: GLOBAL_PATH + '/support/regime/review/rptReview',
            waitTitle: '提示',
            waitMsg: '正在操作...',
            method: 'POST',
            timeOut: 15 * 3600,
            params: {
                ids: ids.join(','),
                rptStatus: status,
                info: info
            },
            success: function (response, opts) {
                var result = Ext.decode(response.responseText);
                Ext.Msg.alert("成功", result.msg);
                fn();
            },
            failure: function (response, opts) {
                var result = Ext.decode(response.responseText);
                Ext.Msg.alert("失败", result.msg);
            }
        });
    }
    Ext.onReady(function () {
        //公共参数
        var commonParams = {
            tmpGroupParams: {
                name: ''
            },
            tmpParams: {
                name: '',
                groupId: null,
                includeGroupChildren: true,
                depId: '',
                includeDownLevel: false,
                rptStatus: rptStatus1[1].value,
                periods: '',
                type: '',
                beginYear: '',
                endYear: ''
            }
        };
        // group model
        createModel('rptGroupModel', function () {
            Ext.define('rptGroupModel', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'parentId',
                    type: 'int'
                }, {
                    name: 'leaf',
                    type: 'boolean'
                }, {
                    name: 'comments',
                    type: 'string'
                }, {
                    name: 'status',
                    type: 'int'
                }, {
                    name: 'sort',
                    type: 'int'
                }]
            });
        });
        // group store
        var rptGroupStore = new Ext.data.TreeStore({
            model: 'rptGroupModel',
            parentIdProperty: 'parentId',
            sorters: 'sort',
            proxy: {
                type: 'ajax',
                url: GLOBAL_PATH + '/support/regime/report/reportGroup/getReportGroups'
            },
            root: {
                id: 0,
                expanded: true,
                name: '报表分组目录'
            }

        });
        // group tree
        var rptGroupTree = new Ext.tree.Panel({
            region: 'west',
            width: 300,
            //flex: 1,
            height: '100%',
            //rootVisible: false,
            displayField: 'name',
            store: rptGroupStore,
            tbar: [' ',{
                xtype: 'textfield',
                width: 150,
                emptyText: '输入名称查询',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.tmpGroupParams.name = n;
                        rptGroupStore.reload({params: commonParams.tmpGroupParams});
                    }
                }
            },'->', {
                xtype: 'checkbox',
                fieldLabel: '包含下级',
                labelWidth: 60,
                labelAlign: 'right',
                checked: true,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.tmpParams.includeGroupChildren = n;
                        rptInfoStore.reload({params: commonParams.tmpParams});
                    }
                }
            },' '],
            listeners: {
                itemclick: function (view, rec) {
                    commonParams.tmpParams.groupId = rec.getId();
                    rptInfoStore.reload({params: commonParams.tmpParams});
                }
            }
        });
        //报表model
        createModel('rptInfo', function () {
            Ext.define('rptInfo', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'time',
                    type: 'string'
                }, {
                    name: 'type',
                    type: 'int'
                }, {
                    name: 'period',
                    type: 'int'
                }, {
                    name: 'rptStatus',
                    type: 'string'
                }, {
                    name: 'submitStatus',
                    type: 'int'
                }, {
                    name: 'dptId',
                    type: 'int'
                }, {
                    name: 'rptStyleId',
                    type: 'int'
                }, {
                    name: 'tmpId',
                    type: 'int'
                }, {
                    name: 'submitDaysDelay',
                    type: 'int'
                },{
                    name: 'year',
                    type: 'int'
                }, {
                    name: 'month',
                    type: 'int'
                }]
            });
        });
        //数据源
        var rptInfoStore = new Ext.data.Store({
            model: 'rptInfo',
            pageSize: 15,
            proxy: {
                type: 'ajax',
                url: GLOBAL_PATH + '/support/regime/collection/queryReportInfos',
                extraParams: commonParams.tmpParams,
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            groupField: 'name',
            autoLoad: true
        });
        // 重新加载参数
        rptInfoStore.on('beforeload', function(s) {
            s.getProxy().extraParams = commonParams.tmpParams;
        });
        var rptInfoMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '通过',
                iconCls: 'Tick',
                handler: function () {
                    var sel = rptInfoGrid.getSelectionModel().getSelection();
                    if (sel.length > 0) {
                        updateRptStatus1(sel, RPT_STATUS.PASS, "",function(){
                            rptInfoStore.reload({params: commonParams.tmpParams});
                        });

                    } else {
                        Ext.Msg.alert('提示', '未选中报表');
                    }

                }
            }, {
                text: '驳回',
                iconCls: 'Cross',
                handler: function () {
                    var sel = rptInfoGrid.getSelectionModel().getSelection();
                    if (sel.length > 0) {
                        Ext.rejectReportWin.init(null, function (rec) {
                            updateRptStatus1(sel, RPT_STATUS.REGECT, rec,function(){
                                rptInfoStore.reload({params: commonParams.tmpParams});
                            });
                        });

                    } else {
                        Ext.Msg.alert('提示', '未选中报表');
                    }

                }
            }]
        });
        //Grid
        var rptInfoGrid = new Ext.grid.Panel({
            region:'center',
            //width: '100%',
            //flex: 1,
            store: rptInfoStore,
            selType: 'checkboxmodel',
            features: [{
                ftype: 'grouping',
                groupHeaderTpl: '表名称:{name} ({rows.length}项)', //print the number of items in the group
                //startCollapsed: true // start all groups collapsed
            }],
            columns: [{
                text: '报表名称',
                dataIndex: 'name',
                flex: 2
            }, {
                text: '时间',
                flex: 1,
                renderer: function (data, m, record) {
                    var period = record.get('period');
                    var year = record.get('year');
                    var month = record.get('month');
                    return getTime(period,year, month);
                }
            }, {
                text: '上报期',
                dataIndex: 'submitDaysDelay',
                flex: 1,
                renderer: function (data, m, record) {
                    var year = record.get('year');
                    var month = record.get('month');
                    var submitDate = getSubmitDate(data, year, month);
                    return submitDate;
                }
            }, {
                text: '报表状态',
                dataIndex: 'rptStatus',
                flex: 0.5,
                renderer: function (data) {
                    for (var i = 0; i < rptStatus1.length; i++) {
                        if (rptStatus1[i].value == data) {
                            return rptStatus1[i].text;
                        }
                    }
                    return '';
                }
            }, {
                text: '报表类型',
                dataIndex: 'type',
                flex: 0.5,
                renderer: function (data) {
                    for (var i = 0; i < rptTypes.length; i++) {
                        if (rptTypes[i].value == data) {
                            return rptTypes[i].text;
                        }
                    }
                    return '';
                }
            }, {
                text: '操作',
                dataIndex: 'rptStatus',
                flex: 0.5,
                renderer: function (data) {
                    if (data == RPT_STATUS.DRAFT || data == RPT_STATUS.REGECT || data == RPT_STATUS.WAITING_FILL) {
                        return '<a style="color:#0000FF">填报</a>';
                    } else {
                        return '<a style="color:#0000FF">查看</a>';
                    }


                }
            }],
            listeners: {
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                    if (rptInfoMenu) {
                        rptInfoMenu.hide();
                    }
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    if (rptInfoMenu) {
                        rptInfoMenu.hide();
                    }
                    var sel = rptInfoGrid.getSelectionModel().getSelection();
                    var isHidden = true;
                    for(var i=0;i<sel.length;i++){
                        if(sel[i].get("rptStatus")!=RPT_STATUS.PASS){
                            isHidden = false;
                            break;
                        }

                    }
                    if(isHidden){
                        rptInfoMenu.items.items[0].hide();
                    }else{
                        rptInfoMenu.items.items[0].show();
                    }
                    //弹出菜单
                    rptInfoMenu.showAt(e.getPoint());
                },
                cellclick: function (_this, td, cellIndex, record) {
                    if (rptInfoMenu) {
                        rptInfoMenu.hide();
                    }
                    if (cellIndex == 6) {//弹出填报窗口
                        var winal1 = open(GLOBAL_PATH + '/support/regime/collection/dataCollection?rptInfoId=' + record.get('id') + '&rptStatus=' + record.get('rptStatus') + '&isReview=' + true);
                        var loop1 = setInterval(function() {
                            if(!winal1){
                                clearInterval(loop1);
                            }
                            if (winal1.closed) {
                                clearInterval(loop1);
                                rptInfoStore.reload({params: commonParams.tmpParams});
                            }
                        }, 1000);
                    }
                },
                containerclick: function () {
                    if (rptInfoMenu) {
                        rptInfoMenu.hide();
                    }
                }
            },
            tbar: {
                xtype: 'panel',
                border: false,
                layout: 'anchor',
                items: [{
                    xtype: 'toolbar',
                    border: false,
                    items: [{
                        xtype: 'querypicker',
                        fieldLabel: '部门',
                        labelWidth: 50,
                        labelAlign: 'right',
                        store: new Ext.data.TreeStore({
                            fields: ['id', 'depName'],
                            proxy: {
                                type: 'ajax',
                                url: GLOBAL_PATH + '/support/sys/dep/queryDepTreeByName'
                            },
                            root: {
                                id: 0,
                                depName: '组织机构',
                                expanded: true
                            },
                            autoLoad: true
                        }),
                        rootVisible: true,
                        displayField: 'depName',
                        valueField: 'id',
                        listeners: {
                            select: function (_this, record, o) {
                                commonParams.tmpParams.depId = record.get('id');
                                rptInfoStore.reload({params: commonParams.tmpParams});
                            }
                        }
                    }, {
                        xtype: 'checkbox',
                        fieldLabel: '包含下级',
                        labelWidth: 60,
                        labelAlign: 'right',
                        listeners: {
                            change: function (_this, n, o) {
                                commonParams.tmpParams.includeDownLevel = n;
                            }
                        }
                    }, {
                        xtype: 'textfield',
                        fieldLabel: '表名',
                        width: 150,
                        labelWidth: 40,
                        labelAlign: 'right',
                        listeners: {
                            change: function (_this, n, o) {
                                commonParams.tmpParams.name = n;
                            }
                        }
                    }, {
                        xtype: 'combobox',
                        fieldLabel: '填报状态',
                        width: 150,
                        labelWidth: 60,
                        labelAlign: 'right',
                        store: new Ext.data.Store({
                            fields: ['value', 'text'],
                            data: rptStatus1
                        }),
                        displayField: 'text',
                        valueField: 'value',
                        value: rptStatus1[1].value,
                        listeners: {
                            change: function (_this, n, o) {
                                commonParams.tmpParams.rptStatus = n;
                                if (n == RPT_STATUS.WAITING_PASS) {
                                    rptInfoGrid.query("*[name=pass]")[0].show();
                                    rptInfoGrid.query("*[name=reject]")[0].show();
                                } else {
                                    rptInfoGrid.query("*[name=pass]")[0].hide();
                                    rptInfoGrid.query("*[name=reject]")[0].hide();
                                }
                                rptInfoStore.reload({params: commonParams.tmpParams});
                            }
                        }
                    }, {
                        xtype: 'button',
                        text: '更多条件',
                        iconCls: 'Basketput',
                        handler: function () {
                            var bar = rptInfoGrid.down('toolbar[name="secondLineToobar"]');
                            if (bar.isHidden()) {
                                bar.show();
                                this.setConfig('iconCls', 'Basketremove');
                            } else {
                                bar.hide();
                                this.setConfig('iconCls', 'Basketput');
                            }
                        }
                    }, '-', {
                        xtype: 'button',
                        text: '查询',
                        iconCls: 'Find',
                        handler: function () {
                            rptInfoStore.reload({params: commonParams.tmpParams});
                        }
                    }, '->', {
                        xtype: 'button',
                        name: 'pass',
                        text: '通过',
                        iconCls: 'Tick',
                        handler: function () {
                            var sel = rptInfoGrid.getSelectionModel().getSelection();
                            if (sel.length > 0) {
                                updateRptStatus1(sel, RPT_STATUS.PASS, "",function(){
                                    rptInfoStore.reload({params: commonParams.tmpParams});
                                });

                            } else {
                                Ext.Msg.alert('提示', '未选中报表');
                            }

                        }
                    }, {
                        xtype: 'button',
                        name: 'reject',
                        text: '驳回',
                        iconCls: 'Cross',
                        handler: function () {
                            var sel = rptInfoGrid.getSelectionModel().getSelection();
                            if (sel.length > 0) {
                                Ext.rejectReportWin.init(null, function (rec) {
                                    updateRptStatus1(sel, RPT_STATUS.REGECT, rec,function(){
                                        rptInfoStore.reload({params: commonParams.tmpParams});
                                    });
                                });

                            } else {
                                Ext.Msg.alert('提示', '未选中报表');
                            }

                        }
                    }]
                }, {
                    xtype: 'toolbar',
                    hidden: 'true',
                    name: 'secondLineToobar',
                    border: false,
                    items: [{
                        xtype: 'combobox',
                        fieldLabel: '报表类型',
                        width: 150,
                        labelWidth: 60,
                        labelAlign: 'right',
                        store: new Ext.data.Store({
                            fields: ['value', 'text'],
                            data: rptTypes
                        }),
                        displayField: 'text',
                        valueField: 'value',
                        listeners: {
                            change: function (_this, n, o) {
                                commonParams.tmpParams.type = n;
                            }
                        }
                    }, {
                        xtype: 'checkboxgroup',
                        fieldLabel: '报送周期',
                        labelWidth: 60,
                        labelAlign: 'right',
                        width: 260,
                        columns: 4,
                        items: [
                            {boxLabel: '年度', name: 'period', inputValue: '1'},
                            {boxLabel: '半年', name: 'period', inputValue: '2'},
                            {boxLabel: '季度', name: 'period', inputValue: '3'},
                            {boxLabel: '月度', name: 'period', inputValue: '4'}
                        ],
                        listeners: {
                            change: function (_this, n, o) {
                                var p = n.period;
                                if (p instanceof Array) {
                                    commonParams.tmpParams.periods = p.join(',');
                                } else {
                                    commonParams.tmpParams.periods = p;
                                }
                            }
                        }
                    }, {
                        xtype: 'combobox',
                        fieldLabel: '开始年',
                        width: 150,
                        labelWidth: 60,
                        labelAlign: 'right',
                        store: new Ext.data.Store({
                            fields: ['value', 'text'],
                            proxy: {
                                type: 'ajax',
                                url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                                extraParams: {
                                    sortType: 1
                                }
                            },
                            autoLoad: true
                        }),
                        displayField: 'text',
                        valueField: 'value',
                        listeners: {
                            change: function (_this, n, o) {
                                commonParams.tmpParams.beginYear = n;
                                rptInfoStore.reload({params: commonParams.tmpParams});
                            }
                        }
                    }, {
                        xtype: 'combobox',
                        fieldLabel: '结束年',
                        width: 150,
                        labelWidth: 60,
                        labelAlign: 'right',
                        store: new Ext.data.Store({
                            fields: ['value', 'text'],
                            proxy: {
                                type: 'ajax',
                                url: GLOBAL_PATH + '/support/manage/metadata/getAllYears',
                                extraParams: {
                                    sortType: -1,
                                    beginItem: 1
                                }
                            },
                            autoLoad: true
                        }),
                        displayField: 'text',
                        valueField: 'value',
                        listeners: {
                            change: function (_this, n, o) {
                                commonParams.tmpParams.endYear = n;
                                rptInfoStore.reload({params: commonParams.tmpParams});
                            }
                        }
                    }]
                }]
            },
            bbar: {
                xtype: 'pagingtoolbar',
                store: rptInfoStore,
                displayInfo: true
            }
        });
        //
        new Ext.panel.Panel({
            width: '100%',
            height: '100%',
            layout: 'border',
            border: false,
            renderTo: 'rptReviewContainer',
            items: [rptGroupTree,rptInfoGrid],
            listeners: {
                render: function () {//初始化，监听窗口resize事件
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
        //时间
        function getTime(period, year, month) {
            var arr = [];
            if (period == 1) {
                arr = [];
            } else if (period == 2) {//半年
                arr = halfFres;
            } else if (period == 3) {//季度
                arr = quarterFres;
            } else {//月
                arr = monthFres;
            }
            //迭代,获取中文
            var monthStr = '';
            for (var i = 0; i < arr.length; i++) {
                if (month == arr[i].value) {
                    monthStr = arr[i].text;
                }
            }
            return year + '年' + monthStr;
        }
    });
</script>
</body>
</html>
