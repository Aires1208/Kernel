package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.util.EventUtils;

import java.io.IOException;
import java.util.List;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeApp;
import static com.navercorp.pinpoint.web.view.StringWrapper.FullDateStr;

/**
 * Created by root on 9/25/16.
 */
public class XEventsDashBoardSerializer extends JsonSerializer<XEventsDashBoard> {

    @Override
    public void serialize(XEventsDashBoard xEventsDashBoard, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeFieldName("apps");
        jgen.writeStartArray();
        for (XApplication xApplication : xEventsDashBoard.getxApplications()) {
            writeApp(xApplication, jgen);
        }
        jgen.writeEndArray();
        writeXEvents(xEventsDashBoard.getResultEvents(), jgen);

        jgen.writeEndObject();

    }

    private void writeXEvents(List<ResultEvent> xEvents, JsonGenerator jgen) throws IOException {
        jgen.writeFieldName("tables");
        jgen.writeStartArray();

        for (ResultEvent event : xEvents) {
            int type = event.getEventType();

            jgen.writeStartObject();
            jgen.writeStringField("objecttype", EventUtils.getObjType(type));
            jgen.writeStringField("objectdn", event.getObjDN());
            jgen.writeStringField("starttime", FullDateStr(event.getStartTime()));
            jgen.writeStringField("endtime", event.getEndTime() > event.getStartTime() ? FullDateStr(event.getEndTime()) : " ");
            jgen.writeStringField("eventname", EventUtils.getDescription(type));
            jgen.writeStringField("level", EventUtils.getLevel(type));
            jgen.writeStringField("detail", event.getDetail());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();

    }
}
