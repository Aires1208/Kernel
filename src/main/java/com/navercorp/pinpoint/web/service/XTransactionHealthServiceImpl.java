package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.report.usercase.CalculateTransactionsHealthUserCase;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import com.navercorp.pinpoint.web.view.XTransactionEvents;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.XTransactionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-8-29.
 */
@Service
public class XTransactionHealthServiceImpl {

    @Autowired
    private XApplicationsService xApplicationsService;

    @Autowired
    private XTransactionServiceImpl xTransactionService;

    public XTransactionEvents calcTransactionHealthEvents(XTraceQuery query) {
        List<XTransactionEvent> transactionEvents = getTransactionEventList(query);
        List<String> appList = getAppStrList();
        return new XTransactionEvents(query.getApplication(), appList, transactionEvents);
    }

    private List<XTransactionEvent> getTransactionEventList(XTraceQuery query) {
        XTransactions xAppTransactions = xTransactionService.getXAppTransactions(query.getApplication(), query.getRange());
        CalculateTransactionsHealthUserCase userCase =
                new CalculateTransactionsHealthUserCase(newArrayList(xAppTransactions.getXBusinessTransactions()), query.getRange());
        return userCase.calcTransactionHealthEvents();
    }


    private List<String> getAppStrList() {
        List<String> appList = newArrayList();
        List<XApplication> applications = xApplicationsService.getXApplications();
        appList.addAll(applications.stream().map(XApplication::getName).collect(Collectors.toList()));
        return appList;
    }
}
