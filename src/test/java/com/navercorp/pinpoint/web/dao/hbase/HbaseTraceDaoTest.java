package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HbaseTraceDaoTest {
    @Mock
    private HbaseOperations2 template2;

    @Mock
    private AbstractRowKeyDistributor rowKeyDistributor;

    @Mock
    private RowMapper<List<SpanBo>> spanMapper;

    @Mock
    private RowMapper<List<SpanBo>> spanAnnotationMapper;

    @InjectMocks
    private TraceDao traceDao = new HbaseTraceDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private SpanBo spanBo1 = new SpanBo("fm-active", 12345600L, 2L, 12345677L, 234, 11111L);
    private SpanBo spanBo2 = new SpanBo("fm-active", 12345600L, 2L, 12346113L, 223, 5465788L);


    @Test
    public void selectSpan() throws Exception {
        //given
        TransactionId id = new TransactionId("fm-active", 12345677L, 1L);

        //when
        when(this.template2.get(anyString(), any(byte[].class), any(byte[].class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(spanBo1, spanBo2));

        List<SpanBo> spanBoList = traceDao.selectSpan(id);

        //then
        assertThat(spanBoList, is(ImmutableList.of(spanBo1, spanBo2)));
    }

    @Test
    public void selectSpanAndAnnotation() throws Exception {
        //given
        TransactionId id = new TransactionId("fm-active", 12345677L, 1L);

        //when
        when(rowKeyDistributor.getDistributedKey(any(byte[].class))).thenReturn(Bytes.toBytes("test"));
        when(this.template2.get(anyString(), any(Get.class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(spanBo1, spanBo2));

        List<SpanBo> spanBoList = traceDao.selectSpanAndAnnotation(id);

        //then
        assertThat(spanBoList, is(ImmutableList.of(spanBo1, spanBo2)));
    }

    @Test
    public void selectSpans() throws Exception {
        //given
        TransactionId id1 = new TransactionId("fm-active", 12345677L, 1L);
        TransactionId id2 = new TransactionId("fm-active", 12345677L, 2L);

        //when
        Field selectSpansLimit = traceDao.getClass().getDeclaredField("selectSpansLimit");
        selectSpansLimit.setAccessible(true);
        selectSpansLimit.setInt(traceDao, 2);
        when(template2.get(anyString(), anyList(), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(newArrayList(spanBo1, spanBo2)));
        when(rowKeyDistributor.getDistributedKey(any(byte[].class))).thenReturn(Bytes.toBytes("12"));
        List<List<SpanBo>> spanList = traceDao.selectSpans(newArrayList(id1, id2));

        //then
        assertThat(spanList, is(ImmutableList.of(newArrayList(spanBo1, spanBo2))));
    }

    @Test
    public void selectAllSpans() throws Exception {
        //given
        TransactionId id1 = new TransactionId("fm-active", 12345677L, 1L);
        TransactionId id2 = new TransactionId("fm-active", 12345677L, 2L);

        //when
        Field selectAllSpansLimit = traceDao.getClass().getDeclaredField("selectAllSpansLimit");
        selectAllSpansLimit.setAccessible(true);
        selectAllSpansLimit.setInt(traceDao, 1);
        when(template2.get(anyString(), anyList(), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(newArrayList(spanBo1, spanBo2)));

        when(rowKeyDistributor.getDistributedKey(any(byte[].class))).thenReturn(Bytes.toBytes("12"));

        List<List<SpanBo>> spanList = traceDao.selectAllSpans(newArrayList(id1, id2));

        //then
        assertThat(spanList, is(ImmutableList.of(newArrayList(spanBo1, spanBo2), newArrayList(spanBo1, spanBo2))));
    }

}