<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2016/2/22
  Time: 14:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/background/fillAnalysisChartGroup.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/background/fillAnalysisChartBase.js"></script>
    <script type="text/javascript">
        var chartBaseModel = createModel("chartBaseModel", function () {
            Ext.define('chartBaseModel', {
                extend: 'Ext.data.Model',
                idProperty: 'id',
                fields: [
                    {name: 'id', type: 'int'},
                    {name: 'title', type: 'string'},
                    {name: 'subTitle', type: 'string'},
                    {name: 'chartType', type: 'int'},
                    {name: 'periodType', type: 'int'},
                    {name: 'timeline', type: 'string'},
                    {name: 'plug', type: 'string'},
                    {name: 'chartStyle', type: 'string'},
                    {name: 'groupId', type: 'int'},
                    {name: 'chartSort', type: 'int'},
                ]
            });
        });
        var chartBaseStore = new Ext.data.Store({
            model: 'chartBaseModel',
//            sorters: 'chartSort',
            autoLoad: false,
            proxy: {
                type: 'ajax',
                actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryAnalysisChartBaseByGroupId',
                api: {
                    create: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                    update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                    destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delAnalysisChartBase'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            }

        });

        var chartBaseGrid = new Ext.grid.Panel({
            region: 'center',
            store: chartBaseStore,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop'
                },
                listeners: {
                    beforedrop: function () {
                        var rec = null;
                        for (var i = 0; i < chartBaseStore.getCount(); i++) {
                            rec = chartBaseStore.getAt(i);
                            if (rec) {
                                rec.set('chartSort', i);
                            }
                        }
                        chartBaseStore.sync();
                    }
                }
            },
            tbar: ['->', {
                type: 'button',
                text: '添加分析图表',
                handler: function () {

                    var groupId = chartBaseStore.groupId;
                    if (groupId) {
                        var win = Ext.fillAnalysisChartBase.init(function (data) {
                            data.chartSort = chartBaseStore.data.length;
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                                method: 'POST',
                                jsonData: data,
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    if (result.success) {
                                        chartBaseStore.add(result.datas[0]);
                                    }
                                    Ext.Msg.alert("提示", result.msg);
                                }
                            })
                        }, groupId);
                    } else {
                        Ext.Msg.alert('提示', "请选择分组！");
                    }
                }
            }, {
                type: 'button',
                text: '修改分析图表',
                handler: function () {
                    var selModel = chartBaseGrid.getSelectionModel();
                    var selected = selModel.getSelection()[0];
                    var groupId = chartBaseStore.groupId;
                    if (selected) {
                        var win = Ext.fillAnalysisChartBase.init(function (data) {
                            selected.set(data);
                            chartBaseStore.sync({
                                failure: function () {
                                    chartBaseStore.reload();
                                }
                            });
                        }, groupId, selected);
                    } else {
                        Ext.Msg.alert('提示', "请选择图表！");
                    }

                }
            }, {
                type: 'button',
                text: '删除分析图表',
                handler: function () {
                    var selModel = chartBaseGrid.getSelectionModel();
                    var selected = selModel.getSelection();
                    var groupId = chartBaseStore.groupId;
                    if (selected && selected.length > 0) {
                        Ext.Msg.confirm('警告', '确定删除选中的图表？', function (btn) {
                            if ('yes' == btn) {
                                chartBaseStore.remove(selected);
                                chartBaseStore.sync();
                            }
                        });
                    } else {
                        Ext.Msg.alert('提示', "请选择图表！");
                    }
                }
            }],
            columns: [
                {text: '标题', dataIndex: 'title', flex: 0.3},
                {text: '副标题', dataIndex: 'subTitle', flex: 0.3},
                {
                    text: '图表类型', dataIndex: 'chartType', flex: 0.2, renderer: function (value) {
                    return ANALYSISCHART_TYPE.getCH(value);
                }
                },
                {
                    text: '周期', dataIndex: 'periodType', flex: 0.1, renderer: function (value) {
                    return PERIOD_TYPE.getCH(value);
                }
                },
                {text: '时间轴', dataIndex: 'timeline', flex: 0.1},
                {text: '插件', dataIndex: 'plug', flex: 0.1},
                {text: '样式', dataIndex: 'chartStyle', flex: 0.1},
                {
                    text: '操作', flex: 0.1, renderer: function () {
                    return '<a style="color:#0000FF">设计</a>';
                }
                }
            ],
            height: '100%',
            width: '80%',
            listeners: {
                cellclick: function (_this, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                    if (cellIndex == 7) {
                        open(GLOBAL_PATH + '/support/resourcecategory/analysis/chart/chartDesign?chartId=' + record.get('id'));
                    }

                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                }
            }
        });

        var chartGroupModel = createModel("chartGroupModel", function () {
            Ext.define('chartGroupModel', {
                extend: 'Ext.data.TreeModel',
                idProperty: 'id',
//                idgen: 'nullid',
                fields: [
                    {name: 'id', type: 'int'},
                    {name: 'name', type: 'string'},
                    {name: 'text', type: 'string'},
                    {name: 'groupSort', type: 'int'},
                    {name: 'pId', type: 'int'}
                ]
            });
        });

        var chartGroupStore = new Ext.data.TreeStore({
            model: 'chartGroupModel',
            parentIdProperty: 'pId',
            sorters: 'groupSort',
            proxy: {
                type: 'ajax',
                actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryAllAnalysisChartGroup',
                api: {
                    create: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartGroup',
                    update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartGroup',
                    destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delAnalysisChartGroup'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            root: {
                id: 0,
                expanded: true
            },
            listeners: {
                /*endupdate:function(e){
                    if(chartGroupStore)
                    chartGroupStore.reload();
                }*/
                write: function (store, operate, callback) {
                    console.log(operate);
                    //Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }

        });

        var chartGroupTree = new Ext.tree.Panel({
            region: 'west',
            width: 300,
            height: '100%',
            rootVisible: false,
            displayField: 'name',
            store: chartGroupStore,
            tbar: ['->', {
                type: 'button',
                text: '添加分组',
                handler: function () {
                    var rec = chartGroupTree.getSelection();
                    if (rec && rec.length > 0) {
                        var selected = rec[0];
                        var win = Ext.fillAnalysisChartGroup.init(function (data) {
                            if (data.level == '0') {
                                data.pId = selected.parentNode.getId();
                            } else {
                                data.pId = selected.getId();
                            }
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartGroup',
                                method: 'POST',
                                jsonData: data,
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    if (result.datas) {
                                        if (data.level == '0') {
                                            result.datas[0].loaded = true;
                                            result.datas[0].leaf = true;
                                            selected.parentNode.appendChild(result.datas[0]);
                                        } else {
                                            result.datas[0].loaded = true;
                                            result.datas[0].leaf = true;
                                            selected.appendChild(result.datas[0]);
                                        }
                                    }
                                    Ext.Msg.alert("提示", result.msg);
                                }
                            })
                            win.close();
                        });
                    } else {
                        //TODO未选择分组
                        var selected = rec[0];
                        var win = Ext.fillAnalysisChartGroup.init(function (data) {
                            data.pId = 0;
                            selected = chartGroupTree.getRootNode();
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartGroup',
                                method: 'POST',
                                jsonData: data,
                                success: function (response, opts) {

                                    var result = Ext.JSON.decode(response.responseText);
                                    if (result.datas) {
                                        result.datas[0].loaded = true;
                                        result.datas[0].leaf = true;
                                        selected.appendChild(result.datas[0]);
                                    }
                                    Ext.Msg.alert("提示", result.msg);
                                }
                            });
                            win.close();
                        });


                    }

                }
            }, {
                type: 'button',
                text: '修改分组',
                handler: function () {
                    var rec = chartGroupTree.getSelection();
                    var win = null;
                    if (rec && rec.length > 0) {
                        var selected = rec[0];
                        win = Ext.fillAnalysisChartGroup.init(function (data) {
                            selected.set(data);
                            chartGroupStore.sync({
                                failure: function () {
                                    chartGroupStore.reload();
                                }
                            });
                        }, selected);
                    } else {
                        //TODO未选择分组
                        Ext.Msg.alert("提示","请选择分组！")
                    }
                }
            }, {
                type: 'button',
                text: '删除分组',
                handler: function () {
                    var rec = chartGroupTree.getSelection();
                    var win = null;
                    if (rec && rec.length > 0) {
//                        chartGroupStore.remove(rec);
                        Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                            if (btn == 'yes') {
                                Ext.Array.each(rec, function (node) {
                                    node.remove();
                                })
                                chartGroupStore.sync();
                            }
                        });


                    } else {
                        //TODO未选择分组
                        Ext.Msg.alert("提示","请选择分组！")
                    }
                }
            }],
            listeners: {
                itemclick: function (view, rec) {
                    chartBaseStore.groupId = rec.getId();
                    chartBaseStore.load({params: {groupId: rec.getId()}});
                }
            },
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    dragGroup: 'structure',
                    dropGroup: 'structure',
                    dragZone: {
                        afterDragOver: function (zone) {
                            var rec = zone.overRecord;
                            if (zone.curRecoder) {
                                if (rec != zone.curRecoder) {
//                                    zone.curRecoder.reject();
                                    if (!zone.curRecoder.hasChildNodes())
                                        zone.curRecoder.set('leaf', true);
                                }
                            }
                            zone.curRecoder = rec;
                            if (rec) {
                                rec.set('loaded', true);
                                rec.set('leaf', false);
                            }

                        }
                    }
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, eOpts) {
                        var dragNode = data.records[0];
                        var pNode = dragNode.parentNode;
                        dragNode.set('pId', pNode.getId());
                        Ext.Array.each(pNode.childNodes, function (child) {
                            child.set('groupSort', child.data.index);
                        });
                        chartGroupTree.getRootNode().findChildBy(function (tmpNode) {
                            if (!tmpNode.hasChildNodes()) {
                                tmpNode.set('leaf', true);
                            }
                            return false;
                        }, null, true);
                        chartGroupStore.sync({
                            failure: function () {
                                chartGroupStore.reload();
                            }
                        });
                    },
                    containercontextmenu: function (_this, e) {
                        //取消冒泡
                        e.preventDefault();
                    },
                    itemcontextmenu: function (_this, record, itemId, index, e) {
                        //取消冒泡
                        e.preventDefault();
                    }

                }
            }
        });
        Ext.onReady(function () {
            var configMainPanel = new Ext.resizablePanel({
                width: '100%',
                height: '100%',
                layout: 'border',
                renderTo: 'analysisChart',
                items: [chartGroupTree, chartBaseGrid]
            });
        });

    </script>
</head>
<body>
<div id="analysisChart" style="width: 100%;height: 100%"></div>
</body>
</html>
