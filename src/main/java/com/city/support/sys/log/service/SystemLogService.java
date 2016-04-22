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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
     * @return
     */
    public List<SystemLog> findByOrder(Page page) {
        if (page != null) {
            return systemLogDao.getPageByOrder(page);

        }
        return systemLogDao.getByOrder();
    }

    /**
     * 返回日志数量
     *
     * @return
     */
    public Integer getLogCount() {
        return systemLogDao.selectLogCount();
    }
}
