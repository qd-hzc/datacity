<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.city.common.pojo.Constant" %>
<%@ page import="com.google.gson.Gson" %>
<%--
  User: HZC
  Date: 2016/5/13
  搜索数据资源，包括：图表分析，报表分析，文字分析，综合表
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    long time = new Date().getTime();
    Gson gson = new Gson();
    List<Map<String, Object>> yearFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.YEAR);
    String yg = gson.toJson(yearFres);

    List<Map<String, Object>> halfFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.HALF);
    String hg = gson.toJson(halfFres);

    List<Map<String, Object>> quarterFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.QUARTER);
    String qg = gson.toJson(quarterFres);

    List<Map<String, Object>> monthFres = Constant.FrequencyType.getAllForArray(Constant.PeriodType.MONTH);
    String mg = gson.toJson(monthFres);

    //报送周期
    List<Map<String, Object>> periods = Constant.PeriodType.getAllForArray();
    String pg = gson.toJson(periods);
%>
<html>
<head>
    <title>搜索资源</title>
    <meta charset="UTF-8"/>
    <style>
        .search-menu-icon {
            background-image: url(<%=contextPath%>/City/resourceCategory/analysis/common/img/icons.png);
            background-repeat: no-repeat;
            width: 20px;
            height: 20px;
            display: inline-block;
            margin-bottom: -4px;
            margin-right: 5px;
        }

        .search-all {
            background-position: -11px 0px;
        }

        .search-report {
            background-position: -31px 0px;
        }

        .search-research {
            background-position: -51px 0px;
        }

        .search-chart {
            background-position: -71px 0px;
        }

        .search-text {
            background-position: -91px 0px;
        }
    </style>
</head>
<body>
<script>

    Ext.onReady(function () {

        var searchText = '${text}';
        var divId = '<%=time%>';
        var contextPath = '<%=contextPath%>';
        var yearFres = <%=yg%>;
        var halfFres = <%=hg%>;
        var quarterFres = <%=qg%>;
        var monthFres = <%=mg%>;
        var periods = <%=pg%>;

        var commonParams = {
            text: searchText,
            type: 1
        };

//        资源类store
        var muluStore = new Ext.data.Store({
            fields: ['name', 'id'],
            data: [{name: '全部', id: 1},
                {name: '综合表', id: 2},
                {name: '报表分析', id: 3},
                {name: '图表分析', id: 4},
                {name: '文字分析', id: 5}]
        });
//        资源类表格
        var muluPanel = new Ext.grid.Panel({
            width: '20%',
            region: 'west',
            store: muluStore,
            hiddenHeaders: true,
            columns: [
                {
                    dataIndex: 'name',
                    flex: 1,
                    renderer: function (data, m, record) {
                        var id = record.get('id');
                        var value = record.get('name');
                        var html = '';
                        html = '<div style="padding-left:15%;width: 100%">';
                        switch (id) {
                            case 1:
                                html += '<div class="search-all search-menu-icon" >';
                                break;
                            case 2:
                                html += '<div class="search-report search-menu-icon" >';
                                break;
                            case 3:
                                html += '<div class="search-research search-menu-icon" >';
                                break;
                            case 4:
                                html += '<div class="search-chart search-menu-icon" >';
                                break;
                            case 5:
                                html += '<div class="search-text search-menu-icon" >';
                                break;

                        }
                        html += '</div><div style="display: inline-block;">' + value + '</div></div>';
                        return html;
                    }
                }
            ],
            listeners: {
                itemclick: function (_this, record, item) {
                    commonParams.type = record.get('id');
                    neirongStore.loadPage(1, {params: commonParams});
                }
            }
        });
//        资源内容store
        var neirongStore = new Ext.data.Store({
            fields: ['name', 'id'],
            pageSize: 10,
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/resourcecategory/analysis/common/analysis/queryResource'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'datas'
                }
            },
            autoLoad: true
        });
        neirongStore.on('beforeload', function (s) {
            s.getProxy().extraParams = commonParams;
        });
