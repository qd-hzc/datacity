package com.city.app.dataDict.icon.service;

import com.city.app.dataDict.icon.dao.AppDataDictMenuIconDao;
import com.city.app.dataDict.icon.entity.AppDataDictMenuIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxl on 2016/3/30.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class AppDataDictMenuIconService {
    @Autowired
    private AppDataDictMenuIconDao appDataDictMenuIconDao;

    /**
     * 根据名字查询
     */
    public List<AppDataDictMenuIcon> queryByType(String name,Integer type) {
        return appDataDictMenuIconDao.queryByType(name,type);
    }
}
