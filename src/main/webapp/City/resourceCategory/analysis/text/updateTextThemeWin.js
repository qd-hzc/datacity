/**
 * Created by wgx on 2016/3/16.
 */
createModel('Ext.updateTextThemeWin', function () {
    Ext.define('Ext.updateTextThemeWin', {
        extend: 'Ext.window.Window',
        width: 600,
        height: 250,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.updateTextThemeWin.init = function(record,fn){
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
            margin: '20 0 0 0'
        },{
            xtype: 'combobox',
            name: 'modelId',
            fieldLabel: '选择模板',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: new Ext.data.Store({
                fields: ['name','id'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/resourceCategory/analysis/text/queryTextModel'
                },
                autoLoad: true
            }),
            columnWidth: 0.45,
            margin: '20 0 0 0'
        },{
            fieldLabel: '排序',
            xtype: 'numberfield',
            name: 'sortIndex',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        },{
            xtype: 'combobox',
            name: 'contentSortType',
            fieldLabel: '内容排序',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'text',
            valueField: 'value',
            store: new Ext.data.Store({
                fields: ['text','value'],
                data: [{
                    text: '根据分析日期排序',
                    value:'analysisDate'
                },{
                    text:'根据索引排序',
                    value:'sortIndex'
                }]
            }),
            value: 'analysisDate',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        },{
            xtype:'textarea',
            name:'infos',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win=new Ext.updateTextThemeWin({
        items: [form],
        buttons: [{
            text: '保存',
            handler: function () {
                if(form.isValid()){
                    if (fn) {
                        fn(form.getForm().getFieldValues());
                    }
                    win.close();
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
        win.setTitle('修改分析主题窗口');
    }else{
        win.setTitle('添加分析主题窗口');
    }
    win.show();
    return win;

}

