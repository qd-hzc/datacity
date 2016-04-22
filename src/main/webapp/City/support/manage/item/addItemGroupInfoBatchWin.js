/**
 * Created by Administrator on 2016/1/5 0005.
 * 批量添加指标信息
 */
createModel('Ext.addItemGroupInfoBatchWin', function () {
    Ext.define('Ext.addItemGroupInfoBatchWin',{
        extend: 'Ext.window.Window',
        width: 800,
        modal: true,
        title: '批量添加指标',
        layout: 'hbox'
    });
});

/**
 * 批量添加分组信息
 * @param groupRecord 分组的记录
 * @param fn 回调
 */
Ext.addItemGroupInfoBatchWin.init= function (groupRecord, fn) {
    var itemParams={
        name: '',
        status: 1
    };
    var itemStore=new Ext.data.Store({
        fields: ['id','name','code','type','status','comments','department','caliberId','sortIndex','itemCalibers'],
        pageSize: 15,
        proxy: {
            type: 'ajax',
            url: Global_Path+'/getItemsForPage',
            extraParams: itemParams,
            reader: {
                type: 'json',
                rootProperty: 'datas'
            }
        },
        autoLoad: true
    });
    var itemGrid=new Ext.grid.Panel({
        store: itemStore,
        selType: 'checkboxmodel',
        enableDD:true,
        viewConfig: {
            copy: true,
            plugins: {
                ptype: 'gridviewdragdrop',
                ddGroup: 'gridDDGroup',
                enableDrop:false
            }
        },
        flex: 2,
        height: 500,
        columns: [{
            text: '指标名',
            dataIndex: 'name',
            flex: 2
        }, {
            text: '指标代码',
            dataIndex: 'code',
            flex: 1
        }, {
            text: '指标类型',
            xtype: 'booleancolumn',
            dataIndex: 'type',
            trueText: '标准类型',
            falseText: '自用类型',
            flex: 1
        },{
            text: '默认部门',
            dataIndex: 'department',
            flex: 1,
            renderer: function (data) {
                if(data){
                    return data.depName;
                }
                return '';
            }
        }, {
            text: '默认口径',
            dataIndex: 'caliberId',
            flex: 1,
            renderer: function (value, m, record) {
                var calibers = record.get('itemCalibers');
                if (calibers && calibers.length) {
                    for (var i = 0; i < calibers.length; i++) {
                        var caliber = calibers[i];
                        if (value == caliber.id) {
                            return caliber.name;
                        }
                    }
                }
                return '';
            }
        }],
        tbar: ['<b>可选指标</b>',{
            xtype: 'textfield',
            fieldLabel: '搜索',
            labelWidth: 50,
            labelAlign: 'right',
            listeners:{
                change: function (_this, n, o) {
                    itemParams.name=n;
                    itemStore.reload({params: itemParams});
                }
            }
        }],
        bbar: {
            xtype: 'pagingtoolbar',
            store: itemStore,
            displayInfo: true
        }
    });
    //已选指标
    var selectedStore=new Ext.data.Store({
        fields: ['id','name','code','type','status','comments','depId','caliberId','sortIndex','itemCalibers'],
        data: []
    });
    var selectedGrid=new Ext.grid.Panel({
        store: selectedStore,
        selType: 'checkboxmodel',
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                ddGroup: 'gridDDGroup'
            }
        },
        flex: 1,
        height: 500,
        columns: [{
            text: '指标名',
            dataIndex: 'name',
            flex: 2
        }, {
            text: '指标代码',
            dataIndex: 'code',
            flex: 1
        }],
        tbar:['<b>请将所选指标拖到此表格中</b>','->',{
            xtype: 'button',
            text: '删除',
            handler: function () {
                var sel= selectedGrid.getSelectionModel().getSelection();
                if(sel.length){
                    selectedStore.remove(sel);
                }
            }
        }]
    });
    var win=new Ext.addItemGroupInfoBatchWin({
        items: [itemGrid,selectedGrid],
        buttons: [{
            text: '保存',
            handler: function () {
                var len=selectedStore.getCount();
                if(len){
                    var ids=[];
                    for(var i=0;i<len;i++){
                        ids.push(selectedStore.getAt(i).get('id'));
                    }
                    //发送请求,批量保存
                    Ext.Ajax.request({
                        url: Global_Path+'/saveItemGroupInfosBatch',
                        params:{
                            itemIds: ids.join(','),
                            groupId: groupRecord.get('id'),
                            groupName: groupRecord.get('name')
                        },
                        success: function(response, opts) {
                            var obj = Ext.decode(response.responseText);
                            Ext.Msg.alert('成功',obj.msg);
                            //刷新
                            win.close();
                            if(fn){
                                fn();
                            }
                        },
                        failure: function(response, opts) {
                            Ext.Msg.alert('失败',"保存失败");
                        }
                    });
                }
            }
        },{
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
};
