package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.view.XHostDashBoard;
import com.navercorp.pinpoint.web.vo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XHostServiceImplTest {
    @Mock
    private XHostsService xHostsService;

    @Mock
    private AgentStatDao agentStatDao;

    @Mock
    private XEventService eventService;

    @InjectMocks
    private XHostService xHostService = new XHostServiceImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private List<AgentStat> getAgentStat() {
        AgentStat agentStat1 = new AgentStat("fm_active", 123456788L);
        agentStat1.setSystemCpuUsage(0.24591);
        agentStat1.setMemTotal(10000);
        agentStat1.setMemUsed(7542);
        agentStat1.setDiskUsage(0.4828);
        agentStat1.setSpeed(1000000);
        agentStat1.setInSpeed(235);
        agentStat1.setOutSpeed(8746);

        AgentStat agentStat2 = new AgentStat("fm_active", 123461788L);
        agentStat2.setSystemCpuUsage(0.23256);
        agentStat2.setMemTotal(10000);
        agentStat2.setMemUsed(7042);
        agentStat2.setDiskUsage(0.4826);
        agentStat2.setSpeed(1000000);
        agentStat2.setInSpeed(455);
        agentStat2.setOutSpeed(801);
        return newArrayList(agentStat1, agentStat2);
    }

    private XHost getHost() {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setAgentId("fm-active");
        agentInfo.setApplicationName("fm_active");
        Set<AgentInfo> agentIds = ImmutableSet.of(agentInfo);
        String hostName = "Esight-80";
        String mac = "52-54-00-48-1E-5B";
        String ipAddr = "10.62.100.80";
        String osType = "Linux : amd64 : 3.10.0-229.el7.x86_64";
        return XHost.Builder().agents(agentIds).hostname(hostName).ipAddr(ipAddr).mac(mac).osType(osType).build();
    }

    @Test
    public void getServerInfosTest() throws JsonProcessingException {
        //given
        Range range = new Range(1484864411135L, 1484882411135L);

        String expect = "{\"summary\":{\"hostId\":\"52-54-00-48-1E-5B@Esight-80\",\"ip\":\"10.62.100.80\",\"os\":\"Linux : amd64 : 3.10.0-229.el7.x86_64\",\"health\":\"Critical\"},\"cpuInfo\":{\"info\":\"\",\"time\":[\"17-01-20 06:35\",\"17-01-20 07:05\",\"17-01-20 07:35\",\"17-01-20 08:05\",\"17-01-20 08:35\",\"17-01-20 09:05\",\"17-01-20 09:35\",\"17-01-20 10:05\",\"17-01-20 10:35\",\"17-01-20 11:05\"],\"data\":[\"23.92\",\"23.92\",\"23.92\",\"23.92\",\"23.92\",\"23.92\",\"23.92\",\"23.92\",\"23.92\",\"23.92\"]},\"memInfo\":{\"info\":\"\",\"time\":[\"17-01-20 06:35\",\"17-01-20 07:05\",\"17-01-20 07:35\",\"17-01-20 08:05\",\"17-01-20 08:35\",\"17-01-20 09:05\",\"17-01-20 09:35\",\"17-01-20 10:05\",\"17-01-20 10:35\",\"17-01-20 11:05\"],\"data\":[\"72.92\",\"72.92\",\"72.92\",\"72.92\",\"72.92\",\"72.92\",\"72.92\",\"72.92\",\"72.92\",\"72.92\"]},\"diskInfo\":{\"info\":\"\",\"time\":[\"17-01-20 06:35\",\"17-01-20 07:05\",\"17-01-20 07:35\",\"17-01-20 08:05\",\"17-01-20 08:35\",\"17-01-20 09:05\",\"17-01-20 09:35\",\"17-01-20 10:05\",\"17-01-20 10:35\",\"17-01-20 11:05\"],\"data\":[\"48.27\",\"48.27\",\"48.27\",\"48.27\",\"48.27\",\"48.27\",\"48.27\",\"48.27\",\"48.27\",\"48.27\"]},\"netInfo\":{\"info\":\"\",\"time\":[\"17-01-20 06:35\",\"17-01-20 07:05\",\"17-01-20 07:35\",\"17-01-20 08:05\",\"17-01-20 08:35\",\"17-01-20 09:05\",\"17-01-20 09:35\",\"17-01-20 10:05\",\"17-01-20 10:35\",\"17-01-20 11:05\"],\"Dl\":[\"345.00\",\"345.00\",\"345.00\",\"345.00\",\"345.00\",\"345.00\",\"345.00\",\"345.00\",\"345.00\",\"345.00\"],\"Ul\":[\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\",\"4773.50\"]}}";

        //when
        when(xHostsService.getXHosts()).thenReturn(ImmutableSet.of(getHost()));
        when(agentStatDao.agentStatExists(anyString(), anyObject())).thenReturn(true);
        when(agentStatDao.getAgentStatList(anyString(), any())).thenReturn(getAgentStat());
        when(eventService.getHostEvents(anyString(), any(Range.class)))
                .thenReturn(newArrayList(new ResultEvent("fakehost", 70092, 222L, 222L, "detail")));

        //then
        XHostDetail xHostDetail = xHostService.getHostDetail(getHost().getHostId(), range);
        XHostDashBoard xHostDashBoard = new XHostDashBoard(xHostDetail);
        String dashboardJson = new ObjectMapper().writeValueAsString(xHostDashBoard);

        assertEquals(dashboardJson, expect);
    }
}
