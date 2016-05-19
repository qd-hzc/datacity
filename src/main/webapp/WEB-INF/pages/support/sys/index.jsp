<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>业务支撑系统-首页</title>
    <jsp:include page="../../common/imp.jsp"/>
    <jsp:include page="../../common/sysConstant.jsp"/>
    <style type="text/css">
        html, body {
            height: 100%
        }

        .top-background {
            *background-image: url("<%=request.getContextPath()%>/City/support/index/img/topbanner.png");
            -webkit-background-size: cover;
            background-size: cover;
        }
    </style>
    <script type="text/javascript">
        var MODULEID = ${MODULEID};
        var root = ${rootJson};
        var resizeInstance = [];
        var indexPanel = null;
        Ext.onReady(function () {
            //后台首页模块模型
            Ext.define('Index.Module', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'moduleName',
                    type: 'string'
                }, {
                    name: 'moduleType',
                    type: 'int',
                    convert: null
                }, {
                    name: 'id',
                    type: 'int'
                }]
            });
            var moduleTreePanels = [];
            var moduleNode = null;
            if (root != null) {
                //根节点不为空
                for (var i = 0; i < root.children.length; i++) {
                    moduleNode = root.children[i];
                    moduleTreePanels.push(new Ext.tree.Panel({
                        width: 200,
                        title: moduleNode.moduleName,
                        displayField: 'moduleName',
                        height: 150,
                        store: Ext.create('Ext.data.TreeStore', {
                            model: 'Index.Module',
                            root: moduleNode
                        }),
                        rootVisible: false,
                        listeners: {
                            render: function (_this, eOpts) {
                                _this.expandAll();
                            },
                            itemclick: function (view, record, item, index, event, eOpts) {
                                if (record.raw.module.moduleType == MODULE_TYPE.FUNMOD) {
                                    var tabPanel = Ext.getCmp("tabCenter");
                                    var moduleId = record.getId();//模块id
                                    var tabId = "module_" + moduleId;//模块panelid
                                    var tab = tabPanel.down("#" + tabId);//查询是否已创建模块
                                    var tabTitle = record.getData().moduleName;
                                    // 如果没有配置页面，跳转到一个固定的页面
                                    var tabURL = record.raw.module.moduleConfig ? record.raw.module.moduleConfig : DEFAULT_CONFIGPAGE;
                                    if (tab != null) {
                                        //如果已打开则跳到打开的tab
                                        tabPanel.setActiveTab(tabId);
                                    } else {
                                        if (tabPanel.items.getCount() >= 10) {
                                            Ext.Msg.alert("提示",'打开的窗口数不能超过10个,请减少打开的窗口数');
                                        } else {
                                            //创建模块配置tab
                                            var panel = {
                                                xtype: 'panel',
                                                id: tabId,
                                                title: tabTitle,
                                                closable: true,
                                                closeAction: 'destroy',
                                                listeners: {
                                                    //激活面板事件
                                                    activate: function () {
                                                        var tabPanel = indexPanel.down("#tabCenter");
                                                        if (tabPanel && this.myPanel) {
                                                            if (this.myPanel.hasListener('reDR'))
                                                                this.myPanel.fireEvent('reDR', tabPanel.getSize());
                                                        }
                                                    },
                                                    close: function () {
                                                        var tabPanel = indexPanel.down("#tabCenter");
                                                        if (tabPanel && this.myPanel) {
                                                            this.myPanel.fireEvent('close', true);
                                                        }
                                                    }
                                                },
                                                loader: {
                                                    url: GLOBAL_PATH + tabURL,
                                                    autoLoad: true,
                                                    loadMask: '正在加载...',
//                                                renderer:'html',//版本问题
                                                    closeAction: "destroy",
                                                    scripts: true,
                                                    renderer: function (loader, response, active) {
                                                        var text = response.responseText;
                                                        loader.getTarget().update(text, true, null);
                                                        return true;
                                                    },
                                                    nocache: true
                                                    /*,如果需要带参数这里再实现,如用户信息，模块信息等
                                                     params: {
                                                     childId: childId,
                                                     param: Ext.encode(param)
                                                     }*/
                                                }
                                            }
                                            var k = tabPanel.add(panel).show();
                                            tabPanel.setActiveTab(tabId);
                                        }
                                    }
                                }
                            }

                        }
                    }));
                }

            } else {
                //根节点为空
            }
            indexPanel = Ext.create('Ext.container.Viewport', {
                renderTo: Ext.getBody(),
                layout: 'border',
                listeners: {
                    resize: function (_this) {
                        var tabPanel = this.down("#tabCenter");
                        var activePanel = tabPanel.getActiveTab();
                        if (tabPanel && activePanel.myPanel) {
                            //根据窗口大小调整
                            if (activePanel.myPanel.hasListener('reDR'))
                                activePanel.myPanel.fireEvent('reDR', tabPanel.getSize());
                        }
                    }
                },
                items: [{
                    region: 'north',//上
                    xtype: 'panel',
                    height: 80,
                    bodyCls: 'top-background',
                    loader: {
                        autoLoad: true,
                        url: '<%=request.getContextPath()%>/support/sys/top',
                        renderer: function (loader, response, active) {
                            var text = response.responseText;
                            loader.getTarget().update(text, true, null);
                            return true;
                        },
                        listeners: {
                            load: function () {
                            }
                        }
                    }
                }, {
                    region: 'west',//左
                    xtype: 'panel',
                    layout: {
                        type: 'accordion',
                        animate: true,
                        collapseFirst: false
                    },
                    items: moduleTreePanels,
                    width: 200,
                    collapsible: true
                }, {
                    id: "tabCenter",
                    frame: true,
                    region: 'center',//中
                    xtype: 'tabpanel',
                    plugins: {
                        ptype: 'tabclosemenu',
                        closeTabText: '关闭当前',
                        closeOthersTabsText: '关闭其他',
                        closeAllTabsText: '关闭所有'
                    },
                    items: [{
                        id: 'module_1',
                        title: '默认主页',
                        layout: 'fit',
                        closable: true,
                        loader: INDEX_PAGE ? {
                            autoLoad: true,
                            url: GLOBAL_PATH + INDEX_PAGE,
                            renderer: function (loader, response) {
                                var text = response.responseText;
                                loader.getTarget().update(text, true, null);
                                return true;
                            }
                        } : {}
                    }]
                }]
            });
        });
    </script>
</head>
<body>
</body>
</html>
