package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.*;

@JsonSerialize(using = XApplicationDashBoardSerializer.class)
public class XApplicationDashBoard {
    private XApplication xApplication;

    private final XServiceTopo xServiceTopo;

    private TransactionHealth transactionHealth = new TransactionHealth();
    private XLoadInfo loadInfo;

    public XApplicationDashBoard(XApplication xApplication, XServiceTopo xServiceTopo, Range range) {

        this.xApplication = xApplication;
        this.xServiceTopo = xServiceTopo;
        xApplication.setRange(range);
        xApplication.setXNodes(xServiceTopo.getXNodes());
        this.setTransactionHealth(xApplication.getTransactionHealth());
    }


    public XServiceTopo getTopo() {
        return xServiceTopo;
    }


    public TransactionHealth getTransactionHealth() {
        return transactionHealth;
    }


    public String getName() {
        return xApplication.getName();
    }

    public XMetrics getLoadMetrics() {
        return new XMetrics("load N/A",new String[]{"9:15","9:30"},new Integer[]{1,2});
    }

    public XMetrics getRespondMetrics() {
        return new XMetrics("load N/A",new String[]{"9:15","9:30"},new Integer[]{1,2});
    }

    public XMetrics getErrorMetrics() {
        return new XMetrics("load N/A",new String[]{"9:15","9:30"},new Integer[]{1,2});
    }

    public void setTransactionHealth(TransactionHealth transactionHealth) {
        this.transactionHealth = transactionHealth;
    }


    public XApplication getXApplication() {
        return xApplication;
    }

    public void setLoadInfo(XLoadInfo loadInfo) {
        this.loadInfo = loadInfo;
    }

    public XLoadInfo getLoadInfo() {
        return loadInfo;
    }
}
