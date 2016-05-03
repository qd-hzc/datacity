<%--
  User: CRX
  Date: 2016/3/21
  content:6个框
--%>
<%
    String contextPath = request.getContextPath();
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<link href="<%=contextPath%>/Plugins/bootstrap/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
<style>
    .caption > div {
        height: 273px;
    }

    .input-group-addon {
        border: none;
        background-color: #00b7ee;
        width: 6.5rem;
        height: 9rem;
    }

    .input-group-addon:last-child {
        border-bottom-left-radius: 4px;
        border-top-left-radius: 4px;
    }

    .esi-thumbnail {
        border: 0px !important;
    }
</style>
<div style="float: left;width: 93%">
    <div class="row" style="margin-top:10px;">
        <div class=" col-md-4">
            <div class="thumbnail esi-thumbnail">
                <div class="caption">
                    <div data-uuid="${uuid}" data-id='paneldiv1'></div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="thumbnail esi-thumbnail">
                <div class="caption">
                    <div data-uuid="${uuid}" data-id="paneldiv2"></div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="thumbnail esi-thumbnail">
                <div class="caption">
                    <div data-uuid="${uuid}" data-id="paneldiv3"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class=" col-md-4">
            <div class="thumbnail esi-thumbnail">
                <div class="caption">
                    <div data-uuid="${uuid}" data-id="paneldiv4"></div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="thumbnail esi-thumbnail">
                <div class="caption">
                    <div data-uuid="${uuid}" data-id="paneldiv5"></div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="thumbnail esi-thumbnail">
                <div class="caption">
                    <div data-uuid="${uuid}" data-id="paneldiv6"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<div style="float: right;width: 7%;">
    <div data-uuid="${uuid}" data-id="form_datetime" style="margin-top: 12px;float: right">
    </div>
</div>

<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap-datetimepicker.js"></script>
<script src="<%=contextPath%>/Plugins/bootstrap/js/bootstrap-datetimepicker.zh-CN.js"></script>

<script>
    (function () {

        var contextPath = '<%=contextPath%>';

        var PANEL = {};
        //6个框的配置页
        PANEL.uuid = '${uuid}';
        PANEL.page = ${page};
        PANEL.contents = PANEL.page.contents;
        PANEL.esi = new EsiTheme();

        //    初始化方法
        PANEL.init = function () {
            if (PANEL.contents && PANEL.contents.length > 0) {
                $.each(PANEL.contents, function (i, n) {
                    PANEL.esi.getPageHtml(n.contentValue, function (data) {
                        if (data.success == false)return;
                        $('[data-uuid=' + PANEL.uuid + '][data-id=' + n.containerId + ']').html(data);

                    })
                });
            }
        }

        $(function () {
            PANEL.init();
            window.datetimepicker = $.datetimepickeresi(
                    $('[data-id=form_datetime][data-uuid=' + PANEL.uuid + ']'), new Date());

        });
    })()
</script>
