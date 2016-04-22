<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2016/4/7
  Time: 14:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>测试页面</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/bootstrap/css/bootstrap-theme.css"/>
    <style type="text/css">
        html,body{
            height: 100%;
        }
        .fcontainer {
            width: 100%;
            /*height: 87%;*/
            position: fixed;
        }

        .fcontainer iframe {
            width: 100%;
            height: 100%;
            border: 0;
        }
    </style>

    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/bootstrap/js/bootstrap.js"></script>
    <script src="<%=request.getContextPath()%>/Plugins/vue/vue.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/themes/EsiTheme.js"></script>

</head>
<body style="padding: 0px 5px;">
<!--  导航条begin  -->
<nav class="navbar navbar-default navbar-fixed-top" style="margin-bottom: 0px">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#"><strong>
                数据城市
            </strong></a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li class=""><a href="#"><span class="glyphicon glyphicon-envelope"
                                               aria-hidden="true"></span>&nbsp;消息</a></li>
                <li class=""><a href="#"><span class="glyphicon glyphicon-user" aria-hidden="true"></span>&nbsp;我的</a>
                </li>
                <li class=""><a href="#"><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;设置</a>
                </li>

            </ul>
        </div>
    </div>
</nav>
<!--  导航条end  -->
<!--  标签页begin  -->
<div class="container-fluid" style="padding-top: 56px;" id="esiTab">
    <div class="row">
        <div class="col-md-12" style="padding: 0px">
            <!-- Nav tabs -->
            <ul class="nav nav-tabs">
                <li v-for="(index, nav) in navs" v-bind:class="index==0? 'active':''">
                    <a href="#nav_{{nav.id}}" data-toggle="tab">
                        {{ nav.name }}
                    </a>
                </li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content" id="navContent">
                <div class="tab-pane" id="nav_{{nav.id}}" v-for="(index, nav) in navs"
                     v-bind:class="index==0? 'active':''">
                    <div class="fcontainer">
                        <iframe width="99%" height="100%"
                                v-bind:src="'<%=request.getContextPath()%>/resourcecategory/themes/commonController/returnPage?themePageId='+nav.id">
                        </iframe>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--  标签页end  -->
<script type="text/javascript">

    var esiPage = ${page};
    $(function () {
        $(window).resize(function(){
            $('.fcontainer').height(document.body.clientHeight-100);
        });
        $('.fcontainer').height(document.body.clientHeight-100);
        console.log(document.body.clientHeight-100);
        var theme = new EsiTheme();
        var initData = {
            navs: [{
                id: 0,
                name: '无数据'
            }]
        };
        var navV = new Vue({
            el: '#esiTab',
            data: initData
        });
        if (esiPage && esiPage.contents && esiPage.contents.length > 0) {
            theme.getData(esiPage.contents[0], function (result) {
                var datas = null;
                if (result.success) {
                    datas = result.datas;
                    initData.navs = datas;
                }
            });
        }
    });
</script>

</body>
</html>
