package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.web.dao.InstanceTraceIdIndexDao;
import com.navercorp.pinpoint.web.dao.ServiceTraceIdIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.util.SplitListUtils;
import com.navercorp.pinpoint.web.view.XTrace;
import com.navercorp.pinpoint.web.view.XTraceTable;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import com.navercorp.pinpoint.web.view.XType;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.XService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author sinwaj
 */
@Service
public class XTraceTableServiceImpl implements XTraceTableService {
    private static final int DEFAULT_TRACE_SPLIT_COUNT = 100;

    @Autowired
    private TraceDao traceDao;

    @Autowired
    private ServiceTraceIdIndexDao serviceTraceIdIndexDao;

    @Autowired
    private InstanceTraceIdIndexDao instanceTraceIdIndexDao;

    @Autowired
    private XApplicationsServiceImpl xApplicationsService;

    @Override
    public XTraceTable getApplicationTraceTable(XTraceQuery query) {
        Set<TransactionId> transactionIds = getAppTransactionIdsbyCmd(query);

        List<XTrace> xTraces = getTransactions(transactionIds, query.getMax(), query.getMin());

        List<XType> xTypes = getTransactionTypes(xTraces);

        return new XTraceTable(xTraces, xTypes);
    }

    @Override
    public XTraceTable getServiceTraceTable(XTraceQuery query) {
        Set<TransactionId> transactionIds = query.getCommand() != null ?
                serviceTraceIdIndexDao.findServiceTraceIdsByTraceName(query.getService(), query.getCommand(), query.getRange()) :
                serviceTraceIdIndexDao.findServiceTranceIds(query.getService(), query.getRange());

        List<XTrace> xTraces = getTransactions(transactionIds, query.getMax(), query.getMin());

        List<XType> xTypes = getTransactionTypes(xTraces);

        return new XTraceTable(xTraces, xTypes);
    }

    @Override
    public XTraceTable getInstanceTraceTable(XTraceQuery query) {
        Set<TransactionId> transactionIds = query.getCommand() != null ?
                instanceTraceIdIndexDao.findTransactionIds(query.getInstance(), query.getCommand(), query.getRange()) :
                instanceTraceIdIndexDao.findInstanceTransactionIds(query.getInstance(), query.getRange());

        List<XTrace> xTraces = getTransactions(transactionIds, query.getMax(), query.getMin());

        List<XType> xTypes = getTransactionTypes(xTraces);

        return new XTraceTable(xTraces, xTypes);
    }

    private Set<TransactionId> getAppTransactionIdsbyCmd(XTraceQuery query) {
        List<XService> serviceList = xApplicationsService.getXServices(query.getApplication());
        Set<TransactionId> transactionIds = newHashSet();
        for (XService service : serviceList) {
            transactionIds.addAll(query.getCommand() != null ?
                    serviceTraceIdIndexDao.findServiceTraceIdsByTraceName(service.getName(), query.getCommand(), query.getRange()):
                    serviceTraceIdIndexDao.findServiceTranceIds(service.getName(), query.getRange()));
        }
        return transactionIds;
    }

    private List<XTrace> getTransactions(Set<TransactionId> transactionIds, long max, long min) {

        List<XTrace> xTraces = newArrayList();
        List<List<TransactionId>> splitTransactionIdList =
                SplitListUtils.splitTransactionIdList(newArrayList(transactionIds), DEFAULT_TRACE_SPLIT_COUNT);

        for (List<TransactionId> splitTransactionIds : splitTransactionIdList) {
            List<List<SpanBo>> spanBos = traceDao.selectAllSpans(splitTransactionIds);

            xTraces.addAll(spanBos.stream().filter(trace -> accept(trace, max, min)).map(XTrace::new).collect(Collectors.toList()));
        }

        return xTraces;
    }

    private boolean accept(List<SpanBo> spanBoList, long max, long min) {
        for (SpanBo spanBo : spanBoList) {
            if (spanBo.isRoot()) {
                return max < 0 || min < 0 || spanBo.getElapsed() <= max && spanBo.getElapsed() >= min;
            }
        }
        return false;
    }

    private List<XType> getTransactionTypes(List<XTrace> xTraces) {
        Set<XType> xTypeSet = newHashSet();
        for (XTrace xTrace : xTraces) {
            String type = xTrace.getType();
            xTypeSet.add(new XType(type, type));
        }

        List<XType> xTypes = newArrayList();
        xTypes.add(new XType("", "All"));
        xTypes.addAll(xTypeSet);

        return xTypes;
    }

}
