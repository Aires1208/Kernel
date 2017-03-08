package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.LimitedScanResult;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.XService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class XTransactionServiceImplTest {

    @Mock
    private XEventService eventService;

    @Mock
    private TraceDao traceDao;

    @Mock
    private ApplicationIndexDao applicationIndexDao;

    @Mock
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Mock
    private XApplicationsService xApplicationsService;

    @Mock
    private XInstanceServiceImpl xInstanceService;

    @InjectMocks
    private XTransactionServiceImpl xTransactionService = new XTransactionServiceImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.when(xApplicationsService.getXServices(anyString())).thenReturn(newArrayList(new XService("fm_active", ServiceType.SPRING)));
        Mockito.when(applicationTraceIndexDao.scanTraceIndex(anyString(), any(Range.class), anyInt(), anyBoolean())).thenReturn(getLimitedRes());

        Mockito.when(traceDao.selectAllSpans(anyList())).thenReturn(getTrace());
        Mockito.when(traceDao.selectSpan(anyObject())).thenReturn(newArrayList(getSpanBo()));

        Mockito.when(xInstanceService.getXServiceName(anyString())).thenReturn("fm_active");

        Mockito.when(eventService.getAppTransactionEvents(anyString(), any(Range.class))).thenReturn(getAppEvents());
        Mockito.when(eventService.getServiceTransactionEvents(anyString(), anyString(), any(Range.class))).thenReturn(getSvcEvents());
        Mockito.when(eventService.getInstanceTransactionEvents(anyString(), anyString(), anyString(), any(Range.class))).thenReturn(getInstEvents());
    }

    private List<ResultEvent> getInstEvents() {
        ResultEvent event = new ResultEvent("app=fm,service=fm_active,instance=fm-active,name=/getActiveAlarm", 10021, 115L, 157L, "detail");

        return ImmutableList.of(event);
    }

    private List<ResultEvent> getSvcEvents() {
        ResultEvent event = new ResultEvent("app=fm,service=fm_active,name=/getActiveAlarm", 10021, 115L, 157L, "detail");

        return ImmutableList.of(event);
    }

    private List<ResultEvent> getAppEvents() {
        ResultEvent event = new ResultEvent("app=fm,name=/getActiveAlarm", 10022, 115L, 157L, "detail");

        return ImmutableList.of(event);
    }

    private List<List<SpanBo>> getTrace() {
        SpanBo spanBo = getSpanBo();
        return ImmutableList.of(newArrayList(spanBo));
    }

    private SpanBo getSpanBo() {
        SpanBo spanBo = new SpanBo("fm-active", 110L, 2L, 115L, 200, 10001L);
        spanBo.setParentSpanId(-1L);
        spanBo.setErrCode(1);
        spanBo.setAgentId("fm-active");
        spanBo.setApplicationId("fm_active");
        spanBo.setRpc("/getActiveAlarm");
        return spanBo;
    }

    private LimitedScanResult<List<TransactionId>> getLimitedRes() {
        LimitedScanResult<List<TransactionId>> limitedScanResult = new LimitedScanResult<>();

        limitedScanResult.setScanData(newArrayList(new TransactionId("fm-active", 110L, 2L)));

        return limitedScanResult;
    }


    @Test
    public void getAppTransactions() throws Exception {
        //given
        String expect = "{\"apps\":[],\"typeList\":[{\"id\":\"OTHER\",\"value\":\"OTHER\"}],\"tables\":[{\"name\":\"/getActiveAlarm\",\"health\":\"Critical\",\"tier\":\"fm-active\",\"responseTime\":200,\"maxResponseTime\":200,\"calls\":1,\"callsPerMin\":\"0.00\",\"errors\":1,\"errorPercent\":\"100.00%\",\"errorsPerMin\":\"0.00\"}]}";
        XTraceQuery query = new XTraceQuery.Builder().Application("fm").From(111L).To(222L).Build();

        //when
        XTransactions xTransactions = xTransactionService.getTransactions("application", query);
        String result = new ObjectMapper().writeValueAsString(xTransactions);

        //then
        assertEquals(result, expect);
    }

    @Test
    public void getServiceTransactions() throws Exception {
        //given
        String expect = "{\"apps\":[],\"typeList\":[{\"id\":\"OTHER\",\"value\":\"OTHER\"}],\"tables\":[{\"name\":\"/getActiveAlarm\",\"health\":\"Warning\",\"tier\":\"fm-active\",\"responseTime\":200,\"maxResponseTime\":200,\"calls\":1,\"callsPerMin\":\"0.00\",\"errors\":1,\"errorPercent\":\"100.00%\",\"errorsPerMin\":\"0.00\"}]}";
        XTraceQuery query = new XTraceQuery.Builder().Application("fm").Service("fm_active").From(111L).To(222L).Build();

        XTransactions xTransactions = xTransactionService.getTransactions("service", query);
        String result = new ObjectMapper().writeValueAsString(xTransactions);

        //then
        assertEquals(result, expect);
    }

    @Test
    public void getInstanceTransactions() throws Exception {
        //given
        XTraceQuery query = new XTraceQuery.Builder().Application("fm").Service("fm_active").Instance("fm-active").From(111L).To(222L).Build();
        String expect = "{\"apps\":[],\"typeList\":[{\"id\":\"OTHER\",\"value\":\"OTHER\"}],\"tables\":[{\"name\":\"/getActiveAlarm\",\"health\":\"Warning\",\"tier\":\"fm-active\",\"responseTime\":200,\"maxResponseTime\":200,\"calls\":1,\"callsPerMin\":\"0.00\",\"errors\":1,\"errorPercent\":\"100.00%\",\"errorsPerMin\":\"0.00\"}]}";

        XTransactions xTransactions = xTransactionService.getTransactions("instance", query);
        String result = new ObjectMapper().writeValueAsString(xTransactions);

        //then
        assertEquals(result, expect);
    }
}