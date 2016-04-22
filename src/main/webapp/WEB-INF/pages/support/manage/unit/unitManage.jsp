<%--
  Created by IntelliJ IDEA.
  User: zhoutao
  Date: 2016/1/4
  Time: 10:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>

</head>
<body>
    <div id="unitManageDiv" style="width: 100%; height: 100%;"></div>

    <script src="<%=request.getContextPath()%>/City/support/manage/unit/addUnitTypeWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/support/manage/unit/updateUnitTypeWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/support/manage/unit/addDataTypeWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/support/manage/unit/addUnitWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/support/manage/unit/updateUnitWin.js"></script>
    <script type="application/javascript">
        selId = null;
        Ext.onReady(function(){

            //单位类型model
            if(!Ext.ClassManager.isCreated("unitType")) {
                Ext.define("unitType", {
                    extend: "Ext.data.Model",
                    fields: [{
                        name: "id",
                        type: "int"
                    },
                        {
                            name: "name",
                            type: "string"
                        },
                        {
                            name: "comments",
                            type: "string"
                        }]
                });
            }

            //单位类型数据源
            var unitTypeStore = Ext.create('Ext.data.TreeStore', {
                model : 'unitType',
                root : {
                    expanded : true,
                    id : '0',
                    text : '计量单位类型',
                    rootVisible : true
                },
                proxy : {
                    type : 'ajax',
                    url : GLOBAL_PATH+'/support/unit/unitManager/findAllUnitTypeTree',
                    reader : {
                        type : 'json',
                        rootProperty : 'unitTypes'
                    }
                },
                autoLoad : true
            });

            //单位类型树
            var unitTypeTreePanel = Ext.create('Ext.tree.Panel', {
                flex : 2.5,
                height : "100%",
                width : "100%",
                store : unitTypeStore,
                tbar : [{
                    xtype :'button',
                    text : '添加类型',
                    handler : function(){
                        Ext.addUnitTypeWin.init(function(){
                            unitTypeStore.reload();
                        });
                    }
                },{
                    xtype :'button',
                    text : '修改类型',
                    handler : function(){
                        var selNode = unitTypeTreePanel.getSelectionModel().getSelection();
                        if(selNode.length == 0 || selNode[0].get("id") == 0){
                            Ext.Msg.alert("提示", "请先选择单位类型!!!");
                            return ;
                        }else{
                            Ext.updateUnitTypeWin.init(function(){
                                unitTypeStore.reload();
                            }, selNode[0]);
                        }


                    }
                },{
                    xtype :'button',
                    text : '删除类型',
                    handler : function(){
                        var selNode = unitTypeTreePanel.getSelectionModel().getSelection();
                        if(selNode.length == 0 || selNode[0].get("id") == 0){
                            Ext.Msg.alert("提示", "请先选择单位类型!!!");
                            return ;
                        }else{
                            Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                                if (btn == 'yes') {
                                    Ext.Ajax.request({
                                        url: GLOBAL_PATH + "/support/unit/unitManager/deleteUnitType",
                                        params: {
                                            id: selNode[0].get("id")
                                        },
                                        success: function (response) {
                                            //Ext.Msg.alert("提示", Ext.decode(response.responseText).msg);
                                            unitTypeStore.reload();
                                        },
                                        failure: function (response) {
                                            Ext.Msg.alert("提示", Ext.decode(response.responseText).msg);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }],
                listeners : {
                    itemclick : function(_this, record){
                        selId = record.get("id");
                        unitStore.reload({params : {id : record.get("id")}});
                    }
                }
            });

            //单位model
            if(!Ext.ClassManager.isCreated("unit")) {
                Ext.define('unit', {
                    extend: "Ext.data.Model",
                    fields: [{
                        name: "id",
                        type: "int"
                    }, {
                        name: "name",
                        type: "string"
                    }, {
                        name: "standard",
                        type: "string"
                    }, {
                        name: "equivalentValue",
                        type: "string"
                    }, {
                        name: "internationalStandard",
                        type: "string"
                    }, {
                        name: "dataFormat",
                        type: "string"
                    }, {
                        name: "order",
                        type: "int"
                    }, {
                        name: "comments",
                        type: "string"
                    }]
                });
            }

            //单位信息数据源
            var unitStore = Ext.create('Ext.data.Store', {
                model : unit,
                pageSize : 20,
                proxy : {
                    type : 'ajax',
                    url : GLOBAL_PATH+"/support/unit/unitManager/findUnitByUnitType",
                    reader : {
                        type : "json",
                        rootProperty : "datas"
                    }
                },
                autoLoad : false

            });

            //单位表格
            var unitGrid = Ext.create('Ext.grid.Panel', {
                store : unitStore,
                flex : 1,
                columns : [{
                    header : "id",
                    dataIndex :　"id",
                    hidden : true
                },{
                    header : "unitType",
                    dataIndex :　"unitType",
                    hidden : true
                },{
                    xtype : "templatecolumn",
                    tpl : '<input name="unitIds" type="checkbox" value="{id}"/>'
                },{
                    header : "dataType",
                    dataIndex :　"dataType",
                    hidden : true
                },{
                    header : "计量单位名称",
                    dataIndex :　"name",
                    flex : 1.5

                },{
                    header : "是否是基准计量单位",
                    dataIndex :　"standard",
                    renderer : function(_this){
                        if(_this == "true")
                            return "是";
                        else
                            return "否";
                    },
                    flex : 1
                },{
                    header : "基准计量单位",
                    dataIndex :　"standardUnit",
                    renderer : function(_this){
                        if(_this == null)
                            return;
                        return _this.name;
                    },
                    flex : 1
                },{
                    header : "换算值",
                    dataIndex :　"equivalentValue",
                    flex : 1.5
                },{
                    header : "是否是国际标准",
                    dataIndex :　"internationalStandard",
                    renderer : function(_this){
                        if(_this == "true")
                            return "是";
                        else
                            return "否";
                    },
                    flex : 1
                },{
                    header : "数据类型",
                    dataIndex :　"dataType",
                    renderer : function(_this){
                        if(_this == null)
                            return;
                        return _this.name;
                    },
                    flex : 1.5
                },{
                    header : "数据格式",
                    dataIndex :　"dataFormat",
                    flex : 1.5
                }],
                tbar : ['->',{
                    xtype :'button',
                    text : '添加单位',
                    handler : function(){ var selNode = unitTypeTreePanel.getSelectionModel().getSelection();
                        if(selNode.length == 0 || selNode[0].get("id") == 0){
                            Ext.Msg.alert("提示", "请先选择单位类型!!!");
                            return ;
                        }else{
                            Ext.addUnitWin.init(selNode[0].get("id"), function(){
                                unitStore.reload({params : {id : selNode[0].get("id")}});
                            });
                        }
                    }
                },{
                    xtype :'button',
                    text : '修改单位',
                    handler : function(){
                        var selNode = unitGrid.getSelectionModel().getSelection();
                        if(selNode.length == 0){
                            Ext.Msg.alert("提示", "请先选择要修改的单位信息!!!");
                            return ;
                        }else {
                            var unitIds = document.getElementsByName("unitIds");
                            var i = 0;
                            for(var id in unitIds){
                                if(unitIds[id].value != undefined && unitIds[id].checked)
                                    i++;
                            }
                            if(i > 1){
                                Ext.Msg.alert("提示", "只能选择一个单位信息!!!");
                                return ;
                            }
                            Ext.updateUnitWin.init(selNode[0], function(){
                                unitStore.reload({params : {id : selNode[0].get("unitType").id}});
                            });
                        }
                    }
                },{
                    xtype :'button',
                    text : '删除单位',
                    handler : function(){
                        var unitIds = document.getElementsByName("unitIds");
                        var ids = "";
                        for(var id in unitIds){
                            if(unitIds[id].value != undefined && unitIds[id].checked)
                                ids += unitIds[id].value+",";
                        }
                        if(ids != "" && ids.indexOf(",")>0){
                            ids = ids.substring(0, ids.lastIndexOf(","));
                            Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                                if (btn == 'yes') {
                                    Ext.Ajax.request({
                                        url: GLOBAL_PATH + "/support/unit/unitManager/deleteUnit",
                                        params: {
                                            unitIds: ids
                                        },
                                        success: function (response) {
                                            // Ext.Msg.alert("提示", Ext.decode(response.responseText).msg);
                                            unitStore.reload({params: {id: unitTypeTreePanel.getSelectionModel().getSelection()[0].get("id")}});
                                        },
                                        failure: function (response) {
                                            Ext.Msg.alert("提示", "此计量单位已被引用,不可删除!");
                                            //Ext.Msg.alert("提示", Ext.decode(response.responseText).msg);
                                        }
                                    });
                                }
                            });
                        }else{
                            Ext.Msg.alert("提示",  "请先选中要删除的单位信息!");
                        }
                    }
                }],
                bbar : ['->', new Ext.PagingToolbar({
                    store : unitStore,
                    border : false,
                    displayInfo : true,
                    displayMsg : '显示第{0}条 到{1}条记录，一共{2}条',
                    emptyMsg : "没有数据",
                    listeners : {
                        beforechange : function(){
                            unitStore.getProxy().extraParams = {
                                id : selId
                            }
                        }
                    }
                })]

            });

            var unitGridPanel =  new Ext.panel.Panel({
                flex : 7.5,
                height : "100%",
                width : "100%",
                layout: {
                    type : 'vbox',
                    align : 'stretch'
                },
                items: [unitGrid]

            });

            //单位管理panel
            var unitManagePanel = Ext.create('Ext.resizablePanel', {
                renderTo : "unitManageDiv",
                //layout : "hbox",
                layout: {
                    type : 'hbox',
                    align : 'stretch'
                },
                width : "100%",
                height : "100%",
                items : [
                    unitTypeTreePanel,
                    unitGridPanel
                ]
            });
           // unitManagePanel.show();

        });

    </script>
</body>
</html>
