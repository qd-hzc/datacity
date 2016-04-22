<%--
  Created by cheruixue.
  Date: 2016/3/10
  Time: 9:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
%>
<html>
<head>
    <title>模版管理</title>
    <script src="<%=contextPath%>/City/resourceCategory/themes/manage/manage-theme-context.js"></script>
</head>
<body>
<script>
    var CRXTHEME = {
        /**
         * 根据类型id返回json：{id:1,name:综合表}
         */
        getContentTypeName: function (id) {
            var idI = parseInt(id);
            var name;
            switch (idI) {
                case 1:
                    name = '综合表';
                    break;
                case 2:
                    name = '自定义表';
                    break;
                case 3:
                    name = '图表';
                    break;
                case 4:
                    name = '地图';
                    break;
                case 5:
                    name = '文字分析';
                    break;
                case 6:
                    name = '文件';
                    break;
                case 7:
                    name = '目录';
                    break;
                case 8:
                    name = '页面';
                    break;
                case 9:
                    name = '数据集';
                    break;
                case 10:
                    name = '分析主题';
                    break;
            }
            return {code: id, name: name};
        },
        /**
         * 删除模板菜单
         * @param record
         */
        deleteManageTheme: function (record) {
            Ext.Ajax.request({
                url: contextPath + '/resourcecategory/themes/manageThemesController/deleteThemePage',
                params: {
                    id: record.get('id')
                },
                success: function (response) {
                    var text = Ext.decode(response.responseText);
                    if (text.success) {
                        Ext.Msg.alert('提示', text.msg);
                        record.remove();
                    }
                },
                failure: function () {
                    Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                }
            });
        },
        /**
         * 显示主题管理树的右键菜单
         * @param record
         * @param e
         */
        showManageThemeRightMenu: function (record, e) {
            var manageThemeRightMenu = Ext.create('Ext.menu.Menu', {
                width: 100,
                renderTo: Ext.getBody(),  // usually rendered by it's containing component
                items: [{
                    text: '添加下级',
                    iconCls: 'Add',
                    handler: function () {
                        var parentId = record.get('id');
                        Ext.manageThemeContextMenuWin.init(parentId, null, function (data) {
                            var newNode = record.createNode(data)
                            record.appendChild(newNode);
                        });
                    }
                }, {
                    text: '添加同级',
                    disabled: record.get('id') == 0,
                    iconCls: 'Controladdblue',
                    handler: function () {
                        var parentId = record.get('parentId');
                        Ext.manageThemeContextMenuWin.init(parentId, null, function (data) {
                            var newNode = record.createNode(data)
                            record.parentNode.appendChild(newNode);
                        });
                    }
                }, '-', {
                    text: '修改',
                    disabled: record.get('id') == 0,
                    iconCls: 'Pageedit',
                    handler: function () {
                        var parentId = record.get('parentId');
                        Ext.manageThemeContextMenuWin.init(parentId, record, function (data) {
                            record.set('name', data.name);
                        });
                    }
                }, {
                    text: '删除',
                    disabled: record.get('id') == 0,
                    iconCls: 'Delete',
                    handler: function () {
                        Ext.Msg.confirm('提示', '确定要删除吗？', function (id) {
                            if (id == 'yes') {
                                CRXTHEME.deleteManageTheme(record);
                            }
                        });
                    }
                }]
            });
            manageThemeRightMenu.showAt(e.getPoint());
            window.manageThemeRightMenu = manageThemeRightMenu;
        },
        /**
         * 根据内容类型获取内容，并设置到内容的combobox中
         */
        getContents: function (combo, record) {
            var id = combo.getId().substr(4);
            var container = Ext.getCmp('content' + id);
//            选择内容时，内容结构为树结构
            var picker = Ext.getCmp('picker' + id);
            var type = parseInt(record.get('code'));
            var url = contextPath;
            switch (type) {
                case 1:
//                     综合表:不分报告期
                    url += '/support/regime/report/getAllReport';
                    picker.setDisabled(true);
                    picker.setVisible(false);
                    picker.echo();
                    container.setVisible(true);
                    container.setDisabled(false);
                    container.setValue('');
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    container.getStore().setConfig(config).load();
                    break;
                case 2:
//                    自定义表
                    url += '/resourcecategory/analysis/report/designCustomResearch/getAllCustomResearch';
                    picker.setDisabled(true);
                    picker.setVisible(false);
                    picker.echo();
                    container.setVisible(true);
                    container.setDisabled(false);
                    container.setValue('');
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    container.getStore().setConfig(config).load();
                    break;
                case 3:
//                     图表
                    url += '/support/resourcecategory/analysis/chart/queryAllChartExceptMap';
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    container.getStore().setConfig(config).load();
                    break;
                case 4:
//                    地图
                    url += '/support/resourcecategory/analysis/chart/queryAllMap';
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    container.getStore().setConfig(config).load();
                    break;
                case 5:
//                    文字分析
                    url += '';
                    break;
                case 6:
//                    文件
                    url += '';
                    break;
                case 7:
//                    目录
                    picker.setCascade('child');
                    picker.setCheckModel('');
                    url += '/resourcecategory/themes/manageThemesController/getThemesByName';
                    container.setVisible(false);
                    container.setDisabled(true);
                    container.setValue('');
                    picker.setVisible(true);
                    picker.setDisabled(false);
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    picker.store.setConfig(config).load();
                    break;
                case 8:
//                    页面
                    url += '/resourcecategory/themes/manageThemesController/getThemesByName';
                    container.setVisible(false);
                    container.setDisabled(true);
                    container.setValue('');
                    picker.setVisible(true);
                    picker.setDisabled(false);
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    picker.setCascade('parent');
                    picker.setCheckModel('double');
                    picker.store.setConfig(config).load();
                    break;
                case 9:
//                     数据集
                    url += '/support/dataSet/getAllDataSet';
                    picker.setDisabled(true);
                    picker.setVisible(false);
                    picker.echo();
                    container.setVisible(true);
                    container.setDisabled(false);
                    container.setValue('');
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    container.getStore().setConfig(config).load();
                    break;
                case 10:
                    //数据集
                    url += '/support/resourceCategory/analysis/text/queryTextThemeList';
                    var config = {proxy: {type: 'ajax', api: {read: url}}};
                    container.getStore().setConfig(config).load();
                    break;
            }
        },
        /**
         * 创建内容
         * @param containerName
         * @param tp
         * @param pc
         * @returns {Ext.panel.Panel}
         */
        createContent: function (containerName, tp, pc, picker) {
            return Ext.create('Ext.panel.Panel', {
                width: '100%',
                layout: 'vbox',
                border: false,
                items: [
                    {
                        xtype: 'displayfield',
                        fieldLabel: containerName,
                        labelWidth: 100,
                        labelAlign: 'right'
                    }, {
                        xtype: 'fieldcontainer',
                        width: '100%',
                        layout: 'column',
                        border: false,
                        items: [
                            tp, pc, picker
                        ]
                    }
                ]
            });
        },
        saveThemeConfig: function (contentsWin, tree) {
            var selection = tree.getSelection();
            if (selection.length < 1 || selection[0].get('id') == 0) {
                Ext.Msg.alert('提示', '请先选择要保存的模板菜单');
                return;
            }
            var id = selection[0].get('id');
            var themeConfigPath = Ext.getCmp('themeConfigPath').getValue();
            if (!themeConfigPath) {
                Ext.Msg.alert('提示', '请选择主题');
                return;
            }
            var modulePath = Ext.getCmp('modulePath').getValue();
            if (!modulePath) {
                Ext.Msg.alert('提示', '请选择模板');
                return;
            }
            var contents = [];
            if (contentsWin.length > 0) {
                for (var i = 0; i < contentsWin.length; i++) {
                    var content = contentsWin[i];
                    if (content.containerId) {
                        var containerId = content.containerId;
                        var typeC = Ext.getCmp('type' + containerId)
                        var type = typeC.getValue();
                        if (!type) continue;
                        var pageContent = {};
                        pageContent.containerId = containerId;
                        pageContent.contentType = type;
                        switch (type) {
                            case 7:
                            case 8:
                                var picker = Ext.getCmp('picker' + containerId);
                                var pickerValue = picker.getFieldValue();
                                pageContent.contentValue = pickerValue;
                                break;
                            default :
                                var c = Ext.getCmp('content' + containerId).getValue();
                                pageContent.contentValue = c;
                                break;
                        }
                        if (!pageContent.contentValue) {
                            Ext.Msg.alert('提示', '请选择内容');
                            return;
                        }
                        contents.push(JSON.stringify(pageContent));
                    }
                }
            }
            var role = Ext.getCmp('privilegeTree').getFieldValue();
            if (!role) {
                Ext.Msg.alert('提示', '请选择权限');
                return;
            }
            var status = Ext.getCmp('show').getValue().status;
            //与后台交互
            $.ajax({
                url: contextPath + '/resourcecategory/themes/manageThemesController/saveThemeConfig',
                type: 'POST',
                data: {
                    id: id,
                    themeConfigPath: themeConfigPath,
                    modulePath: modulePath,
                    content: contents,
                    role: role,
                    status: status
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        Ext.Msg.alert('提示', data.msg);
                    }
                },
                error: function (err) {
                    Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                }
            });
        }
    }
    var contextPath = '<%=contextPath%>';
    Ext.onReady(function () {
        //左侧重点关注树查询数据
        var attentionStore = Ext.create('Ext.data.TreeStore', {
            fields: ['id', 'name', 'parentId', 'themeConfigPath', 'modulePath', 'status', 'leaf'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/resourcecategory/themes/manageThemesController/getManageThemeTree'
                }
            },
            root: {
                expanded: true,
                id: 0,
                name: '模板管理'
            }
        });
        //左侧重点关注 树
        var leftTree = Ext.create('Ext.tree.Panel', {
            width: 200,
            height: '100%',
            store: attentionStore,
            rootVisible: true,
            displayField: 'name',
            region: 'west',
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop',
                    dragZone: {
                        afterDragOver: function (zone) {
                            var rec = zone.overRecord;
                            if(zone.curRecoder){
                                if(rec != zone.curRecoder){
//                                    zone.curRecoder.reject();
                                    if(!zone.curRecoder.hasChildNodes())
                                        zone.curRecoder.set('leaf',true);
                                }
                            }
                            zone.curRecoder = rec;
                            if (rec) {
                                rec.set('loaded', true);
                                rec.set('leaf', false);
                            }

                        }
                    }

                },
                listeners: {
                    'beforedrop': function (node, data, overModel, dropPosition, dropHandlers) {//overModel是一个NodeInterface,data是一个object，有records、item、view等等。
                        dropHandlers.wait = true;
                        var overParentId = overModel.get('parentId');
                        var moveParentId = data.records[0].get('parentId');
                        var moveId = data.records[0].get('id');
                        var overId = overModel.get('id');
                        //与后台交互
                        Ext.Ajax.request({
                            url: contextPath + '/resourcecategory/themes/manageThemesController/sortThemePage',
                            method: 'POST',
                            params: {
                                moveId: moveId,
                                overId: overId,
                                moveParentId: moveParentId,
                                overParentId: overParentId,
                                dropPosition: dropPosition
                            },
                            success: function (data) {
                                if (data) {
                                    dropHandlers.processDrop();
                                } else {
                                    dropHandlers.cancelDrop();
                                }
                            },
                            failure: function (response, opts) {
                                var result = Ext.decode(response.responseText);
                                dropHandlers.cancelDrop();
                            }
                        });
                    }
                }
            },
            listeners: {
                itemcontextmenu: function (_this, record, item, index, e, eOpts) {
                    e.preventDefault();
                    if (window.manageThemeRightMenu) {
                        window.manageThemeRightMenu.hide();
                    }
                    CRXTHEME.showManageThemeRightMenu(record, e);
                },
                itemclick: function (_this, record, item, index, e, eOpts) {
                    Ext.getCmp('caidan').setValue(record.get('name'));
                    var id = record.get('id');
                    Ext.Ajax.request({
                        url: contextPath + '/resourcecategory/themes/manageThemesController/getManageThemeConfig',
                        params: {
                            id: id
                        },
                        success: function (response) {
                            var text = Ext.decode(response.responseText);
                            if (text.success) {
                                var page = text.datas;
                                var contents = page.contents;
                                theme.setValue(page.themeConfigPath);
                                if (page.themeConfigPath) {
                                    moduleStore.loadData(theme.findRecordByValue(page.themeConfigPath).get('modules'), false);
                                }
                                module.setValue(page.modulePath);
                                if (page.modulePath) {
                                    module.fireEvent('select', null, module.findRecordByValue(page.modulePath));
                                }
                                var roles = page.role;
                                Ext.getCmp('privilegeTree').echo(roles);
                                if (contents && contents.length > 0) {
                                    for (var mn = 0; mn < contents.length; mn++) {
                                        var con = contents[mn];
                                        var contId = con.containerId;
                                        var contType = con.contentType;
                                        var contValue = con.contentValue;
                                        var typeCont = Ext.getCmp('type' + contId);
                                        typeCont.setValue(contType);
                                        typeCont.fireEvent('select', typeCont, typeCont.findRecordByValue(contType));
                                        switch (contType) {
                                            case 7://picker
                                            case 8:
                                                var pickerCont = Ext.getCmp('picker' + contId);
                                                pickerCont.echo(contValue);
                                                break;
                                            default ://content
                                                var contentCont = Ext.getCmp('content' + contId);
                                                contentCont.setValue(contValue);
                                                break;
                                        }
                                    }
                                } else {
                                    secondLine.removeAll();
                                }
                            }
                        },
                        failure: function () {
                            Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                        }
                    });
                }
            }
        });
        // 配置主题查询数据
        var themeStore = Ext.create('Ext.data.Store', {
            fields: ['themeName', 'themeConfigPath'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/resourcecategory/themes/manageThemesController/getThemes'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            autoLoad: true
        });
        //        主题下拉列表
        var theme = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '主题',
            id: 'themeConfigPath',
            labelWidth: 100,
            labelAlign: 'right',
            width: '30%',
            store: themeStore,
            queryMode: 'local',
            displayField: 'themeName',
            valueField: 'themeConfigPath',
            listeners: {
                select: function (combo, record, eOpts) {
                    moduleStore.loadData(record.get('modules'), false);
                }
            }
        });
        // 主题下模板查询数据
        var moduleStore = Ext.create('Ext.data.Store', {
            fields: ['moduleName', 'pathType', 'modulePath']
        });
        //        模板下拉列表
        var module = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '模板',
            id: 'modulePath',
            labelWidth: 100,
            labelAlign: 'right',
            width: '30%',
            store: moduleStore,
            queryMode: 'local',
            displayField: 'moduleName',
            valueField: 'modulePath',
            listeners: {
                select: function (combo, record, eOpts) {
                    var contents = record.get('contents');
                    window.contentsWin = contents;
                    secondLine.removeAll();
                    if (contents.length > 0) {
                        for (var i = 0; i < contents.length; i++) {
                            var c = contents[i];
                            var types = c.contentTypes;
                            var containerName= c.containerName;
                            var containerId = c.containerId;
                            var list = [];
                            if (types && types.length > 0) {
                                for (var m = 0; m < types.length; m++) {
                                    list.push(CRXTHEME.getContentTypeName(types[m]));
                                }
                            }
                            var store = Ext.create('Ext.data.Store', {
                                fields: ['code', 'name']
                            });
                            store.loadData(list, false);
                            var contentType = Ext.create('Ext.form.ComboBox', {
                                fieldLabel: '类型',
                                id: 'type' + containerId,
                                labelWidth: 100,
                                labelAlign: 'right',
                                width: '30%',
                                store: store,
                                queryMode: 'local',
                                displayField: 'name',
                                valueField: 'code',
                                listeners: {
                                    select: function (combo, record, eOpts) {
                                        CRXTHEME.getContents(combo, record);
                                    }
                                }
                            });
                            var pageContentStore = Ext.create('Ext.data.Store', {
                                fields: ['id', 'name']
                            });
                            var pageContent = Ext.create('Ext.form.ComboBox', {
                                fieldLabel: '内容',
                                labelWidth: 100,
                                id: 'content' + containerId,
                                labelAlign: 'right',
                                width: '30%',
                                store: pageContentStore,
                                queryMode: 'local',
                                displayField: 'name',
                                valueField: 'id'
                            });
                            var pageContentPickerStore = Ext.create('Ext.data.TreeStore', {
                                fields: ['id', 'name']
                            });
                            var pageContentPicker = Ext.create('Ext.ux.ComboBoxTree', {
                                fieldLabel: '内容',
                                labelWidth: 100,
                                id: 'picker' + containerId,
                                labelAlign: 'right',
                                width: '30%',
                                store: pageContentPickerStore,
                                displayField: 'name',
                                valueField: 'id',
                                editable: false,
                                hidden: true,
                                cascade: 'child',//级联方式:1.child子级联;2.parent,父级联,3,both全部级联
                                checkModel: '',//当json数据为不带checked的数据时只配置为single,带checked配置为double为单选,不配置为多选
                                rootVisible: false,
                                rootChecked: false,//设置root节点是否可以选中
                                multiSelect: true
                            });
                            secondLine.add(CRXTHEME.createContent(containerName, contentType, pageContent, pageContentPicker));
                        }
                    }
                }
            }
        });
        //        右侧容器第一行 包含 主题 和 模板
        var firstLine = Ext.create('Ext.panel.Panel', {
            width: '100%',
            layout: 'column',
            margin: '10 10 10 10',
            border: false,
            items: [
                theme,
                module
            ]
        });
        //        右侧容器第二行 包含 类型 和 内容
        var secondLine = Ext.create('Ext.panel.Panel', {
            width: '100%',
            layout: 'vbox',
            border: false,
            margin: '10 10 10 10',
            items: []

        });
        //权限 面板块
        var privilegeStore = Ext.create('Ext.data.TreeStore', {
            fields: ['id', 'name'],
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/resourcecategory/themes/manageThemesController/getAllRoles'
                }
            },
            autoLoad:true
        });
        var privilegeCombo = Ext.create('Ext.ux.ComboBoxTree', {
            fieldLabel: '权限',
            labelWidth: 100,
            width: '60%',
            id: 'privilegeTree',
            labelAlign: 'right',
            store: privilegeStore,
            displayField: 'name',
            valueField: 'id',
            editable: false,
            cascade: 'child',//级联方式:1.child子级联;2.parent,父级联,3,both全部级联
            checkModel: '',//当json数据为不带checked的数据时只配置为single,带checked配置为double为单选,不配置为多选
            rootChecked: false
        });
        var privilege = Ext.create('Ext.panel.Panel', {
            border: false,
            margin: '10 10 10 10',
            items: [
                privilegeCombo
            ]
        });
        //       显示 面板快
        var show = Ext.create('Ext.panel.Panel', {
            border: false,
            margin: '10 10 10 10',
            items: [
                {
                    xtype: 'radiogroup',
                    fieldLabel: '显示',
                    id: 'show',
                    defaultType: 'radiofield',
                    labelWidth: 100,
                    labelAlign: 'right',
                    layout: 'hbox',
                    items: [
                        {
                            boxLabel: '是',
                            name: 'status',
                            inputValue: '1',
                            margin: '0 20',
                            checked: true
                        }, {
                            boxLabel: '否',
                            name: 'status',
                            inputValue: '0',
                            margin: '0 45'
                        }
                    ]
                }]
        });
        var containerFieldset = Ext.create('Ext.form.FieldSet', {
            title: '模板信息',
            margin: '10 10 10 10',
            padding: '0 0 20 0',
            items: [
                firstLine,
                secondLine,
                privilege,
                show,
                {
                    xtype: 'panel',
                    layout: 'column',
                    border: false,
                    margin: '0 10 10 10',
                    items: [{
                        xtype: 'displayfield',
                        border: false,
                        columnWidth: .92
                    }, {
                        xtype: 'button',
                        text: '保存',
                        width: 70,
                        columnWidth: .08,
                        margin: '0 0 0 0',
                        handler: function () {
                            CRXTHEME.saveThemeConfig(window.contentsWin, leftTree);
                        }
                    }]
                }
            ]
        });
        var rightContainer = Ext.create('Ext.panel.Panel', {
            border: true,
            height: '100%',
            region: 'center',
            scrollable: true,
            items: [
                {
                    xtype: 'displayfield',
                    fieldLabel: '菜单',
                    labelWidth: 100,
                    id: 'caidan',
                    labelAlign: 'right',
                    margin: '10 10',
                    width: '100%'
                },
                containerFieldset
            ]
        });
        var container = Ext.create('Ext.panel.Panel', {
            height: '100%',
            renderTo: 'manageThemeId',
            layout: 'border',
            items: [
                leftTree,
                rightContainer
            ],
            listeners: {
                render: function () {
                    if (indexPanel) {
                        var tabPanel = indexPanel.down('#tabCenter');
                        var myTab = tabPanel.getActiveTab();
                        if (myTab) {
                            myTab.myPanel = this;
                        }
                        if (this.hasListener('reDR')) {
                            this.un('reDR');
                        }
                        this.on('reDR', function (obj) {
                            if (obj) {
                                this.updateBox(obj);
                            }
                        });
                    }
                },
                close: function (panel, eOpts) {
                    this.removeAll();
                }
            }
        });
    });
</script>
<div id="manageThemeId" style="width:100%;height: 100%;"></div>
</body>
</html>
