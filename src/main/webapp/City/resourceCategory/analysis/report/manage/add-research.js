/**
 * 新建模板
 *
 * Created by HZC on 2016/2/24.
 */
Ext.define('Ext.customResearchAddWin', {
    extend: 'Ext.window.Window',
    height: 270,
    width: 450,
    modal: true
});

/**
 *
 * @param researchGroupId
 * @param rc 记录
 * @param fn
 */
Ext.customResearchAddWin.init = function (researchGroupId, rc, fn) {
    var resourceStore = Ext.create('Ext.data.Store', {
        fields: ['id', 'name'],
        proxy: {
            type: 'ajax',
            url: contextPath + '/support/dataSet/queryDataSet',
            reader: {
                type: 'json',
                rootProperty: 'datas'
            }
        },
        autoLoad: true
    });
    var researchForm = Ext.create('Ext.form.FormPanel', {
        frame: false,
        border: true,
        region: 'center',
        layout: 'column',
        items: [
            {
                xtype: 'hidden',
                name: 'id',
                value: rc ? rc.get('id') : null
            }, {
                xtype: 'hidden',
                name: 'researchGroupId',
                value: researchGroupId
            }, {
                xtype: 'hidden',
                name: 'type',
                value: rc ? rc.get('type') : 1
            }, {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: '名称<font color="red">*</font>',
                value: rc ? rc.get('name') : null,
                labelWidth: 70,
                labelAlign: 'right',
                columnWidth: .9,
                allowBlank: false,
                blankText: '必填项',
                mexLength: 15,
                maxLengthText: '最多15个字符',
                enforceMaxLength: true,
                margin: MARGIN_ROW_SPACE
            }, {
                xtype: 'combobox',
                name: 'resourceId',
                value: rc ? ( rc.get('dataSet') ? rc.get('dataSet').id : null) : null,
                fieldLabel: '数据源',
                store: resourceStore,
                labelAlign: 'right',
                labelWidth: 70,
                displayField: 'name',
                valueField: 'id',
                columnWidth: .9,
                allowBlank: true,
                blankText: '必填项',
                margin: MARGIN_ROW_SPACE,
                pageSize: 15,
                queryParam: 'name',
                minChars: 1
            }, {
                xtype: 'textareafield',
                name: 'comments',
                value: rc ? rc.get('comments') : null,
                fieldLabel: '说明',
                labelWidth: 70,
                height: 100,
                labelAlign: 'right',
                columnWidth: .9,
                margin: MARGIN_ROW_SPACE
            }
        ]
    });
    var addResearchWin = Ext.create('Ext.customResearchAddWin', {
        title: '自定义查询模板',
        frame: false,
        border: false,
        layout: 'border',
        items: [researchForm],
        buttons: [
            {
                text: '保存',
                handler: function () {
                    if (researchForm.getForm().isValid()) {
                        researchForm.getForm().submit({
                            url: contextPath + '/resourcecategory/analysis/report/customResearchManage/saveCustomResearch',
                            clientValidation: true,
                            waitTitle: '提示',
                            waitMsg: '正在提交数据...',
                            method: 'POST',
                            success: function (form, action) {
                                var datas = action.result.datas;
                                var msg = action.result.msg;
                                Ext.Msg.alert('提示', msg, function (id) {
                                    if ('ok' == id) {
                                        addResearchWin.close();
                                        if (action.result.code == 200) {
                                            eval(fn)(datas);
                                        }
                                    }
                                });
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
                    addResearchWin.close();
                }
            }
        ]
    });
    addResearchWin.show();
};
