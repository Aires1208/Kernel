package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.util.SplitListUtils;
import com.navercorp.pinpoint.web.util.TraceUtils;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Service
public class XTransactionServiceImpl {
    private static final int DEFAULT_TRACE_SPLIT_COUNT = 100;
    private static final String APPLICATION = "application";
    private static final String SERVICE = "service";
    private static final String INSTANCE = "instance";

    @Autowired
    private XEventService eventService;

    @Autowired
    private TraceDao traceDao;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Autowired
    private XApplicationsService xApplicationsService;

    @Autowired
    private XInstanceServiceImpl xInstanceService;

    public XTransactions getTransactions(String level, XTraceQuery query) {
        XTransactions xTransactions = new XTransactions(newHashMap(), query.getRange(), getAppList());

        switch (level) {
            case APPLICATION:
                xTransactions = getXAppTransactions(query.getApplication(), query.getRange());
                break;
            case SERVICE:
                xTransactions = getXServiceTransactions(query.getService(), query.getRange());
                break;
            case INSTANCE:
                xTransactions = getInstanceTransactions(query.getInstance(), query.getRange());
                break;
            default:
                break;
        }

        setTraceHealth(xTransactions.getTransactionsMap(), level, query);

        return xTransactions;
    }

    private void setTraceHealth(Map<XTransactionName, XBusinessTransactions> transactionsMap, String level, XTraceQuery query) {
        Map<String, List<ResultEvent>> traceEventsMap = buildMap(getEvents(level, query));

        for (Map.Entry<XTransactionName, XBusinessTransactions> entry : transactionsMap.entrySet()) {
            String objDN = buildDN(entry.getKey().getTransactionName(), level, query);

            List<ResultEvent> events = traceEventsMap.get(objDN);

            StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(events, 0);
            entry.getValue().setHealth(userCase.getLevel().getDesc());
        }
    }

    private String buildDN(String key, String level, XTraceQuery query) {
        switch (level) {
            case APPLICATION:
                return "app=" + query.getApplication() + ",name=" + key;
            case SERVICE:
                return "app=" + query.getApplication() + ",service=" + query.getService() + ",name=" + key;
            case INSTANCE:
                return "app=" + query.getApplication() + ",service=" + query.getService() + ",instance=" + query.getInstance() + ",name=" + key;
            default:
                return "";
        }
    }

    private Map<String, List<ResultEvent>> buildMap(List<ResultEvent> events) {
        Map<String, List<ResultEvent>> traceEventsMap = newHashMap();
        if (!CollectionUtils.isEmpty(events)) {
            for (ResultEvent event : events) {
                List<ResultEvent> traceEvents = traceEventsMap.get(event.getObjDN());
                if (CollectionUtils.isEmpty(traceEvents)) {
                    traceEventsMap.put(event.getObjDN(), newArrayList(event));
                } else {
                    traceEvents.add(event);
                }
            }
        }

        return traceEventsMap;
    }

    public XTransactions getXAppTransactions(String appName, Range range) {
        Preconditions.checkArgument(null != appName, "appName must not be null!");

        List<XService> xServiceList = xApplicationsService.getXServices(appName);

        List<XBusinessTransaction> xBusinessTransactions = newArrayList();

        for (XService xService : xServiceList) {
            Set<TransactionId> transactionIds = getTransactionIds(xService.getName(), range);
            xBusinessTransactions.addAll(queryTransactions(transactionIds));
        }

        return buildXTransactions(xBusinessTransactions, range);
    }

    public XTransactions getXServiceTransactions(String serviceName, Range range) {
        Preconditions.checkArgument(null != serviceName, "serviceName must not be null!");

        List<XBusinessTransaction> xBusinessTransactions = queryTransactions(getTransactionIds(serviceName, range));

        return buildXTransactions(xBusinessTransactions, range);
    }

