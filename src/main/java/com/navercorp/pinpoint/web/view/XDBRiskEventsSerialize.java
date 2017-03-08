package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.SqlInfo;

import java.io.IOException;

/**
 * Created by root on 16-10-8.
 */
public class XDBRiskEventsSerialize extends JsonSerializer<XDBRiskEvents> {
    @Override
    public void serialize(XDBRiskEvents dbRiskEvents, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        if (!dbRiskEvents.getXdbRiskEvents().isEmpty()) {
            jgen.writeFieldName("eventList");
            jgen.writeStartArray();
            for (SqlInfo sqlInfo : dbRiskEvents.getXdbRiskEvents()) {
                jgen.writeObject(sqlInfo);
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();
    }
}
