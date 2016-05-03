<%@ page import="com.city.common.pojo.Constant" %>
<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/1/14 0014
  Time: 下午 1:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>报表设计管理</title>
    <meta charset="UTF-8"/>
    <jsp:include page="rptImp.jsp"/>
    <script>
        //当前用户
        var depId = ${depId};
        //报表类型
        var rptTypes =${rptTypes};
        //表样类型
        var styleTypes =${styleTypes};
        //统计对象类型
        var researchObjTypes =${researchObjTypes};
        //默认调查对象
        var surObjId = <%=Constant.systemConfigPojo.getDefaultAreaId()%>;
        //报送周期
        var periods =${periods};
        //报送频率
        var yearFres =${yearFres};
        var halfFres =${halfFres};
        var quarterFres =${quarterFres};
        var monthFres =${monthFres};
    </script>
</head>
<body>
<div id="reportManageContainer" style="width:100%;height:100%;"></div>
<script src="<%=request.getContextPath()%>/City/support/regime/report/manage/addRptTmpWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/regime/report/manage/addRptGroupWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/regime/report/manage/addRptTmpStyleWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/regime/collection/addAllReportInfosWin.js"></script>
<script>
    Ext.onReady(function () {

//公共参数
        var commonParams = {
            groupParams: {
                name: '',
                status: 1
            },
            tmpParams: {
                name: '',
                depId: '',
                includeDownLevel: true,
                periods: '',
                rptType: '',
                beginYear: '',
                endYear: ''
            },
            styleParams: {
                tmpId: ''
            }
        };
        //左侧分组树
        var groupStore = new Ext.data.TreeStore({
            fields: ['id', 'name', 'parentId', 'status', 'comments', 'sort', 'leaf', 'children'],
            root: {
                expanded: 'true',
                id: 0,
                text: '报表分组'
            },
            proxy: {
                type: 'ajax',
                api: {
                    read: GLOBAL_PATH + '/support/regime/report/reportGroup/getReportGroups',
                    update: GLOBAL_PATH + '/support/regime/report/reportGroup/saveGroupSorts'
                }
            },
            nodeParam: 'parentId',
            listeners: {
                beforeload: function (_this, operation) {
                    operation.params = commonParams.groupParams;
                }
            }
        });
        //分组树
        var groupTree = new Ext.tree.Panel({
            flex: 1,
            height: '100%',
            store: groupStore,
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    ddGroup: 'reportGroup'
                },
                listeners: {
                    drop: function (node, data) {
                        var dragNode = data.records[0];
                        var pNode = dragNode.parentNode;
                        dragNode.set('parentId', pNode.getId());
                        Ext.Array.each(pNode.childNodes, function (child) {
                            child.set('sort', child.data.index);
                        });
                        groupStore.sync();
                    },
                    beforedrop: function (n, data, overModel, dropPosition, dropHandlers) {
//                        从报表列表拖拽过来的报表
                        if (data.view.name == 'gridView') {
                            var s = tmpGrid.getSelectionModel().getSelection();
                            var ids = '';
                            if (s.length > 0) {
                                for (var i = 0; i < s.length; i++) {
                                    var sc = s[i];
                                    ids += sc.get('id');
                                    ids += ',';
                                }
                                ids += '-1';
                            }
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + '/support/regime/report/reportGroup/saveReportForGroup',
                                params: {
                                    reportIds: ids,
                                    groupId: overModel.get('id')
                                },
                                success: function (response, opts) {
                                    var obj = Ext.decode(response.responseText);
                                    Ext.Msg.alert('成功', obj.msg);
                                    tmpStore.load({params: commonParams.tmpParams});
                                },
                                failure: function (response, opts) {
                                    Ext.Msg.alert('失败', '操作失败');
                                }
                            });
                            return false;
                        }
                    }
                }
            },
            rootVisible: true,
            tbar: [{
                xtype: 'textfield',
                fieldLabel: '查询',
                labelWidth: 50,
                width: 150,
                labelAlign: 'right',
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.groupParams.name = n;
                        groupStore.reload({
                            params: commonParams.groupParams, callback: function () {
                                if (n) {
                                    groupTree.expandAll();
                                }
                            }
                        });
                    }
                }
