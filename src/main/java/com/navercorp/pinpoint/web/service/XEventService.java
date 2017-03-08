package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

public interface XEventService {
    List<ResultEvent> getInstanceEvents(String appName, String serviceName, String instanceName, Range range);

    List<ResultEvent> getServiceEvents(String appName, String serviceName, Range range);

    List<ResultEvent> getAppEvents(String appName, Range range);

    List<ResultEvent> getInstanceTransactionEvents(String appName, String serviceName, String instanceName, Range range);

    List<ResultEvent> getServiceTransactionEvents(String appName, String serviceName, Range range);

    List<ResultEvent> getAppTransactionEvents(String appName, Range range);

    List<ResultEvent> getHostEvents(String hostId, Range range);
}
