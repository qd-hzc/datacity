<%--
  Created by IntelliJ IDEA.
  User: hzc
  Date: 2015/12/31
  Time: 16:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String contextPath = request.getContextPath();
%>
<head>
    <title>地区管理</title>
</head>
<body>
<script src="<%=contextPath%>/Plugins/eharts/echarts-all.js" type="text/javascript"></script>
<script src="<%=contextPath%>/City/support/manage/area/add-same-win.js" type="text/javascript"></script>
<script src="<%=contextPath%>/City/support/manage/area/add-sub-win.js" type="text/javascript"></script>
<script src="<%=contextPath%>/City/support/manage/area/show-map.js" type="text/javascript"></script>
<script>
    var contextPath = "<%=contextPath%>";
    var MARGIN_ROW_SPACE = '8 0 0 0';
    Ext.onReady(function () {
        /*
         * 地区树Store
         */
        var leftTreeAreaStore = Ext.create('Ext.data.TreeStore', {
            proxy: {
                type: 'ajax',
                api: {
                    read: contextPath + '/area/getAreaByParent',
                    update: contextPath + '/area/saveAreaSorts'
                }
            },
            root: {
                expanded: true,
                id: 0,
                text: '地区'
            }
        });
        /*
         * 地区树
         */
        var leftTreeAreaTreePanel = Ext.create('Ext.tree.Panel', {
            store: leftTreeAreaStore,
            region: 'west',
            rootVisible: true,
            width: '18%',
            border: false,
            frame: false,
            height: '100%',
            autoScroll: true,
            enableDD: true,// 是否支持拖拽效果
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop'
                },
                listeners: {
                    drop: function (node, data, overModel, dropPosition, dropHandlers) {
                        var dragNode = data.records[0];
                        var pNode = dragNode.parentNode;
                        dragNode.set('parentId', pNode.getId());
                        Ext.Array.each(pNode.childNodes, function (child) {
                            child.set('sort', child.data.index);
                        });
                        leftTreeAreaStore.sync();
                    }
                }
            }
            ,
            listeners: {
                itemclick: function (_this, record, item, index, e, eOpts) {
                    areaInfoPanel.getForm().loadRecord(record);
                    console.log(record);
                },
                itemcontextmenu: function (_this, record, item, index, e) {
                    e.preventDefault();
                    if (window.areaInfoMenu) {
                        window.areaInfoMenu.hide();
                    }
                    showAreaInfoMenu(_this, record, item, index, e);
                }
            }
        });

        /**
         * 显示右键菜单
         */
        function showAreaInfoMenu(_this, record, item, index, e) {
            var areaInfoMenu = new Ext.menu.Menu({
                renderTo: Ext.getBody(),
                items: [{
                    text: '添加下级',
                    iconCls: 'Add',
                    handler: function () {
                        var record = leftTreeAreaTreePanel.getSelectionModel().getSelection();
                        if (record && record.length > 0) {
                            Ext.areaManageAddSubWin.show(record[0], function (model) {
                                var selection = record[0];
                                selection.set('leaf', false);
                                selection.set('expanded', true);
                                selection.appendChild(model);
                            });
                        } else {
                            Ext.Msg.alert('提示', '请选择部门');
                        }
                    }
                }, {
                    text: '添加同级',
                    iconCls: 'Controladdblue',
                    disabled: record.get('id') == 0,
                    handler: function () {
                        var record = leftTreeAreaTreePanel.getSelectionModel().getSelection();
                        if (record && record.length > 0) {
                            Ext.areaManageAddSameWin.show(record[0].parentNode, function (model) {
                                record[0].parentNode.appendChild(model);
                            });
                        } else {
                            var root = leftTreeAreaTreePanel.getRootNode();
                            Ext.areaManageAddSameWin.show(root, function (model) {
                                root.set('leaf', false);
                                root.set('expanded', true);
                                root.appendChild(model);
                            });
                        }
                    }
                }, '-', {
                    text: '删除',
                    disabled: record.get('id') == 0,
                    iconCls: 'Delete',
                    handler: function () {
                        Ext.Msg.confirm('提示', '确定要删除吗？', function (id) {
                            if (id == 'yes') {
                                var record = leftTreeAreaTreePanel.getSelectionModel().getSelection();
                                deleteArea(record);
                            }
                        });
                    }
                }]
            });

            function deleteArea(record) {
                $.ajax({
                    type: 'post',
                    url: contextPath + '/area/deleteArea',
                    data: {id: record[0].get('id')},
                    dataType: 'json',
                    success: function (data) {
                        Ext.Msg.alert('提示', data.msg,
                                function (id) {
                                    if ('ok' == id) {
                                        if (data.code == 200) {
                                            record[0].remove();
                                        }
                                    }
                                }
                        );

                    },
                    error: function () {
                        Ext.Msg.alert('提示', '系统繁忙，请稍候再试');
                    }
                })
            }

            areaInfoMenu.showAt(e.getPoint());
            window.areaInfoMenu = areaInfoMenu;
        }

        /*
         * id
         * */

        var areaId = Ext.create('Ext.form.field.Hidden', {
            name: 'id',
            fieldLabel: '地区Id',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .45,
            border: false
        });

        /*
         * parentId
         */
        var areaParentId = Ext.create('Ext.form.field.Hidden', {
            name: 'parentIds',
            fieldLabel: '地区父Id',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .45,
            border: false
        });
        /*
         * 地区名称*/
        var areaName = Ext.create('Ext.form.field.Text', {
            name: 'name',
            fieldLabel: '区域名称',
            labelWidth: 100,
            labelAlign: 'right',
            columnWidth: .7,
            anchor: '80%',
            border: false,
            width: '45%',
            allowBlank: false
        });
        /*
         * 行政区划代码
         * */
        var areaCode = Ext.create('Ext.form.field.Number', {
            name: 'code',
            columnWidth: .7,
            fieldLabel: '行政区划代码',
            anchor: '80%',
            labelWidth: 100,
            labelAlign: 'right',
            border: false,
            width: '45%',
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
        /*
         * 第一行
         * */
        var firstLineArea = Ext.create('Ext.panel.Panel', {
            layout: {
                type: 'hbox'
            },
            margin: MARGIN_ROW_SPACE,
            border: false,
            items: [areaId, areaParentId, areaName, areaCode]
        });
        /*
         * 地区状态
         * */
        var enabledStatus = Ext.create('Ext.form.field.Radio', {
            name: 'status',
            boxLabel: '启用',
            checked: true,
            inputValue: 1
        });
        var disabledStatus = Ext.create('Ext.form.field.Radio', {
            name: 'status',
            boxLabel: '禁用',
            checked: false,
            inputValue: 0
        });
        var areaStatusGroup = Ext.create('Ext.form.RadioGroup', {
            name: 'statusGroup',
            fieldLabel: '区域状态',
            labelAlign: 'right',
            columnWidth: .8,
            items: [enabledStatus, disabledStatus]
        });
        /*
         * 第二行
         * */
        var secondLineArea = Ext.create('Ext.panel.Panel', {
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
        var thirdLineArea = Ext.create('Ext.panel.Panel', {
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
        var forthLineArea = Ext.create('Ext.panel.Panel', {
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
        var fifthLineArea = Ext.create('Ext.panel.Panel', {
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
//            msgTarget: 'side',
//            allowBlank: false,
            anchor: '80%',
            labelAlign: 'right',
            buttonText: '上传地图文件',
            width: '60%',
        });
        /*
         * 提示文字
         * */
        var message = Ext.create('Ext.panel.Panel', {
            border: false,
            items: [
                {
                    xtype: 'tbtext',
                    text: '提示：地图文件支持svg和json格式',
                    margin: '10px 0px 00px 100px'
                }
            ]
        });
        /*
         * 第六行
         * */
        var sixthLineArea = Ext.create('Ext.panel.Panel', {
            layout: 'column',
            border: false,
            margin: MARGIN_ROW_SPACE,
            items: [jsonSvg, {
                xtype: 'button',
                text: '预览地图',
                width: '100px',
                margin: '0 0 0 10px',
                handler: function () {
                    var record = areaInfoPanel.getForm().getRecord();
                    if (record != null && record.data.jsonSvg != null) {
                        var data = record.data;
                        Ext.showMapWin.show(data.jsonSvg, data.mapType);
                    } else {
                        Ext.Msg.alert('提示', '没有要显示的地图')
                    }
                }
            }]
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
        var seventhLineArea = Ext.create('Ext.panel.Panel', {
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

        /*
         * 表单
         * */
        var areaInfoPanel = Ext.create('Ext.form.Panel', {
            region: 'center',
            frame: false,
            autoWidth: true,
            height: '100%',
            items: [{
                xtype: 'fieldset',
                title: '区域信息',
                margin: '10 10 10 10',
                padding: '0 0 20 0',
                items: [firstLineArea, secondLineArea, thirdLineArea, forthLineArea, fifthLineArea, sixthLineArea, message, seventhLineArea, eightLine]
            }, {
                xtype: 'panel',
                layout: 'column',
                border: false,
                margin: '0 10 10 10',
                items: [{
                    xtype: 'displayfield',
                    border: false,
                    columnWidth: .92
                }, {
                    xtype: 'button',
                    text: '保存',
                    width: 70,
                    columnWidth: .08,
                    margin: '0 0 0 0',
                    handler: function () {
                        var record = leftTreeAreaTreePanel.getSelectionModel().getSelection();
                        if (record.length < 1) {
                            Ext.Msg.alert('提示', '请选择需要修改的地区');
                            return;
                        }
                        if (areaInfoPanel.getForm().isValid())
                            areaInfoPanel.getForm().submit({
                                url: contextPath + '/area/souArea',
                                clientValidation: true,
                                waitTitle: '提示',
                                waitMsg: '正在提交数据...',
                                method: 'POST',
                                success: function (form, action) {
                                    try {
                                        Ext.Msg.alert('提示', action.result.datas);
                                        var record = leftTreeAreaTreePanel.getSelectionModel().getSelection();
                                        var obj = Ext.apply(record[0].getData(), areaInfoPanel.getForm().getValues());//copy两个对象的属性和值
                                        record[0].set(obj);
                                        record[0].set('text', obj.name);
                                    } catch (e) {
                                        alert(e);
                                    }
                                    leftTreeAreaStore.load();
                                },
                                failure: function (form, action) {
                                    Ext.Msg.alert('提示', action.result.datas);
                                }
                            });
                        else
                            Ext.Msg.alert('提示', '请把信息填写完整');
                    }
                }]
            }],
            listeners: {
                active: function () {
                    alert(1);
                }
            }
        });
        Ext.create('Ext.panel.Panel', {
            renderTo: 'areaManageId',
            layout: 'border',
            height: '100%',
            border: false,
            frame: false,
            width: '100%',
            items: [leftTreeAreaTreePanel, areaInfoPanel],
            listeners: {
                render: function () {
                    if (indexPanel) {
                        var tabPanel = indexPanel.down('#tabCenter');
                        var myTab = tabPanel.getActiveTab();
                        if (myTab)
                            myTab.myPanel = this;
                    }
                    if (this.hasListener('reDR'))
                        this.un('reDR');
                    this.on('reDR', function (Obj) {
                        if (Obj)
                            this.updateBox(Obj)
                    });
                }
            },
            close: function () {
                var tabPanel = indexPanel.down('#tabCenter');
                if (tabPanel && tabPanel.getActiveTab()) {
                    tabPanel.getActiveTab().fireEvent('close', true)
                }
            },
            myUpdateBox: function (obj) {
                this.updateBox(obj);
            }
        });
    });
</script>
<div id='areaManageId' style="width: 100%;height: 100%;"></div>
</body>
</html>
