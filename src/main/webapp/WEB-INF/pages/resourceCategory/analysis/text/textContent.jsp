<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/5/18
  Time: 9:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文字分析</title>
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
            height: 100%;
            width: 3px;
        }

        ul.esi_nav li.selected a span.sel {
            position: absolute;
            top: 0px;
            left: 1px;
            height: 100%;
            width: 3px;
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

        #center {
            MARGIN-RIGHT: auto;
            MARGIN-LEFT: auto;
        }
    </style>
    <jsp:include page="/WEB-INF/pages/common/imp.jsp"></jsp:include>
    <script src="<%=request.getContextPath()%>/Plugins/vue/vue.js"></script>
</head>
<body>


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


<script>
    (function () {
        var submitTab = new Vue({
            el: '#textContent',
            data: {
                datetime: "",
                sender: "",
                title: "",
                subtitle: "",
                html: ""
            }
        })
        Ext.Ajax.request({
            url: contextPath + "/support/resourceCategory/analysis/text/queryContentById",
            method: 'POST',
            params: {
                contentId: ${contentId}
            },
            success: function (response, opts) {
                var result = Ext.JSON.decode(response.responseText);
                if (result) {
                    var textContent = result;
                    submitTab.html = textContent.content;
                    submitTab.title = textContent.name;
                    submitTab.sender = textContent.creatorName;
                    var oldTime = (new Date(textContent.analysisDate)).getTime(); //得到毫秒数
                    var newTime = new Date(oldTime); //就得到普通的时间了
                    submitTab.datetime = newTime.Format("yyyy-MM-dd").toString();
                } else {
                    Ext.Msg.alert('提示', '无内容');
                }
            }
        });
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
</body>
</html>
