/**
 * Created by wxl on 2016/2/23.
 */
createModel('Ext.dataSet.AddDataSetDataWin', function () {
    Ext.define('Ext.dataSet.AddDataSetDataWin', {
        extend: 'Ext.window.Window',
        width: '90%',
        height: '90%',
        modal: true,
        layout: 'vbox',
        title: '添加基础集内容窗口'
    });
});
/**
 * 初始化添加数据集内容
 * @param dataSetId 数据集id
 * @param fn 回调函数
 */
Ext.dataSet.AddDataSetDataWin.init = function (dataSetId, fn) {
    var itemParams = {
        includeDownLevel: true,
        groupId: 0,
        itemName: '',
        status: 1
    };
    //指标store
    var itemStore = new Ext.data.Store({
        fields: ['id', 'groupId', 'item', 'itemName', 'groupName'],
        proxy: {
            type: 'ajax',
            url: GLOBAL_PATH + '/support/manage/item/getInfosByGroup',
            extraParams: itemParams
        },
        autoLoad: true
    });
    //指标面板
    var itemGrid = new Ext.grid.Panel({
        flex: 3,
        height: '100%',
        store: itemStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '名称',
            dataIndex: 'itemName',
            flex: 1
        }, {
            text: '指标名',
            dataIndex: 'item',
            flex: 1,
            renderer: function (data) {
                return data.name;
            }
        }, {
            text: '体系名',
            dataIndex: 'groupName',
            flex: 1
        }],
        tbar: [{
            xtype: 'querypicker',
            fieldLabel: '指标体系',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            store: new Ext.data.TreeStore({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/item/queryGroups',
                    extraParams: {
                        status: 1
                    }
                },
                root: {
                    id: 0,
                    name: '指标体系',
                    expanded: true
                },
                autoLoad: true
            }),
            queryParam: 'name',
            listeners: {
                select: function (picker, record) {
                    itemParams.groupId = record.get('id');
                    itemStore.reload({params: itemParams});
                }
            }
        }, {
            xtype: 'checkbox',
            fieldLabel: '包含下级',
            labelWidth: 70,
            labelAlign: 'right',
            value: itemParams.includeDownLevel,
            listeners: {
                change: function (_this, n) {
                    itemParams.includeDownLevel = n;
                    itemStore.reload({params: itemParams});
                }
            }
        }, {
            xtype: 'textfield',
            fieldLabel: '搜索',
            labelWidth: 50,
            width: 150,
            labelAlign: 'right',
            listeners: {
                change: function (_this, n, o) {
                    itemParams.itemName = n;
                    itemStore.reload({params: itemParams});
                }
            }
        }],
        listeners: {
            rowclick: itemChange,
            select: itemChange
        }
    });
    //口径 store
    var caliberStore = new Ext.data.Store({
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/getInfoList',
            extraParams: {
                infoType: DATA_INFO_TYPE.CALIBER
            }
        },
        autoLoad: false
    });
    //口径面板
    var caliberGrid = new Ext.grid.Panel({
        flex: 1,
        width: '100%',
        store: caliberStore,
        allowDeselect: true,
        columns: [{
            text: '<b style="color: black">选择口径</b>',
            dataIndex: 'name',
            flex: 1
        }]
    });
    //部门 store
    var depStore = new Ext.data.Store({
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/getInfoList',
            extraParams: {
                infoType: DATA_INFO_TYPE.DEP
            }
        },
        autoLoad: false
    });
    //部门面板
    var depGrid = new Ext.grid.Panel({
        flex: 1,
        width: '100%',
        store: depStore,
        allowDeselect: true,
        columns: [{
            text: '<b style="color: black">选择部门</b>',
            dataIndex: 'name',
            flex: 1
        }]
    });
    //来源表 store
    var rptStore = new Ext.data.Store({
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/getInfoList',
            extraParams: {
                infoType: DATA_INFO_TYPE.RPT
            }
        },
        autoLoad: false
    });
    //来源表面板
    var rptGrid = new Ext.grid.Panel({
        flex: 1,
        width: '100%',
        store: rptStore,
        allowDeselect: true,
        columns: [{
            text: '<b style="color: black">选择来源表</b>',
            dataIndex: 'name',
            flex: 1
        }]
    });
    //时间框架  store
    var tfStore = new Ext.data.Store({
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/queryBarInfos',
            extraParams: {
                dataType: METADATA_TYPE.TIME_FRAME
            }
        },
        autoLoad: false
    });
    //时间框架表格
    var tfGrid = new Ext.grid.Panel({
        flex: 1,
        height: '100%',
        store: tfStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '<b style="color: black">时间框架</b>',
            dataIndex: 'name',
            flex: 1
        }]
    });
    //统计对象  store
    var researchObjStore = new Ext.data.Store({
        fields: ['id', 'name', 'objType', 'areaId', 'areaName'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/queryBarInfos',
            extraParams: {
                dataType: METADATA_TYPE.RESEARCH_OBJ
            }
        },
        autoLoad: false
    });
    //统计对象表格
    var researchObjGrid = new Ext.grid.Panel({
        flex: 2,
        height: '100%',
        store: researchObjStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '<b style="color: black">统计对象</b>',
            dataIndex: 'name',
            flex: 1
        }, {
            text: '<b style="color: black">统计对象类型</b>',
            dataIndex: 'objType',
            flex: 1,
            renderer: function (data) {
                return SUROBJ_TYPE.getCH(data);
            }
        }, {
            text: '<b style="color: black">统计地区</b>',
            dataIndex: 'areaName',
            flex: 1
        }]
    });
    //待选分组目录  store
    var itemMenuStore = new Ext.data.Store({
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/queryBarInfos',
            extraParams: {
                dataType: METADATA_TYPE.ITEM_MENU
            }
        },
        autoLoad: false
    });
    //待选分组目录
    var itemMenuGrid = new Ext.grid.Panel({
        flex: 1,
        height: '100%',
        store: itemMenuStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '<b style="color: black">待选项</b>',
            dataIndex: 'name',
            flex: 1
        }]
    });
    //已选分组目录 store
    var selItemMenuStore = new Ext.data.Store({
        fields: ['id', 'name'],
        data: []
    });
    //已选分组目录
    var selItemMenuGrid = new Ext.grid.Panel({
        flex: 2,
        height: '100%',
        store: selItemMenuStore,
        selModel: 'checkboxmodel',
        columns: [{
            text: '<b style="color: black">已选项</b>',
            dataIndex: 'name',
            flex: 1
        }]
    });
    var win = new Ext.dataSet.AddDataSetDataWin({
        items: [{
            width: '100%',
            flex: 2,
            layout: 'hbox',
            border: false,
            items: [itemGrid, {
                flex: 2,
                height: '100%',
                layout: 'vbox',
                border: false,
                items: [caliberGrid, depGrid, rptGrid]
            }]
        }, {
            width: '100%',
            flex: 1,
            border: false,
            layout: 'hbox',
            items: [tfGrid, researchObjGrid, {
                xtype: 'panel',
                flex: 2,
                border: false,
                height: '100%',
                layout: 'hbox',
                items: [itemMenuGrid, selItemMenuGrid],
                tbar: ['<b>分组目录</b>', '->', {
                    xtype: 'button',
                    iconCls: 'Arrowleft',
                    handler: function () {
                        var selItemMenuSel = selItemMenuGrid.getSelectionModel().getSelection();
                        if (selItemMenuSel.length) {
                            selItemMenuStore.remove(selItemMenuSel);
                        }
                    }
                }, {
                    xtype: 'button',
                    iconCls: 'Arrowright',
                    handler: function () {
                        //添加
                        var itemMenuSel = itemMenuGrid.getSelectionModel().getSelection();
                        if (itemMenuSel.length) {
                            var objs = [];
                            for (var i = 0; i < itemMenuSel.length; i++) {
                                var record = itemMenuSel[i];
                                objs.push({
                                    id: record.get('id'),
                                    name: record.get('name')
                                });
                            }
                            //按分组id排序
                            objs.sort(function (obj1, obj2) {
                                return obj1.id - obj2.id;
                            });
                            //生成要添加的
                            var idArr = [];
                            var nameArr = [];
                            for (var i = 0; i < objs.length; i++) {
                                var rec = objs[i];
                                idArr.push(rec.id);
                                nameArr.push(rec.name);
                            }
                            var obj = {
                                id: idArr.join(','),
                                name: nameArr.join(',')
                            };
                            //添加
                            selItemMenuStore.add(obj);
                        }
                    }
                }]
            }]
        }],
        buttons: [{
            text: '保存',
            handler: function () {
                //保存数据
                var itemSel = itemGrid.getSelectionModel().getSelection();
                var count = itemSel.length;
                if (count) {
                    //保存时间框架,统计对象,分组目录的数据
                    var infoDatas = [];
                    //时间框架
                    var tfSel = tfGrid.getSelectionModel().getSelection();
                    if (tfSel.length) {
                        for (var i = 0; i < tfSel.length; i++) {
                            var record = tfSel[i];
                            infoDatas.push({
                                dataSetId: dataSetId,
                                dataName: record.get('name'),
                                dataType: METADATA_TYPE.TIME_FRAME,
                                dataValue: record.get('id')
                            });
                        }
                    } else {
                        Ext.Msg.alert('提示', '时间框架不可为空!');
                        return;
                    }
                    //统计对象
                    var researchObjSel = researchObjGrid.getSelectionModel().getSelection();
                    if (researchObjSel.length) {
                        for (var i = 0; i < researchObjSel.length; i++) {
                            var record = researchObjSel[i];
                            infoDatas.push({
                                dataSetId: dataSetId,
                                dataName: record.get('name'),
                                dataType: METADATA_TYPE.RESEARCH_OBJ,
                                dataValue: record.get('id'),
                                dataInfo1: record.get('objType'),
                                dataInfo2: record.get('areaId')
                            });
                        }
                    }
                    //分组目录
                    var c = selItemMenuStore.getCount();
                    if (c) {
                        for (var i = 0; i < c; i++) {
                            var record = selItemMenuStore.getAt(i);
                            infoDatas.push({
                                dataSetId: dataSetId,
                                dataName: record.get('name'),
                                dataType: METADATA_TYPE.ITEM_MENU,
                                dataValue: record.get('id')
                            });
                        }
                    }
                    //添加指标
                    var itemDatas = [];
                    for (var i = 0; i < itemSel.length; i++) {
                        var record = itemSel[i];
                        itemDatas.push({
                            dataSetId: dataSetId,
                            dataType: METADATA_TYPE.ITEM,
                            dataName: record.get('item').name,
                            dataValue: record.get('item').id
                        });
                    }
                    itemDatas = unique2(itemDatas, 'dataValue');
                    //设置指标的其他信息
                    if (itemDatas.length == 1) {
                        //口径
                        var caliberSel = caliberGrid.getSelectionModel().getSelection();
                        if (caliberSel.length) {
                            itemDatas[0].dataInfo1 = caliberSel[0].get('id');
                        }
                        //部门
                        var depSel = depGrid.getSelectionModel().getSelection();
                        if (depSel.length) {
                            itemDatas[0].dataInfo2 = depSel[0].get('id');
                        }
                        //所属报表
                        var rptSel = rptGrid.getSelectionModel().getSelection();
                        if (rptSel.length) {
                            itemDatas[0].dataInfo3 = rptSel[0].get('id');
                        }
                    }
                    infoDatas = infoDatas.concat(itemDatas);
                    //发送请求保存数据
                    Ext.Ajax.request({
                        url: baseUrl + '/addDataSetDatas',
                        params: {
                            dataSetDatas: JSON.stringify(infoDatas)
                        },
                        success: function (response) {
                            var obj = Ext.decode(response.responseText);
                            if (obj.success) {
                                win.close();
                                if (fn) {
                                    fn();
                                }
                            } else {
                                Ext.Msg.alert('提示', obj.msg);
                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('提示', '保存失败');
                        }
                    });
                } else {
                    Ext.Msg.alert('提示', '请选择要添加的指标!');
                }
            }
        }, {
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();

    function itemChange() {
        var sel = itemGrid.getSelectionModel().getSelection();
        var len = sel.length;
        if (len) {
            var itemIds = [];
            for (var i = 0; i < sel.length; i++) {
                itemIds.push(sel[i].get('item').id);
            }
            //去重
            itemIds = unique(itemIds);
            if (itemIds.length == 1) {
                //加载口径,部门,来源表信息
                var pms = {itemId: itemIds[0]};
                caliberStore.load({params: pms});
                depStore.load({params: pms});
                rptStore.load({params: pms});
            } else {
                //清空口径,部门,来源表信息
                caliberStore.removeAll();
                depStore.removeAll();
                rptStore.removeAll();
            }
            var params = {itemIds: itemIds.join(',')};
            tfStore.load({params: params});
            researchObjStore.load({
                params: params, callback: function (records) {
                    setDataName(records, [DATA_INFO_TYPE.AREA], [{id: 'areaId', name: 'areaName'}]);
                }
            });
            itemMenuStore.load({
                params: params, callback: function (records) {
                    if(records&&records.length){
                        itemMenuStore.insert(0,{
                            id: 0,
                            name: '无分组'
                        });
                    }
                }
            });
            selItemMenuStore.removeAll();
        }
    }
};

