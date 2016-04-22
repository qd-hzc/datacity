/**
 * Created by HZC on 2016/4/15.
 * 添加综合表分组
 */
createModel('Ext.addReportGroupWin', function () {
    Ext.define('Ext.addReportGroupWin', {
        extend: 'Ext.window.Window',
        width: 600,
        modal: true,
        title: '添加分组窗口'
    });
});

/**
 * 添加或修改节点的窗口
 * @param isAdd 是否为添加的窗口
 * @param node isAdd为true时,node表示其父节点,否则表示该节点
 * @returns {Ext.addReportGroupWin}
 */
Ext.addReportGroupWin.init = function (isAdd, node) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        }, {
            xtype: 'hidden',
            name: 'parentId',
            value: node.get('id')
        }, {
            xtype: 'hidden',
            name: 'sort'
        }, {
            xtype: 'textfield',
            name: 'name',
            allowBlank: false,
            fieldLabel: '分组名<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        //}, {
        //    xtype: 'combobox',
        //    name: 'status',
        //    fieldLabel: '状态',
        //    labelWidth: 70,
        //    labelAlign: 'right',
        //    displayField: 'text',
        //    valueField: 'value',
        //    store: new Ext.data.Store({
        //        fields: ['text', 'value'],
        //        data: [{text: '启用', value: 1}, {text: '废弃', value: 0}]
        //    }),
        //    value: 1,
        //    columnWidth: 0.45,
        //    margin: '20 0 0 0'
        }, {
            xtype: 'textarea',
            name: 'comments',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win = new Ext.addReportGroupWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                var nameValue = form.getValues().name;
                if (form.isValid() && nameValue.trim().length > 0) {
                    form.submit({
                        url: GLOBAL_PATH + '/support/regime/report/reportGroup/saveReportGroup',
                        success: function (form, action) {
                            Ext.Msg.alert('成功', action.result.msg);
                            win.close();
                            var obj = action.result.datas;
                            if (isAdd) {
                                node.set('leaf', false);
                                //node.expand();
                                node.set('expanded', true);
                                obj.leaf = true;
                                obj.text = obj.name;
                                var nNode = node.createNode(obj);
                                //nNode.set('leaf', true);
                                //nNode.set('text', obj.name);
                                node.appendChild(nNode);
                            } else {//修改
                                node.set('name', obj.name);
                                node.set('text', obj.name);
                                //node.set('status', obj.status);
                                node.set('comments', obj.comments);
                            }
                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('失败', "保存失败");
                        }
                    })
                } else {
                    Ext.Msg.alert('提示', '请补全信息');
                }
            }
        }, {
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    if (!isAdd) {
        form.loadRecord(node);
        win.setTitle('修改分组窗口');
    }
    win.show();
    return win;
};
