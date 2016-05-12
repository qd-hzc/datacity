/**
 * Created by zhoutao on 2016/1/26.
 */
if(!Ext.ClassManager.isCreated("Ext.catRoleWin")) {
    Ext.define('Ext.catRoleWin', {
        extend: 'Ext.window.Window',
        height: 505,
        width: 900,
        //closable : false,
        modal: true
    });
}
//定义修改用户窗口函数
Ext.catRoleWin.show = function(fnt,rec){//两个参数，fnt是回调函数，rec是record，可以自定义。
    //-----------------------------基本信息-----------------------------------
    var name = Ext.create('Ext.form.field.Text',{
        name : 'name',
        fieldLabel : '角色名称<font color="red">*</font>',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .9,
        border : false,
        allowBlank : false,
        blankText:'必填项',
        maxLength : 100,
        maxLengthText:'最多100个字符',
        enforceMaxLength : true,
        readOnly : true,
        value : rec.get("name")
    });
    var comments = Ext.create('Ext.form.field.TextArea',{
        name : 'comments',
        fieldLabel : '角色说明',
        labelWidth : 100,
        margin : '20 0 0 0',
        labelAlign : 'right',
        columnWidth : .9,
        height : 180,
        border : false,
        readOnly : true,
        value : rec.get("comments")
    });
    var roleFormPanel = Ext.create('Ext.form.FormPanel',{
        frame : false,
        title : "基本信息",
        border : true,
        height : 400,
        items : [{
            xtype : 'textfield',
            hidden : true,
            name : 'id',
            value : rec.get("id")
        },{
            xtype : 'panel',
            layout : 'column',
            margin : '50 0 0 0 ',
            border : false,
            items : [name]
        },{
            xtype : 'panel',
            layout : 'column',
            border : false,
            items : [comments]
        }]
    });
    //-----------------------------报表授权-----------------------------------
    if(!Ext.ClassManager.isCreated("CatRoleRptAuthModel")){
        Ext.define('CatRoleRptAuthModel',{
            extend : 'Ext.data.Model',
            fields : [{
                name : 'depId'
            },{
                name : 'rptName'
            },{
                name : 'isRead'
            },{
                name : 'isWrite'
            },{
                name : 'isApproval'
            }]
        });
    }

    // 定义已授权的报表数据源
    var RptAuthStore = Ext.create('Ext.data.Store',{
        model: 'CatRoleRptAuthModel',
        pageSize : 20,
        autoLoad: true,
        proxy : {
            type : 'ajax',
            // 请求后台，执行查询已授权的报表数据
            url : GLOBAL_PATH + '/support/sys/user/roleManager/findSelectedRptPage',
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'depId'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
                RptAuthStore.getProxy().extraParams = {
                    roleId : rec.get("id")
                }
            }
        }
    });
    var isRptAuthPanelPanelActivate = false;
    // 定义已授权报表展示面板
    var RptAuthPanel = Ext.create('Ext.grid.Panel',{
        store : RptAuthStore,
        frame : false,
        border : true,
        height : 400,
        title : '已授权报表',
        loadMask : {
            msg : '正在加载数据...'
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .3
        }),{
            header : '报表名',
            dataIndex : 'rptName',
            sortable : true,
            align : 'center',
            flex : 1
        },{
            header : '可填权限',
            dataIndex : 'isWrite',
            sortable : true,
            align : 'center',
            flex : 1,
            renderer : function(value,obj,record,rowIndex,colIndex,store,view){
                var returnValue = '无';
                if(value){
                    returnValue = '有';
                }
                return returnValue;
            }
        },{
            header : '可审权限',
            dataIndex : 'isApproval',
            sortable : true,
            align : 'center',
            flex : 1,
            renderer : function(value,obj,record,rowIndex,colIndex,store,view){
                var returnValue = '无';
                if(value){
                    returnValue = '有';
                }
                return returnValue;
            }
        },{
            header : '可读权限',
            dataIndex : 'isRead',
            sortable : true,
            align : 'center',
            flex : 1,
            renderer : function(value,obj,record,rowIndex,colIndex,store,view){
                var returnValue = '无';
                if(value){
                    returnValue = '有';
                }
                return returnValue;
            }
        }],
        listeners : {'activate' :  function(_this,eOpts ){
            if(!isRptAuthPanelPanelActivate){//第一次被激活时才加载数据
                RptAuthStore.load();
                isRptAuthPanelPanelActivate = true;
            }
        }},
        bbar : ['->', new Ext.PagingToolbar({
            store : RptAuthStore,
            border : false,
            displayInfo : true,
            displayMsg : '显示{0}~{1}条，共{2}条',
            emptyMsg : "没有数据"
        })]
    });
    //-----------------------------菜单授权-----------------------------------
    if(!Ext.ClassManager.isCreated("CatRolefunAuthModel")){
        Ext.define('CatRolefunAuthModel',{
            extend : 'Ext.data.Model',
            fields: [{
                name: 'id',
                type : 'string'
            },{
                name : 'moduleName',
                type : 'string'
            },{
                name : 'moduleDesc',
                type : 'string'
            }, {
                name: 'modulePid',
                type: 'string'
            }]
        });
    }

    // 定义已授权菜单数据源
    var funAuthStore = Ext.create('Ext.data.Store',{
        model: 'CatRolefunAuthModel',
        pageSize : 15,
        autoLoad: true,
        proxy : {
            type : 'ajax',
            //请求后台，执行查询已授权菜单数据操作
            url : GLOBAL_PATH + '/support/sys/user/roleManager/findSelectedFunPage',
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'id'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
                funAuthStore.getProxy().extraParams = {
                    roleId : rec.get("id")
                }
            }
        }
    });
    var isFunAuthPanelPanelActivate = false;
    // 定义已授权菜单的展示面板
    var funAuthPanel = Ext.create('Ext.grid.Panel',{
        store : funAuthStore,
        frame : false,
        border : true,
        height : 400,
        title : '已授权菜单',
        loadMask : {
            msg : '正在加载数据...'
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .2
        }),{
            header : '菜单名',
            dataIndex : 'moduleName',
            sortable : true,
            align : 'center',
            flex : 1
        },{
            header : '完整路径',
            dataIndex : 'moduleDesc',
            sortable : true,
            align : 'center',
            flex : 1.5
        }],
        listeners : {'activate' :  function(_this,eOpts ){
        }},
        bbar : ['->', new Ext.PagingToolbar({
            store : funAuthStore,
            border : false,
            displayInfo : true,
            displayMsg : '显示{0}~{1}条，共{2}条',
            emptyMsg : "没有数据"
        })]
    });
    // 定义用于展示基本信息、配置报表角色信息、配置菜单角色信息等信息展示面板
    var tabPanel = Ext.create('Ext.tab.Panel',{
        frame : false,
        border : false,
        items : [roleFormPanel,RptAuthPanel,funAuthPanel]
    });
    // 定义用户修改窗口
    var catRoleWin = Ext.create('Ext.catRoleWin',{
        title : '查看角色权限信息 ',
        frame : false,
        border : false,
        layout:'fit',
        items : [tabPanel],
        bbar : ['->',{
            text : "关闭",
            padding : "5 20 5 20",
            handler : function(){
                catRoleWin.close();
            }

        }]
    });
    catRoleWin.show();
};

