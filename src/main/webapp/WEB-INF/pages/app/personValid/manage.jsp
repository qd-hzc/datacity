<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/28
  Time: 15:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>人员校验</title>
    <meta charset="UTF-8"/>
    <style>
        html, body, #app_staffValid_container {
            height: 100%;
        }
    </style>
</head>
<body>
<div id="app_staffValid_container"></div>
<script>
    var personContextPath = '<%=request.getContextPath()%>/app/personValid';
    var GLOBAL_PATH='<%=request.getContextPath()%>';//项目路径
    Ext.onReady(function () {
        var personParams={
            name: '',
            validCode:'',
            depId:'',
            includeDownLevel:false

        };
        //表格
        var personStore = new Ext.data.Store({
            fields: ['id', 'name', 'phone', 'email', 'department', 'duty', 'comments', 'validCode','role'],
            proxy: {
                type: 'ajax',
                api: {
                    read: personContextPath + '/queryStaffs',
                    update : personContextPath + '/saveStaff',
                    destroy:personContextPath+'/deleteStaffs'
                }
            },
            autoLoad: true
        });
        var personContainerMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '添加人员',
                iconCls: 'Add',
                handler: addPerson
            }]
        });
        //右击菜单
        var personMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text:'删除人员',
                iconCls: 'Delete',
                handler: function (_this, n, o) {
                    console.log(n);//change事件
                    var selModel = personGrid.getSelectionModel();
                    var sels = selModel.getSelection();
                    if (sels.length >0) {
                        Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                            if (btn == 'yes') {
                                personStore.remove(sels);
                                personStore.sync();

                                /*       personGrid.store.remove(sels[0]);
                                 Ext.Ajax.request({
                                 url: personContextPath + '/deleteStaffs',
                                 params: {
                                 id:sels[0].data.id,
                                 },
                                 method: 'post',
                                 success: function (response, action) {
                                 var obj = Ext.decode(response.responseText);
                                 Ext.Msg.alert('成功',obj.msg);
                                 personStore.reload({params: personParams});
                                 },
                                 failure: function (response, options) {
                                 var obj = Ext.decode(response.responseText);
                                 Ext.Msg.alert('失败',obj.msg);
                                 }
                                 });*/

                            }
                        });
                    }else{
                        Ext.Msg.alert("提示","请选择一位人员");
                        return;
                    }
                }
            }, {
                text:'修改人员',
                iconCls: 'Pageedit',
                handler: function (_this, n, o) {
                    var selModel = personGrid.getSelectionModel();
                    var sels = selModel.getSelection();
                    if (sels.length ==1) {
                        Ext.personValid.EditPersonWin.init(sels[0], function(){ personStore.reload({params: personParams});});
                    }else{
                        Ext.Msg.alert("提示","请选择一位人员");
                        return;
                    }
                }
            }]
        })
        var personGrid = new Ext.grid.Panel({
            width: '100%',

            store: personStore,
            layout: 'fit',
            selModel: 'checkboxmodel',
            columns: [{
                text: '姓名',
                dataIndex: 'name',
                flex: 1
            }, {
                text: '部门',
                dataIndex: 'department',
                flex: 1,
                renderer: function (data) {
                    if (data) {
                        return data.depName;
                    }
                    return '';
                }
            }, {
                text: '职务',
                dataIndex: 'duty',
                flex: 1
            }, {
                text: '电话',
                dataIndex: 'phone',
                flex: 1
            }, {
                text: '邮箱',
                dataIndex: 'email',
                flex: 1
            }, {
                text: '验证码',
                dataIndex: 'validCode',
                flex: 1
            }, {
                text: '角色',
                dataIndex: 'role',
                flex: 1,
                renderer: function (data) {
                    if (data) {
                        return data.name;
                    }
                    return '';
                }
            },{
                text: '说明',
                dataIndex: 'comments',
                flex: 1
            }],
            tbar: [
                {
                    xtype: 'querypicker',
                    fieldLabel: '部门',
                    labelWidth: 50,
                    labelAlign: 'right',
                    store: new Ext.data.TreeStore({
                        fields: ['id', 'depName'],
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
                    rootVisible: true,
                    displayField: 'depName',
                    valueField: 'id',
                    listeners: {
                        select: function (_this, record, o) {
                            personParams.depId = record.get('id');
                        }
                    }
                }, {
                    xtype: 'checkbox',
                    fieldLabel: '包含下级',
                    labelWidth: 60,
                    labelAlign: 'right',
                    listeners: {
                        change: function (_this, n, o) {
                            personParams.includeDownLevel = n;
                        }
                    }
                }, {
                xtype: 'triggertext',
                    fieldLabel: '人员情况',
                    labelWidth: 60,
                    labelAlign: 'right',
                handler: function (_this, n, o) {
                    personParams.name=n;
                }
            },{
                    xtype: 'button',
                    text: '查询',
                    iconCls: 'Find',
                    handler: function () {
                        personStore.reload({params: personParams});
                    }
                },'->', {
                text:'添加人员',
                iconCls: 'Add',
                xtype: 'button',
                handler: addPerson
            },{
                     xtype: 'button',
                    text: '导出人员',
                    handler:function(){
                        var sels=personGrid.getSelectionModel().getSelection();
                        //console.log(personStore.getData().items);
                       if(sels.length==0){
                           var staffs="";
                           for(var i=0;i<personStore.getData().items.length;i++){
                               if(i==personStore.getData().items.length-1){
                                   staffs+=personStore.getData().items[i].getData().id;
                               }else{
                                   staffs+=personStore.getData().items[i].getData().id+",";
                               }
                           }
                           location.href=GLOBAL_PATH + '/app/staffValid/excel/batchExportToExcel?staffs='+staffs;
                           //发送请求,下载文件
//                           Ext.Ajax.request({
//                               url: GLOBAL_PATH + '/app/staffValid/excel/batchExportToExcel',
//                               params:{
//                                   staffs:JSON.stringify(staffs)
//                               },
//                               success: function () {
//                                   Ext.Msg.alert("成功","导出成功");
//                               },
//                               failure: function () {
//                                   Ext.Msg.alert("失败","失败");
//                               }
//                           })
                        }else{
                            var staffs="";
                            for(var i=0;i<sels.length;i++){
                                if(i==sels.length-1){
                                    staffs+=sels[i].getData().id;
                                }else{
                                    staffs+=sels[i].getData().id+",";
                                }


                            }
                            location.href=GLOBAL_PATH + '/app/staffValid/excel/batchExportToExcel?staffs='+staffs;
                           //发送请求,下载文件
//                           Ext.Ajax.request({
//                               url: GLOBAL_PATH + '/app/staffValid/excel/batchExportToExcel',
//                               params:{
//                                   staffs:staffs
//                               },
//                               success: function () {
//                                   Ext.Msg.alert("成功","导出成功");
//                               },
//                               failure: function () {
//                                   Ext.Msg.alert("失败","失败");
//                               }
//                           })

                        }

                    }
        }],
            listeners:{
                containercontextmenu: function (_this, e) {
                    e.preventDefault();
                    personMenu.hide();
                    personContainerMenu.showAt(e.getPoint());
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    personContainerMenu.hide();
                    personMenu.showAt(e.getPoint());
                },
                cellclick: function (_this, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                    personContainerMenu.hide();
                    personMenu.hide();
                },
                containerclick: function ( _this, e, eOpts) {
                    personContainerMenu.hide();
                    personMenu.hide();
                }
            }
        });
        //添加人员
        function addPerson() {

            Ext.personValid.EditPersonWin.init(null,function(){ personStore.reload({params: personParams});});


        }

        //布局
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            border: false,
            layout: 'fit',
            renderTo: 'app_staffValid_container',
            items: [personGrid]
        });
    });
</script>
<script src="<%=request.getContextPath()%>/City/app/personValid/editPersonWin.js"></script>
</body>
</html>
