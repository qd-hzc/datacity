/**
 * 下拉树的多选组件
 * Created by CRX on 2016/3/14.
 */
/**
 * 说明：
 * 创建：
 var picker = Ext.create('Ext.ux.comboboxtree', {
    fieldLabel: '内容',
    labelWidth: 100,
    labelAlign: 'right',
    width: '30%',
    store: Ext.create('Ext.data.TreeStore', {
            fields: ['id', 'name'],
        }),
    displayField: 'name',
    valueField: 'id',
    cascade: 'child',//级联方式:1.child子级联;2.parent,父级联,3,both全部级联
    checkModel: '',//当json数据为不带checked的数据时只配置为single,带checked配置为double为单选,不配置为多选
    rootVisible: false,
    rootChecked: false//设置root节点是否可以选中
});
 //设置属性
 picker.setCascade('parent');
 picker.setCheckModel('double');
 */
Ext.define("Ext.ux.ComboBoxTree", {
    extend: "Ext.form.field.Picker",
    xtype: 'comboboxtree',
    uses: ["Ext.tree.Panel"],
    initComponent: function () {
        var self = this;
        Ext.apply(self, {
            fieldLabel: self.fieldLabel,
            labelWidth: self.labelWidth
        });
        self.callParent();
    },
    createPicker: function () {
        var self = this;
        self.rootChecked = self.rootChecked ? self.rootChecked : false;
        self.picker = new Ext.tree.Panel({
            height: 300,
            autoScroll: true,
            floating: true,
            focusOnToFront: false,
            shadow: true,
            ownerCt: this.ownerCt,
            useArrows: true,
            store: self.store,
            rootVisible: self.rootVisible,
            displayField: self.displayField,
            valueField: self.valueField
        });
        self.picker.on({
            checkchange: function (record, checked) {
                var valueFiled = self.valueField;
                var displayFiled = self.displayField;
                var checkModel = self.checkModel;
                if (checkModel == 'double') {
                    var root = self.picker.getRootNode();
                    root.cascadeBy(function (node) {
                        if (node.get(displayFiled) != record.get(displayFiled)) {
                            node.set('checked', false);
                        }
                    });
                    //if (record.get('leaf') && checked) {
                    if (checked) {
                        self.setFieldValue(record.get(valueFiled)); // 隐藏值
                        self.setValue(record.get(displayFiled)); // 显示值
                    } else {
                        record.set('checked', false);
                        self.setFieldValue(''); // 隐藏值
                        self.setValue(''); // 显示值
                    }
                } else {
                    var cascade = self.cascade;
                    if (checked == true) {
                        if (cascade == 'both' || cascade == 'child' || cascade == 'parent') {
                            if (cascade == 'child' || cascade == 'both') {
                                if (!record.get("leaf") && checked) {
                                    record.cascadeBy(function (record) {
                                        record.set('checked', true);
                                    });
                                }
                            }
                            if (cascade == 'parent' || cascade == 'both') {
                                pNode = record.parentNode;
                                for (; pNode != null; pNode = pNode.parentNode) {
                                    var r = pNode.get("root");
                                    if (!r || (r && self.rootChecked)) {
                                        pNode.set("checked", true);
                                    }
                                }
                            }
                        }
                    } else if (checked == false) {
                        if (cascade == 'both' || cascade == 'child' || cascade == 'parent') {
                            if (cascade == 'child' || cascade == 'both') {
                                if (!record.get("leaf") && !checked) {
                                    record.cascadeBy(function (record) {
                                        record.set('checked', false);
                                    });
                                }
                            }
                        }
                    }
                    var records = self.picker.getView().getChecked(),
                        names = [],
                        values = [];
                    Ext.Array.each(records,
                        function (rec) {
                            names.push(rec.get(displayFiled));
                            values.push(rec.get(valueFiled));
                        });
                    self.setFieldValue(values.join(';')); // 隐藏值
                    self.setValue(names.join(';')); // 显示值
                }
            },
            itemclick: function (tree, record, item, index, e, options) {
                var checkModel = self.checkModel;
                var valueFiled = self.valueField;
                var displayFiled = self.displayField;
                if (checkModel == 'single') {
                    if (record.get('leaf')) {
                        self.setFieldValue(record.get(valueFiled)); // 隐藏值
                        self.setValue(record.get(displayFiled)); // 显示值
                    } else {
                        self.setFieldValue(''); // 隐藏值
                        self.setValue(''); // 显示值
                    }
                }
            }
        });
        return self.picker;
    },
    alignPicker: function () {
        var me = this,
            picker, isAbove, aboveSfx = '-above';
        if (this.isExpanded) {
            picker = me.getPicker();
            if (me.matchFieldWidth) {
                picker.setWidth(me.bodyEl.getWidth());
            }
            if (picker.isFloating()) {
                picker.alignTo(me.inputEl, "", me.pickerOffset); // ""->tl
                isAbove = picker.el.getY() < me.inputEl.getY();
                me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
                picker.el[isAbove ? 'addCls' : 'removeCls'](picker.baseCls + aboveSfx);
            }
        }
    },
    /**
     * 设置级联方式：1.child子级联;2.parent,父级联,3,both全部级联
     * @param value
     * @returns {comboboxtree}
     */
    setCascade: function (value) {
        this.cascade = value;
        return this;
    },
    /**
     * 设置单选或多选：当json数据为不带checked的数据时只配置为single,带checked配置为double为单选,不配置为多选
     * @param value
     * @returns {comboboxtree}
     */
    setCheckModel: function (value) {
        this.checkModel = value;
        return this;
    },
    /**
     * 设置表单值
     * @param value
     * @returns {comboboxtree}
     */
    setFieldValue: function (value) {
        this.fieldValue = value;
        return this;
    },
    /**
     * 获取form表单值
     * @returns {*}
     */
    getFieldValue: function () {
        return this.fieldValue;
    },
    /**
     * 回显
     * @param data valueField值，使用‘；’分号拼接，例如：1；2；3；4；
     */
    echo: function (data) {
        var self = this;
        var proxy = self.store.getConfig('proxy');
        var api = proxy.getApi();
        if (!api.read)return;
        if (self.store.isLoading()) {
            self.store.on('load', function () {
                self.echo(data);
            });
            return;
        }
        var valueFiled = self.valueField;
        var displayFiled = self.displayField;
        var root = self.getPicker().getRootNode();
        check(root);
        if (!data) {
            self.setFieldValue(''); // 隐藏值
            self.setValue(''); // 显示值
        } else {
            var records = self.getPicker().getView().getChecked(),
                names = [],
                values = [];
            Ext.Array.each(records,
                function (rec) {
                    names.push(rec.get(displayFiled));
                    values.push(rec.get(valueFiled));
                });
            self.setFieldValue(values.join(';')); // 隐藏值
            self.setValue(names.join(';')); // 显示值
        }
        function check(r) {
            r.cascadeBy(function (node) {
                if (data && data.indexOf(node.get(valueFiled)) > -1) {
                    node.set('checked', true);
                } else {
                    node.set('checked', false);
                }
                var childs = node.childNodes;
                if (childs && childs.length > 0) {
                    for (var ab = 0; ab < childs.length; ab++) {
                        check(childs[ab]);
                    }
                }
            });
        }
    }
});
