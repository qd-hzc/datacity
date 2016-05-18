/**
 * Created by wgx on 2016/3/1.
 */
Ext.define('Ext.importExcelWin',{
    extend : 'Ext.window.Window',
    height : 230,
    width : 480,
    modal : true
});
Ext.importExcelWin.init = function(rec,fn,isSheet) {
    var formPanel = Ext.create('Ext.form.Panel', {
        width: 480,
        height: 200,
        frame: true,
        items: [{
            xtype: 'filefield',
            name: 'excel',
            fieldLabel: 'Excel',
            labelWidth: 70,
            id: 'excel',
            anchor: '90%',
            labelAlign: 'right',
            buttonText: '选择Excel...',
            margin: '20 0 20 0'
        }],
        buttons: [{
            text: '导入',
            handler: function () {
                if (formPanel.isValid()) {
                    var path = new String(Ext.getCmp('excel').getValue());
                    var start = path.lastIndexOf('\\');
                    var fileName = path.substring(start + 1);
                    if (fileName === '') {
                        Ext.Msg.alert('提示', '请选择要导入的Excel文件！');
                    } else {
                        if (fileName.toLowerCase().match('.xls') || fileName.toLowerCase().match('.xlsx')) {
                            var url = GLOBAL_PATH + '/support/regime/collection/excel/importFromExcel';
                            var _rptInfoId = "";
                            if(isSheet){
                                url =  GLOBAL_PATH + '/support/regime/collection/excel/importFromSheet'
                                _rptInfoId = isSheet;
                            }
                            try {
                                formPanel.submit({
                                    url: url,
                                    params:{
                                        rptInfoId:_rptInfoId
                                    },
                                    timeout: 15 * 60 * 1000,
                                    method: 'POST',
                                    waitTitle: 'Excel导入',
                                    waitMsg: '数据处理中，请稍候...',
                                    success: function (form, action) {
                                        fn();
                                        Ext.Msg.alert('提示', action.result.msg);
                                    },
                                    failure: function (form, action) {
                                        Ext.Msg.alert('提示', action.result.msg);
                                    }
                                });
                            } catch (e) {
                                alert(e);
                            }
                        }
                        else {
                            Ext.Msg.alert('提示', '导入的文件必须是excel文件,请重新选择！');
                        }
                    }
                }
            }
        }]
    });
    var win = Ext.create('Ext.importExcelWin',{
        title: '导入Excel',
        frame : true,
        border : false,
        items : [formPanel]
    });
    win.show();
    return win;
}