//            }, {
//                xtype: 'combobox',
//                fieldLabel: '状态',
//                labelWidth: 50,
//                width: 120,
//                labelAlign: 'right',
//                displayField: 'text',
//                valueField: 'value',
//                store: new Ext.data.Store({
//                    fields: ['text', 'value'],
//                    data: [{text: '全部', value: null}, {text: '启用', value: 1}, {text: '废弃', value: 0}]
//                }),
//                value: commonParams.groupParams.status,
//                listeners: {
//                    change: function (_this, n, o) {
//                        commonParams.groupParams.status = n;
//                        groupStore.reload({
//                            params: commonParams.groupParams, callback: function () {
//                                if (commonParams.groupParams.name) {
//                                    groupTree.expandAll();
//                                }
//                            }
//                        });
//                    }
//                }
            }, {
                xtype: 'checkbox',
                fieldLabel: '包含下级',
                labelWidth: 70,
                labelAlign: 'right',
                value: !!commonParams.tmpParams.includeDownLevel,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.tmpParams.includeDownLevel = n;
                        tmpStore.reload({params: commonParams.tmpParams});
                    }
                }
            }],
            listeners: {
                itemclick: function (_this, record) {
                    if (commonParams.treeMenu) {
                        commonParams.treeMenu.hide();
                    }
                    commonParams.tmpParams.groupId = record.get('id');
                    tmpStore.load({params: commonParams.tmpParams});
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    //设置触发点击事件
                    groupTree.fireEvent('itemclick', _this, record);
                    //阻止浏览器默认行为
                    e.preventDefault();
                    //菜单
                    var menu = commonParams.treeMenu;
                    if (menu) {
                        menu.hide();
                    }
                    menu = commonParams.treeMenu = new Ext.menu.Menu({
                        renderTo: Ext.getBody(),
                        items: [{
                            text: '添加下级',
                            iconCls: 'Add',
                            handler: function () {
                                //未展开时添加会有异常,先展开
                                if (!(record.isLeaf() || record.isExpanded())) {
                                    record.expand();
                                }
                                Ext.addReportGroupWin.init(true, record);
                            }
                        }, {
                            text: '添加同级',
                            iconCls: 'Controladdblue',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.addReportGroupWin.init(true, record.parentNode);
                            }
                        }, '-', {
                            text: '修改',
                            iconCls: 'Pageedit',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.addReportGroupWin.init(false, record);
                            }
                        }, {
                            text: '删除',
                            iconCls: 'Delete',
                            disabled: !record.get('id'),
                            handler: function () {
                                Ext.Msg.confirm('提示', '确定删除?', function (btn) {
                                    if (btn == 'yes') {
                                        //发送请求
                                        Ext.Ajax.request({
                                            url: GLOBAL_PATH + '/support/regime/report/reportGroup/removeGroups',
                                            params: {
                                                id: record.get('id')
                                            },
                                            success: function (response, opts) {
                                                var obj = Ext.decode(response.responseText);
                                                Ext.Msg.alert('成功', obj.msg);
                                                record.remove();
                                                //清空表格内容
                                                tmpStore.removeAll();
                                            },
                                            failure: function (response, opts) {
                                                Ext.Msg.alert('失败', '删除失败');
                                            }
                                        });
                                    }
                                });
                            }
                        }]
                    });
                    menu.showAt(e.getPoint());
                },
                containercontextmenu: function () {
                    if (commonParams.treeMenu) {
                        commonParams.treeMenu.hide();
                    }
                }
            }
        });

        //报表表格
        createModel('ReportTemplate', function () {
            Ext.define('ReportTemplate', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'groupId',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'rptCode',
                    type: 'string'
                }, {
                    name: 'rptType',
                    type: 'int'
                }, {
                    name: 'frequency',
                    type: 'string'
                }, {
                    name: 'department'
                }, {
                    name: 'submitDaysDelay',
                    type: 'int'
                }, {
                    name: 'researchObjType'
                }, {
                    name: 'researchObjId'
                }, {
                    name: 'period',
                    type: 'int'
                }, {
                    name: 'beginYear',
                    type: 'int'
                }, {
                    name: 'endYear',
                    type: 'int'
                }, {
                    name: 'beginPeriod',
                    type: 'int'
                }, {
                    name: 'endPeriod',
                    type: 'int'
                }, {
                    name: 'rptExplain',
                    type: 'string'
                }, {
                    name: 'rptComments',
                    type: 'string'
                }, {
                    name: 'status',
                    type: 'int'
                }]
            });
        });
        var tmpStore = new Ext.data.Store({
            model: 'ReportTemplate',
            pageSize: 15,
            proxy: {
                type: 'ajax',
                url: Global_Path + '/getRptTmpsByGroup',
                extraParams: commonParams.tmpParams,
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            autoLoad: false
        });
        // 重新加载参数
        tmpStore.on('beforeload', function (s) {
            s.getProxy().extraParams = commonParams.tmpParams;
        });
        var tmpContainerMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加报表',
                iconCls: 'Add',
                handler: addRptTmp
            }]
        });
        var tmpMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改报表',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel = tmpGrid.getSelectionModel().getSelection();
                    if (sel.length == 1) {
                        var group = groupTree.getSelectionModel().getSelection()[0];
                        Ext.addRptTmpWin.init(sel[0], group, function () {
                            tmpStore.reload({params: commonParams.tmpParams});
                        });
                    } else {
                        Ext.Msg.alert('提示', '请选中一个报表来修改!');
                    }
                }
            }, {
                text: '删除报表',
                iconCls: 'Delete',
                handler: function () {
                    var sel = tmpGrid.getSelectionModel().getSelection();
                    if (sel.length) {
                        Ext.Msg.confirm('警告', '确定删除?', function (btn) {
                            if (btn == 'yes') {
                                var ids = [];
                                for (var i = 0; i < sel.length; i++) {
                                    ids.push(sel[i].get('id'));
                                }
                                //发送请求删除
                                Ext.Ajax.request({
                                    url: Global_Path + '/removeRptTmps',
                                    params: {
                                        ids: ids.join(',')
                                    },
                                    success: function (response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        Ext.Msg.alert('成功', obj.msg);
                                        tmpStore.reload({params: commonParams.tmpParams});
                                        //清除已加载的表样
                                        styleStore.removeAll();
                                    },
                                    failure: function (response, opts) {
                                        Ext.Msg.alert('失败', '删除失败');
                                    }
                                });
                            }
                        });
                    }
                }
            }, {
                text: '复制报表',
                iconCls: 'Databasecopy',
                handler: function () {
                    var sel = tmpGrid.getSelectionModel().getSelection();
                    if (sel.length) {
                        var tmpIds = [];
                        for (var i = 0; i < sel.length; i++) {
                            tmpIds.push(sel[i].get('id'));
                        }
                        //发送请求删除
                        Ext.Ajax.request({
                            url: Global_Path + '/copyRptTmps',
                            params: {
                                tmpIds: tmpIds.join(',')
                            },
                            success: function (response, opts) {
                                var obj = Ext.decode(response.responseText);
                                Ext.Msg.alert('成功', obj.msg);
                                tmpStore.reload({params: commonParams.tmpParams});
                            },
                            failure: function (response, opts) {
                                Ext.Msg.alert('失败', '操作失败');
                            }
                        });
                    }
                }
            }, '-', {
                text: '添加表样',
                iconCls: 'Pageadd',
                handler: function () {
                    addRptTmpStyle(false);
                }
            }, {//TODO 导入表样等表样设计做完后在家上
                text: '从库导入表样',
                iconCls: 'Basketput'
            }]
        });
        var tmpGrid = new Ext.grid.Panel({
            width: '100%',
            height: 400,
            flex: 3,
            store: tmpStore,
            selType: 'checkboxmodel',
            viewConfig: {
                name: 'gridView',
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'reportGroup',
                    enableDrop: false
                }
            },
            columns: [{
                text: '报表名称',
                dataIndex: 'name',
                flex: 2
            }, {
                text: '报送周期',
                dataIndex: 'period',
                flex: 0.5,
                renderer: function (data) {
                    for (var i = 0; i < periods.length; i++) {
                        if (periods[i].value == data) {
                            return periods[i].text;
                        }
                    }
                    return '';
                }
            }, {
                text: '所属部门',
                dataIndex: 'department',
                flex: 1,
                renderer: function (data) {
                    if (data) {
                        return data.depName;
                    }
                    return data;
                }
            }, {
                text: '有效期',
                flex: 1.5,
                renderer: function (data, m, record) {
                    var period = record.get('period');
                    var beginYear = record.get('beginYear');
                    var endYear = record.get('endYear');
                    var beginPeriod = record.get('beginPeriod');
                    var endPeriod = record.get('endPeriod');
                    return getValidTime(period, beginYear, beginPeriod, endYear, endPeriod);
                }
            }, {
                text: '报表类型',
                dataIndex: 'rptType',
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
                text: '状态',
                dataIndex: 'status',
                flex: 0.5,
                renderer: function (data) {
                    if (data) {
                        return '启用';
                    }
                    return '停用';
                }
//            }, {
//                text: '操作',
//                dataIndex: 'rptType',
//                flex: 0.5,
//                renderer: function (data) {
//                    if (data == 2) {//加工表
//                        return '<b style="color: #3437ff">编辑公式</b>';
//                    }
//                    return '';
//                }
            }, {
                text: '生成往期报表',
                dataIndex: '',
                flex: 0.5,
                renderer: function () {
                    return '<b style="color: #3437ff">生成报表</b>';
                }
            }],
            tbar: [{
                xtype: 'textfield',
                fieldLabel: '搜索',
                labelWidth: 50,
                width: 150,
                labelAlign: 'right',
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.tmpParams.name = n;
                        tmpStore.reload({params: commonParams.tmpParams});
                    }
                }
            }, {
                xtype: 'combobox',
                fieldLabel: '状态',
                labelWidth: 50,
                width: 120,
                labelAlign: 'right',
                displayField: 'text',
                valueField: 'value',
                store: new Ext.data.Store({
                    fields: ['text', 'value'],
                    data: [{text: '全部', value: null}, {text: '启用', value: 1}, {text: '废弃', value: 0}]
                }),
                value: commonParams.tmpParams.status,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.tmpParams.status = n;
                        tmpStore.reload({params: commonParams.tmpParams});
                    }
                }
            }, '->', {
                xtype: 'button',
                text: '添加报表',
                iconCls: 'Add',
                handler: function () {
                    addRptTmp();
                }
            }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: tmpStore,
                displayInfo: true
            },
            listeners: {
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                    tmpMenu.hide();
                    tmpContainerMenu.showAt(e.getPoint());
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    commonParams.styleParams.tmpId = record.get('id');
                    styleStore.load({params: commonParams.styleParams});
                    e.preventDefault();
                    tmpContainerMenu.hide();
                    tmpMenu.showAt(e.getPoint());
                },
                cellclick: function (_this, td, cellIndex, record) {
                    commonParams.styleParams.tmpId = record.get('id');
                    styleStore.load({params: commonParams.styleParams});
                    tmpContainerMenu.hide();
                    tmpMenu.hide();
                    if (cellIndex == 7) {//生成报表
                        Ext.Ajax.request({
                            url: Global_Path + '/getRptStyleByTmp?tmpId=' + commonParams.styleParams.tmpId,
                            success: function (response, opts) {
                                var obj = Ext.decode(response.responseText);
                                Ext.Ajax.request({
                                    url: Global_Path + '/isHasBar?styleId=' + obj[0].id,
                                    success: function (response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        //有主宾栏
                                        if (!obj) {
                                            Ext.Msg.alert("提示", "请先进行表样设计");
                                            return;
                                        } else {
                                            var sel = tmpGrid.getSelectionModel().getSelection();
                                            var record = sel[0];
                                            Ext.addAllReportInfosWin.init(record, function (rec) {
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                },
                containerclick: function () {
                    tmpContainerMenu.hide();
                    tmpMenu.hide();
                }
            }
        });
        //表样表格
        var styleStore = new Ext.data.Store({
            fields: ['id', 'name', 'reportTemplate', 'beginYear', 'endYear', 'beginPeriod', 'endPeriod', 'rptStyle'],
            proxy: {
                type: 'ajax',
                url: Global_Path + '/getRptStyleByTmp',
                extraParams: commonParams.styleParams
            },
            autoLoad: false
        });
        var styleContainerMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加表样',
                iconCls: 'Add',
                handler: function () {
                    addRptTmpStyle(false);
                }
            }, {
                text: '从库导入',
                iconCls: 'Basketput'
            }]
        });
        var styleMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改表样',
                iconCls: 'Pageedit',
                handler: function () {
                    addRptTmpStyle(true);
                }
            }, {
                text: '删除表样',
                iconCls: 'Delete',
                handler: function () {
                    var sel = styleGrid.getSelectionModel().getSelection();
                    if (sel.length) {
                        Ext.Msg.confirm('警告', '确定删除?', function (btn) {
                            if (btn == 'yes') {
                                var ids = [];
                                for (var i = 0; i < sel.length; i++) {
                                    ids.push(sel[i].get('id'));
                                }
                                //发送请求删除
                                Ext.Ajax.request({
                                    url: Global_Path + '/removeRptTmpStyles',
                                    params: {
                                        ids: ids.join(',')
                                    },
                                    success: function (response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        Ext.Msg.alert('成功', obj.msg);
                                        styleStore.reload({params: commonParams.styleParams});
                                    },
                                    failure: function (response, opts) {
                                        Ext.Msg.alert('失败', '删除失败');
                                    }
                                });
                            }
                        });
                    }
                }
            }, {
                text: '复制表样',
                iconCls: 'Databasecopy',
                handler: function () {
                    var sel = styleGrid.getSelectionModel().getSelection();
                    if (sel.length) {
                        var styleIds = [];
                        for (var i = 0; i < sel.length; i++) {
                            styleIds.push(sel[i].get('id'));
                        }
                        //发送请求删除
                        Ext.Ajax.request({
                            url: Global_Path + '/copyRptTmpStyles',
                            params: {
                                styleIds: styleIds.join(',')
                            },
                            success: function (response, opts) {
                                var obj = Ext.decode(response.responseText);
                                Ext.Msg.alert('成功', obj.msg);
                                styleStore.reload({params: commonParams.styleParams});
                            },
                            failure: function (response, opts) {
                                Ext.Msg.alert('失败', '操作失败');
                            }
                        });
                    }
                }
            }, '-', {
                text: '加入到库',
                iconCls: 'Basketadd'
            }]
        });
        var styleGrid = new Ext.grid.Panel({
            width: '100%',
            flex: 2,
            store: styleStore,
            selType: 'checkboxmodel',
            columns: [{
                text: '表样名',
                dataIndex: 'name',
                flex: 1
            }, {
                text: '表样类型',
                hidden: true,
                dataIndex: 'styleType',
                flex: 0.5,
                renderer: function (data) {
                    for (var i = 0; i < styleTypes.length; i++) {
                        if (styleTypes[i].value == data) {
                            return styleTypes[i].text;
                        }
                    }
                    return '';
                }
            }, {
                text: '有效期',
                flex: 1,
                renderer: function (data, m, record) {
                    var period = record.get('reportTemplate').period;
                    var beginYear = record.get('beginYear');
                    var endYear = record.get('endYear');
                    var beginPeriod = record.get('beginPeriod');
                    var endPeriod = record.get('endPeriod');
                    return getValidTime(period, beginYear, beginPeriod, endYear, endPeriod);
                }
            }, {
                text: '表样设计',
                flex: 0.5,
                align: 'center',
                renderer: function () {
                    return '<a style="color:#0000FF">表样设计</a>';
                }
//            }, {
//                text: '审核规则',
//                flex: 0.5,
//                align: 'center',
//                renderer: function () {
//                    return '<a style="color:#0000FF">审核规则</a>';
//                }
            }],
            listeners: {
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                    styleMenu.hide();
                    styleContainerMenu.showAt(e.getPoint());
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    styleContainerMenu.hide();
                    styleMenu.showAt(e.getPoint());
                },
                cellclick: function (_this, td, cellIndex, record) {
                    styleContainerMenu.hide();
                    styleMenu.hide();
                    if (cellIndex == 4) {//弹出设计窗口
                        open(Global_Path + '/designReport/showReportDesign?styleId=' + record.get('id'));
                    }
                },
                containerclick: function () {
                    styleContainerMenu.hide();
                    styleMenu.hide();
                }
            }
        });
        var rightPanel = Ext.create('Ext.panel.Panel', {
            height: '100%',
            flex: 3,
            items: [tmpGrid, styleGrid]
        })
        //容器
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            renderTo: 'reportManageContainer',
            layout: 'hbox',
            items: [groupTree, rightPanel],
            border: 0
        });

        //有效期
        function getValidTime(period, beginYear, beginPeriod, endYear, endPeriod) {
//            if (period == 1) {//年
//                return '从<b style="color:red">' + beginYear + '年</b>到<b style="color:red">' + endYear + '年</b>';
//            }
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
            var beginStr = '';
            var endStr = '';
            for (var i = 0; i < arr.length; i++) {
                if (beginPeriod == arr[i].value) {
                    beginStr = arr[i].text;
                }
                if (endPeriod == arr[i].value) {
                    endStr = arr[i].text;
                }
            }
            if (endYear == 0) {//一直有效
                return '从<b style="color:red">' + beginYear + '年' + beginStr + '</b>起一直有效';
            }
            return '从<b style="color:red">' + beginYear + '年' + beginStr + '</b>到<b style="color:red">' + endYear + '年' + endStr + '</b>';
        }

        //添加报表
        function addRptTmp() {
            var sel = groupTree.getSelectionModel().getSelection();
            if (sel.length) {
                var record = sel[0];
                if (record.get('id')) {
                    Ext.addRptTmpWin.init(null, record, function () {
                        tmpStore.reload({params: commonParams.tmpParams});
                    });
                } else {
                    Ext.Msg.alert('警告', '不能在根节点下添加报表');
                }
            } else {
                Ext.Msg.alert('提示', '请先选择一个分组来添加');
            }
        }

        //添加表样
        function addRptTmpStyle(isUpdate) {
            var record = null;
            if (isUpdate) {
                var styleSel = styleGrid.getSelectionModel().getSelection();
                if (styleSel.length == 1) {
                    record = styleSel[0];
                } else {
                    Ext.Msg.alert('提示', '请选中一个表样来修改!');
                    return;
                }
            }
            var sel = tmpGrid.getSelectionModel().getSelection();
            if (sel.length == 1) {
                var curStyles = [];
                //所有当前模板的表样
                var count = styleStore.getCount();
                if (count) {
                    for (var i = 0; i < count; i++) {
                        var rec = styleStore.getAt(i);
                        if (rec != record) {
                            curStyles.push(styleStore.getAt(i));
                        }
                    }
                }
                //保存
                Ext.addRptTmpStyleWin.init(sel[0], record, curStyles, function () {
                    styleStore.reload({params: commonParams.styleParams});
                });
            } else {
                Ext.Msg.alert('提示', '请选中一个报表模板来添加/保存!');
            }
        }
    });
</script>
</body>
</html>
