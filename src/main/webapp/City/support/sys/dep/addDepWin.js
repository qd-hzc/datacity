/**
 * Created by Administrator on 2015/12/31 0031.
 * 添加指标口径窗口
 */
Ext.define('Ext.addDepWin', {
    extend: 'Ext.window.Window',
    width: 600,
    layout: 'fit',
    modal: true,
    closeAction: 'destroy',
    title: '添加部门'
});
/**
 * 初始化
 */
Ext.addDepWin.init = function (pDep, fnt) {
    var form = new Ext.form.Panel({
        height: '100%',
        layout: 'vbox',
        bodyPadding: '5 5 0',
        width: '80%',
        items: [{
            xtype: 'fieldset',
            width: '100%',
            height: 200,
            title: '部门信息', // title or checkboxToggle creates fieldset header
            defaults: {
                border: false,
                width: '100%',
                layout: 'column'
            },
            layout: 'vbox',
            items: [{
                xtype: 'panel',
                defaults: {
                    xtype: 'textfield',
                    columnWidth: 1 / 2,
                    margin: '5 20',
                    labelAlign: 'right',
                    labelWidth: 80
                },
                items: [{
                    xtype: 'numberfield',
                    hidden:true,
                    fieldLabel: '部门id',
                    name: 'id'
                }, {
                    xtype: 'numberfield',
                    hidden:true,
                    fieldLabel: '上级部门id',
                    allowBlank: false,
                    value: pDep,
                    name: 'pDep'
                }, {
                    fieldLabel: '部门名称',
                    name: 'depName',
                    allowBlank: false
                }, {
                    title: 'Column 2',
                    fieldLabel: '部门简称',
                    name: 'depShortName'
                }]
            }, {
                xtype: 'panel',
                defaults: {
                    xtype: 'textfield',
                    columnWidth: 1 / 2,
                    margin: '5 20',
                    labelAlign: 'right',
                    labelWidth: 80
                },
                items: [{
                    fieldLabel: '部门级别',
                    xtype: 'numberfield',
                    name: 'depLevel'
                }, {
                    fieldLabel: '部门电话',
                    name:'depPhone'
                }]
            }, {
                xtype: 'panel',
                defaults: {
                    xtype: 'textfield',
                    columnWidth: 1 / 2,
                    margin: '5 20',
                    labelAlign: 'right',
                    labelWidth: 80
                },
                items: [{
                    fieldLabel: '部门传真',
                    name: 'depFax'
                }, {
                    fieldLabel: '部门邮箱',
                    name: 'depEmail'
                }]
            }]
        }]
    });

    var win = new Ext.addDepWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt) {
                    if (form.isValid()) {
                        fnt(form.getValues());
                    }
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
    return win;
};

