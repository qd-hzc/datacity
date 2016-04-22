/**
 * Created by wys on 2016/1/22.
 */
createModel('Ext.fillExtSurObjWin', function () {
    Ext.define('Ext.fillExtSurObjWin', {
        extend: 'Ext.window.Window',
        width: 600,
        closeAction: 'destroy',
        modal: true
    });
});
function SurObjPojo(surveyObjName, surveyObjId, surveyObjType, surveyObjGroupId, surveyObjAreaId, surveyObjAreaName, surveyObjCode, surveyObjSort) {
    this.surveyObjName = surveyObjName;
    this.surveyObjId = surveyObjId;
    this.surveyObjCode = surveyObjCode;
    this.surveyObjAreaId = surveyObjAreaId;
    this.surveyObjAreaName = surveyObjAreaName;
    this.surveyObjType = surveyObjType;
    this.surveyObjSort = surveyObjSort;
    this.surveyObjGroupId = surveyObjGroupId;
}
Ext.fillExtSurObjWin.init = function (objStore, groupId) {
    var extObjModel = createModel('areaTreeModel', function () {
        Ext.define('areaTreeModel', {
            extend: 'Ext.data.TreeModel',
            idProperty: 'id',
            fields: [
                {name: 'id', type: 'int'},
                {name: 'name', type: 'string'},
                {name: 'parentId', type: 'int'}
            ]
        });
    });

    //其它统计对象管理store
    var extSurObjStore = new Ext.data.Store({
        model: 'extSurObjModel',
        proxy: {
            type: 'ajax',
            actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
            api: {
                read: GLOBAL_PATH + '/support/manage/surobj/queryExtSurObj'
            },
            reader: {
                type: 'json',
                rootProperty: 'datas'
            }
        },
        autoLoad: true
    });
//其它统计对象Panel
    var extSurObjGridPanel = new Ext.grid.Panel({
        store: extSurObjStore,
        width: '50%',
        multiSelect: true,
        selType:'checkboxmodel',
        region: 'center',
        tbar: ['->', {
            text: '添加到统计对象',
            handler: function () {
                var selModel = extSurObjGridPanel.getSelectionModel();
                var sels = selModel.getSelection();
                var datas = [];
                Ext.Array.each(sels, function (record) {
                    var d = record.data;
                    var store = extSurObjGridPanel.getStore();
                    datas.push(new SurObjPojo(d.surObjName, d.id, SUROBJ_TYPE.TYPE_OTHER, objStore.surObjGroupId, d.surArea.id, d.surArea.name, d.surObjCode, store.indexOf(record)));
                });
                if (datas.length > 0) {
                    Ext.Ajax.request({
                        url: GLOBAL_PATH + "/support/manage/surobj/addSurObj",
                        method: 'POST',
                        jsonData: datas,
                        success: function (response, opts) {
                            var result = Ext.JSON.decode(response.responseText);
                            objStore.add(result.datas);
                            objStore.reload();
                        }
                    })
                }
            }
        }],
        columns: [
            {text: '统计对象名称', dataIndex: 'surObjName', flex: 0.5},
            {text: '编码', dataIndex: 'surObjCode', flex: 0.2},
            {text: '统计对象备注', dataIndex: 'surObjInfo', flex: 0.4}
        ]

    });
//统计对象
    var surObjGrid = new Ext.grid.Panel({
        store: objStore,
        width: '50%',
        region: 'east',
        selType:'checkboxmodel',
        multiSelect: true,
        tbar: ['->', {
            text: '删除统计对象', handler: function () {
                var selModel = surObjGrid.getSelectionModel();
                var sels = selModel.getSelection();
                objStore.remove(sels);
                objStore.sync();
            }
        }],
        columns: [
            {text: '分组名称', dataIndex: 'surveyObjName', flex: 0.3},
            {text: '分组类型', dataIndex: 'surveyObjType', flex: 0.2, renderer: function (data) {
                return SUROBJ_TYPE.getCH(data);
            }},
            {text: '地区', dataIndex: 'surveyObjAreaName', flex: 0.2},
            {text: '编码', dataIndex: 'surveyObjCode', flex: 0.4}
        ]

    });


    //添加修改其它统计对象窗口
    var win = new Ext.fillExtSurObjWin({
        title: "地区统计对象",
        width: 900,
        height: 550,
        layout: 'border',
        items: [extSurObjGridPanel, surObjGrid],
        buttons: [{
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
    return win;
}