/**
 * 编辑人员
 * Created by wxl on 2016/2/26.
 */
createModel('Ext.personValid.EditPersonWin', function () {
    Ext.define('Ext.personValid.EditPersonWin', {
        extend: 'Ext.window.Window',
        width: 550,
        modal: true

    });
});
/**
 * 编辑人员窗口
 * @param record 当前人员
 * @param fn回调函数
 */
Ext.personValid.EditPersonWin.init = function (record, fn) {
    //自定义电话检验
    Ext.apply(Ext.form.field.VTypes, {
        phone: function (v) {
            return /^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$|(^(13[0-9]|15[0|3|6|7|8|9]|18[8|9])\d{8}$)/.test(v)
        },
        phoneText: "请输入有效电话号码，如：0591-6487256，15005059587",
        phoneMask: /[\d-]/i//只允许输入数字和-号
    });
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id',
            value: record ? record.get('id') : []
        }, {
            xtype: 'hidden',
            name: 'duty',
            value: record ? record.get('duty') : ''
        }, {
            xtype: 'textfield',
            name: 'name',
            editable: true,
            columnWidth: 0.45,
            fieldLabel: '姓名<b style="color: red">*</b>',
            allowBlank: false,
            labelWidth: 70,
            labelAlign: 'right',
            margin: '20 0 0 0',
            value: record ? record.get('name') : []
        }, {
            xtype: 'textfield',
            vtype: 'phone',
            name: 'phone',
            editable: true,
            columnWidth: 0.45,
            fieldLabel: '电话',
            labelWidth: 70,
            labelAlign: 'right',
            margin: '20 0 0 0',
            value: record ? record.get('phone') : []
        }, {
            xtype: 'textfield',
            vtype: "email",
            name: 'email',
            editable: true,
            columnWidth: 0.45,
            fieldLabel: '邮箱',
            labelWidth: 70,
            labelAlign: 'right',
            margin: '20 0 0 0',
            value: record ? record.get('email') : []
        }, {
            xtype: 'querypicker',
            name: 'department.id',
            fieldLabel: '部门<b style="color: red">*</b>',
            forceSelection: true,
            allowBlank: false,
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            valueField: 'id',
            displayField: 'depName',
            store: new Ext.data.TreeStore({
                fields: ['id', 'depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/sys/dep/queryDepTreeByName'
                },
                root: {
                    id: 0,
                    depName: '组织机构',
                    expanded: true
                },
                autoLoad: true
            }),
            queryParam: 'depName',
            value: record && record.get("department") ? record.get("department").id : '',
            validator: function () {
                var value = this.getValue();
                return value ? true : '必须为选择的值';
            }
        }, {
            xtype: 'combo',
            name: 'dutyId',
            editable: false,
            columnWidth: 0.45,
            fieldLabel: '职务<b style="color: red">*</b>',
            labelWidth: 70,
            allowBlank: false,
            labelAlign: 'right',
            margin: '20 0 0 0',
            value: record ? record.get('dutyId') : '',
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/manage/metadata/getAllDuties'
                },
                autoLoad: true
            }),
            displayField: 'name',
            valueField: 'id',
            listeners: {
                select: function (_this, record) {
                    form.getForm().findField('duty').setValue(record.get('name'));
                }
            }
        }, {
            xtype: 'combo',
            name: 'role.id',
            fieldLabel: '角色',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            /* value: (record ? record.get('roleId') : '') || '',*/
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/app/dataDict/getRoles'
                },
                autoLoad: true
            }),
            displayField: 'name',
            valueField: 'id',
            queryParam: 'name',
            forceSelection: true,
            value: record && record.get("role") ? record.get("role").id : []
        }, {
            xtype: 'textfield',
            name: 'validCode',
            editable: false,
            //hidden: !record,
            hidden: true,
            columnWidth: 0.45,
            fieldLabel: '验证码',
            labelWidth: 70,
            labelAlign: 'right',
            margin: '20 0 0 0',
            value: record ? record.get('validCode') : []
        }, {
            xtype: 'textarea',
            name: 'comments',
            editable: true,
            columnWidth: 0.9,
            fieldLabel: '说明',
            labelWidth: 70,
            labelAlign: 'right',
            margin: '20 0 20 0',
            value: record ? record.get('comments') : []
        }]
    });
    //if(record==null){
    //    form.getForm().findField("validCode").setConfig("xtype","hidden");
    // }
    var win = new Ext.personValid.EditPersonWin({
        title: record ? '编辑人员窗口' : '添加人员窗口',
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid()) {
                    form.submit({
                        url: personContextPath + '/saveStaff',
                        success: function (form, action) {
                            if (action.result.success) {
                                win.close();
                                Ext.Msg.alert('成功', action.result.msg);
                                fn();
                            } else {
                                Ext.Msg.alert('失败', action.result.msg);
                            }
                        },
                        failure: function (form, action) {
                            if (action.failureType == Ext.form.action.Action.SERVER_INVALID) {
                                Ext.Msg.alert('失败', action.result.msg);
                            } else {
                                Ext.Msg.alert('失败', '保存失败');
                            }
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
