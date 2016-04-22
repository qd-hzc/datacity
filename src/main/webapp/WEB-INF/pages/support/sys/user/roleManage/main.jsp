<%@page language="java" pageEncoding="UTF-8"%>
<html>
<head>
  <title>角色权限管理</title>
  <style type="text/css">
    .prev {
      background-image:url(<%=request.getContextPath()%>/Plugins/extjs/resources/icons/prev.gif) !important;
    }
    .next {
      background-image:url(<%=request.getContextPath()%>/Plugins/extjs/resources/icons/next.gif) !important;
    }

  </style>
</head>
<body>
<script type="text/javascript" src="<%=request.getContextPath()%>/City/support/sys/user/roleManage/catRoleWin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/City/support/sys/user/roleManage/roleAddWin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/City/support/sys/user/roleManage/roleModifyWin.js"></script>

<div id="roleManageId" style="width: 100%;height: 100%;"></div>
<script type="text/javascript">
  Ext.onReady(function(){
    //model
    var moduleModel = createModel('roleModel', function () {
      Ext.define('roleModel', {
        extend: 'Ext.data.Model',
        fields: [{
          name: 'id'
        }, {
          name: 'comments',
          type: 'string'
        }, {
          name: 'name',
          type: 'string'
        }]
      });
    });
    //store
    var roleStore = Ext.create('Ext.data.Store',{
      model: 'roleModel',
      pageSize : 15,
      proxy : {
        type : 'ajax',
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
          var roleName = searchNameField.getValue();//使用funAuthGridPanel获取不到对象，使用Ext.getCmp才行
          roleStore.getProxy().extraParams = {
            roleName : roleName
          }
        }
      }
    });
    //checkboxmodel
    var roleCheckBoxModel = Ext.create('Ext.selection.CheckboxModel',{
      selType : 'checkboxmodel',
      checkOnly : true,//单击列不选中，只有点击选框才选中
      injectCheckbox : 'first'//把复选框放在第一位
    });
    var searchNameField = Ext.create('Ext.form.field.Text', {
            //xtype : 'textfield',
            //id : 'searchName',
            hideLabel : true,
            width : 200,
            name : 'name',
            listeners : {
      specialkey : function(field, e) {
        if (e.getKey() == e.ENTER || e.getKey() == e.TAB) {
          roleStore.loadPage(1,{
            params : {start : 0,limit : roleStore.pageSize,roleName : searchNameField.getValue()}
          });
        }
      }
    }
    });
    //gridpanel
    var roleGridPanel = Ext.create('Ext.grid.Panel',{
      //renderTo : 'roleManageId',
     // id : childId,
      frame : false,
      border : false,
      flex : 1,
      selModel : roleCheckBoxModel,
      store : roleStore,
      //height : '100%',
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
        flex : 1.5
      }, {
        header : '角色说明',
        dataIndex : 'comments',
        sortable : true,
        align : 'center',
        flex : 1.5
      }, {
        header : '查看角色权限信息',
        sortable : true,
        align : 'center',
        flex : .5,
        renderer : function(){
          return '<div style="cursor:pointer;*cursor:hand !important;*cursor:hand;"><a style="text-decoration:none" href="#">查看</a></div>';
        }
      }, {
        header : '修改角色权限信息',
        sortable : true,
        align : 'center',
        flex : .5,
        renderer : function(){
          return '<div style="cursor:pointer;*cursor:hand !important;*cursor:hand;"><a style="text-decoration:none" href="#">修改</a></div>';
        }
      }],
      stripeRows : true,
      tbar : ['角色名称',searchNameField,{
        iconCls : 'query',
        text : '查询',
        handler : function(){
          roleStore.loadPage(1,{//Ext.data.Operation
            params : {
              start : 0,
              limit : roleStore.pageSize,
              roleName : searchNameField.getValue()
            }
          });
        }
      },'->',{
        xtype : 'button',
        iconCls:'add',
        text : '添加',
        handler : function(){
          Ext.roleAddWin.show(function(data){
            roleStore.loadPage(1,{//Ext.data.Operation
              params : {
                start : 0,
                limit : roleStore.pageSize
              }
            });
          },'');

        }
      },'-',{
        xtype : 'button',
        iconCls:'delete',
        text : '删除',
        handler : function(){
          var records = roleGridPanel.getSelectionModel().getSelection();
          if(!records || records.length == 0){
            Ext.Msg.alert('提示','请选择要删除的角色');
            return;
          }
          var roleIds = '[';
          for(var i = 0; i < records.length; i++){
            roleIds += records[i].get('id') + ',';
          }
          Ext.Msg.confirm('询问','确定删除？',function(btn){
            if (btn === 'yes') {
              Ext.Ajax.request({
                url : GLOBAL_PATH + '/support/sys/user/roleManager/deleteRole',
                waitTitle : '提示',
                waitMsg : '正在操作...',
                method : 'POST',
                params : {
                  roleIds : roleIds.substring(0, roleIds.length - 1)+"]"
                },
                success : function(response, opts) {
                  Ext.Msg.alert('提示',"删除成功!");
                  roleStore.loadPage(1,{//Ext.data.Operation
                    start : 0,
                    limit : roleStore.pageSize
                  });
                },
                failure : function(response, opts) {
                }
              });
            }
          });
        }
      }],
      bbar : ['->', new Ext.PagingToolbar({
        store : roleStore,
        border : false,
        displayInfo : true,
        displayMsg : '显示第{0}条 到{1}条记录，一共{2}条',
        emptyMsg : "没有数据"
      })],
      listeners : {
        'cellclick' : function(_this, td, cellIndex, record, tr, rowIndex, e, eOpts){
          if(cellIndex == 4){
            Ext.catRoleWin.show(function(data){

            },record);
          } else if(cellIndex == 5){
            Ext.roleModifyWin.show(function(data){
              roleStore.loadPage(1,{//Ext.data.Operation
                params : {
                  start : 0,
                  limit : roleStore.pageSize
                }
              });
            }, record);
          }
        },
        'afterrender' : function(){
          roleStore.load();
        }

      },
      myUpdateBox : function(obj){
        //this.updateBox({height : obj.height - this.down('pagingtoolbar').height, width : obj.width});
      }
    });

    new Ext.panel.Panel({
      width: '100%',
      height: '100%',
      layout: {
        type : 'vbox',
        align : 'stretch'
      },
      border: false,
      renderTo: 'roleManageId',
      items: [roleGridPanel],
      listeners: {
        render: function () {//初始化，监听窗口resize事件
          if (indexPanel) {
            var tabPanel = indexPanel.down('#tabCenter');
            var myTab = tabPanel.getActiveTab();
            if (myTab)
              myTab.myPanel = this;
          }
          if (this.hasListener('reDR'))
            this.un('reDR');
          this.on('reDR', function (Obj) {
            if (Obj) {
              Obj.height = Obj.height - 40;
              this.updateBox(Obj)
            }
          });
        }
      }
    });
  });
</script>


</body>
</html>