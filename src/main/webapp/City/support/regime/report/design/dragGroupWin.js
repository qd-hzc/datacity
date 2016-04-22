/**
 * Created by wxl on 2016/1/20 0020.
 * 拖拽分组时的弹出框
 */
createModel('Ext.dragGroupWin', function () {
    Ext.define('Ext.dragGroupWin', {
        extend: 'Ext.window.Window',
        width: 450,
        modal: true,
        title: '拖拽分组窗口'
    });
});
/**
 * 初始化窗口
 * @param node 拖动的节点
 * @param overModel 目标节点
 * @param fn 回调函数
 */
Ext.dragGroupWin.init = function (node, overModel, fn) {
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
                {boxLabel: '加为下级', name: 'levelType', inputValue: '2', checked: true},
                {boxLabel: '加为属性', name: 'levelType', inputValue: '3', disabled: overModel.isRoot()}
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
    var win = new Ext.dragGroupWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (form.isValid) {
                    var data = form.getValues();
                    if (data.levelType == 3 && data.level == 1) {
                        Ext.Msg.alert('警告', '加为属性时不能选择拖动下级!');
                        return;
                    }
                    win.close();
                    fn(data);
                }
            }
        }, {
            text: '取消',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
};
