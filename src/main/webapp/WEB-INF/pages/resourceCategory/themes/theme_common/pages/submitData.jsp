<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/4/19
  Time: 14:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String contextPath = request.getContextPath();
%>
<head>
    <title>数据发布</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/bootstrap/js/bootstrap.js"></script>
    <script src="<%=request.getContextPath()%>/Plugins/vue/vue.js"></script>
    <link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <style>
        ul, li {
            margin: 0;
            padding: 0;
        }

        li {
            list-style-type: none;
        }

        .submitcontainer {
            height: 100%;;
        }

        .submitTab a {
            color: #43aea8;
        }

        .submitTab ul.nav {
            width: 95%;
        }

        .submitTab ul.nav li {
            color: #43aea8;
            cursor: pointer;
            display: inline-block;
            font-size: 16px;
            font-weight: 600;
            line-height: 36px;
            list-style: outside none none;
            text-align: center;
            text-decoration: none;
            width: 50%;
        }

        .submitTab ul.nav li.active a {
            background-color: #43aea8;
            color: #ffffff;
        }

        .submitTab ul.nav li.active a:focus {
            background-color: #43aea8;
            color: #ffffff;
        }

        .submitTab ul.nav li.active a:hover {
            background-color: #43aea8;
            color: #ffffff;
        }

        ul.esi_nav {
            -moz-border-bottom-colors: none;
            -moz-border-left-colors: none;
            -moz-border-right-colors: none;
            -moz-border-top-colors: none;
            background-color: #ffffff;
            border-color: #8f8f8f;
            border-image: none;
            border-style: none solid solid;
            border-width: 0 1px 1px;
            overflow: visible;
            width: 95%;
        }

        ul.esi_nav li {
            position: relative;
        }

        ul.esi_nav li a {
            border-bottom: 1px solid #e5e5e5;
            color: #999999;
            cursor: pointer;
            display: block;
            font-size: 12px;
            height: 90px;
            padding-left: 20px;
        }

        ul.esi_nav a span.title {
            left: 20px;
            position: absolute;
            top: 26px;
            color: #666666;
            display: block;
            font-size: 16px;
            line-height: 36px;
            overflow: hidden;
            text-overflow: ellipsis;
            width: 280px;
        }

        ul.esi_nav a span.datetime {
            left: 160px;
            position: absolute;
            top: 10px;
        }

        ul.esi_nav a span.sender {
            left: 20px;
            position: absolute;
            top: 10px;
        }

        ul.esi_nav a span.subtitle {
            left: 20px;
            position: absolute;
            top: 48px;
            display: block;
            font-size: 14px;
            line-height: 36px;
            overflow: hidden;
            text-overflow: ellipsis;
            width: 280px;
        }

        ul.esi_nav li.selected a {
            background-color: #53c2ef;
            color: #ffffff;
        }

        ul.esi_nav li.selected a span.title {
            color: #ffffff;
        }

        .textContent {
            width: 100%;
            height: 600px;
            padding: 20px;
            background-color: #fff;
        }

        .contentInfo {
            color: #999;
            font-size: 16px;
            padding: 10px;
        }

        .contentInfo .sender {
            padding-right: 30px;
        }

        .contentTitle span.title {
            color: #707070;
            font-size: 20px;
            padding: 10px;
            text-align: center;
        }

        .content {
            padding: 10px;
        }

        .submitBtn span{
            width: 95%;
            height: 50px;
            color: #fff;
            background-color: #6f7e95;
            border-bottom: 1px solid #e5e5e5;
            cursor: pointer;
            display: block;
            font-size: 22px;
            line-height:50px;
            text-align: center;
            pading-top:5px
        }
    </style>
</head>
<div class="submitcontainer">
    <div class="container-fluid">
        <div class="row">
            <div class="col-lg-3">
                <div id="submitBtn" class="submitBtn" @click="submit()">
                    <span>+&nbsp&nbsp&nbsp发布</span>
                </div>
                <div class="submitTab">
                    <ul id="submitTab" class="nav nav-tabs" style="margin-top:20px">
                        <li v-for="item in items" :class="{active:item.iscur}" @click="setCur($index)">
                            <a>{{ item.name }}</a>
                        </li>
                    </ul>
                </div>
                <div :class="tab-pane:true">
                    <ul id="submitMenu" class="esi_nav">
                        <li v-for="item in items" :class="{selected:item.isSel}" @click="setSel($index)">
                            <a class="nav">
                                <span class="sender">龙泉街道</span>
                                <span class="datetime">2015-11-04</span>
                                <span class="title">{{ item.name }}</span>
                                <span class="subtitle">—2015年2月青岛经济运行情况分析</span>
                            </a>
                        </li>
                    </ul>
                </div>

            </div>
            <div class="col-lg-9">

                <div id="textContent" class="textContent">
                    <div class="contentInfo">
                        <span class="sender">{{ sender}}</span>
                        <span class="datetime">{{ datetime}}</span>

                    </div>
                    <div class="contentTitle">
                        <span class="title">{{ title}}</span>
                    </div>
                    <div class="content">
                        {{{ html }}}
                    </div>

                </div>

            </div>
        </div>
    </div>

</div>
<!-- Tab panes -->

