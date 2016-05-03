package com.city.common.pojo;

import com.city.common.util.file.FileContentUtil;
import com.city.common.util.ue.LoadUEConfig;
import com.city.resourcecategory.themes.pojo.EsiTheme;
import com.city.resourcecategory.themes.util.FormatThemeUtil;

import java.util.*;

/**
 * 公共配置文件
 *
 * @author Administrator
 */
public class Constant {
    /**
     * 系统配置实体
     */
    public static final SystemConfigPojo systemConfigPojo = new SystemConfigPojo();
    /**
     * 主题
     */
    public static final Map<String, EsiTheme> themes = new HashMap<>();

    static {
        //加载主题
        List<String> configs = FormatThemeUtil.getThemeConfigs();
        if (configs != null && configs.size() > 0) {
            for (String config : configs) {
                themes.put(config, FormatThemeUtil.getThemeByConfig(config));
            }
        }
    }

    /**
     * 默认分组目录值
     */
    public static final Integer DEFAULT_ITEM_MENU = 0;

    // 请求结果
    public static class RequestResult {
        public static int SUCCESS = 0; //成功
        public static int FAIL = -1;   //失败
        public static int EXIST = -2;  //已经存在
    }


    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";

    /**
     * 报表采集类型
     */
    public static class RptType {
        /**
         * 综合表
         */
        public static final int SYNTHESIS = 1;
        /**
         * 加工表
         */
//        public static final int PROCESS = 2;

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "综合表");
            map.put("value", SYNTHESIS);
            result.add(map);
//            map = new HashMap<>();
//            map.put("text", "加工表");
//            map.put("value", PROCESS);
//            result.add(map);
            return result;
        }
    }

    /**
     * 报表设计类型
     */
    public static class RptDesignType {
        /**
         * 综合表
         */
        public static final int SYNTHESIS = 1;
        /**
         * 自定义表
         */
        public static final int CUSTOM = 2;

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "综合表");
            map.put("value", SYNTHESIS);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "自定义表");
            map.put("value", CUSTOM);
            result.add(map);
            return result;
        }
    }

    /**
     * 报表表样类型
     */
    public static class RptStyleType {
        /**
         * 固定表样
         */
        public static final int FIXED = 0;
        /**
         * 主栏不定长
         */
        public static final int DYNAMIC_MAIN = 1;
        /**
         * 宾栏不定长
         */
        public static final int DYNAMIC_GUEST = 2;

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "固定表样");
            map.put("value", FIXED);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "主栏不定长");
            map.put("value", DYNAMIC_MAIN);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "宾栏不定长");
            map.put("value", DYNAMIC_GUEST);
            result.add(map);
            return result;
        }

    }

    /**
     * 统计对象类型
     */
    public static class ResearchObjType {
        /**
         * 地区统计对象
         */
        public static final int AREA = 1;
        /**
         * 名录统计对象
         */
        public static final int COMPANY = 2;
        /**
         * 其他统计对象
         */
        public static final int OTHER = 3;

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "地区类型");
            map.put("value", AREA);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "名录类型");
            map.put("value", COMPANY);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "其他类型");
            map.put("value", OTHER);
            result.add(map);
            return result;
        }
    }

    /**
     * 元数据类型
     */
    public static class MetadataType {
        /**
         * 指标
         */
        public static final int ITEM = 1001;
        /**
         * 动态指标
         */
        public static final int DYNAMIC_ITEM = 10011;
        /**
         * 指标分组
         */
        public static final int ITEM_GROUP = 1002;
        /**
         * 动态分组
         */
        public static final int DYNAMIC_ITEMGROUP = 10021;
        /**
         * 指标目录
         */
        public static final int ITEM_MENU = 3;
        /**
         * 统计对象
         */
        public static final int RESEARCH_OBJ = 1;
        /**
         * 动态统计对象
         */
        public static final int DYNAMIC_SUROBJ = 11;
        /**
         * 统计对象分组
         */
        public static final int RESEARCH_OBJ_GROUP = 2;
        /**
         * 时间框架
         */
        public static final int TIME_FRAME = 12;
        /**
         * 动态时间框架
         */
        public static final int DYNAMIC_TIMEFRAME = 121;
        /**
         * 时间类型
         */
        public static final int TIME = 666;
        public static final String TIME_CH = "时间";
        /**
         * 动态时间
         */
        public static final int DYNAMIC_TIME = 999;

        /**
         * 创建报表时添加的描述的类型
         */
        public static final int SYSTEM_DESCRIBE_TYPE = 8801;

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllTypeForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "指标");
            map.put("value", ITEM);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "动态指标");
            map.put("value", DYNAMIC_ITEM);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "指标分组");
            map.put("value", ITEM_GROUP);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "动态指标分组");
            map.put("value", DYNAMIC_ITEMGROUP);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "指标分组目录");
            map.put("value", ITEM_MENU);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "统计对象");
            map.put("value", RESEARCH_OBJ);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "动态统计对象");
            map.put("value", DYNAMIC_SUROBJ);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "统计对象分组");
            map.put("value", RESEARCH_OBJ_GROUP);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "时间框架");
            map.put("value", TIME_FRAME);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "动态时间框架");
            map.put("value", DYNAMIC_TIMEFRAME);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "描述字段");
            map.put("value", SYSTEM_DESCRIBE_TYPE);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "时间");
            map.put("value", TIME);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "动态时间");
            map.put("value", DYNAMIC_TIME);
            result.add(map);
            return result;
        }

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "指标");
            map.put("value", ITEM);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "指标分组");
            map.put("value", ITEM_GROUP);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "指标分组目录");
            map.put("value", ITEM_MENU);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "统计对象");
            map.put("value", RESEARCH_OBJ);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "统计对象分组");
            map.put("value", RESEARCH_OBJ_GROUP);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "时间框架");
            map.put("value", TIME_FRAME);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "描述字段");
            map.put("value", SYSTEM_DESCRIBE_TYPE);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "时间");
            map.put("value", TIME);
            result.add(map);
            return result;
        }
    }

    /**
     * 调查对象类型
     */
    public static class SurObjType {
        //地区统计对象
        public static final int AREA = 1;
        public static final String AREA_CH = "地区统计对象";
        //其他统计对象
        public static final int OTHER = 2;
        public static final String OTHER_CH = "其他统计对象";
        //名录统计对象
        public static final int COMPANY = 3;
        public static final String COMPANY_CH = "名录统计对象";
    }

    /**
     * 单元格的esi-type值
     */
    public static class TdEsiType {
        /**
         * 数据域
         */
        public static final String DATA = "data";
        /**
         * 主栏区
         */
        public static final String MAIN_BAR = "main";
        /**
         * 宾栏区
         */
        public static final String GUEST_BAR = "second";
    }

    /**
     * 报送周期
     */
    public static class PeriodType {
        /**
         * 年报
         */
        public static final int YEAR = 1;
        /**
         * 半年报
         */
        public static final int HALF = 2;
        /**
         * 季报
         */
        public static final int QUARTER = 3;
        /**
         * 月报
         */
        public static final int MONTH = 4;

        public static final String YEAR_CH = "年";
        public static final String HALF_CH = "半年";
        public static final String QUARTER_CH = "季";
        public static final String MONTH_CH = "月";


        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "年报");
            map.put("value", YEAR);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "半年报");
            map.put("value", HALF);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "季报");
            map.put("value", QUARTER);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "月报");
            map.put("value", MONTH);
            result.add(map);
            return result;
        }

        //获取报告期跨度
        public static int getPeriodSpan(int frequency) {
            switch (frequency) {
                case YEAR:
                    return 12;
                case HALF:
                    return 6;
                case QUARTER:
                    return 3;
                case MONTH:
                    return 1;
                default:
                    return 1;
            }
        }

        //年度
        private static final List<Integer> YEAR_ARR = Arrays.asList(new Integer[]{FrequencyType.YEAR});
        //半年
        private static final List<Integer> HALF_ARR = Arrays.asList(new Integer[]{FrequencyType.HALF_UP, FrequencyType.HALF_DOWN});
        //季度
        private static final List<Integer> QUARTER_ARR = Arrays.asList(new Integer[]{FrequencyType.QUARTER_1, FrequencyType.QUARTER_2, FrequencyType.QUARTER_3, FrequencyType.QUARTER_4});
        //月度
        private static final List<Integer> MONTH_ARR = Arrays.asList(new Integer[]{FrequencyType.MONTH_1, FrequencyType.MONTH_2, FrequencyType.MONTH_3, FrequencyType.MONTH_4, FrequencyType.MONTH_5,
                FrequencyType.MONTH_6, FrequencyType.MONTH_7, FrequencyType.MONTH_8, FrequencyType.MONTH_9, FrequencyType.MONTH_10, FrequencyType.MONTH_11, FrequencyType.MONTH_12});

        /**
         * 查看期度是否在当前频度内
         *
         * @param frequency 频度
         * @param period    期度数据,1-12
         * @return 是否匹配
         */
        public static boolean isMatchPeriod(int frequency, int period) {
            switch (frequency) {
                case YEAR:
                    return YEAR_ARR.contains(period);
                case HALF:
                    return HALF_ARR.contains(period);
                case QUARTER:
                    return QUARTER_ARR.contains(period);
                case MONTH:
                    return MONTH_ARR.contains(period);
                default:
                    return false;
            }
        }
    }

    /**
     * 报送频率
     */
    public static class FrequencyType {
        //以下为年报
        public static final int YEAR = 12;
        public static final String YEAR_STRING = "年";
        //以下为半年报
        public static final int HALF_UP = 6;
        public static final String HALF_UP_STRING = "上半年";
        public static final int HALF_DOWN = 12;
        public static final String HALF_DOWN_STRING = "下半年";
        //以下为季报
        public static final int QUARTER_1 = 3;
        public static final String QUARTER_1_STRING = "1季度";
        public static final int QUARTER_2 = 6;
        public static final String QUARTER_2_STRING = "2季度";
        public static final int QUARTER_3 = 9;
        public static final String QUARTER_3_STRING = "3季度";
        public static final int QUARTER_4 = 12;
        public static final String QUARTER_4_STRING = "4季度";
        //以下为月报
        public static final int MONTH_1 = 1;
        public static final String MONTH_1_STRING = "1月";
        public static final int MONTH_2 = 2;
        public static final String MONTH_2_STRING = "2月";
        public static final int MONTH_3 = 3;
        public static final String MONTH_3_STRING = "3月";
        public static final int MONTH_4 = 4;
        public static final String MONTH_4_STRING = "4月";
        public static final int MONTH_5 = 5;
        public static final String MONTH_5_STRING = "5月";
        public static final int MONTH_6 = 6;
        public static final String MONTH_6_STRING = "6月";
        public static final int MONTH_7 = 7;
        public static final String MONTH_7_STRING = "7月";
        public static final int MONTH_8 = 8;
        public static final String MONTH_8_STRING = "8月";
        public static final int MONTH_9 = 9;
        public static final String MONTH_9_STRING = "9月";
        public static final int MONTH_10 = 10;
        public static final String MONTH_10_STRING = "10月";
        public static final int MONTH_11 = 11;
        public static final String MONTH_11_STRING = "11月";
        public static final int MONTH_12 = 12;
        public static final String MONTH_12_STRING = "12月";

        /**
         * 根据报送频率名称获取数字
         *
         * @param periodType
         * @param FrequencyName
         * @return
         */
        public static int getFrequencyType(int periodType, String FrequencyName) {
            switch (periodType) {
                case PeriodType.YEAR://年报
                    return YEAR;
                case PeriodType.HALF://半年报
                    if (HALF_UP_STRING.equals(FrequencyName)) {
                        return HALF_UP;
                    } else {
                        return HALF_DOWN;
                    }
                case PeriodType.QUARTER://季报
                    if (QUARTER_1_STRING.equals(FrequencyName)) {
                        return QUARTER_1;
                    } else if (QUARTER_2_STRING.equals(FrequencyName)) {
                        return QUARTER_2;
                    } else if (QUARTER_3_STRING.equals(FrequencyName)) {
                        return QUARTER_3;
                    } else {
                        return QUARTER_4;
                    }
                case PeriodType.MONTH://月报
                    if (MONTH_1_STRING.equals(FrequencyName)) {
                        return MONTH_1;
                    } else if (MONTH_2_STRING.equals(FrequencyName)) {
                        return MONTH_2;
                    } else if (MONTH_3_STRING.equals(FrequencyName)) {
                        return MONTH_3;
                    } else if (MONTH_4_STRING.equals(FrequencyName)) {
                        return MONTH_4;
                    } else if (MONTH_5_STRING.equals(FrequencyName)) {
                        return MONTH_5;
                    } else if (MONTH_6_STRING.equals(FrequencyName)) {
                        return MONTH_6;
                    } else if (MONTH_7_STRING.equals(FrequencyName)) {
                        return MONTH_7;
                    } else if (MONTH_8_STRING.equals(FrequencyName)) {
                        return MONTH_8;
                    } else if (MONTH_9_STRING.equals(FrequencyName)) {
                        return MONTH_9;
                    } else if (MONTH_10_STRING.equals(FrequencyName)) {
                        return MONTH_10;
                    } else if (MONTH_11_STRING.equals(FrequencyName)) {
                        return MONTH_11;
                    } else {
                        return MONTH_12;
                    }
                default:
                    return 0;
            }
        }

        /**
         * 根据报送频率数字获取名称
         *
         * @param periodType
         * @param FrequencyType
         * @return
         */
        public static String getFrequencyName(int periodType, Integer FrequencyType) {
            switch (periodType) {
                case PeriodType.YEAR://年报
                    return "";
                case PeriodType.HALF://半年报
                    if (HALF_UP == FrequencyType) {
                        return HALF_UP_STRING;
                    } else {
                        return HALF_DOWN_STRING;
                    }
                case PeriodType.QUARTER://季报
                    if (QUARTER_1 == FrequencyType) {
                        return QUARTER_1_STRING;
                    } else if (QUARTER_2 == FrequencyType) {
                        return QUARTER_2_STRING;
                    } else if (QUARTER_3 == FrequencyType) {
                        return QUARTER_3_STRING;
                    } else {
                        return QUARTER_4_STRING;
                    }
                case PeriodType.MONTH://月报
                    if (MONTH_1 == FrequencyType) {
                        return MONTH_1_STRING;
                    } else if (MONTH_2 == FrequencyType) {
                        return MONTH_2_STRING;
                    } else if (MONTH_3 == FrequencyType) {
                        return MONTH_3_STRING;
                    } else if (MONTH_4 == FrequencyType) {
                        return MONTH_4_STRING;
                    } else if (MONTH_5 == FrequencyType) {
                        return MONTH_5_STRING;
                    } else if (MONTH_6 == FrequencyType) {
                        return MONTH_6_STRING;
                    } else if (MONTH_7 == FrequencyType) {
                        return MONTH_7_STRING;
                    } else if (MONTH_8 == FrequencyType) {
                        return MONTH_8_STRING;
                    } else if (MONTH_9 == FrequencyType) {
                        return MONTH_9_STRING;
                    } else if (MONTH_10 == FrequencyType) {
                        return MONTH_10_STRING;
                    } else if (MONTH_11 == FrequencyType) {
                        return MONTH_11_STRING;
                    } else {
                        return MONTH_12_STRING;
                    }
                default:
                    return "";
            }
        }

        /**
         * 根据报送周期获取
         */
        public static List<Map<String, Object>> getAllForArray(int period) {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = null;
            switch (period) {
                case PeriodType.YEAR://年报
                    map = new HashMap<>();
                    map.put("text", "全年");
                    map.put("value", YEAR);
                    result.add(map);
                    return result;
                case PeriodType.HALF://半年报
                    map = new HashMap<>();
                    map.put("text", "上半年");
                    map.put("value", HALF_UP);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "下半年");
                    map.put("value", HALF_DOWN);
                    result.add(map);
                    return result;
                case PeriodType.QUARTER://季报
                    map = new HashMap<>();
                    map.put("text", "第一季度");
                    map.put("value", QUARTER_1);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "第二季度");
                    map.put("value", QUARTER_2);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "第三季度");
                    map.put("value", QUARTER_3);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "第四季度");
                    map.put("value", QUARTER_4);
                    result.add(map);
                    return result;
                case PeriodType.MONTH://月报
                    map = new HashMap<>();
                    map.put("text", "1月");
                    map.put("value", MONTH_1);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "2月");
                    map.put("value", MONTH_2);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "3月");
                    map.put("value", MONTH_3);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "4月");
                    map.put("value", MONTH_4);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "5月");
                    map.put("value", MONTH_5);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "6月");
                    map.put("value", MONTH_6);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "7月");
                    map.put("value", MONTH_7);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "8月");
                    map.put("value", MONTH_8);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "9月");
                    map.put("value", MONTH_9);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "10月");
                    map.put("value", MONTH_10);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "11月");
                    map.put("value", MONTH_11);
                    result.add(map);
                    map = new HashMap<>();
                    map.put("text", "12月");
                    map.put("value", MONTH_12);
                    result.add(map);
                    return result;
                default:
                    return result;
            }
        }
    }

    //报表报送状态
    public static class RPT_STATUS {

        public static int ALL = 0;//全部
        public static int WAITING_FILL = 1;//待填报
        public static int DRAFT = 2;//草稿
        public static int WAITING_PASS = 3;//待审
        public static int PASS = 4;//已审
        public static int REJECT = 5;//已驳回
        public static int REVIEW_ALL = 10;//全部（报表审核）

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "全部");
            map.put("value", ALL);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "待填报");
            map.put("value", WAITING_FILL);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "草稿");
            map.put("value", DRAFT);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "待审核");
            map.put("value", WAITING_PASS);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "已审核");
            map.put("value", PASS);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "已驳回");
            map.put("value", REJECT);
            result.add(map);
            return result;
        }

        /**
         * 获取审核状态
         *
         * @return
         */
        public static List<Map<String, Object>> getReviewForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("text", "全部");
            map.put("value", REVIEW_ALL);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "待审核");
            map.put("value", WAITING_PASS);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "已审核");
            map.put("value", PASS);
            result.add(map);
            return result;
        }
    }

    //报表填报状态
    public static class SUBMIT_STATUS {

        public static final int NOT = 0;//未报
        public static final int CURRENT = 1;//当期
        public static final int DALAY = 2;//逾期

        /**
         * 获取所有类型
         */
        public static List<Map<String, Object>> getAllForArray() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map = new HashMap<>();
            map.put("text", "未报");
            map.put("value", NOT);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "当期上报");
            map.put("value", CURRENT);
            result.add(map);
            map = new HashMap<>();
            map.put("text", "逾期上报");
            map.put("value", DALAY);
            result.add(map);
            return result;
        }
    }

    //数据类型
    public static class DATA_TYPE {
        public static final int NUMBER = 0;//数字
        public static final int TEXT = 1;//文字
        public static final int OTHER = 2;//其他
    }

    //数据状态
    public static class DATA_STATUS {
        public static final int UNUSABLE = 0;//不可用
        public static final int USABLE = 1;//可用

    }

    //采集类型
    public static class COLLECTION_TYPE {
        public static final int CALCULATE = 0;//计算
        public static final int FILL = 1;//填报
        public static final int EXTRACT = 2;//抽取
        public static final int LEADIN = 3;//导入
    }

    //报表报送状态
    public static class OPERATE_TYPE {
        public static final int DRAFT = 2;//报表修改
        public static final int WAITING_PASS = 3;//报表提交
        public static final int PASS = 4;//审核通过
        public static final int REJECT = 5;//审核驳回
        public static final int DELETE = 6;//删除
        public static final int INSERT = 7;//新建
        public static final int UPDATE = 8;//更新
        public static final int FIND = 9;//查看
        public static final int PHONE = 10;//手机端
        public static final int WEB = 11;//电脑端
        public static final int COPY = 12; //复制
    }

    //日志记录数据类型
    public static class LOG_SOURCE_TYPE {
        public static final int REPORT = 1;//报表类型
        public static final int REPORT_TMP = 2;//报表模板类型
        public static final int TMP_STYLE = 3;//报表模板表样类型
        public static final int REPORT_DATA = 4;//报表数据类型
        public static final int REPORT_GROUP = 5;//报表分组类型
    }

    /**
     * 分析图表类型
     */
    public static class ANALYSISCHART_TYPE {
        //静态分析图表
        public static final int TYPE_STATIC = 0;
        //动态分析图表
        public static final int TYPE_DYMIC = 2;
        public static final String TYPE_STATIC_CH = "静态分析图表";
        public static final String TYPE_DYMIC_CH = "动态分析图表";
    }

    public static class ANALYSISCHART_INFO {

        public static final int SERIES_HIDE = 0;
        public static final int SERIES_SHOW = 1;

        public static final int REALNODE = 1;
        public static final int VIRTUALNODE = 0;

        //静态分析图表
        public static final int TYPE_CATEGORY = 0;
        //动态分析图表
        public static final int TYPE_SERIES = 1;
        //左轴
        public static final int LEFTAXIS = 0;
        //右轴
        public static final int RIGHTAXIS = 1;
        //折线图
        public static final int CHART_LINE = 0;
        //曲线图
        public static final int CHART_CURVE = 1;
        //柱状图
        public static final int CHART_COLUMN = 2;
        //饼图
        public static final int CHART_PIE = 3;
        //散点图
        public static final int CHART_SCATTER = 4;
        //地图
        public static final int CHART_MAP = 5;

        public static final String REALNODE_CH = "实节点";
        public static final String VIRTUALNODE_CH = "虚结点";
        public static final String SERIES_HIDE_CH = "不显示";
        public static final String SERIES_SHOW_CH = "显示";
        public static final String TYPE_CATEGORY_CH = "分类";
        public static final String TYPE_SERIES_CH = "序列";
        public static final String LEFTAXIS_CH = "左轴";
        public static final String RIGHTAXIS_CH = "右轴";
        //折线图
        public static final String CHART_LINE_CH = "折线图";
        public static final String CHART_LINE_EN = "line";
        //曲线图
        public static final String CHART_CURVE_CH = "曲线图";
        public static final String CHART_CURVE_EN = "spline";
        //柱状图
        public static final String CHART_COLUMN_CH = "柱状图";
        public static final String CHART_COLUMN_EN = "bar";
        //饼图
        public static final String CHART_PIE_CH = "饼图";
        public static final String CHART_PIE_EN = "pie";
        //散点图
        public static final String CHART_SCATTER_CH = "散点图";
        public static final String CHART_SCATTER_EN = "scatter";
        //地图
        public static final String CHART_MAP_CH = "地图";
        public static final String CHART_MAP_EN = "map";

    }

    /**
     * 时间范围
     */
    public static class TIMERANGE {
        // TYPE
        //          连续时间范围
        public static final Integer LIANXU = 1;
        //        选择时间范围
        public static final Integer XUANZE = 2;
        //        报告期数时间范围
        public static final Integer BAOGAOQI = 3;
        //无时间
        public static final Integer WU = 0;

        // FORIGN_TYPE
        //      报表分析类型
        public static final Integer TYPE_REPORT = 1;
        //        图表分析类型
        public static final Integer TYPE_CHART = 2;

        //DATA_TYPE
        //        连续：开始年
        public static final Integer DATA_BEGIN_YEAR = 1;
        //        连续：开始期度
        public static final Integer DATA_BEGIN_PERIOD = 2;
        //        连续：结束年
        public static final Integer DATA_END_YEAR = 3;
        //        连续：结束期度
        public static final Integer DATA_END_PERIOD = 4;
        //        选择：年份
        public static final Integer DATA_YEAR = 5;
        //        选择：期度
        public static final Integer DATA_PERIOD = 6;
        //        报告期数
        public static final Integer DATA_NUMBER = 7;
        //元数据为时间,传的年的数据
        public static final int INFO_YEAR = 1;
        //元数据为时间,传的期度的数据
        public static final int INFO_PERIOD = 2;
    }

    /**
     * 图表结构
     */
    public static class STRUCTURE_TYPE {
        //分类轴
        public static final Integer CATEGORY = 0;
        //序列
        public static final Integer SERIES = 1;
    }

    /**
     * 节点类型
     */
    public static class NODE_TYPE {
        //虚节点
        public static final Integer EMPTY = 0;
        public static final String EMPTY_CH = "虚节点";
        //实节点
        public static final Integer REAL = 1;
        public static final String REAL_CH = "实节点";
    }

    /**
     * 主题内容类型
     */
    public static class THEME_CONTENT_TYPE {
        public static final int RPT_SYNTHESIZE = 1;//综合表
        public static final int RPT_CUSTOM = 2;//自定义表
        public static final int CHART = 3;//图表
        public static final int MAP = 4;//地图
        public static final int TEXT_THEME = 10;//分析主题
        public static final int TEXT_DESC = 5;//文字分析
        public static final int FILE = 6;//文件
        public static final int MENU = 7;//目录
        public static final int PAGE = 8;//页面
        public static final int DATA_SET = 9;//数据集
    }

    /**
     * 路径类型
     */
    public static class PATH_TYPE {
        public static final int RELATIVE_FILE = 1;//文件位置相对路径,如: theme_common/pages/index
        public static final int ABS_FILE = 2;//文件位置绝对路径,如: /C:/Users/Administrator
        public static final int PROJECT_PATH = 3;//项目的路径,如:support/sys/index,使用时需在前加上项目ip端口等信息:http://localhost:8088/dm_city
        public static final int WEB_PATH = 4;//外网路径,如: https://www.baidu.com
    }

    /**
     * 文字分析 关联数据内容
     */
    public static class TEXT_DATA_TYPE {
        public static final int RPT_SYNTHESIZE = THEME_CONTENT_TYPE.RPT_SYNTHESIZE;//综合表
        public static final int RPT_CUSTOM = THEME_CONTENT_TYPE.RPT_CUSTOM;//自定义表
        public static final int CHART = THEME_CONTENT_TYPE.CHART;//图表
        public static final int MAP = THEME_CONTENT_TYPE.MAP;//地图
    }

    /**
     * 文字分析内容状态
     */
    public static class TEXT_CONTENT_STATUS {
        public static final int WAIT_CHECK = 3;//待审核
        public static final int CHECKED = 4;//已审核,发布
        public static final int REJECT = 5;//已驳回
    }

    /**
     * 文字分析内容类型
     */
    public static class TEXT_CONTENT_TYPE {
        public static final int COMMON = 1;
        public static final String COMMON_CH = "普通分析";
    }

    /**
     * 文字分析类型
     */
    public static class TEXT_TYPE {
        public static final int THEME = 1;
        public static final int CONTENT = 2;
    }

    //    后台用户登录页面
    public static final String manageIndex = "/support/sys/index";
    //    前台用户登录页面
    public static final String userIndex = "/resourcecategory/themes/commonController/returnIndex";
}
