/**
 * Created by wgx on 2016/2/23.
 */
Ext.define('Ext.rejectReportWin', {
    extend: 'Ext.window.Window',
    width: 600,
    resizable:false,
    modal: true,
    title: '驳回'
});
/**
 * 初始化
 * @param record     系统元数据类型
 * @param fn
 */
Ext.rejectReportWin.init = function (record, fn) {
    var form =  new Ext.form.Panel({
        frame : false,
        border : true,
        layout:'column',
        height : 200,
        items : [{
            xtype:'textarea',
            name:'rejectReason',
            fieldLabel: '驳回原因',
            labelWidth: 70,
            height:170,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win=new Ext.rejectReportWin({
        frame : false,
        border : false,
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if(fn){
                    fn(Ext.query("*[name=rejectReason]")[0].value);
                }
                win.close();
            }
        },{
            text: '关闭',
            handler: function(){
                win.close();
            }
        }]
    });
    win.show();
    return win;
}