package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.XDBsRisk;
import com.navercorp.pinpoint.web.vo.XHealth;
import com.navercorp.pinpoint.web.vo.XServicesRisk;
import com.navercorp.pinpoint.web.vo.XTransactionsRisk;

import java.util.List;

@JsonSerialize(using = XAppReportSerializer.class)
public class XAppReport {
    private final String appName;
    private List<String > applist;
    private XHealth transactionHealth;
    private XHealth serviceHealth;
    private XHealth applicationHealth;
    private XTransactionsRisk transactionRisk;
    private XServicesRisk serviceRisk;
    private XDBsRisk dbsRisk;

    public XAppReport(String appName) {
        this.appName = appName;
    }

    public List<String> getApplist() {
        return applist;
    }

    public void setApplist(List<String> applist) {
        this.applist = applist;
    }

    public String getAppName() {
        return appName;
    }

    public XHealth getTransactionHealth() {
        return transactionHealth;
    }

    public void setTransactionHealth(XHealth transactionHealth) {
        this.transactionHealth = transactionHealth;
    }

    public XHealth getServiceHealth() {
        return serviceHealth;
    }

    public void setServiceHealth(XHealth serviceHealth) {
        this.serviceHealth = serviceHealth;
    }

    public XHealth getApplicationHealth() {
        return applicationHealth;
    }

    public void setApplicationHealth(XHealth applicationHealth) {
        this.applicationHealth = applicationHealth;
    }

    public XTransactionsRisk getTransactionRisk() {
        return transactionRisk;
    }

    public void setTransactionRisk(XTransactionsRisk transactionRisk) {
        this.transactionRisk = transactionRisk;
    }

    public XServicesRisk getServiceRisk() {
        return serviceRisk;
    }

    public void setServiceRisk(XServicesRisk serviceRisk) {
        this.serviceRisk = serviceRisk;
    }

    public XDBsRisk getDBsRisk() {
        return dbsRisk;
    }

    public void setDBsRisk(XDBsRisk dbsRisk) {
        this.dbsRisk = dbsRisk;
    }
}
