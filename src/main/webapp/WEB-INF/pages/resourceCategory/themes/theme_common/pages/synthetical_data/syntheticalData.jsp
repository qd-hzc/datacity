<%--
  Created by IntelliJ IDEA.
  User: CRX
  Date: 2016/4/20
  Time: 15:01
  content:综合数据
  To change this template use File | Settings | File Templates.
--%>
<%
    String contextPath = request.getContextPath();
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap-submenu.min.css" rel="stylesheet">

<style>
    body {
        background-color: #E9E9E9;
    }

    .forDrop > .nav-justified > li {
        display: table-cell !important;
        width: 10% !important;
        min-width: 5rem !important;
        max-width: 10rem !important;
    }

    .forDrop > .nav-pills > li > a {
        background-color: white;
        color: lightseagreen;
        padding-top: 15px !important;
        border-radius: 0px;
    }

    .forDrop > .nav-pills > li.active > a, .nav-pills > li.active > a:focus, .nav-pills > li.active > a:hover {
        background-color: lightseagreen;
        color: white;
    }

    .forDrop > .list-group-item.active, .list-group-item.active:focus, .list-group-item.active:hover {
        border: none;
        background-color: white;
        /*border-color: lightseagreen;*/
        color: lightseagreen;
    }

    .group {
        border: none;
        padding: 15px 10px;
    }

    .list-group-item:first-child, .list-group-item:last-child {
        border-top-left-radius: 0px;
        border-top-right-radius: 0px;
    }

    .dropdown:hover {
        border-left: 10px solid lightseagreen;
        color: lightseagreen;
    }

    .dropdown-menu {
        border-radius: 0px;
        left: 100%;
        top: 0;
        width: 200px;
        margin-top: 0px !important;
        border: 0px !important;
    }

    .dropdown-submenu {
        position: static;
    }

    .dropdown-submenu > a::after {
        content: none;
    }

    .dropdown-menu > li > a:hover, .dropdown-menu > li > a:focus {
        background-color: white;
        background-image: none;
        color: lightseagreen;
    }

    .dropdown-menu > li {
        height: 35px;
    }

    .dropdown {
        position: static;
    }

    .glyphicon-menu-right {
        float: right;
    }

    .esi-dropdown-submenu > a {
        white-space: normal !important;
    }

    .esi-right {
        margin-top: 2px;
    }

    .esi-parent {
        font-weight: 800 !important;
    }
</style>

<script data-id="syntheticalMenuTemp" data-uuid="${uuid}" type="text/template">
    <li role="presentation" data-uuid="${uuid}" data-click="li1">
        <a href="#${uuid}-{id}" data-name="{name}" data-id="${uuid}" aria-controls="${uuid}-{id}" role="tab"
           data-toggle="tab">
            {name}
        </a>
    </li>
</script>

<script data-id="syntheticalContentTemp" data-uuid="${uuid}" type="text/template">
    <div id="${uuid}-{id}"
         data-id="syntheticalContent" data-uuid="${uuid}" role="tabpanel" class="tab-pane">
</script>
<script type="text/template" data-id="syntheticalDropdownTemp" data-uuid="${uuid}">
    <div class="dropdown m-b">
        <div data-submenu="" data-toggle="dropdown" class="list-group-item group dropdown-toggle"
             aria-expanded="false" data-uuid="${uuid}" data-click="li2">
            <div style="margin-left: 30px;width: 80%;cursor:pointer" data-name="{name}">
                {name}
                <span class="glyphicon glyphicon-menu-right"></span>
            </div>
        </div>
</script>
<script type="text/template" data-id="syntheticalDropMenu" data-uuid="${uuid}">
    <ul class="dropdown-menu">
</script>
<script type="text/template" data-id="syntheticalParent" data-uuid="${uuid}">
    <li><a tabindex="0" class="esi-parent" data-index="{index}">{name}</a></li>
</script>
<script type="text/template" data-id="syntheticalChild1" data-uuid="${uuid}">
    <li><a tabindex="0" class="esi-child" data-uuid="${uuid}" data-click="li" style="cursor:pointer;"
           data-id="{id}" data-name="{name}" data-index="{index}">{name}</a></li>
</script>
<script type="text/template" data-id="syntheticalChild2" data-uuid="${uuid}">
    <li class="dropdown-submenu esi-dropdown-submenu">
        <a tabindex="0" class="esi-child" data-name="{name}" data-uuid="${uuid}" data-click="li" data-index="{index}"
           style="cursor: pointer;">
            {name}
            <span class="glyphicon glyphicon-menu-right esi-right"></span>
        </a>

