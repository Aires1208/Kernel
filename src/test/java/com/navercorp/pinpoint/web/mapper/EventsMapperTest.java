package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.htrace.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by root on 16-9-28.
 */
public class EventsMapperTest {
    @Test
    public void mapRow() throws Exception {
        //given
        ResultEvent event1 = new ResultEvent("app=EMS, service=PM", 10032, 12345678, 87654321, "detail1.......");
        ResultEvent event2 = new ResultEvent("app=EMS, service=PM", 10060, 12345678, 87654321, "detail2.......");
        ResultEvent event3 = new ResultEvent("app=EMS, service=PM", 20011, 12345678, 87654321, "detail3.......");

        final Result result = Result.create(newArrayList(createCell(HBaseTables.ACTIVE_EVENT_CF_EVENT, Bytes.toBytes(event1.getEventType()), event1),
                createCell(HBaseTables.ACTIVE_EVENT_CF_EVENT, Bytes.toBytes(event2.getEventType()), event2),
                createCell(HBaseTables.ACTIVE_EVENT_CF_EVENT, Bytes.toBytes(event3.getEventType()), event3)));
        List<ResultEvent> expect_res = newArrayList(event1, event2, event3);

        //when
        EventsMapper mapper = new EventsMapper();
        List<ResultEvent> resultEventList = mapper.mapRow(result, 0);

        //then
        assertThat(resultEventList, is(expect_res));
    }

    private Cell createCell(byte[] family, byte[] qualifier, ResultEvent event) throws JsonProcessingException {
        String value = new ObjectMapper().writeValueAsString(event);
        byte[] bytesVale = Bytes.toBytes(value);

        return CellUtil.createCell(HConstants.EMPTY_BYTE_ARRAY, family, qualifier, HConstants.LATEST_TIMESTAMP, KeyValue.Type.Maximum.getCode(), bytesVale);
    }
}