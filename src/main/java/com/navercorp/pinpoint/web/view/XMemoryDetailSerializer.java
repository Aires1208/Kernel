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

/**
 * Created by root on 17-2-9.
 */
public class XMemoryDetailSerializer extends JsonSerializer<XMemoryDetail> {
    @Override
    public void serialize(XMemoryDetail xHostMemInfo, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeFieldName("memoryUsage");
        jGen.writeStartObject();

        writeSwap(jGen, xHostMemInfo.getMemMetaData().getEsMetricses());

        writePage(jGen, xHostMemInfo.getMemMetaData().getEsMetricses());

        writeVirtual(jGen, xHostMemInfo.getMemMetaData().getEsMetricses());

        writePhysical(jGen, xHostMemInfo.getMemMetaData().getEsMetricses());

        jGen.writeEndObject();
        jGen.writeEndObject();
    }

    private void writePhysical(JsonGenerator jGen, List<ESMetrics> esMetricses) throws IOException {
        jGen.writeFieldName("realmemoryusage");
        jGen.writeStartObject();

        writeTimes(jGen, esMetricses, ESConst.COLLECT_TIME);

        jGen.writeFieldName("idle");
        writeLong(jGen, esMetricses, ESConst.PHY_FREE);

        jGen.writeFieldName("usedNetwork");
        writeFakeData(jGen, esMetricses);

        jGen.writeFieldName("used");
        writeLong(jGen, esMetricses, ESConst.PHY_USED);

        jGen.writeFieldName("total");
        writeLong(jGen, esMetricses, ESConst.PHY_TOTAL);

        jGen.writeEndObject();
    }

    private void writeFakeData(JsonGenerator jGen, List<ESMetrics> esMetricses) throws IOException {
        jGen.writeStartArray();
        for (int i = 0; i < esMetricses.size(); i++) {
            jGen.writeNumber(0.00);
        }
        jGen.writeEndArray();
    }

    private void writeVirtual(JsonGenerator jGen, List<ESMetrics> esMetricses) throws IOException {
        jGen.writeFieldName("virtualmemoryusage");
        jGen.writeStartObject();

        writeTimes(jGen, esMetricses, ESConst.COLLECT_TIME);

        jGen.writeFieldName("used");
        writeLong(jGen, esMetricses, ESConst.VM_USED);

        jGen.writeFieldName("idle");
        writeLong(jGen, esMetricses, ESConst.VM_FREE);


        jGen.writeFieldName("total");
        writeLong(jGen, esMetricses, ESConst.VM_TOTAL);

        jGen.writeEndObject();
    }

    private void writePage(JsonGenerator jGen, List<ESMetrics> esMetricses) throws IOException {
        jGen.writeFieldName("pagescheduling");
        jGen.writeStartObject();

        writeTimes(jGen, esMetricses, ESConst.COLLECT_TIME);

        jGen.writeFieldName("numberIn");
        writeFakeData(jGen, esMetricses);

        jGen.writeFieldName("numberOut");
        writeFakeData(jGen, esMetricses);

        jGen.writeEndObject();
    }

    private void writeSwap(JsonGenerator jGen, List<ESMetrics> esMetricses) throws IOException {
        jGen.writeFieldName("swapmemoryusage");
        jGen.writeStartObject();

        writeTimes(jGen, esMetricses, ESConst.COLLECT_TIME);

        jGen.writeFieldName("used");
        writeLong(jGen, esMetricses, ESConst.SWAP_USED);

        jGen.writeFieldName("idle");
        writeLong(jGen, esMetricses, ESConst.SWAP_FREE);

        jGen.writeFieldName("total");
        writeLong(jGen, esMetricses, ESConst.SWAP_TOTAL);

        jGen.writeEndObject();
    }

    private static void writeLong(JsonGenerator jGen, List<ESMetrics> esMetricses, String key) throws IOException {
        jGen.writeStartArray();
        for (ESMetrics esMetrics : esMetricses) {
            Object value = esMetrics.getValue(key);
            if (value instanceof Long) {
                jGen.writeNumber((long) value / 1024);
            }
        }
        jGen.writeEndArray();
    }
}
