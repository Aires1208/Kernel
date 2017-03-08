package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;

import java.util.List;

public class XTransactionsRisk {

    private List<XDataPoint<Integer>> topCalls;
    private List<XDataPoint<Integer>> topErrors;
    private List<XDataPoint<Long>> topResponse;

    public XTransactionsRisk(List<XDataPoint<Integer>> topCalls, List<XDataPoint<Integer>> topErrors, List<XDataPoint<Long>> topResponse) {
        this.topCalls = topCalls;
        this.topErrors = topErrors;
        this.topResponse = topResponse;
    }

    public List<XDataPoint<Integer>> getTopCalls() {
        return topCalls;
    }

    public List<XDataPoint<Integer>> getTopErrors() {
        return topErrors;
    }

    public List<XDataPoint<Long>> getTopResponse() {
        return topResponse;
    }
}
