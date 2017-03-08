package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.common.bo.AgentInfoBo;
import com.navercorp.pinpoint.common.bo.AgentLifeCycleBo;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.util.AgentLifeCycleState;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.ServiceIndexDao;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XApplicationsDashBoard;
import com.navercorp.pinpoint.web.vo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by 10170261 on 16-6-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class XApplicationServiceImplTest {

    @Mock
    private ApplicationIndexDao applicationIndexDao;

    @Mock
    private ServiceIndexDao serviceIndexDao;

    @Mock
    private AgentInfoService agentInfoService;

    @Mock
    private XEventService eventService;

    @Mock
    private XTracesListServiceImpl tracesListService;

    @InjectMocks
    private XApplicationsService xApplicationsService = new XApplicationsServiceImpl();

    private List<Application> applications = ImmutableList.of(
            new Application("fm_active", ServiceType.TEST),
            new Application("fm_history", ServiceType.STAND_ALONE));

    @Before
    public void SetUp() throws Exception {

        when(applicationIndexDao.selectAllApplicationNames()).thenReturn(applications);

        List<TopoLine> topoLines = ImmutableList.of(new TopoLine(
                newArrayList(new XNode("user", ServiceType.USER.getCode(), 0L, 0L, 0L),
                        new XNode("fm_active", ServiceType.STAND_ALONE.getCode(), 1456L, 2L, 135L),
                        new XNode("fm_history", ServiceType.STAND_ALONE.getCode(), 7763L, 34L, 56L),
                        new XNode("uep_4x", ServiceType.UNKNOWN_DB.getCode(), 0L, 34L, 224L)),
                newArrayList(new XLink("user", "fm_active", 1456L, 2L, 135L),
                        new XLink("user", "fm_history", 7763L, 34L, 56L),
                        new XLink("user", "uep_4x", 0L, 234L, 224L))));
        when(serviceIndexDao.getTopoLineSet(eq("fm"), anyObject())).thenReturn(topoLines);


        AgentInfoBo.Builder active_builder = new AgentInfoBo.Builder();
        active_builder.setAgentId("fm-active");
        active_builder.setEndStatus(AgentLifeCycleState.RUNNING.getCode());
        active_builder.setHostName("test-host");
        active_builder.setApplicationName("fm_active");
        AgentInfo active_info = new AgentInfo(active_builder.build());
        active_info.setStatus(new AgentStatus(new AgentLifeCycleBo("fm-active", 1L, 2L, 234L, AgentLifeCycleState.RUNNING)));

        AgentInfoBo.Builder history_builder = new AgentInfoBo.Builder();
        history_builder.setAgentId("fm-history");
        history_builder.setEndStatus(AgentLifeCycleState.RUNNING.getCode());
        history_builder.setHostName("test-host");
        history_builder.setApplicationName("fm_history");
        AgentInfo history_info = new AgentInfo(history_builder.build());
        history_info.setStatus(new AgentStatus(new AgentLifeCycleBo("fm-history", 123L, 345L, 345L, AgentLifeCycleState.RUNNING)));


        when(agentInfoService.getAgentsByApplicationName(eq("fm_active"), anyLong())).thenReturn(ImmutableSet.of(active_info));
        when(agentInfoService.getAgentsByApplicationName(eq("fm_history"), anyLong())).thenReturn(ImmutableSet.of(history_info));

        ResultEvent fmhistory_event1 = new ResultEvent("app=fm, service=fm_history", 20050, 1467987897L, 0L, "error ratio too high normal");
        ResultEvent fmhistory_event2 = new ResultEvent("app=fm, service=fm_history", 20022, 14754565476L, 66666666666L, "calls over baseline critical");
        ResultEvent fmactive_event3 = new ResultEvent("app=fm, service=fm_active", 20050, 1467987897L, 0L, "error ratio too high normal");
        ResultEvent fmactive_event4 = new ResultEvent("app=fm, service=fm_active", 20022, 14754565476L, 66666666666L, "calls over baseline critical");
        ResultEvent trace_event5 = new ResultEvent("app=fm, name=/favicon.ico", 40012, 1453453L, 0L, "calls heavy critical");
        ResultEvent trace_event6 = new ResultEvent("app=fm, name=/favicon.ico", 40071, 123445665245L, 2224325456892L, "rulename:appresponsetime;ruledetail:responsetime>10s in last10min");

        when(eventService.getServiceEvents(eq("fm"), anyString(), anyObject())).thenReturn(ImmutableList.of(fmhistory_event1, fmhistory_event2, fmactive_event3, fmactive_event4));
        when(eventService.getAppTransactionEvents(eq("fm"), anyObject())).thenReturn(ImmutableList.of(trace_event5, trace_event6));

        XTransactionName trace1 = new XTransactionName("fm_active", "/favicon.ico", 1477115974114L, "fm-active");
        XTransactionName trace2 = new XTransactionName("fm_history", "/api/fm-history/v1/basicstatistics", 1477115953154L, "fm_history");
        when(tracesListService.getAppTracesList(eq("fm"), anyObject())).thenReturn(ImmutableList.of(trace1, trace2));

    }

    @Test
    public void testGetXServices() throws Exception {
        //given
        String expectKey = "fm";
        List<XService> expectXServices = ImmutableList.of(new XService("fm_active", ServiceType.TEST),
                new XService("fm_history", ServiceType.STAND_ALONE));

        //when
        List<XService> xServices = xApplicationsService.getXServices(expectKey);

        //then
        assertThat(xServices, is(expectXServices));
    }

    @Test
    public void testGetApplicationDashBoard() throws IOException {
        //given
        String expect = "{\"summary\":[{\"appName\":\"fm\",\"healthRuleViolations\":2,\"nodeHealth\":{\"critical\":2,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":1,\"normal\":1},\"calls\":\"415\",\"callsPerMin\":\"0.00\",\"responseTime\":\"22.21\",\"errorsPercent\":\"16.87%\",\"errors\":\"70\",\"errorsPerMin\":\"0.00\"}]}";
        Range range = new Range(1L, 3464664137000L);

        //when
        XApplicationsDashBoard XApplicationsDashBoard = xApplicationsService.getXApplicationsDashBoard(range);
        String applicationsJson = new ObjectMapper().writeValueAsString(XApplicationsDashBoard);

        //then
        assertThat(applicationsJson, is(expect));
    }

    @Test
    public void testgetFullAppList() {
        //given

        //when
        List<XApplication> appList = xApplicationsService.getFullAppList();

        //then
        appList.forEach(System.out::println);

    }
}
