/**
 * 主宾栏复制节点
 * Created by Paul on 2016/1/26.
 */
/**复制节点*/
function infoDropCopyHandler(node, overModel, callback) {
    var form = new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'radiogroup',
            fieldLabel: '拖拽级别',
            labelWidth: 70,
            labelAlign: 'right',
            columns: 3,
            columnWidth: 0.9,
            margin: '20 0 0 0',
            items: [
                {boxLabel: '加为同级', name: 'levelType', inputValue: '1', disabled: overModel.isRoot()},
                {boxLabel: '加为下级', name: 'levelType', inputValue: '2', checked: true}
            ]
        }, {
            xtype: 'radiogroup',
            fieldLabel: '子级关系',
            labelWidth: 70,
            labelAlign: 'right',
            columns: 3,
            columnWidth: 0.9,
            margin: '20 0 0 0',
            items: [
                {boxLabel: '拖动下级', name: 'level', inputValue: '1', disabled: node.isLeaf()},
                {boxLabel: '拖动本级', name: 'level', inputValue: '2', checked: true},
                {boxLabel: '全部拖动', name: 'level', inputValue: '3', disabled: node.isLeaf()}
            ]
        }, {
            xtype: 'textfield',
            name: 'dataName',
            allowBlank: false,
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0',
            value: node.get('dataName')
        }]
    });
    var mw = Ext.create('Ext.window.Window', {
        width: 500,
        modal: true,
        title: '信息',
        items: [form],
        buttons: [
            {
                text: '确定',
                handler: function () {
                    var data = form.getForm().getValues();
                    callback(data);
                    mw.close();
                }
            },
            '-',
            {
                text: '取消',
                handler: function () {
                    mw.close();
                }
            }
        ]
    });
    mw.show();
}
