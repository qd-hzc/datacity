/**
 * Created by wys on 2016/1/20.
 */
createModel('Ext.fillSurObjGroupWin', function () {
    Ext.define('Ext.fillSurObjGroupWin', {
        extend: 'Ext.window.Window',
        width: 600,
        closeAction:'destroy',
        modal: true
    });
});
Ext.fillSurObjGroupWin.init = function (rec, fnt) {
    var isAdd = (rec? false:true);
    var title = (isAdd? "添加分组":"修改分组");
    var surObjGroupTypeStore = new Ext.data.Store({
        fields:['typeName','typeValue'],
        data:[
            {typeName:'地区统计对象',typeValue:SUROBJ_TYPE.TYPE_AREA},
            {typeName:'名录统计对象',typeValue:SUROBJ_TYPE.TYPE_COMP},
            {typeName:'其他统计对象',typeValue:SUROBJ_TYPE.TYPE_OTHER}
        ]
    });

    //表单
    var formPanel = new Ext.form.Panel({
        layout: 'vbox',
        border: 0,
        defaults: {
            xtype: 'panel',
            layout: 'column',
            border: 0,
            defaults: {
                margin: '5 10'
            }
        },
        items: [{
            items: [{
                xtype: 'textfield',
                fieldLabel: '名称',
                blankText:'该项为必填项',
                allowBlank:false,
                labelWidth: 50,
                name: 'surveyObjGroupName'
            }, {
                xtype: 'combo',
                fieldLabel: '类型',
                store:surObjGroupTypeStore,
                editable:false,
                allowBlank:false,
                displayField:'typeName',
                valueField:'typeValue',
                labelWidth: 50,
                name: 'surveyObjGroupType'
            }]
        }, {

            items: [{
                width: 470,
                xtype: 'textarea',
                maxLength : 255,
                fieldLabel: '备注',
                labelWidth: 50,
                name: 'surveyObjGroupInfo'
            }]
        }]

    });

    if(!isAdd){
        formPanel.loadRecord(rec);
    }

    var win = new Ext.fillExtSurObjWin({
        title: title,
        width: 500,
        height: 180,
        items: [formPanel],
        buttons: [{
            text: '保存',
            handler: function () {
                if (formPanel.isValid()) {
                    if (fnt) {
                        var data = formPanel.getForm().getFieldValues();
                        fnt(data);
                    }
                }

            }
        }, {
            text: '取消',
            handler: function () {
                win.close();
            }
        }]
    });
    win.show();
    return win;


}