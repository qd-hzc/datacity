/**
 * Created by zhoutao on 2016/1/4.
 */
Ext.define('Ext.updateUnitTypeWin', {
    extend : 'Ext.window.Window',
    width : 400,
    title : '修改单位类型'
});

Ext.updateUnitTypeWin.init = function(fun, node){
    var updateUnitTypeForm = Ext.create('Ext.form.Panel', {
        id : 'updateUnitTypeForm',
        bodyPadding : 10,
        defaultType : 'textfield',
        items : [{
            name : 'id',
            hidden : true,
            value : node.get("id")
        },{
            fieldLabel : '类型名称<span style="color: red">*</span>',
            allowBlank : false,
            labelWidth : 100,
            labelAlign : 'right',
            name : 'name',
            value : node.get("name"),
            anchor : '100%'

        },{
            fieldLabel : '类型说明',
            xtype : 'textareafield',
            labelWidth : 100,
            labelAlign : 'right',
            grow : true,
            name : 'comments',
            value : node.get("comments"),
            anchor : '100%'
        }]
    });

    var updateUnitTypeWin = Ext.create('Ext.updateUnitTypeWin', {
        items : [updateUnitTypeForm],
        buttons : [{
            text : '修改',
            handler : function(){
                if(updateUnitTypeForm.isValid()){
                    updateUnitTypeForm.submit({
                        url : GLOBAL_PATH+"/support/unit/unitManager/updateUnitType",
                        success : function(form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                            updateUnitTypeWin.destroy();
                            fun();//回调
                        },
                        failure: function(form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                        }
                    });
                }
            }

        }]
    });

    updateUnitTypeWin.show();
    return updateUnitTypeWin;
}
