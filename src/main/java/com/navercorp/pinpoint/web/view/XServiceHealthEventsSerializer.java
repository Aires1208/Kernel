package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.util.EventUtils;
import com.navercorp.pinpoint.web.vo.XServiceEvent;
import com.navercorp.pinpoint.web.vo.XServiceHealthEvent;

import java.io.IOException;
import java.util.List;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeArray;
import static com.navercorp.pinpoint.web.view.StringWrapper.FullDateStr;

public class XServiceHealthEventsSerializer extends JsonSerializer<XServiceHealthEvents> {

    @Override
    public void serialize(XServiceHealthEvents xServiceHealthEvents, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("appNames");
        writeArray(jgen, xServiceHealthEvents.getAppList());

        jgen.writeStringField("appName", xServiceHealthEvents.getAppName());

        jgen.writeFieldName("serviceInfoList");
        jgen.writeStartArray();
        for (XServiceHealthEvent xServiceHealthEvent : xServiceHealthEvents.getServiceInfoList()){
            writeXServiceHealthEvent(jgen, xServiceHealthEvent);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeXServiceHealthEvent(JsonGenerator jgen, XServiceHealthEvent xServiceHealthEvent) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("serviceName", xServiceHealthEvent.getServiceName());
        jgen.writeNumberField("serviceScores", xServiceHealthEvent.getServiceScores());

        jgen.writeNumberField("eventlength", xServiceHealthEvent.getEventCount());
        jgen.writeFieldName("eventInfoList");
        jgen.writeStartArray();
        for (ResultEvent event : xServiceHealthEvent.getEventList()){
            writeHealthEventInfo(jgen, event);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeHealthEventInfo(JsonGenerator jgen, ResultEvent event) throws IOException {
        jgen.writeStartObject();
        int eventType = event.getEventType();

        jgen.writeStringField("eventName", EventUtils.getDescription(eventType));
        jgen.writeStringField("eventLevel", EventUtils.getLevel(eventType));
        jgen.writeStringField("eventDetails", event.getDetail());
        jgen.writeStringField("createTime",FullDateStr(event.getStartTime()));
        jgen.writeStringField("beLong", event.getObjDN());

        jgen.writeEndObject();
    }
}
