package com.navercorp.pinpoint.web.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.common.bo.AgentInfoBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.thrift.io.HeaderTBaseSerializer;
import com.navercorp.pinpoint.thrift.io.SerializerFactory;
import com.navercorp.pinpoint.web.cluster.ClusterManager;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgentServiceImplTest {
    @Mock
    private AgentInfoService agentInfoService;

    @Mock
    private ClusterManager clusterManager;

    @Mock
    private SerializerFactory<HeaderTBaseSerializer> commandSerializerFactory;

    @InjectMocks
    private AgentService agentService = new AgentServiceImpl();

    private AgentInfoBo createAgentInfo(String appName, String agentId, long startTime) {
        AgentInfoBo.Builder builder = new AgentInfoBo.Builder();
        builder.setApplicationName(appName);
        builder.setAgentId(agentId);
        builder.setStartTime(startTime);
        builder.setServiceTypeCode(ServiceType.TEST.getCode());

        return builder.build();
    }

    @Test
    public void should_return_expect_agentInfo_when_query_by_appName_and_agentId() throws Exception {
        //given
        final String appName = "fm_active";
        final String agentId = "fm-active";
        final long timestamp = 123456778L;

        AgentInfo agentInfo1 = new AgentInfo(createAgentInfo(appName, agentId, timestamp));

        //when
        when(this.agentInfoService.getAgentsByApplicationName(anyString(), anyShort()))
                .thenReturn(ImmutableSet.of(agentInfo1));

        //then
        AgentInfo agentInfo = agentService.getAgentInfo(appName, agentId);

        assertThat(agentInfo, is(agentInfo1));
    }

    @Test
    public void shoul_return_expect_agentInfo_when_query_with_timestamp() throws Exception {
        //given
        final String appName = "fm_active";
        final String agentId = "fm-active";
        final long timestamp = 123456778L;

        AgentInfo agentInfo1 = new AgentInfo(createAgentInfo(appName, agentId, timestamp));

        //when
        when(this.agentInfoService.getAgentsByApplicationName(anyString(), anyShort()))
                .thenReturn(ImmutableSet.of(agentInfo1));

        //then
        AgentInfo agentInfo = agentService.getAgentInfo(appName, agentId, timestamp);

        assertThat(agentInfo, is(agentInfo1));
        assertEquals(agentInfo.getServiceTypeCode(), 0);
    }

    @Test
    public void should_return_new_AgentInfo_when_query_check_flag_is_true() throws Exception {
        //given
        final String appName = "fm_active";
        final String agentId = "fm-active";
        final long timestamp = 123456778L;

        AgentInfo agentInfo1 = new AgentInfo(createAgentInfo(appName, agentId, timestamp));

        //when
        when(this.agentInfoService.getAgentsByApplicationName(anyString(), anyShort()))
                .thenReturn(ImmutableSet.of(agentInfo1));

        //then
        AgentInfo agentInfo = agentService.getAgentInfo(appName, agentId, timestamp, false);

        assertThat(agentInfo, is(agentInfo1));
        assertEquals(agentInfo.getServiceTypeCode(), 0);
    }

    @Test
    public void getRecentAgentInfoList() throws Exception {
        //given
        final String appName = "fm_active";
        final String agentId = "fm-active";
        AgentInfo agentInfo = new AgentInfo(createAgentInfo(appName, agentId, 123456778L));

        //when
        when(agentInfoService.getRecentAgentsByApplicationName(anyString(), anyLong(), anyLong())).thenReturn(ImmutableSet.of(agentInfo));
        List<AgentInfo> agentInfoList = agentService.getRecentAgentInfoList(appName);

        //then
        assertThat(agentInfoList, is(ImmutableList.of(agentInfo)));

    }

}