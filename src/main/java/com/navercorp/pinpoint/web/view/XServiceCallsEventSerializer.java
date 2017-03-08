package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.XInstanceEvent;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapPercent;

/**
 * Created by root on 16-9-5.
 */
public class XServiceCallsEventSerializer extends JsonSerializer<XServiceCallsEvent> {
    @Override
    public void serialize(XServiceCallsEvent xServiceCallsEvent, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("serviceName", xServiceCallsEvent.getSvcName());
        jgen.writeStringField("calls", wrapDouble(xServiceCallsEvent.getCallsPermin()));
        jgen.writeNumberField("eventSize", xServiceCallsEvent.getEvents().size());
        jgen.writeFieldName("eventList");

        jgen.writeStartArray();
        for (XInstanceEvent xInstanceEvent : xServiceCallsEvent.getEvents()) {
            writeEvent(jgen, xInstanceEvent);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeEvent(JsonGenerator jgen, XInstanceEvent event) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("instanceName", event.getInstanceName());
        jgen.writeStringField("calls", wrapDouble(event.getCallsPermin()));
        jgen.writeStringField("cpuusage", wrapPercent(event.getCpuUsage()));
        jgen.writeStringField("memusage", wrapPercent(event.getMemUsage()));
        jgen.writeStringField("heapusage", wrapPercent(event.getHeapUsage()));
        jgen.writeNumberField("gctime", event.getGcTimePerMin());
        jgen.writeEndObject();
    }
}
