package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.buffer.AutomaticBuffer;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.OffsetFixedBuffer;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-17.
 */
@Component
public class TransactionListMapper implements RowMapper<List<XTransactionName>> {
    @Override
    public List<XTransactionName> mapRow(Result result, int i) throws Exception {
        if (result.isEmpty()) {
            return null;
        }

        final Cell[] cells = result.rawCells();
        String serviceName = Bytes.toString(result.getRow());
        List<XTransactionName> transactionNames = newArrayList();

        for (Cell cell : cells) {
            String transactionName = Bytes.toString(cell.getQualifier());
            final Buffer buffer = new AutomaticBuffer(cell.getValue());
            long lastReportTime = buffer.readLong();
            String agentId = "";
            if (buffer.limit() > 1) {
                agentId = buffer.readPrefixedString();
            }

            transactionNames.add(new XTransactionName(serviceName, transactionName, lastReportTime, agentId));
        }

        return transactionNames;
    }
}
