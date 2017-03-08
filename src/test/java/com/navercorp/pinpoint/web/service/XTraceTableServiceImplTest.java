package com.navercorp.pinpoint.web.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.InstanceTraceIdIndexDao;
import com.navercorp.pinpoint.web.dao.ServiceTraceIdIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.filter.CmdRanger;
import com.navercorp.pinpoint.web.view.XTraceTable;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.XService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XTraceTableServiceImplTest {
    @Mock
    private TraceDao traceDao;

    @Mock
    private ServiceTraceIdIndexDao serviceTraceIdIndexDao;

    @Mock
    private InstanceTraceIdIndexDao instanceTraceIdIndexDao;

    @Mock
    private XApplicationsServiceImpl xApplicationsService;

    @InjectMocks
    private XTraceTableService xTraceTableService = new XTraceTableServiceImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private TransactionId id1 = new TransactionId("fm-active", 123456778L, 1L);
    private TransactionId id2 = new TransactionId("fm-active", 123456778L, 5L);

    private SpanBo spanBo1 = new SpanBo("fm-active", 123456778L, 1L, 123458756L, 23, 0x32345e);
    private SpanBo spanBo2 = new SpanBo("fm-active", 123456778L, 5L, 123459878L, 43, 0xe234fc);

    @Test
    public void should_return_trace_table_when_input_application_FM() throws Exception {
        //given
        final String appName = "fm";
        spanBo1.setParentSpanId(-1);
        XTraceQuery query = new XTraceQuery.Builder().Application(appName).Command("/getAlarm").From(123400000L).To(123600000L).Build();

        //when
        when(xApplicationsService.getXServices(anyString()))
                .thenReturn(ImmutableList.of(new XService("fm_active", ServiceType.STAND_ALONE)));

        when(serviceTraceIdIndexDao.findServiceTraceIdsByTraceName(anyString(), anyString(), any(Range.class)))
                .thenReturn(ImmutableSet.of(id1, id2));

        when(traceDao.selectAllSpans(anyCollectionOf(TransactionId.class))).thenReturn(ImmutableList.of(newArrayList(spanBo1), newArrayList(spanBo2)));

        //then
        XTraceTable traceTable = xTraceTableService.getApplicationTraceTable(query);

        assertEquals(traceTable.getxTraces().size(), 1);
        assertEquals(traceTable.getxTypes().size(), 2);
        assertNull(traceTable.getxTraces().get(0).getPath());
        assertEquals(traceTable.getxTraces().get(0).getType(), new CmdRanger().getType(-1));
    }


    @Test
    public void should_return_trace_table_when_input_is_fm_active() throws Exception {
        //given
        final String serviceName = "fm_active";
        spanBo2.setParentSpanId(-1);
        spanBo2.setRpc("/getAlarm");
        XTraceQuery query = new XTraceQuery.Builder().Service(serviceName).Command("/getAlarm").From(123400000L).To(123600000L).Build();

        //when
        when(serviceTraceIdIndexDao.findServiceTraceIdsByTraceName(anyString(), anyString(), any(Range.class))).thenReturn(ImmutableSet.of(id1, id2));

        when(traceDao.selectAllSpans(anyCollectionOf(TransactionId.class))).thenReturn(ImmutableList.of(newArrayList(spanBo1), newArrayList(spanBo2)));

        //then
        XTraceTable traceTable = xTraceTableService.getServiceTraceTable(query);

        assertEquals(traceTable.getxTraces().size(), 1);
        assertEquals(traceTable.getxTraces().get(0).getPath(), "/getAlarm");
    }


    @Test
    public void should_return_trace_table_when_input_is_fmactive() throws Exception {
        //given
        final String instanceName = "fm-active";
        spanBo1.setParentSpanId(-1);
        spanBo2.setParentSpanId(-1);
        XTraceQuery query = new XTraceQuery.Builder().Instance(instanceName).Command("/getAlarm").From(123400000L).To(123600000L).Build();

        //when
        when(instanceTraceIdIndexDao.findTransactionIds(anyString(), anyString(), any(Range.class))).thenReturn(ImmutableSet.of(id1, id2));

        when(traceDao.selectAllSpans(anyCollectionOf(TransactionId.class))).thenReturn(ImmutableList.of(newArrayList(spanBo2), newArrayList(spanBo1)));

        //then
        XTraceTable traceTable = xTraceTableService.getInstanceTraceTable(query);

        assertEquals(traceTable.getxTraces().size(), 2);
    }
}