/**
 * 编辑数据字典目录窗口
 * Created by wxl on 2016/3/21.
 */
createModel('Ext.dataDict.EditDataDictWin', function () {
    Ext.define('Ext.dataDict.EditDataDictWin', {
        extend: 'Ext.window.Window',
        width: 350,
        modal: true,
        title: '移动数据目录'
    });
});
/**
 * 初始化编辑窗口
 * @param pnode 父节点,若是修改,则父节点不用传 添加时用于
 * @param node 本节点,用于修改,若是添加同级或下级,则本节点不用传
 */
Ext.dataDict.EditDataDictWin.init = function (pnode, node, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        border: false,
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'parentId',
            value: pnode ? pnode.get('id') : ''
        }, {
            xtype: 'hidden',
            name: 'type',
            value: pnode ? pnode.get('type') : 1
        }, {
            xtype: 'hidden',
            name: 'sortIndex'
        }, {
            xtype: 'textfield',
            name: 'name',
            fieldLabel: '名称<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.9,
            margin: '20 0 0 0'
        }, {
            xtype: 'combo',
            name: 'roleId',
            fieldLabel: '角色<b style="color: red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            allowBlank: false,
            columnWidth: 0.9,
            margin: '20 0 0 0',
            value: (pnode ? pnode.get('roleId') : '') || '',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: APP_DATADICT_PATH + '/getRoles'
                },
                autoLoad: true
            }),
            displayField: 'name',
            valueField: 'id',
            queryParam: 'name',
            forceSelection: true
        }, {
            xtype: 'combo',
            name: 'menuIcon.id',
            fieldLabel: '图标',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 0 0',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: APP_DATADICT_PATH + '/queryIcons'
                },
                autoLoad: true
            }),
            displayField: 'name',
            valueField: 'id',
            queryParam: 'name'
        }, {
            xtype: 'combo',
            name: 'menuBg.id',
            fieldLabel: '背景',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: APP_DATADICT_PATH + '/queryBgs'
                },
                autoLoad: true
            }),
            displayField: 'name',
            valueField: 'id',
            queryParam: 'name'
        }, {
            //xtype: 'radiogroup',
            //fieldLabel: '状态',
            //labelWidth: 70,
            //labelAlign: 'right',
            //columns: 2,
            //margin: '0 0 20 0',
            //columnWidth: 0.9,
            //items: [
            //    {boxLabel: '显示', name: 'status', inputValue: '1', checked: true},
            //    {boxLabel: '隐藏', name: 'status', inputValue: '0'}
            //]
            xtype: 'hidden',
            name: 'status',
            value: 0
        }]
    });
    if (node) {
        //图标
        var menuIcon = node.get('menuIcon');
        if (menuIcon) {
            form.down('[name=menuIcon.id]').setValue(menuIcon.id);
        }
        //背景
        var menuBg = node.get('menuBg');
        if (menuBg) {
            form.down('[name=menuBg.id]').setValue(menuBg.id);
        }
        form.loadRecord(node);
    }
    var win = new Ext.dataDict.EditDataDictWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: APP_DATADICT_PATH + '/saveDictMenu',
                        success: function (form, action) {
                            fn(Ext.decode(action.result.datas));
                            win.close();
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('提示', '请求发送失败!');
                        }
                    });
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
