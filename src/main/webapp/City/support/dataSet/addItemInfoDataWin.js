/**
 * Created by wxl on 2016/5/3.
 */
createModel('Ext.dataSet.AddItemInfoDataWin', function () {
    Ext.define('Ext.dataSet.AddItemInfoDataWin', {
        extend: 'Ext.window.Window',
        width: 800,
        height: 500,
        modal: true,
        layout: 'vbox',
        title: '添加指标详细数据窗口'
    });
});
/**
 * 初始化窗口
 * @param dataSetId 数据集id
 * @param itemIds 选中的指标id
 * @param fn 回调
 */
Ext.dataSet.AddItemInfoDataWin.init = function (dataSetId, itemIds, fn) {
    //时间框架  store
    var tfStore = new Ext.data.Store({
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: baseUrl + '/queryBarInfos',
            extraParams: {
                dataType: METADATA_TYPE.TIME_FRAME,
                itemIds: itemIds
            }
        },
        autoLoad: true
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
                dataType: METADATA_TYPE.RESEARCH_OBJ,
                itemIds: itemIds
            }
        },
        autoLoad: true,
        listeners: {
            load: function (_this, records) {
                setDataName(records, [DATA_INFO_TYPE.AREA], [{id: 'areaId', name: 'areaName'}]);
            }
        }
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
                dataType: METADATA_TYPE.ITEM_MENU,
                itemIds: itemIds
            }
        },
        autoLoad: true,
        listeners: {
            load: function (_this, records) {
                if (records && records.length) {
                    itemMenuStore.insert(0, {
                        id: 0,
                        name: '无分组'
                    });
                }
            }
        }
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
    var win = new Ext.dataSet.AddItemInfoDataWin({
        //items: [tfGrid, researchObjGrid, itemMenuGrid, selItemMenuGrid],
        items: [{
            xtype: 'panel',
            flex: 1,
            width: '100%',
            layout: 'hbox',
            items: [tfGrid, researchObjGrid],
            border: false
        }, {
            xtype: 'panel',
            flex: 1,
            width: '100%',
            layout: 'hbox',
            items: [itemMenuGrid, selItemMenuGrid],
            border: false,
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
        }],
        buttons: [{
            text: '保存',
            handler: function () {
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
                if (infoDatas.length) {
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
                    Ext.Msg.alert('提示', '添加的数据为空,直接关闭即可');
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
};
