package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.topo.domain.XRpc;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by ${aires} on 11/23/16.
 */
@Component
public class XRpcMapper implements RowMapper<List<XRpc>> {
    @Override
    public List<XRpc> mapRow(Result result, int i) throws Exception {
        List<XRpc> xRpcs = newArrayList();
        final Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
        XRpc xRpc = new XRpc();
            xRpc.readValue(cell.getValueArray(), cell.getValueOffset());
            xRpcs.add(xRpc);
        }
        return xRpcs;
    }
}
