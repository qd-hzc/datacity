<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/4/22
  Time: 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
%>
<html>
<head>
    <title>文字分析发布</title>
    <meta charset="UTF-8"/>
    <link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
    <style>
        .textContainer {
            padding: 20px;
        }

        div.contentTime {
            height: 40px;
            margin-top: 20px;
            padding-left: 100px;
            padding-right: 37px;
        }

        .content {
            padding-left: 100px;
            height: 500px;
        }

        div.contentTitle {
            height: 40px;
            padding-left: 100px;
        }

        div.contentBase {
            margin-left: 20px;
            margin-right: 20px;
            margin-top: 10px;
            position: relative;
        }

        div.contentBase span.left-title {
            color: #707173;
            font-size: 18px;
            height: 40px;
            left: 0;
            line-height: 40px;
            position: absolute;
            text-align: right;
        }

        div.contentBase .inputtext {
            border: 1px solid #d9d9d9;
            color: #707173;
            font-size: 16px;
            height: 34px;
            margin-left: -2px;
            margin-top: 2px !important;
            padding: 0;
            padding-left: 20px;
            width: 100%;
        }

        i.timeBtn {
            background-image: url("<%=contextPath%>/City/resourceCategory/themes/images/timeBtn.png");
            border: 0 none;
            width: 37px;
            height: 36px;
            margin-top: 2px;
            text-align: center;
            position: absolute;
            right: 0;
            top: 0;
            z-index: 99;
        }

        .footer {
            height: 50px;
        }

        #editor {
            height: 400px;
        }

        input.btnsubmitcss {
            background: #36aeea;
            border: 0 none;
            color: #ffffff;
            cursor: pointer;
            font-size: 16px;
            font-weight: 600;
            height: 36px;
            text-align: center;
            width: 120px;
        }

        div.datetimepicker-years{
            z-index:1000;;
        }
        div.datetimepicker-months{
            z-index:1000;;
        }
        div.datetimepicker-days{
            z-index:1000;;
        }
    </style>
</head>
<body>
<div class="textContainer">
    <div class="contentBase contentTitle">
        <span class="left-title">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp标题</span>
        <input class="inputtext" type="text" id="textTitle"/>
    </div>
    <div class="contentBase contentTime">
        <span class="left-title">发布日期</span>

        <div class="input-append date form_datetime">
            <input class="inputtext" size="16" type="text" value="" id="textTime" disabled="true"
                   style="background:#fff">
            <span class="add-on"><i class="timeBtn icon-calendar" type="button"
                                    style="width: 37px;height: 36px;"></i></span>
        </div>
    </div>

    <div class="contentBase content">
        <span class="left-title">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp内容</span>
        <script type="text/plain" id="editor"></script>
    </div>
    <div class="contentBase footer">
        <div style="width:125px; height:40px; line-height:40px; margin:0 auto;">
            <input id="submitText" class="btnsubmitcss" type="button" value="发布"
                   style="margin-left:10px; margin-right:10px;"/>
        </div>
    </div>

</div>
<script src="<%=contextPath%>/Plugins/jquery/jquery.min.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap-datetimepicker.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript">
    var UEDITOR_HOME_URL = '<%=request.getContextPath()%>/Plugins/ueditor/';
    var contextPath = '<%=contextPath%>';
    var themeId = ${themeId};
    var contentId = null;
    (function () {
        $(".form_datetime").datetimepicker({
            format: "yyyy-mm-dd",
            minView: 2,
            language: 'zh-CN', //汉化
            autoclose: true,
            todayBtn: true,
            minuteStep: 10,
            pickerPosition: 'bottom-left'
        });
        $(".form_datetime").datetimepicker('setEndDate', new Date());
        /**
         * 发布
         */
        $('#submitText').click(function () {
            var $this = $(this);
            var textTitle = $.trim($('#textTitle').val());
            var textTime = $.trim($('#textTime').val());
            var textContent = ue.getContent();
            console.log(textTitle)
            console.log(textTime)
            console.log(textContent)
            if (textTitle != "" && textTime != "") {
                var msg = {
                    themeId: themeId,
                    name: textTitle,
                    id:contentId,
                    status: 3,
                    type: 1,
                    content: textContent,
                    analysisDate: parserDate(textTime),
                    sortIndex: 1

                }
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    url: contextPath + "/support/resourceCategory/analysis/text/addTextContent",
                    data: msg,
                    error: function (error) {
                        alert("出现异常!");
                    },
                    success: function (data) {
                        console.log(data);
                        //改变加载状态 已发送
                        //alert(data.msg);
                        if(data.success) {
                            alert(data.msg);
                            contentId = data.datas.id;
                            $this.attr("value", "修改");
                        }else{
                            alert(data.msg);
                        }
                    }
                });
            } else {
                alert("标题或发布日期不能为空！");
            }
            //var msgType=$('input[name="msgType"]:checked').val();
            /*if(userIds.length&&msgTitle!=""&&msgBody!=""){
             var replyMsgCoding=$('#replyMsgCoding').val();
             var msgAnnex=$('#msgAnnex').val();
             var msgCoding=$('#msgCoding').val();*/
            /*var msg={
             msgReceiver: userIds.join(','),
             msgTitle: msgTitle,
             msgBody: msgBody,
             msgType : msgType,
             replyMsgCoding: replyMsgCoding,
             msgAnnex: msgAnnex,
             msgCoding: msgCoding
             };*/
            /*$.ajax({
             type:"POST",
             dataType:"json",
             url:path+"/public/email/saveEmail",
             data : msg,
             error : function(error) {
             alert("出现异常!");
             },
             success : function(data) {
             //改变加载状态 已发送
             isNeedLoad.send[',3']=false;
             isNeedLoad.receive={};//收件
             reloadData();
             alert(data.msg);
             }
             });*/
            /*}else{
             alert('请将信息补充完整');
             }*/
        });
    })()
    /**
     * 时间格式转换
     * @param date
     * @returns {Date}
     */
    var parserDate = function (date) {
        var t = Date.parse(date);
        if (!isNaN(t)) {
            return new Date(Date.parse(date.replace(/-/g, "/")));
        } else {
            return new Date();
        }
    };
</script>
<script src="<%=contextPath%>/Plugins/ueditor/ueditor.config.js"></script>
<script src="<%=contextPath%>/Plugins/ueditor/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="<%=contextPath%>/Plugins/ueditor/lang/zh-cn/zh-cn.js"></script>
<!-- 实例化编辑器 -->
<script type="text/javascript">
    var ue = UE.getEditor('editor');
</script>
</body>
</html>
