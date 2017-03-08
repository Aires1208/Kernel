package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.dao.TransactionListDao;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XService;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-22.
 */
@Service
public class XTracesListServiceImpl implements XTracesListService {
    @Autowired
    private TransactionListDao transactionListDao;

    @Autowired
    private XApplicationsService applicationsService;

    @Override
    public List<XTransactionName> getAppTracesList(String appName, Range range) {
        List<XService> serviceList = applicationsService.getXServices(appName);

        List<XTransactionName> tracesList = newArrayList();
        for (XService service : serviceList) {
            tracesList.addAll(transactionListDao.getServiceTracesList(service.getName(), range));
        }

        return tracesList;
    }

    @Override
    public List<XTransactionName> getServiceTracesList(String serviceName, Range range) {
        return transactionListDao.getServiceTracesList(serviceName, range);
    }

    @Override
    public List<XTransactionName> getInstTracesList(String serviceName, String instanceName, Range range) {
        List<XTransactionName> servTracesList = getServiceTracesList(serviceName, range);

        List<XTransactionName> instTracesList = newArrayList();
        instTracesList.addAll(servTracesList.stream().filter(trace -> instanceName.equals(trace.getAgentId())).collect(Collectors.toList()));
        return instTracesList;
    }
}
