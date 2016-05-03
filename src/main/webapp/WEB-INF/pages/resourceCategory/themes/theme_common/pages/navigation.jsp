<%--
  User: CRX
  Date: 2016/3/21
  导航栏
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
    <title>导航页</title>
    <link href="<%=contextPath%>/City/resourceCategory/themes/css/navigation.less" rel="stylesheet/less">
    <link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">

</head>
<body>

<jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
<jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>

<script data-id="navigationMenuTemp" data-uuid="${uuid}" type="text/template">
    <li role="presentation" style="cursor: pointer;" data-uuid="${uuid}" data-function="changeTab">
        <a data-id="{id}" data-index="{index}" aria-controls="home"
           role="tab" data-toggle="tab">{name}</a>
    </li>
</script>

<script data-id="navigationContentTemp" data-uuid="${uuid}" type="text/template">
    <div data-id="navigationContent{id}" data-uuid="${uuid}" role="tabpanel" class="tab-pane"></div>
</script>

<div class="container-fluid">

    <ul data-uuid="${uuid}" data-id="navigationdiv1" class="nav nav-pills nav-justified esi-nav-pills"
        role="tablist"></ul>

    <div data-uuid="${uuid}" data-id="navigationdiv2" class="tab-content"
         style="margin-top: 18px;"></div>

</div>
<script src="<%=contextPath%>/Plugins/less/less.min.js"></script>
<script src="<%=contextPath%>/Plugins/jquery/jquery.min.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=contextPath%>/Plugins/eharts/echarts-all.js"></script>
<script src="<%=request.getContextPath()%>/Plugins/vue/vue.js"></script>
<script src="<%=contextPath%>/Plugins/jquery/jquery.esi.1.0.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiTheme.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiReport.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/analysis/chart/echarts/EsiChart.js"></script>

<script>
    (function () {
        var contextPath = '<%=contextPath%>';

        var NAVIGATION = {};
        NAVIGATION.uuid = '${uuid}';
        //页面配置
        NAVIGATION.page =${page};
        //页面配置详细
        NAVIGATION.content = NAVIGATION.page.contents[0];
        //页面返回的目录
        NAVIGATION.result = '';
        NAVIGATION.esi = new EsiTheme();
        /**
         * 初始化
         */
        NAVIGATION.init = function () {
            NAVIGATION.esi.getData(NAVIGATION.content, function (d) {
                var menuContainer = $('[data-id=navigationdiv1][data-uuid=' + NAVIGATION.uuid + ']');
                if (d.success) {
                    var pages = d.datas;
                    if (pages && pages.length > 0) {
                        NAVIGATION.result = pages;
                        var tempMenuHtml = $('[data-id=navigationMenuTemp][data-uuid=' + NAVIGATION.uuid + ']').html();
                        var tempContentHtml = $('[data-id=navigationContentTemp][data-uuid=' + NAVIGATION.uuid + ']').html();
                        var menuHtml = '';
                        var contentHtml = '';
                        $.each(pages, function (i, n) {
                            n.index = i;
                            menuHtml += $.render(tempMenuHtml, n);
                            contentHtml += $.render(tempContentHtml, n);
                        });
                        menuContainer.html(menuHtml);
                        $('[data-uuid=' + NAVIGATION.uuid + '][data-id=navigationdiv2]').html(contentHtml);
                        var menua = menuContainer.find('li:first').addClass('active').find('a');
                        NAVIGATION.loadMenuData(menua);
                    }
                    NAVIGATION.changeTab();
                } else {
                    menuContainer.html('无内容');
                }
            });
        }
        //菜单点击切换
        NAVIGATION.changeTab = function () {
            $('[data-function=changeTab][data-uuid=' + NAVIGATION.uuid + ']').delegate('a', 'click', function () {
                var self = $(this);
                NAVIGATION.loadMenuData(self);
            });
        }

//        加载菜单数据
        NAVIGATION.loadMenuData = function (self) {

            var index = self.data('index');
            $('[data-uuid=' + NAVIGATION.uuid + '][data-id=navigationdiv2]').children('div').removeClass('active').eq(index).addClass('active');
            var page = NAVIGATION.result[index];
            var con = $('[data-id=navigationContent' + page.id + '][data-uuid=' + NAVIGATION.uuid + ']');
            if (!con.html()) {
                NAVIGATION.esi.getPageHtml(page.id, function (data) {
                    if (data.success == false)return;
                    $(con).html(data);
                })
            }
        }

        $(function () {
            NAVIGATION.init();
        });
    })()
</script>
</body>
</html>