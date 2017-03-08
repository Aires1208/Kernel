package com.navercorp.pinpoint.web.service;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.dao.ActiveEventDao;
import com.navercorp.pinpoint.web.dao.HistoryEventDao;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XEventServiceImplTest {

    @Mock
    private XTracesListService tracesListService;

    @Mock
    private ActiveEventDao activeEventDao;

    @Mock
    private HistoryEventDao historyEventDao;

    @InjectMocks
    private XEventService eventService = new XEventServiceImpl();

    private XTransactionName name1 = new XTransactionName("fm_active", "/api/fm-active/v1/activealarms", 1477115974114L, "fm-active");
    private XTransactionName name2 = new XTransactionName("fm_active", "/favicon.ico", 1477036025000L, "fm-active");
    private XTransactionName name3 = new XTransactionName("fm_active", "/api/fm-active/v1/activealarms", 1477036024500L, "fm-active");

    private ResultEvent fm_event1 = new ResultEvent("app=fm", 10011, 11111111111111L, 0L, "calls heavy warning");
    private ResultEvent fm_event2 = new ResultEvent("app=fm", 10082, 333333333333L, 44444444444L, "responseTime over baseline critical");
    private ResultEvent fmactive_event3 = new ResultEvent("app=fm, service=fm_active", 20050, 1467987897L, 0L, "error ratio too high normal");
    private ResultEvent fmactive_event4 = new ResultEvent("app=fm, service=fm_active", 20022, 14754565476L, 66666666666L, "calls over baseline critical");
    private ResultEvent fm_active_event5 = new ResultEvent("app=fm, service=fm_active, instance=fm-active", 30072, 1111117777L, 0L, "responseTime over baseline critical");
    private ResultEvent fm_active_event6 = new ResultEvent("app=fm, service=fm_active, instance=fm-active", 30031, 88888888111L, 224555222888822L, "errors heavy warning");
    private ResultEvent trace_event1 = new ResultEvent("app=fm, name=/favicon.ico", 40012, 1453453L, 0L, "calls heavy critical");
    private ResultEvent trace_event2 = new ResultEvent("app=fm, name=/favicon.ico", 40071, 123445665245L, 2224325456892L, "rulename:appresponsetime;ruledetail:responsetime>10s in last10min");
    private ResultEvent trace_event3 = new ResultEvent("app=fm, service=fm_active, name=/api/fm-active/v1/activealarms", 40022, 14575645L, 0L, "calls over baseline critical");
    private ResultEvent trace_event4 = new ResultEvent("app=fm, service=fm_active, name=/api/fm-active/v1/activealarms", 40031, 123445665245L, 2224325456892L, "errors heavy critcal");
    private ResultEvent trace_event5 = new ResultEvent("app=fm, service=fm_active, instance=fm-active, name=/api/fm-active/v1/activealarms", 40010, 123445665245L, 2224325456892L, "calls heavy normal");
    private ResultEvent trace_event6 = new ResultEvent("app=fm, service=fm_active, instance=fm-active, name=/api/fm-active/v1/activealarms", 40052, 123445665245L, 2224325456892L, "error ratio too high critical");
    private ResultEvent host1_event = new ResultEvent("hostid=fakehost@unknow-mac", 80092, 123L, 125L, "detail");
    private ResultEvent host2_event = new ResultEvent("hostid=fakehost@unknow-mac", 80092, 120L, 123L, "detail");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getAppEvents() {
        //given
        String app = "fm";
        Range range = new Range(System.currentTimeMillis() - 60 * 60000, System.currentTimeMillis());

        //when
        when(activeEventDao.queryEvents(eq("app=fm"))).thenReturn(ImmutableList.of(fm_event1));
        when(historyEventDao.findEvents(eq("app=fm"), anyObject())).thenReturn(ImmutableList.of(newArrayList(fm_event2)));
        List<ResultEvent> events = eventService.getAppEvents(app, range);

        //then
        assertThat(events, is(ImmutableList.of(fm_event1, fm_event2)));
        events.forEach(System.out::println);
    }

    @Test
    public void getServiceEvents() {
        //given
        String app = "fm";
        String service = "fm_active";
        Range range = new Range(System.currentTimeMillis() - 60 * 60000, System.currentTimeMillis());

        //when
        when(activeEventDao.queryEvents("app=fm,service=fm_active")).thenReturn(ImmutableList.of(fmactive_event3));
        when(historyEventDao.findEvents(eq("app=fm,service=fm_active"), anyObject())).thenReturn(ImmutableList.of(newArrayList(fmactive_event4)));
        List<ResultEvent> events = eventService.getServiceEvents(app, service, range);

        //then
        assertThat(events, is(ImmutableList.of(fmactive_event3, fmactive_event4)));
        events.forEach(System.out::println);
    }

    @Test
    public void getInstanceEvents() throws Exception {
        //given
        String app = "fm";
        String service = "fm_active";
        String instance = "fm-active";
        Range range = new Range(System.currentTimeMillis() - 60 * 60000, System.currentTimeMillis());

        //when
        when(activeEventDao.queryEvents("app=fm,service=fm_active,instance=fm-active")).thenReturn(ImmutableList.of(fm_active_event5));
        when(historyEventDao.findEvents(eq("app=fm,service=fm_active,instance=fm-active"), anyObject())).thenReturn(ImmutableList.of(newArrayList(fm_active_event6)));
        List<ResultEvent> events = eventService.getInstanceEvents(app, service, instance, range);

        //then
        assertThat(events, is(ImmutableList.of(fm_active_event5, fm_active_event6)));
        events.forEach(System.out::println);
    }

    @Test
    public void getAppTransactionEvents() {
        //given
        String app = "fm";
        Range range = new Range(System.currentTimeMillis() - 60 * 60000, System.currentTimeMillis());

        //when
        when(tracesListService.getAppTracesList(eq("fm"), anyObject())).thenReturn(ImmutableList.of(name2));
        when(activeEventDao.queryEvents("app=fm,name=/favicon.ico")).thenReturn(ImmutableList.of(trace_event1));
        when(historyEventDao.findEvents(eq("app=fm,name=/favicon.ico"), anyObject())).thenReturn(ImmutableList.of(newArrayList(trace_event2)));
        List<ResultEvent> events = eventService.getAppTransactionEvents(app, range);

        //then
        assertThat(events, is(ImmutableList.of(trace_event1, trace_event2)));
        events.forEach(System.out::println);
    }

    @Test
    public void getServiceTransactionEvents() throws Exception {
        //given
        String app = "fm";
        String service = "fm_active";
        Range range = new Range(System.currentTimeMillis() - 60 * 60000, System.currentTimeMillis());

        //when
        when(tracesListService.getServiceTracesList(eq("fm_active"), anyObject())).thenReturn(ImmutableList.of(name1, name2));
        when(activeEventDao.queryEvents("app=fm,service=fm_active,name=/api/fm-active/v1/activealarms")).thenReturn(ImmutableList.of(trace_event3));
        when(historyEventDao.findEvents(eq("app=fm,service=fm_active,name=/api/fm-active/v1/activealarms"), anyObject())).thenReturn(ImmutableList.of(newArrayList(trace_event4)));
        List<ResultEvent> resultEvents = eventService.getServiceTransactionEvents(app, service, range);

        //then
        assertThat(resultEvents, is(ImmutableList.of(trace_event3, trace_event4)));
        resultEvents.forEach(System.out::println);
    }

    @Test
    public void getInstanceTransactionEvents() throws Exception {
        //given
        String app = "fm";
        String service = "fm_active";
        String instance = "fm-active";
        Range range = new Range(System.currentTimeMillis() - 60 * 60000, System.currentTimeMillis());

        //when
        when(tracesListService.getInstTracesList(eq("fm_active"), eq("fm-active"), anyObject())).thenReturn(ImmutableList.of(name3));
        when(activeEventDao.queryEvents("app=fm,service=fm_active,instance=fm-active,name=/api/fm-active/v1/activealarms")).thenReturn(ImmutableList.of(trace_event5));
        when(historyEventDao.findEvents(eq("app=fm,service=fm_active,instance=fm-active,name=/api/fm-active/v1/activealarms"), anyObject())).thenReturn(ImmutableList.of(newArrayList(trace_event6)));

        List<ResultEvent> resultEvents = eventService.getInstanceTransactionEvents(app, service, instance, range);

        //then
        assertThat(resultEvents, is(ImmutableList.of(trace_event5, trace_event6)));
        resultEvents.forEach(System.out::println);
    }

    @Test
    public void getHostEventTest() throws Exception{
        //given
        String hostId = "fakehost@unknow-mac";

        //when
        when(activeEventDao.queryEvents(anyString())).thenReturn(newArrayList(host1_event));
        when(historyEventDao.findEvents(anyString(), any(Range.class))).thenReturn(newArrayList(newArrayList(host1_event), newArrayList(host2_event)));

        List<ResultEvent> resultEvents = eventService.getHostEvents(hostId, new Range(10L, 1000L));

        assertEquals(resultEvents.size(), 3);
    }
}