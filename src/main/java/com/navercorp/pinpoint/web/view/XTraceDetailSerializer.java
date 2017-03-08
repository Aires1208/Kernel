package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.XMetrics;
import com.navercorp.pinpoint.web.vo.callstacks.Record;
import com.navercorp.pinpoint.web.vo.callstacks.RecordSet;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.FullDataStrMs;
import static com.navercorp.pinpoint.web.view.StringWrapper.FullDateStr;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapPercent;

/**
 * Created by root on 6/12/16.
 */
public class XTraceDetailSerializer extends JsonSerializer<XTraceDetail> {

    @Override
    public void serialize(XTraceDetail xTraceDetail, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();


        jgen.writeFieldName("topo");
        jgen.writeObject(xTraceDetail.getTopo());

        jgen.writeFieldName("cpuInfo");
        writeMetrics(xTraceDetail.getCpuMetrics(), jgen);

        jgen.writeFieldName("memInfo");
        writeMetrics(xTraceDetail.getMemoryMetrics(), jgen);

        jgen.writeFieldName("storeInfo");
        writeMetrics(xTraceDetail.getStoreMetrics(), jgen);

        jgen.writeFieldName("netInfo");
        writeMetrics(xTraceDetail.getNetMetrics(), jgen);

        jgen.writeFieldName("treeTable");
        writeTreeTable(xTraceDetail.getRecordSet(), jgen);


        jgen.writeEndObject();
    }

    private void writeTreeTable(RecordSet recordSet, JsonGenerator jgen) throws IOException {
        jgen.writeStartArray();

        long elapsed = recordSet.getEndTime() > recordSet.getRecordList().get(0).getElapsed() ? recordSet.getEndTime() : recordSet.getRecordList().get(0).getElapsed();
        for (Record record : recordSet.getRecordList()) {
            jgen.writeStartObject();

            jgen.writeNumberField("id", record.getId());
            jgen.writeNumberField("idParent", record.getParentId());
            jgen.writeStringField("method", wrapStr(record.getTitle()));
            jgen.writeStringField("argument", wrapStr(record.getArguments()));
            if (record.isMethod()) {
                jgen.writeStringField("startTime", FullDataStrMs(record.getBegin()));
                jgen.writeStringField("gap", String.valueOf(record.getGap() > 0L ? record.getGap() : 0L));
                jgen.writeStringField("exec", String.valueOf(record.getElapsed()));

                double execPercent = record.getExecutionMilliseconds() / (double) elapsed;
                jgen.writeStringField("execPercent", wrapPercent(execPercent));
                jgen.writeStringField("self", String.valueOf(record.getExecutionMilliseconds()));
                jgen.writeStringField("class", wrapStr(record.getSimpleClassName()));
                jgen.writeStringField("api", wrapStr(record.getApiType()));
                jgen.writeStringField("agent", wrapStr(record.getAgent()));
                jgen.writeStringField("app", wrapStr(record.getApplicationName()));
            } else {
                jgen.writeStringField("startTime", "");
                jgen.writeStringField("gap", "");
                jgen.writeStringField("exec", "");
                jgen.writeStringField("execPercent", "");
                jgen.writeStringField("self", "");
                jgen.writeStringField("class", "");
                jgen.writeStringField("api", "");
                jgen.writeStringField("agent", "");
                jgen.writeStringField("app", "");
            }

            jgen.writeEndObject();
        }

        jgen.writeEndArray();
    }

    private void writeMetrics(XMetrics xMetrics, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("info", xMetrics.getInfo());

        jgen.writeFieldName("time");
        jgen.writeStartArray();
        for (String item : xMetrics.getTimestamps()) {
            jgen.writeObject(item);
        }
        jgen.writeEndArray();

        jgen.writeFieldName("data");
        jgen.writeStartArray();
        for (Integer item : xMetrics.getDataPoints()) {
            jgen.writeObject(item);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }


    private String wrapStr(String input) {
        return input == null ? "N/A" : input;
    }
}