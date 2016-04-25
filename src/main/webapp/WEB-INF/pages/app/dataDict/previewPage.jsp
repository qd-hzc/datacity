<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/4/20
  Time: 14:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>预览界面</title>
    <meta charset="UTF-8"/>
    <link href="<%=request.getContextPath()%>/City/app/dataDict/css/main.css" media="screen" rel="stylesheet">
</head>
<body>
<section id="devices">
    <div class="deviceWrap iphone-6-portrait">
        <div style="width: 375px;height: 603px;padding-top: 20px;padding-bottom: 44px;" class="device">
            <div style="height: 20px; width: 375px" class="flashingTop"><span
                    class="time">3:58 <span>PM</span></span></div>
            <div style="height: 44px" class="flashingBottom"></div>
            <iframe id="iphone-6-portrait"
                    src="<%=request.getContextPath()%>/app/dataDict/previewContent?menuId=${menuId}&name=${name}"></iframe>
        </div>
    </div>
</section>
</body>
</html>
