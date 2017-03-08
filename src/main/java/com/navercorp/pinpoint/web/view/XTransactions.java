package com.navercorp.pinpoint.web.view;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.service.XBusinessTransactions;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@JsonSerialize(using = XTransactionsSerializer.class)
public class XTransactions {
    private List<XApplication> appList;
    private Map<XTransactionName, XBusinessTransactions> transactionsMap = newHashMap();
    private final Range range;

    public XTransactions(Map<XTransactionName, XBusinessTransactions> transactionsMap, Range range, List<XApplication> appList) {
        this.transactionsMap = transactionsMap;
        this.range = range;
        this.appList = appList;
    }


    public Map<XTransactionName, XBusinessTransactions> getTransactionsMap() {
        return transactionsMap;
    }

    public Range getRange() {
        return range;
    }

    public List<XApplication> getAppList() {
        return appList;
    }

    public Collection<XBusinessTransactions> getXBusinessTransactions(){
        return transactionsMap.values();
    }

    public Integer getCalls() {
        Integer calls = 0;
        for (XBusinessTransactions xBusinessTransactions : getXBusinessTransactions()) {
            calls += xBusinessTransactions.getCalls();
        }
        return calls;
    }

    public List<XType> getxTypes() {
        Collection<XBusinessTransactions> xBusinessTransactionses = transactionsMap.values();

        Set<XType> xTypes = newHashSet();
        for (XBusinessTransactions transactions : xBusinessTransactionses) {
            XType xType = new XType(transactions.getTier(),transactions.getTier());

            xTypes.add(xType);

        }
        return newArrayList(xTypes);
    }

    public Integer getErrors() {
        Integer error = 0;
        for (XBusinessTransactions xBusinessTransactions : getXBusinessTransactions()) {
            error += xBusinessTransactions.getErrors();
        }
        return error;
    }

    public double getErrorsPerMin() {
        double errs = getErrors();
        double mins = range.getRange()/(1000*60);
        return mins > 0 ? errs/mins : 0.00;
    }

    public long getResponseTime() {
        long elapsed = 0L;
        long count = 0L;
        for (XBusinessTransactions xBusinessTransactions : getXBusinessTransactions()) {
            elapsed += xBusinessTransactions.getElapsed();
            count += xBusinessTransactions.getTransactionsCount();
        }
        return count > 0 ? elapsed/count : 0L;
    }

    public Long getMaxResponseTime() {
        Long maxRT = 0L;
        for (XBusinessTransactions xBusinessTransactions : getXBusinessTransactions()) {
            maxRT = xBusinessTransactions.getMaxResponseTime() > maxRT ? xBusinessTransactions.getMaxResponseTime() : maxRT;
        }
        return maxRT;
    }

    public Long getMinResponseTime() {
        Long minRT = Long.MAX_VALUE-1;
        if (null != getXBusinessTransactions() && getXBusinessTransactions().size() > 0) {

            for (XBusinessTransactions xBusinessTransactions : getXBusinessTransactions()) {
                minRT = xBusinessTransactions.getMinResponseTime() < minRT ? xBusinessTransactions.getMinResponseTime() : minRT;
            }
            return minRT;

        } else {
            return  0L;
        }
    }
}

