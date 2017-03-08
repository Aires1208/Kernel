package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.mapper.TraceIndexScatterMapper2;
import com.navercorp.pinpoint.web.mapper.TraceIndexScatterMapper3;
import com.navercorp.pinpoint.web.scatter.ScatterData;
import com.navercorp.pinpoint.web.vo.LimitedScanResult;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.SelectedScatterArea;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.scatter.Dot;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class HbaseApplicationTraceIndexDaoTest {
    @Mock
    private HbaseOperations2 hbaseOperations2;

    @Mock
    private RowMapper<List<TransactionId>> traceIndexMapper;

    @Mock
    private AbstractRowKeyDistributor traceIdRowKeyDistributor;

    @InjectMocks
    private ApplicationTraceIndexDao applicationTraceIndexDao = new HbaseApplicationTraceIndexDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private TransactionId id1 = new TransactionId("fm-active", 12345667L, 1L);
    private TransactionId id2 = new TransactionId("fm-active", 12345667L, 2L);
    private TransactionId id3 = new TransactionId("fm-active", 12345667L, 3L);
    private TransactionId id4 = new TransactionId("fm-active", 12345667L, 4L);
    private TransactionId id5 = new TransactionId("fm-active", 12345667L, 5L);

    @Test
    public void testScanTraceIndex() throws Exception {
        //given
        Range range = new Range(1470614874498L, 1470615385300L);

        //when
        when(this.hbaseOperations2.findParallel(anyString(), any(Scan.class), any(AbstractRowKeyDistributor.class), anyInt(), any(RowMapper.class), any(), anyInt()))
                .thenReturn(ImmutableList.of(newArrayList(id1, id2, id3, id4, id5)));

        LimitedScanResult<List<TransactionId>> scanTraceIdList = applicationTraceIndexDao.scanTraceIndex("fm_active", range, 3, false);
        List<TransactionId> transactionIdList = scanTraceIdList.getScanData();

        //then
        assertThat(transactionIdList, is(ImmutableList.of(id1, id2, id3, id4, id5)));
    }

    @Test
    public void testScanTraceIndex_with_given_area() throws Exception {
        //given
        SelectedScatterArea area = new SelectedScatterArea(1470614874498L, 1470615385300L, 1, 100000);

        //when
        when(this.hbaseOperations2.findParallel(anyString(), any(Scan.class), any(AbstractRowKeyDistributor.class), anyInt(), any(RowMapper.class), any(), anyInt()))
                .thenReturn(ImmutableList.of(newArrayList(id1, id2, id3, id4, id5)));

        LimitedScanResult<List<TransactionId>> scanResult = applicationTraceIndexDao.scanTraceIndex("fm_active", area, 1);

        //then
        assertThat(scanResult.getScanData(), is(ImmutableList.of(id1, id2, id3, id4, id5)));
    }


    private Dot dot1 = new Dot(id1, 12345699L, 23, 0, "fm-active");
    private Dot dot2 = new Dot(id2, 12345712L, 18, 0, "fm-active");
    private Dot dot3 = new Dot(id3, 12345822L, 124, 0, "fm-active");

    @Test
    public void scanTraceScatter() throws Exception {
        //given
        SelectedScatterArea area = new SelectedScatterArea(12345600L, 12346000L, 1, 200);

        //when
        when(this.hbaseOperations2.findParallel(anyString(), any(Scan.class), any(AbstractRowKeyDistributor.class), anyInt(), any(TraceIndexScatterMapper2.class), anyInt())).
                thenReturn(ImmutableList.of(newArrayList(dot1, dot2, dot3)));
        List<Dot> dots = applicationTraceIndexDao.scanTraceScatter("fm_active", area, id2, 200, 3);

        //then
        assertThat(dots, is(ImmutableList.of(dot1, dot2, dot3)));
    }

    @Test
    public void scanTraceScatterData() throws Exception {
        //given
        Range range = new Range(12345000L, 12346000L);
        ScatterData scatterData1 = new ScatterData(12345600L, 12345610L, 1, 5);
        ScatterData scatterData2 = new ScatterData(12345690L, 12345700L, 1, 5);

        //when
        when(this.hbaseOperations2.findParallel(anyString(), any(Scan.class), any(AbstractRowKeyDistributor.class), anyInt(), any(TraceIndexScatterMapper3.class), anyInt()))
                .thenReturn(ImmutableList.of(scatterData1, scatterData2));

        ScatterData scatterData = applicationTraceIndexDao.scanTraceScatterData("fm_active", range, 1, 10, 5, false);

        //then
        assertThat(scatterData, is(scatterData1));
    }


}