<script>
    (function () {
        var submitMenu = null;
        var submitBtn = new Vue({
            el: '#submitBtn',
            data: {},
            // 在 `methods` 对象中定义方法
            methods: {
                submit: function () {
                    // 方法内 `this` 指向 submitTab
                    var height = $(window).height() - 63;
                    var html =
                            '<div class="modal-header" style="background-color: #F6F9FE">' +
                            '   <button type="button" class="close" style="margin-right:20px;" data-dismiss="modal" aria-label="Close">' +
                            '       <span aria-hidden="true" class="glyphicon glyphicon-remove"></span>' +
                            '   </button>' +
                            '   <h5 class="modal-title">' + "&nbsp" + '</h5>' +
                            '</div>'+
                            '<iframe id="iframe-esi" style="width: 99%;height:' + height + 'px;background-color:#E7EBEE;" frameborder="0"></iframe>' +

                            '<script> ' +

                            '   var esi = new EsiTheme(); ' +
                            '   $("#iframe-esi").attr("src",\'' +
                            SubmitData.contextPath + '/support/resourceCategory/analysis/text/textSubmit?themeId=' + SubmitData.page.id + '\')' +

                            '<\/script>';
                    SubmitData.esi.detailDialog(html);
                }
            }

        })
        var submitTab = new Vue({
            el: '#submitTab',
            data: {
                items: [
                    {
                        name: '全部',
                        iscur: false
                    },
                    {
                        name: '我的',
                        iscur: true
                    }
                ]
            },
            // 在 `methods` 对象中定义方法
            methods: {
                setCur: function (index) {
                    // 方法内 `this` 指向 submitTab
                    var isChange = false;
                    this.items.map(function (v, i) {
                        if (i == index && v.iscur == false) {
                            isChange = true;
                        }
                        i == index ? v.iscur = true : v.iscur = false;
                    });
                    if (submitMenu && isChange) {
                        submitMenu.setData(index);
                        submitMenu.setSel(0);
                    }
                }
            }

        })
        var submitTab = new Vue({
            el: '#textContent',
            data: {
                datetime: "2015-11-04",
                sender: "龙泉街道",
                title: "",
                subtitle: "—2015年2月青岛经济运行情况分析",
                html: ""
            },
            // 在 `methods` 对象中定义方法
            methods: {
                setContent: function (content) {
                    var _this = this;
                    SubmitData.esi.getData(content, function (d) {
                        if (d.success) {
                            var textContent = d.datas;
                            _this.html = textContent.content;
                            _this.title = textContent.name;
                            _this.sender = textContent.creatorName;
                        }
                    });
                }
            }

        })
        var SubmitData = {};
        SubmitData.uuid = '${uuid}';
        SubmitData.page = ${page};
        SubmitData.contextPath = '<%=contextPath%>';
        //页面返回的目录
        SubmitData.result = '';
        SubmitData.contents = SubmitData.page.contents;
        SubmitData.esi = new EsiTheme();
        SubmitData.content = SubmitData.contents[0];
        SubmitData.esi = new EsiTheme();
        var mapId = 0;
        //    初始化
        console.log(SubmitData.content)
        SubmitData.init = function () {
            SubmitData.esi.getData(SubmitData.content, function (d) {
                if (d.success) {
                    var pages = d.datas;
                    console.log(pages)
                    if (pages) {
                        if (pages.all && pages.all.length > 0) {
                            SubmitData.result = pages;
                            console.log(pages.user)
                            submitMenu = new Vue({
                                el: '#submitMenu',
                                data: {
                                    items: pages.user,
                                    html: "",
                                    isHidden: true,
                                },
                                // 在 `methods` 对象中定义方法
                                methods: {
                                    setSel: function (index) {
                                        // 方法内 `this` 指向 submitTab
                                        this.items.map(function (v, i) {
                                            i == index ? v.isSel = true : v.isSel = false;
                                            if (i == index) {
                                                var content = {
                                                    contentType: 5,
                                                    contentValue: v.id
                                                }
                                                submitTab.setContent(content);
                                            }
                                        });
                                    },
                                    setData: function (index) {
                                        if (index == 0) {
                                            this.items = pages.all;
                                        } else {
                                            this.items = pages.user;
                                        }
                                    }
                                }
                            })
                            submitMenu.items.map(function (v, i) {
                                if (i == 0) {
                                    v.isSel = true;
                                    var content = {
                                        contentType: 5,
                                        contentValue: v.id
                                    }
                                    SubmitData.esi.getData(content, function (d) {
                                        if (d.success) {
                                            var textContent = d.datas;
                                            submitTab.html = textContent.content;
                                            submitTab.title = textContent.name;
                                        }
                                    });
                                } else {
                                    v.isSel = false;
                                }
                            });
                        }
                    }
                }
            });
            // 加载当前分析
            SubmitData.loadData = function (content) {
                SubmitData.esi.getData(content, function (data) {
                    if (data.success) {
                        var result = data.datas;
                        if (result) {

                        }

                    }
                });
            }
        };
        $(function () {
            SubmitData.init();
        });
    })()
</script>
<script src="<%=contextPath%>/Plugins/jquery/jquery.min.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiTheme.js"></script>
</html>
