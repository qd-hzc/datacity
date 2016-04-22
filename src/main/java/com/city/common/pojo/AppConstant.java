package com.city.common.pojo;

/**
 * Created by wxl on 2016/3/23.
 * 手机端所使用的静态变量
 */
public class AppConstant {
    //数据字典数据类型
    public static class DATA_DICT_TYPE {
        public static final int RPT_SYNTHESIZE = Constant.THEME_CONTENT_TYPE.RPT_SYNTHESIZE;//综合表
        public static final int RPT_CUSTOM = Constant.THEME_CONTENT_TYPE.RPT_CUSTOM;//分析报表
        public static final int CHART = Constant.THEME_CONTENT_TYPE.CHART;//分析图表
        public static final int TEXT_THEME = Constant.THEME_CONTENT_TYPE.TEXT_THEME;//分析主题
        public static final int TEXT_DESC = Constant.THEME_CONTENT_TYPE.TEXT_DESC;//文字分析
        public static final int DATA_SET = Constant.THEME_CONTENT_TYPE.DATA_SET;//数据集
    }

    //数据字典展示类型
    public static class DATA_DICT_DISPLAY_TYPE {
        public static final int CHART = 1;//图表展示
    }

    //数据字典目录配置图片类型
    public static class DATA_DICT_MENU_ICON_TYPE {
        public static final int ICON = 1;//图标
        public static final int BG = 2;//背景
    }
}
