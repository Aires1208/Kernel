package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.usercase.CalculateInstanceTopoLineUserCase;
import com.navercorp.pinpoint.web.calltree.span.CallTreeIterator;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.view.XTraceDetail;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.XMetrics;
import com.navercorp.pinpoint.web.vo.XServiceTopo;
import com.navercorp.pinpoint.web.vo.callstacks.RecordSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.view.StringWrapper.DefaultDateStr;

@Service
public class XTraceDetailServiceImpl {
    @Autowired
    private TraceDao traceDao;

    @Autowired
    private ServiceTypeRegistryService registry;

    @Autowired
    private SpanService spanService;

    @Autowired
    private AgentStatDao agentStatDao;

    @Autowired
    private TransactionInfoService transactionInfoService;

    public XTraceDetail getXTraceDetail(String traceIdParam, long focusTimestamp) {

        List<SpanBo> spanBos = traceDao.selectSpanAndAnnotation(new TransactionId(traceIdParam));

        TopoLine topoLine = new CalculateInstanceTopoLineUserCase(spanBos, registry).execute();

        XMetrics cpuXMetrics = getRespondMetrics(spanBos);

        XMetrics memXMetrics = getRespondMetrics(spanBos);

        XMetrics storeXMetrics = getErrorMetrics(spanBos);

        XMetrics netXMetrics = getErrorMetrics(spanBos);

        RecordSet recordSet = getRecordSet(traceIdParam, focusTimestamp);

        return new XTraceDetail(
                new XServiceTopo(topoLine.getXNodes(), topoLine.getXLinks()), cpuXMetrics, memXMetrics, storeXMetrics
                , netXMetrics, recordSet);
    }

    private RecordSet getRecordSet(String traceIdParam, long focusTimestamp) {
        final TransactionId traceId = new TransactionId(traceIdParam);

        // select spans
        final SpanResult spanResult = this.spanService.selectSpan(traceId, focusTimestamp);
        final CallTreeIterator callTreeIterator = spanResult.getCallTree();

        return this.transactionInfoService.createRecordSet(callTreeIterator, focusTimestamp);
    }


    private XMetrics getRespondMetrics(List<SpanBo> spanBos) {
        long errors = 0;
        long total = 0;
        List<String> times = newArrayList();

        List<Integer> elapsedList = newArrayList();
        for (SpanBo spanBo : spanBos) {
            String date = DefaultDateStr(spanBo.getStartTime());
            times.add(date);
            elapsedList.add(spanBo.getElapsed());
            if (0 != spanBo.getErrCode()) {
                errors++;
            }
            total++;
        }
        String desc = errors + " errors," + "total is " + total;
        return new XMetrics(desc, times.toArray(new String[0]), elapsedList.toArray(new Integer[0]));
    }

    private XMetrics getErrorMetrics(List<SpanBo> spanBos) {
        long errors = 0;
        List<String> times = newArrayList();
        List<Integer> elapsedList = newArrayList();
        for (SpanBo spanBo : spanBos) {

            if (0 != spanBo.getErrCode()) {
                errors++;
                String date = DefaultDateStr(spanBo.getStartTime());
                times.add(date);
                times.add(new Date(spanBo.getStartTime()).toString());
                elapsedList.add(spanBo.getElapsed());
            }
        }
        String desc = errors + " errors";
        return new XMetrics(desc, times.toArray(new String[0]), elapsedList.toArray(new Integer[0]));
    }
}
