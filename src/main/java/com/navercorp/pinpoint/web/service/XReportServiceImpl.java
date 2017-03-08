package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.TransactionListDao;
import com.navercorp.pinpoint.web.mapper.TransactionListMapper;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.util.SubListUtils;
import com.navercorp.pinpoint.web.view.XAppReport;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.*;
import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.navercorp.pinpoint.web.view.StringWrapper.DefaultDateStr;

/**
 * Created by root on 16-9-21.
 */
@Service
public class XReportServiceImpl {

    private static final int DEFAULT_TOPN = 5;
    private static final int DEFAULT_SAMPLE_COUNT = 10;

    @Autowired
    private XDBServiceImpl dbService;

    @Autowired
    private XEventService eventService;

    @Autowired
    private XApplicationsService xApplicationsService;

    @Autowired
    private XTransactionServiceImpl xTransactionService;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private TransactionListDao transactionListDao;

    void setEventService(XEventService eventService) {
        this.eventService = eventService;
    }

    public XAppReport getAppReport(String defaultAppName, Integer defaultTopN, Range range) {
        List<String> appList = getAppList();
        Preconditions.checkArgument(!appList.isEmpty(), "application not found");

        String appName = defaultAppName;
        if (!appList.contains(appName)) {
            appName = appList.get(0);
        }

        Integer topN = defaultTopN;
        if (defaultTopN == null) {
            topN = DEFAULT_TOPN;
        }

        XAppReport xAppReport = new XAppReport(appName);
        xAppReport.setApplist(appList);

        //build transaction health
        XHealth transactionHealth = calcTransHealth(appName, range);
        xAppReport.setTransactionHealth(transactionHealth);

        //build service health
        XHealth serviceHealth = calcServicesHealth(appName, range);
        xAppReport.setServiceHealth(serviceHealth);

        //build application Health
        XHealth appHealth = mergeHealth(transactionHealth, serviceHealth);
        xAppReport.setApplicationHealth(appHealth);

        //risk info
        xAppReport.setTransactionRisk(findTransactionRisk(appName, topN, range));
        xAppReport.setServiceRisk(findServicesRisk(appName, topN, range));
        xAppReport.setDBsRisk(dbService.getXDBsRisk(appName, topN, range));

        return xAppReport;
    }

    private XHealth calcTransHealth(String appName, Range range) {
        Set<String> traceNameList = getTransactionNames(appName, range);
        List<ResultEvent> events = eventService.getAppTransactionEvents(appName, range);
        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(events, traceNameList.size());
        XHealth health = new XHealth(userCase.getScore());

        health.setWarning(userCase.getWarning());
        health.setCritical(userCase.getCritical());

        List<Double> healths = newArrayList();
        List<String> timestamps = newArrayList();
        for (Range splitRange : range.splitRange(DEFAULT_SAMPLE_COUNT, 0)) {
            timestamps.add(DefaultDateStr(splitRange.getAvr()));

            List<ResultEvent> splitEvents = eventService.getAppTransactionEvents(appName, splitRange);
            StatisticsEventsUserCase splitUsercase = new StatisticsEventsUserCase(splitEvents, traceNameList.size());
            healths.add(splitUsercase.getScore());
        }

        health.setTimestamps(timestamps);
        health.setHealths(healths);

        return health;
    }

    private XHealth calcServicesHealth(String appName, Range range) {
        List<XService> serviceList = xApplicationsService.getXServices(appName);

        List<ResultEvent> events = getServicesEvents(appName, range, serviceList);


        StatisticsEventsUserCase useCase = new StatisticsEventsUserCase(events, serviceList.size());


        XHealth health = new XHealth(useCase.getScore());
        health.setWarning(useCase.getWarning());
        health.setCritical(useCase.getCritical());

        List<Double> healths = newArrayList();
        List<String> timestamps = newArrayList();
        for (Range splitRange : range.splitRange(DEFAULT_SAMPLE_COUNT, 0)) {
            timestamps.add(DefaultDateStr(splitRange.getAvr()));

            List<ResultEvent> serviceEvents = getServicesEvents(appName, splitRange, serviceList);
            StatisticsEventsUserCase splitUserCase = new StatisticsEventsUserCase(serviceEvents, serviceList.size());
            healths.add(splitUserCase.getScore());
        }

        health.setHealths(healths);
        health.setTimestamps(timestamps);

        return health;
    }

