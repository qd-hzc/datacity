/**
 * Created by Administrator on 2016/1/4 0004.
 * 添加指标分组内容窗口
 */
createModel('Ext.addItemGroupInfoWin', function () {
    Ext.define('Ext.addItemGroupInfoWin',{
        extend: 'Ext.window.Window',
        width: 600,
        modal: true,
        title: '添加分组内容窗口'
    });
});
/**
 * 初始化添加指标内容窗口
 * @param groupRecord 分组
 * @param record 指标内容
 * @param fn 回调
 */
Ext.addItemGroupInfoWin.init= function (groupRecord, record, fn) {
    var caliberStore= new Ext.data.Store({
        fields: ['id','name'],
        data:record?record.get('item').itemCalibers:[]
    });
    var form=new Ext.form.Panel({
        width: '100%',
        layout: 'column',
        items: [{
            xtype: 'hidden',
            name: 'id'
        },{
            xtype: 'hidden',
            name: 'groupId',
            value: groupRecord?groupRecord.get('id'):''
        },{
            xtype: 'hidden',
            name: 'status',
            value: 1
        },{
            xtype: 'hidden',
            name: 'sortIndex'
        },{
            xtype:'hidden',
            name: 'groupName',
            value: groupRecord?groupRecord.get('name'):''
        },{
            xtype: 'combobox',
            name: 'itemId',
            fieldLabel: '选择指标',
            margin: '20 0 0 0',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            columnWidth: 0.45,
            pageSize: 15,
            store: new Ext.data.Store({
                fields: ['id','name'],
                proxy: {
                    type: 'ajax',
                    url: Global_Path+'/getItemsForPage',
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    },
                    extraParams: {
                        status: 1
                    }
                },
                autoLoad: true
            }),
            matchFieldWidth: false,
            queryMode: 'remote',
            minChars:0,
            queryParam: 'name',
            listeners:{
                beforeselect: function (_this, record) {
                    item= record.data;
                    form.down('[name="itemName"]').setValue(record.get('name'));
                    form .down('[name="depId"]').setValue(record.get('department').id);
                    var caliberCombo= form.down('[name="caliberId"]');
                    caliberStore.loadRawData(record.get('itemCalibers'));
                    caliberCombo.setValue(record.get('caliberId'));
                }
            }
        },{
            xtype: 'textfield',
            name: 'itemName',
            allowBlank: false,
            fieldLabel: '名称<b style="color:red">*</b>',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0'
        },{
            xtype: 'querypicker',
            name: 'depId',
            fieldLabel: '部门',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.45,
            margin: '20 0 0 0',
            //valueField: 'id',
            displayField: 'depName',
            store: new Ext.data.TreeStore({
                fields: ['id','depName'],
                proxy: {
                    type: 'ajax',
                    url: GLOBAL_PATH + '/support/sys/dep/queryDepTreeByName'
                },
                root: {
                    id: 0,
                    depName: '组织机构',
                    expanded: true
                },
                autoLoad: true
            }),
            queryParam: 'depName'
        },{
            xtype: 'combobox',
            name: 'caliberId',
            fieldLabel: '口径',
            labelWidth: 70,
            labelAlign: 'right',
            displayField: 'name',
            valueField: 'id',
            store: caliberStore,
            columnWidth: 0.45,
            margin: '20 0 0 0',
            queryMode: 'local'
        },{
            xtype: 'textarea',
            name: 'comments',
            fieldLabel: '备注',
            labelWidth: 70,
            labelAlign: 'right',
            columnWidth: 0.9,
            margin: '20 0 20 0'
        }]
    });
    var win=new Ext.addItemGroupInfoWin({
        items: [form],
        buttons:[{
            text: '保存',
            handler: function () {
                if(form.isValid()){
                    if(form.getValues().itemId){
                        form.submit({
                            url: Global_Path+'/saveItemGroupInfo',
                            success: function(form, action) {
                                Ext.Msg.alert('成功', action.result.msg);
                                win.close();
                                if(fn){
                                    fn();
                                }
                            },
                            failure: function(form, action) {
                                Ext.Msg.alert('失败', "保存失败");
                            }
                        });
                    }else{
                        Ext.Msg.alert('提示','请选择一个指标');
                    }
                }
            }
        },{
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    if(record){
        form.loadRecord(record);
        win.setTitle('修改分组内容窗口');
        form.down('[name="itemId"]').setValue(record.get('item').id);
        var dep= record.get('department');
        if(dep){
            form.down('[name="depId"]').setValue(dep.id);
        }
    }
    win.show();
};
