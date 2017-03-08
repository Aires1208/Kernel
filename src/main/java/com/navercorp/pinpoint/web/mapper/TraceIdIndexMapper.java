package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.OffsetFixedBuffer;
import com.navercorp.pinpoint.common.util.TransactionIdUtils;
import com.navercorp.pinpoint.web.util.TraceUtils;
import com.navercorp.pinpoint.web.vo.TransactionId;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by root on 16-11-14.
 */
@Component
public class TraceIdIndexMapper implements RowMapper<Set<TransactionId>>{
    @Override
    public Set<TransactionId> mapRow(Result result, int i) throws Exception {
        if (result.isEmpty()) {
            return newHashSet();
        }

        final Cell[] cells = result.rawCells();
        Set<TransactionId> transactionIds = newHashSet();

        for (Cell cell : cells) {
            Buffer buffer = new OffsetFixedBuffer(cell.getValueArray(), cell.getValueOffset());
            byte version = buffer.readByte();
            if (version == 0) {
                final int size = buffer.readInt();
                int index = 0;
                while (index < size) {
                    transactionIds.add(TraceUtils.parseTransactionId(buffer.read2PrefixedBytes()));
                    index++;
                }
            }
        }
        return transactionIds;
    }
}