    private XTransactionsRisk findTransactionRisk(String appName, Integer topN, Range range) {
        XTransactions xTransactions = xTransactionService.getXAppTransactions(appName, range);

        if (xTransactions.getXBusinessTransactions().size() == 0 || xTransactions.getXBusinessTransactions() == null) {
            return new XTransactionsRisk(newArrayList(new XDataPoint<>("", 0)),
                    newArrayList(new XDataPoint<>("", 0)),
                    newArrayList(new XDataPoint<>("", 0L)));
        }

        List<XBusinessTransactions> transactionsList = newArrayList(xTransactions.getXBusinessTransactions());

        Collections.sort(transactionsList, (transactions1, transactions2) ->
                Objects.equals(transactions1.getCalls(), transactions2.getCalls()) ? 0 :
                        (((transactions1.getCalls() - transactions2.getCalls()) > 0) ? -1 : 1));
        List<XBusinessTransactions> topNTranxCalls = transactionsList.subList(0, transactionsList.size() <= topN ? transactionsList.size() : topN);
        List<XDataPoint<Integer>> topNCalls = SubListUtils.getTopNCallsTransactions(topNTranxCalls);


        Collections.sort(transactionsList, (transactions1, transactions2) ->
                Objects.equals(transactions1.getErrors(), transactions2.getErrors()) ? 0 :
                        (transactions1.getErrors() - transactions2.getErrors() > 0 ? -1 : 1));
        List<XBusinessTransactions> topNTranxErrors = transactionsList.subList(0, transactionsList.size() <= topN ? transactionsList.size() : topN);
        List<XDataPoint<Integer>> topNErrors = SubListUtils.getTopNErrorsTransactions(topNTranxErrors);


        Collections.sort(transactionsList, (transactions1, transactions2) -> {
            long responseTime1 = transactions1.getAverageResponseTime();
            long responseTime2 = transactions2.getAverageResponseTime();
            return responseTime1 == responseTime2 ? 0 : (responseTime1 - responseTime2 > 0 ? -1 : 1);
        });
        List<XBusinessTransactions> topNTranxElapsed = transactionsList.subList(0, transactionsList.size() <= topN ? transactionsList.size() : topN);
        List<XDataPoint<Long>> topNResponseTime = SubListUtils.getTopNResponseTimeTransactions(topNTranxElapsed);

        return new XTransactionsRisk(topNCalls, topNErrors, topNResponseTime);
    }

    private XServicesRisk findServicesRisk(String appName, Integer topN, Range range) {
        List<XService> serviceList = xApplicationsService.getXServices(appName);
        List<XServiceHealth> serviceHealthList = newArrayList();
        serviceHealthList.addAll(serviceList.stream().map(xService -> queryServiceHealth(appName, xService.getName(), range)).collect(Collectors.toList()));

        Collections.sort(serviceHealthList, (serviceHealth1, serviceHealth2) -> serviceHealth1.getCalls() == serviceHealth2.getCalls() ? 0: (serviceHealth1.getCalls() - serviceHealth2.getCalls() > 0 ? -1 : 1));
        List<XServiceHealth> topNXServicebyCalls = serviceHealthList.subList(0, serviceHealthList.size() <= topN ? serviceHealthList.size() : topN);
        List<XDataPoint<Integer>> topNCalls = SubListUtils.getTopNCallsServices(topNXServicebyCalls);

        Collections.sort(serviceHealthList, (serviceHealth1, serviceHealth2) -> serviceHealth1.getHealthScore() == serviceHealth2.getHealthScore() ? 0 : (serviceHealth1.getHealthScore() - serviceHealth2.getHealthScore() > 0 ? 1 : -1));
        List<XServiceHealth> topNXServicebyHealth = serviceHealthList.subList(0, serviceHealthList.size() <= topN ? serviceHealthList.size() : topN);
        List<XDataPoint<Double>> topNRisk = SubListUtils.getTopNRiskServices(topNXServicebyHealth);

        return new XServicesRisk(topNCalls, topNRisk);
    }

    private XServiceHealth queryServiceHealth(String appName, String serviceName, Range range) {
        List<String> agentIds = applicationIndexDao.selectAgentIds(serviceName);
        List<ResultEvent> eventList = getInstanceEvents(appName, serviceName, agentIds, range);
        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventList, agentIds.size());

        double score = userCase.getScore();
        Integer calls = xTransactionService.getXServiceTransactions(serviceName, range).getCalls();
        return new XServiceHealth(serviceName, calls, score);
    }


    private XHealth mergeHealth(XHealth transactionHealth, XHealth serviceHealth) {
        double appScore = (transactionHealth.getScore() + serviceHealth.getScore()) / 2;
        XHealth health = new XHealth(appScore);

        health.setTimestamps(transactionHealth.getTimestamps());

        List<Double> tmpHealth = newArrayList();
        for (int i = 0; i < DEFAULT_SAMPLE_COUNT; i++) {
            tmpHealth.add((transactionHealth.getHealths().get(i) + serviceHealth.getHealths().get(i)) / 2);
        }
        health.setHealths(tmpHealth);

        return health;
    }

    private List<ResultEvent> getServicesEvents(String appName, Range range, List<XService> serviceList) {
        List<ResultEvent> events = newArrayList();
        for (XService service : serviceList) {
            events.addAll(eventService.getServiceEvents(appName, service.getName(), range));
        }
        return events;
    }

    private List<ResultEvent> getInstanceEvents(String appName, String serviceName, List<String> agentIds, Range range) {
        List<ResultEvent> events = newArrayList();
        for (String agentId : agentIds) {
            events.addAll(eventService.getInstanceEvents(appName, serviceName, agentId, range));
        }
        return events;
    }

    private List<String> getAppList() {
        List<String> appList = newArrayList();
        appList.addAll(xApplicationsService.getXApplications().stream().map(XApplication::getName).collect(Collectors.toList()));
        return appList;
    }

    private Set<String> getTransactionNames(String appName, Range range) {
        List<XService> xServices = xApplicationsService.getXServices(appName);
        Set<String> traceNames = newHashSet();
        for (XService xService : xServices) {
            List<XTransactionName> transactionNames = transactionListDao.getServiceTracesList(xService.getName(), range);

            traceNames.addAll(transactionNames.stream().map(XTransactionName::getTransactionName).collect(Collectors.toList()));
        }

        return traceNames;
    }
}
