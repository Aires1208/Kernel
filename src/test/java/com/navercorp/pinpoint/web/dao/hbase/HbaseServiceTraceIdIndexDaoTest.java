package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.util.TimeSlot;
import com.navercorp.pinpoint.web.dao.ServiceTraceIdIndexDao;
import com.navercorp.pinpoint.web.mapper.TraceIdIndexMapper;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by root on 16-11-14.
 */
public class HbaseServiceTraceIdIndexDaoTest {
    @Mock
    private HbaseOperations2 hbaseOperations2;

    @Mock
    private TimeSlot timeSlot;

    @Mock
    private TraceIdIndexMapper traceIdMapper;

    @InjectMocks
    private ServiceTraceIdIndexDao traceIdIndexDao = new HbaseServiceTraceIdIndexDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findServiceTransactionIds() throws Exception {
        //given
        Range range = new Range(System.currentTimeMillis() - 60000 * 60L, System.currentTimeMillis());
        TransactionId id1 = new TransactionId("fm-active", 12345677L, 1L);
        TransactionId id2 = new TransactionId("fm-active", 12345677L, 2L);


        //when
        when(this.hbaseOperations2.find(anyString(), any(Scan.class), any(RowMapper.class))).thenReturn(ImmutableList.of(newHashSet(id1, id2)));
        Set<TransactionId> transactionIds = traceIdIndexDao.findServiceTranceIds("fm_active", range);

        //then
        assertThat(transactionIds, is(ImmutableSet.of(id1, id2)));
    }

    @Test
    public void findTransactionIds() throws Exception {
        //given
        Range range = new Range(System.currentTimeMillis() - 60000 * 60L, System.currentTimeMillis());
        TransactionId id1 = new TransactionId("fm-active", 12345677L, 1L);
        TransactionId id2 = new TransactionId("fm-active", 12345677L, 2L);

        //when
        when(this.hbaseOperations2.get(anyString(), anyList(), any(RowMapper.class))).thenReturn(ImmutableList.of(newHashSet(id1, id2)));

        Set<TransactionId> transactionIds = traceIdIndexDao.findServiceTraceIdsByTraceName("fm_active", "/getTime", range);

        //then
        assertThat(transactionIds, is(ImmutableSet.of(id2, id1)));
    }

}