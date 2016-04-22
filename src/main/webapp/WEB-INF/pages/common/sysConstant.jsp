<%@ page import="com.city.common.pojo.Constant" %>
<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2015/12/30
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
    var GLOBAL_PATH = '<%=request.getContextPath()%>';
    var INDEX_PAGE = '/app/dataDict/manageJsp';//配置页面首页
    var DEFAULT_CONFIGPAGE = '/support/sys/index';//默认配置页面
    var MODULE_TYPE = {
        ROOT: 0,//根
        SYSMOD: 1,//系统
        MODMOD: 2,//模块
        FUNMOD: 3,//功能
        OPMOD: 4,//操作
        DIRMOD: 5//目录

    };
    var ROLE_TYPE = {
        RPT_ROLE: 1,//报表角色
        MODULE_ROLE: 2//模块角色
    };
    var USER_TYPE = {
        ADMIN: 1//系统管理员
    };

    var ANALYSISCHART_TYPE = {
        TYPE_STATIC: <%=Constant.ANALYSISCHART_TYPE.TYPE_STATIC%>,
        TYPE_DYMIC:<%=Constant.ANALYSISCHART_TYPE.TYPE_DYMIC%>,
        TYPE_STATIC_CH: '<%=Constant.ANALYSISCHART_TYPE.TYPE_STATIC_CH%>',
        TYPE_DYMIC_CH: '<%=Constant.ANALYSISCHART_TYPE.TYPE_DYMIC_CH%>',
        getCH: function (type) {
            switch (type) {
                case this.TYPE_STATIC:
                    return this.TYPE_STATIC_CH;
                case this.TYPE_DYMIC :
                    return this.TYPE_DYMIC_CH;
            }
            return '';

        }
    };
    var ANALYSISCHART_INFO = {
        SERIES_HIDE: 0,
        SERIES_SHOW: 1,

        REALNODE: <%=Constant.ANALYSISCHART_INFO.REALNODE%>,
        VIRTUALNODE:<%=Constant.ANALYSISCHART_INFO.VIRTUALNODE%>,

        TYPE_CATEGORY: <%=Constant.ANALYSISCHART_INFO.TYPE_CATEGORY%>,
        TYPE_SERIES:<%=Constant.ANALYSISCHART_INFO.TYPE_SERIES%>,
        TYPE_CATEGORY_CH: '<%=Constant.ANALYSISCHART_INFO.TYPE_CATEGORY_CH%>',
        TYPE_SERIES_CH: '<%=Constant.ANALYSISCHART_INFO.TYPE_SERIES_CH%>',
        //左轴
        LEFTAXIS: <%=Constant.ANALYSISCHART_INFO.LEFTAXIS%>,
        //右轴
        RIGHTAXIS: <%=Constant.ANALYSISCHART_INFO.RIGHTAXIS%>,
        //折线图
        CHART_LINE: <%=Constant.ANALYSISCHART_INFO.CHART_LINE%>,
        //曲线图
        CHART_CURVE: <%=Constant.ANALYSISCHART_INFO.CHART_CURVE%>,
        //柱状图
        CHART_COLUMN: <%=Constant.ANALYSISCHART_INFO.CHART_COLUMN%>,
        //饼图
        CHART_PIE: <%=Constant.ANALYSISCHART_INFO.CHART_PIE%>,
        //散点图
        CHART_SCATTER: <%=Constant.ANALYSISCHART_INFO.CHART_SCATTER%>,
        //地图
        CHART_MAP: <%=Constant.ANALYSISCHART_INFO.CHART_MAP%>,

        SERIES_HIDE_CH: '<%=Constant.ANALYSISCHART_INFO.SERIES_HIDE_CH%>',
        SERIES_SHOW_CH: '<%=Constant.ANALYSISCHART_INFO.SERIES_SHOW_CH%>',
        REALNODE_CH: '<%=Constant.ANALYSISCHART_INFO.REALNODE_CH%>',
        VIRTUALNODE_CH: '<%=Constant.ANALYSISCHART_INFO.VIRTUALNODE_CH%>',

        LEFTAXIS_CH: '<%=Constant.ANALYSISCHART_INFO.LEFTAXIS_CH%>',
        RIGHTAXIS_CH: '<%=Constant.ANALYSISCHART_INFO.RIGHTAXIS_CH%>',
        //折线图
        CHART_LINE_CH: '<%=Constant.ANALYSISCHART_INFO.CHART_LINE_CH%>',
        //曲线图
        CHART_CURVE_CH: '<%=Constant.ANALYSISCHART_INFO.CHART_CURVE_CH%>',
        //柱状图
        CHART_COLUMN_CH: '<%=Constant.ANALYSISCHART_INFO.CHART_COLUMN_CH%>',
        //饼图
        CHART_PIE_CH: '<%=Constant.ANALYSISCHART_INFO.CHART_PIE_CH%>',
        //散点图
        CHART_SCATTER_CH: '<%=Constant.ANALYSISCHART_INFO.CHART_SCATTER_CH%>',
        //地图
        CHART_MAP_CH: '<%=Constant.ANALYSISCHART_INFO.CHART_MAP_CH%>',

        CHART_LINE_EN: '<%=Constant.ANALYSISCHART_INFO.CHART_LINE_EN%>',//折线图
        CHART_CURVE_EN: '<%=Constant.ANALYSISCHART_INFO.CHART_CURVE_EN%>',//曲线图
        CHART_COLUMN_EN: '<%=Constant.ANALYSISCHART_INFO.CHART_COLUMN_EN%>',//柱状图
        CHART_PIE_EN: '<%=Constant.ANALYSISCHART_INFO.CHART_PIE_EN%>',//饼图
        CHART_SCATTER_EN: '<%=Constant.ANALYSISCHART_INFO.CHART_SCATTER_EN%>',//散点图
        CHART_MAP_EN: '<%=Constant.ANALYSISCHART_INFO.CHART_MAP_EN%>',//地图
        getSeriesShowCH: function (type) {
            switch (type) {
                case this.SERIES_HIDE:
                    return this.SERIES_HIDE_CH;
                case this.SERIES_SHOW :
                    return this.SERIES_SHOW_CH;
            }
            return '';
        },

        getChartInfoTypeCH: function (type) {
            switch (type) {
                case this.TYPE_CATEGORY:
                    return this.TYPE_CATEGORY_CH;
                case this.TYPE_SERIES :
                    return this.TYPE_SERIES_CH;
            }
            return '';

        },
        getChartAxisTypeCH: function (type) {
            switch (type) {
                case this.LEFTAXIS:
                    return this.LEFTAXIS_CH;
                case this.RIGHTAXIS :
                    return this.RIGHTAXIS_CH;
            }
            return '';

        },
        getChartTypeCH: function (type) {
            switch (type) {
                case this.CHART_LINE:
                    return this.CHART_LINE_CH;
                case this.CHART_CURVE:
                    return this.CHART_CURVE_CH;
                case this.CHART_COLUMN:
                    return this.CHART_COLUMN_CH;
                case this.CHART_PIE:
                    return this.CHART_PIE_CH;
                case this.CHART_SCATTER :
                    return this.CHART_SCATTER_CH;
                case this.CHART_MAP :
                    return this.CHART_MAP_CH;
            }
            return '';
        },
        getEN: function (type, plug) {
            switch (type) {
                case 0:
                    return this.CHART_LINE_EN;
                case 1:
                    switch (plug) {
                        case 'highcharts':
                            return this.CHART_CURVE_EN;
                            break;
                        default://默认echarts
                            return this.CHART_LINE_EN;
                    }
                case 2:
                    switch (plug) {
                        case 'highcharts':
                            return 'column';//this.CHART_COLUMN_EN;
                            break;
                        default://默认echarts
                            return this.CHART_COLUMN_EN;
                    }
                case 3:
                    return this.CHART_PIE_EN;
                case 4:
                    return this.CHART_SCATTER_EN;
                case 5:
                    return this.CHART_MAP_EN;
                default :
                    return this.CHART_LINE_EN;
            }
        }
    }
    var PERIOD_TYPE = {
        YEAR: <%=Constant.PeriodType.YEAR %>,
        /**
         * 半年报
         */
        HALF: <%=Constant.PeriodType.HALF %>,
        /**
         * 季报
         */
        QUARTER: <%=Constant.PeriodType.QUARTER %>,
        /**
         * 月报
         */
        MONTH: <%=Constant.PeriodType.MONTH %>,
        YEAR_CH: '<%=Constant.PeriodType.YEAR_CH %>',
        HALF_CH: '<%=Constant.PeriodType.HALF_CH %>',
        QUARTER_CH: '<%=Constant.PeriodType.QUARTER_CH %>',
        MONTH_CH: '<%=Constant.PeriodType.MONTH_CH %>',
        getCH: function (type) {
            switch (type) {
                case this.YEAR :
                    return this.YEAR_CH;
                case this.HALF:
                    return this.HALF_CH;
                case this.QUARTER :
                    return this.QUARTER_CH;
                case this.MONTH:
                    return this.MONTH_CH;
            }
            return '';
        }
    }

    var SUROBJ_TYPE = {
        TYPE_AREA: <%=Constant.SurObjType.AREA%>,
        TYPE_AREA_CH: '<%=Constant.SurObjType.AREA_CH%>',
        TYPE_OTHER: <%=Constant.SurObjType.OTHER%>,
        TYPE_OTHER_CH: '<%=Constant.SurObjType.OTHER_CH%>',
        TYPE_COMP: <%=Constant.SurObjType.COMPANY%>,
        TYPE_COMP_CH: '<%=Constant.SurObjType.COMPANY_CH%>',
        getCH: function (type) {
            switch (type) {
                case this.TYPE_AREA:
                    return SUROBJ_TYPE.TYPE_AREA_CH;
                case this.TYPE_OTHER :
                    return SUROBJ_TYPE.TYPE_OTHER_CH;
                case this.TYPE_COMP_CH :
                    return SUROBJ_TYPE.TYPE_COMP_CH;
            }
            return '';
        }
    }
</script>
