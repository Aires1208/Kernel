package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESConst;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESMetrics;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

/**
 * Created by root on 17-2-17.
 */
public class XProcessDetailSerializer extends JsonSerializer<XProcessDetail> {
    @Override
    public void serialize(XProcessDetail xProcessDetail, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("processInformation");
        jgen.writeStartObject();

        jgen.writeFieldName("topFPIOrderBycpuUsageDatas");
        writeProcess(jgen, xProcessDetail.getTopNUsage().getEsMetricses());

        jgen.writeFieldName("topFPIOrderBytheCPUTimeUsedDatas");
        writeProcess(jgen, xProcessDetail.getTopNTime().getEsMetricses());

        jgen.writeFieldName("topFPIOrderByvirtualSizeDatas");
        writeProcess(jgen, xProcessDetail.getTopNVirt().getEsMetricses());

        jgen.writeEndObject();
        jgen.writeEndObject();
    }

    private void writeProcess(JsonGenerator jgen, List<ESMetrics> esMetricses) throws IOException {
        jgen.writeStartArray();
        for (ESMetrics esMetrics : esMetricses) {
            jgen.writeStartObject();
            jgen.writeStringField("pid", (String) esMetrics.getValue(ESConst.PROCESS_PID));
            jgen.writeStringField("process", (String) esMetrics.getValue(ESConst.PROCESS_NAME));
            Optional<Object> command = Optional.ofNullable(esMetrics.getValue(ESConst.PROCESS_COMMAND));
            jgen.writeStringField("binaryPath", (String) command.orElse(""));
            jgen.writeStringField("sessionID", "");
            jgen.writeStringField("cpuUsage", wrapDouble((Double) esMetrics.getValue(ESConst.PROCESS_CPU_USAGE)));
            jgen.writeStringField("residentSetSize", "");
            jgen.writeStringField("virtualSize", wrapDouble((double) esMetrics.getValue(ESConst.PROCESS_VIRT) / 1024.00));
            jgen.writeStringField("theCPUTimeUsed", String.valueOf(esMetrics.getValue(ESConst.PROCESS_CPU_TIME)));
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
}
