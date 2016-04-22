<%@ page import="com.city.common.pojo.Constant" %><%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/15
  Time: 14:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<style>
    html, body {
        height: 100%;
        width: 100%;
        margin: 0;
        padding: 0;
    }
</style>
<script>
    //文字分析内容状态
    var TEXT_CONTENT_STATUS = {
        WAIT_CHECK: <%=Constant.TEXT_CONTENT_STATUS.WAIT_CHECK%>,
        WAIT_CHECK_STR: '待审核',
        CHECKED: <%=Constant.TEXT_CONTENT_STATUS.CHECKED%>,
        CHECKED_STR: '已审核',
        REJECT: <%=Constant.TEXT_CONTENT_STATUS.REJECT%>,
        REJECT_STR: '已驳回',
        getStr: function (status) {
            switch (status) {
                case this.WAIT_CHECK:
                    return this.WAIT_CHECK_STR;
                case this.CHECKED:
                    return this.CHECKED_STR;
                case this.REJECT:
                    return this.REJECT_STR;
                default:
                    return '';
            }
        },
        getArr: function () {
            return [{
                id: 0,
                name: '全部'
            }, {
                id: this.WAIT_CHECK,
                name: this.WAIT_CHECK_STR
            }, {
                id: this.CHECKED,
                name: this.CHECKED_STR
            }, {
                id: this.REJECT,
                name: this.REJECT_STR
            }];
        }
    };
    //分析数据类型
    var TEXT_DATA_TYPE = {
        RPT_SYNTHESIZE: <%=Constant.TEXT_DATA_TYPE.RPT_SYNTHESIZE%>,//综合表
        RPT_CUSTOM: <%=Constant.TEXT_DATA_TYPE.RPT_CUSTOM%>,//分析报表
        CHART: <%=Constant.TEXT_DATA_TYPE.CHART%>,//图表
        MAP: <%=Constant.TEXT_DATA_TYPE.MAP%>,//地图
        RPT_SYNTHESIZE_STR: '综合表',
        RPT_CUSTOM_STR: '分析表',
        CHART_STR: '图表',
        MAP_STR: '地图',
        getStr: function (type) {
            switch (type) {
                case this.RPT_SYNTHESIZE:
                    return this.RPT_SYNTHESIZE_STR;
                case this.RPT_CUSTOM:
                    return this.RPT_CUSTOM_STR;
                case this.CHART:
                    return this.CHART_STR;
                case this.MAP:
                    return this.MAP_STR;
                default:
                    return '';
            }
        },
        getArr: function () {
            return [{
                id: this.RPT_SYNTHESIZE,
                name: this.RPT_SYNTHESIZE_STR
            }, {
                id: this.RPT_CUSTOM,
                name: this.RPT_CUSTOM_STR
            }, {
                id: this.CHART,
                name: this.CHART_STR
            }, {
                id: this.MAP,
                name: this.MAP_STR
            }];
        }
    };
    //文字分析内容类型
    var TEXT_CONTENT_TYPE = {
        COMMON: <%=Constant.TEXT_CONTENT_TYPE.COMMON%>,
        COMMON_CH: '<%=Constant.TEXT_CONTENT_TYPE.COMMON_CH%>',
        getStr: function (status) {
            switch (status) {
                case this.COMMON:
                    return this.COMMON_CH;
                default:
                    return '';
            }
        }
    };
    //文字分析类型
    var TEXT_TYPE = {
        THEME: <%=Constant.TEXT_TYPE.THEME%>,
        CONTENT: <%=Constant.TEXT_TYPE.CONTENT%>
    };
    //路径
    var TEXT_CONTEXT_PATH = '<%=request.getContextPath()%>/support/resourceCategory/analysis/text';
</script>
