/**
 * Created by zhoutao on 2016/1/27.
 */
if(!Ext.ClassManager.isCreated("Ext.userAddWin")) {
    Ext.define('Ext.userAddWin', {
        extend: 'Ext.window.Window',
        height: 450,
        width: 900,
        modal: true
    });
}
Ext.userAddWin.show = function(fnt,rec){//两个参数，fnt是回调函数，rec是record，可以自定义。
    var MARGIN_ROW_SPACE = '20 0 0 0';
//--------------用户信息 start ----------------
    var userId;
    //--------------第一行----------------
    var userDisplayName = Ext.create('Ext.form.field.Text',{
        name : 'userName',
        fieldLabel : '用户名<font color="red">*</font>',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false,
        allowBlank : false,
        blankText:'必填项',
        maxLength : 20,
        maxLengthText:'最多20个字符',
        enforceMaxLength : true
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
        enforceMaxLength : true
    });
    var firstLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [userDisplayName,loginName]
    });
    //------------------------第二行---------------------------
    if(!Ext.ClassManager.isCreated("userAddDepartmentModel")){
        Ext.define('userAddDepartmentModel',{
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
        //value : rec.get('id')

    });
    var isBasePanelActivate = false;
    var userDepName = Ext.create('Ext.ux.QueryPicker',{
        name : 'department.depName',
        fieldLabel : '部门<font color="red">*</font>',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false,
        displayField: 'depName',
        minPickerHeight: 300,
        width: 190,
        maxPickerHeight : 300,
        rootVisible: false,
        forceSelection : true,
        editable : true, //选框可输入
        allowBlank:false,
        //value : rec.get('id'),
        //定义部门下拉列表数据源
        store: Ext.create('Ext.data.TreeStore',{
            model: 'userAddDepartmentModel',
            root: {
                id : '0',
                text: "测试",
                expanded: true,
                depId : '0'
            },
            proxy: {
                type : 'ajax',
                //请求后台，执行查询部门树
                url : GLOBAL_PATH + '/support/sys/dep/queryAllDep',
                reader: {
                    type: 'json'
                }
            },
            listeners : {
                'load' : function(_this, node, records, successful, eOpts){
                }
            }
        }),
        listeners : {
            'select' : function(picker, record, eOpts){
                 userDepId.setValue(record.get('id'));

            }
        }
    });
    var userDuty = Ext.create('Ext.form.field.Text',{
        name : 'duty',
        fieldLabel : '职务',
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .45,
        border : false

    });
    var secondLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [userDepId,userDepName,userDuty]
    });
    //--------------------第三行-------------------
    var mobile = Ext.create('Ext.form.field.Text',{
        name : 'mobilePhone',
        fieldLabel : '移动电话',
        labelWidth : 100,
        labelAlign : 'right',
        regex : /^\d+$/,
        maxLength :11,
        enforceMaxLength : true,
        columnWidth : .45
    });
    var email = Ext.create('Ext.form.field.Text',{
        name : 'email',
        fieldLabel : '电子邮件',
        labelWidth : 100,
        labelAlign : 'right',
        vtype : 'email',
        vtypeText:'该输入项必须是电子邮件地址，<br>格式如：“user@qq.com”',
        columnWidth : .45
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
        checked : true,
        inputValue : "true"
    });
    var woman = Ext.create('Ext.form.field.Radio',{
        name : 'sex',
        boxLabel: '女',
        checked : false,
        inputValue : "false"
    });
    var userGenderGroup = Ext.create('Ext.form.RadioGroup',{
        name : 'sexGroup',
        fieldLabel: "性别",
        labelAlign : 'right',
        columnWidth : .45,
        items : [man,woman]
    });
    var normal = Ext.create('Ext.form.field.Radio',{
        name : 'state',
        boxLabel: '启用',
        checked : true,
        inputValue : "true"
    });
    var unNormal = Ext.create('Ext.form.field.Radio',{
        name : 'state',
        boxLabel: '禁用',
        checked : false,
        inputValue : "false"
    });
    var userStatusGroup = Ext.create('Ext.form.RadioGroup',{
        name : 'stateGroup',
        fieldLabel: "状态",
        labelAlign : 'right',
        columnWidth : .45,
        items : [normal,unNormal]
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
        labelWidth : 100,
        labelAlign : 'right',
        columnWidth : .9,
        height : 110,
        border : false
    });
    var fifthLine = Ext.create('Ext.panel.Panel',{
        layout : 'column',
        border : false,
        margin :MARGIN_ROW_SPACE,
        items : [comments]
    });
    var userAddFormPanel = Ext.create('Ext.form.FormPanel',{
        frame : false,
        id : 'userAdd_0',
        border : false,
        padding : "40 0 0 0",
        region : 'center',
        items : [firstLine,secondLine,thirdLine,forthLine,fifthLine]
    });
