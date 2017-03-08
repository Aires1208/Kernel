package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XServicesRisk {
    private List<XDataPoint<Integer>> topBusyServices = newArrayList();
    private List<XDataPoint<Double>> topRiskServices = newArrayList();

    public XServicesRisk(List<XDataPoint<Integer>> topBusyServices, List<XDataPoint<Double>> topRiskServices) {
        this.topBusyServices = topBusyServices;
        this.topRiskServices = topRiskServices;
    }

    public List<XDataPoint<Integer>> getTopBusyServices() {
        return topBusyServices;
    }

    public List<XDataPoint<Double>> getTopRiskServices() {
        return topRiskServices;
    }

}
