package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.topo.domain.XDot;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.view.StringWrapper.DefaultDateStr;

/**
 * Created by root on 7/26/16.
 */
public class XLoadInfo {
    private List<XDot> xDots = newArrayList();
    private Range range;

    public XLoadInfo(List<XDot> xDots, Range range) {
        this.xDots = xDots;
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public String[] getTimes() {
        List<String> times = newArrayList();
        times.addAll(xDots.stream().map(xDot -> DefaultDateStr(xDot.getAcceptedTime())).collect(Collectors.toList()));
        return times.toArray(new String[0]);
    }


    public Long[] getCalls() {
        List<Long> callData = newArrayList();
        callData.addAll(xDots.stream().map(xDot -> xDot.getxMetric().getCalls()).collect(Collectors.toList()));
        return callData.toArray(new Long[0]);
    }

    public Long[] getErrors() {
        List<Long> errorData = newArrayList();
        errorData.addAll(xDots.stream().map(xDot -> xDot.getxMetric().getErrors()).collect(Collectors.toList()));
        return errorData.toArray(new Long[0]);
    }

    public Double[] getResponses() {
        List<Double> responseData = newArrayList();
        for (XDot xDot : xDots) {
            double response = xDot.getxMetric().getResponseTime();
            double calls = xDot.getxMetric().getCalls();
            double avgResponse = calls > 0 ? response / calls : 0.00;
            BigDecimal bg = new BigDecimal(avgResponse);
            double processedResponse = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            responseData.add(processedResponse);
        }

        return responseData.toArray(new Double[0]);
    }

}
