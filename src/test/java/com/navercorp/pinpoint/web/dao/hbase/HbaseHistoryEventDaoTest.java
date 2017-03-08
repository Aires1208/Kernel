package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.HistoryEventDao;
import com.navercorp.pinpoint.web.mapper.EventsMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


public class HbaseHistoryEventDaoTest {

    @Mock
    private HbaseOperations2 hbaseOperations2;

    @Mock
    private EventsMapper eventsMapper;

    @InjectMocks
    private HistoryEventDao eventDao = new HbaseHistoryEventDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findEvents() throws Exception {
        //given
        final String objDN = "MySql";
        ResultEvent event1 = new ResultEvent(objDN, 10022, 1L, 3L, "detail");
        ResultEvent event2 = new ResultEvent(objDN, 20121, 4L, 9L, "detail");
        List<List<ResultEvent>> expectEvents = ImmutableList.of(newArrayList(event1, event2));
        when(this.hbaseOperations2.find(anyString(), any(Scan.class), any(EventsMapper.class))).thenReturn(expectEvents);

        //when
        List<List<ResultEvent>> events = eventDao.findEvents(objDN, new Range(0L , 10L));

        //then
        assertThat(events, is(expectEvents));
    }

}