</script>
<div class=" row">

    <div data-id="syntheticalDiv1" class=" col-md-2 col-sm-3 col-xs-4 forDrop" style="padding-right: 0px;">

        <ul data-id="syntheticalDiv11" data-uuid="${uuid}"
            class="nav nav-pills nav-justified" role="tablist">

        </ul>

        <div data-id="syntheticalDiv12" data-uuid="${uuid}" class="tab-content">

        </div>
    </div>

    <div class="col-md-10 col-sm-9 col-xs-8">
        <div style="background-color: white;min-height: 500px;width: 100%;padding: 20px 15px;">
            <div class="row" style="margin-bottom: 15px;">
                <div class="col-md-10">
                    <a data-uuid="${uuid}" data-id="index" style="cursor: pointer;">首页</a>

                    <span data-uuid="${uuid}" data-id="menu"></span>
                </div>
                <div class="col-md-2">
                    <span aria-hidden="true" class="glyphicon glyphicon-star-empty close"
                          style="margin-right: 10px;"></span>
                    <span aria-hidden="true" class="glyphicon glyphicon-share-alt close"
                          style="margin-right:10px;"></span>
                </div>
            </div>

            <div data-id="syntheticalDiv2" data-uuid="${uuid}" style="padding: 10px 15px;">
            </div>
        </div>
    </div>

</div>
<script src="<%=contextPath%>/Plugins/jquery/jquery.esi.1.0.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/themes/EsiTheme.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap-submenu.min.js"></script>