//--------------用户信息 end ----------------

//--------------角色 start ----------------
    var roleFlag = false;
    if(!Ext.ClassManager.isCreated("userAddRoleModel")){
        Ext.define('userAddRoleModel',{
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

    // 定义可选菜单角色的数据源
    var roleWestStore = Ext.create('Ext.data.Store',{
        model: 'userAddRoleModel',
        pageSize : 10,
        autoLoad: true,
        proxy : {
            type : 'ajax',
            //请求后台，执行查询可选菜单角色数据操作
            url : GLOBAL_PATH + '/support/sys/user/roleManager/findRoleByNamePage',
            actionMethods : {//默认：{create: 'POST', read: 'GET', update: 'POST', destroy: 'POST'}
                read : 'POST'
            },
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'id'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
                //在store去后台load数据时先需要传递查询条件
                // 角色名称
                var roleName = roleWestPanel.query("*[name=roleNameForQuery]")[0].getValue();//使用funAuthGridPanel获取不到对象，使用Ext.getCmp才行
                roleWestStore.getProxy().extraParams = {
                    roleName : roleName
                }
            }
        }
    });
    // 定义菜单角色复选框模型
    var roleWestCheckBoxModel = Ext.create('Ext.selection.CheckboxModel',{
        selType : 'checkboxmodel',
        checkOnly : true,//单击列不选中，只有点击选框才选中
        injectCheckbox : 'first'//把复选框放在第一位
    });
    //定义可选菜单角色展示面板
    var roleWestPanel = Ext.create('Ext.grid.Panel',{
        selModel : roleWestCheckBoxModel,
        store : roleWestStore,
        frame : false,
        border : true,
        region : 'west',
        width : '44%',
        loadMask : {
            msg : '正在加载数据...'
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .2
        }),{
            header : '角色名称',
            dataIndex : 'name',
            sortable : true,
            align : 'center',
            flex : 1,
            renderer : function(value,obj,record,rowIndex,colIndex,store,view){
                var tipId = new Date().getTime();
                //record.set('funTipId',tipId);
                return '<div style="cursor:pointer;*cursor:hand !important;*cursor:hand;"><a style="text-decoration:none" href="#" name='+tipId+'>'+value+'</a></div>';
            }
        },{
            header : '角色说明',
            dataIndex : 'comments',
            sortable : true,
            align : 'center',
            flex : 1.1
        }],
        tbar : ['<font color="#003D79"><strong>可选角色</strong></font>','->','角色名称',{
            xtype : 'textfield',
            hideLabel : true,
            width : 160,
            name : 'roleNameForQuery',
            listeners : {
                specialkey : function(field, e) {
                    // 监听键盘的TAB和ENTER
                     if (e.getKey() == e.ENTER || e.getKey() == e.TAB) {
                     roleWestStore.loadPage(1,{
                     params : {start : 0,limit : roleWestStore.pageSize}
                     });
                     }
                }
            }
        },{
            icon : GLOBAL_PATH + '/img/query.gif',
            text : '查询',
            handler : function(){
                // 执行查询操作
                roleWestStore.loadPage(1,{//Ext.data.Operation
                 params : {
                 start : 0,
                 limit : roleWestStore.pageSize
                 }
                 });
            }
        }],
        // 分页
        bbar : ['->', new Ext.PagingToolbar({
            store : roleWestStore,
            border : false,
            displayInfo : true,
            displayMsg : '',
            emptyMsg : "没有数据"
        })]
    });
    // 定义已选菜单角色数据源
    var roleEastStore = Ext.create('Ext.data.Store',{
        model: 'userAddRoleModel',
        pageSize : 10,
        autoLoad: false,
        proxy : {
            type : 'ajax',
            //请求后台，按条件执行查询已关联的菜单角色。
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
            }
        }
    });
    // 定义已选菜单复选框模型
    var roleEastCheckBoxModel = Ext.create('Ext.selection.CheckboxModel',{
        selType : 'checkboxmodel',
        checkOnly : true,//单击列不选中，只有点击选框才选中
        injectCheckbox : 'first'//把复选框放在第一位
    });
    // 定义已选菜单角色展示面板
    var roleEastPanel = Ext.create('Ext.grid.Panel',{
        selModel : roleEastCheckBoxModel,
        store : roleEastStore,
        frame : false,
        border : true,
        region : 'east',
        width : '52%',
        loadMask : {
            msg : '正在加载数据...'
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .2
        }),{
            header : '角色名称',
            dataIndex : 'name',
            sortable : true,
            align : 'center',
            flex : .8
        },{
            header : '角色说明',
            dataIndex : 'comments',
            sortable : true,
            align : 'center',
            flex : 1.1
        }],
        tbar : ['<font color="#003D79"><strong>已关联角色</strong></font>'],
        bbar : ['->', new Ext.PagingToolbar({
            store : roleEastStore,
            border : false,
            displayInfo : false,
            displayMsg : '',
            emptyMsg : "没有数据"
        })]
    });
    // 新增和删除菜单角色的操作中间面板
    var roleCenterPanel = Ext.create('Ext.panel.Panel',{
        frame : false,
        border : false,
        region : 'center',
        layout : 'anchor',
        items :[{
            xtype : 'button',
            iconCls :'next',
            iconAlign : 'right',
            y : 165,
            x : 4.5,
            width : 22,
            handler : function(){
                // 执行新增操作
                roleFlag = true;
                  var records = roleWestPanel.getSelectionModel().getSelection();
                 var store = roleEastPanel.getStore();
                 for(var i = 0; i < records.length > 0; i++){
                     var isNeedInsert = true;
                     for(var j = 0; j < store.getCount(); j++){
                         var record = store.getAt(j);
                         if(records[i].get('id') == record.get('id')){
                             isNeedInsert = false;
                             break;
                         }
                     }
                     if(isNeedInsert){//插入数据
                         var copyRecord = records[i].copy();
                         //copyRecord.set('userId',rec.get('userId'));
                         store.add(copyRecord);
                     }
                 }
            }
        },{
            xtype : 'button',
            iconCls:'prev',
            y : 175,
            x : 4.5,
            width : 22,
            handler : function(){
                roleFlag = true;
                // 执行删除已选操作
                var records = roleEastPanel.getSelectionModel().getSelection();
                 roleEastPanel.getStore().remove(records);
            }
        }]
    });
    var isrolePanelActivate = false;
    // 定义展示配置菜单角色的面板
    var rolePanel = Ext.create('Ext.panel.Panel',{
        frame : true,
        border : false,
        id : 'userAdd_1',
        layout : 'border',
        //title : '添加角色',
        height : 400,
        items :[roleWestPanel,roleEastPanel,roleCenterPanel]
    });
