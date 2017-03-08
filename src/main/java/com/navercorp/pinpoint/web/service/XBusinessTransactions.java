package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.filter.CmdRanger;
import com.navercorp.pinpoint.web.report.usercase.HealthLevel;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XBusinessTransaction;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XBusinessTransactions {

    private final static long ONE_MIN = 1000 * 60;

    private List<XBusinessTransaction> xBusinessTransactions = newArrayList();
    private String health;
    private XTransactionName traceName;

    public XBusinessTransactions(XTransactionName traceName) {
        this.traceName = traceName;
    }

    public String getTraceName() {
        return null != traceName.getTransactionName() ? traceName.getTransactionName() : "";
    }

    public String getAgentId() {
        return traceName.getAgentId();
    }

    public String getServiceName() {
        return traceName.getServiceName();
    }

    public String getHealth() {
        return null != health ? health : HealthLevel.NORMAL.getDesc();
    }

    public int getTransactionsCount() {
        return xBusinessTransactions.size();
    }

    public Integer getErrors() {
        Integer errors = 0;
        for (XBusinessTransaction xBusinessTransaction : xBusinessTransactions) {
            errors += xBusinessTransaction.getErrors();
        }
        return errors;
    }

    public Long getElapsed() {
        Long elapsed = 0L;
        for (XBusinessTransaction xBusinessTransaction : xBusinessTransactions) {
            elapsed += xBusinessTransaction.getElapsed();
        }
        return elapsed;
    }


    public String getTier() {
        int cmdCode = StringUtils.isNumeric(traceName.getTransactionName()) ? Integer.parseInt(traceName.getTransactionName()) : -1;
        return new CmdRanger().getType(cmdCode);
    }

    public Long getAverageResponseTime() {
        return xBusinessTransactions.size() == 0 ? 0L : getElapsed() / (long) xBusinessTransactions.size();
    }

    public Long getMaxResponseTime() {
        Long maxRT = 0L;
        for (XBusinessTransaction xBusinessTransaction : xBusinessTransactions) {
            maxRT = xBusinessTransaction.getElapsed() > maxRT ? xBusinessTransaction.getElapsed() : maxRT;
        }
        return maxRT;
    }

    public Long getMinResponseTime() {
        Long minRT = Long.MAX_VALUE - 1;
        for (XBusinessTransaction xBusinessTransaction : xBusinessTransactions) {
            minRT = xBusinessTransaction.getElapsed() < minRT ? xBusinessTransaction.getElapsed() : minRT;
        }
        return minRT;
    }

    public Integer getCalls() {
        Integer calls = 0;
        for (XBusinessTransaction xBusinessTransaction : xBusinessTransactions) {
            calls += xBusinessTransaction.getCalls();
        }
        return calls;
    }

    public double getCallsperMin(Range range) {
        double calls = getCalls();
        double mins = range.getRange() / (ONE_MIN);
        return mins > 0 ? calls / mins : 0.00;
    }

    public double getErrperMin(Range range) {
        double errs = getErrors();
        double mins = range.getRange() / (ONE_MIN);
        return mins > 0 ? errs / mins : 0.00;
    }

    public double getErrpercent() {
        double tmpErrors = getErrors();
        double tmpCalls = getCalls();
        return tmpCalls > 0 ? tmpErrors / tmpCalls : 0.00;
    }

    public void addAll(List<XBusinessTransaction> xBusinessTransactions) {
        this.xBusinessTransactions.addAll(xBusinessTransactions);
    }

    public void add(XBusinessTransaction xBusinessTransaction) {
        this.xBusinessTransactions.add(xBusinessTransaction);
    }

    public void setHealth(String health) {
        this.health = health;
    }

}
