package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.util.SplitListUtils;
import com.navercorp.pinpoint.web.view.XTransScatter;
import com.navercorp.pinpoint.web.view.XTransScatters;
import com.navercorp.pinpoint.web.vo.LimitedScanResult;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.XService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * Created by ${aires} on 11/24/16.
 */
@Service
public class XRpcScatterPlotServiceImpl implements XRpcScatterPlotService {
    private static final int DEFAULT_TRACE_SPLIT_COUNT = 100;
    private static final Long QUERY_DB_TIME_SLICE = 24 * 60 * 60 * 1000L;
    private static final Long AGGREGATION_TIME_SLICE = 15 * 60 * 1000L;
    private static final Integer MAX_SCATTER_PLOT_NUMN = 3 * 10000;
    private static final List<Long> RES_AGGREGATE_TIME_SLICE_ARRAY = newArrayList(
            0L, 500L, 1000L, 2000L, 5000L);
    @Autowired
    private XApplicationsService xApplicationsService;
    @Autowired
    private ApplicationTraceIndexDao applicationTraceIndexDao;
    @Autowired
    private TraceDao traceDao;
    @Autowired
    private XTransScatterServiceImpl xTransScatterServiceImpl;

    @Override
    public XTransScatters getXRpcScatterPlots(String applicationName, String rpc, Range range) {
        List<XTransScatter> normals = newArrayList();
        List<XTransScatter> warnings = newArrayList();
        List<XTransScatter> criticals = newArrayList();
        // 1  Time slice
        List<Range> ranges = range.splitRange(0, QUERY_DB_TIME_SLICE);
        for (Range rangeF : ranges
                ) {
            // 2 get spanlist
            List<SpanBo> filterSpanBo = getSpanBosFilterByRpc(applicationName, rpc, rangeF, -1L, -1L);
            // 3 get XTransScatters
            if (null != filterSpanBo && filterSpanBo.size() > 0) {
                XTransScatters xTransScattersTemp = xTransScatterServiceImpl.getScattersFilterBySpanBo(filterSpanBo);
                // 4 aggreation
                xTransScattersTemp = togetherXTransScatters(xTransScattersTemp, rangeF, AGGREGATION_TIME_SLICE);
                normals.addAll(xTransScattersTemp.getNormals());
                warnings.addAll(xTransScattersTemp.getWarnings());
                criticals.addAll(xTransScattersTemp.getCriticals());
            }
        }
        // 5 zhuanhuan
        XTransScatters xTransScatters = new XTransScatters.Builder()
                .Normals(normals)
                .Warings(warnings)
                .Criticals(criticals)
                .build();
        // depth juhe
        return depthAggregationXtransScatters(xTransScatters, range, AGGREGATION_TIME_SLICE, MAX_SCATTER_PLOT_NUMN);
    }

    @Override
    public List<XRpcScatterPlotTables> getXRpcScatterPlotTavlesList(String applicationName, String rpc, Range range, Long resMin, Long resMax) {
        List<SpanBo> filterSpanBo = getSpanBosFilterByRpc(applicationName, rpc, range, resMin, resMax);
        List<XRpcScatterPlotTables> XRpcScatterPlotTables = newArrayList();
        for (SpanBo spanBo : filterSpanBo) {
            XRpcScatterPlotTables xRpcScatterPlotTables = new XRpcScatterPlotTables(
                    spanBo.getStartTime(),
                    spanBo.getRpc(),
                    "post/get",
                    spanBo.getElapsed(),
                    spanBo.getSpanId());
            XRpcScatterPlotTables.add(xRpcScatterPlotTables);
        }
        return XRpcScatterPlotTables;
    }

    private XTransScatters depthAggregationXtransScatters(XTransScatters xTransScatters, Range range, Long time, Integer maxNum) {
        boolean flag = false;
        Integer i = 2;
        while (!flag) {
            int num = xTransScatters.getWarnings().size() + xTransScatters.getNormals().size() + xTransScatters.getCriticals().size();
            i++;
            if (num > maxNum) {
                xTransScatters = togetherXTransScatters(xTransScatters, range, i * time);
            } else {
                flag = true;
            }
            // max depth aggregation num < 768;
            if( i > 768){
                break;
            }
        }
        if(xTransScatters.getNormals().size() + xTransScatters.getWarnings().size() + xTransScatters.getCriticals().size() > maxNum) {
            List<XTransScatter> normals = newArrayList();
            List<XTransScatter> warnings = newArrayList();
            List<XTransScatter> criticals = newArrayList();
            return new XTransScatters.Builder()
                    .Normals(normals)
                    .Warings(warnings)
                    .Criticals(criticals)
                    .build();
        }
        return xTransScatters;
    }

