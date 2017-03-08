package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.util.RowKeyUtils;
import com.navercorp.pinpoint.common.util.TimeSlot;
import com.navercorp.pinpoint.web.dao.InstanceTraceIdIndexDao;
import com.navercorp.pinpoint.web.mapper.TraceIdIndexMapper;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionId;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by root on 16-11-14.
 */
@Repository
public class HbaseInstanceTraceIdIndexDao implements InstanceTraceIdIndexDao {
    private static final int CACHESIZE = 256;
    private static final int MINUTE_MS = 60000;

    @Autowired
    private HbaseOperations2 template2;

    @Autowired
    private TimeSlot timeSlot;

    @Autowired
    private TraceIdIndexMapper traceIdMapper;

    @Override
    public Set<TransactionId> findInstanceTransactionIds(String agentId, Range range) {
        Preconditions.checkArgument(agentId != null, new NullPointerException("agentId must not be empty."));

        Scan scan = createScan(agentId, range);

        List<Set<TransactionId>> transactionIds = template2.find(HBaseTables.INSTANCE_TRACEID_INDEX, scan, traceIdMapper);

        Set<TransactionId> transactionIdSet = newHashSet();
        transactionIds.forEach(transactionIdSet::addAll);

        return transactionIdSet;
    }

    private Scan createScan(String agentId, Range range) {
        long startTimeSlot = timeSlot.getTimeSlot(range.getFrom()) - 1;
        byte[] startRowkey = RowKeyUtils.createTimeSlotRowKey(agentId, startTimeSlot);
        long endTimeSlot = timeSlot.getTimeSlot(range.getTo()) + 1;
        byte[] endRowKey = RowKeyUtils.createTimeSlotRowKey(agentId, endTimeSlot);

        Scan scan = new Scan();
        scan.setCaching(CACHESIZE);
        scan.setReversed(true);
        scan.setStartRow(startRowkey);
        scan.setStopRow(endRowKey);
        scan.setId("InstanceTraceIdIndexScan");

        return scan;
    }

    @Override
    public Set<TransactionId> findTransactionIds(String agentId, String traceName, Range range) {
        Preconditions.checkArgument(agentId != null, new NullPointerException("agentId must not be empty."));
        Preconditions.checkArgument(traceName != null, new NullPointerException("traceName must not be empty."));

        List<Get> getList = createGetList(agentId, traceName, range);

        List<Set<TransactionId>> transactionIds = template2.get(HBaseTables.INSTANCE_TRACEID_INDEX, getList, traceIdMapper);

        Set<TransactionId> transactionIdSet = newHashSet();
        transactionIds.forEach(transactionIdSet::addAll);

        return transactionIdSet;
    }

    private List<Get> createGetList(String agentId, String traceName, Range range) {
        long startTimeSlot = timeSlot.getTimeSlot(range.getFrom() - MINUTE_MS);
        long endTimeSlot = timeSlot.getTimeSlot(range.getTo() + MINUTE_MS);
        List<Get> getList = newArrayList();
        while (startTimeSlot <= endTimeSlot) {
            byte[] rowKey = RowKeyUtils.createTimeSlotRowKey(agentId, startTimeSlot);
            Get get = new Get(rowKey);
            get.addColumn(HBaseTables.INSTANCE_TRACEID_CF_NAME, Bytes.toBytes(traceName));
            getList.add(get);
            startTimeSlot += MINUTE_MS;
        }
        return getList;
    }
}
