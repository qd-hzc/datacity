/**
 * Created by zhoutao on 2016/1/27.
 */
if(!Ext.ClassManager.isCreated("Ext.catUserWin")) {
    Ext.define('Ext.catUserWin', {
        extend: 'Ext.window.Window',
        height: 505,
        width: 900,
        //closable : false,
        modal: true
    });
}
//定义修改用户窗口函数
Ext.catUserWin.show = function(fnt,rec){//两个参数，fnt是回调函数，rec是record，可以自定义。
    var MARGIN_ROW_SPACE = '20 0 0 0';
    //console.log(rec);
//--------------用户信息 start ----------------
    //--------------第一行----------------
    var userDisplayName = Ext.create('Ext.form.field.Text',{
        name : 'userDisplayName',
        fieldLabel : '用户名<font color="red">*</font>',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false,
        allowBlank : false,
        blankText:'必填项',
        maxLength : 20,
        maxLengthText:'最多20个字符',
        enforceMaxLength : true,
        value : rec.get("userName")
    });
    var loginName = Ext.create('Ext.form.field.Text',{
        name : 'loginName',
        fieldLabel : '登录名<font color="red">*</font>',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false,
        allowBlank : false,
        blankText:'必填项',
        maxLength : 20,
        maxLengthText:'最多20个字符',
        enforceMaxLength : true,
        value : rec.get("loginName")
    });
    var firstLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [userDisplayName,loginName]
    });
    //------------------------第二行---------------------------
    if(!Ext.ClassManager.isCreated("catUserDepartmentModel")){
        Ext.define('catUserDepartmentModel',{
            extend : 'Ext.data.Model',
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'text',
                type : 'string'
            }, {
                name : 'leaf'
            }, {
                name : 'expanded'
            }, {
                name : 'children'
            }, {
                name : 'pDep'
            }, {
                name : 'depName',
                type : 'string'
            }]
        });
    }

    var userDepId = Ext.create('Ext.form.field.Hidden',{
        name : 'department.id',
        fieldLabel : '部门Id',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false
    });
    var isBasePanelActivate = false;
    var userDepName = Ext.create('Ext.form.field.Text',{
        name : 'department.depName',
        fieldLabel : '部门<font color="red">*</font>',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false,
        width: 190,
        readOnly : true,
        editable : true, //选框可输入
        value : rec.get('department').depName,
        //定义部门下拉列表数据源
        listeners : {
            'select' : function(picker, record, eOpts){

            }
        }
    });
    var userDuty = Ext.create('Ext.form.field.Text',{
        name : 'userDuty',
        fieldLabel : '职务',
        labelWidth : 100,
        labelAlign : 'right',
        readOnly : true,
        columnWidth : .45,
        border : false,
        value : rec.get('duty')
    });
    var secondLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [userDepId,userDepName,userDuty]
    });
    //--------------------第三行-------------------
    var mobile = Ext.create('Ext.form.field.Text',{
        name : 'mobile',
        fieldLabel : '移动电话',
        labelWidth : 100,
        labelAlign : 'right',
        readOnly : true,
        regex : /^\d+$/,
        maxLength :11,
        enforceMaxLength : true,
        columnWidth : .45,
        value : rec.get('mobilePhone')
    });
    var email = Ext.create('Ext.form.field.Text',{
        name : 'email',
        fieldLabel : '电子邮件',
        readOnly : true,
        labelWidth : 100,
        labelAlign : 'right',
        vtype : 'email',
        vtypeText:'该输入项必须是电子邮件地址，<br>格式如：“user@qq.com”',
        columnWidth : .45,
        value : rec.get('email')
    });
    var thirdLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [mobile,email]
    });
    //--------------------第四行-------------------
    var man = Ext.create('Ext.form.field.Radio',{
        name : 'sex',
        boxLabel: '男',
        readOnly : true,
        checked : true,
        inputValue : "true"
    });
    var woman = Ext.create('Ext.form.field.Radio',{
        name : 'sex',
        boxLabel: '女',
        readOnly : true,
        checked : false,
        inputValue : "false"
    });
    var userGenderGroup = Ext.create('Ext.form.RadioGroup',{
        name : 'sexGroup',
        fieldLabel: "性别",
        labelAlign : 'right',
        columnWidth : .45,
        items : [man,woman],
        listeners : {
            'afterrender' : function(){
                userGenderGroup.setValue({'sex':rec.get("sex")});
            }
        }
    });
    var normal = Ext.create('Ext.form.field.Radio',{
        name : 'state',
        boxLabel: '启用',
        readOnly : true,
        checked : true,
        inputValue : "true"
    });
    var unNormal = Ext.create('Ext.form.field.Radio',{
        name : 'state',
        boxLabel: '禁用',
        readOnly : true,
        checked : false,
        inputValue : "false"
    });
    var userStatusGroup = Ext.create('Ext.form.RadioGroup',{
        name : 'stateGroup',
        fieldLabel: "状态",
        labelAlign : 'right',
        columnWidth : .45,
        items : [normal,unNormal],
        listeners : {
            'afterrender' : function(){
                userStatusGroup.setValue({'state':rec.get("state")});
            }
        }
    });
    var forthLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [userGenderGroup,userStatusGroup]
    });
    //--------------------第五行-------------------
    var comments = Ext.create('Ext.form.field.TextArea',{
        name : 'userInfo',
        fieldLabel : '备注',
        readOnly : true,
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .9,
        height : 110,
        border : false,
        value : rec.get('userInfo')
    });
    var fifthLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [comments]
    });
    var userFormPanel = Ext.create('Ext.form.FormPanel',{
        frame : false,
        border : false,
        padding : "40 0 0 0",
        region : 'center',
        title : '基本信息',
        items : [firstLine,secondLine,thirdLine,forthLine,fifthLine]
    });
