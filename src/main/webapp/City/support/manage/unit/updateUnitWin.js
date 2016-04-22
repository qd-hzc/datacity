/**
 * Created by zhoutao on 2016/1/5.
 */
Ext.define('Ext.updateUnitWin', {
    extend : "Ext.window.Window",
    width : 600,
    title : "修改计量单位信息"
});
//添加数据类型
function addDataTypeNew(){
    Ext.addDataTypeWin.init(function(id){
        var combo = Ext.getCmp("comboUpdateDataTypeId");
        combo.getStore().reload();
        combo.setValue(id);
    });
}

Ext.updateUnitWin.init = function(record, fun){
    var firstLinePanel = Ext.create("Ext.panel.Panel", {
        border : 0,
        layout : "column",
        bodyPadding : 5,
        defaults : {
            anchor : "95%",
            labelAlign : "right"
        },
        items : [{
            name : "id",
            xtype : "textfield",
            value : record.get("id"),
            hidden : true
        },{
            name : "unitType.id",
            xtype : "textfield",
            value : record.get("unitType").id,
            hidden : true
        },{
            xtype : "textfield",
            fieldLabel : "计量单位名称<span style='color: red'>*</span>",
            columnWidth :.45 ,
            allowBlank : false,
            maxLength : 100,
            maxLengthText:'最多100个字符',
            name : "name",
            value : record.get("name")
        },{
            xtype : "displayfield",
            columnWidth :.075
        },{
            xtype : "radiogroup",
            columns : 2,
            fieldLabel : "基准计量单位<span style='color: red'>*</span>",
            items :[{
                xtype : "radio",
                columnWidth :.25,
                name : "standard",
                boxLabel : "是",
                inputValue : "1"
            },{
                xtype : "radio",
                columnWidth :.2,
                name : "standard",
                boxLabel : "否",
                inputValue : "0"
            }],
            listeners : {
                beforerender : function(_this){

                    var standard =  record.get("standard");
                    if(standard != null){
                        if(standard == "true")
                            _this.setValue({standard : "1"});
                        else
                            _this.setValue({standard : "0"});
                    }
                }
            }
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
                            id : record.get("unitType").id
                        };
                    }
                }
            }),
            displayField : "name",
            valueField : "id",
            listeners : {
                beforerender : function(_this){
                    var standardUnit = record.get("standardUnit");
                    if(standardUnit != null)
                        _this.setValue(standardUnit.id);
                }
            }

        },{
            xtype : "displayfield",
            columnWidth :.075
        },{
            xtype : "textfield",
            fieldLabel : "换算值",
            columnWidth :.45,
            name : "equivalentValue",
            value : record.get("equivalentValue")
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
            id : "comboUpdateDataTypeId",
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
            },
            value : record.get("dataType").id
        },{
            xtype : "displayfield",
            columnWidth :.075,
            value : "<a href='javascript: addDataTypeNew();' style='cursor: pointer'>添加</a>"
        },{
            id : "textDataFormatId",
            xtype : "textfield",
            fieldLabel : "数据格式<span style='color: red'>*</span>",
            allowBlank : false,
            name : "dataFormat",
            columnWidth :.45,
            value : record.get("dataFormat")
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
            xtype : "radiogroup",
            columns : 2,
            fieldLabel : "国际标准<span style='color: red'>*</span>",
            value : record.get("internationalStandard"),
            items : [{
                xtype : "radio",
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
            }],
            listeners : {
                beforerender : function(_this){

                    var internationalStandard =  record.get("internationalStandard");
                    if(internationalStandard != null){
                        if(internationalStandard == "true")
                            _this.setValue({internationalStandard : "1"});
                        else
                            _this.setValue({internationalStandard : "0"});
                    }
                }
            }
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
            columnWidth :.9,
            value : record.get("comments")
        }]
    });

    var updateUnitForm = Ext.create("Ext.form.Panel", {
        id : "addUnitForm",
        bodyPadding : 5,
        defaults : {
            labelWidth : 100,
            labelAlign : "right"
        },
        defaultType : "textfield",
        items : [firstLinePanel,secodLinePanel,thirdLinePanel,forthLinePanel,fiveLinePanel]
    });

    var updateUnitWin = Ext.create("Ext.updateUnitWin", {
        items : [updateUnitForm],
        buttons : [{
            text : "修改",
            handler : function(){
                if(updateUnitForm.isValid){
                    updateUnitForm.submit({
                        url : GLOBAL_PATH+"/support/unit/unitManager/updateUnit",
                        success : function(form, action){
                            Ext.Msg.alert('提示', action.result.msg);
                            updateUnitWin.destroy();
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


    updateUnitWin.show();

    return updateUnitWin;
}
