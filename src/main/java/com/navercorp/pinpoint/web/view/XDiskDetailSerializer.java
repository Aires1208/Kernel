package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESConst;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESMetrics;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;

import java.io.IOException;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeDouble;
import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeTimes;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

/**
 * Created by root on 17-2-15.
 */
public class XDiskDetailSerializer extends JsonSerializer<XDiskDetail> {
    @Override
    public void serialize(XDiskDetail xDiskDetail, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeFieldName("disk");
        jGen.writeStartObject();

        writeDiskInfo(xDiskDetail.getDiskInfo(), jGen);

        writeDiskIO(xDiskDetail.getMetrics(), jGen);

        writeDiskTps(xDiskDetail.getMetrics(), jGen);

        jGen.writeEndObject();
        jGen.writeEndObject();
    }

    private void writeDiskTps(ESQueryResult metrics, JsonGenerator jGen) throws IOException {
        jGen.writeFieldName("diskusage");
        jGen.writeStartObject();

        writeTimes(jGen, metrics.getEsMetricses(), ESConst.COLLECT_TIME);

        jGen.writeFieldName("numberOfTransfersPerSecond");
        writeDouble(jGen, metrics.getEsMetricses(), ESConst.DEVICE_TPS);

        jGen.writeEndObject();
    }

    private void writeDiskIO(ESQueryResult metrics, JsonGenerator jGen) throws IOException {
        jGen.writeFieldName("diskoperations");
        jGen.writeStartObject();

        writeTimes(jGen, metrics.getEsMetricses(), ESConst.COLLECT_TIME);

        jGen.writeFieldName("readsTheNumberOfBlocksPerSecond");
        writeDouble(jGen, metrics.getEsMetricses(), ESConst.DEVICE_READ_PERSECOND);

        jGen.writeFieldName("numberOfBlocksWrittenPerSecond");
        writeDouble(jGen, metrics.getEsMetricses(), ESConst.DEVICE_WRITE_PERSECOND);

        jGen.writeEndObject();
    }

    private void writeDiskInfo(ESQueryResult queryResult, JsonGenerator jGen) throws IOException {
        jGen.writeFieldName("diskDatas");
        jGen.writeStartArray();
        for (ESMetrics esMetrics : queryResult.getEsMetricses()) {
            jGen.writeStartObject();
            jGen.writeStringField("name", (String) esMetrics.getValue(ESConst.DEVICE_NAME));
            jGen.writeNumberField("theMajorNumber", 0);
            jGen.writeNumberField("theMinorNumber", 0);
            jGen.writeStringField("readTheNumberOfBlocks", wrapDouble((double) esMetrics.getValue(ESConst.DEVICE_READ)));
            jGen.writeStringField("numberOfBlocksWritten", wrapDouble((double) esMetrics.getValue(ESConst.DEVICE_WRITE)));
            jGen.writeEndObject();
        }
        jGen.writeEndArray();
    }
}
