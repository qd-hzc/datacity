/**
 * Created by zhoutao on 2016/1/26.
 */
createModel('Ext.roleModifyWin', function () {
    Ext.define('Ext.roleModifyWin', {
        extend: 'Ext.window.Window',
        height: 505,
        width: 900,
        closeAction : 'destroy',
        //closable : false,
        modal: true
    });
});
//定义修改用户窗口函数
Ext.roleModifyWin.show = function(fnt,rec){//两个参数，fnt是回调函数，rec是record，可以自定义。
    //-----------------------------基本信息-----------------------------------
    var roleId =Ext.create("Ext.form.field.Hidden", {
        name : "id",
        value : rec.get("id")
    });
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
        value : rec.get("comments")
    });
    var roleModifyFormPanel = Ext.create('Ext.form.Panel',{
        frame : false,
        title : "基本信息",
        border : true,
        height : 400,
        items : [roleId,{
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
    //--------------菜单权限 start ----------------
    var funFlag = false;
//treeFunModel  model的作用是可以从store中取到扩展的值
    if(!Ext.ClassManager.isCreated("roleModifyTreeFunModel")){
        Ext.define('roleModifyTreeFunModel', {
            extend: 'Ext.data.Model',
            idProperty: 'id',
            fields: [{
                name: 'id',
                type : 'string'
            },{
                name : 'moduleName',
                type : 'string'
            }, {
                name: 'modulePid',
                type: 'string'
            }]
        });
    }

    //treestore
    var selectFunWestTreeStore = Ext.create('Ext.data.TreeStore',{
        model: 'roleModifyTreeFunModel',
        reader: {
            messageProperty: 'msg',
            type: 'json'
        },
        proxy : {//代理
            type : 'ajax',//异步取数据
            url : GLOBAL_PATH + '/support/sys/module/queryAllCheckModule'
        },
        root : {//根节点
            id: MODULE_TYPE.ROOT,
            expanded : true,
            checked :false,
            moduleName : '可选菜单'
        },
        rootVisible: true,
        autoLoad: true,
        listeners : {
            'beforeload' : function(_this, operation, eOpts){
                operation.params = {
                    //isHaveCheckedBox : true
                }
            }
        }
    });
    var selectFunWestTreePanel = Ext.create('Ext.tree.Panel',{
        frame : false,
        border : true,
        region : 'west',
        width : '48%',
        displayField: 'moduleName',
        rootVisible: true,
        autoScroll : true,
        //title : '可选菜单',
        store : selectFunWestTreeStore,
        nodeParam : 'nodeId'//传到后台的节点id，默认是node
    });
    //级联子节点
    function checkChild(node,checked){
        node.set('checked',checked);
        node.eachChild(function(childNode){
            checkChild(childNode,checked);
        });
        node.expandChildren(true);
    }
    //级联父节点
    function checkFather(node,checked){
        //判断同级有没有被选中，不需要管下级
        node.set('checked',checked);
        if(!checked){
            if(node.parentNode){
                var needContinue = true;
                node.parentNode.eachChild(function(childNode){
                    if(childNode.get('checked')){
                        needContinue = false;
                    }
                });
                if(needContinue)
                    checkFather(node.parentNode,checked);
            }
        }else{
            if(node.parentNode){
                checkFather(node.parentNode,checked);
            }
        }
    }
    //根据节点获取被选中的所有子节点
    function findChildNode(node,dataList){
        if(node.data.checked){
            dataList[dataList.length] = node.data;
        }
        node.eachChild(function(childNode){
            findChildNode(childNode,dataList);
        });
    }

    //根据节点获取所有子节点
    var ifRepeat = 0;
    function findAllChildNode(node,dataList){
        for(i=0;i<dataList.length;i++){
            if(dataList[i].id == node.data.id){
                ifRepeat = 1;
            }
        }
        if(ifRepeat == 0){
            dataList[dataList.length] = node.data;
        }
        ifRepeat = 0;
        node.eachChild(function(childNode){
            findAllChildNode(childNode,dataList);
        });
    }

    //根据节点获取被选中的所有子节点
    function findChildNodeId(node,dataList){
        if(node.get('id') != '0'){//不要根节点
            dataList[dataList.length] = node.get('id');
        }
        node.eachChild(function(childNode){
            findChildNodeId(childNode,dataList);
        });
        return dataList;
    }

    //根据节点获取被选中的所有子节点
    function findRptChildNodeId(node,dataList){
        if(node.get('depId') != '0'){//不要外部门跟节点
            var data = node.get('depId');
            if(node.get('reportPermissionType'))
                data += "|"+node.get('reportPermissionType');
            if(node.get('isRead') == true)
                data += "|READ";
            if(node.get('isWrite') == true)
                data += "|WRITE";
            if(node.get('isApproval') == true)
                data += "|APPROVAL";

            dataList[dataList.length] = data;
        }
        node.eachChild(function(childNode){
            findRptChildNodeId(childNode,dataList);
        });
        return dataList;
    }

    function delNode(node){
        var childNodes = node.childNodes;
        if(childNodes && childNodes.length > 0){
            for(var i = childNodes.length - 1; i >= 0 ; i--){
                var checked = childNodes[i].get('checked');
                if(checked){
                    childNodes[i].remove();
                }else{
                    delNode(childNodes[i]);
                }
            }

        }
    }
    function getData(node,dataList){
        var recData = node.data;
        var obj = Ext.apply(new Object(),recData);
        var checked = obj.checked;
        if(checked){
            dataList[dataList.length] = obj;
            var childNodes = node.childNodes;
            if(childNodes && childNodes.length > 0){
                var childDataList = new Array();
                for(var i = 0; i < childNodes.length; i++){
                    getData(childNodes[i],childDataList);
                }
                obj.children = [];
                obj.children[0] = childDataList;
            }
        }
    }
    selectFunWestTreePanel.on('checkchange',function(node,checked){//这里的node相当于一个model，也相当于record。
        //第一步，先级联子节点
        checkChild(node,checked);
        //第二步，再级联父节点
        checkFather(node,checked);
    });
    var dataList = new Array();//存储被选中的树节点的集合
    var isNeedToDataBase = true;//用于判断是否需要从数据库中读取数据
    //treestore
    var selectFunEastTreeStore = Ext.create('Ext.data.TreeStore',{
        model: 'roleModifyTreeFunModel',
        autoLoad : true,
        reader: {
            type: 'json'
        },
        proxy : {//代理
            type : 'ajax',//异步取数据
            actionMethods : {//默认：{create: 'POST', read: 'GET', update: 'POST', destroy: 'POST'}
                read : 'POST'
            },
            url : GLOBAL_PATH + '/support/sys/user/roleManager/findSelectedFunTree?roleId='+rec.get("id")
        },
        root : {//根节点
            moduleName : '选中菜单',
            id : '0',
            expanded : true,
            checked : false
        },
        listeners : {
            'beforeload' : function(_this, operation, eOpts){
            }
        }
    });
    var selectFunEastTreePanel = Ext.create('Ext.tree.Panel',{
        frame : false,
        rootVisible : true,
        border : true,
        region : 'east',
        width : '48%',
        displayField: 'moduleName',
        autoScroll : true,
        store : selectFunEastTreeStore,
        //title : '已选菜单',
        nodeParam : 'nodeId'//传到后台的节点id，默认是node
    });
    selectFunEastTreePanel.on('checkchange',function(node,checked){
        checkChild(node,checked);
    });
    var selectFunCenterPanel = Ext.create('Ext.panel.Panel',{
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
                funFlag = true;
                //找出所有被选中的树节点
                dataList = new Array();
                findChildNode(selectFunWestTreePanel.getRootNode(),dataList);
                findAllChildNode(selectFunEastTreePanel.getRootNode(),dataList);
                //把选中的树节点传到后台，后台根据id值拼出树，然后传到前台展示
                isNeedToDataBase = false;
                selectFunEastTreeStore.reload({
                    params : {
                        isNeedToDataBase : isNeedToDataBase,
                        roleId : rec.get("id"),
                        dataList : Ext.encode(dataList)
                    }
                });

            }
        },{
            xtype : 'button',
            iconCls:'prev',
            y : 175,
            x : 4.5,
            width : 22,
            handler : function(){
                funFlag = true;
                var rootNode = selectFunEastTreePanel.getRootNode();
                delNode(rootNode);
                rootNode.set('checked',false);
            }
        }]
    });
    var functionRolePanel = Ext.create('Ext.panel.Panel',{
        title : '已授权菜单',
        id : 'roleAdd_1',
        height : 400,
        frame : false,
        border : true,
        layout : 'border',
        items : [selectFunWestTreePanel,selectFunEastTreePanel,selectFunCenterPanel]
    });


//--------------菜单权限 end ----------------

//--------------报表权限 start ----------------

//-----------------------------报表授权-----------------------------------
    var flagRpt = false;
    if(!Ext.ClassManager.isCreated("roleModifySelectRptModel")){
        Ext.define('roleModifySelectRptModel',{
            extend : 'Ext.data.Model',
            fields : [{
                name : 'depId'
            },{
                name : 'text',
            },{
                name : 'reportPermissionType'
            },{
                name : 'isRead'
            },{
                name : 'isApproval'
            },{
                name : 'isWrite'
            },{
                name:'isAll'
            }]
        });
    }

    var selectRptWestStore = Ext.create('Ext.data.TreeStore',{
        model: 'roleModifySelectRptModel',
        autoLoad : true,
        pageSize : 10000,
        proxy : {
            type : 'ajax',
            url : GLOBAL_PATH + '/support/sys/user/roleManager/findAllPermissionsTree',
            actionMethods : {
                read : 'POST'
            },
            reader : {
                type : 'json'
            }
        },
        listeners : {
            'beforeload' : function(store, operation, eOpts){
            }
        }
    });


    var selectRptWestPanel = Ext.create('Ext.tree.Panel',{
        store : selectRptWestStore,
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
        root:{
            id:'root',
            text:'报表管理',
            expanded:true
        },
        rootVisible: true,
        columns : [{
            xtype : 'treecolumn',
            header : '报表名称',
            dataIndex : 'text',
            sortable : true,
            align : 'left',
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
                    var store = selectRptWestPanel.getStore();
                    if(checked==true){
                        store.getAt(row).set('isWrite',true);
                        store.getAt(row).set('isApproval',true);
                        store.getAt(row).set('isRead',true);
                        store.getAt(row).set('checked', true);
                        // selectRptWestCheckBoxModel.selectRange(row,row,true);
                        //第一步，先级联子节点
                        checkChild(store.getAt(row),true);
                        //第二步，再级联父节点
                        checkFather(store.getAt(row),true);
                    }else{
                        store.getAt(row).set('isWrite',false);
                        store.getAt(row).set('isApproval',false);
                        store.getAt(row).set('isRead',false);
                        store.getAt(row).set('checked', false);
                        // selectRptWestCheckBoxModel.deselectRange(row,row);
                        //第一步，先级联子节点
                        checkChild(store.getAt(row),false);
                        //第二步，再级联父节点
                        checkFather(store.getAt(row),false);
                    }
                }
            }
        }]
    });
    selectRptWestPanel.on('checkchange',function(node,checked){//这里的node相当于一个model，也相当于record。
        //第一步，先级联子节点
        checkChild(node,checked);
        //第二步，再级联父节点
        checkFather(node,checked);
    });
    var selectRptEastStore = Ext.create('Ext.data.TreeStore',{
        model: 'roleModifySelectRptModel',
        autoLoad : false,
        pageSize : 2000,
        proxy : {
            type : 'ajax',
            url : GLOBAL_PATH + '/support/sys/user/roleManager/findSelectedRptTree?roleId='+rec.get("id"),
            actionMethods : {
                read : 'POST'
            },
            reader : {
                type : 'json'
            }
        },
        root:{
            id:'root',
            text:'已选报表管理',
            expanded:true
        },
        rootVisible: false,
        listeners : {
            'beforeload' : function(store, operation, eOpts){
            }
        }
    });

    var selectRptEastPanel = Ext.create('Ext.tree.Panel',{
        store : selectRptEastStore,
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
        columns : [{
            xtype : 'treecolumn',
            header : '报表名称',
            dataIndex : 'text',
            sortable : true,
            align : 'left',
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
        }]
    });
    var rptList = new Array();
    var flagToDataBase = true;
    var selectRptCenterPanel = Ext.create('Ext.panel.Panel',{
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
                flagRpt = true;
                //找出所有被选中的树节点
                rptList = new Array();
                findChildNode(selectRptWestPanel.getRootNode(),rptList);
                findAllChildNode(selectRptEastPanel.getRootNode(),rptList);
                //把选中的树节点传到后台，后台根据id值拼出树，然后传到前台展示
                flagToDataBase = false;
                selectRptEastStore.reload({
                    params : {
                        isNeedToDataBase : flagToDataBase,
                        roleId : rec.get('id'),
                        dataList : Ext.encode(rptList)
                    }
                });
            }
        },{
            xtype : 'button',
            iconCls:'prev',
            y : 175,
            x : 2,
            width : 22,
            handler : function(){
                flagRpt = true;
                var rootNode = selectRptEastPanel.getRootNode();
                delNode(rootNode);
                rootNode.set('checked',false);
            }
        }]
    });
    var reportRolePanel = Ext.create('Ext.panel.Panel',{
        title : '已授权报表',
        id : 'roleAdd_2',
        height : 400,
        frame : false,
        border : true,
        layout : 'border',
        items : [selectRptWestPanel,selectRptEastPanel,selectRptCenterPanel],
        listeners : {
        }
    });
