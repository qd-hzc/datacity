package com.city.resourcecategory.analysis.common.service;

import com.city.common.pojo.Constant;
import com.city.resourcecategory.analysis.common.entity.TimeRangeEntity;
import com.city.support.sys.user.entity.User;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxl on 2016/3/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration
        (locations = {"classpath:spring-context.xml",
                "classpath:spring-hibernate.xml", "classpath:spring-mvc.xml"})
public class TestTimeRangeService {
    @Autowired
    private TimeRangeService timeRangeService;

    private Integer foreignType = Constant.TIMERANGE.TYPE_REPORT;
    private Integer foreignId = 1;

    @Test
    public void testQuery() {
        List<TimeRangeEntity> ranges = timeRangeService.queryTimeRange(foreignType, foreignId);
        System.out.println(new Gson().toJson(ranges));
    }

    @Test
    public void testSave1() {
        //添加连续
        //开始年
        int lianxu = Constant.TIMERANGE.LIANXU;
        List<TimeRangeEntity> ranges = new ArrayList<>();

        TimeRangeEntity range = new TimeRangeEntity();
        range.setType(lianxu);
        range.setDataType(Constant.TIMERANGE.DATA_BEGIN_YEAR);
        range.setDataValue(2015);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(lianxu);
        range.setDataType(Constant.TIMERANGE.DATA_BEGIN_PERIOD);
        range.setDataValue(Constant.FrequencyType.QUARTER_1);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(lianxu);
        range.setDataType(Constant.TIMERANGE.DATA_END_YEAR);
        range.setDataValue(2016);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(lianxu);
        range.setDataType(Constant.TIMERANGE.DATA_END_PERIOD);
        range.setDataValue(Constant.FrequencyType.QUARTER_4);
        ranges.add(range);

        User user = new User();
        String str = new Gson().toJson(ranges);
        timeRangeService.saveTimeRange(str, foreignType, foreignId, user);
    }

    @Test
    public void testSave2() {
        //添加选择
        //开始年
        int xuanze = Constant.TIMERANGE.XUANZE;
        List<TimeRangeEntity> ranges = new ArrayList<>();

        TimeRangeEntity range = new TimeRangeEntity();
        range.setType(xuanze);
        range.setDataType(Constant.TIMERANGE.DATA_YEAR);
        range.setDataValue(2015);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(xuanze);
        range.setDataType(Constant.TIMERANGE.DATA_YEAR);
        range.setDataValue(2014);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(xuanze);
        range.setDataType(Constant.TIMERANGE.DATA_YEAR);
        range.setDataValue(2013);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(xuanze);
        range.setDataType(Constant.TIMERANGE.DATA_PERIOD);
        range.setDataValue(Constant.FrequencyType.QUARTER_2);
        ranges.add(range);

        range = new TimeRangeEntity();
        range.setType(xuanze);
        range.setDataType(Constant.TIMERANGE.DATA_PERIOD);
        range.setDataValue(Constant.FrequencyType.QUARTER_4);
        ranges.add(range);

        String str = new Gson().toJson(ranges);
        User user = new User();
        timeRangeService.saveTimeRange(str, foreignType, foreignId, user);
    }

    @Test
    public void testSave3() {
        int baogaoqi = Constant.TIMERANGE.BAOGAOQI;
        List<TimeRangeEntity> ranges = new ArrayList<>();

        TimeRangeEntity range = new TimeRangeEntity();
        range.setType(baogaoqi);
        range.setDataType(Constant.TIMERANGE.DATA_NUMBER);
        range.setDataValue(4);
        ranges.add(range);

        String str = new Gson().toJson(ranges);
        User user = new User();
        timeRangeService.saveTimeRange(str, foreignType, foreignId, user);
    }
}
