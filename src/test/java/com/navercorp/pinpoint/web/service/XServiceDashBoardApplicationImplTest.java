package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.common.bo.AgentInfoBo;
import com.navercorp.pinpoint.common.bo.AgentLifeCycleBo;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.util.AgentLifeCycleState;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.InstanceIndexDao;
import com.navercorp.pinpoint.web.dao.ServiceIndexDao;
import com.navercorp.pinpoint.web.view.XInstanceDashBoard;
import com.navercorp.pinpoint.web.view.XServiceDashBoard;
import com.navercorp.pinpoint.web.vo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XServiceDashBoardApplicationImplTest {

    private ResultEvent fmactive_event3 = new ResultEvent("app=fm, service=fm_active", 20050, 1467987897L, 0L, "error ratio too high normal");
    private ResultEvent fmactive_event4 = new ResultEvent("app=fm, service=fm_active", 20022, 14754565476L, 66666666666L, "calls over baseline critical");
    private ResultEvent fm_active_event5 = new ResultEvent("app=fm, service=fm_active, instance=fm-active", 30072, 1111117777L, 0L, "responseTime over baseline critical");
    private ResultEvent fm_active_event6 = new ResultEvent("app=fm, service=fm_active, instance=fm-active", 30031, 88888888111L, 224555222888822L, "errors heavy warning");

    @Mock
    private ServiceIndexDao serviceIndexDao;

    @Mock
    private AgentInfoService agentInfoService;

    @Mock
    private XEventService eventService;

    @Mock
    private XTracesListServiceImpl tracesListService;

    @Mock
    private ApplicationIndexDao applicationIndexDao;

    @Mock
    private InstanceIndexDao instanceIndexDao;

    @Mock
    private ServiceTypeRegistryService registryService;

    @InjectMocks
    private XServiceDetailServiceImpl xServiceDetailService = new XServiceDetailServiceImpl();

    @Before
    public void SetUp() throws Exception {
        AgentInfoBo.Builder active_builder = new AgentInfoBo.Builder();
        active_builder.setAgentId("fm-active");
        active_builder.setEndStatus(AgentLifeCycleState.RUNNING.getCode());
        active_builder.setHostName("test-host");
        active_builder.setApplicationName("fm_active");
        AgentInfo active_info = new AgentInfo(active_builder.build());
        active_info.setStatus(new AgentStatus(new AgentLifeCycleBo("fm-active", 1L, 2L, 234L, AgentLifeCycleState.RUNNING)));
        when(agentInfoService.getAgentsByApplicationName(anyString(), anyLong())).thenReturn(ImmutableSet.of(active_info));

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_return_expect_serviceDetail_when_input_is_fm() throws Exception {
        //given
        String exception = "{\"summary\":{\"appName\":\"fm_active\",\"healthRuleViolations\":2,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":1},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"calls\":\"0\",\"callsPerMin\":\"0.00\",\"responseTime\":\"0.00\",\"errorsPercent\":\"0.00\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\"},\"topo\":{\"nodes\":[{\"key\":\"user\",\"name\":\"user\",\"type\":\"USER\",\"tracked\":\"false\",\"instances\":[]},{\"key\":\"fm_active\",\"name\":\"fm_active\",\"type\":\"JAVA\",\"tracked\":\"false\",\"instances\":[]}],\"links\":[{\"from\":\"user\",\"to\":\"fm_active\",\"respondTime\":\"23451calls/123errors/0ms\"}]},\"loadInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[0,0,0,0,0,0,0,0,0,0,0,0]},\"respondInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]},\"errorInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[0,0,0,0,0,0,0,0,0,0,0,0]}}";
        List<TopoLine> topoLines = getTopoLines();
        Range range = new Range(123L, 343L);
        List<Application> applications = ImmutableList.of(new Application("fm_active", ServiceType.STAND_ALONE),
                new Application("fm_history", ServiceType.STAND_ALONE));

        //when
        when(serviceIndexDao.getTopoLineSet(anyString(), anyObject())).thenReturn(topoLines);
        when(applicationIndexDao.selectAllApplicationNames()).thenReturn(applications);
        when(applicationIndexDao.selectAgentIds(anyString())).thenReturn(newArrayList("fm-agent"));
        when(eventService.getServiceEvents(anyString(), anyString(), any(Range.class))).thenReturn(ImmutableList.of(fmactive_event3, fmactive_event4));

        //then
        XServiceDashBoard xServiceDashBoard = xServiceDetailService.getXServiceDashBoard("fm", "fm_active", range);
        String serviceDetail = new ObjectMapper().writeValueAsString(xServiceDashBoard);
        assertEquals(serviceDetail, exception);
    }

    @Test
    public void should_return_instance_detail_when_input_is_fm_active() throws Exception {

        //given
        String expection = "{\"summary\":{\"appName\":\"fm-active\",\"healthRuleViolations\":2,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"calls\":\"0\",\"callsPerMin\":\"0.00\",\"responseTime\":\"0.00\",\"errorsPercent\":\"0.00\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\",\"serverIp\":\"\",\"hostId\":\"@test-host\",\"pid\":\"0\",\"serviceType\":\"JAVA\",\"runIn\":\"Server\",\"agentId\":\"fm-active\",\"agentVersion\":\"\",\"startTime\":\"70-01-01 08:00\",\"status\":\"Running\"},\"topo\":{\"nodes\":[{\"key\":\"user\",\"name\":\"user\",\"type\":\"USER\",\"tracked\":\"false\",\"instances\":[]},{\"key\":\"fm-active\",\"name\":\"fm-active\",\"type\":\"JAVA\",\"metrics\":\"5213calls/234errors/0ms\",\"count\":1,\"tracked\":\"true\",\"instances\":[{\"id\":\"fm-active\",\"value\":\"fm-active\"}]}],\"links\":[{\"from\":\"user\",\"to\":\"fm-active\",\"respondTime\":\"2512calls/34errors/17ms\"}]},\"loadInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[0,0,0,0,0,0,0,0,0,0,0,0]},\"respondInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]},\"errorInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[0,0,0,0,0,0,0,0,0,0,0,0]}}";
        Range range = new Range(1230L, 1420L);
        List<TopoLine> instanceTopo = newArrayList(new TopoLine(newArrayList(
                new XNode("user", ServiceType.USER.getCode(), 223L, 22L, 2542L),
                new XNode("fm-active", ServiceType.STAND_ALONE.getCode(), 5123L, 234L, 5213L)),
                newArrayList(new XLink("user", "fm-active", 43421L, 34L, 2512L))));

        //when
        when(instanceIndexDao.getTopoLineSet(anyString(), any(Range.class))).thenReturn(instanceTopo);
        when(registryService.findServiceType(anyShort())).thenReturn(ServiceType.SPRING);
        when(agentInfoService.getAgentInfo(eq("fm-active"), anyLong())).thenReturn(getAgentInfo());
        when(eventService.getInstanceEvents(anyString(), anyString(), anyString(), any(Range.class))).thenReturn(ImmutableList.of(fm_active_event5, fm_active_event6));

        //then
        XInstanceDashBoard instanceDashBoard = xServiceDetailService.getXInstanceDetail("fm", "fm_active", "fm-active", range);
        String instancedetail = new ObjectMapper().writeValueAsString(instanceDashBoard);

        assertThat(expection, is(instancedetail));

    }

    private AgentInfo getAgentInfo() {
        AgentInfoBo.Builder inst_builder = new AgentInfoBo.Builder();
        inst_builder.setAgentId("fm-active");
        inst_builder.setEndStatus(AgentLifeCycleState.RUNNING.getCode());
        inst_builder.setHostName("test-host");
        inst_builder.setApplicationName("fm_active");
        inst_builder.setServiceTypeCode(ServiceType.SPRING.getCode());
        AgentInfo inst_info = new AgentInfo(inst_builder.build());
        inst_info.setStatus(new AgentStatus(new AgentLifeCycleBo("fm-active", 1L, 2L, 234L, AgentLifeCycleState.RUNNING)));

        return inst_info;
    }

    private List<TopoLine> getTopoLines() {
        return newArrayList(new TopoLine(newArrayList(
                new XNode("user", ServiceType.USER.getCode(), 111L, 2L, 2222L),
                new XNode("fm_active", ServiceType.STAND_ALONE.getCode(), 5555L, 11L, 5443L)),
                newArrayList(new XLink("user", "fm_active", 4321L, 123L, 23451L))));
    }
}