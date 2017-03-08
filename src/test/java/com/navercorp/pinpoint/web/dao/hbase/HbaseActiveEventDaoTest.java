package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.ActiveEventDao;
import com.navercorp.pinpoint.web.mapper.EventsMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by root on 16-11-9.
 */
@RunWith(MockitoJUnitRunner.class)
public class HbaseActiveEventDaoTest {

    @Mock
    private HbaseOperations2 hbaseOperations2;

    @Mock
    private EventsMapper resultEventRowMapper;

    @InjectMocks
    private ActiveEventDao activeEventDao = new HbaseActiveEventDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void queryEvents() throws Exception {
        //given
        String dn = "app=fm";
        final ResultEvent expectEvent = new ResultEvent(dn, 10021, 1L, 0L, "detail:");

        when(this.hbaseOperations2.get(anyString(), eq(Bytes.toBytes(dn)), anyObject())).thenReturn(ImmutableList.of(expectEvent));

        //when
        List<ResultEvent> events = activeEventDao.queryEvents(dn);

        //then
        assertThat(events, is(ImmutableList.of(expectEvent)));
    }

}