//--------------用户信息 end ----------------

//--------------角色授权start----------------
    if(!Ext.ClassManager.isCreated("catUserRoleAuthModel")){
        Ext.define('catUserRoleAuthModel',{
            extend : 'Ext.data.Model',
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'comments',
                type : 'string'
            }]
        });
    }

    // 定义已授权角色数据源
    var roleAuthStore = Ext.create('Ext.data.Store',{
        model: 'catUserRoleAuthModel',
        pageSize : 10,
        autoLoad: true,
        proxy : {
            type : 'ajax',
            //请求后台，执行查询已授权菜单数据操作
            url : GLOBAL_PATH + '/support/sys/user/userManage/findRolesByUser',
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'id'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
               /* operation.params = {
                    roleType : 1,
                    userId : rec.get('userId'),
                    userDepId : rec.get('userDepId')
                };*/
                roleAuthStore.getProxy().extraParams = {
                    userId : rec.get("id")
                }
            }
        }
    });
    var isRoleAuthPanelPanelActivate = false;
    // 定义已授权角色的展示面板
    var roleAuthPanel = Ext.create('Ext.grid.Panel',{
        store : roleAuthStore,
        frame : false,
        border : true,
        height : 400,
        title : '已授权角色',
        loadMask : {
            msg : '正在加载数据...'
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .2
        }),{
            header : '角色名',
            dataIndex : 'name',
            sortable : true,
            align : 'center',
            flex : 1
        },{
            header : '角色说明',
            dataIndex : 'comments',
            sortable : true,
            align : 'center',
            flex : 1.5
        }],
        listeners : {'activate' :  function(_this,eOpts ){
           /* if(!isRoleAuthPanelPanelActivate){//第一次被激活时才加载数据
                roleAuthStore.load();
                isRoleAuthPanelPanelActivate = true;
            }*/
        }},
        bbar : ['->', new Ext.PagingToolbar({
            store : roleAuthStore,
            border : false,
            displayInfo : true,
            displayMsg : '显示{0}~{1}条，共{2}条',
            emptyMsg : "没有数据"
        })]
    });
//--------------角色授权end----------------

//--------------报表授权start----------------
    if(!Ext.ClassManager.isCreated("catUserRptAuthModel")){
        Ext.define('catUserRptAuthModel',{
            extend : 'Ext.data.Model',
            fields : [{
                name : 'id'
            },{
                name : 'name'
            },{
                name : 'depId'
            },{
                name : 'isApproval'
            },{
                name : 'isWrite'
            },{
                name : 'isRead'
            }]
        });
    }

    // 定义已授权的报表数据源
    var RptAuthStore = Ext.create('Ext.data.Store',{
        model: 'catUserRptAuthModel',
        pageSize : 10,
        autoLoad : true,
        proxy : {
            type : 'ajax',
            // 请求后台，执行查询已授权的报表数据
            url : GLOBAL_PATH + '/support/sys/user/userManage/findUserRptPermissionPage',
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'id'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
                RptAuthStore.getProxy().extraParams = {
                    userId : rec.get("id")
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
            dataIndex : 'name',
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
                if(value == 1){
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
                if(value == 1){
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
                if(value == 1){
                    returnValue = '有';
                }
                return returnValue;
            }
        }],
        listeners : {'activate' :  function(_this,eOpts ){
         /*   if(!isRptAuthPanelPanelActivate){//第一次被激活时才加载数据
                RptAuthStore.load();
                isRptAuthPanelPanelActivate = true;
            }*/
        }},
        bbar : ['->', new Ext.PagingToolbar({
            store : RptAuthStore,
            border : false,
            displayInfo : true,
            displayMsg : '显示{0}~{1}条，共{2}条',
            emptyMsg : "没有数据"
        })]
    });
//--------------报表授权start----------------

//--------------菜单授权start----------------
    if(!Ext.ClassManager.isCreated("catUserFunAuthModel")){
        Ext.define('catUserFunAuthModel',{
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
        model: 'catUserFunAuthModel',
        pageSize : 10,
        autoLoad: true,
        proxy : {
            type : 'ajax',
            //请求后台，执行查询已授权菜单数据操作
            url : GLOBAL_PATH + '/support/sys/user/userManage/findFunsByUser',
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'id'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
               /* operation.params = {
                    roleType : 1,
                    userId : rec.get('userId'),
                    userDepId : rec.get('userDepId')
                };*/
                funAuthStore.getProxy().extraParams = {
                    userId : rec.get("id")
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
          /*  if(!isFunAuthPanelPanelActivate){//第一次被激活时才加载数据
                funAuthStore.load();
                isFunAuthPanelPanelActivate = true;
            }*/
        }},
        bbar : ['->', new Ext.PagingToolbar({
            store : funAuthStore,
            border : false,
            displayInfo : true,
            displayMsg : '显示{0}~{1}条，共{2}条',
            emptyMsg : "没有数据"
        })]
    });
//--------------菜单授权end----------------
    // 定义用于展示基本信息、配置报表角色信息、配置菜单角色信息等信息展示面板
    var tabPanel = Ext.create('Ext.tab.Panel',{
        frame : false,
        border : false,
        items : [userFormPanel,roleAuthPanel,RptAuthPanel,funAuthPanel]
    });
    // 定义用户修改窗口
    var catUserWin = Ext.create('Ext.catUserWin',{
        title : '查看用户信息 ',
        frame : false,
        border : false,
        layout:'fit',
        items : [tabPanel],
        bbar : ['->',{
            text : "关闭",
            padding : "5 20 5 20",
            handler : function(){
                catUserWin.close();
            }

        }]
    });
    catUserWin.show();
};

