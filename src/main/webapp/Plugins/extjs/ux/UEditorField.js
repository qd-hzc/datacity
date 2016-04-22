/**
 * 文本编辑器面板
 * Created by wxl on 2016/3/16.
 */
createModel('Ext.form.field.UEditor', function () {
    Ext.define('Ext.form.field.UEditor', {
        extend: 'Ext.form.field.Text',
        xtype: 'ueditor',//xtype名称
        fieldSubTpl: [
            '<textarea id="{id}" {inputAttrTpl} style="width:100%;height:100%"',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="rows"> rows="{rows}" </tpl>',
            '<tpl if="cols"> cols="{cols}" </tpl>',
            '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
            '<tpl if="size"> size="{size}"</tpl>',
            '<tpl if="maxLength !== undefined"> maxLength="{maxLength}"</tpl>',
            '<tpl if="readOnly"> readonly="readonly"</tpl>',
            '<tpl if="disabled"> disabled="disabled"</tpl>',
            '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
            ' autocomplete="off">\n',
            '<tpl if="value">{[Ext.util.Format.htmlEncode(values.value)]}</tpl>',
            '</textarea>',
            {
                disableFormats: true
            }
        ],
        defaultUeditorConfig: {
            autoHeightEnabled: false,
            autoFloatEnabled: false,
            zIndex: 99999,
            //定制工具栏
            toolbars: [[
                'source', '|', 'undo', '|','bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript',
                'subscript', 'removeformat', 'formatmatch','autotypeset', 'pasteplain', '|', 'forecolor', 'backcolor',
                'insertorderedlist', 'insertunorderedlist',  'cleardoc', '|','customstyle', 'paragraph', 'fontfamily',
                'fontsize', '|','directionalityltr', 'directionalityrtl', 'indent', '|','justifyleft', 'justifycenter',
                'justifyright', 'justifyjustify', '|','link', 'unlink', '|', 'imagenone', 'imageleft', 'imageright',
                'imagecenter', '|', 'simpleupload', 'insertimage', 'scrawl',  'attachment',  'insertcode','pagebreak',
                'template', 'background', '|', 'horizontal', 'date', 'time', 'spechars', 'snapscreen', 'wordimage', '|',
                'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol',
                'mergecells', 'mergeright', 'mergeleft', 'mergeup', 'mergedown', 'splittocells', 'splittorows', 'splittocols',
                'lefttophzc', 'centertophzc', 'righttophzc', 'leftmiddlehzc', 'centermiddlehzc', 'rightmiddlehzc', 'leftbottomhzc',
                'centerbottomhzc', 'rightbottomhzc','|','print', 'preview', 'searchreplace', 'help'
            ]]
        },//定制默认配置项
        initComponent: function () {
            var me = this;
            me.callParent(arguments);
        },
        afterRender: function () {
            var me = this;
            me.callParent(arguments);
            if (!me.ue) {

                me.ueditorConfig = Ext.apply(me.ueditorConfig,me.defaultUeditorConfig);
                console.log(me)
                me.ue = UE.getEditor(me.getInputId(), Ext.apply(me.ueditorConfig/*, {
                    initialFrameHeight: me.height || '600px',
                    initialFrameWidth: '100%'
                }*/));
                me.ue.ready(function () {
                    me.UEditorIsReady = true;
                });
                //这块 组件的父容器关闭的时候 需要销毁编辑器 否则第二次渲染的时候会出问题 可根据具体布局调整
                var win = me.up('window');
                if (win && win.closeAction == "hide") {
                    win.on('beforehide', function () {
                        me.onDestroy();
                    });
                } else {
                    var panel = me.up('panel');
                    if (panel && panel.closeAction == "hide") {
                        panel.on('beforehide', function () {
                            me.onDestroy();
                        });
                    }
                }
            } else {
                me.ue.setContent(me.getValue());
            }
        },
        setValue: function (value) {
            var me = this;
            if (!me.ue) {
                me.setRawValue(me.valueToRaw(value));
            } else {
                me.ue.ready(function () {
                    me.ue.setContent(value);
                });
            }
            me.callParent(arguments);
            return me.mixins.field.setValue.call(me, value);
        },
        getRawValue: function () {
            var me = this;
            if (me.UEditorIsReady) {
                me.ue.sync(me.getInputId());
            }
            v = (me.inputEl ? me.inputEl.getValue() : Ext.valueFrom(me.rawValue, ''));
            me.rawValue = v;
            return v;
        },
        destroyUEditor: function () {
            var me = this;
            if (me.rendered) {
                try {
                    me.ue.destroy();
                    var dom = document.getElementById(me.id);
                    if (dom) {
                        dom.parentNode.removeChild(dom);
                    }
                    me.ue = null;
                } catch (e) {
                }
            }
        },
        onDestroy: function () {
            var me = this;
            me.callParent();
            me.destroyUEditor();
        }
    });
});
