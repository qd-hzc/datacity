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
        .submitTab {

        }
        .submitTab a {
            color: #43aea8;
        }

        .submitTab ul.nav {
            width: 100%;
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
            border-style: none;
            border-width: 0 1px 1px;
            overflow: visible;
            width: 100%;
            border-radius: 0 0 4px 4px;

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
        ul.esi_nav li a span.sel {
            position: absolute;
            top: 0px;
            left: 0px;
            height:100%;
            width:3px;
        }
        ul.esi_nav li.selected a span.sel {
            position: absolute;
            top: 0px;
            left: 1px;
            height:100%;
            width:3px;
            background-color: #43aea8;
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
            background-color: #f0f7fd;
        }

        /*ul.esi_nav li.selected a span.title {
            color: #ffffff;
        }*/

        .textContent {
            width: 100%;
            height: 600px;
            padding: 20px;
            overflow: auto;
            border-radius: 5px;
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

        .submitBtn span {
            width: 100%;
            height: 50px;
            color: #fff;
            background-color: #6f7e95;
            border-bottom: 1px solid #e5e5e5;
            cursor: pointer;
            display: block;
            font-size: 22px;
            line-height: 50px;
            text-align: center;
            border-radius: 5px;
            pading-top: 5px
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
                <div class="tab-pane submitMenu" style="overflow:auto;padding-right:2px;">
                    <ul id="submitMenu" class="esi_nav">
                        <li v-for="item in items" :class="{selected:item.isSel}" @click="setSel($index)">
                            <a class="nav">
                                <span class="sel"></span>
                                <span class="sender">{{ item.creatorName}}</span>
                                <span class="datetime">{{ item.analysisDate }}</span>
                                <span class="title">{{ item.name }}</span>
                                <span class="subtitle">{{item.subTitle}}</span>
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
        var height = $(window).height();
        $(".textContent").height(height-130);
        $(".submitMenu").height(height-205);
        var submitMenu = null;
        submitMenu = new Vue({
            el: '#submitMenu',
            data: {
                items: [],
                html: "",
                isHidden: true,
            },
            // 在 `methods` 对象中定义方法
            methods: {
                setSel: function (index) {
                    // 方法内 `this` 指向 submitTab
                    var hasData = false;
                    this.items.map(function (v, i) {
                        hasData = true;
                        i == index ? v.isSel = true : v.isSel = false;
                        if (i == index) {
                            var content = {
                                contentType: 5,
                                contentValue: v.id
                            }
                            submitTab.setContent(content);
                        }
                    });
                    if (!hasData) {
                        submitTab.datetime = "";
                        submitTab.sender = "";
                        submitTab.title = "";
                        submitTab.subtitle = "";
                        submitTab.html = "";
                    }
                },
                setData: function (index, pages) {
                    if (index == 0) {
                        this.items = pages.all;
                    } else {
                        this.items = pages.user;
                    }
                }
            }
        })
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
                            '</div>' +
                            '<iframe id="iframe-esi" style="width: 99%;height:' + height + 'px;background-color:#E7EBEE;" frameborder="0"></iframe>' +

                            '<script> ' +

                            '   var esi = new EsiTheme(); ' +
                            '   $("#iframe-esi").attr("src",\'' +
                            SubmitData.contextPath + '/support/resourceCategory/analysis/text/textSubmit?themeId=' + SubmitData.content.contentValue + '\')' +

                            '<\/script>';
                    SubmitData.esi.detailDialog(html);
                    $(".close").on("click",function(){
                        SubmitData.init();
                    })
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
                    var hasText = false;
                    this.items.map(function (v, i) {
                        if (i == index && v.iscur == false) {
                            isChange = true;
                        }
                        i == index ? v.iscur = true : v.iscur = false;
                    });
                    if (submitMenu && isChange) {
                        submitMenu.setData(index, SubmitData.result);
                        submitMenu.setSel(0);
                    }
                }
            }

        })
        var submitTab = new Vue({
            el: '#textContent',
            data: {
                datetime: "",
                sender: "",
                title: "",
                subtitle: "",
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
                            var oldTime = (new Date(textContent.analysisDate)).getTime(); //得到毫秒数
                            var newTime = new Date(oldTime); //就得到普通的时间了
                            _this.datetime = newTime.Format("yyyy-MM-dd").toString();
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
        SubmitData.init = function () {
            SubmitData.esi.getData(SubmitData.content, function (d) {
                if (d.success) {
                    var pages = d.datas;
                    if (pages) {
                        if (pages.all) {
                            $.each(pages, function (i, page) {
                                $.each(page, function (i, text) {
                                    var oldTime = (new Date(text.analysisDate)).getTime(); //得到毫秒数
                                    var newTime = new Date(oldTime); //就得到普通的时间了
                                    text.analysisDate = newTime.Format("yyyy-MM-dd").toString();
                                })
                            })

                            SubmitData.result = pages;
                            if (submitMenu) {
                                submitMenu.setData(1, SubmitData.result);
                                submitMenu.setSel(0);
                            }
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
                                            submitTab.sender = textContent.creatorName;
                                            var oldTime = (new Date(textContent.analysisDate)).getTime(); //得到毫秒数
                                            var newTime = new Date(oldTime); //就得到普通的时间了
                                            submitTab.datetime = newTime.Format("yyyy-MM-dd").toString();
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
        window.onresize = function(){
            var height = $(window).height();
            $(".textContent").height(height-130);
            $(".submitMenu").height(height-205);
        }
    })()
    // 对Date的扩展，将 Date 转化为指定格式的String
    // 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
    // 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
    // 例子：
    // (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
    // (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
    Date.prototype.Format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1,                 //月份
            "d+": this.getDate(),                    //日
            "h+": this.getHours(),                   //小时
            "m+": this.getMinutes(),                 //分
            "s+": this.getSeconds(),                 //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }
</script>
</html>
