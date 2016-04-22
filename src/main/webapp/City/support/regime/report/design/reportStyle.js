/**
 * 表样设计
 *
 * Created by Paul on 2016/1/27.
 */
createModel('Ext.reportStyle', function () {
    Ext.define('Ext.reportStyle', {
        extend: 'Ext.window.Window',
        width: 450,
        modal: true,
        title: '拖拽分组窗口'
    });
});

Ext.reportStyle.init = function () {
    return Ext.create('Ext.panel.Panel', {
        title: '设计',
        html: '这是设计表样内容区'
    });
}
