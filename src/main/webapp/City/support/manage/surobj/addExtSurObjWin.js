/**
 * Created by wys on 2016/1/18.
 */
createModel('Ext.addExtSurObjWin', function () {
    Ext.define('Ext.addExtSurObjWin', {
        extend: 'Ext.window.Window',
        width: 600,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.addExtSurObjWin.init = function (rec, fnt) {
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
    //判断是添加还是修改
    var title = '添加其它统计对象';
    var isAdd = true;
    if (rec) {
        title = '修改其它统计对象'
        isAdd = false;
    }

    var areaStore = Ext.create('Ext.data.TreeStore', {
        model: 'areaTreeModel',
        proxy: {
            actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
            type: 'ajax',
            url: GLOBAL_PATH + '/area/queryAreaByName',
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

    var formPanel = new Ext.form.Panel({
        layout: 'vbox',
        border: 0,
        defaults: {
            xtype: 'panel',
            layout: 'column',
            border: 0,
            defaults: {
                margin: '5 10'
            }
        },
        items: [{
            items: [{
                xtype: 'textfield',
                fieldLabel: '名称',
                labelWidth: 50,
                blankText:'该项为必填项',
                allowBlank:false,
                name: 'surObjName'
            }, {
                xtype: 'textfield',
                fieldLabel: '编码',
                labelWidth: 50,
                name: 'surObjCode'
            }]
        }, {
            items: [{
                id: 'areaSel',
                xtype: 'querypicker',
                fieldLabel: '地区',
                editable: false,
                queryParam: 'areaName',
                labelWidth: 50,
                blankText:'该项为必填项',
                allowBlank:false,
                store: areaStore,
                queryMode: 'remote',
                displayField: 'name',
                name: 'surAreaId'
            }]
        }, {

            items: [{
                width: 470,
                xtype: 'textarea',
                fieldLabel: '备注',
                labelWidth: 50,
                name: 'surObjInfo'
            }]
        }]
    });
    if (!isAdd) {
        formPanel.loadRecord(rec);
    }


    //添加修改其它统计对象窗口
    var win = new Ext.addExtSurObjWin({
        title: title,
        width: 500,
        height: 250,
        items: [formPanel],
        buttons: [{
            text: '保存',
            handler: function () {
                if (formPanel.isValid()) {
                    if (fnt) {
                        if (formPanel.isValid()) {
                            var data = formPanel.getForm().getFieldValues();
                            var areaT = Ext.getCmp('areaSel');
                            data.areaName = areaT.getRawValue();
                            fnt(data);
                        }
                    }
                }

            }
        }, {
            text: '取消',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
    return win;
}