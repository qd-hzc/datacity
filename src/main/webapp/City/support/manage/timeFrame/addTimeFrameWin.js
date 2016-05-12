/**
 * Created by wgx on 2016/1/6.
 */
/**
 *
 */
Ext.define('Ext.addTimeFrameWin',{
    extend:'Ext.window.Window',
    width:600,
    modal:true,
    title:'添加时间框架窗口'
});
/**
 * 初始化
 * @param record     时间框架信息
 * @param path       地址
 * @param fn
 */
Ext.addTimeFrameWin.init=function(record,fn){
    var form = new Ext.form.Panel({
        width:'100%',
        layout:'column',
        items:[{
            xtype:'hidden',
            name:'id'
        },{
            xtype:'textfield',
            name:'name',
            allowBlank: false,
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            maxLength:50,
            margin: '20 0 0 0',
            validator:function(text){
                if(text.length && text.replace(/\s+/g, "").length<text.length){
                    return "不允许输入空格！";
                }else {
                    return true;
                }
            }
        },{
            xtype:'textfield',
            name:'code',
            fieldLabel: '代码',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        },{
            xtype:'numberfield',
            name:'order',
            fieldLabel: '排序',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            minValue: 0,
            margin: '20 0 0 0'
        },{
            xtype:'textfield',
            name:'equation',
            fieldLabel: '公式',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        },{
            xtype:'textarea',
            name:'comments',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            maxLength:1000,
            margin: '20 0 20 0'
        }]
    });
    var win=new Ext.addTimeFrameWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if (fn) {
                    if (form.isValid()) {
                        if(record)
                            fn(form.getForm().getFieldValues());
                        else
                            fn(form.getForm().getFieldValues());
                    }
                }
            }
        },{
            text: '关闭',
            handler: function(){
                win.close();
            }
        }]
    });
    if(record){
        form.loadRecord(record);
        win.setTitle('修改时间框架窗口');
    }
    win.show();
    return win;
}