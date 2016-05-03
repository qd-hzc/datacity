package com.city.support.sys.log.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.EsiEventWatched;
import com.city.common.event.watcher.SystemLogWatched;
import com.city.common.pojo.Page;
import com.city.common.util.SessionUtil;
import com.city.support.sys.log.dao.SystemLogDao;
import com.city.support.sys.log.entity.SystemLog;
import com.city.support.sys.user.entity.User;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wgx on 2016/2/24.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class SystemLogService {
    @Autowired
    SystemLogDao systemLogDao;

    public void save(SystemLog systemLog) {
        systemLogDao.insert(systemLog, true);
    }

    private EsiEventWatched esiEventWatched;

    /**
     * 系统日志监听
     *
     * @param systemLogWatched
     */
    @Autowired
    public SystemLogService(SystemLogWatched systemLogWatched) {
        esiEventWatched = systemLogWatched;
        EsiListenerAdapter listener = new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                if (SystemLogWatched.SYS_LOG.equals(eEvent.getEventName())) {
                    saveLog(eEvent);
                } else if (SystemLogWatched.SYS_LOG_LIST.equals(eEvent.getEventName())) {
                    saveLogList(eEvent);
                }
                return true;
            }
        };
        esiEventWatched.addListener(listener, null);
    }

    /**
     * 批量保存日志
     *
     * @param eEvent
     */
    private void saveLogList(EsiEvent eEvent) {
        List<SystemLog> list = (List) eEvent.getArgs().get(SystemLogWatched.SYS_LOG_LIST);
        for (int i = 0; i < list.size(); i++) {
            SystemLog log = list.get(i);
            systemLogDao.saveOrUpdate(log, true);
        }
    }

    /**
     * 保存日志
     *
     * @param eEvent
     */
    private void saveLog(EsiEvent eEvent) {
        //保存日志
        SystemLog systemLog = (SystemLog) eEvent.getArgs().get(SystemLogWatched.SYS_LOG);
        systemLogDao.saveOrUpdate(systemLog, false);
    }


    /**
     * 返回所有系统日志
     *
     * @param name
     * @param sDate
     * @param eDate
     * @return
     */
    public List<SystemLog> findByOrder(String name, Date sDate, Date eDate) {
        return systemLogDao.getByOrder(name, sDate, eDate);
    }

    /**
     * 返回日志数量
     *
     * @param name
     * @param sDate
     * @param eDate
     * @return
     */
    public Integer getLogCount(String name, Date sDate, Date eDate) {
        return systemLogDao.selectLogCount(name, sDate, eDate);
    }

    /**
     * 根据搜索内容查询系统日志
     *
     * @param page
     * @param name  名称
     * @param sDate 开始时间
     * @param eDate 结束时间
     * @return
     * @author crx
     * @createDate 2016-4-27
     */
    public List<SystemLog> findSystemLogByCondition(Page page, String name, Date sDate, Date eDate) {
        return systemLogDao.findSystemLogByCondition(page, name, sDate, eDate);
    }

    /**
     * 返回Excel列表
     *
     * @param response
     * @param text     表格列名称
     * @param list     按照查询条件得到要导出为Excel的系统日志
     * @return
     */
    public Object getExcel(HttpServletResponse response, String text, List<SystemLog> list) {
        return systemLogDao.createExcel(response, text, list);
    }
}