/**
 * 添加下级地区
 * Created by Paul on 2016/1/4.
 */
Ext.define('Ext.itemDictManageAddSubWin', {
    extend: 'Ext.window.Window',
    height: 320,
    width: 380,
    modal: true
});
Ext.itemDictManageAddSubWin.show = function (rec, fnt) {// 两个参数，fnt是回调函数，rec是record，可以自定义。
    var itemDictName = Ext.create('Ext.form.field.Text', {
        name: 'name',
        fieldLabel: '目录名称<font color="red">*</font>',
        labelWidth: 100,
        labelAlign: 'right',
        columnWidth: .9,
        border: false,
        maxLength: 15,
        enforceMaxLength: true,
        allowBlank: false,
        blankText: '必填项',
        maxLength: 20,
        maxLengthText: '最多20个字符',
        enforceMaxLength: true
    });
    var itemDictCode = Ext.create('Ext.form.field.Number', {
        name: 'code',
        fieldLabel: '代码<font color="red">*</font>',
        labelWidth: 100,
        labelAlign: 'right',
        columnWidth: .9,
        border: false,
        // regex : /^\d+$/,
        // regexText : '行政区划代码只能是6位数字格式',
        allowBlank: false,
        blankText: '必填项',
        maxLength: 10,
        enforceMaxLength: true,
        regex: /^\d{0,}$/,
        regexText: "只能输入数字",
        allowDecimals: false,
        hideTrigger: true,
        keyNavEnabled: false,
        mouseWheelEnabled: false
    });
    var firstLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [itemDictName]
    });
    var secondLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [itemDictCode]
    });

    var enabled = Ext.create('Ext.form.field.Radio', {
        name: 'status',
        boxLabel: '启用',
        checked: true,
        inputValue: 1
    });
    var disabled = Ext.create('Ext.form.field.Radio', {
        name: 'status',
        boxLabel: '禁用',
        checked: false,
        inputValue: 0
    });
    var itemDictStatusGroup = Ext.create('Ext.form.RadioGroup', {
        name: 'itemDictStatusGroup',
        fieldLabel: "目录状态",
        labelAlign: 'right',
        columnWidth: .9,
        items: [enabled, disabled]
    });
    var secondLineTwo = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [itemDictStatusGroup]
    });
    /*
     * 地区备注
     * */
    var itemDictComments = Ext.create('Ext.form.field.TextArea', {
        name: 'comments',
        fieldLabel: '目录备注',
        labelWidth: 100,
        labelAlign: 'right',
        columnWidth: .8,
        height: 100,
        anchor: '80%',
        maxLength: 255,
        maxLengthText: '超过长度限制',
        border: false,
        enforceMaxLength: true
    });
    /*
     * 第三行
     * */
    var thirdLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [itemDictComments]
    });


    var formPanel = Ext.create('Ext.form.FormPanel', {
        frame: false,
        border: true,
        region: 'center',
        items: [firstLine, secondLine, secondLineTwo, thirdLine]
    });
    var itemDictManageAddSubWin = Ext.create('Ext.itemDictManageAddSubWin', {
        title: '添加下级目录',
        frame: false,
        border: false,
        layout: 'border',
        items: [formPanel],
        listeners: {
            'close': function (panel, eOpts) {
            }
        },
        buttons: [{
            text: '保存',
            handler: function () {
                if (formPanel.getForm().isValid())
                    formPanel.getForm().submit({
                        url: contextPath + '/itemDict/saveItemDict',
                        clientValidation: true,
                        waitTitle: '提示',
                        waitMsg: '正在提交数据...',
                        method: 'POST',
                        params: {
                            parentId: rec.get('id')
                        },
                        success: function (form, action) {
                            var datas = Ext.decode(action.result.datas);
                            var msg = action.result.msg;
                            // 提示
                            Ext.Msg.alert('提示', msg,
                                function (id) {
                                    if ('ok' == id) {
                                        itemDictManageAddSubWin.close();
                                        // 把model信息返回给jsp页面，用于增加树信息
                                        eval(fnt)(datas);
                                    }
                                });

                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                        }
                    });
                else
                    Ext.Msg.alert('提示', '请按要求填写红框内信息');
            }
        }, {
            text: '取消',
            handler: function () {
                itemDictManageAddSubWin.close();
            }
        }]
    });
    itemDictManageAddSubWin.show();
};
