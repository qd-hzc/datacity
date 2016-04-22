<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/12/29 0029
  Time: 下午 6:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>指标管理</title>
    <meta charset="UTF-8"/>
    <jsp:include page="itemImp.jsp"/>
</head>
<body>
<div id="itemContainer" style="width:100%;height:100%;"></div>
<script src="<%=request.getContextPath()%>/City/support/manage/item/addItemWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/manage/item/addItemInfoWin.js"></script>
<script src="<%=request.getContextPath()%>/City/support/manage/item/addCaliberWin.js"></script>
<script>
    Ext.onReady(function () {
        //参数管理
        var commonParams = {
            itemParams: {
                name: '',
                status: 1
            },
            caliberParams: {},
            caliberSorted: true
        };
        //指标model
        createModel('Item', function () {
            Ext.define('Item', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'code',
                    type: 'string'
                }, {
                    name: 'type',
                    type: 'int'
                }, {
                    name: 'status',
                    type: 'int'
                }, {
                    name: 'comments',
                    type: 'string'
                }, {
                    name: 'department'
                }, {
                    name: 'caliberId'
                }, {
                    name: 'sortIndex',
                    type: 'int'
                }, {
                    name: 'itemCalibers'
                },{
                    name: 'itemInfos'
                }]
            });
        });
        //指标数据源
        var itemStore = new Ext.data.Store({
            model: 'Item',
            pageSize: 10,
            proxy: {
                type: 'ajax',
                api:{
                    read: '<%=request.getContextPath()%>/support/manage/item/getItemsForPage'
                },
                extraParams:commonParams.itemParams,
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            autoLoad: true,
           /* listeners: {
                beforeload: function (_this,operation) {
                    operation.params=commonParams.itemParams;
                }
            }*/
        });
        //指标选项菜单
        var itemMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改指标',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel= itemGrid.getSelectionModel().getSelection();
                    if(sel.length==1){
                        var record= sel[0];
                        Ext.addItemWin.init(record,function(rec){
                            itemStore.reload({params:commonParams.itemParams});
                        });
                    }else{
                        Ext.Msg.alert('提示','请选中一个指标修改');
                    }
                }
            },{
                text: '删除指标',
                iconCls: 'Delete',
                handler: function(){
                    var sel= itemGrid.getSelectionModel().getSelection();
                    if(sel.length){
                        Ext.Msg.confirm('警告','确定要删除么?删除后,指标体系中对应的指标也会删除!', function (btn) {
                            if(btn=='yes'){
                                var items=[];
                                for(var i=0;i<sel.length;i++){
                                    items.push(sel[i].data);
                                }
                                //发送请求,改变状态
                                Ext.Ajax.request({
                                    url: Global_Path+'/removeItems',
                                    params:{
                                        items: JSON.stringify(items)
                                    },
                                    success: function(response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        Ext.Msg.alert('成功',obj.msg);
                                        itemStore.reload({params: commonParams.itemParams});
                                        //清除口径和信息内容
                                        caliberStore.loadRawData([]);

                                    },
                                    failure: function(response, opts) {
                                        Ext.Msg.alert('失败',"操作失败");
                                    }
                                });
                            }
                        });
                    }else{
                        Ext.Msg.alert('提示','未选中指标');
                    }
                }
            },'-',{
                text: '启用指标',
                iconCls: 'Lockopen',
                handler: function(){
                    setItemStatus(1);
                }
            }, {
                text: '废弃指标',
                iconCls: 'Lock',
                handler: function () {
                    setItemStatus(0);
                }
            }, '-', {
                text: '添加口径',
                iconCls: 'Pageadd',
                handler: addCaliber
            }, {
                text: '添加信息',
                iconCls: 'Noteadd',
                handler: function () {
                    addItemInfo();
                }
            }]
        });
        //指标表格菜单
        var itemContainerMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加指标',
                iconCls: 'Add',
                handler: function () {
                    Ext.addItemWin.init(null,function(rec){
                        itemStore.reload({params:commonParams.itemParams});
                    });
                }
            }]
        });
        //指标表格
        var itemGrid = new Ext.grid.Panel({
            width: '100%',
            flex: 1,
            selType: 'checkboxmodel',
            store: itemStore,
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
            }, {
                text: '指标状态',
                xtype: 'booleancolumn',
                dataIndex: 'status',
                trueText: '启用',
                falseText: '废弃',
                flex: 1
            }, {
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
            },{
                text: '备注',
                dataIndex: 'comments',
                flex: 2
            }],
            listeners: {
                cellclick: function (_this, td, cellIndex, record) {
                    commonParams.caliberParams.itemId = record.get('id');
                    commonParams.caliberSorted=true;
                    caliberStore.loadRawData(record.get('itemCalibers'));
                    infoStore.load({params:commonParams.caliberParams});
                    //菜单操作
                    itemMenu.hide();
                    itemContainerMenu.hide();
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    itemMenu.showAt(e.getPoint());
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    //弹出菜单
                    itemContainerMenu.showAt(e.getPoint());
                },
                containerclick: function () {
                    itemMenu.hide();
                    itemContainerMenu.hide();
                }
            },
            tbar: ['<b>指标管理</b>', '-', {
                xtype: 'textfield',
                fieldLabel: '搜索',
                labelWidth: 50,
                labelAlign: 'right',
                listeners:{
                    change: function (_this, n, o) {
                        commonParams.itemParams.name=n;
                        itemStore.loadPage(1,{params:commonParams.itemParams});
                    }
                }
            },{
                xtype: 'combobox',
                fieldLabel: '状态',
                labelWidth: 50,
                labelAlign: 'right',
                displayField: 'text',
                valueField: 'value',
                store: new Ext.data.Store({
                    fields: ['text','value'],
                    data: [{text: '全部',value: null},{text: '启用',value:1},{text:'废弃',value:0}]
                }),
                value: commonParams.itemParams.status,
                listeners:{
                    change: function (_this, n, o) {
                        commonParams.itemParams.status=n;
                        itemStore.loadPage(1,{params:commonParams.itemParams});
                    }
                }
            }, '->',{
                xtype: 'button',
                iconCls: 'Add',
                text: '添加指标',
                handler: function () {
                    Ext.addItemWin.init(null,function(rec){
                        itemStore.reload({params:commonParams.itemParams});
                    });
                }
            }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: itemStore,
                displayInfo: true
            }
        });
        //指标口径 菜单
        var caliberMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改口径',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel= caliberGrid.getSelectionModel().getSelection();
                    Ext.addCaliberWin.init('',sel[0], function (data) {
                        caliberStore.reload({params: commonParams.caliberParams});
                        itemStore.reload({params:commonParams.itemParams});
                    });
                }
            }, {
                text: '删除口径',
                iconCls: 'Delete',
                handler: function(){
                    var sel= caliberGrid.getSelectionModel().getSelection();
                    if(sel.length){
                        Ext.Msg.confirm('警告','确定要删除么?', function (btn) {
                            if(btn=='yes'){
                                var calibers=[];
                                for(var i=0;i<sel.length;i++){
                                    calibers.push(sel[i].data);
                                }
                                //发送请求,改变状态
                                Ext.Ajax.request({
                                    url: Global_Path+'/removeCalibers',
                                    params:{
                                        calibers: JSON.stringify(calibers)
                                    },
                                    success: function(response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        Ext.Msg.alert('成功',obj.msg);
                                        //刷新
                                        caliberStore.reload({params: commonParams.caliberParams});
                                        itemStore.reload({params: commonParams.itemParams});
                                    },
                                    failure: function(response, opts) {
                                        Ext.Msg.alert('失败',"操作失败");
                                    }
                                });
                            }
                        });
                    }else{
                        Ext.Msg.alert('提示','未选中指标');
                    }
                }
            },'-',{
                text: '保存顺序',
                iconCls: 'Pagesave',
                handler: function () {
                    if(!commonParams.caliberSorted){
                        var datas=[];
                        var len=caliberStore.getCount();
                        for(var i=0;i<len;i++){
                            datas.push(caliberStore.getAt(i).get('id')+':'+(i+1));
                        }
                        //发送请求,保存顺序
                        Ext.Ajax.request({
                            url: Global_Path+'/saveCaliberSorts',
                            params:{
                                datas: datas.join(',')
                            },
                            success: function(response, opts) {
                                var obj = Ext.decode(response.responseText);
                                Ext.Msg.alert('成功',obj.msg);
                                itemStore.reload({params: commonParams.itemParams});
                                commonParams.infoSorted=true;
                            },
                            failure: function(response, opts) {
                                Ext.Msg.alert('失败',"操作失败");
                            }
                        });
                    }else{
                        Ext.Msg.alert('提示','无需保存顺序');
                    }
                }
            }]
        });
        var caliberContainerMenu= new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加口径',
                iconCls: 'Add',
                handler: addCaliber
            }]
        });
        //指标口径model
        createModel('ItemCaliber', function () {
            Ext.define('ItemCaliber', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'itemId',
                    type: 'int'
                }, {
                    name: 'itemExplain',
                    type: 'string'
                }, {
                    name: 'statisticsScope',
                    type: 'string'
                }, {
                    name: 'statisticsMethod',
                    type: 'string'
                }, {
                    name: 'countMethod',
                    type: 'string'
                }, {
                    name: 'sortIndex',
                    type: 'int'
                }]
            });
        });
        //指标口径数据源
        var caliberStore = new Ext.data.Store({
            model: 'ItemCaliber',
            proxy: {
                type: 'ajax',
                api:{
                    read: '<%=request.getContextPath()%>/support/manage/item/queryCalibersByItem'
                }
            },
            autoLoad: false
        });
        //指标口径表格
        var caliberGrid = new Ext.grid.Panel({
            flex: 1,
            height: '100%',
            selType: 'checkboxmodel',
            store: caliberStore,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'caliberSortGroup'
                },
                listeners: {
                    beforedrop: function () {
                        //将caliberSorted设为false
                        commonParams.caliberSorted=false;
                    }
                }
            },
            columns: [{
                text: '口径名',
                dataIndex: 'name',
                flex: 1
            }, {
                text: '指标解释',
                dataIndex: 'itemExplain',
                flex: 1
            }, {
                text: '统计范围',
                dataIndex: 'statisticsScope',
                flex: 1
            }, {
                text: '统计方法',
                dataIndex: 'statisticsMethod',
                flex: 1
            }, {
                text: '计算方法',
                dataIndex: 'countMethod',
                flex: 1
            }],
            listeners: {
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    e.preventDefault();
                    caliberMenu.showAt(e.getPoint());
                },
                cellclick: function () {
                    caliberMenu.hide();
                    caliberContainerMenu.hide();
                },
                containerclick: function () {
                    caliberMenu.hide();
                    caliberContainerMenu.hide();
                },
                containercontextmenu: function (_this,e) {
                    //取消冒泡
                    e.preventDefault();
                    caliberContainerMenu.showAt(e.getPoint());
                }
            }
        });
        //指标信息model
        createModel('ItemInfo', function () {
            Ext.define('ItemInfo',{
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id',
                    type: 'int'
                },{
                    name: 'name',
                    type: 'string'
                },{
                    name: 'itemId',
                    type: 'int'
                },{
                    name: 'dataFormat',
                    type: 'string'
                },{name: 'unit'},{name: 'timeFrame'},{name: 'dataType'}]
            });
        });
        //指标信息数据源
        var infoStore= new Ext.data.Store({
            model: 'ItemInfo',
            proxy: {
                type: 'ajax',
                url: Global_Path+'/getItemInfosByItem'
            },
            autoLoad: false
        });
        //菜单
        var infoMenu=new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改信息',
                iconCls: 'Pageedit',
                handler: function () {
                    var sel= infoGrid.getSelectionModel().getSelection();
                    if(sel.length==1){
                        var record= sel[0];
                        Ext.addItemInfoWin.init('',record, function (data) {
                            //添加在口径表格
                            infoStore.reload({params:commonParams.caliberParams});
                        });
                    }else{
                        Ext.Msg.alert('警告','请选中一条信息修改');
                    }
                }
            },{
                text: '删除信息',
                iconCls: 'Delete',
                handler: function () {
                    var sel= infoGrid.getSelectionModel().getSelection();
                    if(sel.length){
                        Ext.Msg.confirm('警告','确定删除?', function (btn) {
                            if(btn=='yes'){
                                var ids=[];
                                for(var i=0;i<sel.length;i++){
                                    ids.push(sel[i].get('id'));
                                }
                                //发送请求,删除
                                Ext.Ajax.request({
                                    url: Global_Path+'/removeItemInfos',
                                    params:{
                                        ids: ids.join(',')
                                    },
                                    success: function(response, opts) {
                                        var obj = Ext.decode(response.responseText);
                                        Ext.Msg.alert('成功',obj.msg);
                                        //刷新
                                        infoStore.reload({params: commonParams.caliberParams});
                                    },
                                    failure: function(response, opts) {
                                        Ext.Msg.alert('失败',"操作失败");
                                    }
                                });
                            }
                        });
                    }
                }
            }]
        });
        var infoContainerMenu= new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加信息',
                iconCls: 'Add',
                handler: function () {
                    addItemInfo();
                }
            }]
        });
        //指标信息表格
        var infoGrid=new Ext.grid.Panel({
            flex: 1,
            height: '100%',
            selType: 'checkboxmodel',
            store: infoStore,
            columns: [/*{
                text: '信息名',
                dataIndex: 'name',
                flex: 2
            },*/{
                text: '时间框架',
                dataIndex: 'timeFrame',
                flex: 1,
                renderer: function (data) {
                    if(data){
                        return data.name;
                    }
                    return '';
                }
            },{
                text: '单位',
                dataIndex: 'unit',
                flex: 1,
                renderer: function (data) {
                    if(data){
                        return data.name;
                    }
                    return '';
                }
            },{
                text: '数据类型',
                dataIndex: 'dataType',
                flex: 1,
                renderer: function (data) {
                    if(data){
                        return data.name;
                    }
                    return '';
                }
            },{
                text: '数据格式',
                dataIndex: 'dataFormat',
                flex: 1
            }],
            listeners: {
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    e.preventDefault();
                    infoMenu.showAt(e.getPoint());
                },
                cellclick: function () {
                    infoMenu.hide();
                    infoContainerMenu.hide();
                },
                containerclick: function () {
                    infoMenu.hide();
                    infoContainerMenu.hide();
                },
                containercontextmenu: function (_this,e) {
                    //取消冒泡
                    e.preventDefault();
                    infoContainerMenu.showAt(e.getPoint());
                }
            }
        });
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            layout: 'vbox',
            renderTo: 'itemContainer',
            items: [itemGrid, {
                xtype: 'panel',
                width: '100%',
                flex: 1,
                border: 0,
                layout: 'hbox',
                items: [caliberGrid,infoGrid]
            }]
        });

        /**
         * 改变指标状态
         */
        function setItemStatus(status){
            var sel= itemGrid.getSelectionModel().getSelection();
            if(sel.length){
                var ids=[];
                for(var i=0;i<sel.length;i++){
                    ids.push(sel[i].get('id'));
                }
                //发送请求,改变状态
                Ext.Ajax.request({
                    url: Global_Path+'/setItemStatus',
                    params:{
                        ids: ids.join(','),
                        status: status
                    },
                    success: function(response, opts) {
                        var obj = Ext.decode(response.responseText);
                        Ext.Msg.alert('成功',obj.msg);
                        itemStore.reload({params: commonParams.itemParams});
                    },
                    failure: function(response, opts) {
                        Ext.Msg.alert('失败',"操作失败");
                    }
                });
            }else{
                Ext.Msg.alert('提示','未选中指标');
            }
        }

        /**
         * 添加指标口径
         */
        function addCaliber(){
            var sel= itemGrid.getSelectionModel().getSelection();
            if(sel.length==1){
                var itemRecord= sel[0];
                Ext.addCaliberWin.init(itemRecord.get('id'),null, function (data) {
                    //添加在口径表格
                    caliberStore.add(new ItemCaliber(data));
                    //更新表格
                    var itemCalibers= itemRecord.get('itemCalibers');
                    itemCalibers.push(data);
                    itemRecord.set('itemCalibers',itemCalibers);
                });
            }else{
                Ext.Msg.alert('提示','请选中一个指标来添加口径');
            }
        }

        /**
         * 添加指标信息
         */
        function addItemInfo(){
            var sel= itemGrid.getSelectionModel().getSelection();
            if(sel.length==1){
                var itemRecord= sel[0];
                Ext.addItemInfoWin.init(itemRecord.get('id'),null, function (data) {
                    //添加在口径表格
                    infoStore.reload({params:commonParams.caliberParams});
                });
            }else{
                Ext.Msg.alert('提示','请选中一个指标来添加信息');
            }
        }

    });
</script>
</body>
</html>
