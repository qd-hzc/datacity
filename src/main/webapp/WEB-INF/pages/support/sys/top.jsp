<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/25
  Time: 10:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>header</title>
    <meta charset="UTF-8"/>
    <style>
        html,body{
            height: 100%;
        }
        #top_container {
            width: 400px;
            float: right;
            text-align: right;
            padding-right: 20px;
            padding-top: 20px;
            margin-right: 10px;
            background: rgba(255, 255, 255, 0) none repeat scroll 0 0 !important;
            filter: Alpha(opacity=0);
            z-index: 1000;
        }

    </style>
</head>
<body>
<div id="top_container"></div>
<div style="position: absolute;z-index: 0;width: 100%;height: 100%"><img style="width: 100%;height: 100%;" src="<%=request.getContextPath()%>/City/support/index/img/topbanner.png"></div>
<script>
    Ext.onReady(function () {
        //用户的model
        createModel('userModel', function () {
            Ext.define('userModel', {
                extend: 'Ext.data.Model',
                fields: [{
                    name: 'id'
                }, {
                    name: 'userName',
                    type: 'string'
                }, {
                    name: 'loginName',
                    type: 'string'
                }, {
                    name: 'loginPwd',
                    type: 'string'
                }, {
                    name: 'duty',
                    type: 'string'
                }, {
                    name: 'email',
                    type: 'string'
                }, {
                    name: 'mobilePhone',
                    type: 'string'
                }, {
                    name: 'sex',
                    type: 'string'
                }, {
                    name: 'state',
                    type: 'string'
                }, {
                    name: 'userInfo',
                    type: 'string'
                }, {
                    name: 'department'
                }]
            });
        });
        //用户
        var user = new userModel(${user});

        new Ext.panel.Panel({
            width: '100%',
            height: 45,
            renderTo: 'top_container',
            margin: '5 0 0 0',
            border: false,
            bodyStyle: 'background: rgba(255, 255, 255, 0) none repeat scroll 0 0 !important; filter: Alpha(opacity=0);z-index:1000',
            defaults: {
                height: 25,
                margin: '5'
            },
            items: [
                {
                    xtype: 'button',
                    iconCls: 'Magnifier',
                    text: '搜索',
                    handler: function (e) {
                        var position = e.getPosition();
                        var temp = $('#abcde');
                        if (temp.length > 0) {
                            temp.remove();
                        } else {
                            $(window.document.body).append('<div id="abcde"  style="border: 1px solid grey;width:190px;background-color: white;">' +
                                    '<input style="width: 170px;"/>' +
                                    '<img onclick="searchResource(this);" style="position: absolute;right: 0;top:4px;cursor:pointer;" src="../../Plugins/extjs/resources/icons/magnifier.png" />' +
                                    '</div>')
                            var top = position[1] + 25;
                            var left = position[0] - 140;
                            var a = $('#abcde').css({
                                'position': 'fixed',
                                'top': top,
                                'left': left,
                                'z-index': 99999
                            });
                        }
                        window.searchResource = function (obj) {
                            var textValue = $(obj).prev().val();

                            var tabPanel = Ext.getCmp("tabCenter");
                            var tabURL = '/resourcecategory/analysis/common/analysis/goToSearchResearch';
                            //创建模块配置tab
                            var panel = {
                                xtype: 'panel',
                                title: "搜索-" + textValue,
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
                                    closeAction: "destroy",
                                    scripts: true,
                                    params: {text: textValue},
                                    renderer: function (loader, response, active) {
                                        var text = response.responseText;
                                        loader.getTarget().update(text, true, null);
                                        return true;
                                    },
                                    nocache: true
                                }
                            }
                            tabPanel.add(panel).show();
                            $('#abcde').remove();
                        }
                    }
                }, {//用户
                    xtype: 'button',
                    iconCls: 'User',
                    text: user.get('userName'),
                    handler: function () {
                        Ext.catUserWin.show(null, user);
                    }
                }, {//主页
                    xtype: 'button',
                    iconCls: 'House',
                    text: '主页',
                    menu: {
                        xtype: 'menu',
                        items: [{
                            text: '用户主页',
                            handler: function () {
                                window.location.href = contextPath + '/resourcecategory/themes/commonController/returnIndex'
                            }
                        }, {
                            text: '管理主页',
                            handler: function () {
                                window.location.href = contextPath + '/support/sys/index'
                            }
                        }]
                    }
                }, {//消息
                    xtype: 'button',
                    iconCls: 'Note',
                    text: '消息',
                    menu: {
                        xtype: 'menu',
                        items: [{
                            text: '消息1'
                        }, {
                            text: '消息2'
                        }]
                    }
                }, {//退出
                    xtype: 'button',
                    iconCls: 'Usercross',
                    text: '退出',
                    handler: function () {
                        Ext.Msg.confirm('提示', '确认退出?', function (btn) {
                            if (btn == 'yes') {
                                location.href = '<%=request.getContextPath()%>/support/sys/logout';
                            }
                        });
                    }
                }]
        });
    });
</script>
<script src="<%=request.getContextPath()%>/City/support/sys/user/userManage/catUserWin.js"></script>
</body>
</html>
