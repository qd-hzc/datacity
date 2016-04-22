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

        ul.nav li {
            color: #4b8ed5;
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

        ul.nav li.active a, ul.nav li.active a:focus {
            background-color: #3091df;
            color: #ffffff;
        }
    </style>
</head>
<div class="submitcontainer">
    <div class="container-fluid">
        <div class="row">
            <div class="col-lg-3">
                <div>
                    <ul id="submitTab" class="{{ item.class }}" style="margin-top:20px">
                        <li v-for="item in items">
                            {{ item.name }}
                        </li>
                    </ul>
                </div>
                <div class="tab-pane">
                    <ul id="submitMenu" class="esi_nav" style="margin-top:20px;">
                        <li v-for="item in items">
                            <a class="nav">
                                <span class="datetime">2015-11-04</span>
                                <span class="sender">龙泉街道</span>
                                <span class="title">{{ item.name }}</span>
                                <span class="subtitle">—2015年2月青岛经济运行情况分析</span>
                            </a>
                        </li>
                    </ul>
                </div>

            </div>
            <div class="col-lg-9">
            </div>
        </div>
    </div>

</div>
<!-- Tab panes -->

<script>
    (function () {
        var submitTab = new Vue({
            el: '#submitTab',
            data: {
                items: [
                    {
                        name: '全部',
                        class: 'nav nav-tabs '
                    },
                    {name: '我的',
                        class: 'nav nav-tabs active'}
                ]
            }
        })
        var SubmitData = {};
        SubmitData.uuid = '${uuid}';
        SubmitData.page = ${page};
        //页面返回的目录
        SubmitData.result = '';
        SubmitData.contents = SubmitData.page.contents;
        SubmitData.esi = new EsiTheme();
        SubmitData.content = SubmitData.contents[0];
        var mapId = 0;
        //    初始化
        SubmitData.init = function () {
            SubmitData.esi.getData(SubmitData.content, function (d) {
                if (d.success) {
                    var pages = d.datas;
                    console.log(pages)
                    if (pages && pages.length > 0) {
                        SubmitData.result = pages;

                        var submitMenu = new Vue({
                            el: '#submitMenu',
                            data: {
                                items: pages
                            }
                        })

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
