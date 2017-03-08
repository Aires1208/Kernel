package com.navercorp.pinpoint.web.report.usercase;

import com.navercorp.pinpoint.web.service.XBusinessTransactions;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionEvent;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by root on 16-9-8.
 */
public class CalculateTransactionsHealthUserCase {
    private List<XBusinessTransactions> xBusinessTransactions;
    private Range range;
    private List<XTransactionEvent> events = newArrayList();

    public CalculateTransactionsHealthUserCase(List<XBusinessTransactions> transactionses, Range range) {
        this.xBusinessTransactions = transactionses;
        this.range = range;

        this.events = calcTransactionHealthEvents();
    }

    public List<XTransactionEvent> calcTransactionHealthEvents() {
        return newArrayList();
    }
}
