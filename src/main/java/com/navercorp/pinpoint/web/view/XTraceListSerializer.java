package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.XTransactionName;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.simplifyStr;

/**
 * Created by root on 16-10-18.
 */
public class XTraceListSerializer extends JsonSerializer<XTraceList> {
    @Override
    public void serialize(XTraceList xTraceList, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("traceList");
        jgen.writeStartArray();
        for (XTransactionName transactionName : xTraceList.getTransactionNames()) {
            jgen.writeStartObject();
            jgen.writeStringField("fullname", transactionName.getTransactionName());
            jgen.writeStringField("simplifiedname", simplifyStr(transactionName.getTransactionName()));
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