<script>
    (function () {
        var SYNTHETICAL = {};
        SYNTHETICAL.uuid = '${uuid}';
        SYNTHETICAL.page =${page};
        SYNTHETICAL.content = SYNTHETICAL.page.contents[1].contentType == 7 ? SYNTHETICAL.page.contents[1] : SYNTHETICAL.page.contents[0];
        SYNTHETICAL.pagecontent = SYNTHETICAL.page.contents[1].contentType == 8 ? SYNTHETICAL.page.contents[1] : SYNTHETICAL.page.contents[0];
        SYNTHETICAL.esi = new EsiTheme();
        var catalogue = SYNTHETICAL.content.contentValue;
//        第一级菜单的导航栏名字
        SYNTHETICAL.firstMenu = '';
//        第二级菜单的导航栏名字
        SYNTHETICAL.secMenu = '';
//        第二级以后菜单的导航栏名字
        SYNTHETICAL.childMenu = [];

//            加载初始页面
        SYNTHETICAL.initPage = function () {
            SYNTHETICAL.esi.getPageHtml(SYNTHETICAL.pagecontent.contentValue, function (data) {
                $('[data-id=syntheticalDiv2][data-uuid=' + SYNTHETICAL.uuid + ']').html(data);
                $('[data-id=menu][data-uuid=' + SYNTHETICAL.uuid + ']').html('');

            })

        }

        SYNTHETICAL.init = function () {

            this.initPage();

            $('[data-uuid=' + this.uuid + '][data-id=index]').click(function () {
                SYNTHETICAL.initPage();
            })

//            加载菜单
            this.esi.getSyntheticalMenu(catalogue, function (data) {
                if (data.success) {
                    menu(data.datas);
                    $('[data-submenu]').submenupicker();
                    SYNTHETICAL.showDetail();
                }
            });
            function menu(o) {
                if (o.length > 0) {
//                  左侧菜单
                    var menuNav = $('[data-id=syntheticalDiv11][data-uuid=' + SYNTHETICAL.uuid + ']');
                    var menuTab = $('[data-id=syntheticalDiv12][data-uuid=' + SYNTHETICAL.uuid + ']');
//                  左侧标签页 一级菜单
                    var tempMenuHtml = $('[data-id=syntheticalMenuTemp][data-uuid=' + SYNTHETICAL.uuid + ']').html();
//                  左侧标签页菜单内容
                    var tempContentHtml = $('[data-id=syntheticalContentTemp][data-uuid=' + SYNTHETICAL.uuid + ']').html();
//                  二级菜单
                    var tempDropdownHtml = $('[data-id=syntheticalDropdownTemp][data-uuid=' + SYNTHETICAL.uuid + ']').html();
//                    var tempDropMenuHtml = $('[data-id=syntheticalDropMenuTemp][data-uuid=' + SYNTHETICAL.uuid + ']').html();

                    var parent = $('[data-id=syntheticalParent][data-uuid=' + SYNTHETICAL.uuid + ']').html();
                    var child1 = $('[data-id=syntheticalChild1][data-uuid=' + SYNTHETICAL.uuid + ']').html();
                    var child2 = $('[data-id=syntheticalChild2][data-uuid=' + SYNTHETICAL.uuid + ']').html();
                    var dropMenuHtml = $('[data-id=syntheticalDropMenu][data-uuid=' + SYNTHETICAL.uuid + ']').html();

//                    第一级菜单的html
                    var firstMenuHtml = '';
//                    第二级菜单的container的html
                    var secondMenuContentHtml = '';
                    for (var i = 0; i < o.length; i++) {

//                        第一级菜单
                        var menu = o[i];
                        if (i == 0) {
                            SYNTHETICAL.firstMenu = ' > ' + menu.name;
                        }
                        firstMenuHtml += $.render(tempMenuHtml, menu);
//                        少一个</div>
                        secondMenuContentHtml += $.render(tempContentHtml, menu);

                        var child = menu.child;
//                        第二级菜单
                        if (child.length > 0) {
//                            第二级菜单的html
                            var secMemuHtml = '';
                            for (var j = 0; j < child.length; j++) {
                                var secM = child[j];

//                                少一个</div>
                                secMemuHtml += $.render(tempDropdownHtml, secM);

                                var secMChild = secM.child;

                                if (secMChild.length > 0) {

                                    var diguiHtml = '';

//                                    递归二级菜单之后的所有菜单
                                    diguiHtml = diguiCaidan(secMChild, diguiHtml, 0);

                                    secMemuHtml += diguiHtml;
                                    secMemuHtml += '</div>';

                                } else {
                                    secMemuHtml += "</div>";
                                }
                            }

                            secondMenuContentHtml += secMemuHtml;
                            secondMenuContentHtml += '</div>';

                        } else {
                            secondMenuContentHtml += '</div>';
                        }

                    }
                    menuNav.html(firstMenuHtml).children(':first-child').addClass('active');
                    menuTab.html(secondMenuContentHtml).children(':first-child').addClass('active');

                }

                /**
                 * 递归第二级菜单之后的所有菜单
                 * @param menus
                 * @param html
                 * @param index 菜单的层级，从第三级菜单开始，即第三级菜单层级是0
                 * @returns {*}
                 */
                function diguiCaidan(menus, html, index) {
                    var length = menus.length;
                    if (length > 0) {
//                        少了一个</ul>
                        html += dropMenuHtml;


                        for (var i = 0; i < length; i++) {
                            var n = menus[i];
                            n.index = index;
//                            是否为菜单
                            var isTitle = n.title;

                            if (isTitle) {
                                html += $.render(parent, n);
                            } else {

                                var nC = n.child;
//                                有子
                                if (nC.length > 0) {
                                    html += $.render(child2, n);

                                    html = diguiCaidan(nC, html, index + 1);
                                    html += '</li>';
                                } else {
                                    html += $.render(child1, n);
                                }

                            }

                        }
                        html += '</ul>';
                    }
                    return html;
                }
            }
        }

        SYNTHETICAL.showDetail = function () {

//            记录菜单导航
            $('[data-uuid=' + this.uuid + '][data-click=li1]').delegate('a', 'click', function (o) {
                var name = $(this).attr('data-name');
                SYNTHETICAL.firstMenu = ' > ' + name;
            });
//            记录菜单导航
            $('[data-uuid=' + this.uuid + '][data-click=li2]').delegate('div', 'click', function (o) {
                var name = $(this).attr('data-name');
                SYNTHETICAL.secMenu = ' > ' + name;
            });

            $('[data-uuid=' + this.uuid + '][data-click=li]').click(function (o) {
                var id = $(this).attr('data-id');

//                  记录菜单导航
                var index = $(this).data('index');
                var name = $(this).attr('data-name');
                SYNTHETICAL.childMenu[index] = name;
                SYNTHETICAL.childMenu.splice(index + 1, SYNTHETICAL.childMenu.length - index - 1);

                if (id) {

                    SYNTHETICAL.esi.getPageHtml(id, function (data) {
                        if (data.success == false)return;

//                        设置导航
                        var value = SYNTHETICAL.firstMenu + SYNTHETICAL.secMenu + ' > ' + SYNTHETICAL.childMenu.join(' > ');
                        $('[data-id=menu][data-uuid=' + SYNTHETICAL.uuid + ']').html(value);
                        SYNTHETICAL.childMenu = [];

//                  右侧内容
                        $('[data-id=syntheticalDiv2][data-uuid=' + SYNTHETICAL.uuid + ']').html(data);

                    })

                }
            })
        }

        $(function () {
            SYNTHETICAL.init();
        });
    })()
</script>