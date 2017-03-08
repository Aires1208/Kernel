package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.bo.JvmInfoBo;
import com.navercorp.pinpoint.common.bo.ServerMetaDataBo;
import com.navercorp.pinpoint.web.view.XJVMDashBoard;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.AgentStat;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})
public class XJVMDashBoardServiceImplTest {
    @Autowired
    private XJVMDashBoardServiceImpl xjvmDashBoardService;

    @Before
    public void setUp() throws Exception {
        AgentStatService agentStatService = mock(AgentStatService.class);
        AgentInfoService agentInfoService = mock(AgentInfoService.class);

        ServerMetaDataBo.Builder builder = new ServerMetaDataBo.Builder();
        builder.serverInfo("apache-tomcat-7.0");
        builder.serviceInfos(newArrayList());
        builder.vmArgs(newArrayList("test1", "test2"));
        ServerMetaDataBo metaDataBo = builder.build();

        JvmInfoBo jvmInfo = new JvmInfoBo(0);

        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setAgentId("fm-active");
        agentInfo.setServerMetaData(metaDataBo);
        agentInfo.setJvmInfo(jvmInfo);

        when(agentInfoService.getAgentInfo(eq("fm-active"), anyLong())).thenReturn(agentInfo);

        AgentStat stat1 = new AgentStat("fm-active", 10500);
        stat1.setHeapMax(100000000000L);
        stat1.setHeapUsed(300000000L);
        stat1.setNonHeapMax(2222222222L);
        stat1.setJvmPoolPermGenUsed(234.2134);
        stat1.setGcOldTime(342L);
        stat1.setJvmCpuUsage(0.2343);
        stat1.setSystemCpuUsage(0.3344);
        stat1.setSampledNewCount(21);
        stat1.setSampledContinuationCount(12);
        stat1.setUnsampledContinuationCount(2);
        stat1.setUnsampledNewCount(23L);

        when(agentStatService.selectAgentStatList(eq("fm-active"), anyObject())).thenReturn(ImmutableList.of(stat1));

        xjvmDashBoardService.setAgentInfoService(agentInfoService);
        xjvmDashBoardService.setAgentStatService(agentStatService);
    }

    @Ignore
    @Test
    public void getXJVMDashBoardTest() throws JsonProcessingException {
        //given
        Range range = new Range(10000L, 20000L);
        String expectJvm = "{\"heapInfo\":{\"info\":\" \",\"time\":[\"70-01-01 08:00:10\"],\"max\":[95367],\"used\":[286],\"fgc\":[342]},\"permGen\":{\"info\":\" \",\"time\":[\"70-01-01 08:00:10\"],\"max\":[2119],\"used\":[0],\"fgc\":[342]},\"jvmSys\":{\"info\":\" \",\"time\":[\"70-01-01 08:00:10\"],\"jvm\":[23.43],\"sys\":[33.44]},\"tps\":{\"info\":\" \",\"time\":[\"70-01-01 08:00:10\"],\"sc\":[21],\"sn\":[12],\"uc\":[23],\"un\":[2]},\"vmargs\":\"test1;test2\",\"jvmVersion\":null,\"gcTypeName\":null}";

        //when
        XJVMDashBoard xjvmDashBoard = xjvmDashBoardService.getXJVMDashBoard("fm-active", range);
        String jvmDashboard = new ObjectMapper().writeValueAsString(xjvmDashBoard);

        //then
        assertThat(jvmDashboard, is(expectJvm));
    }
}
