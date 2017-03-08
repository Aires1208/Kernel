package com.navercorp.pinpoint.web.util;

import com.navercorp.pinpoint.web.service.XBusinessTransactions;
import com.navercorp.pinpoint.web.vo.XServiceHealth;
import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-9-22.
 */
public class SubListUtils {
    public static List<XDataPoint<Long>> getTopNResponseTimeTransactions(List<XBusinessTransactions> topNResponseTimeTransactions) {
        List<XDataPoint<Long>> topResponseTime = newArrayList();
        topResponseTime.addAll(topNResponseTimeTransactions.stream().map(xBusinessTransactions -> new XDataPoint<>(xBusinessTransactions.getTraceName(), xBusinessTransactions.getAverageResponseTime())).collect(Collectors.toList()));
        return topResponseTime;
    }

    public static List<XDataPoint<Integer>> getTopNErrorsTransactions(List<XBusinessTransactions> topNErrorsTransactions) {
        List<XDataPoint<Integer>> topNErrors = newArrayList();
        topNErrors.addAll(topNErrorsTransactions.stream().map(xBusinessTransactions -> new XDataPoint<>(xBusinessTransactions.getTraceName(), xBusinessTransactions.getErrors())).collect(Collectors.toList()));
        return topNErrors;
    }

    public static List<XDataPoint<Integer>> getTopNCallsTransactions(List<XBusinessTransactions> topNCallsTransactions) {
        List<XDataPoint<Integer>> topNCalls = newArrayList();
        topNCalls.addAll(topNCallsTransactions.stream().map(xBusinessTransactions -> new XDataPoint<>(xBusinessTransactions.getTraceName(), xBusinessTransactions.getCalls())).collect(Collectors.toList()));
        return topNCalls;
    }

    public static List<XDataPoint<Double>> getTopNRiskServices(List<XServiceHealth> topNRiskServices) {
        List<XDataPoint<Double>> topRisk = newArrayList();
        topRisk.addAll(topNRiskServices.stream().map(xServiceHealth -> new XDataPoint<>(xServiceHealth.getServiceName(), xServiceHealth.getHealthScore())).collect(Collectors.toList()));
        return topRisk;
    }

    public static List<XDataPoint<Integer>> getTopNCallsServices(List<XServiceHealth> topNCallsServices) {
        List<XDataPoint<Integer>> topCalls = newArrayList();
        topCalls.addAll(topNCallsServices.stream().map(xServiceHealth -> new XDataPoint<>(xServiceHealth.getServiceName(), xServiceHealth.getCalls())).collect(Collectors.toList()));
        return topCalls;
    }
}
