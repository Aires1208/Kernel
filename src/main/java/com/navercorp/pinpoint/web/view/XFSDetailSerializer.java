package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESConst;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESMetrics;

import java.io.IOException;
import java.util.List;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeTimes;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

/**
 * Created by root on 17-2-16.
 */
public class XFSDetailSerializer extends JsonSerializer<XFSDetail> {
    @Override
    public void serialize(XFSDetail xfsDetail, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("fileSystem");
        jgen.writeStartObject();

        writeFSInfos(jgen, xfsDetail.getFileStatics().getEsMetricses());

        writeFSUsage(jgen, xfsDetail.getFileUsages().getEsMetricses());

        writeFSUsed(jgen, xfsDetail.getFileUsages().getEsMetricses());

        jgen.writeEndObject();
        jgen.writeEndObject();
    }

    private void writeFSUsed(JsonGenerator jgen, List<ESMetrics> esMetricses) throws IOException {
        jgen.writeFieldName("aggregatefilesystemspaceusage");
        jgen.writeStartObject();

        writeTimes(jgen, esMetricses, ESConst.COLLECT_TIME);

        jgen.writeFieldName("used");
        jgen.writeStartArray();
        for (ESMetrics esMetricse : esMetricses) {
            jgen.writeNumber((Long) esMetricse.getValue(ESConst.FILE_USED));
        }
        jgen.writeEndArray();

        jgen.writeFieldName("total");
        jgen.writeStartArray();
        for (ESMetrics esMetricse : esMetricses) {
            jgen.writeNumber((Long) esMetricse.getValue(ESConst.FILE_TOTAL));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeFSUsage(JsonGenerator jgen, List<ESMetrics> fsUsages) throws IOException {
        jgen.writeFieldName("aggregatefilesystemusage");
        jgen.writeStartObject();

        writeTimes(jgen, fsUsages, ESConst.COLLECT_TIME);

        jgen.writeFieldName("percentageUsed");
        writeUsage(jgen, fsUsages, ESConst.FILE_USED);

        jgen.writeFieldName("availablePercentage");
        writeUsage(jgen, fsUsages, ESConst.FILE_FREE);

        jgen.writeEndObject();
    }

    private void writeUsage(JsonGenerator jgen, List<ESMetrics> fsUsages, String type) throws IOException {
        jgen.writeStartArray();
        for (ESMetrics fsUsage : fsUsages) {
            long total = (Long) fsUsage.getValue(ESConst.FILE_TOTAL);
            long index = (Long) fsUsage.getValue(type);

            jgen.writeString(wrapDouble(100 * index / (double) total));
        }
        jgen.writeEndArray();
    }

    private void writeFSInfos(JsonGenerator jgen, List<ESMetrics> fsStatics) throws IOException {
        jgen.writeFieldName("fileSystemDatas");
        jgen.writeStartArray();
        for (ESMetrics fsStatic : fsStatics) {
            jgen.writeStartObject();
            jgen.writeStringField("name", (String) fsStatic.getValue(ESConst.FILE_SYSTEM));
            jgen.writeStringField("status", "");
            jgen.writeStringField("type", "");
            jgen.writeStringField("disk", (String) fsStatic.getValue(ESConst.FILE_MOUNTON));
            jgen.writeNumberField("size", (Integer) fsStatic.getValue(ESConst.FILE_TOTAL));
            jgen.writeNumberField("usageAmount", (Integer) fsStatic.getValue(ESConst.FILE_USED));
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
}
