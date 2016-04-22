package com.city.support.regime.collection.service;

import com.city.common.util.ConvertUtil;
import com.city.support.regime.collection.dao.ExcelMapDao;
import com.city.support.regime.collection.dao.ImportRuleDao;
import com.city.support.regime.collection.entity.ExcelMap;
import com.city.support.regime.collection.entity.ImportRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wys on 2016/2/3.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImportRuleService {
    @Autowired
    private ImportRuleDao importRuleDao;
    @Autowired
    private ExcelMapDao excelMapDao;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public List<ImportRule> queryAllImportRule() {
        return importRuleDao.queryAll();
    }

    public void addImportRule(ImportRule importRule) {
        importRuleDao.insert(importRule, true);
    }

    public void updateImportRule(ImportRule importRule) {
        ConvertUtil<ImportRule> convertUtil = new ConvertUtil<>();
        ImportRule data = importRuleDao.queryById(importRule.getId());
        convertUtil.replication(importRule, data, ImportRule.class.getName());
        importRuleDao.update(data, true);
    }

    public void delImportRule(List<ImportRule> importRuleList) {
        for (ImportRule importRule : importRuleList) {
            importRuleDao.delete(importRule, true);
        }
    }

    public List queryExcelMap(Integer importRuleId) {
        ImportRule importRule = importRuleDao.queryById(importRuleId);
        List<ExcelMap> result = new ArrayList<>();
        result.addAll(importRule.getExcelMaps());
        return result;
    }

    public void addExcelMap(Integer importRuleId, ExcelMap excelMap) {
        ImportRule importRule = importRuleDao.queryById(importRuleId);
        for (ExcelMap tmpExcelMap : importRule.getExcelMaps()) {
            if (tmpExcelMap.getTmpId().equals(excelMap.getTmpId()))
                return;
        }
        excelMapDao.insert(excelMap, true);
        importRule.getExcelMaps().add(excelMap);
    }

    public void updateExcelMap(ExcelMap excelMap) {
        ConvertUtil<ExcelMap> convertUtil = new ConvertUtil<>();
        ExcelMap data = excelMapDao.queryById(excelMap.getId());
        convertUtil.replication(excelMap, data, ExcelMap.class.getName());
        excelMapDao.update(data, true);

    }

    public void delExcelMap(Integer importRuleId, List<ExcelMap> excelMap) {
        ImportRule importRule = importRuleDao.queryById(importRuleId);
        importRule.getExcelMaps().removeAll(excelMap);
        excelMapDao.flush();
        excelMapDao.clearSession();
        for (ExcelMap tmpExcelMap : excelMap) {
            excelMapDao.delete(tmpExcelMap, true);
        }
    }


}
