package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;


public class XTransScattersSerializer extends JsonSerializer<XTransScatters>{

    @Override
    public void serialize(XTransScatters xTransScatters, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException{
        jgen.writeStartObject();

        jgen.writeFieldName("criticalScatter");
        writeXTransScatters(xTransScatters.getCriticals(),jgen);

        jgen.writeFieldName("warningScatter");
        writeXTransScatters(xTransScatters.getWarnings(),jgen);


        jgen.writeFieldName("normalScatter");
        writeXTransScatters(xTransScatters.getNormals(),jgen);

        jgen.writeEndObject();

    }

    public void writeXTransScatters(List<XTransScatter> xTransScatters, JsonGenerator jgen) throws IOException{
        jgen.writeStartArray();

        for (XTransScatter xTransScatter : xTransScatters) {
            jgen.writeStartArray();

            jgen.writeNumber(xTransScatter.getStartTime());
            jgen.writeNumber(xTransScatter.getResponse());

            jgen.writeEndArray();
        }

        jgen.writeEndArray();
    }

}
