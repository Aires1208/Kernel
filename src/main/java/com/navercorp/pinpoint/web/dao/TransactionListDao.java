package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;

import java.util.List;


/**
 * Created by root on 16-10-17.
 */
public interface TransactionListDao {
    List<XTransactionName> scan();
    List<XTransactionName> getServiceTracesList(String serviceName, Range range);
}
