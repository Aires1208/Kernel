package com.navercorp.pinpoint.web.dao.hbase;


import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.vo.AgentStat;
import com.navercorp.pinpoint.web.vo.Range;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.hadoop.hbase.ResultsExtractor;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class HbaseAgentStatDaoTest {
    @Mock
    private HbaseOperations2 hbaseOperations2;

    @Mock
    private RowMapper<List<AgentStat>> agentStatMapper;

    @Mock
    private AbstractRowKeyDistributor rowKeyDistributor;

    @InjectMocks
    private AgentStatDao agentStatDao = new HbaseAgentStatDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAgentStat() {
        //given
        final String agnetId = "fm";
        AgentStat agentStat1 = new AgentStat(agnetId, 1111L);
        AgentStat agentStat2 = new AgentStat(agnetId, 2222L);
        List<AgentStat> expectAgentstats = ImmutableList.of(agentStat1, agentStat2);


        //when
        when(this.hbaseOperations2.find(anyString(), any(Scan.class), any(AbstractRowKeyDistributor.class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(expectAgentstats));
        List<AgentStat> agentstats = agentStatDao.getAgentStatList(agnetId, new Range(1L, 2222222L));


        //then
        assertThat(agentstats, is(expectAgentstats));
    }

    @Test
    public void testIsAgentStatExists() {
        //given
        final String agnetId = "fm";

        //when
        when(this.hbaseOperations2.find(anyString(), any(Scan.class), any(AbstractRowKeyDistributor.class), any(ResultsExtractor.class)))
                .thenReturn(true);

        boolean isAgentstatExists = agentStatDao.agentStatExists(agnetId, new Range(1L, 2L));

        //then
        assertTrue(isAgentstatExists);
    }
}
