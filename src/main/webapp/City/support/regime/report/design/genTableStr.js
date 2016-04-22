/**
 * Created by wxl on 2016/1/26 0026.
 */
/**
 * 获取主宾蓝的节点信息
 * @param mainBarTree
 * @param guestBarTree
 * @param mainList 主栏叶子节点存储列表
 * @param guestList 宾栏叶子节点存储列表
 * @returns {{mainBar: Array, guestBar: Array}}
 */
function getBarNodes(mainBarTree, guestBarTree, mainList, guestList) {
    return {
        mainBar: getTreeNodes(mainBarTree.getRootNode(), null, mainList),
        guestBar: getTreeNodes(guestBarTree.getRootNode(), null, guestList),
        tmpId: style.reportTemplate.id,
        unitSelector: tdUnitSelector
    };
}

/**
 * 获取表样
 */
function genTableStr(mainBarTree, guestBarTree, fn) {
    var mainList = [];
    var guestList = [];
    //表格主宾蓝信息
    var data = getBarNodes(mainBarTree, guestBarTree, mainList, guestList);
    //校验
    var valid = checkGridValid(mainList, guestList);
    if (!valid.success) {//校验失败
        fn({success: false, msg: '生成表样失败,失败原因:<br><b style="color:red">' + valid.msg + '</b>'});
        return;
    }
    //校验成功,继续
    //发送请求,解析表样
    var myMask = new Ext.LoadMask({
        msg: '正在生成表样,请稍候!',
        target: Ext.getCmp('tableRenderPanel')
    });
    myMask.show();
    Ext.Ajax.request({
        url: contextPath + '/support/regime/report/designReport/genTableStr',
        method: 'POST',
        params: {
            data: JSON.stringify(data)
        },
        success: function (response) {
            myMask.hide();
            fn(Ext.decode(response.responseText));
        },
        failure: function (response, opts) {
            myMask.hide();
            fn({success: false, msg: '生成表样失败'});
        }
    });
}
/**
 * 保存主宾蓝信息
 */
function saveBarInfos(mainBarTree, guestBarTree, fn) {
    var mainList = [];
    var guestList = [];
    //表格主宾蓝信息
    var data = getBarNodes(mainBarTree, guestBarTree, mainList, guestList);
    //校验
    var valid = checkGridValid(mainList, guestList);
    if (!valid.success) {//校验失败
        fn({success: false, msg: '校验失败,失败原因:<br><b style="color:red">' + valid.msg + '</b>'});
        return;
    }
    //校验成功,继续
    //发送请求,保存主宾栏
    var myMask = new Ext.LoadMask({
        msg: '正在保存主宾栏信息,请稍候!',
        target: Ext.getCmp('tableRenderPanel')
    });
    myMask.show();
    Ext.Ajax.request({
        url: contextPath + '/support/regime/report/designReport/saveBarInfo',
        method: 'POST',
        params: {
            data: JSON.stringify(data),
            styleId: style.id
        },
        success: function (response) {
            myMask.hide();
            fn(Ext.decode(response.responseText));
        },
        failure: function (response, opts) {
            myMask.hide();
            fn({success: false, msg: '保存失败'});
        }
    });
}
/**
 * 检查是否合格
 * @param mainList
 * @param guestList
 */
function checkGridValid(mainList, guestList) {
    if (mainList.length == 0 || guestList.length == 0) {
        return {success: false, msg: '主栏或宾栏无数据'};
    }
    //校验数据
    var props = [];
    for (var i = 0; i < mainList.length; i++) {
        var mainProp = mainList[i].properties;
        for (var j = 0; j < guestList.length; j++) {
            props.push(mainProp.concat(guestList[j].properties));
        }
    }
    var reasons = [];
    for (var i = 0; i < props.length; i++) {
        var checkResult = checkProps(props[i]);
        if (!checkResult.success) {
            reasons = reasons.concat(checkResult.reasons);
        }
    }
    if (reasons.length == 0) {
        return {success: true};
    }
    var repReason = unique(reasons);
    return {success: false, msg: repReason.join('；')};
}

