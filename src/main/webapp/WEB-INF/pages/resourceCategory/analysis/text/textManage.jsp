<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/15
  Time: 14:03
  To change this template use File | Settings | File Templates.
  文字分析管理界面
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>文字分析</title>
    <meta charset="UTF-8"/>
    <jsp:include page="textImp.jsp"/>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/text/updateTextThemeWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/text/updateTextContentWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/text/addContentWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/text/addAnalysisDataWin.js"></script>
</head>
<body>
<div id="textManageContainer" style="width: 100%;height: 100%;"></div>
<script>
    var textModel =${textModel};
    var textLabel =${textLabel};
    var themeStore =null;
    var contentStore =null;
    //公共参数
    var commonParams = {
        themeParams: {
            name:""
        },
        contentParams: {
            name:"",
            status:null,
            themeId: null,
            contentSortType: null
        },
        dataParams: {
            foreignId: null,
            foreignType: null
        }
    };
    Ext.onReady(function () {

        function hideMenu() {
            if (themeMenu) {
                themeMenu.hide();
            }
            if (contentMenu) {
                contentMenu.hide();
            }
            if (dataMenu) {
                dataMenu.hide();
            }
        }
        function addTextContent() {
            var win = Ext.updateTextContentWin.init(null, function (data) {
                if (!data.sortIndex || data.sortIndex <= 0) {
                    data.sortIndex = contentStore.data.length + 1;
                }
                var isDefault = false;
                if (!commonParams.contentParams.themeId || !commonParams.contentParams.contentSortType) {
                    commonParams.contentParams.themeId = themeStore.getAt(0).getId();
                    commonParams.contentParams.contentSortType = themeStore.getAt(0).get("contentSortType");
                    isDefault = true;
                }
                console.log(commonParams.contentParams)
                Ext.Ajax.request({
                    url: GLOBAL_PATH + "/support/resourceCategory/analysis/text/updateTextContent",
                    method: 'POST',
                    jsonData: data,
                    params: commonParams.contentParams,
                    success: function (response, opts) {
                        var result = Ext.JSON.decode(response.responseText);
                        if (result.success) {
                            contentStore.add(result.datas[0]);
                            if(isDefault){
                                contentStore.reload({params: commonParams.contentParams});
                            }
                            themeStore.reload({params: commonParams.themeParams});
                        }
                        Ext.Msg.alert("提示",result.msg)
                    }
                });
                win.close();
            });
        }

        var themeModel = createModel("TextTheme", function () {
            Ext.define("TextTheme", {
                extend: "Ext.data.Model",
                field: [
                    {
                        name: 'id',
                        type: 'int'
                    }, {
                        name: 'name',
                        type: 'string'
                    }, {
                        name: 'modelId',
                        type: 'int'
                    }, {
                        name: 'infos',
                        type: 'string'
                    }, {
                        name: 'sortIndex',
                        type: 'int'
                    }, {
                        name: 'contentSortType',
                        type: 'string'
                    }, {
                        name: 'creator',
                        type: 'int'
                    }, {
                        name: 'createTime',
                        type: 'string'
                    }, {
                        name: 'updator',
                        type: 'int'
                    }, {
                        name: 'updateTime',
                        type: 'string'
                    }, {
                        name: 'unChecked',
                        type: 'int'
                    }
                ]
            })
        });
        var themeStore = new Ext.data.Store({
            autoLoad: true,
            model: "TextTheme",
            proxy: {
                type: 'ajax',
                extraParams: commonParams.themeParams,
                actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                api: {
                    read: GLOBAL_PATH + '/support/resourceCategory/analysis/text/queryTextThemeByCondition',
                    create: GLOBAL_PATH + '/support/resourceCategory/analysis/text/updateTextTheme',
                    update: GLOBAL_PATH + '/support/resourceCategory/analysis/text/updateTextTheme',
                    destroy: GLOBAL_PATH + '/support/resourceCategory/analysis/text/deleteTextTheme'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    messageProperty: 'msg'
                }
            },
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }
        });
        this.themeStore = themeStore;
        var themeMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '修改',
                iconCls: 'Pageedit',
                handler: function () {
                    var selModel = themeGrid.getSelectionModel();
                    var selected = selModel.getSelection()[0];
                    if (selected) {
                        var win = Ext.updateTextThemeWin.init(selected, function (data) {
                            if (!data.sortIndex || data.sortIndex <= 0) {
                                data.sortIndex = contentStore.data.length + 1;
                            }
                            selected.set('name', data.name);
                            selected.set('modelId', data.modelId);
                            selected.set('sortIndex', data.sortIndex);
                            selected.set('contentSortType', data.contentSortType);
                            selected.set('infos', data.infos);
                            themeStore.sync({
                                failure:function(){
                                    themeStore.reload({params: commonParams.themeParams});
                                }
                            });
                            win.close();
                        });
                    }
                }
            }, {
                text: '删除',
                iconCls: 'Delete',
                handler: function () {
                    Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                        if (btn == 'yes') {
                            var selModel = themeGrid.getSelectionModel();
                            var selected = selModel.getSelection();
                            if (selected) {
                                themeStore.remove(selected);
                                commonParams.contentParams.themeId = null;
                                commonParams.contentParams.contentSortType = null;
                                themeStore.sync();
                            }
                        }
                    });
                }
            }, {
                text: '添加分析',
                iconCls: 'Pageedit',
                handler: function () {
                    addTextContent();
                }
            }, {
                text: '添加分析数据',
                iconCls: 'Pageedit',
                handler: function () {
                    var selModel = themeGrid.getSelectionModel();
                    var selected = selModel.getSelection();
                    if (selected) {
                        var win = Ext.addAnalysisDataWin.init(selected[0],TEXT_TYPE.THEME, function (data) {
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + "/support/resourceCategory/analysis/text/addAnalysisData",
                                method: 'POST',
                                jsonData: data,
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    if (result.success) {
                                        dataStore.load({params: commonParams.dataParams});
                                    }
                                    Ext.Msg.alert("提示",result.msg)

                                }
                            });
                            win.close();
                        });
                    }
                }
            }]
        });
        //主题表格
        var themeGrid = new Ext.grid.Panel({
            flex: 1,
            height: '100%',
            store: themeStore,
            tbar:[{
                xtype: 'textfield',
                width: 150,
                emptyText: '输入名称或说明查询',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.themeParams.name = n;
                        themeStore.reload({params: commonParams.themeParams});
                    }
                }
            },'->', {
                text: '添加主题',
                iconCls: 'Add',
                handler: function () {
                    var win = Ext.updateTextThemeWin.init(null, function (data) {
                        //metadataTypeStore.reload();
                        if (!data.sortIndex || data.sortIndex <= 0) {
                            //console.log(data)
                            data.sortIndex = themeStore.data.length + 1;
                        }
                        Ext.Ajax.request({
                            url: GLOBAL_PATH + "/support/resourceCategory/analysis/text/updateTextTheme",
                            method: 'POST',
                            jsonData: data,
                            success: function (response, opts) {
                                var result = Ext.JSON.decode(response.responseText);
                                if (result.success) {
                                    //console.log(result.datas[0])
                                    themeStore.add(result.datas[0]);
                                }
                                Ext.Msg.alert("提示",result.msg)
                            }
                        });
                        win.close();
                    });
                }
            }],
            columns: [{
                text: '分析主题',
                dataIndex: 'name',
                flex: 1
            }, {
                text: '分析器情况',
                dataIndex: 'unChecked',
                flex: 1,
                renderer: function (data) {
                    if(data){
                        return data+"篇待审核";
                    }else{
                        return "0篇待审核"
                    }

                }
            }, {
                text: '模板',
                dataIndex: 'modelId',
                flex: 1,
                renderer: function (data) {
                    for (var i = 0; i < textModel.length; i++) {
                        if (data == textModel[i].value) {
                            return textModel[i].text;
                        }
                    }
                    return "";
                }
            }],
            listeners: {
                cellclick: function (_this, td, cellIndex, record) {

                    hideMenu();
                    commonParams.contentParams.themeId = record.getId();
                    commonParams.contentParams.contentSortType = record.get("contentSortType")
                    contentStore.load({params: commonParams.contentParams});
                    commonParams.dataParams.foreignId = record.getId();
                    commonParams.dataParams.foreignType = TEXT_TYPE.THEME;
                    dataStore.load({params: commonParams.dataParams});
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    hideMenu();
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    hideMenu();
                    commonParams.contentParams.themeId = record.getId();
                    commonParams.contentParams.contentSortType = record.get("contentSortType")
                    contentStore.load({params: commonParams.contentParams});
                    commonParams.dataParams.foreignId = record.getId();
                    commonParams.dataParams.foreignType = TEXT_TYPE.THEME;
                    dataStore.load({params: commonParams.dataParams});
                    themeMenu.showAt(e.getPoint());
                    //弹出菜单
                },
                containerclick: function () {
                    hideMenu();
                }
            }
        });
        var contentModel = createModel("TextContent", function () {
            Ext.define("TextContent", {
                extend: "Ext.data.Model",
                field: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'status',
                    type: 'int'
                }, {
                    name: 'type',
                    type: 'int'
                }, {
                    name: 'content',
                    type: 'int'
                }, {
                    name: 'infos',
                    type: 'string'
                }, {
                    name: 'sortIndex',
                    type: 'int'
                }, {
                    name: 'analysisDate',
                    type: 'date'
                }, {
                    name: 'creator',
                    type: 'int'
                }, {
                    name: 'createTime',
                    type: 'string'
                }, {
                    name: 'updator',
                    type: 'int'
                }, {
                    name: 'updateTime',
                    type: 'string'
                }, {
                    name: 'labelIds',
                    type: 'string'
                }]
            })
        });
        var contentStore = new Ext.data.Store({
            autoLoad: false,
            model: "TextContent",
            proxy: {
                type: 'ajax',
                extraParams: commonParams.contentParams,
                actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                api: {
                    read: GLOBAL_PATH + '/support/resourceCategory/analysis/text/queryTextContentByThemeId',
                    create: GLOBAL_PATH + '/support/resourceCategory/analysis/text/updateTextContent',
                    update: GLOBAL_PATH + '/support/resourceCategory/analysis/text/updateTextContent',
                    destroy: GLOBAL_PATH + '/support/resourceCategory/analysis/text/deleteTextContent'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    messageProperty: 'msg'
                }
            },
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }/*,
                update:function(_this, record, operation, modifiedFieldNames, details, eOpts){
                    /!*var date = record.get("analysisDate");
                    console.log(date);
                    if(date){
                        var date2 = Ext.Date.parse(date,"Ymd");
                    }*!/

                    //console.log(date2);
                }*/
            }
        });
        // 重新加载参数
        contentStore.on('beforeload', function (s) {
            console.log(commonParams.contentParams)
            s.getProxy().extraParams = commonParams.contentParams;
        });
        this.contentStore =contentStore;
        var contentMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                name:'edit',
                text: '修改',
                iconCls: 'Pageedit',
                handler: function () {
                    var selModel = contentGrid.getSelectionModel();
                    var selected = selModel.getSelection();
                    if (selected&&selected.length==1) {
                        var win = Ext.updateTextContentWin.init(selected[0], function (data) {
                            commonParams.contentParams.themeId = selected[0].get("theme").id;
                            selected[0].set('name', data.name);
                            selected[0].set('type', data.type);
                            selected[0].set('sortIndex', data.sortIndex);
                            selected[0].set('infos', data.infos);
                            selected[0].set('labelIds', data.labelIds);
                            selected[0].set('analysisDate', data.analysisDate);
                            contentStore.sync({
                                failure:function(){
                                    contentStore.reload({params: commonParams.contentParams});
                                }
                            });
                            win.close();
                        });
                    }else{
                        Ext.Msg.alert("提示","请选中一个文字分析！");
                    }
                }
            }, {
                text: '删除',
                iconCls: 'Delete',
                handler: function () {
                    Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                        if (btn == 'yes') {
                            var selModel = contentGrid.getSelectionModel();
                            var selected = selModel.getSelection();
                            if (selected) {
                                contentStore.remove(selected);
                                contentStore.sync();
                            }
                        }
                    });
                }
            },{
                text: '添加分析数据',
                iconCls: 'Pageedit',
                handler: function () {
                    var selModel = contentGrid.getSelectionModel();
                    var selected = selModel.getSelection();
                    if (selected) {
                        var win = Ext.addAnalysisDataWin.init(selected[0],TEXT_TYPE.CONTENT, function (data) {
                            Ext.Ajax.request({
                                url: GLOBAL_PATH + "/support/resourceCategory/analysis/text/addAnalysisData",
                                method: 'POST',
                                jsonData: data,
                                success: function (response, opts) {
                                    var result = Ext.JSON.decode(response.responseText);
                                    if (result.success) {
                                        //console.log(result.datas[0])
                                        dataStore.load({params: commonParams.dataParams});
                                    }
                                    Ext.Msg.alert("提示",result.msg)
                                }
                            });
                            win.close();
                        });
                    }
                }
            }]
        });
        //分析内容表格
        var contentGrid = new Ext.grid.Panel({
            flex: 1,
            width: '100%',
            border: false,
            multiSelect: true,
            //allowDeselect: true,
            store: contentStore,
            columns: [{
                text: '名称',
                dataIndex: 'name',
                flex: 1.5
            }, {
                text: '类型',
                dataIndex: 'type',
                flex: 0.5,
                renderer: function (data) {
                    return TEXT_CONTENT_TYPE.getStr(data);
                }
            }, {
                text: '状态',
                dataIndex: 'status',
                flex: 0.5,
                renderer: function (data) {
                    return TEXT_CONTENT_STATUS.getStr(data);
                }
            }, {
                text: '标签',
                dataIndex: 'labelIds',
                flex: 1,
                renderer: function (data) {
                    if(data){
                        var labelIds =data.split(",");
                        var labelStr = ""
                        for(var i=0;i<labelIds.length;i++){
                            for (var j = 0; j < textLabel.length; j++) {
                                if (labelIds[i] == textLabel[j].value) {
                                    labelStr += textLabel[j].text+",";
                                }
                            }
                        }
                        return labelStr?labelStr.substring(0,labelStr.length-1):"";
                    }
                    return "";

                }
            }, {
                text: '操作',
                dataIndex: 'status',
                flex: 0.5,
                renderer: function (data) {
                    if (data == TEXT_CONTENT_STATUS.CHECKED) {
                        return '<a style="color:#0000FF">查看内容</a>';
                    } else {
                        return '<a style="color:#0000FF">修改内容</a>';
                    }

                }
            }, {
                text: '说明',
                dataIndex: 'infos',
                flex: 1
            }],
            listeners: {
                cellclick: function (_this, td, cellIndex, record) {
                    hideMenu();
                    commonParams.dataParams.foreignId = record.getId();
                    commonParams.dataParams.foreignType = TEXT_TYPE.CONTENT;
                    dataStore.load({params: commonParams.dataParams});
                    if (cellIndex == 4) {//弹出修改窗口
                        var win = Ext.addContentWin.init(record, function (data) {
                            record.set('content', data.content);
                            contentStore.sync();
                            win.close();
                        });
                    }
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    hideMenu();
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    hideMenu();
                    contentMenu.showAt(e.getPoint(),record);
                    commonParams.dataParams.foreignId = record.getId();
                    commonParams.dataParams.foreignType = TEXT_TYPE.CONTENT;
                    dataStore.load({params: commonParams.dataParams});
                    var text ="编辑";
                    if(record.get("status")==TEXT_CONTENT_STATUS.CHECKED){
                        text = "查看";
                    }
                    contentMenu.query('*[name=edit]')[0].setText(text);
                    //弹出菜单
                },
                containerclick: function () {
                    hideMenu();
                }
            }
        });
        // 分析数据model
        var dataModel = createModel("TextData", function () {
            Ext.define("TextData", {
                extend: "Ext.data.Model",
                field: [{
                    name: 'id',
                    type: 'int'
                }, {
                    name: 'dataName',
                    type: 'string'
                }, {
                    name: 'dataType',
                    type: 'int'
                }, {
                    name: 'dataValue',
                    type: 'int'
                }, {
                    name: 'foreignType',
                    type: 'int'
                }, {
                    name: 'foreignId',
                    type: 'int'
                }]
            })
        });
        // 分析数据数据源
        var dataStore = new Ext.data.Store({
            autoLoad: false,
            model: "TextData",
            proxy: {
                type: 'ajax',
                extraParams: commonParams.dataParams,
                actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                api: {
                    read: GLOBAL_PATH + '/support/resourceCategory/analysis/text/queryTextData',
                    destroy: GLOBAL_PATH + '/support/resourceCategory/analysis/text/deleteAnalysisData'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas',
                    messageProperty: 'msg'
                }
            },
            listeners: {
                write: function (store, operate, callback) {
                    Ext.Msg.alert('提示', operate._resultSet.message);
                }
            }
        });
        // 菜单
        var dataMenu = new Ext.menu.Menu({
            renderTo: Ext.getBody(),
            items: [{
                text: '删除',
                iconCls: 'Delete',
                handler: function () {
                    Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                        if (btn == 'yes') {
                            var selModel = dataGrid.getSelectionModel();
                            var selected = selModel.getSelection();
                            if (selected) {
                                dataStore.remove(selected);
                                dataStore.sync();
                            }
                        }
                    });
                }
            }]
        })
        //分析数据表格

        var dataGrid = new Ext.grid.Panel({
            flex: 1,
            width: '100%',
            store: dataStore,
            columns: [{
                text: '分析数据名',
                dataIndex: 'dataName',
                flex: 1
            }, {
                text: '分析数据类型',
                dataIndex: 'dataType',
                flex: 1,
                renderer: function (data) {
                    return TEXT_DATA_TYPE.getStr(data);
                }
            },{
                text: '分析数据归属',
                dataIndex: 'foreignType',
                renderer: function (data) {
                    return data==1?'分析主题':'分析内容';
                }
            }],
            listeners: {
                cellclick: function (_this, td, cellIndex, record) {
                    hideMenu();
                },
                containercontextmenu: function (_this, e) {
                    //取消冒泡
                    e.preventDefault();
                    hideMenu();
                },
                itemcontextmenu: function (_this, record, itemId, index, e) {
                    //取消冒泡
                    e.preventDefault();
                    hideMenu();
                    dataMenu.showAt(e.getPoint());
                    //弹出菜单
                },
                containerclick: function () {
                    hideMenu();
                }
            }
        });


        //分析内容表格
        var contentPanel = new Ext.panel.Panel({
            flex: 2,
            layout: 'vbox',
            height: '100%',
            items: [contentGrid, dataGrid],
            tbar: [{
                xtype: 'textfield',
                width: 150,
                emptyText: '输入名称或说明查询',
                triggerCls: 'x-form-clear-trigger',
                onTriggerClick: function () {
                    this.reset();
                },
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.contentParams.name = n;
                        contentStore.reload({params: commonParams.contentParams});
                    }
                }
            }, {
                xtype: 'combo',
                fieldLabel: '状态',
                labelWidth: 40,
                labelAlign: 'right',
                width: 120,
                displayField: 'name',
                valueField: 'id',
                forceSelection: true,
                editable: false,
                store: new Ext.data.Store({
                    fields: ['id', 'name'],
                    data: TEXT_CONTENT_STATUS.getArr()
                }),
                listeners: {
                    select: function (_this, record, o) {
                        commonParams.contentParams.status = record.get("id");
                        contentStore.reload({params: commonParams.contentParams});
                    }
                }
            },'->', {
                text: '添加分析',
                iconCls: 'Add',
                handler: function () {
                    if (themeStore.getAt(0)) {
                        addTextContent();
                    } else {
                        Ext.Msg.alert("提示", "请先创建分析主题！");
                    }

                }
            }, '|', {
                text: '通过',
                iconCls: 'Accept',
                handler: function () {
                    checkTextContent(TEXT_CONTENT_STATUS.CHECKED);
                }
            }, {
                text: '驳回',
                iconCls: 'Cancel',
                handler: function () {
                    checkTextContent(TEXT_CONTENT_STATUS.REJECT);
                }
            }]
        });

        //容器
        new Ext.resizablePanel({
            width: '100%',
            height: '100%',
            renderTo: 'textManageContainer',
            layout: 'hbox',
            border: false,
            items: [themeGrid, contentPanel],
            tbar: [ '->',{
                text: '管理模板',
                iconCls: 'Boxpicture',
                handler: function () {
                    Ext.text.ManageModelWin.init();
                }
            }, {
                text: '管理标签',
                iconCls: 'Pagewhitestack',
                handler: function () {
                    Ext.text.ManageLabelWin.init();
                }
            }]
        });

        //审核
        function checkTextContent(status) {
            var sel = contentGrid.getSelectionModel().getSelection();
            if (sel.length) {
                var ids = [];
                for (var i = 0; i < sel.length; i++) {
                    var rec = sel[i];
                    //if (rec.get('status') != TEXT_CONTENT_STATUS.CHECKED) {//已通过审核的不可进行其他操作
                        ids.push(rec.get('id'));
                    //}
                }
                if (ids.length) {
                    //发送请求
                    Ext.Ajax.request({
                        url: TEXT_CONTEXT_PATH + '/checkTextContent',
                        params: {
                            ids: ids.join(','),
                            status: status
                        },
                        success: function (response) {
                            var obj = Ext.decode(response.responseText);
                            Ext.Msg.alert('提示', obj.msg);
                            contentStore.reload();
                            themeStore.reload({params: commonParams.themeParams});
                        },
                        failure: function () {
                            Ext.Msg.alert('提示', "请求发送失败!");
                        }
                    });
                } else {
                    Ext.Msg.alert('提示', "已通过审核的分析不能再进行任何操作!");
                }

            } else {
                Ext.Msg.alert('提示', '没事别乱点o(╯□╰)o');
            }
        }
    });
</script>
<script src="<%=request.getContextPath()%>/City/resourceCategory/analysis/text/manageModelWin.js"></script>
<script src="<%=request.getContextPath()%>/City/resourceCategory/analysis/text/manageLabelWin.js"></script>
</body>
</html>
