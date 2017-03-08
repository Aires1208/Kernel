/*
 * Copyright 2016 NAVER Corp.
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
import com.navercorp.pinpoint.web.vo.TransactionHealth;
import com.navercorp.pinpoint.web.vo.XService;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.wrap;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

/**
 * @author HyunGil JeMaong
 */
public class XServiceSerializer extends JsonSerializer<XService> {

    @Override
    public void serialize(XService xService, JsonGenerator jgen, SerializerProvider provider) throws IOException {


        jgen.writeStartObject();
        jgen.writeStringField("appName", xService.getName());
        jgen.writeNumberField("healthRuleViolations", xService.getEventCount());

        jgen.writeFieldName("nodeHealth");
        writeNodeHealth(xService.getNodeHealth(), jgen);

        jgen.writeFieldName("transactionHealth");
        writeTransactionHealth(xService.getTransactionHealth(), jgen);

        jgen.writeStringField("calls", wrap(xService.getCalls()));
        jgen.writeStringField("callsPerMin", wrapDouble(xService.getCallsPerMin()));
        jgen.writeStringField("responseTime", wrapDouble(xService.getResponse()));
        jgen.writeStringField("errorsPercent", wrapDouble(xService.getErrorsPercent()));
        jgen.writeStringField("errors", wrap(xService.getErrors()));
        jgen.writeStringField("errorsPerMin", wrapDouble(xService.getErrorsPerMin()));
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
