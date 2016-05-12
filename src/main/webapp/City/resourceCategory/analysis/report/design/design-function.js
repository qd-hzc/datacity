/**
 * Created by wxl on 2016/1/26 0026.
 */
/**
 * 节点对象
 */
function EsiNode(nodeData) {
    this.dataName = nodeData.dataName;
    this.dataType = nodeData.dataType;
    this.isRealNode = !!nodeData.isRealNode;
    this.properties = RESEARCHHZC.getProps(nodeData.properties, false);
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
    this.properties = this.properties.concat(RESEARCHHZC.getProps(pProps, true));
};
var RESEARCHHZC = {
    /**
     * 生成表样
     */
    genTableStr: function (mainBarTree, guestBarTree, fo, fn) {
        var mainList = [];
        var guestList = [];
        //表格主宾蓝信息
        var data = RESEARCHHZC.getBarNodes(fo, mainBarTree, guestBarTree, mainList, guestList);
        //校验
        var valid = RESEARCHHZC.checkGridValid(mainList, guestList);
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
            url: contextPath + '/resourcecategory/analysis/report/designCustomResearch/genTableStr',
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
    },
    /**
     * 保存主宾蓝信息
     */
    saveBarInfos: function (fo, mainBarTree, guestBarTree, fn) {
        var mainList = [];
        var guestList = [];
        //表格主宾蓝信息
        var data = RESEARCHHZC.getBarNodesForSaveZhuBin(fo, mainBarTree, guestBarTree, mainList, guestList);

        var realDate = RESEARCHHZC.getBarNodes(fo, mainBarTree, guestBarTree, [], []);
        //校验
        var valid = RESEARCHHZC.checkGridValid(mainList, guestList);
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
            url: contextPath + '/resourcecategory/analysis/report/designCustomResearch/saveBarInfo',
            method: 'POST',
            params: {
                data: JSON.stringify(data),
                styleId: fo.researchId,
                realDate: JSON.stringify(realDate)
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
    },
    /**
     * 检查是否合格
     * @param mainList
     * @param guestList
     */
    checkGridValid: function (mainList, guestList) {
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
            var checkResult = RESEARCHHZC.checkProps(props[i]);
            if (!checkResult.success) {
                reasons = reasons.concat(checkResult.reasons);
            }
        }
        if (reasons.length == 0) {
            return {success: true};
        }
        var repReason = this.unique(reasons);
        return {success: false, msg: repReason.join('；')};
    },
    /**
     * 校验每个单元格的属性
     */
    checkProps: function (props) {
        var itemLen = 0, itemGroupLen = 0, tfLen = 0, surObjLen = 0, surObjGroupLen = 0,
            timeLen = 0;
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
                case METADATA_TYPE.TIME:
                    timeLen++;
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
        //时间
        if (timeLen == 0) {
            reasons.push('时间缺失');
        }
        return {success: reasons.length == 0, reasons: reasons};
    },
//数组去重复
    unique: function (arr) {
        var result = [], hash = {};
        for (var i = 0, elem; (elem = arr[i]) != null; i++) {
            if (!hash[elem]) {
                result.push(elem);
                hash[elem] = true;
            }
        }
        return result;
    },


    /**
     * 获取属性
     * @param props 属性源
     * @param extend 是否为继承的属性
     * @returns {Array} 生成的属性
     */
    getProps: function (props, extend) {
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
    },
    /**
     * 获取属性,返回list
     * @param props 属性源
     * @param extend 是否为继承的属性
     * @returns {Array} 生成的属性
     */
    getPropsList: function (props, extend) {
        var properties = [];
        if (props && props.length) {
            for (var i = 0; i < props.length; i++) {
                var prop = props[i];
                if (prop.dataValue == 0 && prop.dataType == 666) {
                    continue;
                }
                properties.push([
                    prop.dataName,
                    prop.dataValue,
                    prop.dataType,
                    prop.dataInfo1,
                    prop.dataInfo2,
                    extend
                ]);
            }
        }
        return properties;
    },

    /**
     * 获取树节点
     * @param node 要便利的节点
     * @param list 要添加子节点的list
     * @return {Array}
     */
    getTreeNodes: function (node, pEsiNode, list) {
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
                esiNode.setChildren(arguments.callee(child, esiNode, list));
                nodes.push(esiNode);
                //将子节点添加到列表中
                if (esiNode.leaf) {
                    list.push(esiNode);
                }
            }
        }
        return nodes;
    },
    /**
     * 生成新的树节点
     * <pre>
     *      1：做什么？复制整棵树，替换树中的时间节点为具体时间
     *      2：原理：自定义查询生成表格：选中时间频度和时间范围，在主栏或者宾栏中添加时间占位，在生成时间结构时，直接复制时间树，
     *      然后将时间节点替换为具体时间（年，期度），生成一棵新的、替换掉时间占位变为具体时间的、Ext.tree.Panel对象，
     *      将整棵树返回
     *
     *      生成的时间节点属性说明：
     *      dataName:时间名称：2016，上半年，一季度，一月。如果为报告期数，则为 $y$ $y-1$ $m$ $m-1$
     *      dataValue：时间值：2016,6,3,1。如果为报告期数，则为$y$ $y-1$ $m$ $m-1$
     *      dataInfo1：记录dataValue的类型：年、月
     *      dataInfo2：记录时间范围：timeRange：连续，选择，报告期数
     *      properties：记录时间属性值：如果为报告期，年报：两个properties：一个是年的，一个是固定值12的；非年报：年单独一个节点，期度单独一个节点
     * </pre>
     * @param fo 包含时间频度和时间范围的对象
     * @param node 要遍历的tree
     * @author hzc
     * @createDate 2016-3-1
     */
    getTreeNodesForCreate: function (fo, node) {
        //时间范围
        var _tr = fo.timeRange.getValue().timeRange;
        //时间频度
        var _tf = fo.timeFrequency.getValue().frequency;
        //开始年
        var _by = null;
        //开始期度
        var _bp = null;
        //结束年
        var _ey = null;
        //结束期度
        var _ep = null;
        //报告期数
        var _bq = null;
        switch (_tr) {
            case '1':
                _by = fo.lianxuBeginYearCombobox.getValue();
                _ey = fo.lianxuEndYearCombobox.getValue();
                if (_tf != '1') {
                    _bp = fo.lianxuBeginPeriodCombobox.getValue();
                    _ep = fo.lianxuEndPeriodCombobox.getValue();
                }
                break;
            case '2':
                _by = fo.xuanzeYearMultyCombobox.getValue();
                //如果年份为正序，则修改该排序即可
                RESEARCHHZC.sortListDesc(_by);
                if (_tf != '1') {
                    _ep = fo.xuanzePeriodMultyCombobox.getValue();
                    //    如果期度为正序，则修改此排序
                    RESEARCHHZC.sortListDesc(_ep);
                }
                break;
            case '3':
                _bq = fo.baogaoqi.getValue();
                break;
        }
        //复制新节点
        var newNode = node;
        var childNodes = newNode.childNodes;
        if (childNodes && childNodes.length > 0) {
            for (var i = 0; i < childNodes.length; i++) {
                //遍历所有子节点，替换时间占位为具体时间
                var child = childNodes[i];
                var child_data = child.data;
                var data_type = child_data.properties[0].dataType;
                if (data_type == (METADATA_TYPE.TIME + '') && !child_data.copy) {
                    //    如果是该节点为时间类型节点
                    //    处理时间节点，根据时间频度和时间范围，拼装时间节点
                    //    将时间节点的子节点复制给每个具体时间节点
                    var dataType = child_data.dataType;
                    //时间节点的属性
                    var timeProperties = RESEARCHHZC.getPropsList(child_data.properties, true);
                    switch (_tf) {
                        case '1'://时间频度：年
                            switch (_tr) {
                                case '1'://时间范围：连续
                                    var _byi = parseInt(_by);
                                    var _eyi = parseInt(_ey);
                                    //如果年份为正序，则使用'for (var a = 0; a < (_eyi - _byi + 1); a++) {'替换即可
                                    for (var a = (_eyi - _byi); a > -1; a--) {
                                        var np = RESEARCHHZC.createPropertiesObj([[_byi + a, _byi + a, METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true],
                                            [FREQUENCY_TYPE.YEAR, FREQUENCY_TYPE.YEAR, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]
                                        ], timeProperties);
                                        //node
                                        var children = RESEARCHHZC.childNodesCopy(child);
                                        var nd = RESEARCHHZC.createNodeObj([_byi + a, _byi + a, dataType, METADATA_TYPE.INFO_YEAR, _tr, np, true, children]);
                                        //ext node
                                        var end = newNode.createNode(nd);
                                        end.appendChild(children);
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '2'://时间范围：选择
                                    for (var b = 0; b < _by.length; b++) {
                                        var np = RESEARCHHZC.createPropertiesObj([[_by[b], _by[b], METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true],
                                            [FREQUENCY_TYPE.YEAR, FREQUENCY_TYPE.YEAR, METADATA_TYPE.TIME,
                                                METADATA_TYPE.INFO_PERIOD, _tr, true]
                                        ], timeProperties);
                                        var children = RESEARCHHZC.childNodesCopy(child);
                                        var nd = RESEARCHHZC.createNodeObj([_by[b], _by[b], data_type, METADATA_TYPE.INFO_YEAR, _tr, np, true, children]);
                                        var end = newNode.createNode(nd);
                                        end.appendChild(children);
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '3'://时间范围：报告期数
                                    for (var c = 0; c < _bq; c++) {
                                        var children = RESEARCHHZC.childNodesCopy(child);
                                        var yn = '';
                                        if (c != 0) {
                                            yn = '-' + c;
                                        }
                                        var np = RESEARCHHZC.createPropertiesObj([['$y' + yn + '$', , METADATA_TYPE.TIME,
                                                METADATA_TYPE.INFO_YEAR, _tr, true],
                                                [FREQUENCY_TYPE.YEAR, ,
                                                    METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                            timeProperties)
                                        var nd = RESEARCHHZC.createNodeObj(['$y' + yn + '$', , dataType, METADATA_TYPE.INFO_YEAR,
                                            _tr, np, true, children]);
                                        var end = newNode.createNode(nd);
                                        end.appendChild(children);
                                        newNode.appendChild(end);
                                    }
                                    break;
                            }
                            break;
                        case '2'://时间频度：半年
                            switch (_tr) {
                                case '1'://时间范围：连续
                                    var _byi = parseInt(_by);
                                    var _eyi = parseInt(_ey);
                                    var _bpi = parseInt(_bp);
                                    var _epi = parseInt(_ep);
                                    //如果年份为正序，则使用'for (var a = 0; a < (_eyi - _byi + 1); a++) {'替换即可
                                    var cha = _eyi - _byi;
                                    for (var a = cha; a > -1; a--) {
                                        var np = RESEARCHHZC.createPropertiesObj([[_byi + a, _byi + a, METADATA_TYPE.TIME,
                                            METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        //node
                                        var childrenUp = RESEARCHHZC.childNodesCopy(child);
                                        var childrenDown = RESEARCHHZC.childNodesCopy(child);
                                        var nd = RESEARCHHZC.createNodeObj([_byi + a, _byi + a, dataType, METADATA_TYPE.INFO_YEAR, _tr, np, true, null]);
                                        //ext node
                                        //1、创建年node
                                        //2、创建期度node
                                        var end = newNode.createNode(nd);

                                        if (cha == 0) {
                                            //同年
                                            var halfYearNp;
                                            var halfYearNd;

                                            if (_bpi == FREQUENCY_TYPE.HALF_UP) {

                                                if (_bpi != _epi) {
                                                    //同年，并且有下半年
                                                    var downYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                        FREQUENCY_TYPE.HALF_DOWN, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD,
                                                        _tr, true]], timeProperties);
                                                    var downYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                        FREQUENCY_TYPE.HALF_DOWN, dataType, METADATA_TYPE.INFO_PERIOD, _tr, downYearNp, true, childrenDown]);
                                                    var down = end.createNode(downYearNd);
                                                    down.appendChild(childrenDown);
                                                    //如果期度为正序，在修改append顺序即可
                                                    end.appendChild(down);
                                                }

                                                var upYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_UP_STRING,
                                                    FREQUENCY_TYPE.HALF_UP, METADATA_TYPE.TIME,
                                                    METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                var upYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_UP_STRING,
                                                    FREQUENCY_TYPE.HALF_UP, dataType, METADATA_TYPE.INFO_PERIOD, _tr, upYearNp, true, childrenUp]);
                                                var up = end.createNode(upYearNd);
                                                up.appendChild(childrenUp);
                                                end.appendChild(up);

                                            } else {
                                                halfYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                        FREQUENCY_TYPE.HALF_DOWN, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                    timeProperties);
                                                halfYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                    FREQUENCY_TYPE.HALF_DOWN, dataType, METADATA_TYPE.INFO_PERIOD, _tr, halfYearNp, true, childrenDown]);
                                                var halfYear = end.createNode(halfYearNd)
                                                halfYear.appendChild(childrenDown);
                                                end.appendChild(halfYear);
                                            }
                                        } else {
                                            switch (a) {
                                                case 0://开始年
                                                    var halfYearNp;
                                                    var halfYearNd;
                                                    if (_bpi == FREQUENCY_TYPE.HALF_UP) {
                                                        //年节点下有上半年、下半年两个期度节点
                                                        var upYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_UP_STRING,
                                                            FREQUENCY_TYPE.HALF_UP, METADATA_TYPE.TIME,
                                                            METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                        var downYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                            FREQUENCY_TYPE.HALF_DOWN, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD,
                                                            _tr, true]], timeProperties);
                                                        var upYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_UP_STRING,
                                                            FREQUENCY_TYPE.HALF_UP, dataType, METADATA_TYPE.INFO_PERIOD, _tr, upYearNp, true, childrenUp]);
                                                        var downYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                            FREQUENCY_TYPE.HALF_DOWN, dataType, METADATA_TYPE.INFO_PERIOD, _tr, downYearNp, true, childrenDown]);
                                                        var up = end.createNode(upYearNd);
                                                        var down = end.createNode(downYearNd);
                                                        up.appendChild(childrenUp);
                                                        down.appendChild(childrenDown);
                                                        //如果期度为正序，在修改append顺序即可
                                                        end.appendChild(down);
                                                        end.appendChild(up);
                                                    } else {
                                                        halfYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                                FREQUENCY_TYPE.HALF_DOWN, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                            timeProperties);
                                                        halfYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                            FREQUENCY_TYPE.HALF_DOWN, dataType, METADATA_TYPE.INFO_PERIOD, _tr, halfYearNp, true, childrenDown]);
                                                        var halfYear = end.createNode(halfYearNd)
                                                        halfYear.appendChild(childrenDown);
                                                        end.appendChild(halfYear);
                                                    }
                                                    break;
                                                case _eyi - _byi://结束年
                                                    var halfYearNp;
                                                    var halfYearNd;
                                                    if (_epi == FREQUENCY_TYPE.HALF_DOWN) {
                                                        //年节点下有上半年、下半年两个期度节点
                                                        var upYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_UP_STRING,
                                                                FREQUENCY_TYPE.HALF_UP, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                            timeProperties);
                                                        var downYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                                FREQUENCY_TYPE.HALF_DOWN, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                            timeProperties);
                                                        var upYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_UP_STRING,
                                                            FREQUENCY_TYPE.HALF_UP, dataType, METADATA_TYPE.INFO_PERIOD, _tr, upYearNp, true, childrenUp]);
                                                        var downYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                            FREQUENCY_TYPE.HALF_DOWN, dataType, METADATA_TYPE.INFO_PERIOD, _tr, downYearNp, true, childrenDown]);
                                                        var up = end.createNode(upYearNd);
                                                        var down = end.createNode(downYearNd);
                                                        up.appendChild(childrenUp);
                                                        down.appendChild(childrenDown);
                                                        //如果期度为正序，在修改append顺序即可
                                                        end.appendChild(down);
                                                        end.appendChild(up);
                                                    } else {
                                                        halfYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_UP_STRING,
                                                                FREQUENCY_TYPE.HALF_UP, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                            timeProperties);
                                                        halfYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_UP_STRING,
                                                            FREQUENCY_TYPE.HALF_UP, dataType, METADATA_TYPE.INFO_PERIOD, _tr, halfYearNp, true, childrenUp]);
                                                        var halfYear = end.createNode(halfYearNd);
                                                        halfYear.appendChild(childrenUp);
                                                        end.appendChild(halfYear);
                                                    }
                                                    break;
                                                default ://中间年
                                                    //年节点下有上半年、下半年两个期度节点
                                                    var upYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_UP_STRING,
                                                            FREQUENCY_TYPE.HALF_UP, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                        timeProperties);
                                                    var downYearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                            FREQUENCY_TYPE.HALF_DOWN, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                        timeProperties);
                                                    var upYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_UP_STRING,
                                                        FREQUENCY_TYPE.HALF_UP, dataType, METADATA_TYPE.INFO_PERIOD, _tr, upYearNp, true, childrenUp]);
                                                    var downYearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.HALF_DOWN_STRING,
                                                        FREQUENCY_TYPE.HALF_DOWN, dataType, METADATA_TYPE.INFO_PERIOD, _tr, downYearNp, true, childrenDown]);
                                                    var up = end.createNode(upYearNd);
                                                    var down = end.createNode(downYearNd);
                                                    up.appendChild(childrenUp);
                                                    down.appendChild(childrenDown);
                                                    //如果期度为正序，在修改append顺序即可
                                                    end.appendChild(down);
                                                    end.appendChild(up);
                                                    break;
                                            }
                                        }
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '2'://时间范围：选择
                                    for (var b = 0; b < _by.length; b++) {
                                        var y = _by[b];
                                        var np = RESEARCHHZC.createPropertiesObj([[y, y, METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        //node
                                        var nd = RESEARCHHZC.createNodeObj([y, y, dataType, METADATA_TYPE.INFO_YEAR, _tr, np, true, null]);
                                        //ext node
                                        //1、创建年node
                                        //2、创建期度node
                                        var end = newNode.createNode(nd);
                                        for (var c = 0; c < _ep.length; c++) {
                                            var children = RESEARCHHZC.childNodesCopy(child);
                                            var p = parseInt(_ep[c]);
                                            var yearNp = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(2, p),
                                                    p, METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]],
                                                timeProperties);
                                            var yearNd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(2, p),
                                                p, dataType, METADATA_TYPE.INFO_PERIOD, _tr, yearNp, true, children]);
                                            var n = end.createNode(yearNd);
                                            n.appendChild(children);
                                            end.appendChild(n);
                                        }
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '3'://时间范围：报告期数
                                    for (var c = 0; c < _bq; c++) {
                                        var children = RESEARCHHZC.childNodesCopy(child);
                                        var yn = '';
                                        if (c != 0) {
                                            yn = '-' + c;
                                        }
                                        var yearNp = RESEARCHHZC.createPropertiesObj([['$y$', , METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        var yearNd = RESEARCHHZC.createNodeObj(['$y$', , dataType, METADATA_TYPE.INFO_YEAR,
                                            _tr, yearNp, true, null]);
                                        //生成年节点
                                        var end = newNode.createNode(yearNd);
                                        //生成期度节点
                                        var periodNp = RESEARCHHZC.createPropertiesObj([['$m' + yn + '$', , METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                        var periodNd = RESEARCHHZC.createNodeObj(['$m' + yn + '$', , dataType, METADATA_TYPE.INFO_PERIOD, _tr, periodNp, true, children]);

                                        var pend = end.createNode(periodNd);
                                        pend.appendChild(children);
                                        end.appendChild(pend);
                                        newNode.appendChild(end);
                                    }
                                    break;
                            }
                            break;
                        case '3'://时间频度：季
                            switch (_tr) {
                                case '1'://连续
                                    var _byi = parseInt(_by);
                                    var _eyi = parseInt(_ey);
                                    //如果年份为正序，则使用'for (var a = 0; a < (_eyi - _byi + 1); a++) {'替换即可
                                    var cha = _eyi - _byi;
                                    for (var a = cha; a > -1; a--) {
                                        var npp = RESEARCHHZC.createPropertiesObj([[_byi + a, _byi + a, METADATA_TYPE.TIME,
                                            METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        //node:每个时间节点下的内容都是相同的，所以有多少时间节点就要复制多少个
                                        var children1 = RESEARCHHZC.childNodesCopy(child);
                                        var children2 = RESEARCHHZC.childNodesCopy(child);
                                        var children3 = RESEARCHHZC.childNodesCopy(child);
                                        var children4 = RESEARCHHZC.childNodesCopy(child);
                                        var children = [children1, children2, children3, children4];
                                        var ndp = RESEARCHHZC.createNodeObj([_byi + a, _byi + a, dataType,
                                            METADATA_TYPE.INFO_YEAR, _tr, npp, true, null]);
                                        //ext node
                                        //1、创建年node
                                        var end = newNode.createNode(ndp);
                                        //2、创建期度node

                                        /**
                                         * 生成季度下的期度节点
                                         * @param num 从哪个季度开始
                                         * @param endP 到哪个季度结束
                                         */
                                        function generateQuarterNode(num, endP) {
                                            var list = [];
                                            endP || (endP = 12);
                                            for (var abc = num, bc = 0; abc < endP + 1; abc = abc + 3, bc++) {
                                                var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(3, abc), abc,
                                                    METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(3, abc), abc, dataType,
                                                    METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[bc]]);
                                                var pq = end.createNode(nd);
                                                pq.appendChild(children[bc]);
                                                list.push(pq);
                                            }
                                            //如果期度为正序，则修改此排序即可
                                            RESEARCHHZC.sortListDesc(list);
                                            Ext.Array.each(list, function (r) {
                                                end.appendChild(r);
                                            });
                                        }

                                        /**
                                         * 生成季度下的期度节点
                                         * @param num 到哪个季度结束
                                         */
                                        function generateQuarterNodeDesc(num) {
                                            var arr = [];
                                            for (var abc = num, bc = 0; abc > 2; abc = abc - 3, bc++) {
                                                var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(3, abc), abc,
                                                    METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(3, abc), abc, dataType,
                                                    METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[bc]]);
                                                var pq = end.createNode(nd);
                                                pq.appendChild(children[bc]);
                                                arr.push(pq);
                                            }
                                            //如果期度为正序，则修改此排序即可
                                            //RESEARCHHZC.sortListDesc(arr);
                                            Ext.Array.each(arr, function (r) {
                                                end.appendChild(r);
                                            });
                                        }

                                        if (cha == 0) {
                                            //同年
                                            var _bpi = parseInt(_bp);
                                            var _epi = parseInt(_ep);
                                            switch (_bpi) {
                                                case 3://从第一季度开始
                                                    generateQuarterNode(3, _epi);
                                                    break;
                                                case 6://从第二季度开始
                                                    generateQuarterNode(6, _epi);
                                                    break;
                                                case 9://从第三季度开始
                                                    generateQuarterNode(9, _epi);
                                                    break;
                                                case 12://从第四季度开始
                                                    generateQuarterNode(12, _epi);
                                                    break;
                                            }
                                        } else {
                                            switch (a) {
                                                case 0://开始年
                                                    var _bpi = parseInt(_bp);
                                                    switch (_bpi) {
                                                        case 3://从第一季度开始
                                                            generateQuarterNode(3);
                                                            break;
                                                        case 6://从第二季度开始
                                                            generateQuarterNode(6);
                                                            break;
                                                        case 9://从第三季度开始
                                                            generateQuarterNode(9);
                                                            break;
                                                        case 12://从第四季度开始
                                                            generateQuarterNode(12);
                                                            break;
                                                    }
                                                    break;
                                                case _eyi - _byi://结束年
                                                    var _epi = parseInt(_ep);
                                                    switch (_epi) {
                                                        case 3://从第一季度开始
                                                            generateQuarterNodeDesc(3);
                                                            break;
                                                        case 6://从第二季度开始
                                                            generateQuarterNodeDesc(6);
                                                            break;
                                                        case 9://从第三季度开始
                                                            generateQuarterNodeDesc(9);
                                                            break;
                                                        case 12://从第四季度开始
                                                            generateQuarterNodeDesc(12);
                                                            break;
                                                    }
                                                    break;
                                                default ://中间年
                                                    //年节点下有四个季度的期度节点
                                                    var list_middle_quarter = [];
                                                    for (var abc = 3, bc = 0; abc < 13; abc = abc + 3, bc++) {
                                                        var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(3, abc), abc,
                                                            METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                        var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(3, abc), abc, dataType,
                                                            METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[bc]]);
                                                        var p = end.createNode(nd);
                                                        p.appendChild(children[bc]);
                                                        list_middle_quarter.push(p);
                                                    }
                                                    //如果期度为正序，则修改此排序即可
                                                    RESEARCHHZC.sortListDesc(list_middle_quarter);
                                                    Ext.Array.each(list_middle_quarter, function (r) {
                                                        end.appendChild(r);
                                                    });
                                                    break;
                                            }
                                        }
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '2'://选择
                                    for (var a = 0; a < _by.length; a++) {
                                        var y = _by[a];
                                        var npp = RESEARCHHZC.createPropertiesObj([[y, y, METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        //node:每个时间节点下的内容都是相同的，所以有多少时间节点就要复制多少个
                                        var children1 = RESEARCHHZC.childNodesCopy(child);
                                        var children2 = RESEARCHHZC.childNodesCopy(child);
                                        var children3 = RESEARCHHZC.childNodesCopy(child);
                                        var children4 = RESEARCHHZC.childNodesCopy(child);
                                        var children = [children1, children2, children3, children4];
                                        var ndp = RESEARCHHZC.createNodeObj([y, y, dataType, METADATA_TYPE.INFO_YEAR, _tr, npp, true, null]);
                                        //ext node
                                        //1、创建年node
                                        //2、创建期度node
                                        var end = newNode.createNode(ndp);
                                        for (var b = 0; b < _ep.length; b++) {
                                            var period = _ep[b];
                                            var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(3, parseInt(period)), period,
                                                METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                            var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(3, parseInt(period)), period, dataType, METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[b]]);
                                            var periodNode = end.createNode(nd);
                                            periodNode.appendChild(children[b]);
                                            end.appendChild(periodNode);
                                        }
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '3'://报告期数
                                    for (var c = 0; c < _bq; c++) {
                                        var children = RESEARCHHZC.childNodesCopy(child);
                                        var yn = '';
                                        if (c != 0) {
                                            yn = '-' + c;
                                        }
                                        var yearNp = RESEARCHHZC.createPropertiesObj([['$y$', , METADATA_TYPE.TIME,
                                            METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        var yearNd = RESEARCHHZC.createNodeObj(['$y$', , dataType, METADATA_TYPE.INFO_YEAR,
                                            _tr, yearNp, true, null]);
                                        //生成年节点
                                        var end = newNode.createNode(yearNd);
                                        //生成期度节点
                                        var periodNp = RESEARCHHZC.createPropertiesObj([['$m' + yn + '$', , METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD,
                                            _tr, true]], timeProperties);
                                        var periodNd = RESEARCHHZC.createNodeObj(['$m' + yn + '$', '$m' + yn + '$', dataType, METADATA_TYPE.INFO_PERIOD, _tr, periodNp, true, children]);

                                        var pend = end.createNode(periodNd);
                                        pend.appendChild(children);
                                        end.appendChild(pend);
                                        newNode.appendChild(end);
                                    }
                                    break;
                            }
                            break;
                        case '4'://时间频度：月
                            switch (_tr) {
                                case '1'://连续
                                    var _byi = parseInt(_by);
                                    var _eyi = parseInt(_ey);
                                    //如果年份为正序，则使用'for (var a = 0; a < (_eyi - _byi + 1); a++) {'替换即可
                                    var cha = _eyi - _byi;
                                    for (var a = cha; a > -1; a--) {
                                        var npp = RESEARCHHZC.createPropertiesObj([[_byi + a, _byi + a, METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        //node:每个时间节点下的内容都是相同的，所以有多少时间节点就要复制多少个
                                        var children1 = RESEARCHHZC.childNodesCopy(child),
                                            children2 = RESEARCHHZC.childNodesCopy(child),
                                            children3 = RESEARCHHZC.childNodesCopy(child),
                                            children4 = RESEARCHHZC.childNodesCopy(child),
                                            children5 = RESEARCHHZC.childNodesCopy(child),
                                            children6 = RESEARCHHZC.childNodesCopy(child),
                                            children7 = RESEARCHHZC.childNodesCopy(child),
                                            children8 = RESEARCHHZC.childNodesCopy(child),
                                            children9 = RESEARCHHZC.childNodesCopy(child),
                                            children10 = RESEARCHHZC.childNodesCopy(child),
                                            children11 = RESEARCHHZC.childNodesCopy(child),
                                            children12 = RESEARCHHZC.childNodesCopy(child);
                                        var children = [children1, children2, children3, children4, children5, children6, children7, children8, children9, children10, children11, children12];
                                        var ndp = RESEARCHHZC.createNodeObj([_byi + a, _byi + a, dataType, METADATA_TYPE.INFO_YEAR, _tr, npp, true, null]);
                                        //ext node
                                        //1、创建年node
                                        //2、创建期度node
                                        var end = newNode.createNode(ndp);

                                        /**
                                         * 生成期度节点
                                         * @param num 从哪个月开始
                                         * @param endP 到哪个月结束
                                         */
                                        function generateMonthNode(num, endP) {
                                            endP || (endP = 12);
                                            var list = [];
                                            for (var abc = num, bc = 0; abc < endP + 1; abc = abc + 1, bc++) {
                                                var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(4, abc), abc,
                                                    METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(4, abc), abc, dataType,
                                                    METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[bc]]);
                                                var pq = end.createNode(nd);
                                                pq.appendChild(children[bc]);
                                                list.push(pq);
                                            }
                                            RESEARCHHZC.sortListDesc(list);
                                            Ext.Array.each(list, function (r) {
                                                end.appendChild(r);
                                            });
                                        }

                                        /**
                                         * 生成季度下的期度节点
                                         * @param num 到哪一期度结束
                                         */
                                        function generateMonthNodeDesc(num) {
                                            var arr = [];
                                            for (var abc = num, bc = 0; abc > 0; abc = abc - 1, bc++) {
                                                var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(4, abc), abc,
                                                    METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(4, abc), abc, dataType,
                                                    METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[bc]]);
                                                var pq = end.createNode(nd);
                                                pq.appendChild(children[bc]);
                                                arr.push(pq);
                                            }
                                            //如果期度为正序，则修改此排序即可
                                            //RESEARCHHZC.sortListDesc(arr);
                                            Ext.Array.each(arr, function (r) {
                                                end.appendChild(r);
                                            });
                                        }

                                        if (cha == 0) {
                                            //同年
                                            var _bpi = parseInt(_bp);
                                            var _epi = parseInt(_ep);
                                            switch (_bpi) {
                                                case 1:
                                                    generateMonthNode(1, _epi);
                                                    break;
                                                case 2:
                                                    generateMonthNode(2, _epi);
                                                    break;
                                                case 3://从第3个月开始
                                                    generateMonthNode(3, _epi);
                                                    break;
                                                case 4:
                                                    generateMonthNode(4, _epi);
                                                    break;
                                                case 5:
                                                    generateMonthNode(5, _epi);
                                                    break;
                                                case 6://从第6个月开始
                                                    generateMonthNode(6, _epi);
                                                    break;
                                                case 7:
                                                    generateMonthNode(7, _epi);
                                                    break;
                                                case 8:
                                                    generateMonthNode(8, _epi);
                                                    break;
                                                case 9://从第9个月开始
                                                    generateMonthNode(9, _epi);
                                                    break;
                                                case 10:
                                                    generateMonthNode(10, _epi);
                                                    break;
                                                case 11:
                                                    generateMonthNode(11, _epi);
                                                    break;
                                                case 12://从第12个月开始
                                                    generateMonthNode(12, _epi);
                                                    break;
                                            }
                                        } else {
                                            switch (a) {
                                                case 0://开始年
                                                    var _bpi = parseInt(_bp);
                                                    switch (_bpi) {
                                                        case 1:
                                                            generateMonthNode(1);
                                                            break;
                                                        case 2:
                                                            generateMonthNode(2);
                                                            break;
                                                        case 3://从第3个月开始
                                                            generateMonthNode(3);
                                                            break;
                                                        case 4:
                                                            generateMonthNode(4);
                                                            break;
                                                        case 5:
                                                            generateMonthNode(5);
                                                            break;
                                                        case 6://从第6个月开始
                                                            generateMonthNode(6);
                                                            break;
                                                        case 7:
                                                            generateMonthNode(7);
                                                            break;
                                                        case 8:
                                                            generateMonthNode(8);
                                                            break;
                                                        case 9://从第9个月开始
                                                            generateMonthNode(9);
                                                            break;
                                                        case 10:
                                                            generateMonthNode(10);
                                                            break;
                                                        case 11:
                                                            generateMonthNode(11);
                                                            break;
                                                        case 12://从第12个月开始
                                                            generateMonthNode(12);
                                                            break;
                                                    }
                                                    break;
                                                case _eyi - _byi://结束年
                                                    var _epi = parseInt(_ep);
                                                    switch (_epi) {
                                                        case 1:
                                                            generateMonthNodeDesc(1);
                                                            break;
                                                        case 2:
                                                            generateMonthNodeDesc(2);
                                                            break;
                                                        case 3://从第一季度开始
                                                            generateMonthNodeDesc(3);
                                                            break;
                                                        case 4:
                                                            generateMonthNodeDesc(4);
                                                            break;
                                                        case 5:
                                                            generateMonthNodeDesc(5);
                                                            break;
                                                        case 6://从第二季度开始
                                                            generateMonthNodeDesc(6);
                                                            break;
                                                        case 7:
                                                            generateMonthNodeDesc(7);
                                                            break;
                                                        case 8:
                                                            generateMonthNodeDesc(8);
                                                            break;
                                                        case 9://从第三季度开始
                                                            generateMonthNodeDesc(9);
                                                            break;
                                                        case 10:
                                                            generateMonthNodeDesc(10);
                                                            break;
                                                        case 11:
                                                            generateMonthNodeDesc(11);
                                                            break;
                                                        case 12://从第四季度开始
                                                            generateMonthNodeDesc(12);
                                                            break;
                                                    }
                                                    break;
                                                default ://中间年
                                                    var list = [];
                                                    //年节点下有四个季度的期度节点
                                                    for (var abc = 1, bc = 0; abc < 13; abc = abc + 1, bc++) {
                                                        var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(4, abc), abc,
                                                            METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                                        var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(4, abc), abc, dataType,
                                                            METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[bc]]);
                                                        var p = end.createNode(nd);
                                                        p.appendChild(children[bc]);
                                                        list.push(p);
                                                    }
                                                    RESEARCHHZC.sortListDesc(list);
                                                    Ext.Array.each(list, function (r) {
                                                        end.appendChild(r);
                                                    });
                                                    break;
                                            }
                                        }
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '2'://选择
                                    for (var a = 0; a < _by.length; a++) {
                                        var y = _by[a];
                                        var npp = RESEARCHHZC.createPropertiesObj([[y, y, METADATA_TYPE.TIME, METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        //node:每个时间节点下的内容都是相同的，所以有多少时间节点就要复制多少个
                                        var children1 = RESEARCHHZC.childNodesCopy(child), children2 = RESEARCHHZC.childNodesCopy(child), children3 = RESEARCHHZC.childNodesCopy(child), children4 = RESEARCHHZC.childNodesCopy(child);
                                        var children5 = RESEARCHHZC.childNodesCopy(child), children6 = RESEARCHHZC.childNodesCopy(child), children7 = RESEARCHHZC.childNodesCopy(child), children8 = RESEARCHHZC.childNodesCopy(child);
                                        var children9 = RESEARCHHZC.childNodesCopy(child), children10 = RESEARCHHZC.childNodesCopy(child), children11 = RESEARCHHZC.childNodesCopy(child), children12 = RESEARCHHZC.childNodesCopy(child);
                                        var children = [children1, children2, children3, children4, children5, children6, children7, children8, children9, children10, children11, children12];
                                        var ndp = RESEARCHHZC.createNodeObj([y, y, dataType, METADATA_TYPE.INFO_YEAR, _tr, npp, true, null]);
                                        //ext node
                                        //1、创建年node
                                        //2、创建期度node
                                        var end = newNode.createNode(ndp);
                                        for (var b = 0; b < _ep.length; b++) {
                                            var period = _ep[b];
                                            var np = RESEARCHHZC.createPropertiesObj([[FREQUENCY_TYPE.getString(4, parseInt(period)), period,
                                                METADATA_TYPE.TIME, METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                            var nd = RESEARCHHZC.createNodeObj([FREQUENCY_TYPE.getString(4, parseInt(period)), period, dataType, METADATA_TYPE.INFO_PERIOD, _tr, np, true, children[b]]);
                                            var periodNode = end.createNode(nd);
                                            periodNode.appendChild(children[b]);
                                            end.appendChild(periodNode);
                                        }
                                        newNode.appendChild(end);
                                    }
                                    break;
                                case '3'://报告期数
                                    for (var c = 0; c < _bq; c++) {
                                        var children = RESEARCHHZC.childNodesCopy(child);
                                        var yn = '';
                                        if (c != 0) {
                                            yn = '-' + c;
                                        }
                                        var yearNp = RESEARCHHZC.createPropertiesObj([['$y$', , METADATA_TYPE.TIME,
                                            METADATA_TYPE.INFO_YEAR, _tr, true]]);
                                        var yearNd = RESEARCHHZC.createNodeObj(['$y$', '$y$', dataType, METADATA_TYPE.INFO_YEAR,
                                            _tr, yearNp, true, null]);
                                        //生成年节点
                                        var end = newNode.createNode(yearNd);
                                        //生成期度节点
                                        var periodNp = RESEARCHHZC.createPropertiesObj([['$m' + yn + '$', , METADATA_TYPE.TIME,
                                            METADATA_TYPE.INFO_PERIOD, _tr, true]], timeProperties);
                                        var periodNd = RESEARCHHZC.createNodeObj(['$m' + yn + '$', '$m' + yn + '$', dataType, METADATA_TYPE.INFO_PERIOD, _tr, periodNp, true, children]);

                                        var pend = end.createNode(periodNd);
                                        pend.appendChild(children);
                                        end.appendChild(pend);
                                        newNode.appendChild(end);
                                    }
                                    break;
                            }
                            break;
                    }
                    child.remove();
                    i = i - 1;
                }
                arguments.callee(fo, child);
            }
        }
        return newNode;
    },
    /**
     * 返回节点的所有子节点的复制节点
     * @param node 复制的节点
     * @returns {Array} 所有子节点的复制节点
     * @author hzc
     * @createDate 2016-3-2
     */
    childNodesCopy: function (node) {
        var childs = [];
        if (node.childNodes.length > 0) {
            Ext.Array.each(node.childNodes, function (data) {
                childs.push(copyNode(data, true, false));
            });
        }
        return childs;
    },
    /**
     * 生成节点属性对象 ，非node节点
     * @param obj 节点属性值：多维数组
     * @param arr 时间节点属性
     * @returns {Array} 返回属性
     * @author hzc
     * @createDate 2016-3-2
     */
    createPropertiesObj: function (obj, arr) {
        var rs = [];
        if (obj) {
            Ext.Array.each(obj, function (data) {
                rs.push({
                    dataName: data[0],
                    dataValue: data[1],
                    dataType: data[2],
                    dataInfo1: data[3],
                    dataInfo2: data[4],
                    copy: data[5]
                });
            });
        }
        if (arr && arr.length > 0) {
            Ext.Array.each(arr, function (data) {
                rs.push({
                    dataName: data[0],
                    dataValue: data[1],
                    dataType: data[2],
                    dataInfo1: data[3],
                    dataInfo2: data[4],
                    copy: data[5]
                });
            });
        }
        return rs;
    },

    /**
     * 生成节点对象：非node
     * @param obj
     * @returns {{dataName: *, dataValue: *, dataType: *, dataInfo1: *, dataInfo2: *, properties: *, copy: *, children: *}}
     */
    createNodeObj: function (obj) {
        return {
            dataName: obj[0],
            dataValue: obj[1],
            dataType: obj[2],
            dataInfo1: obj[3],
            dataInfo2: obj[4],
            properties: obj[5],
            copy: obj[6],
            children: obj[7]
        };
    },
    /**
     * 生成主宾栏树
     * @param fo
     * @param mainBarTree
     * @param guestBarTree
     * @param mainList
     * @param guestList
     * @returns {{mainBar: Array, guestBar: Array, tmpId: *, unitSelector: tdUnitSelector}}
     * @author hzc
     * @createDate 2016-3-3
     */
    getBarNodesForSaveZhuBin: function (fo, mainBarTree, guestBarTree, mainList, guestList) {
        return {
            mainBar: RESEARCHHZC.getTreeNodes(mainBarTree.getRootNode(), null, mainList),
            guestBar: RESEARCHHZC.getTreeNodes(guestBarTree.getRootNode(), null, guestList),
            tmpId: fo.researchId,
            unitSelector: tdUnitSelector
        };
    },
    /**
     * 获取主宾蓝的节点信息
     * @param mainBarTree
     * @param guestBarTree
     * @param mainList 主栏叶子节点存储列表
     * @param guestList 宾栏叶子节点存储列表
     * @returns {{mainBar: Array, guestBar: Array}}
     */
    getBarNodes: function (fo, mainBarTree, guestBarTree, mainList, guestList) {
        var copyMainRoot = copyNode(mainBarTree.getRootNode(), true, false);
        var copyGuestRoot = copyNode(guestBarTree.getRootNode(), true, false);
        return {
            mainBar: RESEARCHHZC.getTreeNodes(RESEARCHHZC.getTreeNodesForCreate(fo, copyMainRoot), null, mainList),
            guestBar: RESEARCHHZC.getTreeNodes(RESEARCHHZC.getTreeNodesForCreate(fo, copyGuestRoot), null, guestList),
            tmpId: fo.researchId,
            unitSelector: tdUnitSelector
        };
    },
    /**
     * 生成时间范围对象集合
     * @param fo
     * @returns {Array}
     * @author hzc
     * @createDate 2016-3-2
     */
    generateRanges: function (fo) {
        //时间范围
        var _tr = fo.timeRange.getValue().timeRange;
        //时间频度
        var _tf = fo.timeFrequency.getValue().frequency;
        //开始年
        var _by = null,
        //开始期度
            _bp = null,
        //结束年
            _ey = null,
        //结束期度
            _ep = null,
        //报告期数
            _bq = null,
            list = [];

        switch (_tf) {
            case '2':
            case '3':
            case '4':
                if (_tr == 1) {
                    var __bp = fo.lianxuBeginPeriodCombobox.value.length;
                    var __ep = fo.lianxuEndPeriodCombobox.value.length;
                    if (__bp > 2 || __ep > 2) {
                        return [];
                    }
                }
                break;
        }

        switch (_tr) {
            case
            '1'
            :
                //连续
                _by = fo.lianxuBeginYearCombobox.getValue();
                _ey = fo.lianxuEndYearCombobox.getValue();
                if (_tf != '1') {
                    _bp = fo.lianxuBeginPeriodCombobox.getValue();
                    _ep = fo.lianxuEndPeriodCombobox.getValue();
                }
                switch (_tf) {
                    case '1':
                        //年
                        list.push(generateTimeRangeObj(METADATA_TYPE.LIANXU, _by, METADATA_TYPE.DATA_BEGIN_YEAR));
                        list.push(generateTimeRangeObj(METADATA_TYPE.LIANXU, _ey, METADATA_TYPE.DATA_END_YEAR));
                        break;
                    case '2':
                    case '3':
                    case '4':
                        //        半年
                        list.push(generateTimeRangeObj(METADATA_TYPE.LIANXU, _by, METADATA_TYPE.DATA_BEGIN_YEAR));
                        list.push(generateTimeRangeObj(METADATA_TYPE.LIANXU, _ey, METADATA_TYPE.DATA_END_YEAR));
                        list.push(generateTimeRangeObj(METADATA_TYPE.LIANXU, _bp, METADATA_TYPE.DATA_BEGIN_PERIOD));
                        list.push(generateTimeRangeObj(METADATA_TYPE.LIANXU, _ep, METADATA_TYPE.DATA_END_PERIOD));
                        break;
                }
                break;
            case
            '2'
            ://选择
                _by = fo.xuanzeYearMultyCombobox.getValue();
                if (_tf != '1') {
                    _ep = fo.xuanzePeriodMultyCombobox.getValue();
                }
                switch (_tf) {
                    case '1':
                        //年
                        Ext.Array.each(_by, function (data) {
                            list.push(generateTimeRangeObj(METADATA_TYPE.XUANZE, data, METADATA_TYPE.DATA_YEAR));
                        });
                        break;
                    case '2':
                    case '3':
                    case '4':
                        Ext.Array.each(_by, function (data) {
                            list.push(generateTimeRangeObj(METADATA_TYPE.XUANZE, data, METADATA_TYPE.DATA_YEAR));
                        });
                        Ext.Array.each(_ep, function (data) {
                            list.push(generateTimeRangeObj(METADATA_TYPE.XUANZE, data, METADATA_TYPE.DATA_PERIOD));
                        });
                        break;
                }
                break;
            case
            '3'
            ://报告期数
                _bq = fo.baogaoqi.getValue();
                list.push(generateTimeRangeObj(METADATA_TYPE.BAOGAOQI, _bq, METADATA_TYPE.BAOGAOQI));
                break;
        }
        function generateTimeRangeObj(type, dataValue, dataType) {
            return {
                type: type,
                dataValue: dataValue,
                dataType: dataType
            }
        }

        return list;
    },
    /**
     * 保存自定义报表的时间频度和时间范围
     * @param fo
     * @param researchId
     * @param fn
     * @author hzc
     * @createDate 2016-3-2
     */
    saveRangeAndFrequency: function (fo, fn) {
        var timeRange = RESEARCHHZC.generateRanges(fo);
        if (timeRange.length < 1) {
            Ext.Msg.alert('提示', '请选择连续期度');
            return;
        }
        var rs = true;
        Ext.Array.each(timeRange, function (r) {
            if (!r.dataValue) {
                rs = false;
            }
        });
        if (!rs) {
            Ext.Msg.alert('提示', '请选择时间范围');
            return;
        }
        Ext.Ajax.request({
            url: contextPath + '/resourcecategory/analysis/common/analysis/saveTimeRangeAndFrequency',
            method: 'POST',
            params: {
                timeRange: JSON.stringify(timeRange),
                foreignId: fo.researchId,
                foreignType: RPT_DESIGN_TYPE.TYPE_REPORT,
                period: fo.timeFrequency.getValue().frequency
            },
            success: function (response) {
                fn(Ext.decode(response.responseText));
            },
            failure: function (response, opts) {
                fn({success: false, msg: '保存失败'});
            }
        });
    }
    ,
    /**
     * 集合排序，倒序
     * @param list
     */
    sortListDesc: function (list) {
        list.sort(function (a, b) {
            return b.get('dataValue') - a.get('dataValue');
        });
    }
    ,
    /**
     * 集合排序，正序
     * @param list
     */
    sortListAsc: function (list) {
        list.sort(function (a, b) {
            return a.get('dataValue') - b.get('dataValue');
        });
    }
}