    private XTransScatters togetherXTransScatters(XTransScatters xTransScatters, Range range, Long time) {
        HashMap<String, XTransScatter> xMapNormalsTemps = new HashMap<>();
        HashMap<String, XTransScatter> xMapWarningsTemps = new HashMap<>();
        HashMap<String, XTransScatter> xMapCriticalsTemps = new HashMap<>();
        List<Range> ranges = range.splitRange(0, time);
        for (Range rangeT : ranges) {

            xMapNormalsTemps = filterXTransScatterListByResAndRange(xMapNormalsTemps, xTransScatters.getNormals(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(0), RES_AGGREGATE_TIME_SLICE_ARRAY.get(1), rangeT);
            xMapNormalsTemps = filterXTransScatterListByResAndRange(xMapNormalsTemps, xTransScatters.getNormals(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(1), RES_AGGREGATE_TIME_SLICE_ARRAY.get(2), rangeT);

            xMapWarningsTemps = filterXTransScatterListByResAndRange(xMapWarningsTemps, xTransScatters.getWarnings(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(2), RES_AGGREGATE_TIME_SLICE_ARRAY.get(3), rangeT);
            xMapWarningsTemps = filterXTransScatterListByResAndRange(xMapWarningsTemps, xTransScatters.getWarnings(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(3), RES_AGGREGATE_TIME_SLICE_ARRAY.get(4), rangeT);
            xMapWarningsTemps = getXTransScatterListWhenResSoLong(xMapWarningsTemps, xTransScatters.getWarnings(), rangeT);

            xMapCriticalsTemps = filterXTransScatterListByResAndRange(xMapCriticalsTemps, xTransScatters.getCriticals(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(0), RES_AGGREGATE_TIME_SLICE_ARRAY.get(1), rangeT);
            xMapCriticalsTemps = filterXTransScatterListByResAndRange(xMapCriticalsTemps, xTransScatters.getCriticals(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(1), RES_AGGREGATE_TIME_SLICE_ARRAY.get(2), rangeT);
            xMapCriticalsTemps = filterXTransScatterListByResAndRange(xMapCriticalsTemps, xTransScatters.getCriticals(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(2), RES_AGGREGATE_TIME_SLICE_ARRAY.get(3), rangeT);
            xMapCriticalsTemps = filterXTransScatterListByResAndRange(xMapCriticalsTemps, xTransScatters.getCriticals(),
                    RES_AGGREGATE_TIME_SLICE_ARRAY.get(3), RES_AGGREGATE_TIME_SLICE_ARRAY.get(4), rangeT);
            xMapCriticalsTemps = getXTransScatterListWhenResSoLong(xMapCriticalsTemps, xTransScatters.getCriticals(), rangeT);
        }

        List<XTransScatter> normals = newArrayList();
        List<XTransScatter> warnings = newArrayList();
        List<XTransScatter> criticals = newArrayList();

        normals.addAll(xMapNormalsTemps.values());
        warnings.addAll(xMapWarningsTemps.values());
        criticals.addAll(xMapCriticalsTemps.values());
        xTransScatters = new XTransScatters.Builder()
                .Normals(normals)
                .Warings(warnings)
                .Criticals(criticals)
                .build();
        return xTransScatters;
    }

    private HashMap<String, XTransScatter> filterXTransScatterListByResAndRange(HashMap<String, XTransScatter> xTransScatterMap,
                                                                                List<XTransScatter> xTransScatterList, Long min, Long max, Range range) {
        long avr = range.getAvr();
        long from = range.getFrom();
        long to = range.getTo();
        List<XTransScatter> xTransScatterListTemp = xTransScatterList.stream()
                .filter(item -> item.getResponse() > min && item.getResponse() <= max
                        && item.getStartTime() >= from && item.getStartTime() < to)
                .map(item -> new XTransScatter(avr, (min + max) / 2)).collect(toList());
        if (xTransScatterListTemp.size() > 0) {
            xTransScatterMap.put(avr + String.valueOf((min + max) / 2L), xTransScatterListTemp.get(0));
        }
        return xTransScatterMap;
    }

    private HashMap<String, XTransScatter> getXTransScatterListWhenResSoLong(HashMap<String, XTransScatter> xTransScatterMap,
                                                                             List<XTransScatter> xTransScatterList, Range range) {
        long from = range.getFrom();
        long to = range.getTo();
        List<XTransScatter> warningTemps5000 = xTransScatterList.stream()
                .filter(item -> item.getResponse() > 5000 && item.getStartTime() >= from && item.getStartTime() < to)
                .map(item -> new XTransScatter(item.getStartTime(), item.getResponse()))
                .collect(toList());
        for (XTransScatter xTransScatter : warningTemps5000
                ) {
            xTransScatterMap.put(String.valueOf(xTransScatter.getStartTime()) + String.valueOf(xTransScatter.getResponse()), xTransScatter);
        }
        return xTransScatterMap;
    }

    private List<SpanBo> getSpanBosFilterByRpc(String applicationName, String rpc, Range range, Long resMin, Long resMax) {
        List<SpanBo> filterSpanBo = newArrayList();
        try {
            List<XService> servicelist = xApplicationsService.getXServices(applicationName);
            for (XService service : servicelist) {
                LimitedScanResult<List<TransactionId>> transactionIds = applicationTraceIndexDao.scanTraceIndex(service.getName(), range, 5000, false);
                List<TransactionId> transactionIdList = transactionIds.getScanData();
                List<List<TransactionId>> splitTransactionIdList =
                        SplitListUtils.splitTransactionIdList(newArrayList(transactionIdList), DEFAULT_TRACE_SPLIT_COUNT);
                for (List<TransactionId> splitTransactionIds : splitTransactionIdList) {
                    List<List<SpanBo>> spanBos = traceDao.selectAllSpans(splitTransactionIds);
                    for (List<SpanBo> spanBoList : spanBos) {
                        if (resMin == -1l || resMax == -1l) {
                            List<SpanBo> filterSpanBoList1 = spanBoList.stream()
                                    .filter(item -> item.getRpc().equals(rpc))
                                    .collect(toList());
                            filterSpanBo.addAll(filterSpanBoList1);
                        } else {
                            List<SpanBo> filterSpanBoList2 = spanBoList.stream()
                                    .filter(item -> item.getRpc().equals(rpc) && item.getElapsed() >= resMin && item.getElapsed() <= resMax)
                                    .collect(toList());
                            filterSpanBo.addAll(filterSpanBoList2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return newArrayList();
        }
        return filterSpanBo;
    }
}
