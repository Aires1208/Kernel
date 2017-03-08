package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.buffer.AutomaticBuffer;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by root on 16-10-18.
 */
public class TransactionListMapperTest {
    @Test
    public void mapRow() throws Exception {
        long lastTime = System.currentTimeMillis();
        final Result result = Result.create(newArrayList(
                createCell(Bytes.toBytes("EMS_minos"),
                        HBaseTables.TRANSACTIONLIST_CF_NAME,
                        Bytes.toBytes("8502"),
                        createBytes(lastTime, "minos-agent")),
                createCell(Bytes.toBytes("EMS_minos"),
                        HBaseTables.TRANSACTIONLIST_CF_NAME,
                        Bytes.toBytes("/api/fm-history/v1/hisalarms"),
                        createBytes(lastTime + 20L, "minos-agent"))));
        List<XTransactionName> expect = newArrayList(new XTransactionName("EMS_minos", "8502", lastTime, "minos-agent"),
                new XTransactionName("EMS_minos", "/api/fm-history/v1/hisalarms", lastTime + 20L, "minos-agent"));

        //when
        TransactionListMapper mapper = new TransactionListMapper();
        List<XTransactionName> res = mapper.mapRow(result, 0);


        //then
        assertThat(res, is(expect));
    }

    private Cell createCell(byte[] row, byte[] cfName, byte[] qualifier, byte[] value) {
        return CellUtil.createCell(row, cfName, qualifier, System.currentTimeMillis(), KeyValue.Type.Maximum.getCode(), value);
    }

    private byte[] createBytes(long timeStamp, String agentId) {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(timeStamp);
        buffer.putPrefixedString(agentId);

        return buffer.getBuffer();
    }

}