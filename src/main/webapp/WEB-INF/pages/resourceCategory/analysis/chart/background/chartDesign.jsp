<%--
  Created by IntelliJ IDEA.
  User: wys
  Date: 2016/2/29
  Time: 15:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图表设计器</title>
    <jsp:include page="/WEB-INF/pages/common/imp.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/pages/common/sysConstant.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/pages/common/metaDataImp.jsp"></jsp:include>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/background/fillChartInfoWin.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/design/addStructureGroupWin.js"></script>
    <script src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/design/addStructureWin.js"
            type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/City/resourceCategory/analysis/chart/design/addStructureTimeDataWin.js"
            type="text/javascript"></script>
    <script type="text/javascript">
        var chartId = ${chartId};
        var fres = ${fres};// 报送频率
        var periodType = ${periodType};
        var metadataTypes = ${metadataTypes};
        Ext.onReady(function () {

            function updateChartStructure(data, pNode, fn) {
                Ext.Ajax.request({
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartStructure',
                    method: 'POST',
                    jsonData: data,
                    success: function (response, opts) {
                        var result = Ext.JSON.decode(response.responseText);
                        var node = pNode.createNode(result.datas[0]);
                        pNode.appendChild(node);
                        if (fn) {
                            fn(node);
                        }
                        if(node.get("metaType")==METADATA_TYPE.TIME)
                        Ext.Ajax.request({
                            url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartStructureTimeData',
                            method: 'POST',
                            jsonData: Ext.JSON.decode(result.datas[0].metaExt).timeRange,
                            params: {
                                foreignType: 2,// 图表
                                foreignId: result.datas[0].id// 图表结构id
                            },
                            success: function (response, opts) {
                                var result = Ext.JSON.decode(response.responseText);
                            }
                        })
                    }
                })
            }

            // 不带子节点添加分类轴或序列
            function addChartStructure(_node, dataType, metaExt, overNode, _pos, pNode) {

                var data = {
                    chartId: chartId,
                    metaName: _node.get("dataName"),
                    metaId: _node.get("dataValue"),
                    metaType: dataType,
                    structureType: overNode.get("structureType"),
                    realNode: 0,
                    leaf: true
                };
                if (metaExt) {
                    data.metaExt = Ext.encode(metaExt);
                }
                if (_pos == 'append') {
                    data.parentId = overNode.get("id");
                    pNode = overNode;
                    data.structureSort = pNode.childNodes.length + 1;

                } else {
                    data.parentId = overNode.parentNode.get("id");
                    data.structureSort = pNode.childNodes.length + 1;
                }
                updateChartStructure(data, pNode, null);
            }

            // 带子节点添加分类轴或序列(刷新有点问题)
            function addChartStructureGroup(_node, dataType, metaExt, overNode, _pos, pNode) {
                var _overNode = overNode;
                var nodes = [];
                var data = {
                    chartId: chartId,
                    metaName: _node.get("dataName"),
                    metaId: _node.get("dataValue"),
                    metaType: dataType,
                    structureType: overNode.get("structureType"),
                    realNode: 0,
                    leaf: _node.get("leaf")
                };

                /*                _node.findChildBy(function(node){
                 createStructureNode(node,overNode,nodes);
                 },null,true);*/

                if (metaExt) {
                    data.metaExt = Ext.encode(metaExt);
                }
                if (_pos == 'append') {
                    data.parentId = overNode.get("id");
                    pNode = overNode;
                    data.structureSort = pNode.childNodes.length + 1;
                } else {
                    data.parentId = overNode.parentNode.get("id");
                    data.structureSort = pNode.childNodes.length + 1;
                }
                if (!_node.get("leaf")) {
                    updateChartStructure(data, pNode, function (node) {
                        addChartStructureChildren(node, _node.childNodes)
                    });
                }
            }


            function addChartStructureChildren(pNode, childnodes, isleaf) {
                if (!isleaf) {
                    Ext.Array.each(childnodes, function (child) {
                        var dataType = child.get("dataType");
                        var dataInfo1 = child.get("dataInfo1");
                        var data = {
                            chartId: chartId,
                            metaName: child.get("dataName"),
                            metaId: child.get("dataValue"),
                            metaType: dataType,
                            structureType: pNode.get("structureType"),
                            realNode: 0,
                            leaf: child.get("leaf")
                        };
                        var metaExt = getObjMetaExt(child);
                        if (metaExt && dataType == METADATA_TYPE.RESEARCH_OBJ) {
                            data.metaExt = Ext.encode(metaExt);
                        }
                        data.parentId = pNode.get("id");
                        data.structureSort = child.data.index + 1;
                        updateChartStructure(data, pNode, function (node) {
                            addChartStructureChildren(node, child.childNodes, child.get("leaf"));
                        });
                    });
                }
            }


            //-----------------------------------------------元数据开始---------------------------------------------
            var dataModel = createModel("dataModel", function () {
                Ext.define('dataModel', {
                    extend: 'Ext.data.TreeModel',
                    idProperty: 'id',
                    fields: [
                        /*{name: 'id', type: 'int'},*/
                        {name: 'dataName', type: 'string'},
                        {name: 'dataValue', type: 'int'},
                        {name: 'dataType', type: 'int'},
                        {name: 'dataInfo1', type: 'string'},
                        {name: 'dataInfo2', type: 'string'}
                    ]
                });
            });
            var itemDataStore = new Ext.data.TreeStore({
                model: 'dataModel',
                autoLoad: true,
                //parentIdProperty:'parentId',
                proxy: {
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
//                    url: GLOBAL_PATH + '/resourcecategory/analysis/common/analysis/getItemGroupTree',
                    url: GLOBAL_PATH + '/support/regime/report/designReport/getItemTree?node=0',
                    /*api: {
                     create: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                     update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                     destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delAnalysisChartBase'
                     },*/
                    reader: {
                        type: 'json'/*,
                         rootProperty: 'datas'*/
                    }
                },
                root: {
                    expanded: true,
                    id: 0,
                    children: []
                }

            });
            var groupDataStore = new Ext.data.TreeStore({
                model: 'dataModel',
                autoLoad: true,
                proxy: {
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
//                    url: GLOBAL_PATH + '/resourcecategory/analysis/common/analysis/getItemGroupTree',
                    url: GLOBAL_PATH + '/resourcecategory/analysis/common/analysis/getGroupInfoTreeForChart',
                    /*api: {
                     create: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                     update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateAnalysisChartBase',
                     destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delAnalysisChartBase'
                     },*/
                    reader: {
                        type: 'json'/*,
                         rootProperty: 'datas'*/
                    }
                },
                root: {
                    expanded: true,
                    id: 0,
                    children: []
                }

            });
            var metaItem = new Ext.tree.Panel({
                title: '指标元数据',
                width: '100%',
                autoScroll: true,
                flex: 1,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop',
                        dragGroup: 'structure',
                        enableDrop: false,
                        dragZone: {
                            onBeforeDrag: function (data) {
                                //只能拖动指标
                                var rec = null;
                                var dataType = null;

                                if (data.event.record) {
                                    data.copy = true;
                                    rec = data.event.record;
                                    dataType = rec.get('dataType');
                                    if (dataType == METADATA_TYPE.ITEM) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        }
                    }
                },
                store: itemDataStore,
                displayField: 'dataName',
                rootVisible: false,
                listeners: {
                    itemclick: function (_this, record, item, index, e, eOpts) {
                    }
                }
            });
            var metaDict = new Ext.tree.Panel({
                title: '其他元数据',
                width: '100%',
                flex: 1,
                store: groupDataStore,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop',
                        enableDrop: false,
                        dragGroup: 'structure',
                        dragZone: {
                            onBeforeDrag: function (data) {
                                //不能拖动描述类型
                                var rec = null;
                                var dataType = null;

                                if (data.event.record) {
                                    data.copy = true;
                                    rec = data.event.record;
                                    dataType = rec.get('dataType');
                                    if (dataType != METADATA_TYPE.SYSTEM_DESCRIBE_TYPE) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        }
                    }
                },
                displayField: 'dataName',
                rootVisible: false
            });
            var metaPanel = new Ext.panel.Panel({
                title: '元数据',
                region: 'west',
                width: '20%',
                border: 0,
                height: '100%',
                layout: 'vbox',
                items: [metaItem, metaDict]
            });

            //-----------------------------------------------元数据结束---------------------------------------------
            //-----------------------------------------------结构开始---------------------------------------------
            function getObjMetaExt(_node) {
                var obj = {
                    objType: _node.get("dataInfo1")
                }
                if (_node.get("dataInfo1") != SUROBJ_TYPE.AREA) {
                    obj.areaId = _node.get("dataInfo2");
                }
                return obj;
            }

            /**
             * 根据元数据节点类型 创建结构节点
             * * @param node
             */
            function judgeMetaToCreateStructure(node, overNode, pos) {
                var _node = node;
                var pNode = overNode.parentNode;
                var _overNode = overNode;
                var _pos = pos;
                var dataType = node.get('dataType');
                var structureType = node.get("structureType");
                var pStructureType = overNode.get("structureType");
                switch (dataType) {

                    case METADATA_TYPE.ITEM :
                        showAddStructureWindow(_node, function (metaExt) {
                            addChartStructure(_node, dataType, metaExt, overNode, _pos, pNode);
                        })
                        break;
                    case METADATA_TYPE.TIME_FRAME:
                        addChartStructure(_node, dataType, null, overNode, _pos, pNode);
                        break;
                    case METADATA_TYPE.ITEM_MENU:
                        if (node.data.leaf) {
                            addChartStructure(_node, dataType, null, overNode, _pos, pNode);
                        } else {
                            var win = Ext.addStructureGroup.init(null, function (data) {
                                if (data.group == 0) {
                                    addChartStructure(_node, dataType, null, overNode, _pos, pNode);
                                } else {
                                    addChartStructureGroup(_node, dataType, null, overNode, _pos, pNode);
                                }
                                win.close();
                            });
                        }
                        break;
                    case METADATA_TYPE.RESEARCH_OBJ_GROUP:
                        var win = Ext.addStructureGroup.init(null, function (data) {
                            if (data.group == 0) {
                                addChartStructure(_node, dataType, null, overNode, _pos, pNode);
                            } else {
                                addChartStructureGroup(_node, dataType, null, overNode, _pos, pNode);
                            }
                            win.close();
                        });
                        break;
                    case METADATA_TYPE.RESEARCH_OBJ:
                        var metaExt = getObjMetaExt(_node);
                        addChartStructure(_node, dataType, metaExt, overNode, _pos, pNode);
                        break;
                    case METADATA_TYPE.TIME:
                        var win = Ext.addStructureTimeDataWin.init(fres, chartId, function (data) {
                            var metaExt = {
                                timeRange: data.timeRange,
                                periodType: periodType
                            };
                            addChartStructure(_node, dataType, metaExt, overNode, _pos, pNode);
                            win.close();
                        });
                        break;
                    default:
                        if (dataType) {
                            addChartStructure(_node, dataType, null, overNode, _pos, pNode);
                        }
                        break;
                }
            }

            function createChartInfo(root) {
                var nodeList = [];
                var infoNodeList = [];
                var _type = root.get('structureType');
                root.findChildBy(function (tmpNode) {
                    if (tmpNode.isLeaf() || tmpNode.get('realNode') == ANALYSISCHART_INFO.REALNODE) {
                        nodeList.push(tmpNode);
                    }
                    return false;
                }, null, true);

                Ext.Array.each(nodeList, function (tmpNode) {
                    var tmp = {
                        name: tmpNode.get('metaName'),
                        chartId: chartId,
                        infoType: _type,
                        chartType: ANALYSISCHART_INFO.CHART_LINE,
                        isShow: ANALYSISCHART_INFO.SERIES_SHOW,
                        axis: ANALYSISCHART_INFO.LEFTAXIS,
                        structureId: tmpNode.get('id'),
                        infoSort: tmpNode.get('structureSort'),
                        metaType: tmpNode.get('metaType')
                    };
                    infoNodeList.push(tmp);

                });
                Ext.Ajax.request({
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartInfo',
                    method: 'POST',
                    jsonData: infoNodeList,
                    success: function (response, opts) {
                        var result = Ext.JSON.decode(response.responseText);
                        if (result.success) {
                            if (_type == ANALYSISCHART_INFO.TYPE_CATEGORY) {
                                infoCategoryStore.add(result.datas);
                            } else if (_type == ANALYSISCHART_INFO.TYPE_SERIES) {
                                infoSeriesStore.add(result.datas);
                            }
                        }
                    }
                });

            }

            var structureModel = createModel("structureModel", function () {
                Ext.define('structureModel', {
                    extend: 'Ext.data.TreeModel',
                    idProperty: 'id',
                    parentIdProperty: 'parentId',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'chartId', type: 'int'},
                        {name: 'metaName', type: 'string'},
                        {name: 'metaId', type: 'int'},
                        {name: 'metaType', type: 'int'},
                        {name: 'metaExt', type: 'string'},
                        {name: 'structureType', type: 'int'},
                        {name: 'parentId', type: 'int'},
                        {name: 'structureSort', type: 'int'},
                        {name: 'realNode', type: 'int'},
                        {name: 'leaf', type: 'boolean'}

                    ]
                });
            });
            var categoryStore = new Ext.data.TreeStore({
                model: 'structureModel',
                autoLoad: true,
                parentIdProperty: 'parentId',
                proxy: {
                    type: 'ajax',
                    extraParams: {structureType: ANALYSISCHART_INFO.TYPE_CATEGORY, chartId: chartId},
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
//                    url: GLOBAL_PATH + '/resourcecategory/analysis/common/analysis/getItemGroupTree',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryChartStructure',
                    api: {
//                        create: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartStructure',
                        update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartStructure',
                        destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delChartStructure'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                },
                root: {
                    expanded: true,
                    id: 0,
                    loaded: true,
                    structureType: ANALYSISCHART_INFO.TYPE_CATEGORY,
                    metaName: '分类轴',
                    children: []
                },
                listeners: {
                    load: function (store, records, successful, eOpts) {
                        var _this = store;
                        var root = records[0];
                        if (root) {
                            root.findChildBy(function (node) {
                                _this.byIdMap[node.getId()] = node;
                                return false;
                            }, true);
                        }
                    }
                }

            });
            var seriesStore = new Ext.data.TreeStore({
                model: 'structureModel',
                autoLoad: true,
                parentIdProperty: 'parentId',
                proxy: {
                    extraParams: {structureType: ANALYSISCHART_INFO.TYPE_SERIES, chartId: chartId},
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
//                    url: GLOBAL_PATH + '/resourcecategory/analysis/common/analysis/getItemGroupTree',
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryChartStructure',
                    api: {
//                     create: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartStructure',
                        update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartStructure',
                        destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delChartStructure'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                },
                root: {
                    expanded: true,
                    id: 0,
                    loaded: true,
                    metaName: '序列',
                    structureType: ANALYSISCHART_INFO.TYPE_SERIES,
                    children: []
                },
                listeners: {
                    load: function (store, records, successful, eOpts) {
                        var _this = store;
                        var root = records[0];
                        if (root) {
                            root.findChildBy(function (node) {
                                _this.byIdMap[node.getId()] = node;
                                return false;
                            }, true);
                        }
                    }
                }

            });
            var structureMenu = null;


            function refresh(record) {
                var structureType = record.get("structureType");
                if (structureType == STRUCTURE_TYPE.CATEGORY) {
                    structureCategory.getRootNode().findChildBy(function (tmpNode) {
                        tmpNode.set('structureType', STRUCTURE_TYPE.CATEGORY);
                        if (!tmpNode.hasChildNodes()) {
                            tmpNode.set('loaded', true);
                            tmpNode.set('leaf', true);
                        }
                        return false;
                    }, null, true);
                    categoryStore.sync({
                        success: function () {
                            infoCategoryStore.reload();
                        }
                    });
                }
                if (structureType == STRUCTURE_TYPE.SERIES) {
                    structureSeries.getRootNode().findChildBy(function (tmpNode) {
                        tmpNode.set('structureType', STRUCTURE_TYPE.SERIES);
                        if (!tmpNode.hasChildNodes()) {
                            tmpNode.set('loaded', true);
                            tmpNode.set('leaf', true);
                        }
                        return false;
                    }, null, true);
                    seriesStore.sync({
                        success: function () {
                            infoSeriesStore.reload();
                        }
                    });
                }
            }

            function itemcontextHandler(_this, record, item, index, e) {
                //阻止浏览器默认行为
                e.preventDefault();
                //右键菜单
                if (structureMenu) {
                    structureMenu.hide()
                }
                if (record.get("id") != 0) {
                    var nodeType = record.get("realNode");
                    var editType = NODE_TYPE.REAL;
                    var text = "设为实节点";
                    if (NODE_TYPE.REAL == record.get("realNode")) {
                        text = "设为虚节点";
                        editType = NODE_TYPE.EMPTY;
                    }
                    var isHidden = true;
                    if(record.get('metaType')==METADATA_TYPE.TIME||record.get('metaType')==METADATA_TYPE.ITEM){
                        isHidden = false;
                    }
                    structureMenu = new Ext.menu.Menu({
                        renderTo: Ext.getBody(),
                        items: [{
                            text: '修改',
                            name: "editInfo",
                            iconCls: 'Pageedit',
                            hidden:isHidden,
                            handler: function () {
                                if(record.get('metaType')==METADATA_TYPE.TIME){
                                    var win = Ext.addStructureTimeDataWin.init(fres, chartId, function (data) {
                                        var metaExt = {
                                            timeRange: data.timeRange,
                                            periodType: periodType
                                        };
                                        record.set("metaExt",Ext.encode(metaExt));
                                        refresh(record);
                                        setPropertyGrid(record);
                                        win.close();
                                    },record);
                                }
                                if(record.get('metaType')==METADATA_TYPE.ITEM){
                                    showAddStructureWindow(record, function (metaExt) {
                                    })
                                }
                            }

                        },{
                            text: '修改节点',
                            name: "editNode",
                            iconCls: 'Pageedit',
                            handler: function () {
                                record.set("realNode", editType);
                                refresh(record);
                                setPropertyGrid(record);
                            }

                        }, {
                            text: '删除节点',
                            iconCls: 'Delete',
                            handler: function () {
                                record.remove();
                                refresh(record);
                                setPropertyGrid(null);
                            }
                        }]
                    });
                    structureMenu.query('*[name=editNode]')[0].setText(text);
                    structureMenu.showAt(e.getPoint());
                }

            };
            var structureCategory = new Ext.tree.Panel({
                title: '分类轴结构',
                tbar: ['->', {
                    type: 'button',
                    text: '生成分类轴',
                    handler: function () {
                        createChartInfo(structureCategory.getRootNode());
                    }
                }],
                width: '100%',
                flex: 1,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop',
                        dragGroup: 'structure',
                        dropGroup: 'structure',
                        dragZone: {
                            afterDragOver: function (zone) {
                                var rec = zone.overRecord;
                                if (zone.curRecoder) {
                                    if (rec != zone.curRecoder) {
//                                    zone.curRecoder.reject();
                                        if (!zone.curRecoder.hasChildNodes()) {
                                            zone.curRecoder.set('leaf', true);
                                        }
                                    }
                                }
                                zone.curRecoder = rec;
                                if (rec) {
                                    rec.set('loaded', true);
                                    rec.set('leaf', false);
                                }
                            }
                        }
                    },
                    listeners: {
                        beforedrop: function (view, data, overModel, dropPosition, dropHandlers, eOpts) {
                            //TODO 添加节点a
                            var rec = data.records[0];
                            judgeMetaToCreateStructure(rec, overModel, dropPosition);
                            if (rec.get("dataType")) {
                                return false;
                            }

                        },
                        drop: function (node, data, overModel, dropPosition, eOpts) {
                            var dragNode = data.records[0];
                            var pNode = dragNode.parentNode;
                            dragNode.set('parentId', pNode.getId());
                            dragNode.set('structureType', STRUCTURE_TYPE.CATEGORY);
                            Ext.Array.each(pNode.childNodes, function (child) {
                                child.set('structureSort', child.data.index + 1);
                                child.set('structureType', STRUCTURE_TYPE.CATEGORY);
                            });
                            structureCategory.getRootNode().findChildBy(function (tmpNode) {
                                tmpNode.set('structureType', STRUCTURE_TYPE.CATEGORY);
                                if (!tmpNode.hasChildNodes()) {
                                    tmpNode.set('loaded', true);
                                    tmpNode.set('leaf', true);
                                }
                                return false;
                            }, null, true);
                            categoryStore.sync({
                                params: {drag: true}, success: function () {
                                    infoCategoryStore.reload();
                                }
                            });
                            structureSeries.getRootNode().findChildBy(function (tmpNode) {
                                tmpNode.set('structureType', STRUCTURE_TYPE.SERIES);
                                if (!tmpNode.hasChildNodes()) {
                                    tmpNode.set('loaded', true);
                                    tmpNode.set('leaf', true);
                                }
                                return false;
                            }, null, true);
                            seriesStore.sync({
                                params: {drag: true}, success: function () {
                                    infoSeriesStore.reload();
                                }
                            });
                            return false;
                        }
                    }
                },
                store: categoryStore,
                displayField: 'metaName',
                rootVisible: true,
                listeners: {
                    'itemclick': function (_this, record) {
                        if (structureMenu) {
                            structureMenu.hide();
                        }
                        setPropertyGrid(record);
                    },
                    itemcontextmenu: itemcontextHandler,
                    containerclick: function () {
                        if (structureMenu) {
                            structureMenu.hide();
                        }
                    }
                }
            });
            // 元数据类型
            function getMetaType(metaType) {
                if (metaType == 0) {
                    return "根节点";
                }
                for (var i = 0; i < metadataTypes.length; i++) {
                    if (metadataTypes[i].value == metaType) {
                        return metadataTypes[i].text;
                    }
                }
                return "";
            }

            // 节点类型
            function getNodeType(realNode) {
                if (realNode == NODE_TYPE.REAL) {
                    return NODE_TYPE.REAL_CH;
                } else {
                    return NODE_TYPE.EMPTY_CH;
                }
            }

            function getObjStr(type) {
                if (type == SUROBJ_TYPE.AREA) {
                    return SUROBJ_TYPE.AREA_CH;
                }
                if (type == SUROBJ_TYPE.OTHER) {
                    return SUROBJ_TYPE.OTHER_CH;
                }
                if (type == SUROBJ_TYPE.COMPANY) {
                    return SUROBJ_TYPE.COMPANY_CH;
                }
                return '';
            }

            function getMetaExt(record, metaType) {
                if (metaType == METADATA_TYPE.TIME) {
                    var metaExt = Ext.decode(record.get("metaExt"));
                    var metaExtStr = "";
                    if (metaExt.periodType) {
                        metaExtStr += "报送周期:" + PERIOD_TYPE.getCH(metaExt.periodType) + ";";
                    }
                    if (metaExt.timeRange) {
                        metaExtStr += "时间跨度:" + METADATA_TYPE.getTimeRange(metaExt.timeRange[0].type, metaExt.periodType, metaExt.timeRange);
                    }

                    return metaExtStr;
                }
                var metaExt = Ext.decode(record.get("metaExt"));
                var metaExtStr = "";
                if (metaExt.dep) {
                    metaExtStr += "部门:" + metaExt.depName + ";";
                }
                if (metaExt.caliber) {
                    metaExtStr += "口径:" + metaExt.caliberName + ";";
                }
                if (metaExt.rptTmp) {
                    metaExtStr += "报表:" + metaExt.rptName + ";";
                }
                if (metaExt.objType) {

                    metaExtStr += getObjStr(metaExt.objType) + ";"
                }

                return metaExtStr.substring(0, metaExtStr.length > 0 ? metaExtStr.length - 1 : 0);
            }

            function setPropertyGrid(record) {
                if (record) {

                    var metaType = record.get("metaType");
                    var metaTypeStr = getMetaType(metaType);
                    var realNode = record.get("realNode");
                    var realNodeStr = getNodeType(realNode);
                    if (record.get("metaExt")) {
                        var metaExtStr = getMetaExt(record, metaType);
                    }
                    var obj = {
                        "名称": record.get("metaName"),
                        "类型": metaTypeStr,
                        "节点类型": realNodeStr,
                        "扩展信息": metaExtStr
                    }
                    propertyGrid.setSource(obj);
                } else {
                    propertyGrid.setSource({});
                }

            }

            var structureSeries = new Ext.tree.Panel({
                title: '序列结构',
                tbar: ['->', {
                    type: 'button',
                    text: '生成序列',
                    handler: function () {
                        createChartInfo(structureSeries.getRootNode());
                    }
                }],
                width: '100%',
                flex: 1,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop',
                        dragGroup: 'structure',
                        dropGroup: 'structure',
                        dragZone: {
                            afterDragOver: function (zone) {
                                var rec = zone.overRecord;
                                if (zone.curRecoder) {
                                    if (rec != zone.curRecoder) {
//                                    zone.curRecoder.reject();
                                        if (!zone.curRecoder.hasChildNodes()) {
                                            zone.curRecoder.set('leaf', true);
                                        }
                                    }
                                }
                                zone.curRecoder = rec;
                                if (rec) {
                                    rec.set('loaded', true);
                                    rec.set('leaf', false);
                                }
                            }
                        }
                    },
                    listeners: {
                        beforedrop: function (view, data, overModel, dropPosition, dropHandlers, eOpts) {
                            var rec = data.records[0];
                            judgeMetaToCreateStructure(rec, overModel, dropPosition);
                            if (rec.get("dataType")) {
                                return false;
                            }
                        },
                        drop: function (node, data, overModel, dropPosition, eOpts) {
                            var dragNode = data.records[0];
                            var pNode = dragNode.parentNode;
                            dragNode.set('parentId', pNode.getId());
                            dragNode.set('structureType', STRUCTURE_TYPE.SERIES);
                            Ext.Array.each(pNode.childNodes, function (child) {
                                child.set('structureSort', child.data.index + 1);
                            });
                            structureSeries.getRootNode().findChildBy(function (tmpNode) {
                                tmpNode.set('structureType', STRUCTURE_TYPE.SERIES);
                                if (!tmpNode.hasChildNodes()) {
                                    tmpNode.set('loaded', true);
                                    tmpNode.set('leaf', true);
                                }
                                return false;
                            }, null, true);
                            seriesStore.sync({
                                params: {drag: true},
                                success: function () {
                                    infoSeriesStore.reload();
                                }
                            });
                            structureCategory.getRootNode().findChildBy(function (tmpNode) {
                                tmpNode.set('structureType', STRUCTURE_TYPE.CATEGORY);
                                if (!tmpNode.hasChildNodes()) {
                                    tmpNode.set('loaded', true);
                                    tmpNode.set('leaf', true);
                                }
                                return false;
                            }, null, true);
                            categoryStore.sync({
                                params: {drag: true},
                                success: function () {
                                    infoCategoryStore.reload();
                                }
                            });
                            return false;
                        }
                    }
                },
                store: seriesStore,
                displayField: 'metaName',
                rootVisible: true,
                listeners: {
                    'itemclick': function (_this, record) {
                        if (structureMenu) {
                            structureMenu.hide();
                        }
                        setPropertyGrid(record);
                    }

                    ,
                    itemcontextmenu: itemcontextHandler,
                    containerclick: function () {
                        if (structureMenu) {
                            structureMenu.hide();
                        }
                    }
                }
            });
            var structurePanel = new Ext.panel.Panel({
                title: '图表结构',
                region: 'center',
                width: '20%',
                height: '100%',
                border: 0,
                layout: 'vbox',
                items: [structureCategory, structureSeries]
            });
            var propertyGrid = Ext.create('Ext.grid.property.Grid', {
                width: "100%",
                height: '100%',
                border: 0,
                sortableColumns: false,
                disableSelection: true,
                shrinkWrap: false,// 滚动条
                listeners: {
                    beforecellclick: function (_this, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                        // 禁用编辑
                        return false;
                    }
                }
            });

            var nodeInfo = new Ext.panel.Panel({
                title: '节点信息',
                width: '50%',
                height: '100%',
                //autoScroll: true,
                items: [propertyGrid]
            });

            //-----------------------------------------------结构结束---------------------------------------------
            //-----------------------------------------------信息开始---------------------------------------------

            var chartInfoModel = createModel("chartInfoModel", function () {
                Ext.define('chartInfoModel', {
                    extend: 'Ext.data.Model',
                    idProperty: 'id',
                    fields: [
                        {name: 'id', type: 'int'},
                        {name: 'name', type: 'string'},
                        {name: 'chartId', type: 'int'},
                        {name: 'infoType', type: 'int'},
                        {name: 'chartType', type: 'int'},
                        {name: 'isShow', type: 'int'},
                        {name: 'axis', type: 'int'},
                        {name: 'info', type: 'string'},
                        {name: 'style', type: 'string'},
                        {name: 'group', type: 'string'},
                        {name: 'structureId', type: 'int'},
                        {name: 'infoSort', type: 'int'},
                        {name: 'metaType', type: 'int'}
                    ]
                });
            });

            var infoCategoryStore = new Ext.data.Store({
                model: 'chartInfoModel',
                autoLoad: true,
                proxy: {
                    extraParams: {infoType: ANALYSISCHART_INFO.TYPE_CATEGORY, chartId: chartId},
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryChartInfo',
                    api: {
                        update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartInfo',
                        destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delChartInfo'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                }
            });

            var infoSeriesStore = new Ext.data.Store({
                model: 'chartInfoModel',
                autoLoad: true,
                proxy: {
                    extraParams: {infoType: ANALYSISCHART_INFO.TYPE_SERIES, chartId: chartId},
                    type: 'ajax',
                    actionMethods: {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    url: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/queryChartInfo',
                    api: {
                        update: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/updateChartInfo',
                        destroy: GLOBAL_PATH + '/support/resourcecategory/analysis/chart/delChartInfo'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'datas'
                    }
                }
            });

            var infoCategory = new Ext.grid.Panel({
                title: '分类轴',
                width: '50%',
                sortableColumns: false,
                multiSelect: true,
                allowDeselect: true,
                viewConfig: {
                    plugins: {
                        ptype: 'gridviewdragdrop'
                    },
                    listeners: {
                        drop: function (node, data, overModel, dropPosition, eOpts) {
                            var datas = infoCategoryStore.getData();
                            datas.each(function (data, index) {
                                data.set('infoSort', index);
                            });
                            infoCategoryStore.sync();
                        }
                    }
                },
                tbar: ['->', {
                    text: '修改分类轴',
                    handler: function () {
                        var selected = infoCategory.getSelection();
                        if (selected && selected.length > 0) {
                            Ext.fillChartInfoWin.init(function (data) {
                                var _data = data;
                                Ext.Array.each(selected, function (rec) {
                                    rec.set(_data);
                                });
                                infoCategoryStore.sync();
                            }, selected, ANALYSISCHART_INFO.TYPE_CATEGORY);
                        }
                    }
                }, {
                    text: '删除分类轴',
                    handler: function () {
                        Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                            if (btn == 'yes') {
                                var selected = infoCategory.getSelection();
                                if (selected && selected.length > 0) {
                                    infoCategoryStore.remove(selected);
                                    infoCategoryStore.sync();
                                }
                            }
                        });
                    }
                }],
                columns: [
                    {text: '名称', dataIndex: 'name', flex: 1},
                    /*{text: '样式', dataIndex: 'style', flex: 0.5},*/
                    {text: '说明', dataIndex: 'info', flex: 0.5}
                ],
                store: infoCategoryStore,
                height: '100%'
            });
            var infoTop = new Ext.panel.Panel({
                title: '图表信息Top',
                width: '100%',
                border: 0,
                layout: 'hbox',
                flex: 1,
                items: [nodeInfo, infoCategory]
            });
            var infoSeries = new Ext.grid.Panel({
                title: '序列',
                sortableColumns: false,
                store: infoSeriesStore,
                multiSelect: true,
                allowDeselect: true,
                viewConfig: {
                    plugins: {
                        ptype: 'gridviewdragdrop'
                    },
                    listeners: {
                        drop: function (node, data, overModel, dropPosition, eOpts) {
                            var datas = infoSeriesStore.getData();
                            datas.each(function (data, index) {
                                data.set('infoSort', index);
                            });
                            infoSeriesStore.sync();
                        }
                    }
                },
                width: '100%',
                flex: 1,
                tbar: ['->', {
                    text: '修改序列',
                    handler: function () {
                        var selected = infoSeries.getSelection();
                        if (selected && selected.length > 0) {
                            Ext.fillChartInfoWin.init(function (data) {
                                var _data = data;
                                Ext.Array.each(selected, function (rec) {
                                    rec.set(_data);
                                });
                                infoSeriesStore.sync();
                            }, selected, ANALYSISCHART_INFO.TYPE_SERIES);
                        }
                    }
                }, {
                    text: '删除序列',
                    handler: function () {
                        Ext.Msg.confirm('警告', '确定要删除么?', function (btn) {
                            if (btn == 'yes') {
                                var selected = infoSeries.getSelection();
                                if (selected && selected.length > 0) {
                                    infoSeriesStore.remove(selected);
                                    infoSeriesStore.sync();
                                }
                            }
                        });
                    }
                }],
                columns: [
                    {text: '名称', dataIndex: 'name', flex: 1},
                    {
                        text: '图表', dataIndex: 'chartType', renderer: function (value) {
                        return ANALYSISCHART_INFO.getChartTypeCH(value)
                    }, flex: 0.2
                    },
                    {
                        text: '坐标轴', dataIndex: 'axis', renderer: function (value) {
                        return ANALYSISCHART_INFO.getChartAxisTypeCH(value);
                    }, flex: 0.2
                    },
                    {text: '分组', dataIndex: 'group', flex: 0.2},
                    {
                        text: '显示', dataIndex: 'isShow', renderer: function (value) {
                        return ANALYSISCHART_INFO.getSeriesShowCH(value);
                    }, flex: 0.2
                    },
                    /*{text: '样式', dataIndex: 'style', flex: 0.2},*/
                    {text: '说明', dataIndex: 'info', flex: 0.4}
                ]
            });


            var infoPanel = new Ext.panel.Panel({
                title: '图表信息',
                region: 'east',
                width: '60%',
                height: '100%',
                layout: 'vbox',
                border: 0,
                items: [infoTop, infoSeries]
            });
            //-----------------------------------------------信息结束---------------------------------------------
            //-----------------------------------------------主页面板开始---------------------------------------------
            var mainPanel = new Ext.panel.Panel({
                title: '图表设计',
                html: 'ss',
                height: '100%',
                layout: 'border',
                border: 0,
                items: [metaPanel, structurePanel, infoPanel],
                buttons: [{
                    text: '预览图表',
                    handler: function () {
//                        Ext.fillChartInfoWin.init();
                        createChartInfo(structureCategory.getRootNode());
                        createChartInfo(structureSeries.getRootNode());
                        open(GLOBAL_PATH + '/support/resourcecategory/analysis/chart/test?chartId=' + chartId);
                    }
                }]

            });
            var viewport = new Ext.container.Viewport({
                layout: 'fit',
                items: mainPanel
            });
            //-----------------------------------------------主页面板结束---------------------------------------------

        });

    </script>
</head>
<body style="height: 100%">

</body>
</html>
