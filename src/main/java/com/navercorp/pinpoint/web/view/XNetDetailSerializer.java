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
 * Created by root on 17-2-16.
 */
public class XNetDetailSerializer extends JsonSerializer<XNetDetail> {
    @Override
    public void serialize(XNetDetail xNetDetail, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("networkInterface");
        jgen.writeStartObject();

        writeNetStatics(jgen, xNetDetail.getNetStatics().getEsMetricses());

        writeNetIo(jgen, xNetDetail.getNetUsages().getEsMetricses());

        writeNEtErrs(jgen, xNetDetail.getNetUsages().getEsMetricses());

        jgen.writeEndObject();
        jgen.writeEndObject();
    }

    private void writeNEtErrs(JsonGenerator jgen, List<ESMetrics> netErrors) throws IOException {
        jgen.writeFieldName("aggregatenetworkinterfaceerror");
        jgen.writeStartObject();

        writeTimes(jgen, netErrors, ESConst.COLLECT_TIME);

        jgen.writeFieldName("theNumberOfPacketConflicts");
        writeNetValue(jgen, netErrors, ESConst.NET_COLLS);

        jgen.writeFieldName("theNumberOfPacketInputErrors");
        writeNetValue(jgen, netErrors, ESConst.NET_RECEIVE_ERRORS);

        jgen.writeFieldName("theNumberOfPacketOutputErrors");
        writeNetValue(jgen, netErrors, ESConst.NET_TRANSMIT_ERRORS);

        jgen.writeEndObject();
    }

    private void writeNetIo(JsonGenerator jgen, List<ESMetrics> netIO) throws IOException {
        jgen.writeFieldName("aggregatenetworkinterfaceiorate");
        jgen.writeStartObject();

        writeTimes(jgen, netIO, ESConst.COLLECT_TIME);

        jgen.writeFieldName("theNumberOfKilobytesReceivedPerSecond");
        writeNetValue(jgen, netIO, ESConst.NET_RECEIVE_BYTES);

        jgen.writeFieldName("theNumberOfKilobytesTransferredPerSecond");
        writeNetValue(jgen, netIO, ESConst.NET_TRANSMIT_BYTES);

        jgen.writeEndObject();
    }

    private void writeNetValue(JsonGenerator jgen, List<ESMetrics> netIO, String valueType) throws IOException {
        jgen.writeStartArray();
        for (ESMetrics esMetrics : netIO) {
            jgen.writeNumber((Long) esMetrics.getValue(valueType));
        }
        jgen.writeEndArray();
    }


    private void writeNetStatics(JsonGenerator jgen, List<ESMetrics> netStatics) throws IOException {
        jgen.writeFieldName("networkInterfaceDatas");
        jgen.writeStartArray();
        for (ESMetrics netStatic : netStatics) {
            jgen.writeStartObject();
            jgen.writeStringField("name", (String) netStatic.getValue(ESConst.NET_NAME));
            jgen.writeStringField("status", "");
            jgen.writeStringField("ipv4Address", (String) netStatic.getValue(ESConst.NET_V4_ADDRESS));
            jgen.writeNumberField("maximumTransmissionUnit", (Integer) netStatic.getValue(ESConst.NET_MTU));
            jgen.writeStringField("type", "");
            jgen.writeStringField("macAddress", (String) netStatic.getValue(ESConst.NET_MAC_ADDRESS));
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
}
