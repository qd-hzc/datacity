<%@ page import="com.city.common.pojo.AppConstant" %><%--
  Created by IntelliJ IDEA.
  User: wxl
  Date: 2016/3/23
  Time: 14:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<script>
    //数据字典类型
    var DATA_DICT_TYPE = {
        RPT_SYNTHESIZE: <%=AppConstant.DATA_DICT_TYPE.RPT_SYNTHESIZE%>,//综合表
        RPT_CUSTOM: <%=AppConstant.DATA_DICT_TYPE.RPT_CUSTOM%>,//分析报表
        CHART: <%=AppConstant.DATA_DICT_TYPE.CHART%>,//分析图表
        TEXT_THEME: <%=AppConstant.DATA_DICT_TYPE.TEXT_THEME%>,//分析主题
        TEXT_DESC: <%=AppConstant.DATA_DICT_TYPE.TEXT_DESC%>,//文字分析
        DATA_SET: <%=AppConstant.DATA_DICT_TYPE.DATA_SET%>,//数据集

        RPT_SYNTHESIZE_STR: '综合表',//综合表
        RPT_CUSTOM_STR: '分析报表',//分析报表
        CHART_STR: '分析图表',//分析图表
        TEXT_THEME_STR: '分析主题',//分析主题
        TEXT_DESC_STR: '文字分析',//文字分析
        DATA_SET_STR: '数据集',//数据集
        getStr: function (type) {
            switch (type) {
                case this.RPT_SYNTHESIZE:
                    return this.RPT_SYNTHESIZE_STR;
                case this.RPT_CUSTOM:
                    return this.RPT_CUSTOM_STR;
                case this.CHART:
                    return this.CHART_STR;
                case this.TEXT_THEME:
                    return this.TEXT_THEME_STR;
                case this.TEXT_DESC:
                    return this.TEXT_DESC_STR;
                case this.DATA_SET:
                    return this.DATA_SET_STR;
                default:
                    return '';

            }
        },
        getArr: function () {
            return [
                {id: this.RPT_SYNTHESIZE, name: this.RPT_SYNTHESIZE_STR},
                {id: this.RPT_CUSTOM, name: this.RPT_CUSTOM_STR},
                {id: this.CHART, name: this.CHART_STR},
                {id: this.TEXT_THEME, name: this.TEXT_THEME_STR},
                {id: this.TEXT_DESC, name: this.TEXT_DESC_STR},
                {id: this.DATA_SET, name: this.DATA_SET_STR}
            ];
        }
    };
    //数据字典 展示类型
    var DATA_DICT_DISPLAY_TYPE = {
        CHART: <%=AppConstant.DATA_DICT_DISPLAY_TYPE.CHART%>,//图表展示
        CHART_STR: '图表展示',
        getStr: function (type) {
            switch (type) {
                case this.CHART:
                    return this.CHART_STR;
                default:
                    return '';
            }
        },
        getArr: function () {
            return [
                {id: this.CHART, name: this.CHART_STR}
            ];
        }
    };
</script>
