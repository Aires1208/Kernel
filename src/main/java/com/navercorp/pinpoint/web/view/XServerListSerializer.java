package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.view.XHostList;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.simplifyStr;

/**
 * Created by root on 16-10-17.
 */
public class XServerListSerializer extends JsonSerializer<XHostList> {
    @Override
    public void serialize(XHostList xHostList, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("serverlist");
        jgen.writeStartArray();
        for (String fullName : xHostList.getFullNames()) {
            jgen.writeStartObject();
            jgen.writeStringField("fullname", fullName);
            jgen.writeStringField("simplifiedname", simplifyStr(fullName));
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
