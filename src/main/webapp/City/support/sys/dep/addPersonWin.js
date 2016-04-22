/**
 * Created by wys on 2016/1/13.
 */
Ext.define('Ext.addPersonWin', {
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
Ext.addPersonWin.init = function ( fnt,isCreate,record) {

    var form = new Ext.form.Panel({
        height: '100%',
        layout: 'vbox',
        bodyPadding: '5 5 0',
        width: '80%',
        items: [{
            xtype: 'fieldset',
            width: '100%',
            height: 200,
            title: '人员信息', // title or checkboxToggle creates fieldset header
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
                    fieldLabel: '人员id',
                    name: 'id'
                }, {
                    fieldLabel: '人员名称',
                    name: 'name',
                    allowBlank: false
                }, {
                    title: 'Column 2',
                    fieldLabel: '联系方式',
                    name: 'connect'
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
                    fieldLabel: '人员职务',
                    xtype: 'numberfield',
                    name: 'duty'
                }, {
                    fieldLabel: '电话',
                    name:'tel'
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
                    fieldLabel: '备注',
                    name: 'personInfo'
                }]
            }]
        }]
    });
    if(!isCreate){
        form.loadRecord(record)
    }
    var win = new Ext.addPersonWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fnt) {
                    if (form.isValid()) {
                        if(isCreate)
                            fnt(form.getForm().getFieldValues());
                        else
                            fnt(form.getForm().getFieldValues());
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