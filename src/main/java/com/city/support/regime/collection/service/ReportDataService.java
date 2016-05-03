package com.city.support.regime.collection.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.RptTmpWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Constant;
import com.city.support.regime.collection.dao.ReportDataDao;
import com.city.support.regime.collection.entity.ReportData;
import com.city.support.regime.collection.entity.ReportDataId;
import com.city.support.regime.collection.entity.ReportInfo;
import com.city.support.regime.collection.pojo.RptHtmlPojo;
import com.city.support.regime.collection.util.SystemLogUtils;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wgx on 2016/2/15.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportDataService {
    @Autowired
    private ReportDataDao reportDataDao;
    @Autowired
    private ReportInfoService reportInfoService;

    private EsiEventWatched esiEventWatched;
    private EsiEventWatched esiEventWatched1;
    @Autowired
    public ReportDataService(RptTmpWatched rptTmpWatched,SystemLogWatched systemLogWatched) {
        esiEventWatched = rptTmpWatched;
        esiEventWatched1 = systemLogWatched;
        esiEventWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                Map<String, Object> args = eEvent.getArgs();
                switch (eEvent.getEventName()) {
                    case RptTmpWatched.BEFOREDELETETMP://报表模板删除前
                        String tmpIds = (String)args.get(RptTmpWatched.PARAMS_TMPIDS);
                        removeRptDatas(tmpIds);
                        break;
                }
                return true;
            }
        },null);
    }

    /**
     *
     * @param map                 数据信息
     * @param reportDataId
     * @param value               值
     * @param tmpId
     * @param unitId
     * @param unit
     * @param dataFormat
     * @param dataType
     * @param collectionType
     * @param explain
     */
    public void saveOrSubmitRptData(Map<String, String> map,ReportDataId reportDataId, String value,Integer tmpId,Integer unitId,String unit,String dataFormat,Integer dataType,Integer collectionType,String explain) {
        //{'itemcaliber':'','area':'16','itemgroup':21,'item':63,'timeframe':9,'surobjgroup':42.0,'depid':'121','surobj':3813.0}
        ReportData rptData = getReportDataByReportDataId(reportDataId);
        if(rptData==null) {//如果不存在
            ReportData reportData = new ReportData();
            reportData.setReportDataId(reportDataId);
            if (map.get("itemgroup") != null) {
                reportData.setItemgroup((int) (double) Double.valueOf(map.get("itemgroup")));
            }
            if (map.get("surobjgroup") != null) {
                reportData.setSurobjgroup((int) (double) Double.valueOf(map.get("surobjgroup")));
            }
            reportData.setItemValue(value);
            reportData.setCell("");//加工表使用
            if (unitId != null) {
                reportData.setUnitId(unitId);
            } else {
                reportData.setUnitId(0);
            }
            if (unit != null && !"".equals(unit)) {
                reportData.setUnit(unit);
            } else {
                reportData.setUnit("");
            }
            if (tmpId != null) {
                reportData.setRptTmpId(tmpId);
            } else {
                reportData.setRptTmpId(0);
            }
            if (dataType != null) {
                reportData.setDataType(dataType);
            } else {
                reportData.setDataType(Constant.DATA_TYPE.NUMBER);
            }
            reportData.setDataFormat(dataFormat);
            reportData.setDataStatus(Constant.DATA_STATUS.UNUSABLE);
            if (collectionType != null) {
                reportData.setCollectionType(collectionType);
            } else {
                reportData.setCollectionType(Constant.COLLECTION_TYPE.CALCULATE);
            }
            reportData.setExplain(explain);

            reportDataDao.saveOrUpdate(reportData, true);
        }

    }
    private void sendListener(User user, Integer operateType, ReportInfo reportInfo, String method,List<ReportData> reportDataList) {
        List<SystemLog> systemLogList = null;
        systemLogList = SystemLogUtils.createReportDataLog(user, operateType, reportInfo, method, reportDataList);
        //添加并触发事件
        EsiEvent esiEvent = new EsiEvent();
        esiEvent.setEventName(SystemLogWatched.SYS_LOG_LIST);
        esiEvent.getArgs().put(SystemLogWatched.SYS_LOG_LIST,systemLogList);
        esiEventWatched1.notifyAllListener(esiEvent);
    }
    /**
     *
     * @param rptHtml
     */
    public ReportData saveOrSubmitRptData(User user,RptHtmlPojo rptHtml,Integer rptStatus,ReportInfo reportInfo) {
        Map<String, String> map = rptHtml.getMap();
        ReportDataId reportDataId = rptHtml.getReportDataId();
        String value = rptHtml.getValue();
        Integer tmpId = rptHtml.getTmpId();
        Integer unitId = rptHtml.getUnitId();
        String unit = rptHtml.getUnit();
        String dataFormat = rptHtml.getDataFormat();
        Integer dataType = rptHtml.getDataType();
        Integer collectionType = rptHtml.getCollectionType();
        String explain = rptHtml.getExplain();
        String time = rptHtml.getTime();
        //ReportData rptData = getReportDataByReportDataId(reportDataId);
        if(Constant.RPT_STATUS.PASS!=rptStatus&&Constant.RPT_STATUS.WAITING_PASS!=rptStatus) {//如果不存在
            ReportData reportData = new ReportData();
            reportData.setReportDataId(reportDataId);
            if (map.get("itemgroup") != null) {
                reportData.setItemgroup((int) (double) Double.valueOf(map.get("itemgroup")));
            }
            if (map.get("surobjgroup") != null) {
                reportData.setSurobjgroup((int) (double) Double.valueOf(map.get("surobjgroup")));
            }
            reportData.setItemValue(value);
            reportData.setCell("");//加工表使用
            if (unitId != null) {
                reportData.setUnitId(unitId);
            } else {
                reportData.setUnitId(Constant.DEFAULT_ITEM_MENU);
            }
            if (unit != null && !"".equals(unit)) {
                reportData.setUnit(unit);
            } else {
                reportData.setUnit("");
            }
            if (tmpId != null) {
                reportData.setRptTmpId(tmpId);
            } else {
                reportData.setRptTmpId(Constant.DEFAULT_ITEM_MENU);
            }
            if (time != null&& !"".equals(time)) {
                reportData.setTime(time);
            } else {
                reportData.setTime("");
            }
            if (dataType != null) {
                reportData.setDataType(dataType);
            } else {
                reportData.setDataType(Constant.DATA_TYPE.NUMBER);
            }
            reportData.setDataFormat(dataFormat);
            reportData.setDataStatus(Constant.DATA_STATUS.UNUSABLE);
            if (collectionType != null) {
                reportData.setCollectionType(collectionType);
            } else {
                reportData.setCollectionType(Constant.COLLECTION_TYPE.CALCULATE);
            }
            reportData.setExplain(explain);
            reportDataDao.saveOrUpdate(reportData, true);
            return reportData;
        }
        return null;
    }
    public ReportData getReportDataByReportDataId(ReportDataId reportDataId){
        return reportDataDao.queryById(reportDataId);
    }

    public List<ReportData> getReportDataByRptId(Integer rptId){
        return reportDataDao.queryByRptId(rptId);
    }
    private void removeRptDatas(String tmpIds) {
        reportDataDao.removeRptDatasByTmpIds(tmpIds);
    }
    private void removeRptDatasByInfoId(String infoIds) {
        reportDataDao.removeRptDatasByTmpIds(infoIds);
    }

    /**
     * 批量添加数据，方便提高记录日志效率
     * @param user
     * @param rptHtmlList
     * @param rptStatus
     * @param reportInfo
     */
    public void saveOrSubmitRptDataList(User user, List<RptHtmlPojo> rptHtmlList, Integer rptStatus, ReportInfo reportInfo) {
        List<ReportData> reportDataList = new ArrayList<>();
        for (RptHtmlPojo rptHtml : rptHtmlList) {
            ReportData reportData = saveOrSubmitRptData(user, rptHtml, rptStatus, reportInfo);
            if(reportData!=null){
                reportDataList.add(reportData);
            }
        }
        // 添加触发事件
        sendListener(user, SystemLog.UPDATE, reportInfo, Thread.currentThread().getStackTrace()[1].getMethodName(), reportDataList);
    }
}
