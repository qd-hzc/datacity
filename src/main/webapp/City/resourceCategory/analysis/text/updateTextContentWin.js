/**
 * Created by wgx on 2016/3/16.
 */
createModel('Ext.updateTextContentWin', function () {
    Ext.define('Ext.updateTextContentWin', {
        extend: 'Ext.window.Window',
        width: 600,
        height: 300,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.updateTextContentWin.init = function (record, fn) {
    var isChecked = record ? record.get('status') == TEXT_CONTENT_STATUS.CHECKED : false;
    if (record && record.get("analysisDate")) {
        var analysisDate = Ext.Date.format(new Date(record.get("analysisDate")), 'Y年m月d日');
        analysisDate = analysisDate.substring(2, analysisDate.length);
    } else {
        analysisDate = "";
    }
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'type',
            fieldLabel: '类型',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            editable: false,
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text', 'value'],
                data: [{
                    text: '普通分析',
                    value: 1
                }]
            }),
            value: 1,
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            fieldLabel: '排序',
            xtype: 'numberfield',
            name: 'sortIndex',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'combobox',
            name: 'labelIds',
            fieldLabel: '标签',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            multiSelect: true,
            forceSelection: true,
            store: new Ext.data.Store({
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: TEXT_CONTEXT_PATH + '/queryTextLabels'
                },
                autoLoad: true
            }),
            minChars: 0,
            queryMode: 'remote',
            queryParam: 'name',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            id: 'analysisDate',
            fieldLabel: '时间',
            xtype: 'datefield',
            anchor: '100%',
            name: 'analysisDate',
            maxValue: new Date(),
            value: analysisDate,
            //value:new Date(record.get("analysisDate")),
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        }, {
            xtype: 'textarea',
            name: 'infos',
            fieldLabel: '说明',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win = new Ext.updateTextContentWin({
        items: [form],
        buttons: [{
            text: '保存',
            hidden: isChecked,
            handler: function () {
                if (form.isValid()) {
                    if (fn) {
                        var datas = form.getValues();
                        datas.analysisDate = dateFormat(Ext.getCmp("analysisDate").getValue());
                        var labelIds = datas.labelIds;
                        if (labelIds) {
                            datas.labelIds = labelIds.join(',');
                        } else {
                            datas.labelIds = '';
                        }
                        fn(datas);
                    }
                    win.close();
                }
            }
        }, {
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    if (record) {
        form.loadRecord(record);
        Ext.getCmp("analysisDate").setValue(analysisDate);
        if (isChecked) {
            win.setTitle('查看分析内容窗口');
        } else {
            win.setTitle('修改分析内容窗口');
        }
        //设置标签
        var labelIds = record.get('labelIds');
        if (labelIds) {
            var labelIdStrArr = labelIds.split(',');
            var labelIdArr = [];
            for (var i = 0; i < labelIdStrArr.length; i++) {
                labelIdArr.push(labelIdStrArr[i]);
            }
            form.getForm().findField("labelIds").setValue(labelIdArr);
        }

    } else {
        win.setTitle('添加分析内容窗口');
    }
    win.show();
    return win;
//将时间转化为 2011-08-20 00:00:00 格式
//解决Ext5的formPanel通过grid的store查询问题
    function dateFormat(value) {
        if (null != value) {
            return Ext.Date.format(new Date(value), 'Y-m-d H:i:s');
        } else {
            return null;
        }
    }
};

