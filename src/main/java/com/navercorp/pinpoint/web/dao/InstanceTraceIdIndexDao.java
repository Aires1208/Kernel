package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;

import java.util.Set;

/**
 * Created by root on 16-11-14.
 */
public interface InstanceTraceIdIndexDao {
    Set<TransactionId> findInstanceTransactionIds(String agentId, Range range);

    Set<TransactionId> findTransactionIds(String agentId, String traceName ,Range range);
}
