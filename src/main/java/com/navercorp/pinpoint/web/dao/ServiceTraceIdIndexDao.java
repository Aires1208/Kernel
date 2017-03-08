package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;

import java.util.Set;

/**
 * Created by root on 16-11-14.
 */
public interface ServiceTraceIdIndexDao {
    Set<TransactionId> findServiceTranceIds(String serviceName, Range range);

    Set<TransactionId> findServiceTraceIdsByTraceName(String serviceName, String traceName , Range range);
}
