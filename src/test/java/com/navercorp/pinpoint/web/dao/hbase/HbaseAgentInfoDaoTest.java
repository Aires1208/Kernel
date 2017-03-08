package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.bo.AgentInfoBo;
import com.navercorp.pinpoint.common.hbase.HbaseTemplate2;
import com.navercorp.pinpoint.web.dao.AgentInfoDao;
import com.navercorp.pinpoint.web.mapper.AgentInfoMapper;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.hadoop.hbase.ResultsExtractor;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class HbaseAgentInfoDaoTest {

    @Mock
    private HbaseTemplate2 hbaseTemplate2;

    @Mock
    private AgentInfoMapper agentInfoMapper;

    @InjectMocks
    AgentInfoDao agentInfoDao = new HbaseAgentInfoDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_return_expectAgentInfo_when_given_fm() throws Exception {
        //given
        final String agentId = "fm";
        AgentInfoBo.Builder builder = new AgentInfoBo.Builder();
        builder.setAgentId(agentId);
        builder.setApplicationName("fm_active");
        builder.setHostName("localhost");
        final AgentInfo expectAgentInfo = new AgentInfo(builder.build());

        when(this.hbaseTemplate2.find(anyString(), any(Scan.class), any(ResultsExtractor.class))).thenReturn(expectAgentInfo);

        //when
        AgentInfo initialAgentInfo = agentInfoDao.getInitialAgentInfo(agentId);
        AgentInfo agentInfo = agentInfoDao.getAgentInfo(agentId, System.currentTimeMillis());

        //then
        assertThat(initialAgentInfo, is(expectAgentInfo));
        assertThat(agentInfo, is(expectAgentInfo));
    }


    @Test
    public void should_return_expect_agnetInfo_list_when_input_given_agnetIds() {
        //given
        final String agentId1 = "fm";
        final String agentId2 = "IaasOps";
        AgentInfoBo.Builder builder1 = new AgentInfoBo.Builder();
        builder1.setAgentId(agentId1);
        builder1.setApplicationName("fm_active");
        AgentInfoBo.Builder builder2 = new AgentInfoBo.Builder();
        builder2.setAgentId(agentId2);
        builder2.setApplicationName("IaasOps_fm");
        when(this.hbaseTemplate2.findParallel(anyString(), anyListOf(Scan.class), any(ResultsExtractor.class)))
                .thenReturn(ImmutableList.of(new AgentInfo(builder1.build()), new AgentInfo(builder2.build())));
        when(this.hbaseTemplate2.find(anyString(), anyListOf(Scan.class), any(ResultsExtractor.class)))
                .thenReturn(ImmutableList.of(new AgentInfo(builder1.build()), new AgentInfo(builder2.build())));

        //when
        List<AgentInfo> initialInfos = agentInfoDao.getInitialAgentInfos(ImmutableList.of(agentId1, agentId2));
        List<AgentInfo> agentInfos = agentInfoDao.getAgentInfos(ImmutableList.of(agentId1, agentId2), System.currentTimeMillis());

        //then
        assertThat(initialInfos, is(ImmutableList.of(new AgentInfo(builder1.build()), new AgentInfo(builder2.build()))));
        assertThat(agentInfos, is(ImmutableList.of(new AgentInfo(builder1.build()), new AgentInfo(builder2.build()))));
    }
}