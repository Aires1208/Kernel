package com.navercorp.pinpoint.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.navercorp.pinpoint.common.topo.domain.XRpc;
import com.navercorp.pinpoint.web.dao.RpcStatisticDao;
import com.navercorp.pinpoint.web.view.XRpcs;
import com.navercorp.pinpoint.web.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by ${10183966} on 11/24/16.
 */
@Service
public class XRpcStatisticServiceImpl implements XRpcStatisticService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long INTERVAL = 1000 * 60 * 60 * 60;

    @Autowired
    private RpcStatisticDao rpcStatisticDao;

    @Autowired
    private XApplicationsService xApplicationsService;


    private static List<XRpc> quickSort(List<XRpc> xRpcs, int beginSortIndex) {
//        String tag = "-";
//        for (int forward = 0; forward < beginSortIndex; forward++) {
//            tag += "-";
//        }
        int size = xRpcs.size();
        if (size == 0)
            return xRpcs;
        XRpc pivot = xRpcs.get(0);
        List<XRpc> lLower = newArrayList();
        List<XRpc> lHigher = newArrayList();
        for (XRpc rpc : xRpcs) {
            if (rpc.getCount() > pivot.getCount()) {
                lLower.add(rpc);
            }
            if (rpc.getCount() < pivot.getCount()) {
                lHigher.add(rpc);
            }
        }
        List<XRpc> sorted = newArrayList();
        sorted.addAll(quickSort(lLower, beginSortIndex++));
        sorted.add(pivot);
        sorted.addAll(quickSort(lHigher, beginSortIndex++));
        return sorted;
    }

    private static List<Range> splitRange(Range range) {
        long tmpFrom = range.getFrom();
        long tmpTo = range.getTo();

        List<Range> ranges = newArrayList();
        while (tmpFrom < tmpTo) {
            ranges.add(new Range(tmpFrom, Math.min(tmpFrom + INTERVAL, tmpTo)));
            tmpFrom += INTERVAL;
        }
        return ranges;
    }

    private static List<XRpc> aggregationAlgorithm(List<XRpc> xRpcs) {
        Map<String, List<XRpc>> mappingResult = mappingXRpcs(xRpcs);

        return reduceXRpcs(mappingResult);
    }

    private static Map<String, List<XRpc>> mappingXRpcs(List<XRpc> xRpcs) {
        Map<String, List<XRpc>> xrpcMp = newHashMap();
        for (XRpc xRpc : xRpcs) {
            if (null != xrpcMp.get(xRpc.getRpc())) {
                xrpcMp.get(xRpc.getRpc()).add(xRpc);
            } else {
                xrpcMp.put(xRpc.getRpc(), newArrayList(xRpc));
            }
        }
        return xrpcMp;
    }

    private static List<XRpc> reduceXRpcs(Map<String, List<XRpc>> mappingResult) {
        List<XRpc> xRpcs = newArrayList();
        Iterator iterator = mappingResult.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<XRpc>> entry = (Map.Entry<String, List<XRpc>>) iterator.next();
            List<XRpc> xRpcList = entry.getValue();
            String method = "";
            int count = 0;
            int successCount = 0;
            long min_time = Long.MAX_VALUE;
            long max_time = Long.MIN_VALUE;
            long duration = 0;
            for (XRpc xRpc : xRpcList) {
                count += xRpc.getCount();
                successCount += xRpc.getSuccessCount();
                min_time = (min_time < xRpc.getMin_time()) ? min_time : xRpc.getMin_time();
                max_time = (max_time > xRpc.getMax_time()) ? max_time : xRpc.getMax_time();
                duration += xRpc.getDuration();
                method = xRpc.getMethod();
            }
            XRpc xRpc = new XRpc(method, count, successCount, min_time, max_time, duration, entry.getKey());
            xRpc.setAvg_time(count != 0 ? duration / count : 0);
            xRpcs.add(xRpc);

        }
        return xRpcs;
    }


    @Override
    public Result getXRpcStatisticList(String appName, Range range) {
        ResultBuilder resultBuilder = ResultBuilder.newResult();
        List<XRpc> xRpcList = newArrayList();
        try {
            List<XService> xServices = xApplicationsService.getXServices(appName);
            List<Range> splitRanges = splitRange(range);
            for (Range rangeAfterSplit : splitRanges) {
                for (XService xService : xServices) {
                    xRpcList.addAll(aggregationAlgorithm(rpcStatisticDao.getXRpcList(xService.getName(), rangeAfterSplit)));
                }
            }
            XRpcs object = new XRpcs();
            if (xRpcList.size() > 50) {
                object.setURLAggregation(transformListXRpcToVoXRpc(quickSort(aggregationAlgorithm(xRpcList), 0).subList(0, 50)));
            } else {
                object.setURLAggregation(transformListXRpcToVoXRpc(quickSort(aggregationAlgorithm(xRpcList), 0)));
            }
            object.setUseCaseAggregation(newArrayList());

            resultBuilder.data((JSONObject) JSON.toJSON(object)).message("SUCCESS").status(1);
        } catch (Exception e) {
            logger.warn("build RPC error:{}", e.getMessage(), e);
            resultBuilder.status(0).message(e.getMessage());
        }
//    else {
//            resultBuilder.status(0).message("FAIL");
//        }
        return resultBuilder.build();
    }

    private static VoXRpc transformXRpcToVoXRpc(XRpc rpc) {
        double successRatio = (double) rpc.getSuccessCount() / rpc.getCount();
        BigDecimal b = BigDecimal.valueOf(successRatio);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new VoXRpc(rpc.getMethod(), rpc.getCount(), String.valueOf((int) (f1 * 100)) + "%", rpc.getMin_time(), rpc.getMax_time(), rpc.getDuration(), rpc.getAvg_time(), rpc.getRpc());
    }

    private static List<VoXRpc> transformListXRpcToVoXRpc(List<XRpc> xRpcs) {
        List<VoXRpc> voXRpcs = newArrayList();
        voXRpcs.addAll(xRpcs.stream().map(XRpcStatisticServiceImpl::transformXRpcToVoXRpc).collect(Collectors.toList()));
        return voXRpcs;
    }

}
