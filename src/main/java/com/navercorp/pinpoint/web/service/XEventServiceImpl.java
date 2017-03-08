package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.dao.ActiveEventDao;
import com.navercorp.pinpoint.web.dao.HistoryEventDao;
import com.navercorp.pinpoint.web.view.XEventsDashBoard;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-9-20.
 */
@Service
public class XEventServiceImpl implements XEventService {

    @Autowired
    private ActiveEventDao activeEventDao;

    @Autowired
    private HistoryEventDao historyEventDao;

    @Autowired
    private XApplicationsService xApplicationsService;

    @Autowired
    private XTracesListService tracesListService;

    @Override
    public List<ResultEvent> getAppEvents(String appName, Range range) {
        Preconditions.checkArgument(null != appName, new NullPointerException("appName must not be empty."));

        String objDN = "app=" + appName;
        List<ResultEvent> activeEvents = activeEventDao.queryEvents(objDN);
        List<List<ResultEvent>> historyEvents = historyEventDao.findEvents(objDN, range);

        return mergeResult(activeEvents, historyEvents);
    }

    @Override
    public List<ResultEvent> getServiceEvents(String appName, String serviceName, Range range) {
        Preconditions.checkArgument(null != appName, new NullPointerException("appName must not be empty."));
        Preconditions.checkArgument(null != serviceName, new NullPointerException("serviceName must not be empty."));

        String objDN = "app=" + appName + ",service=" + serviceName;

        List<ResultEvent> activeEvents = activeEventDao.queryEvents(objDN);
        List<List<ResultEvent>> historyEvents = historyEventDao.findEvents(objDN, range);

        return mergeResult(activeEvents, historyEvents);
    }

    @Override
    public List<ResultEvent> getInstanceEvents(String appName, String serviceName, String instanceName, Range range) {
        Preconditions.checkArgument(null != appName, new NullPointerException("appName must not be empty."));
        Preconditions.checkArgument(null != serviceName, new NullPointerException("serviceName must not be empty."));
        Preconditions.checkArgument(null != instanceName, new NullPointerException("instanceName must not be empty."));

        String objDN = "app=" + appName + ",service=" + serviceName + ",instance=" + instanceName;
        List<ResultEvent> activeEvents = activeEventDao.queryEvents(objDN);
        List<List<ResultEvent>> historyEvents = historyEventDao.findEvents(objDN, range);

        return mergeResult(activeEvents, historyEvents);
    }

    @Override
    public List<ResultEvent> getAppTransactionEvents(String appName, Range range) {
        Preconditions.checkArgument(null != appName, new NullPointerException("appName must not be empty."));

        List<XTransactionName> names = tracesListService.getAppTracesList(appName, range);
        List<ResultEvent> activeEvents = newArrayList();
        List<List<ResultEvent>> historyEvents = newArrayList();

        for (XTransactionName trace : names) {
            String objDN = "app=" + appName + ",name=" + trace.getTransactionName();
            activeEvents.addAll(activeEventDao.queryEvents(objDN));
            historyEvents.addAll(historyEventDao.findEvents(objDN, range));
        }

        return mergeResult(activeEvents, historyEvents);
    }

    @Override
    public List<ResultEvent> getHostEvents(String hostId, Range range) {
        Preconditions.checkArgument(null != hostId, "hostId must not be empty.");

        String objDN = "hostid=" + hostId;

        List<ResultEvent> activeEvents = activeEventDao.queryEvents(objDN);
        List<List<ResultEvent>> historyEvents = historyEventDao.findEvents(objDN, range);

        return mergeResult(activeEvents, historyEvents);
    }

    @Override
    public List<ResultEvent> getServiceTransactionEvents(String appName, String serviceName, Range range) {
        Preconditions.checkArgument(null != appName, "appName must not be empty.");
        Preconditions.checkArgument(null != serviceName, "serviceName must not be empty.");

        List<XTransactionName> names = tracesListService.getServiceTracesList(serviceName, range);
        List<ResultEvent> activeEvents = newArrayList();
        List<List<ResultEvent>> historyEvents = newArrayList();

        for (XTransactionName trace : names) {
            String objDN = "app=" + appName + ",service=" + serviceName + ",name=" + trace.getTransactionName();
            activeEvents.addAll(activeEventDao.queryEvents(objDN));
            historyEvents.addAll(historyEventDao.findEvents(objDN, range));
        }

        return mergeResult(activeEvents, historyEvents);
    }

    @Override
    public List<ResultEvent> getInstanceTransactionEvents(String appName, String serviceName, String instanceName, Range range) {
        Preconditions.checkArgument(null != appName, "appName must not be empty.");
        Preconditions.checkArgument(null != serviceName, "serviceName must not be empty.");
        Preconditions.checkArgument(null != instanceName, "instanceName must not be empty.");

        List<XTransactionName> names = tracesListService.getInstTracesList(serviceName, instanceName, range);
        List<ResultEvent> activeEvents = newArrayList();
        List<List<ResultEvent>> historyEvents = newArrayList();

        for (XTransactionName trace : names) {
            String objDN = "app=" + appName + ",service=" + serviceName + ",instance=" + instanceName + ",name=" + trace.getTransactionName();
            activeEvents.addAll(activeEventDao.queryEvents(objDN));
            historyEvents.addAll(historyEventDao.findEvents(objDN, range));
        }

        return mergeResult(activeEvents, historyEvents);
    }

    public XEventsDashBoard getAppEventsDashBoard(String appName, Range range) {
        List<ResultEvent> events = getAppEvents(appName, range);
        List<ResultEvent> tracesEvents = getAppTransactionEvents(appName, range);
        events.addAll(tracesEvents);
        return new XEventsDashBoard(xApplicationsService.getFullAppList(), events);
    }

    public XEventsDashBoard getServiceEventsDashBoard(String appName, String serviceName, Range range) {
        List<ResultEvent> events = getServiceEvents(appName, serviceName, range);
        events.addAll(getServiceTransactionEvents(appName, serviceName, range));
        return new XEventsDashBoard(xApplicationsService.getFullAppList(), events);
    }


    public XEventsDashBoard getInstanceEventsDashBoard(String appName, String serviceName, String instanceName, Range range) {
        List<ResultEvent> events = getInstanceEvents(appName, serviceName, instanceName, range);
        events.addAll(getInstanceTransactionEvents(appName, serviceName, instanceName, range));
        return new XEventsDashBoard(xApplicationsService.getFullAppList(), events);
    }


    private List<ResultEvent> mergeResult(List<ResultEvent> activeEvents, List<List<ResultEvent>> historyEvents) {
        List<ResultEvent> resultEvents = newArrayList();

        if (null != activeEvents && !activeEvents.isEmpty()) {
            resultEvents.addAll(activeEvents);
        }

        if (null != historyEvents && !historyEvents.isEmpty()) {
            historyEvents.forEach(resultEvents::addAll);
        }

        return resultEvents;
    }

}
