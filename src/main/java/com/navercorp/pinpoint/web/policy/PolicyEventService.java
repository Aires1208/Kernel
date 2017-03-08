package com.navercorp.pinpoint.web.policy;

import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.service.XApplicationsService;
import com.navercorp.pinpoint.web.service.XBusinessTransactions;
import com.navercorp.pinpoint.web.service.XHostsService;
import com.navercorp.pinpoint.web.service.XTransactionServiceImpl;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class PolicyEventService {

    @Autowired
    private XHostsService serverStatService;

    @Autowired
    private XApplicationsService xApplicationsService;

    @Autowired
    private XTransactionServiceImpl transactionService;

    @Autowired
    private AgentStatDao agentStatDao;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    private List<XApplication> xApplications = newArrayList();

    public List<String> buildMessage(long timestamp) {
        initXApplications();
        List<String> policyEvents = newArrayList();
        policyEvents.addAll(createAppPolicyMessage(timestamp));
        policyEvents.addAll(createServicePolicyMessage(timestamp));
        policyEvents.addAll(createInstancePolicyMessage(timestamp));
        policyEvents.addAll(createHostPolicyMessage(timestamp));
        return policyEvents;
    }

    private void initXApplications() {
        this.xApplications = xApplicationsService.getXApplications();
    }

    private List<String> createAppPolicyMessage(long timeStamp) {
        List<String> appPolicyMessages = newArrayList();
        for (XApplication xApplication : xApplications) {
            XTransactions xTransactions = transactionService.getXAppTransactions(xApplication.getName(), create1MinuteRange(timeStamp));
            if (null != xTransactions) {
                appPolicyMessages.add(createPolicyMessage("app", "app=" + xApplication.getName(), "calls", xTransactions.getCalls(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("app", "app=" + xApplication.getName(), "errors", xTransactions.getErrors(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("app", "app=" + xApplication.getName(), "responsetime", xTransactions.getResponseTime(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("app", "app=" + xApplication.getName(), "maxresponsetime", xTransactions.getMaxResponseTime(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("app", "app=" + xApplication.getName(), "minresponsetime", xTransactions.getMinResponseTime(), System.currentTimeMillis()));
                Map<XTransactionName, XBusinessTransactions> stringXBusinessTransactionsMap = xTransactions.getTransactionsMap();
                for (Map.Entry<XTransactionName, XBusinessTransactions> entry : stringXBusinessTransactionsMap.entrySet()) {
                    String key = entry.getKey().getTransactionName();
                    XBusinessTransactions xBusinessTransactions = entry.getValue();
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",name=" + key, "calls", xBusinessTransactions.getCalls(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",name=" + key, "errors", xBusinessTransactions.getErrors(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",name=" + key, "responsetime", xBusinessTransactions.getAverageResponseTime(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",name=" + key, "maxresponsetime", xBusinessTransactions.getMaxResponseTime(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",name=" + key, "minresponsetime", xBusinessTransactions.getMinResponseTime(), System.currentTimeMillis()));
                }
            }
        }
        return appPolicyMessages;
    }

    private List<String> createServicePolicyMessage(long timeStamp) {
        List<String> appPolicyMessages = newArrayList();
        for (XApplication xApplication : xApplications) {
            List<XService> xServices = xApplication.getXServices();
            for (XService xService : xServices) {
                XTransactions xTransactions = transactionService.getXServiceTransactions(xService.getName(), create1MinuteRange(timeStamp));
                if (xTransactions == null) {
                    continue;
                }
                appPolicyMessages.add(createPolicyMessage("service", " app=" + xApplication.getName() + ",service=" + xService.getName(), "calls", xTransactions.getCalls(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("service", " app=" + xApplication.getName() + ",service=" + xService.getName(), "errors", xTransactions.getErrors(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("service", " app=" + xApplication.getName() + ",service=" + xService.getName(), "responsetime", xTransactions.getResponseTime(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("service", " app=" + xApplication.getName() + ",service=" + xService.getName(), "maxresponsetime", xTransactions.getMaxResponseTime(), System.currentTimeMillis()));
                appPolicyMessages.add(createPolicyMessage("service", " app=" + xApplication.getName() + ",service=" + xService.getName(), "minresponsetime", xTransactions.getMinResponseTime(), System.currentTimeMillis()));
                Map<XTransactionName, XBusinessTransactions> stringXBusinessTransactionsMap = xTransactions.getTransactionsMap();
                for (Map.Entry<XTransactionName, XBusinessTransactions> entry : stringXBusinessTransactionsMap.entrySet()) {
                    String key = entry.getKey().getTransactionName();
                    XBusinessTransactions xBusinessTransactions = entry.getValue();
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",name=" + key, "calls", xBusinessTransactions.getCalls(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",name=" + key, "errors", xBusinessTransactions.getErrors(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",name=" + key, "responsetime", xBusinessTransactions.getAverageResponseTime(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",name=" + key, "maxresponsetime", xBusinessTransactions.getMaxResponseTime(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",name=" + key, "minresponsetime", xBusinessTransactions.getMinResponseTime(), System.currentTimeMillis()));
                }
            }
        }
        return appPolicyMessages;
    }


    private List<String> createInstancePolicyMessage(long timeStamp) {
        List<String> appPolicyMessages = newArrayList();
        for (XApplication xApplication : xApplications) {
            String appName = xApplication.getName();
            Range range = create1MinuteRange(timeStamp);
            List<XService> xServices = xApplication.getXServices();
            for (XService xService : xServices) {
                List<String> agentIds = applicationIndexDao.selectAgentIds(xService.getName());
                for (String agentId : agentIds) {
                    XTransactions xTransactions = transactionService.getInstanceTransactions(agentId, range);
                    if (null != xTransactions) {
                        appPolicyMessages.add(createPolicyMessage("instance", "app=" + appName + ",service=" + xService.getName() + ",instance=" + agentId, "calls", xTransactions.getCalls(), System.currentTimeMillis()));
                        appPolicyMessages.add(createPolicyMessage("instance", "app=" + appName + ",service=" + xService.getName() + ",instance=" + agentId, "errors", xTransactions.getErrors(), System.currentTimeMillis()));
                        appPolicyMessages.add(createPolicyMessage("instance", "app=" + appName + ",service=" + xService.getName() + ",instance=" + agentId, "responsetime", xTransactions.getResponseTime(), System.currentTimeMillis()));
                        appPolicyMessages.add(createPolicyMessage("instance", "app=" + appName + ",service=" + xService.getName() + ",instance=" + agentId, "maxresponsetime", xTransactions.getMaxResponseTime(), System.currentTimeMillis()));
                        appPolicyMessages.add(createPolicyMessage("instance", "app=" + appName + ",service=" + xService.getName() + ",instance=" + agentId, "minresponsetime", xTransactions.getMinResponseTime(), System.currentTimeMillis()));
                        Map<XTransactionName, XBusinessTransactions> stringXBusinessTransactionsMap = xTransactions.getTransactionsMap();
                        for (Map.Entry<XTransactionName, XBusinessTransactions> entry : stringXBusinessTransactionsMap.entrySet()) {
                            String key = entry.getKey().getTransactionName();
                            XBusinessTransactions xBusinessTransactions = entry.getValue();
                            appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",instance=" + agentId + ",name=" + key, "calls", xBusinessTransactions.getCalls(), System.currentTimeMillis()));
                            appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",instance=" + agentId + ",name=" + key, "errors", xBusinessTransactions.getErrors(), System.currentTimeMillis()));
                            appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",instance=" + agentId + ",name=" + key, "responsetime", xBusinessTransactions.getAverageResponseTime(), System.currentTimeMillis()));
                            appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",instance=" + agentId + ",name=" + key, "maxresponsetime", xBusinessTransactions.getMaxResponseTime(), System.currentTimeMillis()));
                            appPolicyMessages.add(createPolicyMessage("transaction", "app=" + xApplication.getName() + ",service=" + xService.getName() + ",instance=" + agentId + ",name=" + key, "minresponsetime", xBusinessTransactions.getMinResponseTime(), System.currentTimeMillis()));
                        }
                    }
                }
            }
        }
        return appPolicyMessages;
    }

    private List<String> createHostPolicyMessage(long timeStamp) {
        List<String> appPolicyMessages = newArrayList();
        Set<XHost> hosts = serverStatService.getXHosts();
        for (XHost host : hosts) {
            double cpuUsed = 0.0;
            double memUsed = 0.0;
            double inSpeed = 0.0;
            double outSpeed = 0.0;
            double diskUsage = 0.0;
            double heapUsed = 0.0;
            long gcTime = 0L;
            List<String> agentIds = newArrayList(host.getAgentIds());
            if (null != agentIds && !agentIds.isEmpty()) {
                String agentId = agentIds.get(0);
                if (agentStatDao.agentStatExists(agentId, create1MinuteRange(timeStamp))) {
                    List<AgentStat> agentStatList = agentStatDao.getAgentStatList(agentId, create1MinuteRange(timeStamp));
                    for (AgentStat agentStat : agentStatList) {
                        cpuUsed += agentStat.getSystemCpuUsage();
                        memUsed += agentStat.getMemUsed();
                        inSpeed += agentStat.getInSpeed();
                        outSpeed += agentStat.getOutSpeed();
                        diskUsage += agentStat.getDiskUsage();
                        heapUsed += agentStat.getHeapUsed() / agentStat.getHeapMax();
                        gcTime += agentStat.getGcOldTime();

                    }
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "cpuusage", cpuUsed / agentStatList.size(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "memusage", memUsed / agentStatList.size(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "diskusage", diskUsage / agentStatList.size(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "networkin", inSpeed / agentStatList.size(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "networkout", outSpeed / agentStatList.size(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "jvm", heapUsed / agentStatList.size(), System.currentTimeMillis()));
                    appPolicyMessages.add(createPolicyMessage("host", "hostid=" + host.getHostId(), "gcTime", gcTime, System.currentTimeMillis()));
                }
            }
        }
        return appPolicyMessages;
    }

    private static String createPolicyMessage(String objecttype, String objectname, String metricname, Object metricvalue, long timestamp) {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"objecttype\":").append("\"").append(objecttype).append("\"").append(",");
        stringBuilder.append("\"objectname\":").append("\"").append(objectname).append("\"").append(",");
        stringBuilder.append("\"metricname\":").append("\"").append(metricname).append("\"").append(",");
        stringBuilder.append("\"metricvalue\":").append(metricvalue).append(",");
        stringBuilder.append("\"timestamp\":").append(timestamp).append("}");
        return stringBuilder.toString();
    }

    private Range create1MinuteRange(long timestamp) {
        return new Range(timestamp - 30 * 1000, timestamp + 30 * 1000);
    }
}
