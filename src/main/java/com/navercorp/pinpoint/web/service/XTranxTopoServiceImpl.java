package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.topo.domain.AgentInfoDigest;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.common.usercase.CalculateInstanceTopoLineUserCase;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.topo.usercases.CalculateTopoServiceUserCase;
import com.navercorp.pinpoint.web.topo.usercases.Util.XNodeHelper;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by root on 16-9-20.
 */
@Service
public class XTranxTopoServiceImpl {
    @Autowired
    private AgentInfoService agentInfoService;

    @Autowired
    private ServiceTypeRegistryService registryService;

    @Autowired
    private XApplicationsService applicationsService;

    @Autowired
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Autowired
    private TraceDao traceDao;

    public XServiceTopo getAppTranxTopo(String appName, String command, Range range) {
        Preconditions.checkArgument(null != appName, new NullPointerException("appName must not be null."));
        Preconditions.checkArgument(null != command, new NullPointerException("command must not be null."));

        List<TransactionId> transactionIds = newArrayList();
        for (XService xService : applicationsService.getXServices(appName)) {
            transactionIds.addAll(getTransactionIds(xService.getName(), range));
        }

        return getxServiceTopo(command, transactionIds, range);
    }

    public XServiceTopo getServiceTranxTopo(String appName, String serviceName, String command, Range range) {
        Preconditions.checkArgument(null != serviceName, new NullPointerException("service Name must not be empty."));
        Preconditions.checkArgument(null != command, new NullPointerException("command must not be null."));
        Set<TransactionId> transactionIds = getTransactionIds(serviceName, range);
        return getxServiceTopo(command, newArrayList(transactionIds), range);
    }

    public XServiceTopo getInstanceTranxTopo(String appName, String serviceName, String instanceName, String command, Range range) {
        Preconditions.checkArgument(null != serviceName && null != instanceName, new NullPointerException("service and instance Name must not be empty."));
        Preconditions.checkArgument(null != command, new NullPointerException("command must not be null."));

        Set<TransactionId> transactionIds = getTransactionIds(serviceName, range);
        if (transactionIds.isEmpty()) {
            return new XServiceTopo(newArrayList(), newArrayList());
        }

        List<TopoLine> topoLines = newArrayList();
        for (TransactionId transactionId : transactionIds) {
            List<SpanBo> spanBoList = traceDao.selectSpan(transactionId);
            if (command.equals(getRpc(spanBoList)) || isTraced(instanceName, spanBoList)) {
                CalculateInstanceTopoLineUserCase userCase = new CalculateInstanceTopoLineUserCase(traceDao.selectSpan(transactionId), registryService);
                topoLines.add(userCase.execute());
            }
        }

        XServiceTopo serviceTopo = createServiceTopo(range, topoLines);

        return serviceTopo;
    }

    private XServiceTopo createServiceTopo(Range range, List<TopoLine> topoLines) {
        XServiceTopo serviceTopo = new XServiceTopo(newArrayList(), newArrayList());
        if (topoLines.size() > 0) {
            CalculateTopoServiceUserCase userCase = new CalculateTopoServiceUserCase(topoLines);
            serviceTopo = userCase.getServiceTopo();

            for (XNode xNode : serviceTopo.getXNodes()) {
                List<AgentInfoDigest> agentInfoDigests = XNodeHelper.getAgentInfoDigests(xNode.getName(), agentInfoService, range);
                xNode.setInstances(agentInfoDigests);
                xNode.setNodeHealth(XNodeHelper.getNodeHealth(agentInfoDigests));
            }
        }
        return serviceTopo;
    }

    private Set<TransactionId> getTransactionIds(String serviceName, Range range) {
        LimitedScanResult<List<TransactionId>> limitedScanResult =  applicationTraceIndexDao.scanTraceIndex(serviceName,range,5000,false);
        List<TransactionId> transactionIds = limitedScanResult.getScanData();

        return newHashSet(transactionIds);
    }

    private String getRpc(List<SpanBo> spanBoList) {
        if (null == spanBoList || 0 == spanBoList.size())
            return null;

        for (SpanBo spanBo : spanBoList) {
            if (spanBo.isRoot() || null != spanBo.getRpc())
                return spanBo.getRpc();
        }
        return "unknow";
    }

    private XServiceTopo getxServiceTopo(String command, List<TransactionId> transactionIds, Range range) {
        if (transactionIds.isEmpty()) {
            return new XServiceTopo(newArrayList(), newArrayList());
        }

        List<TopoLine> topoLines = newArrayList();
        for (TransactionId transactionId : transactionIds) {
            List<SpanBo> spanBoList = traceDao.selectSpan(transactionId);
            if (command.equals(getRpc(spanBoList))) {
                CalculateInstanceTopoLineUserCase userCase = new CalculateInstanceTopoLineUserCase(traceDao.selectSpan(transactionId), registryService);
                topoLines.add(userCase.execute());
            }
        }

        XServiceTopo serviceTopo = createServiceTopo(range, topoLines);

        return serviceTopo;
    }

    private boolean isTraced(String instanceName, List<SpanBo> spanBos) {
        for (SpanBo spanBo : spanBos) {
            if (spanBo.getAgentId().equals(instanceName)) {
                return true;
            }
        }

        return false;
    }
}

