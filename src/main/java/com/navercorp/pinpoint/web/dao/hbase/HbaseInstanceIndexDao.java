package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.util.BytesUtils;
import com.navercorp.pinpoint.common.util.RowKeyUtils;
import com.navercorp.pinpoint.common.util.TimeSlot;
import com.navercorp.pinpoint.common.util.TimeUtils;
import com.navercorp.pinpoint.web.dao.InstanceIndexDao;
import com.navercorp.pinpoint.web.mapper.TopoLineMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Repository
public class HbaseInstanceIndexDao implements InstanceIndexDao {
    private static final int CACHESIZE = 256;
    @Autowired
    private HbaseOperations2 template2;

    @Autowired
    private TopoLineMapper topoLineMapper;

    @Autowired
    private TimeSlot timeSlot;

    @Override
    public List<TopoLine> getTopoLineSet(String appName, Range range) {
        Range slotRange = createStatisticsRange(range);
        Scan scan = createScan(appName, slotRange);
        return template2.find(HBaseTables.INSTANCEINDEX, scan, topoLineMapper);
    }

    private Scan createScan(String appName, Range slotRange) {
        byte[] startKey = createRowKey(appName, slotRange.getFrom());
        byte[] endKey = createRowKey(appName, slotRange.getTo());

        Scan scan = new Scan();
        scan.setCaching(CACHESIZE);
        scan.setReversed(true);
        scan.setStartRow(startKey);
        scan.setStopRow(endKey);
        scan.setId("ServiceIndexScan");

        return scan;
    }

    private Range createStatisticsRange(Range range) {
        if (range == null) {
            throw new NullPointerException("range must not be null");
        }
        final long startTime = timeSlot.getTimeSlot(range.getFrom()) - 1;
        final long endTime = timeSlot.getTimeSlot(range.getTo()) + 1;
        return Range.createUncheckedRange(startTime, endTime);
    }

    private byte[] createRowKey(String appName, long rowTimeslot) {
        byte[] appnameKey = BytesUtils.toBytes(appName);
        long reverseStartTimestamp = TimeUtils.reverseTimeMillis(rowTimeslot);
        return RowKeyUtils.concatFixedByteAndLong(appnameKey, HBaseTables.AGENT_NAME_MAX_LEN, reverseStartTimestamp);
    }
}
