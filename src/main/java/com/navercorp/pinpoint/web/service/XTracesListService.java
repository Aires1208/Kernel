package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;

import java.util.List;

/**
 * Created by root on 16-11-21.
 */
public interface XTracesListService {
    List<XTransactionName> getAppTracesList(String appName, Range range);

    List<XTransactionName> getServiceTracesList(String serviceName, Range range);

    List<XTransactionName> getInstTracesList(String serviceName, String instanceName, Range range);
}
