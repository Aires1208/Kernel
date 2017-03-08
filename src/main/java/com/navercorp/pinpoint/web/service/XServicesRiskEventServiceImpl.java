package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.view.*;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class XServicesRiskEventServiceImpl {
    private final static long ONE_MIN = 1000 * 60;

    @Autowired
    private XApplicationsService xApplicationsService;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private AgentStatDao agentStatDao;

    @Autowired
    private XTransactionServiceImpl xTransactionService;

    @Autowired
    public XEventService eventService;

    public XServiceHealthEvents calcServiceHealthEvents(String appName, Range range) {
        List<String> appList = getAppList();
        Preconditions.checkArgument(appList.contains(appName), "application not found");

        List<XServiceHealthEvent> xServiceHealthEventList = newArrayList();
        for (XService xService : xApplicationsService.getXServices(appName)) {
            List<ResultEvent> instanceEvents = newArrayList();
            List<String> agentIds = applicationIndexDao.selectAgentIds(xService.getName());
            for (String agentId : agentIds) {
                instanceEvents.addAll(eventService.getInstanceEvents(appName, xService.getName(), agentId, range));
            }
            if (!instanceEvents.isEmpty()) {
                StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(instanceEvents, agentIds.size());
                XServiceHealthEvent event = new XServiceHealthEvent(xService.getName(), userCase.getScore(), instanceEvents);
                xServiceHealthEventList.add(event);
            }
        }

        Collections.sort(xServiceHealthEventList, (event1, event2) ->
                event1.getServiceScores() == event2.getServiceScores() ? 0 :
                (event1.getServiceScores() - event2.getServiceScores() > 0 ? 1 : -1));

        return new XServiceHealthEvents(appList, appName, xServiceHealthEventList);
    }

    private List<String> getAppList() {
        List<String> appList = newArrayList();
        appList.addAll(xApplicationsService.getXApplications().
                stream().map(XApplication::getName).collect(Collectors.toList()));
        return appList;
    }


    public XServiceCallsEvents getServiceCallsEvents(String appName, Range range) {
        List<String> appList = getAppList();
        Preconditions.checkArgument(appList.contains(appName), new IllegalArgumentException("appName not found"));

        List<XService> serviceList = xApplicationsService.getXServices(appName);
        List<XServiceCallsEvent> callsEvents = newArrayList();
        callsEvents.addAll(serviceList.stream().map(xService ->
                calcServiceCallsEvent(xService, range)).collect(Collectors.toList()));

        Collections.sort(callsEvents, (event1, event2) ->
                event1.getCallsPermin() == event2.getCallsPermin() ? 0 :
                        ((event1.getCallsPermin() - event2.getCallsPermin()) > 0 ? -1 : 1));

        return new XServiceCallsEvents(appName, appList, callsEvents);
    }

    private XServiceCallsEvent calcServiceCallsEvent(XService xService, Range range) {
        List<String> agentIds = applicationIndexDao.selectAgentIds(xService.getName());
        List<XInstanceEvent> instanceEvents = newArrayList();
        instanceEvents.addAll(agentIds.stream().map(agentId ->
                calcInstanceEvent(agentId, range)).collect(Collectors.toList()));

        int svcCalls = 0;
        for (XInstanceEvent xInstanceEvent : instanceEvents) {
            svcCalls += xInstanceEvent.getTotalcalls();
        }
        double callsPermin = svcCalls / (double) (range.getRange() / ONE_MIN);

        Collections.sort(instanceEvents, (event1, event2) ->
                (event1.getCallsPermin() == event2.getCallsPermin()) ? 0 :
                (((event1.getCallsPermin() - event2.getCallsPermin()) > 0) ? -1 : 1));

        return new XServiceCallsEvent(xService.getName(), callsPermin, instanceEvents);
    }

    private XInstanceEvent calcInstanceEvent(String agentId, Range range) {
        if (!agentStatDao.agentStatExists(agentId, range)) {
            return new XInstanceEvent(agentId, -1, -1, 0.00, 0.00, 0.00, 0L);
        }

        List<AgentStat> agentStats = agentStatDao.getAgentStatList(agentId, range);

        double cpuUsage = 0.00;
        double memUsage = 0.00;
        double heapUsage = 0.00;
        long gcTime = 0L;
        for (AgentStat agentStat : agentStats) {
            cpuUsage += agentStat.getSystemCpuUsage() + agentStat.getJvmCpuUsage();
            memUsage += agentStat.getMemUsage();
            heapUsage += agentStat.getHeapUsed() / (double) agentStat.getHeapMax();
            gcTime += agentStat.getGcNewTime() + agentStat.getGcOldTime();
        }

        cpuUsage /= agentStats.size();
        memUsage /= agentStats.size();
        heapUsage /= agentStats.size();
        gcTime /= (range.getRange() / ONE_MIN);
        XTransactions xTransactions = xTransactionService.getInstanceTransactions(agentId, range);
        double callsPermin = xTransactions.getCalls() / (double) (range.getRange() / ONE_MIN);
        return new XInstanceEvent(agentId, callsPermin, xTransactions.getCalls(), cpuUsage, memUsage, heapUsage, gcTime);
    }
}
