package com.navercorp.pinpoint.web.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESMetrics;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.vo.XService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.navercorp.pinpoint.web.view.StringWrapper.FullDateStr;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapNumberDouble;

/**
 * Created by root on 16-10-26.
 */
public class JsonFormatUtils {
    public static void writeApp(XApplication xApplication, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("name", xApplication.getName());
        jgen.writeStringField("level", "application");
        jgen.writeFieldName("services");
        jgen.writeStartArray();
        for (XService xService : xApplication.getXServices()) {
            writeService(xService, xApplication.getName(), jgen);
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    public static void writeService(XService xService, String name, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("name", xService.getName());
        jgen.writeStringField("appName", name);
        jgen.writeStringField("level", "service");
        jgen.writeFieldName("instances");
        jgen.writeStartArray();
        for (String agentId : xService.getAgentIds()) {
            jgen.writeStartObject();
            jgen.writeStringField("name", agentId);
            jgen.writeStringField("appName", name);
            jgen.writeStringField("serviceName", xService.getName());
            jgen.writeStringField("level", "instance");
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    public static void writeArray(JsonGenerator jgen, List<?> list) throws IOException {
        jgen.writeStartArray();

        for (Object obj : list){
            jgen.writeObject(obj);
        }

        jgen.writeEndArray();
    }

    public static void writeTimes(JsonGenerator jGen, List<ESMetrics> esMetricses, String key) throws IOException {
        jGen.writeFieldName("time");
        jGen.writeStartArray();
        for (ESMetrics esMetrics : esMetricses) {
            Object value = esMetrics.getValue(key);
            if (value instanceof Long) {
                jGen.writeString(FullDateStr((long) value));
            }
        }
        jGen.writeEndArray();
    }

    public static void writeDouble(JsonGenerator jGen, List<ESMetrics> esMetricses, String key) throws IOException {
        jGen.writeStartArray();
        for (ESMetrics esMetrics : esMetricses) {
            Object value = esMetrics.getValue(key);
            if (value instanceof Double) {
                jGen.writeString(wrapDouble((Double) value));
            }
        }
        jGen.writeEndArray();
    }
}
