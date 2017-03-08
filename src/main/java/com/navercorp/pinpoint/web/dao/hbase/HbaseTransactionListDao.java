package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.TransactionListDao;
import com.navercorp.pinpoint.web.mapper.TransactionListMapper;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-17.
 */
@Repository
public class HbaseTransactionListDao implements TransactionListDao {
    @Autowired
    private HbaseOperations2 template2;

    @Autowired
    private TransactionListMapper mapper;

    @Override
    public List<XTransactionName> scan() {
        Scan scan = new Scan();
        List<List<XTransactionName>> transactionLists = template2.find(HBaseTables.TRANSACTION_LIST, scan, mapper);

        List<XTransactionName> resultList = newArrayList();
        transactionLists.forEach(resultList::addAll);

        return resultList;
    }

    @Override
    public List<XTransactionName> getServiceTracesList(String serviceName, Range range) {
        final byte[] rowKey = Bytes.toBytes(serviceName);
        List<XTransactionName> xTransactionNames = template2.get(HBaseTables.TRANSACTION_LIST, rowKey, mapper);

        if (xTransactionNames == null || xTransactionNames.isEmpty()) {
            return newArrayList();
        }

        List<XTransactionName> traceList = newArrayList();
        traceList.addAll(xTransactionNames.stream().filter(trace -> range.isInRange(trace.getLastTime())).collect(Collectors.toList()));

        return traceList;
    }

}
