package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.topo.domain.XRpc;
import com.navercorp.pinpoint.common.util.TimeSlot;
import com.navercorp.pinpoint.web.dao.RpcStatisticDao;
import com.navercorp.pinpoint.web.mapper.XRpcMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class HbaseRpcStatisticDaoTest {

    @Mock
    private HbaseOperations2 template2;

    @Mock
    private TimeSlot timeSlot;

    @Mock
    private XRpcMapper xRpcMapper;

    @InjectMocks
    private RpcStatisticDao rpcStatisticDao = new HbaseRpcStatisticDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getXRpcList() throws Exception {
        //given
        XRpc rpc = new XRpc("Get", 2, 2, 10L, 20L, 15L, "/getTimestamp");

        Mockito.when(timeSlot.getTimeSlot(anyLong())).thenReturn(110L);
        Mockito.when(template2.find(anyString(), any(Scan.class), any(RowMapper.class))).thenReturn(newArrayList(newArrayList(rpc), newArrayList(rpc)));

        List<XRpc> rpcs = rpcStatisticDao.getXRpcList("fm", new Range(100L, 200L));

        assertThat(rpcs.size(), is(2));
    }

}