//--------------菜单权限 end ----------------

//--------------报表权限 start ----------------
    var rptFlag = false;
    if(!Ext.ClassManager.isCreated("userAddRptModel")){
        Ext.define('userAddRptModel',{
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

    var rptWestStore = Ext.create('Ext.data.Store',{
        model: 'userAddRptModel',
        autoLoad : true,
        proxy : {
            type : 'ajax',
            url : GLOBAL_PATH + '/support/sys/user/userManage/findRptTmpsByName',
            actionMethods : {
                read : 'POST'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
                var rptName = rptWestPanel.query("*[name=rptNameForQueryAdd]")[0].getValue();//使用funAuthGridPanel获取不到对象，使用Ext.getCmp才行
                rptWestStore.getProxy().extraParams = {
                    name : rptName
                }
            }
        }
    });
    var rptWestCheckBoxModel = Ext.create('Ext.selection.CheckboxModel',{
        // selType : 'checkboxmodel',
        checkOnly : true,//单击列不选中，只有点击选框才选中
        injectCheckbox : 'first' //把复选框放在第一位

    });


    var rptWestPanel = Ext.create('Ext.grid.Panel',{
        selModel : rptWestCheckBoxModel,
        store : rptWestStore,
        frame : false,
        border : true,
        region : 'west',
        width : '57%',
        id: 'test11',
        loadMask : {
            msg : '正在加载数据...'
        },
        viewConfig:{
            loadMask:true
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .1
        }),{
            header : '报表名称',
            dataIndex : 'name',
            sortable : true,
            align : 'center',
            flex : .4
        },{
            xtype : 'checkcolumn',
            header : '填报',
            dataIndex : 'isWrite',
            sortable : true,
            align : 'center',
            flex : .1,
            listeners: {
                'checkchange': function(_this,row,checked,eOpts){
                }
            }
        },{
            xtype : 'checkcolumn',
            header : '审核',
            dataIndex : 'isApproval',
            sortable : true,
            align : 'center',
            flex : .1,
            listeners: {
                'checkchange': function(_this,row,checked,eOpts){
                }
            }
        },{
            xtype : 'checkcolumn',
            header : '可读',
            dataIndex : 'isRead',
            sortable : true,
            align : 'center',
            flex : .1,
            listeners: {
                'checkchange': function(_this,row,checked,eOpts){
                }
            }
        },{
            xtype : 'checkcolumn',
            header : '全部',
            dataIndex : 'isAll',
            sortable : true,
            align : 'center',
            flex : .1,
            listeners: {
                'checkchange': function(_this,row,checked,eOpts){
                    var store = rptWestPanel.getStore();
                    if(checked==true){
                        store.getAt(row).set('isWrite',true);
                        store.getAt(row).set('isApproval',true);
                        store.getAt(row).set('isRead',true);
                        // selectRptWestCheckBoxModel.selectRange(row,row,true);

                    }else{
                        store.getAt(row).set('isWrite',false);
                        store.getAt(row).set('isApproval',false);
                        store.getAt(row).set('isRead',false);
                        // selectRptWestCheckBoxModel.deselectRange(row,row);
                    }
                }
            }
        }],
        tbar : ['<font color="#003D79"><strong>可选报表权限</strong></font>','->','报表名称',{
            xtype : 'textfield',
            hideLabel : true,
            width : 160,
            name : 'rptNameForQueryAdd',
            listeners : {
                specialkey : function(field, e) {
                    // 监听键盘的TAB和ENTER
                    if (e.getKey() == e.ENTER || e.getKey() == e.TAB) {
                        rptWestStore.load();
                    }
                }
            }
        },{
            icon : GLOBAL_PATH + '/img/query.gif',
            text : '查询',
            handler : function(){
                // 执行查询操作
                rptWestStore.load();
            }
        }],
        bbar : ['->']
    });
    var rptEastStore = Ext.create('Ext.data.Store',{
        model: 'userAddRptModel',
        autoLoad : false,
        pageSize : 10,
        proxy : {
            type : 'ajax',
            url : GLOBAL_PATH + '/support/sys/user/userManage/findSelectedRptPermissionPage',
            reader : {
                type : 'json',
                rootProperty : 'datas',
                totalProperty : 'total',
                idProperty : 'id'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
                rptEastStore.getProxy().extraParams = {
                    userId : rec.get("id")
                }
            }
        }
    });

    var rptEastCheckBoxModel = Ext.create('Ext.selection.CheckboxModel',{
        selType : 'checkboxmodel',
        checkOnly : true,//单击列不选中，只有点击选框才选中
        injectCheckbox : 'first'//把复选框放在第一位
    });
    var rptEastPanel = Ext.create('Ext.grid.Panel',{
        selModel : rptEastCheckBoxModel,
        store : rptEastStore,
        frame : false,
        border : true,
        region : 'east',
        width : '40%',
        loadMask : {
            msg : '正在加载数据...'
        },
        viewConfig:{
            loadMask:true
        },
        autoScroll : true,
        columns : [new Ext.grid.RowNumberer({
            header : '序号',
            align : 'center',
            flex : .2
        }),{
            header : '报表名称',
            dataIndex : 'name',
            sortable : true,
            align : 'center',
            flex : .8
        },{
            header : '状态',
            dataIndex : 'status',
            sortable : true,
            align : 'center',
            flex : .4,
            renderer : function(value,obj,record,rowIndex,colIndex,store,view){
                var returnValue = '';
                if(record.get('isWrite') == 1 || record.get('isWrite') == true){
                    returnValue += '填报' + ',';
                }
                if(record.get('isApproval') == 1 || record.get('isApproval') == true){
                    returnValue += '审核' + ',';
                }
                if(record.get('isRead') == 1 || record.get('isRead') == true){
                    returnValue += '可读' + ',';
                }
                return returnValue.substring(0, returnValue.length - 1);
            }
        }],
        tbar : ['<font color="#003D79"><strong>已选</strong></font>'],
        bbar : ['->', new Ext.PagingToolbar({
            store : rptEastStore,
            border : false,
            displayInfo : false,
            displayMsg : '',
            emptyMsg : "没有数据"
        })]
    });

    var rptCenterPanel = Ext.create('Ext.panel.Panel',{
        frame : false,
        border : false,
        region : 'center',
        layout : 'anchor',
        items :[{
            xtype : 'button',
            iconCls :'next',
            iconAlign : 'right',
            y : 165,
            x : 2,
            width : 22,
            handler : function(){
                var records = rptWestPanel.getSelectionModel().getSelection();
                var blankAuths = [];
                var hasAuths = [];
                //判断该报表是不是已经选择，则覆盖之前的
                var store = rptEastPanel.getStore();
                for(var i = 0; i < records.length > 0; i++){
                    var index = store.getCount();
                    for(var j = store.getCount() - 1; j >= 0; j--){//store.getCount()获取store中的总行数，而store.getTotalCount()获取的是通过proxy代理从后台获取的总行数
                        if(store.getAt(j).get('id') == records[i].get('id')){
                            store.remove(store.getAt(j));
                            index = j;
                            break;
                        }
                    }
                    var copyRecord = records[i].copy();//Creates a copy (clone) of this Model instance
                    //填报、审核、可读的值改成统一的整形，方便到后台取值
                    if(copyRecord.get('isApproval') == true){
                        copyRecord.set('isApproval',1);
                    }else{
                        copyRecord.set('isApproval',0);
                    }
                    if(copyRecord.get('isRead') == true){
                        copyRecord.set('isRead',1);
                    }else{
                        copyRecord.set('isRead',0);
                    }
                    if(copyRecord.get('isWrite') == true){
                        copyRecord.set('isWrite',1);
                    }else{
                        copyRecord.set('isWrite',0);
                    }
                    if(copyRecord.get('isApproval')==0&&copyRecord.get('isWrite')==0&&copyRecord.get('isRead')==0){
                        blankAuths.push(copyRecord);
                        continue;
                    }
                    hasAuths.push(copyRecord);
                    store.insert(index,copyRecord);
                    rptFlag = true;
                }
                /*if(hasAuths.length>0&&blankAuths.length==0){
                 showMsg('提示','关联成功');
                 }*/
                if(hasAuths.length==0 || blankAuths.length>0){
                    var rptNames =[] ;
                    for(var i=0;i<blankAuths.length;i++){
                        rptNames.push(blankAuths[i].get('name'));
                    }
                    Ext.Msg.alert('提示','报表未选择操作权限,请先选择 ');
                }
            }
        },{
            xtype : 'button',
            iconCls:'prev',
            y : 175,
            x : 2,
            width : 22,
            handler : function(){
                var records = rptEastPanel.getSelectionModel().getSelection();
                rptEastPanel.getStore().remove(records);
                rptFlag = true;
            }
        }]
    });
    var reportPanel = Ext.create('Ext.panel.Panel',{
        //title : '选择报表  - ' + rec.get("roleName"),
        frame : false,
        id : 'userAdd_2',
        border : true,
        layout : 'border',
        items : [rptWestPanel,rptEastPanel,rptCenterPanel],
        listeners : {
        }
    });
//--------------报表权限 end ----------------

//--------------完成 start-------------------

    var finishPanel = Ext.create("Ext.panel.Panel",{
        frame : false,
        border : false,
        id : 'userAdd_3',
        layout : "form",
        height : 400,
        //padding: "150 0 0 350",
        items : [{
            xtype : "checkbox",
            boxLabel : "添加信息",
            inputValue : "1",
            margin : "10,0,0,0",
            checked : true,
            id : "ckbInfo"
        },{
            xtype : "checkbox",
            boxLabel : "添加角色",
            inputValue : "2",
            margin : "50,0,0,0",
            checked : true,
            id : "ckbRole"
        },{
            xtype : "checkbox",
            boxLabel : "添加报表权限",
            inputValue : "3",
            margin : "50,0,0,0",
            checked : true,
            id : "ckbRpt"
        }]
    });

//--------------完成 end  -------------------
    var userAddWin = Ext.create('Ext.userAddWin',{
        title : '添加用户',
        frame : false,
        border : false,
        layout : 'card',
        items : [userAddFormPanel,rolePanel,reportPanel,finishPanel],
        bbar : ['->',{
            text : '上一步',
            id : 'btnPrev2',
            hidden : true,
            listeners : {
                "click" : function(_this){
                    var layout = userAddWin.getLayout();
                    layout.setActiveItem(layout.getPrev());
                    if(layout.getPrev() == false){
                        _this.hide();
                    }

                    Ext.getCmp("btnNext2").setText("下一步")
                }
            }
        },{
            text : '下一步',
            id : 'btnNext2',
            listeners : {
                "click" : function(_this){
                    var layout = userAddWin.getLayout();
                    var activeItem = layout.getActiveItem();
                    switch (activeItem.getId()){
                        case 'userAdd_0'://基本信息
                            if(userAddFormPanel.getForm().isValid()){
                                userAddFormPanel.getForm().submit({
                                    url : GLOBAL_PATH+"/support/sys/user/userManage/saveUser",
                                    success : function(form, action){
                                        userId = action.result.userId;
                                    },
                                    failure: function(form, action) {
                                        //Ext.Msg.alert('提示', action.result.msg);
                                    }
                                })
                            }else{
                                return;
                            }
                            break;
                        case 'userAdd_1'://角色
                            if(roleFlag){
                                //先获取records，然后编码
                                var store = roleEastPanel.getStore();
                                var array = new Array();
                                for(var i = 0; i < store.getCount(); i++){
                                    var record = store.getAt(i);
                                    array.push(record.data);
                                }
                                Ext.Ajax.request({
                                    //请求后台，执行更新已选菜单角色操作
                                    url : GLOBAL_PATH + '/support/sys/user/userManage/updateUserRoleList',
                                    waitTitle : '提示',
                                    waitMsg : '正在操作...',
                                    method : 'POST',
                                    params : {
                                        userId : userId,
                                        dataList : Ext.encode(array)
                                    },
                                    success : function(response, opts) {
                                        //var result = Ext.decode(response.responseText);
                                    },
                                    failure : function(response, opts) {
                                        //var result = Ext.decode(response.responseText);
                                    }
                                });
                            }

                            break;
                        case 'userAdd_2'://报表权限
                            if(rptFlag){
                                //先获取records，然后编码
                                var store = rptEastPanel.getStore();
                                var array = new Array();
                                for(var i = 0; i < store.getCount(); i++){
                                    var tmpData ={};
                                    tmpData.id = store.getAt(i).data.id;
                                    tmpData.isRead = store.getAt(i).data.isRead;
                                    tmpData.isApproval = store.getAt(i).data.isApproval;
                                    tmpData.isWrite = store.getAt(i).data.isWrite;

                                    array.push(tmpData);
                                }
                                Ext.Ajax.request({
                                    url : GLOBAL_PATH + '/support/sys/user/userManage/updateUserRptPermission',
                                    waitTitle : '提示',
                                    waitMsg : '正在操作...',
                                    method : 'POST',
                                    params : {
                                        userId : userId,
                                        dataList : Ext.encode(array)
                                    },
                                    success : function(response, opts) {

                                    },
                                    failure : function(response, opts) {
                                    }
                                });
                            }
                            break;
                        case 'userAdd_3'://完成
                            userAddWin.close();
                            fnt();
                            break;
                    }
                    layout.setActiveItem(layout.getNext());
                    if(layout.getNext() == false){
                        finishPanel.getComponent("ckbRole").setValue(roleFlag);
                        finishPanel.getComponent("ckbRpt").setValue(rptFlag);
                        _this.setText("完成");
                    }else{
                        Ext.getCmp("btnPrev2").show();
                        _this.setText("下一步");
                    }
                }
            }
        }]

    });
    userAddWin.show();
};


