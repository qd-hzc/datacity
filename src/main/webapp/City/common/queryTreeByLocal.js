/**
 * 树的本地查询方法,将方法在文本框的change事件绑定即可
 * @param tree
 * @param store
 * @param queryParam 查询参数,即显示名
 * @param queryStr 查询的字符串
 */
function queryTreeByLocal(tree, store, queryParam, queryStr) {
    tree.collapseAll();
    var depFilter = new Ext.util.Filter({
        filterFn: function (node) {
            var reg = new RegExp(queryStr, 'i');
            var visible = reg.test(node.get(queryParam));
            if (visible && queryStr != '') {
                tree.expandNode(node.parentNode, false)
            }
            var children = node.childNodes;
            var len = children && children.length;
            for (var i = 0; i < len; i++) {
                if (children[i].get('visible')) {
                    visible = children[i].get('visible');
                    break;
                }
            }
            return visible;
        }
    });
    store.filter(depFilter);
    store.filters.clear();
}
