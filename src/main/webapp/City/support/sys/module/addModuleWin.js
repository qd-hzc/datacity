/**
 * Created by Administrator on 2015/12/31 0031.
 * 添加指标口径窗口
 */
Ext.define('Ext.addModuleWin', {
    extend: 'Ext.window.Window',
    width: 600,
    layout: 'fit',
    modal: true,
    closeAction: 'destroy',
    title: '添加模块'
});

/**
 * 初始化
 */

Ext.addModuleWin.init = function (modulePid, fnt) {
    var moduleWinStateGroup = Ext.create('Ext.form.RadioGroup', {
        fieldLabel: '模块状态',
        labelAlign: 'right',
        columnWidth: .5,
        items: [{
            name: 'moduleState',
            inputValue: '1',
            boxLabel: '启用',
            checked: true
        }, {
            name: 'moduleState',
            inputValue: '0',
            boxLabel: '禁用'
        }]
    });
    var form = new Ext.form.Panel({
        height: '100%',
        layout: 'vbox',
        bodyPadding: '5 5 0',
        width: '80%',
        items: [{
            xtype: 'fieldset',
            flex: 2,
            width: '100%',
            //height: 300,
            title: '模块信息', // title or checkboxToggle creates fieldset header
            defaults: {
                border: false,
                width: '100%',
                layout: 'column'
            },
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
                    xtype: 'hidden',
                    fieldLabel: '模块id',
                    name: 'id'
                }, {
                    xtype: 'numberfield',
                    hidden:true,
                    fieldLabel: '上级模块id',
                    allowBlank: false,
                    value: modulePid,
                    name: 'modulePid'
                }, {
                    fieldLabel: '模块名称',
                    allowBlank: false,
                    name: 'moduleName'
                }, {
                    fieldLabel: '模块简称',
                    name: 'moduleShortName'
                }]
            },{
                xtype: 'panel',
                defaults: {
                    columnWidth: 1 / 2,
                    margin: '5 20',
                    labelAlign: 'right',
                    labelWidth: 80
                },
                items: [moduleWinStateGroup,{
                    xtype: 'combobox',
                    name: 'moduleType',
                    fieldLabel: '模块类型',
                    displayField: 'text',
                    valueField: 'value',
                    store: new Ext.data.Store({
                        fields: ['text','value'],
                        data: [{
                            text:'系统',
                            value:MODULE_TYPE.SYSMOD
                        },{
                            text:'模块',
                            value:MODULE_TYPE.MODMOD
                        },{
                            text:'功能',
                            value:MODULE_TYPE.FUNMOD
                        },{
                            text:'操作',
                            value:MODULE_TYPE.OPMOD
                        },{
                            text: '目录',
                            value:MODULE_TYPE.DIRMOD
                        }]
                    }),
                    value: MODULE_TYPE.MODMOD,
                    columnWidth: 0.5,
                }]
            } ,{
                xtype: 'panel',
                defaults: {
                    xtype: 'textfield',
                    columnWidth: 1 / 2,
                    margin: '5 20',
                    labelAlign: 'right',
                    labelWidth: 80
                },
                items: [{
                    fieldLabel: '模块首页',
                    name: 'moduleIndex'
                }, {
                    fieldLabel: '配置页面',
                    name: 'moduleConfig'
                }, {
                    fieldLabel: '模块参数',
                    name: 'moduleParams'
                }, {
                    fieldLabel: '模块描述',
                    name: 'moduleDesc'
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
                    fieldLabel: '模块图标',
                    name: 'moduleIcon'
                }, {
                    fieldLabel: '模块图片',
                    name: 'modulePic'
                }]
            }, {
                xtype: 'panel',
                defaults: {
                    xtype: 'textarea',
                    columnWidth: 1,
                    margin: '5 20',
                    labelAlign: 'right',
                    labelWidth: 80
                },
                items: [{
                    fieldLabel: '备注',
                    name: 'moduleComment'
                }]
            }]
        }]
    });

    var win = new Ext.addModuleWin({
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

