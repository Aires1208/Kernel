package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.XTransactionEvent;

import java.io.IOException;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeArray;

/**
 * Created by root on 16-8-29.
 */
public class XTransactionEventsSerializer extends JsonSerializer<XTransactionEvents> {
    @Override
    public void serialize(XTransactionEvents events, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("appNames");
        writeArray(jgen, events.getAppList());
        jgen.writeStringField("appName", events.getAppName());

        jgen.writeFieldName("transactionInfoList");
        jgen.writeStartArray();
        for (XTransactionEvent xtransactionEvent : events.getEventList()) {
            writeEvent(xtransactionEvent, jgen);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeEvent(XTransactionEvent event, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("createTime", event.getEventTime());
        jgen.writeStringField("transactionName", event.getTraceName());
        jgen.writeStringField("eventName", event.getEventName());
        jgen.writeStringField("eventLevel", event.getLevel().getDesc());
        jgen.writeStringField("eventDetails", event.getEventDetail());

        jgen.writeEndObject();
    }

}
