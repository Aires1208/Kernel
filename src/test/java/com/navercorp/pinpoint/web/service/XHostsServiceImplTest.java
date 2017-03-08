package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.view.HostStat;
import com.navercorp.pinpoint.web.view.StatLine;
import com.navercorp.pinpoint.web.view.XHostList;
import com.navercorp.pinpoint.web.view.XHostsDashBoard;
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
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XHostsServiceImplTest {
    @Mock
    ApplicationIndexDao applicationIndexDao;

    @Mock
    AgentInfoService agentInfoService;

    @Mock
    private AgentStatDao agentStatDao;

    @Mock
    private XEventService eventService;

    @InjectMocks
    private XHostsService xHostsService = new XHostsServiceImpl();

    private Application app = new Application("fm_active", ServiceType.TEST);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getServersStatTest() {
        //given
        StatLine statLine = new StatLine(0.2154, 0.5, 0.4512,0.21 );
        HostStat.Builder builder = HostStat.Builder();
        HostStat hostStat = builder.hostId("02:42:d6:22:3a:f4@test-host")
                .ipAddr("127.0.0.1")
                .osType("x86_64 x86_64 x86_64 GNU/Linux")
                .health("CRITICAL")
                .services(newHashSet("fm_active"))
                .statLines(newHashSet(statLine)).build();
        List<HostStat> expectStat = newArrayList(hostStat);

        //when
        when(agentStatDao.agentStatExists(anyString(), any(Range.class))).thenReturn(true);
        when(agentStatDao.getAgentStatList(anyString(), any(Range.class))).thenReturn(ImmutableList.of(getStat()));
        when(applicationIndexDao.selectAllApplicationNames()).thenReturn(ImmutableList.of(app));
        when(applicationIndexDao.selectAgentIds(anyString())).thenReturn(ImmutableList.of("fm-active"));
        when(agentInfoService.getAgentInfo(anyString(), anyLong())).thenReturn(getAgentInfo());
        when(eventService.getHostEvents(anyString(), any(Range.class)))
                .thenReturn(newArrayList(new ResultEvent("fakehost", 70092, 222L, 222L, "detail")));

        //then
        List<HostStat> hostStatList = xHostsService.getHostsDashBoard(new Range(100L, 200L));

        assertThat(hostStatList, is(expectStat));
        assertThat(hostStat.getCpu(), is(statLine.getCpuUsage()));
        assertThat(hostStat.getDisk(), is(statLine.getDiskUsage()));
        assertThat(hostStat.getMem(), is(statLine.getMemUsage()));
        assertThat(hostStat.getNet(), is(statLine.getNetUsage()));
    }

    private AgentInfo getAgentInfo() {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setAgentId("fm-active");
        agentInfo.setApplicationName("fm_active");
        agentInfo.setHostName("test-host");
        agentInfo.setMac("02:42:d6:22:3a:f4");
        agentInfo.setIp("127.0.0.1");
        agentInfo.setOs("x86_64 x86_64 x86_64 GNU/Linux");

        return agentInfo;
    }

    private AgentStat getStat() {
        AgentStat stat = new AgentStat("fm-active", 10050L);
        stat.setSystemCpuUsage(0.2154);
        stat.setMemUsed(100000L);
        stat.setMemTotal(200000L);
        stat.setDiskUsage(0.4512);
        stat.setInSpeed(12 * 1024L);
        stat.setOutSpeed(9 * 1024L);
        stat.setSpeed(100L * 1024 * 1024);

        return stat;
    }


    @Test
    public void getServerListTest() throws JsonProcessingException {
        //given
        String expectJson = "{\"serverlist\":[{\"fullname\":\"02:42:d6:22:3a:f4@test-host\",\"simplifiedname\":\"02:42:d6:22:3a:f4@test-host\"}]}";

        //when
        when(agentStatDao.agentStatExists(anyString(), any(Range.class))).thenReturn(true);
        when(applicationIndexDao.selectAllApplicationNames()).thenReturn(ImmutableList.of(app));
        when(applicationIndexDao.selectAgentIds(anyString())).thenReturn(ImmutableList.of("fm-active"));
        when(agentInfoService.getAgentInfo(anyString(), anyLong())).thenReturn(getAgentInfo());


        //then
        Set<XHost> serverIds = xHostsService.getXHosts();
        XHostList serverList = new XHostList(serverIds);
        String serverListJson = new ObjectMapper().writeValueAsString(serverList);

        System.out.println(serverListJson);
        assertThat(serverListJson, is(expectJson));
    }
}
