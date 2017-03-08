package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XDBRisk {
    private String dbName;
    private List<XDataPoint<Double>> topSlowSql = newArrayList();
    private List<XDataPoint<Integer>> topFrequencySql = newArrayList();

    public XDBRisk(String dbName, List<XDataPoint<Double>> topSlowSql, List<XDataPoint<Integer>> topFrequencySql) {
        this.dbName = dbName;
        this.topSlowSql = topSlowSql;
        this.topFrequencySql = topFrequencySql;
    }

    public String getDbName() {
        return dbName;
    }

    public List<XDataPoint<Double>> getTopSlowSql() {
        return topSlowSql;
    }

    public List<XDataPoint<Integer>> getTopFrequencySql() {
        return topFrequencySql;
    }
}
