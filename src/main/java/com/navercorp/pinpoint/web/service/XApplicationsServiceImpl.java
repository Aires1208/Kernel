package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.topo.domain.AgentInfoDigest;
import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.ServiceIndexDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.topo.usercases.CalculateTopoServiceUserCase;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XApplicationsDashBoard;
import com.navercorp.pinpoint.web.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.navercorp.pinpoint.web.topo.usercases.Util.XNodeHelper.getAgentInfoDigests;

@Service
public class XApplicationsServiceImpl implements XApplicationsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XEventService eventService;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private XTracesListServiceImpl tracesListService;

    @Autowired
    private AgentInfoService agentInfoService;

    @Autowired
    private ServiceIndexDao serviceIndexDao;

    @Override
    public XApplicationsDashBoard getXApplicationsDashBoard(Range range) {

        List<XApplication> xApplications = getXApplications();
        for (XApplication xApplication : xApplications) {
            buildXApplication(xApplication, range);
        }

        return new XApplicationsDashBoard(xApplications);
    }

    private void buildXApplication(XApplication xApplication, Range range) {

        //1. calls infos
        List<TopoLine> topoLines = serviceIndexDao.getTopoLineSet(xApplication.getName(), range);
        logger.info("find topology : {}", topoLines.toString());
        CalculateTopoServiceUserCase topoServiceUserCase = new CalculateTopoServiceUserCase(topoLines);

        // 1.1 topology info
        XServiceTopo xServiceTopo = topoServiceUserCase.getServiceTopo();
        logger.info("build topology,appName:{},  nodes: {}, links: {}", xApplication.getName(), xServiceTopo.getXNodes(), xServiceTopo.getXLinks());
        xApplication.setRange(range);
        xApplication.setXNodes(xServiceTopo.getXNodes());

        // 1.2 instance info
        Set<String> svcsTraced = newHashSet();
        for (XNode xNode : xServiceTopo.getXNodes()) {
            if (isEmptyString(xNode.getName())) {
                continue;
            }
            List<AgentInfoDigest> agentInfoDigests = getAgentInfoDigests(xNode.getName(), agentInfoService, range);
            xNode.setInstances(agentInfoDigests);
            if (!agentInfoDigests.isEmpty()) {
                svcsTraced.add(xNode.getName());
            }
        }

        // 1.3 node health
        List<ResultEvent> servicesEvents = newArrayList();
        for (String service : svcsTraced) {
            servicesEvents.addAll(eventService.getServiceEvents(xApplication.getName(), service, range));
        }
        StatisticsEventsUserCase serviceEventUserCase = new StatisticsEventsUserCase(servicesEvents, svcsTraced.size());
        xApplication.setServiceHealth(new NodeHealth(serviceEventUserCase.getCritical(),
                serviceEventUserCase.getWarning(), serviceEventUserCase.getNormal()));

        //2. transactions infos
        List<ResultEvent> traceEventList = eventService.getAppTransactionEvents(xApplication.getName(), range);
        xApplication.setEventCount(eventService.getAppEvents(xApplication.getName(), range).size() + traceEventList.size());
        List<XTransactionName> tracesList = tracesListService.getAppTracesList(xApplication.getName(), range);
        StatisticsEventsUserCase eventsUserCase =
                new StatisticsEventsUserCase(traceEventList, tracesList.size());
        xApplication.setTransactionHealth(new TransactionHealth(
                eventsUserCase.getCritical(),
                eventsUserCase.getWarning(),
                eventsUserCase.getNormal()));

        //3. loadInfo
        //ToDo
    }


    @Override
    public List<XApplication> getXApplications() {
        List<Application> applications = applicationIndexDao.selectAllApplicationNames();
        Map<String, List<XService>> applicationsMap = newHashMap();
        for (Application application : applications) {
            String serviceName = application.getName();

            int index = serviceName.indexOf("_");
            if (index < 0) {
                logger.error("Wrong Application Name: {}." + serviceName);
                continue;
            }
            String applicationName = serviceName.substring(0, index);

            XService xService = new XService(serviceName, application.getServiceType());
            List<XService> applicationList = new ArrayList<>();

            if (applicationsMap.containsKey(applicationName)) {
                applicationList = applicationsMap.get(applicationName);
            }

            applicationList.add(xService);

            applicationsMap.put(applicationName, applicationList);
        }


        List<XApplication> xApplications = newArrayList();
        Set<String> applicationNames = applicationsMap.keySet();
        xApplications.addAll(applicationNames.stream().map(name -> new XApplication(name, applicationsMap.get(name))).collect(Collectors.toList()));

        return xApplications.size() == 0 ? getDefaultXApplications(applications) : xApplications;
    }

    private List<XApplication> getDefaultXApplications(List<Application> applications) {
        List<XApplication> xApplications = newArrayList();
        List<XService> xServices = newArrayList();
        xServices.addAll(applications.stream().map(application -> new XService(application.getName(), application.getServiceType())).collect(Collectors.toList()));
        xApplications.add(new XApplication("FAKE_EMS", xServices));

        return xApplications;
    }

    @Override
    public List<XService> getXServices(String applicationName) {
        if (applicationName == null) {
            throw new NullPointerException("productName must not be null!");
        }

        List<XApplication> xApplications = getXApplications();
        for (XApplication xApplication : xApplications) {
            if (xApplication.getName().equals(applicationName)) {
                return xApplication.getXServices();
            }
        }

        return newArrayList();
    }

    @Override
    public List<XApplication> getFullAppList() {
        List<XApplication> xApplications = getXApplications();
        for (XApplication app : xApplications) {
            for (XService service : app.getXServices()) {
                service.setAgentIds(applicationIndexDao.selectAgentIds(service.getName()));
            }
        }
        return xApplications;
    }

    private static boolean isEmptyString(String str) {
        return null == str || str.equals("");
    }
}
