package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.topo.domain.XRpc;
import com.navercorp.pinpoint.common.util.RowKeyUtils;
import com.navercorp.pinpoint.common.util.TimeSlot;
import com.navercorp.pinpoint.web.dao.RpcStatisticDao;
import com.navercorp.pinpoint.web.mapper.XRpcMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Repository
public class HbaseRpcStatisticDao implements RpcStatisticDao {
    private static final int CACHESIZE = 256;
    @Autowired
    private HbaseOperations2 template2;

    @Autowired
    private TimeSlot timeSlot;

    @Autowired
    private XRpcMapper xRpcMapper;

    @Override
    public List<XRpc> getXRpcList(String appliactionName, Range range) {
        Preconditions.checkArgument(appliactionName != null, new NullPointerException("agentId must not be empty."));

        Scan scan = createScan(appliactionName, range);
        List<List<XRpc>> xRpcs = template2.find(HBaseTables.RPC_STATISTIC, scan, xRpcMapper);
        List<XRpc> xRpcList = newArrayList();
        xRpcs.forEach(xRpcList::addAll);
        return xRpcList;
    }


    private Scan createScan(String applicationName, Range range) {
        long startTimeSlot = timeSlot.getTimeSlot(range.getFrom());
        byte[] startRowKey = RowKeyUtils.createTimeSlotRowKey(applicationName, startTimeSlot);
        long endTimeSlot = timeSlot.getTimeSlot(range.getTo());
        byte[] endRowKey = RowKeyUtils.createTimeSlotRowKey(applicationName, endTimeSlot);

        Scan scan = new Scan();
        scan.setCaching(CACHESIZE);
        scan.setReversed(true);
        scan.setStartRow(startRowKey);
        scan.setStopRow(endRowKey);
        scan.setId("RpcStatisticScan");

        return scan;
    }
}
