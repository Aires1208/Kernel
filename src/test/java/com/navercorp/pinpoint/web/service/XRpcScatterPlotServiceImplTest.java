package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.view.XTransScatter;
import com.navercorp.pinpoint.web.view.XTransScatters;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import scala.Int;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by ${aires} on 11/24/16.
 */

public class XRpcScatterPlotServiceImplTest {

    @Mock
    private XApplicationsService xApplicationsService;

    @Mock
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Mock
    private TraceDao traceDao;

    @Mock
    private XTransScatterServiceImpl xTransScatterServiceImpl;


    @InjectMocks
    private XRpcScatterPlotServiceImpl xRpcScatterPlotService = new XRpcScatterPlotServiceImpl();

    private static final Long QUERY_DB_TIME_SLICE = 24 * 60 * 60 * 1000L;
    private static final Long AGGREGATION_TIME_SLICE = 15 * 60 * 1000L;
    private static final int MAX_SCATTER_PLOT_NUMN = 3 * 10000;
    private static final Long res300 = 300L;
    private static final Long res600 = 600L;
    private static final Long res1200 = 1200L;
    private static final Long res2500 = 2500L;
    private static final Long res6000 = 6000L;
    private static final Long from = 148173150236400L;
    private static final Long to = 148179198236400L;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTogetherXTransScatters() throws Exception {
        XTransScatters xTransScatters = new XTransScatters.Builder()
                .Normals(createNormalsXTransScatterListData())
                .Warings(createWarningXTransScatterListData())
                .Criticals(createCriticalXTransScatterListData())
                .build();
        Method togettherxTransScatters = xRpcScatterPlotService.getClass().getDeclaredMethod("togetherXTransScatters",
                new Class[]{XTransScatters.class, Range.class, Long.class});
        togettherxTransScatters.setAccessible(true);
        Object xt = togettherxTransScatters.invoke(xRpcScatterPlotService, xTransScatters, new Range(from, to), AGGREGATION_TIME_SLICE);
        XTransScatters xt1 = (XTransScatters) xt;
//        xTransScatters = xRpcScatterPlotService.togetherXTransScatters(xTransScatters, new Range(from, to), AGGREGATION_TIME_SLICE);
        System.out.println("normal size:" + xt1.getNormals().size());
        System.out.println("warning size:" + xt1.getWarnings().size());
        System.out.println("critical size:" + xt1.getCriticals().size());
        //then
        assertThat(xt1.getNormals().size(), is(4));
        assertThat(xt1.getWarnings().size(), is(4));
        assertThat(xt1.getCriticals().size(), is(4));
    }

    private List<XTransScatter> createNormalsXTransScatterListData() {
        List<XTransScatter> xTransScatters = newArrayList(
                new XTransScatter(from, res300),
                new XTransScatter(from + 100L, res300 + 100L),
                new XTransScatter(from, res600),
                new XTransScatter(from + 100L, res600 + 100L),
                new XTransScatter(from + AGGREGATION_TIME_SLICE, res300),
                new XTransScatter(from + AGGREGATION_TIME_SLICE + 100L, res300 + 100L),
                new XTransScatter(from + AGGREGATION_TIME_SLICE, res600),
                new XTransScatter(from + AGGREGATION_TIME_SLICE + 100L, res600 + 100L));
        return xTransScatters;
    }

    private List<XTransScatter> createWarningXTransScatterListData() {
        List<XTransScatter> xTransScatters = newArrayList(
                new XTransScatter(from, res1200),
                new XTransScatter(from + 100L, res1200 + 100L),
                new XTransScatter(from, res2500),
                new XTransScatter(from + 100L, res2500 + 100L),
                new XTransScatter(from + AGGREGATION_TIME_SLICE, res1200),
                new XTransScatter(from + AGGREGATION_TIME_SLICE + 100L, res1200 + 100L),
                new XTransScatter(from + AGGREGATION_TIME_SLICE, res2500),
                new XTransScatter(from + AGGREGATION_TIME_SLICE + 100L, res2500 + 100L));
        return xTransScatters;
    }

    private List<XTransScatter> createCriticalXTransScatterListData() {
        List<XTransScatter> xTransScatters = newArrayList(
                new XTransScatter(from, res6000),
                new XTransScatter(from, res6000 + 100L),
                new XTransScatter(from, res6000 + 200L),
                new XTransScatter(from + 100L, res6000 + 200L));
        return xTransScatters;
    }

    @Test
    public void testDepthAggregationXtransScatters() throws Exception {
        XTransScatters xTransScatters = new XTransScatters.Builder()
                .Normals(createNormalsXTransScatterListData())
                .Warings(createWarningXTransScatterListData())
                .Criticals(createCriticalXTransScatterListData())
                .build();
        int num1 = xTransScatters.getNormals().size() + xTransScatters.getWarnings().size() + xTransScatters.getCriticals().size();
        System.out.println("num1:" + num1);
        Method depthAggregationXtransScatters = xRpcScatterPlotService.getClass().getDeclaredMethod("depthAggregationXtransScatters",
                new Class[]{XTransScatters.class, Range.class, Long.class, Integer.class});
        depthAggregationXtransScatters.setAccessible(true);
        Object xt = depthAggregationXtransScatters.invoke(xRpcScatterPlotService, xTransScatters, new Range(from, to), AGGREGATION_TIME_SLICE, 5);
        XTransScatters xt1 = (XTransScatters) xt;
        int num2 = xt1.getNormals().size() + xt1.getWarnings().size() + xt1.getCriticals().size();
        System.out.println("num2:" + num2);
        assertTrue(num2 <= 5);
    }

    @Test
    public void testGetSpanBosFilterByRpc() throws Exception {

    }

    @Test
    public void getXRpcScatterPlotList() throws Exception {
        Range range = new Range(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7, System.currentTimeMillis());

        xRpcScatterPlotService.getXRpcScatterPlots("FM1", "/api/fm-active/v1/activealarms", range);
        xRpcScatterPlotService.getXRpcScatterPlotTavlesList("FM1", "/api/fm-active/v1/activealarms", range, (long) 0, (long) 1000);

    }
}