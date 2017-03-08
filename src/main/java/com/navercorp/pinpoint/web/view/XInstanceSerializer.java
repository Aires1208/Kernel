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
import com.navercorp.pinpoint.web.util.TypeUtils;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.TransactionHealth;
import com.navercorp.pinpoint.web.vo.XInstance;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.DefaultDateStr;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrap;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

/**
 * @author HyunGil JeMaong
 */
public class XInstanceSerializer extends JsonSerializer<XInstance> {

    @Override
    public void serialize(XInstance xInstance, JsonGenerator jgen, SerializerProvider provider) throws IOException {


        jgen.writeStartObject();
        jgen.writeStringField("appName", xInstance.getName());
        jgen.writeNumberField("healthRuleViolations", xInstance.getEventCount());

        jgen.writeFieldName("nodeHealth");
        writeNodeHealth(xInstance.getNodeHealth(),jgen);

        jgen.writeFieldName("transactionHealth");
        writeTransactionHealth(xInstance.getTransactionHealth(),jgen);

        jgen.writeStringField("calls", wrap(xInstance.getCalls()));
        jgen.writeStringField("callsPerMin", wrapDouble(xInstance.getCallsPerMin()));
        jgen.writeStringField("responseTime", wrapDouble(xInstance.getResponse()));
        jgen.writeStringField("errorsPercent", wrapDouble(xInstance.getErrorsPercent()));
        jgen.writeStringField("errors", wrap(xInstance.getErrors()));
        jgen.writeStringField("errorsPerMin", wrapDouble(xInstance.getErrorsPerMin()));

        AgentInfo agentInfo = xInstance.getAgentInfo();
        jgen.writeStringField("serverIp", agentInfo.getIp());
        jgen.writeStringField("hostId", agentInfo.getMac() + "@" + agentInfo.getHostName());
        jgen.writeStringField("pid", String.valueOf(agentInfo.getPid()));

        jgen.writeStringField("serviceType", TypeUtils.getType(agentInfo.getServiceTypeCode()));

        jgen.writeStringField("runIn", agentInfo.isDocker() ? "Docker" : "Server");
        jgen.writeStringField("agentId", agentInfo.getAgentId());
        jgen.writeStringField("agentVersion", agentInfo.getAgentVersion());

        String startTime = DefaultDateStr(agentInfo.getStartTimestamp());
        jgen.writeStringField("startTime", startTime);
        jgen.writeStringField("status", agentInfo.getStatus().getState().toString());

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
