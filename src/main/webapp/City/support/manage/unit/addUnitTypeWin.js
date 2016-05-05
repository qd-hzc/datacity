/**
 * Created by zhoutao on 2016/1/4.
 */
Ext.define('Ext.addUnitTypeWin', {
    extend: 'Ext.window.Window',
    width: 400,
    title: '添加单位类型'
});

Ext.addUnitTypeWin.init = function (fun) {
    var addUnitTypeForm = Ext.create('Ext.form.Panel', {
        id: 'addUnitTypeForm',
        bodyPadding: 10,
        defaultType: 'textfield',
        items: [{
            fieldLabel: '类型名称<span style="color: red">*</span>',
            allowBlank: false,
            labelWidth: 100,
            labelAlign: 'right',
            name: 'name',
            anchor: '100%'
        }, {
            fieldLabel: '类型说明',
            xtype: 'textareafield',
            labelWidth: 100,
            labelAlign: 'right',
            grow: true,
            name: 'comments',
            anchor: '100%'
        }]
    });

    var addUnitTypeWin = Ext.create('Ext.addUnitTypeWin', {
        items: [addUnitTypeForm],
        buttons: [{
            text: '保存',
            handler: function () {
                if (addUnitTypeForm.isValid()) {
                    addUnitTypeForm.submit({
                        url: GLOBAL_PATH + "/support/unit/unitManager/addUnitType",
                        success: function (form, action) {
                            Ext.Msg.alert('提示11', action.result.msg);
                            addUnitTypeWin.destroy();
                            fun();//回调
                        },
                        failure: function (form, action) {
                            if (action.response.status == 200) {
                                Ext.Msg.alert('提示', '登录超时', function () {
                                    window.location.href = contextPath;
                                });
                            } else {
                                Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                            }
                        }
                    });
                }
            }

        }]
    });

    addUnitTypeWin.show();
    return addUnitTypeWin;
}
