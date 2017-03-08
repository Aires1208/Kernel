package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeArray;

/**
 * Created by root on 16-9-5.
 */
public class XServiceCallsEventsSerializer extends JsonSerializer<XServiceCallsEvents> {
    @Override
    public void serialize(XServiceCallsEvents xServiceCallsEvents, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeFieldName("appNames");
        writeArray(jgen, xServiceCallsEvents.getAppList());

        jgen.writeStringField("appName", xServiceCallsEvents.getAppName());

        jgen.writeFieldName("serviceInfoList");

        writeArray(jgen, xServiceCallsEvents.getEvents());

        jgen.writeEndObject();
    }
}