//--------------报表权限 end ----------------
    // 定义用于展示基本信息、配置报表角色信息、配置菜单角色信息等信息展示面板
    var tabPanel = Ext.create('Ext.tab.Panel',{
        frame : false,
        border : false,
        items : [roleModifyFormPanel,functionRolePanel,reportRolePanel]
    });
    // 定义用户修改窗口
    var roleModifyWin = Ext.create('Ext.roleModifyWin',{
        title : '修改角色权限信息 ',
        frame : false,
        border : false,
        layout:'fit',
        items : [tabPanel],
        bbar : ['->',{
            text : "修改",
            padding : "5 20 5 20",
            handler : function(){
                var flag = true;
                if(roleModifyFormPanel.getForm().isValid()) {
                    Ext.Ajax.request({
                        url : GLOBAL_PATH + '/support/sys/user/roleManager/updateRole',
                        waitTitle : '提示',
                        waitMsg : '正在操作...',
                        method : 'POST',
                        params : roleModifyFormPanel.getValues(),
                        success : function(response, opts) {
                            /* funFlag = false;
                             console.log(funFlag);*/
                        },
                        failure : function(response, opts) {
                            flag = false;
                        }
                    });
                }
                if(funFlag){
                    Ext.Ajax.request({
                        url : GLOBAL_PATH + '/support/sys/user/roleManager/updateRoleFunList',
                        waitTitle : '提示',
                        waitMsg : '正在操作...',
                        method : 'POST',
                        params : {
                            roleId : rec.get("id"),
                            moduleIds : Ext.encode(findChildNodeId(selectFunEastTreePanel.getRootNode(),new Array()))
                        },
                        success : function(response, opts) {
                           funFlag = false;
                            /*  console.log(funFlag);*/
                        },
                        failure : function(response, opts) {
                            flag = false;
                        }
                    });
                }
                if(flagRpt){
                    Ext.Ajax.request({
                        url : GLOBAL_PATH + '/support/sys/user/roleManager/updateRolePermissionList',
                        waitTitle : '提示',
                        waitMsg : '正在操作...',
                        method : 'POST',
                        params : {
                            roleId : rec.get("id"),
                            perms : Ext.encode(findRptChildNodeId(selectRptEastPanel.getRootNode(),new Array()))
                        },
                        success : function(response, opts) {
                           flagRpt = false;
                            /*console.log(flagRpt);*/
                        },
                        failure : function(response, opts) {
                            flag = false;
                        }
                    });
                }

                if(flag){
                    Ext.Msg.alert("提示","修改成功!");
                    roleModifyWin.close();
                    fnt();
                }else{
                    Ext.Msg.alert("提示","修改失败!");
                    roleModifyWin.close();
                }


            }

        }]
    });
    roleModifyWin.show();
};

