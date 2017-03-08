package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.events.ResultEvent;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class EventsMapper implements RowMapper<List<ResultEvent>> {

    @Override
    public List<ResultEvent> mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return newArrayList();
        }
        List<ResultEvent> resultEvents = new ArrayList<>();
        for (Cell cell : result.rawCells()) {
            byte[] value = cell.getValue();
            String originValue = Bytes.toString(value);
            ResultEvent resultEvent = new ObjectMapper().readValue(originValue, ResultEvent.class);
            resultEvents.add(resultEvent);
        }
        
        return resultEvents;
    }

}
