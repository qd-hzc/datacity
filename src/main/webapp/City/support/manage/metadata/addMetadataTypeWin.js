/**
 * Created by wgx on 2016/1/14.
 */
/**
 * Created by wgx on 2016/1/14.
 */
Ext.define('Ext.addMetadataTypeWin',{
    extend:'Ext.window.Window',
    width:600,

    modal:true,
    title:'添加系统元数据类型窗口'
});
/**
 * 初始化
 * @param record     系统元数据类型
 * @param path       地址
 * @param fn
 */
Ext.addMetadataTypeWin.init=function(record,fn){
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
            margin: '20 0 0 0'
        },{
            xtype: 'combobox',
            name: 'infoPage',
            fieldLabel: '信息页面',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text','value'],
                data: [{
                    text: '默认页面',
                    value:'defaultPage'
                },{
                    text:'数据类型页面',
                    value:'unitdataPage'
                }]
            }),
            value: 'defaultPage',
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
    if (record) {
        form.loadRecord(record);
    }
    var win=new Ext.addMetadataTypeWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if(form.isValid()){
                    if (fn) {
                        if (form.isValid()) {
                            if(record)
                                fn(form.getForm().getFieldValues());
                            else
                                fn(form.getForm().getFieldValues());
                        }
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
        win.setTitle('修改系统元数据窗口');
    }
    win.show();
    return win;
}
