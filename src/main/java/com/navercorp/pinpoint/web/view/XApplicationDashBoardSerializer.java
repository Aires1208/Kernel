/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.TransactionHealth;
import com.navercorp.pinpoint.web.vo.XLoadInfo;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.*;

/**
 * @author emeroad
 */
public class XApplicationDashBoardSerializer extends JsonSerializer<XApplicationDashBoard> {

    @Override
    public void serialize(XApplicationDashBoard xApplicationDashBoard, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        jgen.writeFieldName("summary");
        writeApplications(xApplicationDashBoard, jgen);

        jgen.writeFieldName("topo");
        jgen.writeObject(xApplicationDashBoard.getTopo());

        XLoadInfo xLoadInfo = xApplicationDashBoard.getLoadInfo();

        jgen.writeFieldName("loadInfo");
        writeObjects(xLoadInfo.getTimes(), xLoadInfo.getCalls(), xLoadInfo.getRange(), jgen);

        jgen.writeFieldName("respondInfo");
        writeObjects(xLoadInfo.getTimes(), xLoadInfo.getResponses(), xLoadInfo.getRange(), jgen);

        jgen.writeFieldName("errorInfo");
        writeObjects(xLoadInfo.getTimes(), xLoadInfo.getErrors(), xLoadInfo.getRange(), jgen);

        jgen.writeEndObject();
    }

    private void writeObjects(String[] times, Object[] counters, Range range, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("info", "");

        jgen.writeFieldName("time");
        jgen.writeStartArray();
        jgen.writeString(DefaultDateStr(range.getFrom()));
        for (String item : times) {
            jgen.writeObject(item);
        }
        jgen.writeString(DefaultDateStr(range.getTo()));
        jgen.writeEndArray();

        jgen.writeFieldName("data");
        jgen.writeStartArray();
        jgen.writeObject(counters[0]);
        for (Object item : counters) {
            jgen.writeObject(item);
        }
        jgen.writeObject(counters[counters.length - 1]);
        jgen.writeEndArray();

        jgen.writeEndObject();
    }


    public void writeApplications(XApplicationDashBoard xApplicationDashBoard, JsonGenerator jgen) throws IOException {
        XApplication xApplication = xApplicationDashBoard.getXApplication();
        jgen.writeStartObject();
        jgen.writeStringField("appName", xApplication.getName());
        jgen.writeNumberField("healthRuleViolations", xApplication.getEventCount());

        jgen.writeFieldName("nodeHealth");
        writeNodeHealth(xApplication.getServiceHealth(), jgen);

        jgen.writeFieldName("transactionHealth");
        writeTransactionHealth(xApplicationDashBoard.getTransactionHealth(), jgen);


        jgen.writeStringField("calls", wrap(xApplication.getCalls()));
        jgen.writeStringField("callsPerMin", wrapDouble(xApplication.getCallsPerMin()));
        jgen.writeStringField("responseTime", wrapDouble(xApplication.getResponse()));
        jgen.writeStringField("errorsPercent", wrapPercent(xApplication.getErrorsPercent()));
        jgen.writeStringField("errors", wrap(xApplication.getErrors()));
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
