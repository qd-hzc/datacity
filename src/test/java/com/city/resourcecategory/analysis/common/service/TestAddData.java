package com.city.resourcecategory.analysis.common.service;

import com.city.common.BaseTest;
import com.city.common.util.ConvertUtil;
import com.city.support.regime.collection.dao.ReportDataDao;
import com.city.support.regime.collection.entity.ReportData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxl on 2016/3/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestAddData extends BaseTest {
    @Autowired
    private ReportDataDao reportDataDao;

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void testAdd() {
        List<ReportData> datas = reportDataDao.queryByRptId(150);

        ReportData data0 = datas.get(0);
        //插入10000条数据 并读取

        ConvertUtil<ReportData> util = new ConvertUtil<>();
        String name = ReportData.class.getName();
        ReportData data = null;
        int year = 12015;
        for (int i = 0; i < 10000; i++) {
            data = new ReportData();
            util.replication(data0, data, name);
//            data.setYear(++year);
//            data.setMonth(12);
//            data.getReportDataId().setTime(year + "年12月");
//            System.out.println(data.getReportDataId().getTime());
//            reportDataDao.insert(data, true);
        }
        reportDataDao.getSession().flush();
        System.out.println("10000条数据已全部插入!");
    }

}
