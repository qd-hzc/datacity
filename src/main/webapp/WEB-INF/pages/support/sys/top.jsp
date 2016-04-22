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
        #top_container {
            height: 50px;
            width: 345px;
            float: right;
            margin-right: 10px;
        }
    </style>
</head>
<body>
<div id="top_container"></div>
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
            height: 35,
            renderTo: 'top_container',
            margin: '5 0 0 0',
            defaults: {
                height: 25,
                margin: '5'
            },
            items: [{//用户
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
                        text: '主页1'
                    }, {
                        text: '主页2'
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
