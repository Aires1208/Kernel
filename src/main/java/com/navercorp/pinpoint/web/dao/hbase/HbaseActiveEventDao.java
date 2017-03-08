package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.ActiveEventDao;
import com.navercorp.pinpoint.web.mapper.EventsMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Repository
public class HbaseActiveEventDao implements ActiveEventDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HbaseOperations2 hbaseOperations2;

    @Autowired
    private EventsMapper eventsMapper;

    @Override
    public List<ResultEvent> queryEvents(String objDN) {

        final byte[] rowKey = Bytes.toBytes(objDN);
        List<ResultEvent> events = this.hbaseOperations2.get(HBaseTables.ACTIVE_EVENT, rowKey, eventsMapper);
        logger.debug("read result event list: {}, objDN={}", events, objDN);
        return events.isEmpty() ? newArrayList() : events;
    }

}
