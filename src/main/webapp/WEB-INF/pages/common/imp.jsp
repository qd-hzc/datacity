<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!--引入extjs-->
<link href="<%=request.getContextPath()%>/Plugins/extjs/resources/ext-theme-green/happy-theme-green-all.css"
      rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/extjs/resources/css/icon.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/extjs/ext-all.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/extjs/ux/ComboBoxTree.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/extjs/ux/QueryPicker.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/extjs/ext-locale-zh_CN.js"></script>
<!--引入jquery-->
<script src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
<script type="text/javascript">
    Ext.override(Ext.data.proxy.Ajax, {
        constructor: function () {
            this.callParent(arguments);
            this.defaultExceptionHandler = this.on('exception', function (proxy, obj, operation) {
                var result = null;
                var datas = null;
                try {
                    result = Ext.JSON.decode(obj.responseText);
                    datas = result.datas;
                    if (datas != null) {
                        switch (datas.actionHandler) {
                            case "login":
                                window.location.href = '<%=request.getContextPath()%>';
                                break;
                            case "showMsg":
                                Ext.Msg.alert("提示", result.msg);
                                break;
                            case "callBack":
                                eval(datas.callBack);
                                break;
                            default :
                                Ext.Msg.alert("提示", result.msg, function () {
                                    var fun = datas.callback ? datas.callback : "";
                                    eval(fun);
                                });
                        }
                    } else if (datas == null) {
                        Ext.Msg.alert("提示", result.msg, function () {
                            var fun = result.callback ? result.callback : "";
                            eval(fun);
                        });
                    }
                } catch (e) {
                    if (obj.getResponseHeader('toIndex') == "yes")
                        Ext.Msg.alert("提示", "登录超时", function () {
                            window.location.href = '<%=request.getContextPath()%>'
                        });
                }
            })
        }
    });
    //封装model
    function createModel(name, fnt) {
        var depModel = null;
        if (!Ext.ClassManager.isCreated(name)) {
            if (fnt) {
                fnt();
            }
            depModel = Ext.ClassManager.get(name);
        } else {
            depModel = Ext.ClassManager.get(name);
        }
        return depModel
    }
    createModel('nullid', function () {
        Ext.define('Ext.data.NullidGenerator', {
                    extend: 'Ext.data.identifier.Generator',
                    alias: 'data.identifier.nullid',
                    isUnique: true,
                    config: {
                        id: null
                    },
                    constructor: function (config) {
                        this.callParent([config]);

                        this.reconfigure(config);
                    },
                    reconfigure: function () {

                        this.generate = function () {
                            return "";
                        };
                    },

                    clone: null
                },
                function () {
                    this.Global = new this({
                        id: 'nullid'
                    });
                });
    });
    //可监听resize的面板
    createModel('Ext.resizablePanel', function () {
        Ext.define('Ext.resizablePanel', {
            xtype: 'resizablepanel',
            extend: 'Ext.panel.Panel',
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
                        if (Obj)
                            this.updateBox(Obj)
                    });
                }
            }
        });
    });
    //自带消除触发器的文本框,将change事件绑定到handler上
    createModel('Ext.form.field.TriggerText', function () {
        Ext.define('Ext.form.field.TriggerText', {
            xtype: 'triggertext',
            emptyText: '请输入名称检索',
            extend: 'Ext.form.field.Text',
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.reset();
                    }
                }
            },
            listeners: {
                render: function (_this) {
                    if (_this.handler) {
                        _this.on('change', _this.handler);
                    }
                }
            }
        });
    });
    /**
     * 返回复制节点
     * @param node 被复制的节点
     * @param deep 是否复制子节点：true，false
     * @param keepNodeInfo 是否保留节点id：true，false
     * @returns {*} 返回复制节点
     */
    function copyNode(node, deep, keepNodeInfo) {
        var _thisFun = arguments.callee,
                _targetNode = node,
                _keepNodeInfo = keepNodeInfo,
                nodeData = Ext.clone(_targetNode.data);

        if (!_keepNodeInfo) {
            nodeData.id = null;
        }
        nodeData.root = false;

        var cloneNode = _targetNode.createNode(nodeData);

        if (deep) {
            if (_targetNode.childNodes) {
                Ext.Array.each(_targetNode.childNodes, function (data) {
                    cloneNode.appendChild(_thisFun(data, true, _keepNodeInfo));
                })
            }
        }
        return cloneNode;
    }
    var contextPath = '<%=request.getContextPath()%>';
    var UEDITOR_HOME_URL = '<%=request.getContextPath()%>/Plugins/ueditor/';
</script>
<script type="text/javascript" charset="utf-8"
        src="<%=request.getContextPath()%>/Plugins/ueditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8"
        src="<%=request.getContextPath()%>/Plugins/ueditor/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8"
        src="<%=request.getContextPath()%>/Plugins/ueditor/lang/zh-cn/zh-cn.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/extjs/ux/UEditorField.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/extjs/ux/TabCloseMenu.js"></script>
