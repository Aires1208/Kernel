package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeApp;

/**
 * Created by root on 16-10-10.
 */
public class XASIListSerialize extends JsonSerializer<XASIList> {
    @Override
    public void serialize(XASIList xasiList, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("apps");
        jgen.writeStartArray();
        for (XApplication xApplication : xasiList.getxApplications()) {
            writeApp(xApplication, jgen);
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
