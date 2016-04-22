/**
 * Created by wys on 2016/2/2.
 */
Ext.define('Ext.batchWin', {
    extend: 'Ext.window.Window',
    width: 600,
    height: 500,
    layout: 'fit',
    modal: true,
    closeAction: 'destroy',
    title: '批量操作'
});
/**
 * 初始化
 */
Ext.batchWin.init = function () {
    var tmpStore = new Ext.data.Store();
    var tabPanel = new Ext.tab.Panel({
        items: [{
            title: '单表'
        }, {
            title: '多表'
        }]
    });

    var win = new Ext.batchWin({
        items: [tabPanel]
    });
    win.show();
    return win;
};