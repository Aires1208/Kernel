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
public class XCpuDetailSerializer extends JsonSerializer<XCpuDetail> {
    @Override
    public void serialize(XCpuDetail xCpuDetail, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("aggregateCPUUsage");
        jgen.writeStartObject();

        writeCpus(jgen, xCpuDetail.getCpuStatics().getEsMetricses());

        writeTopN(jgen, xCpuDetail.getTop5Cpu().getEsMetricses());

        writeCpuRatio(jgen, xCpuDetail.getMetrics().getEsMetricses());

        jgen.writeEndObject();

        jgen.writeEndObject();
    }

    private void writeCpuRatio(JsonGenerator jgen, List<ESMetrics> esMetrics) throws IOException {
        jgen.writeFieldName("aggregatecpuusagedetails");
        jgen.writeStartObject();

        writeTimes(jgen, esMetrics, ESConst.COLLECT_TIME);

        jgen.writeFieldName("percentageOfUsers");
        writeValues(jgen, esMetrics, ESConst.CPU_USER);

        jgen.writeFieldName("userNicePercentage");
        writeValues(jgen, esMetrics, ESConst.CPU_NICE);

        jgen.writeFieldName("percentageOfSystem");
        writeValues(jgen, esMetrics, ESConst.CPU_SYSTEM);

        jgen.writeFieldName("waitForIOPercentage");
        writeValues(jgen, esMetrics, ESConst.CPU_IOWAIT);

        jgen.writeFieldName("userToSystemPercentage");
        jgen.writeStartArray();
        for (ESMetrics esMetric : esMetrics) {
            long user = (long) esMetric.getValue(ESConst.CPU_USER);
            long system = (long) esMetric.getValue(ESConst.CPU_SYSTEM);

            jgen.writeString(wrapDouble(100.00 * user / (double) (system + user)));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeTopN(JsonGenerator jgen, List<ESMetrics> topNMetrics) throws IOException {
        jgen.writeFieldName("topfivecpuusage");
        jgen.writeStartObject();
        jgen.writeFieldName("cpuIdentification");
        jgen.writeStartArray();
        for (ESMetrics topNMetric : topNMetrics) {
            jgen.writeString((String) topNMetric.getValue(ESConst.CPU_ID));
        }
        jgen.writeEndArray();

        jgen.writeFieldName("percentageOfSystemTime");
        writeValues(jgen, topNMetrics, ESConst.CPU_SYSTEM);

        jgen.writeFieldName("percentOfUserTime");
        writeValues(jgen, topNMetrics, ESConst.CPU_USER);

        jgen.writeFieldName("waitForIOTimePercentage");
        writeValues(jgen, topNMetrics, ESConst.CPU_IOWAIT);

        jgen.writeFieldName("percentageOfIdleTime");
        writeValues(jgen, topNMetrics, ESConst.CPU_IDEL);

        jgen.writeEndObject();
    }

    private void writeValues(JsonGenerator jgen, List<ESMetrics> topNMetrics, String valueType) throws IOException {
        jgen.writeStartArray();
        for (ESMetrics topNMetric : topNMetrics) {

            long total = getTotal(topNMetric);

            double system = total > 0 ? ((Long) topNMetric.getValue(valueType)) / (double) total : 0.00;
            jgen.writeString(wrapDouble(system * 100));

        }
        jgen.writeEndArray();
    }

    private long getTotal(ESMetrics metrics) {
        return (long) metrics.getValue(ESConst.CPU_IDEL)
                + (long) metrics.getValue(ESConst.CPU_NICE)
                + (long) metrics.getValue(ESConst.CPU_SYSTEM)
                + (long) metrics.getValue(ESConst.CPU_USER)
                + (long) metrics.getValue(ESConst.CPU_SOFTIRQ)
                + (long) metrics.getValue(ESConst.CPU_IRQ)
                + (long) metrics.getValue(ESConst.CPU_IOWAIT);
    }

    private void writeCpus(JsonGenerator jgen, List<ESMetrics> esMetricses) throws IOException {
        jgen.writeFieldName("cpu");
        jgen.writeStartArray();
        for (ESMetrics esMetrics : esMetricses) {
            jgen.writeStartObject();
            jgen.writeStringField("identification", (String) esMetrics.getValue(ESConst.CPU_ID));
            jgen.writeStringField("vendorIdentification", (String) esMetrics.getValue(ESConst.CPU_VENDOR));
            jgen.writeStringField("serialNumber", (String) esMetrics.getValue(ESConst.CPU_FAMILY));
            jgen.writeStringField("model", (String) esMetrics.getValue(ESConst.CPU_MODEL));
            jgen.writeStringField("modelName", (String) esMetrics.getValue(ESConst.CPU_MODEL_NAME));
            jgen.writeStringField("clockSpeed", (String) esMetrics.getValue(ESConst.CPU_MHZ));
            jgen.writeStringField("theCacheSize", (String) esMetrics.getValue(ESConst.CPU_CACHE));
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
}
