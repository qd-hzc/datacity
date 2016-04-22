/**
 * A Picker field that contains a tree panel on its popup, enabling selection of tree nodes.
 */
if(!Ext.ClassManager.isCreated("Ext.ux.QueryPicker")) {
    Ext.define('Ext.ux.QueryPicker', {
        extend: 'Ext.form.field.Picker',
        xtype: 'querypicker',

        uses: [
            'Ext.tree.Panel'
        ],

        triggerCls: Ext.baseCSSPrefix + 'form-arrow-trigger',

        config: {
            /**
             * @cfg {Ext.data.TreeStore} store
             * A tree store that the tree picker will be bound to
             */
            store: null,

            /**
             * @cfg {String} displayField
             * The field inside the model that will be used as the node's text.
             * Defaults to the default value of {@link Ext.tree.Panel}'s `displayField` configuration.
             */
            displayField: null,

            /**
             * @cfg {Array} columns
             * An optional array of columns for multi-column trees
             */
            columns: null,

            /**
             * @cfg {Boolean} selectOnTab
             * Whether the Tab key should select the currently highlighted item. Defaults to `true`.
             */
            selectOnTab: true,

            /**
             * @cfg {Number} maxPickerHeight
             * The maximum height of the tree dropdown. Defaults to 300.
             */
            maxPickerHeight: 200,

            /**
             * @cfg {Number} minPickerHeight
             * The minimum height of the tree dropdown. Defaults to 100.
             */
            queryMode: 'local',
            minPickerHeight: 200
        },

        listeners: {
            render: function () {
                var me = this;
                me.inputEl.on('keypress', function (e) {
                    if (me.editable && e.keyCode == e.ENTER) {
                        me.fireEvent('editChange', me, me.inputEl.getValue(), me.getPicker().getStore());
                        me.setValue(null);
                        if (!me.isExpanded) {
                            me.expand();
                            me.inputEl.focus();
                        }

                    }
                });
            },
            editChange: function (_this, text, store) {
                //请求的参数
                var queryParam = (_this.queryParam ? _this.queryParam : 'queryParam');
                var tree = _this.getPicker();
                if (_this.queryMode == 'local') {
                    var filter = new Ext.util.Filter({
                        filterFn: function (node) {
                            var reg = new RegExp(text, 'i');
                            var visible = reg.test(node.get(queryParam));
                            var children = node.childNodes;
                            var len = children && children.length;
                            for (var i = 0; i < len; i++) {
                                if (children[i].get('visible')) {
                                    if (!node.isRoot() && !node.isLeaf()) {
                                        node.set('expanded', true);
                                    }
                                    return true;
                                }
                            }
                            return visible;
                        }
                    });
                    store.filter(filter);
                    store.filters.clear();
                    if (text == '')
                        tree.collapseAll();
                } else {
                    var obj = Ext.JSON.decode('{"' + queryParam + '":"' + text + '"}');
                    if (_this.beforeQuery) {
                        _this.beforeQuery(text, obj, store, _this);
                    }
                    if (!_this.afterQuery) {
                        store.reload({
                            params: obj,
                            callback: function () {
                                tree.expandAll();
                            }
                        });
                    } else {
                        store.reload({
                            params: obj,
                            callback: _this.afterQuery
                        });
                    }
                }
            }
        },
        editable: true,

        /**
         * @event select
         * Fires when a tree node is selected
         * @param {Ext.ux.TreePicker} picker        This tree picker
         * @param {Ext.data.Model} record           The selected record
         */

        initComponent: function () {
            var me = this;
            me.callParent(arguments);
            me.mon(me.store, {
                scope: me,
                load: me.onLoad,
                update: me.onUpdate
            });
        },

        /**
         * Creates and returns the tree panel to be used as this field's picker.
         */
        createPicker: function () {
            var me = this,
                picker = new Ext.tree.Panel({
                    shrinkWrapDock: 2,
                    store: me.store,
                    floating: true,
                    displayField: me.displayField,
                    columns: me.columns,
                    minHeight: me.minPickerHeight,
                    maxHeight: me.maxPickerHeight,
                    manageHeight: false,
                    rootVisible: me.rootVisible || false,
                    shadow: false,
                    listeners: {
                        scope: me,
                        itemclick: me.onItemClick
                    },
                    viewConfig: {
                        listeners: {
                            scope: me,
                            render: me.onViewRender
                        }
                    }
                }),
                view = picker.getView();

            if (Ext.isIE9 && Ext.isStrict) {
                // In IE9 strict mode, the tree view grows by the height of the horizontal scroll bar when the items are highlighted or unhighlighted.
                // Also when items are collapsed or expanded the height of the view is off. Forcing a repaint fixes the problem.
                view.on({
                    scope: me,
                    highlightitem: me.repaintPickerView,
                    unhighlightitem: me.repaintPickerView,
                    afteritemexpand: me.repaintPickerView,
                    afteritemcollapse: me.repaintPickerView
                });
            }
            return picker;
        },

        onViewRender: function (view) {
            view.getEl().on('keypress', this.onPickerKeypress, this);
        },

        /**
         * repaints the tree view
         */
        repaintPickerView: function () {
            var style = this.picker.getView().getEl().dom.style;

            // can't use Element.repaint because it contains a setTimeout, which results in a flicker effect
            style.display = style.display;
        },

        /**
         * Handles a click even on a tree node
         * @private
         * @param {Ext.tree.View} view
         * @param {Ext.data.Model} record
         * @param {HTMLElement} node
         * @param {Number} rowIndex
         * @param {Ext.event.Event} e
         */
        onItemClick: function (view, record, node, rowIndex, e) {
            this.selectItem(record);
        },

        /**
         * Handles a keypress event on the picker element
         * @private
         * @param {Ext.event.Event} e
         * @param {HTMLElement} el
         */
        onPickerKeypress: function (e, el) {
            var key = e.getKey();

            if (key === e.ENTER || (key === e.TAB && this.selectOnTab)) {
                this.selectItem(this.picker.getSelectionModel().getSelection()[0]);
            }
        },

        /**
         * Changes the selection to a given record and closes the picker
         * @private
         * @param {Ext.data.Model} record
         */
        selectItem: function (record) {
            var me = this;
            me.setValue(record.getId());
            me.fireEvent('select', me, record);
            me.collapse();
        },

        /**
         * Runs when the picker is expanded.  Selects the appropriate tree node based on the value of the input element,
         * and focuses the picker so that keyboard navigation will work.
         * @private
         */
        onExpand: function () {
            var me = this,
                picker = me.picker,
                store = picker.store,
                value = me.value,
                node;


            if (value) {
                node = store.getNodeById(value);
            }

            if (!node) {
                node = store.getRoot();
            }

            picker.selectPath(node.getPath());
        },

        /**
         * Sets the specified value into the field
         * @param {Mixed} value
         * @return {Ext.ux.TreePicker} this
         */
        setValue: function (value) {
            var me = this,
                record;

            me.value = value;

            if (me.store.loading) {
                // Called while the Store is loading. Ensure it is processed by the onLoad method.
                return me;
            }

            // try to find a record in the store that matches the value
            record = value ? me.store.getNodeById(value) : me.store.getRoot();
            if (value === undefined) {
                record = me.store.getRoot();
                me.value = record.getId();
            } else {
                record = me.store.getNodeById(value);
            }

            // set the raw value to the record's display field if a record was found
            me.setRawValue(record ? record.get(me.displayField) : '');

            return me;
        },

        getSubmitValue: function () {
            return this.value;
        },

        /**
         * Returns the current data value of the field (the idProperty of the record)
         * @return {Number}
         */
        getValue: function () {
            return this.value;
        },

        /**
         * Handles the store's load event.
         * @private
         */
        onLoad: function () {
            var value = this.value;

            if (value) {
                this.setValue(value);
            }
        },

        onUpdate: function (store, rec, type, modifiedFieldNames) {
            var display = this.displayField;

            if (type === 'edit' && modifiedFieldNames && Ext.Array.contains(modifiedFieldNames, display) && this.value === rec.getId()) {
                this.setRawValue(rec.get(display));
            }
        }

    });
}
