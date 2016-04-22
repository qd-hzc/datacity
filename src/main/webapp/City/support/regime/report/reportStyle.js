/**
 * 表样设计
 *
 * Created by Paul on 2016/1/27.
 */
var MINE = {
    ue: '',
    /**
     * 获取编辑器内容的所有node
     */
    getNodes: function () {
        return UE.htmlparser(MINE.getContent(), true);
    },
    /**
     * 设置表格标题
     * @param title
     */
    setTitle: function (title) {
        MINE.ue.execCommand('insertcaption');
        var focusNode = MINE.ue.selection.getStart();
        focusNode.innerHTML = title;
    },
    /**
     * 设置编辑器内容
     * @param html
     */
    setContent: function (html) {
        MINE.ue.setContent(html);
    },
    /**
     * 获取编辑器内容
     * @returns {*|String}
     */
    getContent: function () {
        return MINE.ue.getContent();
    },
    /**
     * 生成表样设计器
     * @param style 表样
     */
    reportStyle: function (style) {
        var table = style.rptStyle ? style.rptStyle : style.designStyle;

        //table = UE.utils.unhtml(table);

        if (!table) {
            table = '';
        }

        var height = document.documentElement.clientHeight - 157;
        config.initialFrameHeight = height;

        //初始化富文本
        MINE.ue = UE.getEditor('container', config);

        MINE.ue.ready(function () {
            MINE.setContent(table);
        });
    },
    /**
     * 重置表样
     * <pre>
     *      重置表样时，如果新生成了表样，则重置为新生成的表样，
     *      否则判断原来表样是否已经有了，如果有则使用原表样，没有则使用表样，再没有则显示空
     * <pre>
     *
     * @param htmlTable 新生成的表样
     * @param style 表样
     */
    resetStyle: function (style) {
        MINE.setContent(style.designStyle);
    },
    /**
     * 保存报表设计
     */
    saveContent: function (styleId) {
        var content = MINE.getContent();
        //content = UE.utils.html(content);
        $.ajax({
            url: contextPath + '/support/regime/report/designReport/saveReportContent',
            type: 'post',
            dataType: 'json',
            data: {content: content, styleId: styleId},
            success: function (data) {
                Ext.MessageBox.alert('提示', data.datas);
            },
            error: function () {
                Ext.MessageBox.alert('提示', '保存失败，请稍候再试');
            }
        });
    }
}


