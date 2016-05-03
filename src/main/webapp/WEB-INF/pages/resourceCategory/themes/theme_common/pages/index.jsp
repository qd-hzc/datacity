<%@ page import="com.city.common.util.SessionUtil" %>
<%@ page import="com.city.support.sys.user.pojo.CurrentUser" %>
<%--
  User: HZC
  Date: 2016/3/18
  主题模板--> 主页
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<%
    String contextPath = request.getContextPath();
    CurrentUser currentUser = SessionUtil.getCurrentUser(request.getSession());
%>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>数据城市 英瑞利</title>

    <link rel="icon" sizes="any" mask href="<%=contextPath%>/City/resourceCategory/themes/images/icon.jpg">
    <link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=contextPath%>/City/resourceCategory/themes/css/index.less" rel="stylesheet/less">

</head>
<body>

<div class="container-fluid">
    <div class="row">
        <div class="col-xs-12">
            <div class="row" id="title">
                <div class="col-xs-7 col-sm-8 col-md-9 esi-main-title">
                    <span class="esi-title">济南统计局</span>
                </div>
                <div class="col-xs-5 col-sm-4 col-md-3 text-center esi-main-setting">
                    <div class="row">
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                    aria-haspopup="true" aria-expanded="false">
                                <span class="glyphicon glyphicon-envelope"></span>
                                <span>消息</span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="#">系统内消息</a></li>
                            </ul>
                        </div>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                    aria-haspopup="true" aria-expanded="false">
                                <span class="glyphicon glyphicon-user"></span>
                                <span>${user.userName}</span>
                            </button>
                            <ul class="dropdown-menu">
                                <li id="manageUrl"><a href="<%=contextPath%><%=currentUser.getUser().getIndexPage()%>">管理主页</a>
                                </li>
                                <li><a href="#">我的订阅</a></li>
                                <li><a href="#">个人消息</a></li>
                                <li><a href="#" onclick="logOut()">退出登录</a></li>
                            </ul>
                        </div>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                    aria-haspopup="true" aria-expanded="false">
                                <span class="glyphicon glyphicon-th-large"></span>
                                <span>设置</span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="#">系统设置</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">

        <iframe class="col-xs-12" id="indexdiv1" style="padding-right: 0px;padding-left: 0px;" frameborder="0">
        </iframe>

    </div>
</div>

<script src="<%=contextPath%>/Plugins/less/less.min.js"></script>
<script src="<%=contextPath%>/Plugins/jquery/jquery.min.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiTheme.js"></script>

<script>
    (function () {
        var contextPath = '<%=contextPath%>';

        var INDEX = {};
        INDEX.page =${page};
        INDEX.content = INDEX.page.contents[0];
        INDEX.esi = new EsiTheme();
//        初始化
        INDEX.init = function () {
            var manageRole = <%=currentUser.getUser().isManageRole()%>;
            if (!manageRole) {
                $('#manageUrl').remove();
            }
            $('#indexdiv1').attr('src', INDEX.esi.getPageUrl(INDEX.content.contentValue));
        }

        $(function () {
            INDEX.init();
            function resetHeight() {
                var height = $(window).height();
                $('#indexdiv1').height(height - $('#title').height());
            }

            resetHeight();

            $(window).resize(function () {
                resetHeight();
            });
        });

        window.logOut = function () {
            window.location.href = contextPath + '/support/sys/logout'
        }

    })()
</script>
</body>
</html>
