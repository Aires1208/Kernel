package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.FixedBuffer;
import com.navercorp.pinpoint.common.buffer.OffsetFixedBuffer;
import com.navercorp.pinpoint.common.hbase.HBaseTables;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.common.util.BytesUtils;
import com.navercorp.pinpoint.common.util.TimeUtils;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.common.hbase.HBaseTables.APPLICATION_NAME_MAX_LEN;

@Component
public class TopoLineMapper implements RowMapper<TopoLine> {

    @Override
    public TopoLine mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return null;
        }

        final Cell[] cells = result.rawCells();
        List<XNode> xnodeList = newArrayList();
        List<XLink> xlinkList = newArrayList();

        byte[] rowkey = result.getRow();
        long reversedTimestamp = BytesUtils.bytesToLong(rowkey, APPLICATION_NAME_MAX_LEN);
        long timestamp = TimeUtils.recoveryTimeMillis(reversedTimestamp);

        for (Cell cell : cells) {
            String cellCF = BytesUtils.toString(cell.getFamily());
            if ("N".equals(cellCF)) {
                XNode xNode = new XNode(Bytes.toString(cell.getQualifier()));
                xNode.readValue(cell.getValueArray(), cell.getValueOffset());

                xnodeList.add(xNode);
            }else if ("L".equals(cellCF)) {
                Buffer buffer = new OffsetFixedBuffer(cell.getQualifierArray(), cell.getQualifierOffset());
                String from = buffer.readPrefixedString();
                String to = buffer.readPrefixedString();
                XLink xLink = new XLink(from, to);
                xLink.readValue(cell.getValueArray(), cell.getValueOffset());

                xlinkList.add(xLink);
            }
            else {
                continue;
            }
        }

        TopoLine topoLine = new TopoLine(xnodeList, xlinkList);
        topoLine.setTimestamp(timestamp);

        return topoLine;
    }
}