//        资源内容表格
        var neirongPanel = new Ext.grid.Panel({
            width: '80%',
            region: 'west',
            store: neirongStore,
            hiddenHeaders: true,
            columns: [
                {
                    flex: 1,
                    renderer: function (data, m, record) {
                        var html = '<div style="margin:10px 20px;font-size:1rem;color:#2F62A4;">';
                        var type = record.get('type');
                        var comments = record.get('comments');
                        comments = comments ? comments : '';
                        var extraName = record.get('extraName');
                        extraName = extraName ? extraName : '';

                        switch (type) {
                            case 2://综合表
                                html += '<div class="search-report search-menu-icon"></div><div style="display: inline-block;">' + record.get('name') + '</div></div>';
                                var period = record.get('period');
                                var pString = '';
                                for (var i = 0; i < periods.length; i++) {
                                    if (periods[i].value == period) {
                                        pString = periods[i].text;
                                    }
                                }
                                html += '<div style="margin:10px 45px;"><span>周期：' + pString + '</span><span style="margin-left:40px;">所属部门：' + record.get('departmentName') + '</span></div>';
                                break;
                            case 3://分析报表
                                html += '<div class="search-research search-menu-icon"></div><div style="display: inline-block;">' + record.get('name') + '</div></div>';
                                html += '<div style="margin:10px 45px;"><span>分组：' + extraName + '</span><span style="margin-left:40px;">描述：' + comments + '</span></div>';
                                break;
                            case 4://分析图表
                                html += '<div class="search-chart search-menu-icon"></div><div style="display: inline-block;">' + record.get('name') + '</div></div>';
                                html += '<div style="margin:10px 45px;"><span>分组：' + extraName + '</span><span style="margin-left:40px;">描述：' + comments + '</span></div>';
                                break;
                            case 5://分析文字
                                html += '<div class="search-text search-menu-icon"></div><div style="display: inline-block;">' + record.get('name') + '</div></div>';
                                html += '<div style="margin:10px 45px;"><span>主题：' + extraName + '</span><span style="margin-left:40px;">描述：' + comments + '</span></div>';
                                break;
                        }
                        return html;
                    }
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                store: neirongStore,
                displayInfo: true
            },
            listeners: {
                itemclick: function (_this, record, item, index, e, epts) {
                    var type = record.get('type');
                    var id = record.get('id');
                    alertDetail(type, id);
                }
            }
        });

//        显示详细
        function alertDetail(type, id) {
            switch (type) {
                case 2://综合表
                    Ext.showReportStyleWin.init(id, yearFres, halfFres, quarterFres, monthFres);
                    break;
                case 3://分析报表
                    open(contextPath + '/resourcecategory/analysis/report/designCustomResearch/showDesignResearch?_cr=' + id);
                    break;
                case 4://分析图表
                    open(contextPath + "/support/resourcecategory/analysis/chart/chartDesign?chartId=" + id);
                    break;
                case 5://文字分析
                    Ext.Ajax.request({
                        url: GLOBAL_PATH + "/support/resourceCategory/analysis/text/queryContentById",
                        method: 'POST',
                        params: {
                            contentId: id
                        },
                        success: function (response, opts) {
                            var result = Ext.JSON.decode(response.responseText);
                            if (result) {
                                Ext.showTextWin.init(result);
                            } else {
                                Ext.Msg.alert('提示', '无内容');
                            }
                        }
                    });
                    break;
            }
        }


        Ext.create('Ext.panel.Panel', {
            renderTo: divId,
            layout: 'border',
            height: '100%',
            border: false,
            frame: false,
            width: '100%',
            items: [muluPanel, neirongPanel],
            tbar: [{
                xtype: 'textfield',
                labelWidth: 50,
                width: 150,
                labelAlign: 'right',
                value: searchText,
                listeners: {
                    change: function (_this, n, o) {
                        commonParams.text = n;
                    }
                }
            }, {
                xtype: 'button',
                text: '搜索',
                iconCls: 'Magnifier',
                listeners: {
                    click: function () {
                        neirongStore.loadPage(1, {params: commonParams});
                    }
                }
            }],
            listeners: {
                render: function () {
                    if (indexPanel) {
                        var tabPanel = indexPanel.down('#tabCenter');
                        var myTab = tabPanel.getActiveTab();
                        if (myTab) {
                            myTab.myPanel = this;
                        }
                        if (this.hasListener('reDR')) {
                            this.un('reDR');
                        }
                        this.on('reDR', function (obj) {
                            if (obj) {
                                obj.height = obj.height - 40;
                                this.updateBox(obj);
                            }
                        });
                    }
                },
                myUpdateBox: function (obj) {
                    this.updateBox(obj);
                }
            }
        });
    });
</script>
<div id="<%=time%>" style="height: 100%;width:100%;"></div>
<script src="<%=contextPath%>/City/resourceCategory/analysis/common/showTextWin.js"></script>
<script src="<%=contextPath%>/City/resourceCategory/analysis/common/showReportStyle.js"></script>
</body>
</html>
