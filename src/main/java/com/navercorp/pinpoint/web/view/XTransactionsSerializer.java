package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.service.XBusinessTransactions;
import com.navercorp.pinpoint.web.vo.Range;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeApp;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapPercent;


public class XTransactionsSerializer extends JsonSerializer<XTransactions> {

    @Override
    public void serialize(XTransactions xTransactions, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeFieldName("apps");
        jgen.writeStartArray();
        for (XApplication xApplication : xTransactions.getAppList()) {
            writeApp(xApplication, jgen);
        }
        jgen.writeEndArray();

        jgen.writeFieldName("typeList");
        writeXTypes(xTransactions.getxTypes(), jgen);

        jgen.writeFieldName("tables");
        writeXTransactions(xTransactions.getXBusinessTransactions(), jgen, xTransactions.getRange());

        jgen.writeEndObject();
    }

    public void writeXTransactions(Collection<XBusinessTransactions> xBusinessTransactionses, JsonGenerator jgen, Range range) throws IOException {

        jgen.writeStartArray();

        for (XBusinessTransactions xBusinessTransactions : xBusinessTransactionses) {
            jgen.writeStartObject();

            jgen.writeStringField("name", xBusinessTransactions.getTraceName());
            jgen.writeStringField("health", xBusinessTransactions.getHealth());
            jgen.writeStringField("tier", xBusinessTransactions.getAgentId());
            jgen.writeNumberField("responseTime", xBusinessTransactions.getAverageResponseTime());
            jgen.writeNumberField("maxResponseTime", xBusinessTransactions.getMaxResponseTime());
            jgen.writeNumberField("calls", xBusinessTransactions.getCalls());
            jgen.writeStringField("callsPerMin", wrapDouble(xBusinessTransactions.getCallsperMin(range)));
            jgen.writeNumberField("errors", xBusinessTransactions.getErrors());
            jgen.writeStringField("errorPercent", wrapPercent(xBusinessTransactions.getErrpercent()));
            jgen.writeStringField("errorsPerMin", wrapDouble(xBusinessTransactions.getErrperMin(range)));

            jgen.writeEndObject();

        }
        jgen.writeEndArray();
    }

    private void writeXTypes(List<XType> xTypes, JsonGenerator jgen) throws IOException {
        jgen.writeStartArray();

        for (XType xType : xTypes) {
            jgen.writeStartObject();

            jgen.writeStringField("id", xType.getId());
            jgen.writeStringField("value", xType.getValue());

            jgen.writeEndObject();
        }

        jgen.writeEndArray();
    }

}
