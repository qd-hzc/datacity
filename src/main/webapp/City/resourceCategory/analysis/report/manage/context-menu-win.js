/**
 * 自定义查询分组右键菜单
 *
 * Created by HZC on 2016/2/24.
 */

Ext.define('Ext.researchGroupContextMenuWin', {
    extend: 'Ext.window.Window',
    height: 120,
    width: 340,
    modal: true
});
/**
 * 显示菜单窗口
 * @param prec 父亲（自定义查询分组）
 * @param rec 当前（自定义查询分组）
 * @param fn 回调函数
 */
Ext.researchGroupContextMenuWin.init = function (prec, rec, fn) {
    var researchGroupPanel = Ext.create('Ext.form.FormPanel', {
        frame: false,
        border: true,
        region: 'center',
        items: [
            {
                xtype: 'hidden',
                name: rec ? 'id' : '_abc',
                value: rec ? rec.get('id') : null
            }, {
                xtype: 'hidden',
                name: 'parentId',
                value: prec.get('id')
            }, {
                xtype: 'textfield',
                name: 'name',
                value: rec ? rec.get('name') : null,
                fieldLabel: '名称<font color="red">*</font>',
                labelWidth: 100,
                labelAlign: 'right',
                columnWidth: .9,
                allowBlank: false,
                blankText: '必填项',
                maxLength: 15,
                maxLengthText: '最多15个字符',
                enforceMaxLength: true,
                margin: '10 0 10 10'
            }
        ]
    });
    var researchGroupWin = Ext.create('Ext.researchGroupContextMenuWin', {
        title: '自定义查询分组',
        frame: false,
        border: false,
        layout: 'border',
        items: [researchGroupPanel],
        buttons: [
            {
                text: '保存',
                handler: function () {
                    if (researchGroupPanel.getForm().isValid()) {
                        researchGroupPanel.getForm().submit({
                            url: contextPath + '/resourcecategory/analysis/report/customResearchManage/saveOrUpdateGroup',
                            clientValidation: true,
                            waitTitle: '提示',
                            waitMsg: '正在提交数据...',
                            method: 'POST',
                            success: function (form, action) {
                                var datas = action.result.datas;
                                var msg = action.result.msg;
                                Ext.Msg.alert('提示', msg, function (id) {
                                    if ('ok' == id) {
                                        researchGroupWin.close();
                                        if (action.result.code == 200) {
                                            eval(fn)(datas);
                                        }
                                    }
                                });
                            },
                            failure: function (form, acction) {
                                Ext.Msg.alert('提示', action.result.msg);
                            }
                        });
                    }
                }
            }, {
                text: '取消',
                handler: function () {
                    researchGroupWin.close();
                }
            }
        ]
    });
    researchGroupWin.show();
};