    public XTransactions getInstanceTransactions(String instanceName, Range range) {
        Preconditions.checkArgument(null != instanceName, "instanceName must not be null!");

        Set<TransactionId> transactionIds = getTransactionIds(xInstanceService.getXServiceName(instanceName), range);
        List<XBusinessTransaction> xBusinessTransactions = newArrayList();
        for (TransactionId transactionId : transactionIds) {
            List<SpanBo> spanBos = traceDao.selectSpan(transactionId);
            if (isTraced(instanceName, spanBos)) {
                xBusinessTransactions.add(new XBusinessTransaction(transactionId, spanBos).build());
            }
        }

        return buildXTransactions(xBusinessTransactions, range);
    }

    private Set<TransactionId> getTransactionIds(String serviceName, Range range) {
        LimitedScanResult<List<TransactionId>> limitedScanResult =
                applicationTraceIndexDao.scanTraceIndex(serviceName, range, 5000, false);
        List<TransactionId> transactionIds = limitedScanResult.getScanData();

        return newHashSet(transactionIds);
    }

    private List<XBusinessTransaction> queryTransactions(Set<TransactionId> transactionIds) {
        List<XBusinessTransaction> xBusinessTransactions = newArrayList();

        List<List<TransactionId>> splitTransactionIdList =
                SplitListUtils.splitTransactionIdList(newArrayList(transactionIds), DEFAULT_TRACE_SPLIT_COUNT);

        for (List<TransactionId> transactionIdList : splitTransactionIdList) {
            List<List<SpanBo>> traces = traceDao.selectAllSpans(transactionIdList);

            xBusinessTransactions.addAll(traces.stream().map(trace
                    -> new XBusinessTransaction(getTransactionId(trace), trace).build()).collect(Collectors.toList()));
        }
//        for (TransactionId transactionId : transactionIds) {
//            List<SpanBo> spanBos = traceDao.selectSpan(transactionId);
//            xBusinessTransactions.add(new XBusinessTransaction(transactionId, spanBos).build());
//        }

        return xBusinessTransactions;
    }

    private TransactionId getTransactionId(List<SpanBo> trace) {
        Preconditions.checkNotNull(!CollectionUtils.isEmpty(trace));
        String transactionId = trace.get(0).getTransactionId();
        return TraceUtils.parseTransactionId(transactionId);
    }

    private XTransactions buildXTransactions(List<XBusinessTransaction> transactions, Range range) {
        Map<XTransactionName, XBusinessTransactions> transactionsMap = newHashMap();
        for (XBusinessTransaction businessTransaction : transactions) {
            String traceName = businessTransaction.getTraceName();

            XTransactionName xTransactionName =
                    new XTransactionName(businessTransaction.getService(), businessTransaction.getAgentId(), traceName);
            XBusinessTransactions xBusinessTransactions = transactionsMap.get(xTransactionName);
            if (xBusinessTransactions == null) {
                xBusinessTransactions = new XBusinessTransactions(xTransactionName);
                transactionsMap.put(xTransactionName, xBusinessTransactions);
            }
            xBusinessTransactions.add(businessTransaction);
        }
        return new XTransactions(transactionsMap, range, getAppList());
    }

    private List<ResultEvent> getEvents(String level, XTraceQuery query) {
        switch (level) {
            case APPLICATION:
                return eventService.getAppTransactionEvents(query.getApplication(), query.getRange());
            case SERVICE:
                return eventService.getServiceTransactionEvents(query.getApplication(), query.getService(), query.getRange());
            case INSTANCE:
                return eventService.getInstanceTransactionEvents(query.getApplication(),
                        query.getService(), query.getInstance(), query.getRange());
            default:
                break;
        }

        return newArrayList();
    }

    private List<XApplication> getAppList() {
        List<XApplication> xApplications = xApplicationsService.getXApplications();
        for (XApplication app : xApplications) {
            for (XService service : app.getXServices()) {
                service.setAgentIds(applicationIndexDao.selectAgentIds(service.getName()));
            }
        }
        return xApplications;
    }

    private boolean isTraced(String name, List<SpanBo> spanBos) {
        for (SpanBo spanBo : spanBos) {
            if (spanBo.getAgentId().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
