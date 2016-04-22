package com.city.support.regime.collection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wgx on 2016/2/23.
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class ReportReviewService {
    public void setrptReviewResult(String ids, int rptStatus) {

    }
}
