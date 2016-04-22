/**
 * 添加下级地区
 * Created by Paul on 2016/1/4.
 */
Ext.define('Ext.areaManageAddSubWin', {
    extend: 'Ext.window.Window',
    height: 470,
    width: 380,
    modal: true
});
Ext.areaManageAddSubWin.show = function (rec, fnt) {// 两个参数，fnt是回调函数，rec是record，可以自定义。
    var areaName = Ext.create('Ext.form.field.Text', {
        name: 'name',
        fieldLabel: '区域名称<font color="red">*</font>',
        labelWidth: 100,
        labelAlign: 'right',
        columnWidth: .9,
        border: false,
        allowBlank: false,
        blankText: '必填项',
        maxLength: 20,
        maxLengthText: '最多20个字符',
        enforceMaxLength: true
    });
    var areaCode = Ext.create('Ext.form.field.Number', {
        name: 'code',
        fieldLabel: '行政区划代码<font color="red">*</font>',
        labelWidth: 100,
        labelAlign: 'right',
        columnWidth: .9,
        border: false,
        // regex : /^\d+$/,
        // regexText : '行政区划代码只能是6位数字格式',
        allowBlank: false,
        blankText: '必填项',
        maxLength: 10,
        enforceMaxLength: true,
        regex: /^\d{0,}$/,
        regexText: "只能输入数字",
        allowDecimals: false,
        hideTrigger: true,
        keyNavEnabled: false,
        mouseWheelEnabled: false
    });
    var firstLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [areaName]
    });
    var secondLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [areaCode]
    });

    var enabled = Ext.create('Ext.form.field.Radio', {
        name: 'status',
        boxLabel: '启用',
        checked: true,
        inputValue: 1
    });
    var disabled = Ext.create('Ext.form.field.Radio', {
        name: 'status',
        boxLabel: '禁用',
        checked: false,
        inputValue: 0
    });
    var areaStatusGroup = Ext.create('Ext.form.RadioGroup', {
        name: 'areaStatusGroup',
        fieldLabel: "区域状态",
        labelAlign: 'right',
        columnWidth: .9,
        items: [enabled, disabled]
    });
    var secondLineTwo = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [areaStatusGroup]
    });

    /*
     * 标准区划
     * */
    var enabledStandard = Ext.create('Ext.form.field.Radio', {
        name: 'isStandard',
        boxLabel: '标准区划',
        checked: true,
        inputValue: 1
    });
    var disabledStandard = Ext.create('Ext.form.field.Radio', {
        name: 'isStandard',
        boxLabel: '非标准区划',
        checked: false,
        inputValue: 0
    });
    var standardStatusGroup = Ext.create('Ext.form.RadioGroup', {
        name: 'standardGroup',
        fieldLabel: '区划类别',
        labelAlign: 'right',
        columnWidth: .8,
        items: [enabledStandard, disabledStandard]
    });
    /*
     * 第三行
     * */
    var thirdLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [standardStatusGroup]
    });

    /*
     * 经纬度
     * */
    var longitude = Ext.create('Ext.form.field.Text', {
        name: 'longitude',
        fieldLabel: '经度',
        labelWidth: 100,
        labelAlign: 'right',
        border: false,
        width: '45%'
    });
    var latitude = Ext.create('Ext.form.field.Text', {
        name: 'latitude',
        fieldLabel: '纬度',
        labelWidth: 100,
        labelAlign: 'right',
        border: false,
        width: '45%'
    });
    /*
     * 第四行
     * */
    var forthLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [longitude, latitude]
    });

    /*
     * 地区英文全拼
     * */
    var nameEn = Ext.create('Ext.form.field.Text', {
        name: 'nameEn',
        fieldLabel: '英文全拼',
        labelWidth: 100,
        labelAlign: 'right',
        border: false,
    });
    /*
     * 第五行
     * */
    var fifthLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [nameEn]
    });

    /*
     * 地图文件
     * */
    var jsonSvg = Ext.create('Ext.form.field.File', {
        name: 'file',
        fieldLabel: '地图文件',
        labelWidth: 100,
        anchor: '80%',
        labelAlign: 'right',
        buttonText: '上传地图文件',
        width: '90%'
    });
    /*
     * 第六行
     * */
    var sixthLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [jsonSvg]
    });

    /*
     * 地区备注
     * */
    var areaComments = Ext.create('Ext.form.field.TextArea', {
        name: 'comments',
        fieldLabel: '区域备注',
        labelWidth: 100,
        labelAlign: 'right',
        columnWidth: .8,
        height: 100,
        anchor: '80%',
        maxLength: 255,
        maxLengthText: '超过长度限制',
        border: false,
        enforceMaxLength: true
    });
    /*
     * 第七行
     * */
    var seventhLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [areaComments]
    });
    /*
     * 地区等级*/
    var cityLevel = Ext.create('Ext.data.Store', {
        fields: ['sort', 'name'],
        proxy: {
            type: 'ajax',
            url: contextPath + '/support/manage/metadata/getAreaLevel'
        },
        autoLoad: true
    });
    var cityLevelCom = Ext.create('Ext.form.ComboBox', {
        name: 'regionLevel',
        fieldLabel: '地区等级',
        labelAlign: 'right',
        labelWidth: 100,
        store: cityLevel,
        queryMode: 'local',
        displayField: 'name',
        valueField: 'sort',
        editable: false,
        allowBlank: false
    });
    var eightLine = Ext.create('Ext.panel.Panel', {
        layout: 'column',
        border: false,
        margin: MARGIN_ROW_SPACE,
        items: [cityLevelCom]
    });

    var formPanel = Ext.create('Ext.form.FormPanel', {
        frame: false,
        border: true,
        region: 'center',
        items: [firstLine, secondLine, secondLineTwo, thirdLine, forthLine, fifthLine, sixthLine, seventhLine, eightLine]
    });
    var areaManageAddSubWin = Ext.create('Ext.areaManageAddSubWin', {
        title: '添加下级区域',
        frame: false,
        border: false,
        layout: 'border',
        items: [formPanel],
        listeners: {
            'close': function (panel, eOpts) {
            }
        },
        buttons: [{
            text: '保存',
            handler: function () {
                if (formPanel.getForm().isValid())
                    formPanel.getForm().submit({
                        url: contextPath + '/area/saveArea',
                        clientValidation: true,
                        waitTitle: '提示',
                        waitMsg: '正在提交数据...',
                        method: 'POST',
                        params: {
                            parentIds: rec.get('id')
                        },
                        success: function (form, action) {
                            var datas = Ext.decode(action.result.datas);
                            var msg = action.result.msg;
                            // 提示
                            Ext.Msg.alert('提示', msg,
                                function (id) {
                                    if ('ok' == id) {
                                        areaManageAddSubWin.close();
                                        // 把model信息返回给jsp页面，用于增加树信息
                                        eval(fnt)(datas);
                                    }
                                });

                        },
                        failure: function (form, action) {
                            Ext.Msg.alert('提示', action.result.msg);
                        }
                    });
                else
                    Ext.Msg.alert('提示', '请把信息改完整');
            }
        }, {
            text: '取消',
            handler: function () {
                areaManageAddSubWin.close();
            }
        }]
    });
    areaManageAddSubWin.show();
};
