package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.web.vo.TransactionHealth;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapPercent;

public class XApplicationSerializer extends JsonSerializer<XApplication> {

    @Override
    public void serialize(XApplication xApplication, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("appName", xApplication.getName());

        jgen.writeNumberField("healthRuleViolations", xApplication.getEventCount());

        jgen.writeFieldName("nodeHealth");
        writeNodeHealth(xApplication.getServiceHealth(), jgen);

        jgen.writeFieldName("transactionHealth");
        writeTransactionHealth(xApplication.getTransactionHealth(), jgen);

        jgen.writeStringField("calls", String.valueOf(xApplication.getCalls()));
        jgen.writeStringField("callsPerMin", wrapDouble(xApplication.getCallsPerMin()));
        jgen.writeStringField("responseTime", wrapDouble(xApplication.getResponse()));
        jgen.writeStringField("errorsPercent", wrapPercent(xApplication.getErrorsPercent()));
        jgen.writeStringField("errors", String.valueOf(xApplication.getErrors()));
        jgen.writeStringField("errorsPerMin", wrapDouble(xApplication.getErrorsPerMin()));

        jgen.writeEndObject();
    }

    private void writeTransactionHealth(TransactionHealth transactionHealth, JsonGenerator jgen) throws IOException {

        jgen.writeStartObject();

        jgen.writeNumberField("critical", transactionHealth.getCritical());
        jgen.writeNumberField("warning", transactionHealth.getWarning());
        jgen.writeNumberField("normal", transactionHealth.getNormal());

        jgen.writeEndObject();

    }


    private void writeNodeHealth(NodeHealth nodeHealth, JsonGenerator jgen) throws IOException {

        jgen.writeStartObject();

        jgen.writeNumberField("critical", nodeHealth.getCritical());
        jgen.writeNumberField("warning", nodeHealth.getWarning());
        jgen.writeNumberField("normal", nodeHealth.getNormal());

        jgen.writeEndObject();

    }


}
