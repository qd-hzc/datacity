/**
 * Created by wxl on 2016/1/21 0021.
 * 编辑节点窗口
 */
createModel('Ext.addNodeInfoWin', function () {
    Ext.define('Ext.addNodeInfoWin', {
        extend: 'Ext.window.Window',
        width: 350,
        modal: true,
        title: '编辑节点窗口'
    });
});
/**
 * 初始化编辑窗口
 * @param node 要修改的节点
 */
Ext.addNodeInfoWin.init = function (node, fn) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{//名字
            xtype: 'textfield',
            name: 'dataName',
            allowBlank: false,
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 0 0',
            hidden: node ? node.get('dataType') == METADATA_TYPE.ITEM : false
        }, {
            xtype: 'combobox',
            name: 'dataInfo1',
            fieldLabel: '口径',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: contextPath + '/support/regime/report/designReport/getItemCaliber',
                    extraParams: {
                        itemId: node ? node.get('dataValue') : 0
                    }
                },
                autoLoad: true
            }),
            columnWidth: 0.9,
            margin: '20 0 20 0',
            hidden: node ? node.get('dataType') != METADATA_TYPE.ITEM : true
        }, {
            xtype: 'combobox',
            fieldLabel: '部门<b style="color:red">*</b>',
            labelWidth: 70,
            name: 'dataInfo2',
            columnWidth: 0.9,
            margin: '0 0 20 0',
            labelAlign: 'right',
            displayField: 'depName',
            valueField: 'id',
            allowBlank: false,
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/getItemDep',
                    extraParams: {
                        itemId: node ? node.get('dataValue') : 0
                    }
                },
                autoLoad: true
            }),
            hidden: rptDesignType == RPT_DESIGN_TYPE.SYNTHESIS || (node ? node.get('dataType') != METADATA_TYPE.ITEM : true)
        }, {//节点属性
            xtype: 'radiogroup',
            fieldLabel: '节点属性',
            labelWidth: 70,
            labelAlign: 'right',
            columns: 2,
            columnWidth: 0.9,
            margin: '20 0 20 0',
            items: [
                {
                    boxLabel: '实节点', name: 'isRealNode', inputValue: 1,
                    handler: function (_this, checked) {
                        var nameField = form.down('[name=dataName]');
                        if (checked) {//实节点
                            nameField.setValue('<空白节点>');
                            nameField.setEditable(false);
                        } else {
                            nameField.setValue(node ? node.get('dataName') : '');
                            nameField.setEditable(true);
                        }
                    }
                },
                {boxLabel: '虚节点', name: 'isRealNode', inputValue: 0, checked: true}
            ],
            hidden: node ? node.get('dataType') == METADATA_TYPE.ITEM : false
        }]
    });
    if (node) {
        form.loadRecord(node);
    }
    var win = new Ext.addNodeInfoWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    var datas = form.getValues();
                    if(datas.dataName=='<空白节点>'&&!datas.isRealNode){
                        Ext.Msg.alert('提示', '虚节点不能使用此名称!');
                        return;
                    }
                    if (node) {
                        node.set('dataName', datas.dataName);
                        node.set('isRealNode', datas.isRealNode);
                        node.set('dataInfo1', datas.dataInfo1);
                        node.set('dataInfo2', datas.dataInfo2);
                        win.close();
                        if (fn) {
                            fn();
                        }
                    } else {
                        if (fn) {
                            fn(datas);
                        }
                        win.close();
                    }
                } else {
                    Ext.Msg.alert('提示', '请补全信息');
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
