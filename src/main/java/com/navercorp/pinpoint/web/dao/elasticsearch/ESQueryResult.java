package com.navercorp.pinpoint.web.dao.elasticsearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/13/17.
 */
public class ESQueryResult {
    private String name;
    private List<ESMetrics> esMetricses = new ArrayList<ESMetrics>();

    public ESQueryResult(String name, List<ESMetrics> esMetricses) {
        this.name = name;
        this.esMetricses = esMetricses;
    }

//    public ESQueryResult(String name, List<ESMetrics> totalMetrics,List<ESMetrics> esMetricses) {
//        this.name = name;
//        this.esMetricses = esMetricses;
//    }


    public String getName() {
        return name;
    }

    public List<ESMetrics> getEsMetricses() {
        return esMetricses;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ESQueryResult is following:").append(name).append("\n");
        stringBuilder.append(esMetricses);
        return  stringBuilder.toString();
    }
}
