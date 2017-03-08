package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.util.TimeUtils;
import com.navercorp.pinpoint.web.dao.HistoryEventDao;
import com.navercorp.pinpoint.web.mapper.EventsMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Repository
public class HbaseHistoryEventDao implements HistoryEventDao {
    private static final int ONE_MINUTE = 60000;

    @Autowired
    private HbaseOperations2 hbaseOperations2;

    @Autowired
    private EventsMapper eventsMapper;

    @Override
    public List<List<ResultEvent>> findEvents(String objDN, Range range) {
        if (objDN == null) {
            throw new NullPointerException("objDN must not be null");
        }

        Scan scan = CreateScan(objDN, range);
        List<List<ResultEvent>> eventList = hbaseOperations2.find(HBaseTables.HISTORY_EVENT, scan, eventsMapper);
        return eventList;
    }

    private Scan CreateScan(String objDN, Range range) {
        Scan scan = new Scan();
        scan.addFamily(HBaseTables.HISTORY_EVENT_CF_EVENT);
        scan.setStartRow(generateRowKey(objDN, range.getFrom() - ONE_MINUTE));
        scan.setStopRow(generateRowKey(objDN, range.getTo()));
        scan.setReversed(true);
        scan.setId("HistoryEventScan");
        return scan;
    }

    private static byte[] generateRowKey(String objDN, long timestamp) {
        long reverseTimestamp = TimeUtils.reverseTimeMillis(timestamp);
        return Bytes.toBytes(objDN + "^" + reverseTimestamp);
    }

}
