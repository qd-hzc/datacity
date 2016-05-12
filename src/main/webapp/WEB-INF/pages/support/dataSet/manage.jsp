<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/2/22
  Time: 14:10
  To change this template use File | Settings | File Templates.
  数据及管理
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>数据及管理</title>
    <meta charset="UTF-8"/>
    <style>
        div.iconContainer {
            height: 15px;
            position: absolute;
            right: 2px;
            display: inline-block;
        }

        div.icon {
            width: 15px;
            height: 15px;
            float: right;
        }
    </style>
    <jsp:include page="../../common/metaDataImp.jsp"></jsp:include>
    <jsp:include page="../../common/sysConstant.jsp"></jsp:include>
    <jsp:include page="dataInfoType.jsp"></jsp:include>
</head>
<body>
<div id="dataSetManage" style="width:100%;height: 100%;"></div>
<script>
    var GLOBAL_PATH = '<%=request.getContextPath()%>';
    var baseUrl = '<%=request.getContextPath()%>/support/dataSet';
    Ext.onReady(function () {
        var commonParams = {
            dataSetParams: {
                name: ''
            },
            dataSetDataParams: {
                dataSetId: ''
            },
            itemParams: {
                dataName: ''
            }
        };
        //数据集表格 store
        var dataSetStore = new Ext.data.Store({
            fields: ['id', 'name', 'comments', 'baseFlag', 'expandFlag'],
            proxy: {
                type: 'ajax',
                url: baseUrl + '/queryDataSet',
                extraParams: commonParams.dataSetParams,
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            autoLoad: true
        });
        //右键菜单
        var dataSetMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改数据集',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel = dataSetGrid.getSelectionModel().getSelection();
                    if (sel.length == 1) {
                        saveDataSet(sel[0]);
                    } else {
                        Ext.Msg.alert('提示', '只能选中一条数据来修改!');
                    }
                }
            }, {
                text: '删除数据集',
                iconCls: 'Delete',
                handler: function () {
                    var sel = dataSetGrid.getSelectionModel().getSelection();
                    if (sel.length) {
                        Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                            if (btn == 'yes') {
                                var ids = [];
                                for (var i = 0; i < sel.length; i++) {
                                    ids.push(sel[i].get('id'));
                                }
                                //发送请求删除
                                Ext.Ajax.request({
                                    url: baseUrl + '/removeDataSets',
                                    params: {
                                        ids: ids.join(',')
                                    },
                                    success: function (response) {
                                        dataSetStore.reload({params: commonParams.dataSetParams});
                                        //清空已选
                                        itemStore.removeAll();
                                        tfStore.removeAll();
                                        researchObjStore.removeAll();
                                        itemMenuStore.removeAll();
                                    },
                                    failure: function (response) {
                                        Ext.Msg.alert('失败', '删除失败!');
                                    }
                                });
                            }
                        });
                    }
                }
            }, '-', {
                text: '添加基础集数据',
                iconCls: 'Pageadd',
                handler: saveDataSetData
            }, {
                text: '添加扩展集数据',
                iconCls: 'Noteadd'
            }, {
                text: '清空数据集',
                iconCls: 'Cart',
                handler: clearDataSetDatas
            }]
        });
        var dataSetCMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加数据集',
                iconCls: 'Add',
                handler: saveDataSet
            }]
        });
        //数据集表格
        var dataSetGrid = new Ext.grid.Panel({
            flex: 2,
            height: '100%',
            selModel: 'checkboxmodel',
            store: dataSetStore,
            columns: [{
                text: '数据集',
                flex: 1,
                dataIndex: 'name'
            }, {
                text: '说明',
                flex: 1,
                dataIndex: 'comments',
                renderer: function (value, metaData, record) {
                    var comments = value || '&nbsp;';
                    //扩展集数据
                    var expandFalg = record.get('expandFlag');
                    var expandStr = '';
                    if (expandFalg) {
                        expandStr = '<div class="Keyadd icon"></div>';
                    }
                    //基础集数据
                    var baseFlag = record.get('baseFlag');
                    var baseStr = '';
                    if (baseFlag) {
                        baseStr = '<div class="Key icon"></div>';
                    }
                    return comments + '<div class="iconContainer">' + expandStr + baseStr + '</div>';
                }
            }],
            tbar: [{
                xtype: 'textfield',
                emptyText: '查询数据集',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n) {
                        commonParams.dataSetParams.name = n;
                        dataSetStore.reload({params: commonParams.dataSetParams});
                    }
                },
                width: '65%'
            }, '->', {
                xtype: 'button',
                text: '添加',
                iconCls: 'Add',
                width: 60,
                handler: saveDataSet
            }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: dataSetStore,
                displayInfo: true,
                beforePageText: '',
                afterPageText: '/{0}'
            },
            listeners: {
                containerclick: function () {//点击空白处
                    dataSetCMenu.hide();
                    dataSetMenu.hide();
                },
                containercontextmenu: function (_this, e) {//右击空白处
                    e.preventDefault();
                    dataSetCMenu.showAt(e.getXY());
                },
                rowcontextmenu: function (_this, record, tr, rowIndex, e) {//右击行
                    e.preventDefault();
                    //首先选中
                    commonParams.dataSetDataParams.dataSetId = record.get('id');
                    reloadDataSetData(null, true);
                    //显示菜单
                    dataSetMenu.showAt(e.getXY());
                },
                rowclick: function (_this, record) {//点击行
                    commonParams.dataSetDataParams.dataSetId = record.get('id');
                    reloadDataSetData(null, true);
                    dataSetCMenu.hide();
                    dataSetMenu.hide();
                }
            }
        });
        //基础集 指标表格 store
        var itemStore = getDataSetDataStore(METADATA_TYPE.ITEM, ['caliberName', 'depName', 'rptName']);
        //右键菜单
        var itemCMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加基础集数据',
                iconCls: 'Add',
                handler: saveDataSetData
            }, {
                text: '清空数据集',
                iconCls: 'Cart',
                handler: clearDataSetDatas
            }, {
                text: '添加指标详细数据',
                iconCls: 'Pageadd',
                handler: addItemInfoDatas
            }]
        });
        var itemMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改指标',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel = itemGrid.getSelectionModel().getSelection();
                    Ext.dataSet.EditItemWin.init(sel[0], function () {
                        reloadDataSetData(METADATA_TYPE.ITEM);
                    });
                }
            }, {
                text: '删除指标',
                iconCls: 'Delete',
                handler: function () {
                    removeDataSetDatas(itemGrid, METADATA_TYPE.ITEM);
                }
            }]
        });
        //基础集 指标表格
        var itemGrid = new Ext.grid.Panel({
            flex: 3,
            width: '100%',
            selType: 'checkboxmodel',
            store: itemStore,
            columns: [{
                text: '指标',
                dataIndex: 'dataName',
                flex: 1
            }, {
                text: '口径',
                dataIndex: 'caliberName',
                flex: 1
            }, {
                text: '部门',
                dataIndex: 'depName',
                flex: 1
            }, {
                text: '所属报表',
                dataIndex: 'rptName',
                flex: 1
            }],
            tbar: ['<b>指标</b>', {
                xtype: 'textfield',
                emptyText: '在此处查询指标',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n) {
                        commonParams.itemParams.dataName = n;
                        reloadDataSetData(METADATA_TYPE.ITEM);
                    }
                }
            }, '->', {
                xtype: 'button',
                text: '添加基础集数据',
                iconCls: 'Add',
                handler: saveDataSetData
            }, {
                xtype: 'button',
                text: '清空数据集',
                iconCls: 'Cart',
                handler: clearDataSetDatas
            }, '-', {
                text: '添加指标详细数据',
                iconCls: 'Pageadd',
                handler: addItemInfoDatas
            }],
            listeners: {
                containerclick: function () {//点击空白处
                    itemCMenu.hide();
                    itemMenu.hide();
                },
                containercontextmenu: function (_this, e) {//右击空白处
                    e.preventDefault();
                    itemCMenu.showAt(e.getXY());
                },
                rowcontextmenu: function (_this, record, tr, rowIndex, e) {//右击行
                    e.preventDefault();
                    itemMenu.showAt(e.getXY());
                },
                rowclick: function (_this, record) {//点击行
                    itemCMenu.hide();
                    itemMenu.hide();
                }
            }
        });
        //时间框架 store
        var tfStore = getDataSetDataStore(METADATA_TYPE.TIME_FRAME);
        var tfMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '删除时间框架',
                iconCls: 'Delete',
                handler: function () {
                    removeDataSetDatas(tfGrid, METADATA_TYPE.TIME_FRAME);
                }
            }]
        });
        //基础集 时间框架表格
        var tfGrid = new Ext.grid.Panel({
            flex: 2,
            height: '100%',
            store: tfStore,
            selModel: 'checkboxmodel',
            columns: [{
                text: '<b>时间框架</b>',
                dataIndex: 'dataName',
                flex: 1
            }],
            listeners: {
                containerclick: function () {//点击空白处
                    tfMenu.hide();
                },
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                },
                rowcontextmenu: function (_this, record, tr, rowIndex, e) {//右击行
                    e.preventDefault();
                    tfMenu.showAt(e.getXY());
                },
                rowclick: function (_this, record) {//点击行
                    tfMenu.hide();
                }
            }
        });
        //统计对象 store
        var researchObjStore = getDataSetDataStore(METADATA_TYPE.RESEARCH_OBJ, ['areaName']);
        //右键菜单
        var researchObjMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '删除调查对象',
                iconCls: 'Delete',
                handler: function () {
                    removeDataSetDatas(researchObjGrid, METADATA_TYPE.RESEARCH_OBJ);
                }
            }]
        });
        //基础集 统计对象表格
        var researchObjGrid = new Ext.grid.Panel({
            flex: 3,
            height: '100%',
            store: researchObjStore,
            selModel: 'checkboxmodel',
            columns: [{
                text: '统计对象',
                dataIndex: 'dataName',
                flex: 1
            }, {
                text: '统计对象类型',
                dataIndex: 'dataInfo1',
                flex: 1,
                renderer: function (data) {
                    return SUROBJ_TYPE.getCH(data);
                }
            }, {
                text: '统计地区',
                dataIndex: 'areaName',
                flex: 1
            }],
            listeners: {
                containerclick: function () {//点击空白处
                    researchObjMenu.hide();
                },
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                },
                rowcontextmenu: function (_this, record, tr, rowIndex, e) {//右击行
                    e.preventDefault();
                    researchObjMenu.showAt(e.getXY());
                },
                rowclick: function (_this, record) {//点击行
                    researchObjMenu.hide();
                }
            }
        });
        //统计对象 store
        var itemMenuStore = getDataSetDataStore(METADATA_TYPE.ITEM_MENU);
        //右键菜单
        var itemMenuMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '删除分组目录',
                iconCls: 'Delete',
                handler: function () {
                    removeDataSetDatas(itemMenuGrid, METADATA_TYPE.ITEM_MENU);
                }
            }]
        });
        //基础集 分组目录
        var itemMenuGrid = new Ext.grid.Panel({
            flex: 3,
            height: '100%',
            store: itemMenuStore,
            selModel: 'checkboxmodel',
            columns: [{
                text: '分组目录',
                dataIndex: 'dataName',
                flex: 1
            }],
            listeners: {
                containerclick: function () {//点击空白处
                    itemMenuMenu.hide();
                },
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                },
                rowcontextmenu: function (_this, record, tr, rowIndex, e) {//右击行
                    e.preventDefault();
                    itemMenuMenu.showAt(e.getXY());
                },
                rowclick: function () {//点击行
                    itemMenuMenu.hide();
                }
            }
        });
        //布局
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            renderTo: 'dataSetManage',
            layout: 'hbox',
            items: [dataSetGrid, {
                xtype: 'tabpanel',
                flex: 7,
                height: '100%',
                border: false,
                items: [{
                    title: '基础集',
                    width: '100%',
                    height: '100%',
                    layout: 'vbox',
                    border: false,
                    items: [itemGrid, {
                        flex: 2,
                        width: '100%',
                        layout: 'hbox',
                        border: false,
                        items: [tfGrid, researchObjGrid, itemMenuGrid]
                    }]
                }, {
                    title: '扩展集'
                }]
            }]
        });
        //保存数据集
        function saveDataSet(record) {
            Ext.saveDataSetWin.init(record, function () {
                dataSetStore.reload({params: commonParams.dataSetParams});
            });
        }

        //保存指标数据
        function saveDataSetData() {
            var dataSetId = commonParams.dataSetDataParams.dataSetId;
            if (dataSetId) {
                Ext.dataSet.AddDataSetDataWin.init(dataSetId, function () {
                    reloadDataSetData();
                    //重新计算数据集flag
                    dataSetStore.reload({params: commonParams.dataSetParams});
                });
            } else {
                Ext.Msg.alert('提示', '需要选一个数据集来添加!');
            }
        }

        //删除指标数据
        function removeDataSetDatas(grid, dataType) {
            var sel = grid.getSelectionModel().getSelection();
            if (sel.length) {
                Ext.Msg.confirm('警告', '确定删除?', function (btn) {
                    if (btn == 'yes') {
                        var ids = [];
                        for (var i = 0; i < sel.length; i++) {
                            ids.push(sel[i].get('id'));
                        }
                        //发送请求,删除指标数据
                        Ext.Ajax.request({
                            url: baseUrl + '/removeDataSetDatas',
                            params: {
                                ids: ids.join(',')
                            },
                            success: function (response) {
                                var obj = Ext.decode(response.responseText);
                                if (obj.success) {
                                    reloadDataSetData(dataType);
                                } else {
                                    Ext.Msg.alert('提示', '删除失败');
                                }
                            },
                            failure: function () {
                                Ext.Msg.alert('提示', '删除失败');
                            }
                        });
                    }
                });
            }
        }

        //清除数据集
        function clearDataSetDatas() {
            Ext.Msg.confirm('警告', '确定要清空该数据集的数据么?', function (btn) {
                if (btn == 'yes') {
                    Ext.Ajax.request({
                        url: baseUrl + '/clearDataSetDatas',
                        params: {
                            dataSetId: commonParams.dataSetDataParams.dataSetId
                        },
                        success: function (response) {
                            var obj = Ext.decode(response.responseText);
                            if (obj.success) {
                                reloadDataSetData();
                                //重新计算数据集flag
                                dataSetStore.reload({params: commonParams.dataSetParams});
                            } else {
                                Ext.Msg.alert('提示', '操作失败');
                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('提示', '操作失败');
                        }
                    });
                }
            });
        }

        //添加指标详细数据
        function addItemInfoDatas() {
            var dataSetId = commonParams.dataSetDataParams.dataSetId;
            if (dataSetId) {
                //先获取指标
                var sel = itemGrid.getSelectionModel().getSelection();
                if (sel.length) {
                    var itemIds = [];
                    for (var i = 0; i < sel.length; i++) {
                        itemIds.push(sel[i].get('dataValue'));
                    }
                    //弹出窗口来添加数据
                    Ext.dataSet.AddItemInfoDataWin.init(dataSetId, itemIds.join(','), function () {
                        reloadDataSetData();
                        //重新计算数据集flag
                        dataSetStore.reload({params: commonParams.dataSetParams});
                    });
                } else {
                    Ext.Msg.alert('提示', '请选中指标来添加!');
                }
            } else {
                Ext.Msg.alert('提示', '需要选一个数据集来添加!');
            }
        }

        //刷新数据及数据表格
        function reloadDataSetData(dataType, exceptDataSet) {
            if (!exceptDataSet) {
                dataSetStore.reload({params: commonParams.dataSetParams});
            }
            if (dataType) {
                switch (dataType) {
                    case METADATA_TYPE.ITEM:
                        itemStore.reload({
                            params: {
                                dataSetId: commonParams.dataSetDataParams.dataSetId,
                                dataName: commonParams.itemParams.dataName
                            },
                            callback: function (records) {
                                setDataName(records, [DATA_INFO_TYPE.CALIBER, DATA_INFO_TYPE.DEP, DATA_INFO_TYPE.RPT], [{
                                    id: 'dataInfo1',
                                    name: 'caliberName'
                                }, {id: 'dataInfo2', name: 'depName'}, {id: 'dataInfo3', name: 'rptName'}]);
                            }
                        });
                        break;
                    case METADATA_TYPE.TIME_FRAME:
                        tfStore.reload({params: commonParams.dataSetDataParams});
                        break;
                    case METADATA_TYPE.RESEARCH_OBJ:
                        researchObjStore.reload({
                            params: commonParams.dataSetDataParams, callback: function (records) {
                                setDataName(records, [DATA_INFO_TYPE.AREA], [{id: 'dataInfo2', name: 'areaName'}]);
                            }
                        });
                        break;
                    case METADATA_TYPE.ITEM_MENU:
                        itemMenuStore.reload({params: commonParams.dataSetDataParams});
                        break;
                }
            } else {
                itemStore.reload({
                    params: {
                        dataSetId: commonParams.dataSetDataParams.dataSetId,
                        dataName: commonParams.itemParams.dataName
                    },
                    callback: function (records) {
                        setDataName(records, [DATA_INFO_TYPE.CALIBER, DATA_INFO_TYPE.DEP, DATA_INFO_TYPE.RPT], [{
                            id: 'dataInfo1',
                            name: 'caliberName'
                        }, {id: 'dataInfo2', name: 'depName'}, {id: 'dataInfo3', name: 'rptName'}]);
                    }
                });
                tfStore.reload({params: commonParams.dataSetDataParams});
                researchObjStore.reload({
                    params: commonParams.dataSetDataParams, callback: function (records) {
                        setDataName(records, [DATA_INFO_TYPE.AREA], [{id: 'dataInfo2', name: 'areaName'}]);
                    }
                });
                itemMenuStore.reload({params: commonParams.dataSetDataParams});
            }
        }

        //获取数据集数据的store
        function getDataSetDataStore(dataType, extrasFields) {
            if (!extrasFields) {
                extrasFields = [];
            }
            return new Ext.data.Store({
                fields: ['id', 'dataSetId', 'dataName', 'dataType', 'dataValue', 'dataInfo1', 'dataInfo2', 'dataInfo3'].concat(extrasFields),
                proxy: {
                    type: 'ajax',
                    url: baseUrl + '/queryDataSetData',
                    extraParams: {
                        dataType: dataType
                    }
                },
                autoLoad: false
            });
        }
    });
    /**
     * 设置数据名
     * @param records 回调时查询出的数据
     * @param infoTypes 类型数组,DATA_INFO_TYPE中
     * @param objs [{id,name}]
     */
    function setDataName(records, infoTypes, objs) {
        if (records && records.length) {
            var objIdArr = [];
            for (var i = 0; i < objs.length; i++) {
                var ids = [];
                for (var j = 0; j < records.length; j++) {
                    var curId = records[j].get(objs[i].id);
                    if (curId) {
                        ids.push(curId);
                    }
                }
                objIdArr.push(ids);
            }
            for (var k = 0; k < objIdArr.length; k++) {
                var curObjIds = objIdArr[k];
                if (!curObjIds.length) {
                    continue;
                }
                var obj = objs[k];
                //发送请求
                Ext.Ajax.request({
                    url: baseUrl + '/queryInfoName',
                    async: infoTypes.length == 1,
                    params: {
                        dataIds: curObjIds.join(','),
                        dataType: infoTypes[k]
                    },
                    success: function (res) {
                        var arr = Ext.decode(res.responseText);
                        if (arr && arr.length) {
                            for (var i = 0; i < records.length; i++) {
                                for (var j = 0; j < arr.length; j++) {
                                    var record = records[i];
                                    if (record.get(obj.id) == arr[j][0]) {
                                        record.set(obj.name, arr[j][1], {dirty: false});
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }
</script>
<script src="<%=request.getContextPath()%>/City/support/dataSet/saveDataSetWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/dataSet/addDataSetDataWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/dataSet/addItemInfoDataWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/dataSet/editItemWin.js"></script>
<script src="<%=request.getContextPath()%>/City/common/arrFuns.js"></script>
</body>
</html>
