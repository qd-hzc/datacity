<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<!--[if lt IE 7]> <html class="lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]> <html class="lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]> <html class="lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!-->
<html lang="en"> <!--<![endif]-->
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>系统登录</title>
    <link href="<%=request.getContextPath()%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/City/login/css/style.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/jquery/jquery.esi.1.0.js"></script>
    <script type="text/javascript">
        var GLOBAL_PATH = "<%=request.getContextPath()%>";
        $(function () {
            document.onkeydown = function (event) {
                var e = event || window.event || arguments.callee.caller.arguments[0];
                if (e && e.keyCode == 13) {
                    $('#submit').click();
                }
            }
            $("#submit").click(function () {
                var userName = $("#user-name").val();
                var userPwd = $("#user-pwd").val();
                if (!userName || !userPwd) {
                    $.showError('请填写用户名或密码', '提示');
                    return;
                }
                $.ajax({
                    url: GLOBAL_PATH + "/support/sys/login",
                    type: "POST",
                    dataType: "JSON",
                    data: {
                        loginName: userName,
                        loginPwd: userPwd
                    },
                    success: function (result, textStatus, jqXHR) {
                        if (result.success) {
                            if ((!result.datas) || result.datas == "") {
                                //TODO 待添加默认跳转页面 后期通过配置文件实现，添加全局变量
                                //location.href = "跳转到默认页面";
                            } else {
                                location.href = GLOBAL_PATH + result.datas;
                            }
                        } else {
                            alert(result.msg);
                        }

                    }
                });
            });
//            $("#submit").click();
        });
    </script>
    <style>
        html, body {
            height: 100%;
            overflow: hidden;
        }

        body {
            background-image: url("<%=request.getContextPath()%>/City/login/img/bg.jpg");
            background-size: 100% 100%;
        }
    </style>
</head>
<body>
<section class="container">
    <div class="login">
        <h1>登录</h1>

        <form method="post">
            <p><input type="text" id="user-name" name="user" placeholder="用户名"></p>

            <p><input type="password" id="user-pwd" name="pwd" placeholder="密码"></p>

            <p id="submit" style="text-align: right"><input type="button" name="commit" value="登录"></p>
        </form>
    </div>
</section>
</body>
</html>
