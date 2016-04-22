/**
 * Created by zhoutao on 2016/1/5.
 */
Ext.define('Ext.addUnitWin', {
    extend : "Ext.window.Window",
    width : 600,
    title : "添加计量单位信息"
});
//添加数据类型
function addDataType(){
    Ext.addDataTypeWin.init(function(id){
        var combo = Ext.getCmp("comboDataTypeId");
        combo.getStore().reload();
        combo.setValue(id);
    });
}

Ext.addUnitWin.init = function(unitTypeId, fun){
    var firstLinePanel = Ext.create("Ext.panel.Panel", {
        border : 0,
        layout : "column",
        bodyPadding : 5,
        defaults : {
            anchor : "95%",
            labelAlign : "right"
        },
        items : [{
            name : "unitType.id",
            xtype : "textfield",
            value : unitTypeId,
            hidden : true
        },{
            xtype : "textfield",
            fieldLabel : "计量单位名称<span style='color: red'>*</span>",
            columnWidth :.45 ,
            allowBlank : false,
            maxLength : 100,
            maxLengthText:'最多100个字符',
            name : "name"
        },{
            xtype : "displayfield",
            columnWidth :.075
        },{
            xtype : "radio",
            fieldLabel : "基准计量单位<span style='color: red'>*</span>",
            columnWidth :.25,
            name : "standard",
            boxLabel : "是",
            checked : true,
            inputValue : "1"
        },{
            xtype : "radio",
            columnWidth :.2,
            name : "standard",
            boxLabel : "否",
            inputValue : "0"
        }]
    });

    var secodLinePanel = Ext.create("Ext.panel.Panel", {
        border : 0,
        layout : "column",
        bodyPadding : 5,
        defaults : {
            anchor : "95%",
            labelAlign : "right"
        },
        items : [{
            xtype : "combo",
            fieldLabel : "基准计量单位",
            columnWidth :.45,
            name : "standardUnit.id",
            store : Ext.create("Ext.data.Store", {
                fields : ["id", "name"],
                proxy : {
                    type : "ajax",
                    url : GLOBAL_PATH+"/support/unit/unitManager/findStandardUnitsByUnitType",
                    reader : {
                        type : "json"
                    }
                },
                autoLoad : true,
                listeners : {
                    beforeload : function(_this){
                        _this.getProxy().extraParams = {
                            id : unitTypeId
                        };
                    }
                }
            }),
            displayField : "name",
            valueField : "id"

        },{
            xtype : "displayfield",
            columnWidth :.075
        },{
            xtype : "textfield",
            fieldLabel : "换算值",
            columnWidth :.45,
            name : "equivalentValue"
        }]
    });

    var thirdLinePanel = Ext.create("Ext.panel.Panel", {
        border : 0,
        layout : "column",
        bodyPadding : 5,
        defaults : {
            anchor : "95%",
            labelAlign : "right"
        },
        items : [{
            id : "comboDataTypeId",
            xtype : "combo",
            fieldLabel : "数据类型<span style='color: red'>*</span>",
            allowBlank : false,
            name : "dataType.id",
            columnWidth :.45,
            store : Ext.create("Ext.data.Store", {
                fields : ["id", "name", "dataFormat"],
                proxy : {
                    type : "ajax",
                    url : GLOBAL_PATH+"/support/unit/unitManager/findAllDataTypes",
                    reader : {
                        type : "json"
                    }
                },
                autoLoad : true
            }),
            displayField : "name",
            valueField : "id",
            listeners : {
                'select' : function(combobox, record){
                    Ext.getCmp("textDataFormatId").setValue(record.get("dataFormat"));
                }
            }
        },{
            xtype : "displayfield",
            columnWidth :.075,
            value : "<a href='javascript: addDataType();' style='cursor: pointer'>添加</a>"
        },{
            id : "textDataFormatId",
            xtype : "textfield",
            fieldLabel : "数据格式<span style='color: red'>*</span>",
            allowBlank : false,
            name : "dataFormat",
            columnWidth :.45
        }]
    });

    var forthLinePanel = Ext.create("Ext.panel.Panel", {
        border : 0,
        layout : "column",
        bodyPadding : 5,
        defaults : {
            anchor : "95%",
            labelAlign : "right"
        },
        items : [{
            xtype : "radio",
            fieldLabel : "国际标准<span style='color: red'>*</span>",
            columnWidth :.25,
            name : "internationalStandard",
            boxLabel : "是",
            checked : true,
            inputValue : "1"
        },{
            xtype : "displayfield",
            columnWidth :.075
        },{
            xtype : "radio",
            columnWidth :.2,
            name : "internationalStandard",
            boxLabel : "否",
            inputValue : "0"
        }]
    });

    var fiveLinePanel = Ext.create("Ext.panel.Panel", {
        border : 0,
        layout : "column",
        bodyPadding : 5,
        defaults : {
            anchor : "95%",
            labelAlign : "right"
        },
        items : [{
            xtype : "textareafield",
            grow : true,
            name : "comments",
            fieldLabel : "备注说明",
            maxLength : 200,
            maxLengthText:'最多200个字符',
            columnWidth :.9
        }]
    });

    var addUnitForm = Ext.create("Ext.form.Panel", {
        id : "addUnitForm",
        bodyPadding : 5,
        defaults : {
            labelWidth : 100,
            labelAlign : "right"
        },
        defaultType : "textfield",
        items : [firstLinePanel,secodLinePanel,thirdLinePanel,forthLinePanel,fiveLinePanel]
    });

    var addUnitWin = Ext.create("Ext.addUnitWin", {
        items : [addUnitForm],
        buttons : [{
            text : "保存",
            handler : function(){
                if(addUnitForm.isValid){
                    addUnitForm.submit({
                        url : GLOBAL_PATH+"/support/unit/unitManager/addUnit",
                        success : function(form, action){
                            Ext.Msg.alert('提示', action.result.msg);
                            addUnitWin.destroy();
                            fun();
                        },
                        failure: function(form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                        }
                    })
                }
            }

        }]
    });


    addUnitWin.show();

    return addUnitWin;
}
