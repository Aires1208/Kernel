package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.topo.domain.*;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.InstanceIndexDao;
import com.navercorp.pinpoint.web.dao.ServiceIndexDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.topo.usercases.CalculateScatterDataUserCase;
import com.navercorp.pinpoint.web.topo.usercases.CalculateTopoServiceUserCase;
import com.navercorp.pinpoint.web.topo.usercases.Util.XNodeHelper;
import com.navercorp.pinpoint.web.view.XInstanceDashBoard;
import com.navercorp.pinpoint.web.view.XServiceDashBoard;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.topo.usercases.Util.XNodeHelper.getAgentInfoDigest;

@Service
public class XServiceDetailServiceImpl {

    @Autowired
    private XEventService eventService;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private AgentInfoService agentInfoService;

    @Autowired
    private ServiceIndexDao serviceIndexDao;

    @Autowired
    private InstanceIndexDao instanceIndexDao;

    @Autowired
    private XTracesListServiceImpl tracesListService;

    @Autowired
    private ServiceTypeRegistryService registryService;

    private XService getService(String serviceName) {

        List<Application> applications = applicationIndexDao.selectAllApplicationNames();
        for (Application application : applications) {
            if (serviceName.equals(application.getName())) {
                return new XService(application.getName(), application.getServiceType());
            }
        }
        return new XService(serviceName, ServiceType.UNKNOWN, new TransactionHealth(0, 0, 0));
    }

    public XServiceDashBoard getXServiceDashBoard(String appName, String serviceName, Range range) {
        //1. topology
        List<TopoLine> topoLines = serviceIndexDao.getTopoLineSet(appName, range);
        CalculateTopoServiceUserCase serviceUserCase = new CalculateTopoServiceUserCase(topoLines);
        XServiceTopo xServiceTopo = serviceUserCase.getBound1ServiceTopo(serviceName);

        for (XNode xNode : xServiceTopo.getXNodes()) {
            if (illegalAgentId(xNode.getName())) {
                continue;
            }
            List<AgentInfoDigest> agentInfoDigests = XNodeHelper.getAgentInfoDigests(xNode.getName(), agentInfoService, range);
            xNode.setInstances(agentInfoDigests);
            if (!agentInfoDigests.isEmpty()) {
                xNode.setServiceType(agentInfoDigests.iterator().next().getServiceType());
            }
        }

        XService xService = getService(serviceName);

        // Health Info
        List<ResultEvent> instancesEvents = newArrayList();
        List<String> agentIds = applicationIndexDao.selectAgentIds(serviceName);
        for (String agentId : agentIds) {
            instancesEvents.addAll(eventService.getInstanceEvents(appName, serviceName, agentId, range));
        }
        StatisticsEventsUserCase nodeHealthUserCase = new StatisticsEventsUserCase(instancesEvents, agentIds.size());
        xService.setNodeHealth(new NodeHealth(nodeHealthUserCase.getCritical(), nodeHealthUserCase.getWarning(), nodeHealthUserCase.getNormal()));

        List<XTransactionName> tracesList = tracesListService.getServiceTracesList(serviceName, range);
        List<ResultEvent> transactionsEvents = eventService.getServiceTransactionEvents(appName, serviceName, range);
        StatisticsEventsUserCase tracesHealthUserCase =
                new StatisticsEventsUserCase(transactionsEvents, tracesList.size());
        xService.setTransactionHealth(new TransactionHealth(
                tracesHealthUserCase.getCritical(),
                tracesHealthUserCase.getWarning(),
                tracesHealthUserCase.getNormal()));
        xService.setEventCount(eventService.getServiceEvents(appName, serviceName, range).size() + transactionsEvents.size());

        //2.metrics
        List<XDot> xDots = new CalculateScatterDataUserCase(topoLines, range).mergeTopoLinesGenerateXDots();

        return new XServiceDashBoard(xService,
                xServiceTopo, new XLoadInfo(xDots, range));
    }


    public XInstanceDashBoard getXInstanceDetail(String application, String service, String instanceName, Range range) {
        //1.Topo
        List<TopoLine> topoLines = instanceIndexDao.getTopoLineSet(application, range);
        CalculateTopoServiceUserCase userCase = new CalculateTopoServiceUserCase(topoLines);
        XServiceTopo xServiceTopo = userCase.getBound1ServiceTopo(instanceName);

        for (XNode node : xServiceTopo.getXNodes()) {
            if (illegalAgentId(node.getName())) {
                continue;
            }
            AgentInfoDigest agentInfoDigest = getAgentInfoDigest(node.getName(), agentInfoService, range);
            node.setInstances(agentInfoDigest != null ? newArrayList(agentInfoDigest) : null);
            if (null != agentInfoDigest) {
                node.setServiceType(agentInfoDigest.getServiceType());
            }
        }

        List<ResultEvent> instanceEvents = eventService.getInstanceEvents(application, service, instanceName, range);
        List<ResultEvent> transactionsEvents = eventService.getInstanceTransactionEvents(application, service, instanceName, range);

        //2.metrics
        AgentInfo agentInfo = agentInfoService.getAgentInfo(instanceName, range.getTo());
        ServiceType serviceType = registryService.findServiceType(agentInfo.getServiceTypeCode());
        XInstance xInstance = new XInstance(instanceName, serviceType);
        xInstance.setAgentInfo(agentInfo);
        xInstance.setEventCount(instanceEvents.size() + transactionsEvents.size());

        // transaction health
        List<XTransactionName> tracesList = tracesListService.getInstTracesList(service, instanceName, range);
        StatisticsEventsUserCase tracesHealthUserCase =
                new StatisticsEventsUserCase(transactionsEvents, tracesList.size());
        xInstance.setTransactionHealth(new TransactionHealth(
                tracesHealthUserCase.getCritical(),
                tracesHealthUserCase.getWarning(),
                tracesHealthUserCase.getNormal()));

        // load info
        List<XDot> xDots = new CalculateScatterDataUserCase(topoLines, range).mergeTopoLinesGenerateXDots();

        return new XInstanceDashBoard(xInstance,
                xServiceTopo, new XLoadInfo(xDots, range));
    }

    private static boolean illegalAgentId(String str) {
        return null == str || "".equals(str) || str.length() > HBaseTables.AGENT_NAME_MAX_LEN;
    }
}
