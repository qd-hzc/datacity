/**
 * 修改菜单
 * Created by CRX on 2016/3/10.
 */
Ext.define('Ext.manageThemeContextMenuWin', {
    extend: 'Ext.window.Window',
    width: 450,
    height: 120,
    modal: true
});
Ext.manageThemeContextMenuWin.init = function (parentId, record, fn) {
    var manageThemeForm = Ext.create('Ext.form.Panel', {
        width: '100%',
        height: 80,
        items: [
            {
                xtype: 'hidden',
                name: 'parentId',
                value: parentId
            }, {
                xtype: 'hidden',
                name: 'id',
                value: record ? record.get('id') : null
            }, {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: '菜单名称<font color="red">*</font>',
                labelWidth: 100,
                labelAlign: 'right',
                margin: '10 20',
                width: '80%',
                allowBlank: false,
                blankText: '请填写菜单名称',
                maxLength: 15,
                maxLengthText: '最多15个字符',
                enforceMaxLength: true,
                value: record ? record.get('name') : null
            }
        ]
    });
    var manageThemeContextMenuInfo = Ext.create('Ext.manageThemeContextMenuWin', {
        title: '导航栏信息',
        layout: 'border',
        items: [
            manageThemeForm
        ],
        buttons: [
            {
                text: '保存',
                handler: function () {
                    var form = manageThemeForm.getForm();
                    if (form.isValid()) {
                        form.submit({
                            url: contextPath + '/resourcecategory/themes/manageThemesController/saveOrUpdateManageTheme',
                            waitTitle: '提示',
                            waitMsg: '正在提交数据...',
                            method: 'POST',
                            success: function (form, action) {
                                Ext.Msg.alert('提示', action.result.msg);
                                if (action.result.success) {
                                    manageThemeContextMenuInfo.close();
                                    eval(fn)(action.result.datas)
                                }
                            },
                            failure: function (form, action) {
                                Ext.Msg.alert('提示', action.result.msg);
                            }
                        });
                    }
                }
            }, {
                text: '取消',
                handler: function () {
                    manageThemeContextMenuInfo.close();
                }
            }
        ]
    });
    manageThemeContextMenuInfo.show();
}
