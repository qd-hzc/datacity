<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2016/4/8
  Time: 11:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试重点</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/Plugins/bootstrap/css/bootstrap-theme.css"/>
    <style>
        html, body {
            height: 100%;
        }

        .esi-data-panel table {
            width: 100%;
        }

        .esi-data-panel .panel-footer td {
            text-align: center;
        }

    </style>
    <jsp:include page="/WEB-INF/pages/common/imp.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/bootstrap/js/bootstrap.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/Plugins/eharts/echarts-all.js"></script>
    <script src="<%=request.getContextPath()%>/Plugins/vue/vue.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/themes/EsiTheme.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/echarts/EsiChart.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/echarts/VueChart.js"></script>

</head>
<body style="padding-top: 5px;">
<div class="container-fluid" id="cards">
    <child-component :item="y" v-ref:qqq>
    </child-component>
    <div class="row">
        <div class="col-md-2 col-md-offset-10" style="text-align: right"><!-- Single button -->
            <div class="btn-group">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    2014年2月<span class="glyphicon glyphicon-calendar" aria-hidden="true"></span>
                </button>
                <ul class="dropdown-menu dropdown-menu-right">
                    <li><a href="#">Action</a></li>
                    <li><a href="#">Another action</a></li>
                    <li><a href="#">Something else here</a></li>
                    <li class="divider"></li>
                    <li><a href="#">Separated link</a></li>
                </ul>
            </div>
        </div>
    </div>
    <esi-chart :esi-id="'echart-3419'" width="100%" height="260px"
               :esi-chart-id="3419" :esi-chartoption="esiChartoption" v-ref:profile></esi-chart>
    <div class="row" style="margin-top: 5px" v-for="row in cardRows">

        <div class="col-md-4" v-for="(index,card ) in row">
            <div class="panel panel-info esi-data-panel">
                <div class="panel-heading">
                    <table>
                        <tr>
                            <td><strong>{{card.name}}</strong></td>
                            <td style="text-align: right">
                                <small>2014年2月</small>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="panel-body">
                    <esi-chart :esi-id="'echart-'+getChartId(card.contents)+index" width="100%" height="260px"
                               :esi-chart-id="getChartId(card.contents)" :esi-chartoption="esiChartoption" :esi-charttime="esiCharttime" v-ref:profile></esi-chart>
                </div>
                <div class="panel-footer">
                    <table>
                        <tr>
                            <td>36万元</td>
                            <td>20%</td>
                            <td>详情</td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var esiPage = ${page};
    $(function () {
        var GLOBAL_PATH = '<%=request.getContextPath()%>';
        var theme = new EsiTheme();
        if (esiPage && esiPage.contents && esiPage.contents.length > 0) {
            theme.getData(esiPage.contents[0], function (result) {
                var datas = null, cards = [], row = 0, rowCard = null, vueData = {};
                if (result.success) {
                    datas = result.datas;
                    //构造三个一行数据结构
                    for (var i = 0; i < datas.length; i++) {
                        if (i % 3 == 0) {
                            rowCard = [];
                            cards[row] = rowCard;
                            rowCard.push(datas[i]);
                            row++;
                        } else {
                            rowCard.push(datas[i]);
                        }
                    }
                    vueData.cardRows = cards;
                    vueData.esiChartoption={jj:999};
                    var child = Vue.extend({
                        template: '<div ttt="{{item}}"><h1>{{item}}</h1><div>{{y}}</div></div>',
                        props: ['item'],
                        data: function() {
                            return {
                                //item: '777'
                            }
                        }
                    });
                    Vue.component('child-component',child);
                    var navV = new Vue({
                        el: '#cards',
                        data: vueData,
                        methods: {
                            getChartId: function (content) {
                                var result = '';
                                if (content) {
                                    for (var i = 0; i < content.length; i++) {
                                        if (content[i].contentType == 3) {
                                            this['echart-'+content[i].contentValue+'-option'] = {aa:6}
                                            result = content[i].contentValue;
                                        }
                                    }
                                }
                                return result;
                            }
                        }
                    });
                    var child = navV.$refs.qqq;
                    console.log(navV)

                }
            });
        }
    })

</script>

</body>
</html>