/**
 * 校验每个单元格的属性
 */
function checkProps(props) {
    var itemLen = 0, itemGroupLen = 0, tfLen = 0, surObjLen = 0, surObjGroupLen = 0;
    for (var i = 0; i < props.length; i++) {
        var dataType = parseInt(props[i].dataType);
        switch (dataType) {
            case METADATA_TYPE.ITEM:
                itemLen++;
                break;
            case METADATA_TYPE.ITEM_GROUP:
                itemGroupLen++;
                break;
            case METADATA_TYPE.TIME_FRAME:
                tfLen++;
                break;
            case METADATA_TYPE.RESEARCH_OBJ:
                surObjLen++;
                break;
            case METADATA_TYPE.RESEARCH_OBJ_GROUP:
                surObjGroupLen++;
                break;
        }
    }
    var reasons = [];
    //原因:指标
    if (itemLen == 0) {
        reasons.push('指标缺失');
    } else if (itemLen > 1) {
        reasons.push('单元格中指标多余一个');
    }
    //原因:时间框架
    if (tfLen == 0) {
        reasons.push('时间框架缺失');
    } else if (tfLen > 1) {
        reasons.push('单元格中时间框架多余一个');
    }
    //原因:指标体系分组
    if (itemGroupLen > 1) {
        reasons.push('单元格中指标体系分组至多一个');
    }
    //原因:调查对象
    if (surObjLen > 1) {
        reasons.push('单元格中调查对象至多一个');
    }
    //调查对象分组
    if (surObjGroupLen > 1) {
        reasons.push('单元格中调查对象分组至多一个');
    }
    return {success: reasons.length == 0, reasons: reasons};
}
//数组去重复
function unique(arr) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem]) {
            result.push(elem);
            hash[elem] = true;
        }
    }
    return result;
}

/**
 * 节点对象
 */
function EsiNode(nodeData) {
    this.dataName = nodeData.dataName;
    this.dataType = nodeData.dataType;
    this.isRealNode = !!nodeData.isRealNode;
    this.properties = getProps(nodeData.properties, false);
    this.depth = nodeData.depth;
    this.leaf = true;
}
//设置子级
EsiNode.prototype.setChildren = function (children) {
    if (children && children.length) {
        this.leaf = false;
        this.children = children;
    } else {
        this.leaf = true;
        this.children = null;
    }
};
//添加父级的属性
EsiNode.prototype.addParentProps = function (pProps) {
    this.properties = this.properties.concat(getProps(pProps, true));
};
/**
 * 获取属性
 * @param props 属性源
 * @param extend 是否为继承的属性
 * @returns {Array} 生成的属性
 */
function getProps(props, extend) {
    var properties = [];
    if (props && props.length) {
        for (var i = 0; i < props.length; i++) {
            var prop = props[i];
            properties.push({
                dataType: prop.dataType,
                dataName: prop.dataName,
                dataValue: prop.dataValue,
                dataInfo1: prop.dataInfo1,
                dataInfo2: prop.dataInfo2,
                extend: extend
            });
        }
    }
    return properties;
}

/**
 * 获取树节点
 * @param node 要便利的节点
 * @param list 要添加子节点的list
 * @return {Array}
 */
function getTreeNodes(node, pEsiNode, list) {
    var nodes = null;
    var childNodes = node.childNodes;
    if (childNodes && childNodes.length) {
        nodes = [];
        for (var i = 0; i < childNodes.length; i++) {
            var child = childNodes[i];
            var esiNode = new EsiNode(child.data);
            //将父级的属性也加上去
            if (pEsiNode) {
                esiNode.addParentProps(pEsiNode.properties);
            } else {
                esiNode.addParentProps(node.get('properties'));
            }
            //设置子集
            esiNode.setChildren(arguments.callee(child, esiNode, list));
            nodes.push(esiNode);
            //将子节点添加到列表中
            if (esiNode.leaf) {
                list.push(esiNode);
            }
        }
    }
    return nodes;
}
