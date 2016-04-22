/**
 * Created by zhoutao on 2016/1/4.
 */
Ext.define('Ext.addDataTypeWin', {
    extend : 'Ext.window.Window',
    width : 300,
    modal : true,
    title : '添加数据类型'
});

Ext.addDataTypeWin.init = function(fun){
    var addDataTypForm = Ext.create('Ext.form.Panel', {
        id : 'addDataTypForm',
        bodyPadding : 10,
        defaultType : 'textfield',
        items : [{
            fieldLabel : '类型名称<span style="color: red">*</span>',
            allowBlank : false,
            labelWidth : 100,
            labelAlign : 'right',
            name : 'name',
            anchor : '100%'
        },{
            fieldLabel : '数据格式<span style="color: red">*</span>',
            allowBlank : false,
            labelWidth : 100,
            labelAlign : 'right',
            grow : true,
            name : 'dataFormat',
            anchor : '100%'
        }]
    });

    var addDataTypeWin = Ext.create('Ext.addDataTypeWin', {
        items : [addDataTypForm],
        buttons : [{
            text : '保存',
            handler : function(){
                if(addDataTypForm.isValid()){
                    addDataTypForm.submit({
                        url : GLOBAL_PATH+"/support/unit/unitManager/addDataType",
                        success : function(form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                            addDataTypeWin.destroy();
                            fun(action.result.datas.id);//回调
                        },
                        failure: function(form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                        }
                    });
                }
            }

        }]
    });

    addDataTypeWin.show();
    return addDataTypeWin;
}
