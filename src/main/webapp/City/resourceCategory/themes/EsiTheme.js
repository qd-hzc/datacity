/**
 * 主题相关方法
 * Created by HZC on 2016/3/23.
 */

function EsiTheme() {
    this._path = '/' + window.location.pathname.split('/')[1];
}
EsiTheme.prototype = {
    _ajax: function (url, obj, dataType, fn) {
        $.ajax({
            type: 'post',
            url: this._path + url,
            //async: false,
            data: obj,
            dataType: dataType ? dataType : 'json',
            success: function (d) {
                fn(d)
            },
            error: function (e) {
                var r = {success: false, code: 500, msg: "请求失败", datas: e};
                fn(r);
            }
        });
    },
    _ajaxJson: function (url, obj, dataType, fn) {
        $.ajax({
            type: 'post',
            url: this._path + url,
            //async: false,
            data: obj,
            dataType: dataType ? dataType : 'json',
            contentType: 'application/json;charset=utf-8',
            success: function (d) {
                fn(d)
            },
            error: function (e) {
                var r = {success: false, code: 500, msg: "请求失败", datas: e};
                fn(r);
            }
        });
    },
    /**
     * 设置dom的高度
     * @param diff 差量
     * @param dom dom jquery对象
     */
    resetHeight: function (diff, dom) {
        $(dom).height($(window.document).height() - diff);
    },
    /**
     * 返回页面html
     * <pre>
     *   返回一个页面和一个page对象
     *   {
 *        "id": 121,//id
 *        "name": "经济指标",//目录名称
 *        "parentId": 0,//目录父id
 *        "themeConfigPath": "theme_common/common.config",//目录对应的配置文件
 *        "modulePath": "theme_common/pages/panel",//目录对应的页面
 *        "status": 1,//状态
 *        "contents": [//目录对应页面的内容
 *            {
 *                "id": 89,//id
 *                "themePageId": 121,//目录id
 *                "containerId": "paneldiv2",//内容id
 *                "contentType": 8,//内容类型
 *                "contentValue": "161",//1:综合表，2：自定义表，3：图表，4：地图，5：文字分析，6：文件，7：目录，8：页面，9：数据集
 *            }
 *        ],
 *        "sortIndex": 4,//顺序
 *        "leaf": true,//是否为子节点，如果为子节点，则该菜单无子菜单
 *        "role": "145;181;148"//目录权限
 *    }
     * </pre>
     * @param id 页面id
     */
    getPageHtml: function (id, fn) {
        this._ajax('/resourcecategory/themes/commonController/returnPage', {themePageId: id}, 'html', fn);
    },
    /**
     * 返回页面数据
     * 详细返回结果查看document文档
     * @param obj
     */
    getData: function (obj, fn) {
        this._ajax('/resourcecategory/themes/commonController/returnData', obj, 'json', fn);
    },
    /**
     * 加载页面
     * @param dom 加载页面的父dom
     * @param id 页面id
     */
    load: function (dom, id) {
        $(dom).load(this._path + '/resourcecategory/themes/commonController/returnPage', {themePageId: id});
    },
    /**
     * 页面href加载url
     * @param url 页面url
     * @param data 需要传递的data：页面传值
     */
    toUrl: function (url, data) {
        var a = this._path + '/resourcecategory/themes/commonController/returnDetails?url=' + url + ( data ? ('&data=' + data ) : null);
        window.location.href = a;
    },
    /**
     * 返回请求页面的url
     * @param id
     * @returns {string}
     */
    getPageUrl: function (id) {
        return this._path + '/resourcecategory/themes/commonController/returnPage?themePageId=' + id;
    },
    /**
     * 返回数据集中的最新一期数据的时间框架
     * @param id 数据集id
     * @param fn 回调函数
     */
    getTimeFrames: function (id, fn) {
        this._ajax('/resourcecategory/themes/commonController/getTimeFrames', {id: id}, 'json', fn);
    },

    /**
     * 页面跳转下级页面
     * @param id 当前页面page.id
     * @param data 页面之间传值
     */
    loadSubPage: function (id, data) {
        window.location.href =
            this._path + '/resourcecategory/themes/commonController/loadSubPage?id=' + id + '&data=' + (data ? ( JSON.stringify(data)) : undefined);
    },
    /**
     * 获取所有配置信息
     * 返回结果参考：getData
     * 根据不同类型，结果对应getData中的类型的结果
     *          [
     *              {
 *                  content:ThemePageContent,
 *                  data:Object
 *              }
     *          ]
     * @param contents
     * @param fn
     */
    getAllData: function (contents, fn) {
        contents = JSON.stringify(contents);
        this._ajaxJson('/resourcecategory/themes/commonController/returnAllData', contents, 'json', fn);
    },
    /**
     * 显示详情的dialog
     * @param content
     */
    detailDialog: function (content) {
        var width = $(window.document).width() + 17;
        var height = $(window).height();
        var html = '' +
            '<div id="esi-theme-dialog" class="modal fade bs-example-modal-lg"' +
            'tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">' +
            '<div class="modal-dialog modal-lg" style="margin:0px;width:' + width + 'px;">' +
            '<div class="modal-content" style="background-color:#E7EBEE;border-radius: 0px;height:' + height + 'px;">' +

            content +

            '</div>' +
            '</div></div></div>';
        $('#esi-theme-dialog').remove();
        $(document.body).append(html);
        $('#esi-theme-dialog').modal('show');
        $('#esi-theme-dialog').on('shown.bs.modal', function (e) {
            $('#esi-theme-dialog').css('padding-right', '0');
        })
    },
    /**
     * 返回重点关注菜单
     * @param menus 菜单id,使用逗号分隔：3,4,5,8
     * @param fn :回调函数
     */
    getSyntheticalMenu: function (menus, fn) {
        this._ajax('/resourcecategory/themes/commonController/getSyntheticalMenus', {menus: menus}, 'json', fn);
    }
}

