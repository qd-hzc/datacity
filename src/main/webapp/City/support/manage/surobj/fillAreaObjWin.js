/**
 * Created by wys on 2016/1/21.
 */
createModel('Ext.fillAreaObjWin', function () {
    Ext.define('Ext.fillAreaObjWin', {
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
Ext.fillAreaObjWin.init = function (objStore, groupId) {
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

    var areaStore = Ext.create('Ext.data.TreeStore', {
        model: 'areaTreeModel',
        proxy: {
            actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
            type: 'ajax',
            url: GLOBAL_PATH + '/area/queryAreaByName',
            extraParams: {checkTreeFlag: 1},
            reader: {
                type: 'json'
            }
        },

        root: {
            expanded: true,
            id: 0,
            name: "根节点"
        },
        autoLoad: true
    });
//地区树Panel
    var areaTreePanel = new Ext.tree.Panel({
        store: areaStore,
        height: '100%',
        width: '40%',
        displayField: 'name',
        region: 'center',
        rootVisible: false,
        tbar: ['->', {
            text: '添加到统计对象', handler: function () {
                var k = areaTreePanel.getChecked();
                var surObjList = [];
                Ext.Array.each(k, function () {
                    var d = this.data;
                    surObjList.push(new SurObjPojo(d.name, d.id, SUROBJ_TYPE.TYPE_AREA, objStore.surObjGroupId, d.id, d.name, d.code, d.index));
                });
                Ext.Ajax.request({
                    url: GLOBAL_PATH + "/support/manage/surobj/addSurObj",
                    method: 'POST',
                    jsonData: surObjList,
                    success: function (response, opts) {
                        var result = Ext.JSON.decode(response.responseText);
                        objStore.add(result.datas);
                        objStore.reload();
                    }
                })
            }
        }],
        listeners: {
            checkchange: function (node, checked) {
                var tree = this;
                /*node.eachChild(function (_this) {
                    _this.set('checked', checked);
                    tree.fireEvent('checkchange', _this, checked);
                });*/
            }
        }
    });
//统计对象
    var surObjGrid = new Ext.grid.Panel({
        store: objStore,
        width: '60%',
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


    //添加修改其他统计对象窗口
    var win = new Ext.fillAreaObjWin({
        title: "地区统计对象",
        width: 800,
        height: 550,
        layout: 'border',
        items: [areaTreePanel, surObjGrid],
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