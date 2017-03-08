package com.navercorp.pinpoint.web.vo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XDBsRisk {
    private List<String > dbList = newArrayList();
    private List<XDBRisk> dbRisks = newArrayList();

    public XDBsRisk(List<String> dbList, List<XDBRisk> dbRisks) {
        this.dbList = dbList;
        this.dbRisks = dbRisks;
    }

    public List<String> getDbList() {
        return dbList;
    }

    public List<XDBRisk> getDbRisks() {
        return dbRisks;
    }
}
