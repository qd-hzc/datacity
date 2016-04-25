/**
 * jquery扩展
 * Created by HZC on 2016/3/18.
 */
(function ($, undefined) {
    var toasting = {
        gettoaster: function () {
            var toaster = $('#' + settings.toaster.id);
            if (toaster.length < 1) {
                toaster = $(settings.toaster.template).attr('id', settings.toaster.id).css(settings.toaster.css).
                    addClass(settings.toaster['class']);
                if ((settings.stylesheet) && (!$("link[href=" + settings.stylesheet + "]").length)) {
                    $('head').appendTo('<link rel="stylesheet" href="' + settings.stylesheet + '">');
                }
                $(settings.toaster.container).append(toaster);
            }
            return toaster;
        },
        notify: function (title, message, priority) {
            var $toaster = this.gettoaster();
            var $toast = $(settings.toast.template.replace('%priority%', priority)).hide().css(settings.toast.css)
                .addClass(settings.toast['class']);
            $('.title', $toast).css(settings.toast.csst).html(title);
            $('.message', $toast).css(settings.toast.cssm).html(message);
            if ((settings.debug) && (window.console)) {
                console.log(toast);
            }
            $toaster.append(settings.toast.display($toast));
            if (settings.donotdismiss.indexOf(priority) === -1) {
                var timeout = (typeof settings.timeout === 'number') ? settings.timeout : ((typeof settings.timeout === 'object') && (priority in settings.timeout)) ? settings.timeout[priority] : 1500;
                setTimeout(function () {
                    settings.toast.remove($toast, function () {
                        $toast.remove();
                    });
                }, timeout);
            }
        }
    };
    var defaults = {
        'toaster': {
            'id': 'toaster',
            'container': 'body',
            'template': '<div></div>',
            'class': 'toaster',
            'css': {
                'position': 'fixed',
                'top': '10px',
                'right': '10px',
                'width': '300px',
                'zIndex': 50000
            }
        },
        'toast': {
            'template': '<div class="alert alert-%priority% alert-dismissible" role="alert">' +
            '<button type="button" class="close" data-dismiss="alert">' +
            '<span aria-hidden="true">&times;</span>' +
            '<span class="sr-only">Close</span>' +
            '</button>' +
            '<span class="title"></span>: <span class="message"></span>' +
            '</div>',
            'css': {},
            'cssm': {},
            'csst': {'fontWeight': 'bold'},
            'fade': 'slow',
            'display': function ($toast) {
                return $toast.fadeIn(settings.toast.fade);
            },
            'remove': function ($toast, callback) {
                return $toast.animate({
                        opacity: '0',
                        padding: '0px',
                        margin: '0px',
                        height: '0px'
                    }, {
                        duration: settings.toast.fade,
                        complete: callback
                    }
                );
            }
        },
        'debug': false,
        'timeout': 3500,
        'stylesheet': null,
        'donotdismiss': []
    };
    var settings = {};
    $.extend(settings, defaults);
    $.toaster = function (options) {
        if (typeof options === 'object') {
            if ('settings' in options) {
                settings = $.extend(settings, options.settings);
            }
            var title = ('title' in options) ? options.title : 'Notice';
            var message = ('message' in options) ? options.message : null;
            var priority = ('priority' in options) ? options.priority : 'success';

            if (message !== null) {
                toasting.notify(title, message, priority);
            }
        }
    };
    $.toaster.reset = function () {
        settings = {};
        $.extend(settings, defaults);
    };
})(jQuery);
(function ($, undefined) {
    $.extend({
        /**
         * 显示成功信息
         * @param msg
         * @param title
         */
        showSucc: function (msg, title) {
            if (!title) {
                title = 'Success';
            }
            $.toaster({priority: 'success', title: title, message: msg});
        },
        /**
         * 显示失败消息
         * @param msg
         * @param title
         */
        showError: function (msg, title) {
            if (!title) {
                title = 'Error';
            }
            $.toaster({priority: 'danger', title: title, message: msg});
        },
        /**
         * 全局字符串替换
         * @param str 操作的字符串
         * @param oldStr 被替换的字符串
         * @param newStr 新字符串
         * @returns {*}
         */
        replace: function (str, oldStr, newStr) {
            return str.replace(new RegExp(oldStr, 'g'), newStr);
        },
        /**
         * js模板
         * @param templateHtml ：模板html
         * @param data ：数据对象
         * @returns {*}
         */
        render: function (templateHtml, data) {
            return templateHtml.replace(/\{([\w\.]*)\}/g, function (str, key) {
                var keys = key.split("."),
                    v = data[keys.shift()];
                for (var i = 0, l = keys.length; i < l; i++)
                    v = v[keys[i]];
                return (typeof v !== "undefined" && v !== null) ? v : "";
            });
        },
        /**
         * 格式化时长字符串，格式为"HH:MM:SS"
         * @param ts
         * @returns {*}
         */
        timeToStr: function (ts) {
            if (isNaN(ts)) {
                return "--:--:--";
            }
            var h = parseInt(ts / 3600);
            var m = parseInt((ts % 3600) / 60);
            var s = parseInt(ts % 60);
            return (this.ultZeroize(h) + ":" + this.ultZeroize(m) + ":" + this.ultZeroize(s));
        },
        /**
         * 格式化日期时间字符串，格式为"YYYY-MM-DD HH:MM:SS"
         * @param d
         * @returns {string}
         */
        dateToStr: function (d) {
            return (d.getFullYear() + "-" + this.ultZeroize(d.getMonth() + 1) + "-" + this.ultZeroize(d.getDate()) + " " + this.ultZeroize(d.getHours()) + ":" + this.ultZeroize(d.getMinutes()) + ":" + this.ultZeroize(d.getSeconds()));
        },
        /**
         * 格式化数字，前面加0，默认长度：2
         * @param v
         * @param l
         * @returns {string}
         */
        ultZeroize: function (v, l) {
            var z = "";
            l = l || 2;
            v = String(v);
            for (var i = 0; i < l - v.length; i++) {
                z += "0";
            }
            return z + v;
        },
        /**
         * 传入dom
         * <pre>
         *      监听时间变化
         *      datetimepicker.on('changeDate', function (ev) {}
         * </pre>
         * @param dom 要显示日期控件的dom元素
         * @param time 初始时间：2016-04
         * @returns {*}
         */
        datetimepickeresi: function (dom, time) {
            var html = '<div class="input-group date form_datetime">' +
                '<input type="text" style="display: none" value=""/>' +
                '<span class="input-group-addon">' +
                '<span class="glyphicon glyphicon-calendar" style="color: white;"></span>' +
                '<div style="font-size: 20px;color: white;align-content: center;cursor:pointer;" ' +
                'class="month"></div>' +
                '<div style="font-size: 15px;color: white;align-content: center;cursor:pointer;" ' +
                'class="year"></div>' +
                '</span>' +
                '</div>';
            var a = dom.append(html).children('div');
            //设置时间框选项
            var datetimepicker = a.datetimepicker({
                startView: 3,
                minView: 4,
                format: "yyyy-mm", //选择日期后，文本框显示的日期格式
                language: 'zh-CN', //汉化
                autoclose: true, //选择日期后自动关闭
                todayBtn: true
            });
            setDate(time);
            function setDate(date) {
                var span = dom.children('div').children('span').children();
                var year = span.eq(2);
                year.empty();
                var month = span.eq(1);
                month.empty();
                var arr = date.toString().split(" ");//将时间转化为数组
                year.append(arr[3]);//将年份显示到时间框内
                month.append(convertMonth(arr[1]));//将月份显示到时间框内
            }

            //根据时间变化更新时间框显示年月
            datetimepicker.on('changeDate', function (ev) {
                var temp = ev.date;//获取时间转为String 格式为Mon Feb 01 2016 15:03:24 GMT+0800
                setDate(temp);
            });
            return datetimepicker;
            //将获取到的英文月份转换为中文
            function convertMonth(eMonth) {
                switch (eMonth) {
                    case 'Jan':
                        cMonth = '一月';
                        break;
                    case 'Feb':
                        cMonth = '二月';
                        break;
                    case 'Mar':
                        cMonth = '三月';
                        break;
                    case 'Apr':
                        cMonth = '四月';
                        break;
                    case 'May':
                        cMonth = '五月';
                        break;
                    case 'Jun':
                        cMonth = '六月';
                        break;
                    case 'Jul':
                        cMonth = '七月';
                        break;
                    case 'Aug':
                        cMonth = '八月';
                        break;
                    case 'Sep':
                        cMonth = '九月';
                        break;
                    case 'Oct':
                        cMonth = '十月';
                        break;
                    case 'Nov':
                        cMonth = '十一';
                        break;
                    case 'Dec':
                        cMonth = '十二';
                        break;
                }
                return cMonth;
            }
        },
        /**
         * 显示dialog，
         * 高度自适应
         * @param title 标题
         * @param content 内容
         */
        dialog: function (title, content) {
            var html = '<div id="esi-jquery-dialog" class="modal fade bs-example-modal-lg"' +
                'tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">' +
                '<div class="modal-dialog modal-lg">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<button type="button" class="close" data-dismiss="modal" aria-label="Close">' +
                '<span aria-hidden="true">&times;</span></button>' +

                '<h4 class="modal-title">' + title + '</h4>' +

                '</div>' +

                '<br class="modal-body">' +

                content +

                '</div>' +
                '</div></div></div>';
            $('#esi-jquery-dialog').remove();
            $(document.body).append(html);
            $('#esi-jquery-dialog').modal('show');
        }
    });
})(jQuery);
