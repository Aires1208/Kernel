package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.topo.domain.*;
import com.navercorp.pinpoint.web.dao.ServiceIndexDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.topo.usercases.CalculateScatterDataUserCase;
import com.navercorp.pinpoint.web.topo.usercases.CalculateTopoServiceUserCase;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XApplicationDashBoard;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.navercorp.pinpoint.web.topo.usercases.Util.XNodeHelper.getAgentInfoDigests;

@Service
public class XServiceDashBoardServiceImpl {
    @Autowired
    private XEventService eventService;

    @Autowired
    private AgentInfoService agentInfoService;

    @Autowired
    private XApplicationsService XApplicationsService;

    @Autowired
    private XTracesListServiceImpl tracesListService;

    @Autowired
    private ServiceIndexDao serviceIndexDao;

    public XApplicationDashBoard getXApplicationDashBoard(String appName, Range range) {
        //1. services
        List<XService> xServices = XApplicationsService.getXServices(appName);

        //2. topology info
        List<TopoLine> topoLines = serviceIndexDao.getTopoLineSet(appName, range);
        CalculateTopoServiceUserCase userCase = new CalculateTopoServiceUserCase(topoLines);
        XServiceTopo xServiceTopo = userCase.getServiceTopo();

        Set<String> svcsTraced = newHashSet();
        for (XNode xNode : xServiceTopo.getXNodes()) {
            if (isEmptyString(xNode.getName())) {
                continue;
            }
            List<AgentInfoDigest> agentInfoDigests = getAgentInfoDigests(xNode.getName(), agentInfoService, range);
            xNode.setInstances(agentInfoDigests);
            if (!agentInfoDigests.isEmpty()) {
                svcsTraced.add(xNode.getName());
                xNode.setServiceType(agentInfoDigests.get(0).getServiceType());
            }
        }

        XApplication xApplication = new XApplication(appName, xServices);

        XApplicationDashBoard xApplicationDashBoard = new XApplicationDashBoard(xApplication, xServiceTopo, range);


        //3. health info
        List<ResultEvent> transactionEvents = eventService.getAppTransactionEvents(xApplication.getName(), range);
        xApplication.setEventCount(eventService.getAppEvents(appName, range).size() + transactionEvents.size());

        List<XTransactionName> tracesList = tracesListService.getAppTracesList(appName, range);
        StatisticsEventsUserCase traceHealthUserCase =
                new StatisticsEventsUserCase(transactionEvents, tracesList.size());
        xApplicationDashBoard.setTransactionHealth(new TransactionHealth(
                traceHealthUserCase.getCritical(),
                traceHealthUserCase.getWarning(),
                traceHealthUserCase.getNormal()));

        List<ResultEvent> servicesEvents = newArrayList();
        for (String service : svcsTraced) {
            servicesEvents.addAll(eventService.getServiceEvents(appName, service, range));
        }
        StatisticsEventsUserCase serviceEventUserCase = new StatisticsEventsUserCase(servicesEvents, svcsTraced.size());
        xApplication.setServiceHealth(new NodeHealth(serviceEventUserCase.getCritical(),
                serviceEventUserCase.getWarning(), serviceEventUserCase.getNormal()));

        //4. load info
        List<XDot> xDots = new CalculateScatterDataUserCase(topoLines, range).mergeTopoLinesGenerateXDots();

        xApplicationDashBoard.setLoadInfo(new XLoadInfo(xDots, range));

        return xApplicationDashBoard;
    }

    private static boolean isEmptyString(String str) {
        return str == null || str.matches("");
    }
}
