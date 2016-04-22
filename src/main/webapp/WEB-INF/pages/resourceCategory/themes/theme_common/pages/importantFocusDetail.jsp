<%--
  User: CRX
  Date: 2016/3/22
  经济指标详情页
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<%
    String contextPath = request.getContextPath();
%>

<%@include file="../common/constant.jsp" %>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>详情</title>

    <link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <style>
        .esi-margin-top-3 {
            margin-top: 3rem;
        }

        .esi-margin-3 > h5 {
            margin-bottom: 2rem;
        }

        .esi-padding-1 {
            padding: 1rem;
        }

        .esi-padding-2 {
            padding: 2rem;
        }

        .row {
            background-color: white;
            border-radius: 4px;
        }

        h5 {
            font-size: 16px;
        }
    </style>
</head>
<body style="background-color:#E7EBEE;">

<jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
<jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>

<div data-id="detail" data-uuid='${uuid}' class="container-fluid" style="padding:0 4rem 4rem;">

    <div class="row esi-margin-top-3">
        <div class="col-xs-12 esi-padding-1">
            <h5>总体情况</h5>
        </div>
        <div class="col-xs-12 esi-padding-2">
            <div data-id="detaildiv1" data-uuid="${uuid}">
            </div>
        </div>
    </div>

    <div class="row esi-margin-top-3">
        <div class="col-xs-12 esi-padding-1">
            <h5>趋势</h5>
        </div>
        <div class="col-xs-12 esi-padding-2">
            <div data-id="detaildiv2" data-uuid="${uuid}">
            </div>
        </div>
    </div>

    <div class="row esi-margin-top-3">
        <div class="col-xs-12 esi-padding-1">
            <h5>地域信息</h5>
        </div>
        <div class="col-xs-12 esi-padding-2">
            <div data-id="detaildiv3" data-uuid="${uuid}">
            </div>
        </div>
    </div>

    <div class="row esi-margin-top-3" style="background-color: #E7EBEE;">
        <div class="col-xs-6">
            <div style="margin-right: 1rem;">
                <div class="row">
                    <div class="col-xs-12 esi-padding-1">
                        <h5>对标</h5>
                    </div>
                    <div class="col-xs-12 esi-padding-2">
                        <div data-id="detaildiv4" data-uuid="${uuid}">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-xs-6">
            <div style="margin-left: 1rem;">
                <div class="row">
                    <div class="col-xs-12 esi-padding-1">
                        <h5>结构</h5>
                    </div>
                    <div class="col-xs-12 esi-padding-2">
                        <div data-id="detaildiv5" data-uuid="${uuid}">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<%=contextPath%>/Plugins/jquery/jquery.min.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/Plugins/eharts/echarts-all.js"></script>

<script src="<%=contextPath%>/Plugins/jquery/jquery.esi.1.0.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiTheme.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiReport.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/analysis/chart/echarts/EsiChart.js"></script>

<script>
    (function () {

        var contextPath = '<%=contextPath%>';
        var Detail = {};
        Detail.uuid = '${uuid}';
//        当前页面
        Detail.page = ${page};
        Detail.esi = new EsiTheme();

//        初始化
        Detail.init = function () {

            var contents = Detail.page.contents;

            if (contents && contents.length > 0) {
                Detail.esi.getAllData(contents, function (d) {
                    if (d.success) {
                        $.each(d.datas, function (i, n) {
                            var datas = n.data;
                            if (datas && datas.length > 0) {
//                                加载菜单详细内容
                                for (var j = 0; j < datas.length; j++) {
                                    var moduleId = datas[j].id;
                                    var dom = $('[data-uuid=' + Detail.uuid + '][data-id=' + n.content.containerId + ']');
                                    Detail.esi.load(dom, moduleId);
                                }
                            } else {
                                $.showSucc('暂无数据', '提示');
                            }
                        })
                    } else {
                        $.showError(d.msg, '提示');
                    }
                });
            }
        }

        $(function () {
            Detail.init();
        });
    })()
</script>
</body>
</html>
