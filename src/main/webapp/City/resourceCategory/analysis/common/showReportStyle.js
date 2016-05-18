/**
 *
 * Created by Paul on 2016/5/17.
 */
createModel('Ext.showReportStyleWin', function () {
    Ext.define('Ext.showReportStyleWin', {
        extend: 'Ext.window.Window',
        width: 800,
        closeAction: 'destroy',
        modal: true
    });
});
Ext.showReportStyleWin.init = function (id, yearFres, halfFres, quarterFres, monthFres) {

    //表样表格
    var styleStore = new Ext.data.Store({
        fields: ['id', 'name', 'reportTemplate', 'beginYear', 'endYear', 'beginPeriod', 'endPeriod', 'rptStyle'],
        proxy: {
            type: 'ajax',
            url: contextPath + '/support/regime/report/getRptStyleByTmp',
            extraParams: {tmpId: id}
        },
        autoLoad: true
    });

    var styleGrid = new Ext.grid.Panel({
        width: '100%',
        flex: 2,
        store: styleStore,
        scrollable: true,
        columns: [{
            text: '表样名',
            dataIndex: 'name',
            flex: 1
        }, {
            text: '有效期',
            flex: 1,
            renderer: function (data, m, record) {
                var period = record.get('reportTemplate').period;
                var beginYear = record.get('beginYear');
                var endYear = record.get('endYear');
                var beginPeriod = record.get('beginPeriod');
                var endPeriod = record.get('endPeriod');
                return getValidTime(period, beginYear, beginPeriod, endYear, endPeriod);
            }
        }, {
            text: '表样设计',
            flex: 0.5,
            align: 'center',
            renderer: function () {
                return '<a style="color:#0000FF">表样设计</a>';
            }
        }],
        listeners: {
            cellclick: function (_this, td, cellIndex, record) {
                if (cellIndex == 2) {//弹出设计窗口
                    open(contextPath + '/support/regime/report/designReport/showReportDesign?styleId=' + record.get('id'));
                }
            }
        }
    });
//有效期
    function getValidTime(period, beginYear, beginPeriod, endYear, endPeriod) {
        var arr = [];
        if (period == 1) {
            arr = [];
        } else if (period == 2) {//半年
            arr = halfFres;
        } else if (period == 3) {//季度
            arr = quarterFres;
        } else {//月
            arr = monthFres;
        }
        //迭代,获取中文
        var beginStr = '';
        var endStr = '';
        for (var i = 0; i < arr.length; i++) {
            if (beginPeriod == arr[i].value) {
                beginStr = arr[i].text;
            }
            if (endPeriod == arr[i].value) {
                endStr = arr[i].text;
            }
        }
        if (endYear == 0) {//一直有效
            return '从<b style="color:red">' + beginYear + '年' + beginStr + '</b>起一直有效';
        }
        return '从<b style="color:red">' + beginYear + '年' + beginStr + '</b>到<b style="color:red">' + endYear + '年' + endStr + '</b>';
    }

    var win = new Ext.window.Window({
        title: '查看',
        width: 800,
        height: 600,
        modal: true,
        items: [styleGrid],
        buttons: [{
            text: '关闭',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
};
