<%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/4/20
  Time: 15:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>预览内容</title>
    <meta charset="UTF-8"/>
    <jsp:include page="/WEB-INF/pages/common/appDataDictImp.jsp"/>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"/>
    <jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/City/app/dataDict/css/style.css">
    <style>
        thead {
            display: none;
        }

        table {
            white-space: nowrap;
        }

        table td {
            text-align: center;
        }

        .btn-group {
            z-index: 999;
        }

        .data_content.table {
            text-align: center;
        }

        .btn-group {
            z-index: 999;
        }

        .esitablediv {
            position: relative;
            display: inline-block;
            width: 100%;
        }

        .esimaintablediv {
            position: relative;
            display: inline-block;
            width: 100%;
            overflow-x: auto;
        }

        .esiclonetablediv {
            position: absolute;
            top: 0px;
            left: 0px;
            height: 100%;
        }

        .esimaintable {
            position: relative;
        }

        .esiclonetable {
            background: #FFFFFF;

        }
    </style>
</head>
<body>
<div id="header" class="clearfix">
    <h1 id="content_title">${name}</h1>
</div>
<div id="main" class="article-page">
    <div class="concern-main">
    </div>
</div>
<script src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
<script>
    //类型对应的div的css名称
    var DATA_DIV_CSS_NAME = {
        TABLE: "table",
        CHART: "charts",
        TEXT: "text",
        getCssName: function (type) {
            var cssName = "";

            switch (type) {
                case DATA_DICT_TYPE.RPT_SYNTHESIZE:
                case DATA_DICT_TYPE.RPT_CUSTOM:
                    cssName = DATA_DIV_CSS_NAME.TABLE;
                    break;
                case DATA_DICT_TYPE.CHART:
                    cssName = DATA_DIV_CSS_NAME.CHART;
                    break;
                case DATA_DICT_TYPE.TEXT_THEME:
                case DATA_DICT_TYPE.TEXT_DESC:
                    cssName = DATA_DIV_CSS_NAME.TEXT;
                    break;
            }

            return cssName;
        }
    };
    var GLOBAL_PATH = '<%=request.getContextPath()%>';
    $(function () {
        var data =${infos};
        if (data) {
            var mainDivDom = $("#main");//内容div
            //添加节点
            var contentStr = ''; //内容
            var navStr = '<div class="concern-date"><ul id="nav">'; //导航  锚点
            for (var group in data) {
                //添加导航列表
                navStr += '<li><a href="#' + group + '">' + group + '</a></li>';
                //添加内容
                contentStr += '<div id="' + group + '"><h2 class="title">' + group + '</h2>';
                var contents = data[group];
                if (contents.length > 1) { //多个
                    contentStr += '<div class="btn-group">';
                    for (var i = 0; i < contents.length; i++) {
                        contentStr += '<span data-index="' + i + '">' + contents[i].dataName + '</span>';
                    }
                    contentStr += '</div>';
                }
                for (var i = 0; i < contents.length; i++) {
                    var content = contents[i];
                    var dataType = content.dataType;
                    var dataValue = content.dataValue;
                    contentStr += '<div data-index="' + i + '" data-type="' + dataType + '" data-value="' + dataValue + '" class="data_content ' + DATA_DIV_CSS_NAME.getCssName(dataType) + '"></div>';
                }
                contentStr += '</div>';
            }
            navStr += '</ul></div>';
            //添加锚点
            mainDivDom.addClass('concern');
            mainDivDom.append(navStr);
            //添加列表并定位
            $('#nav').html(navStr);
            $('#nav li:first').addClass('on');
            //添加内容
            mainDivDom.append('<div class="concern-main">' + contentStr + '</div>');
            $('.data_content').each(function () {
                var _this = $(this);
                var index = _this.attr('data-index');
                var dataType = parseInt(_this.attr('data-type'));
                var dataValue = _this.attr('data-value');
                new EsiData(dataType, dataValue).init(_this.get(0), function () {
                    if (dataType == DATA_DICT_TYPE.RPT_SYNTHESIZE || dataType == DATA_DICT_TYPE.RPT_CUSTOM) {
                        var mainTable = _this.find('table');
                        var cloneTable = mainTable.clone();
                        mainTable.addClass('esimaintable');
                        cloneTable.find('td[esi-type=data]').remove();
                        cloneTable.find('td[esi-type=second]').remove();
                        cloneTable.find('tbody td:first').height(mainTable.find('tbody td:first').height());
                        cloneTable.addClass('esiclonetable');
                        if (mainTable.width() < _this.width()) {
                            cloneTable.hide();
                            mainTable.css('width', '100%');
                        }
                        _this.find('.esiclonetablediv').append(cloneTable);
                    }
                    if (index != 0) {
                        _this.hide();
                    }
                });
            });
            $('.btn-group span').eq(0).addClass('on');
        }
        //导航切换
        $(document).on('click', '.concern-date li', function () {
            $(this).addClass('on').siblings().removeClass('on');
        });
        //tab切换
        $(document).on('click', '.btn-group span', function() {
            var _this = $(this);
            var container = _this.parent().parent();
            var index = _this.attr('data-index');
            container.find('.data_content[data-index="' + index + '"]').show().siblings('.data_content').hide();
            _this.addClass('on').siblings().removeClass('on');
        });
    });
</script>
<script src="<%=request.getContextPath()%>/Plugins/eharts/echarts.min.js"></script>
<script src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/echarts/EsiChart.js"></script>
<script src="<%=request.getContextPath()%>/City/app/dataDict/js/EsiData.js"></script>
</body>
</html>
