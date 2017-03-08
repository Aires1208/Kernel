package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.bo.AnnotationBo;
import com.navercorp.pinpoint.common.bo.AnnotationBoList;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.OffsetFixedBuffer;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationMapper implements RowMapper<Map<Long, List<AnnotationBo>>> {

    @Override
    public Map<Long, List<AnnotationBo>> mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return Collections.emptyMap();
        }
        Cell[] rawCells = result.rawCells();
        Map<Long, List<AnnotationBo>> annotationList = new HashMap<>();

        for (Cell cell : rawCells) {

            Buffer buffer = new OffsetFixedBuffer(cell.getQualifierArray(), cell.getQualifierOffset());
            long spanId = buffer.readLong();

            if (CellUtil.matchingFamily(cell, HBaseTables.TRACES_CF_ANNOTATION)) {
                int valueLength = cell.getValueLength();
                if (valueLength == 0) {
                    continue;
                }

                buffer.setOffset(cell.getValueOffset());
                AnnotationBoList annotationBoList = new AnnotationBoList();
                annotationBoList.readValue(buffer);
                if (annotationBoList.size() > 0) {
                    annotationBoList.setSpanId(spanId);
                    annotationList.put(spanId, annotationBoList.getAnnotationBoList());
                }
            }
        }
        return annotationList;
    }
}
