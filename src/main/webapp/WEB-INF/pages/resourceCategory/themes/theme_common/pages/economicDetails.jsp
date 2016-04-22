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
        .esi-margin-3 {
            margin-top: 3rem;
        }

        .esi-margin-3 > h4 {
            margin-bottom: 2rem;
        }

        .esi-padding-2 {
            padding-top: 2rem;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
<jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>

<div data-id="detail" data-uuid='${uuid}' class="container">

    <div data-id="detaildiv1" data-uuid="${uuid}" class="esi-margin-3">
        <h4>总体情况</h4>
    </div>

    <div data-id="detaildiv2" data-uuid="${uuid}" class="esi-margin-3">
        <h4>排名&对标情况</h4>
    </div>

    <div data-id="detaildiv3" data-uuid="${uuid}" class="esi-margin-3">
        <h4>结构分析</h4>
    </div>

    <div data-id="detaildiv4" data-uuid="${uuid}" class="esi-margin-3">
        <h4>完成情况</h4>
    </div>
</div>

<script data-id="menutemp" data-uuid="${uuid}" type="text/html">
    <li role="presentation">
        <a data-id="${uuid}{id}" aria-controls="total1" role="tab" data-toggle="tab"
           href="#${uuid}{id}">{name}</a>
    </li>
</script>

<script data-id="contenttemp" data-uuid="${uuid}" type="text/html">
    <div role="tabpanel" class="tab-pane esi-padding-2" id="${uuid}{id}">
        {name}
    </div>
</script>

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
//        页面之间传值
        Detail.data = ${data} ? JSON.parse('${data}') : null;
        Detail.uuid = '${uuid}';
//        当前页面
        Detail.page = ${page};
        Detail.esi = new EsiTheme();

//        初始化
        Detail.init = function () {

            var contents = Detail.page.contents;

            /**
             * 生成菜单和内容区域
             * @param datas
             * @param n
             */
            function genMenuAndContentArea(datas, n) {
                var menuStr = '<ul class="nav nav-tabs" role="tablist">';
                var contentStr = '<div class="tab-content">';
                var menuTemp = $('[data-id=menutemp][data-uuid=' + Detail.uuid + ']').html();
                var contentTemp = $('[data-id=contenttemp][data-uuid=' + Detail.uuid + ']').html();

//                                生成菜单和内容区域
                for (var i = 0; i < datas.length; i++) {
                    var menu = datas[i];
                    menuStr += $.render(menuTemp, menu);
                    contentStr += $.render(contentTemp, menu);
                }
                menuStr += '</ul>';
                contentStr += '</div>';
                $('[data-id=' + n.containerId + '][data-uuid=' + Detail.uuid + ']').append(menuStr + contentStr);
            }

            if (contents && contents.length > 0) {
                Detail.esi.getAllData(contents, function (d) {
                    if (d.success) {
                        $.each(d.datas, function (i, n) {
                            var datas = n.data;
                            if (datas && datas.length > 0) {
                                genMenuAndContentArea(datas, n.content);
//                                加载菜单详细内容
                                for (var j = 0; j < datas.length; j++) {
                                    var content = datas[j].id;
                                    var dom = $('#' + Detail.uuid + content);
                                    Detail.esi.load(dom, content);
                                    if (j == 0) {
                                        $('[data-id=' + Detail.uuid + content + ']').attr('aria-expanded', 'true')
                                                .parent().addClass('active');
                                        dom.addClass('active');
                                    }
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
