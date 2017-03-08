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
import com.navercorp.pinpoint.common.topo.domain.AgentInfoDigest;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.web.util.TypeUtils;
import com.navercorp.pinpoint.web.vo.XServiceTopo;

import java.io.IOException;
import java.util.List;


public class XServiceTopoSerializer extends JsonSerializer<XServiceTopo> {

    @Override
    public void serialize(XServiceTopo xServiceTopo, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("nodes");
        writeNodes(xServiceTopo.getXNodes(),jgen);

        jgen.writeFieldName("links");
        writeLinks(xServiceTopo.getXLinks(),jgen);

        jgen.writeEndObject();
    }

    private void writeNodes(List<XNode> xNodes, JsonGenerator jgen) throws IOException{
        jgen.writeStartArray();

        for (XNode xNode : xNodes) {
            jgen.writeStartObject();

            jgen.writeStringField("key", xNode.getName());
            jgen.writeStringField("name", xNode.getName());
            jgen.writeStringField("type", TypeUtils.getType(xNode.getServiceType()));
            if(xNode.getCount() > 0){
                jgen.writeStringField("metrics", xNode.getMetrics());
                jgen.writeNumberField("count", xNode.getCount());
                jgen.writeStringField("tracked", "true");
            } else {
                jgen.writeStringField("tracked", "false");
            }
            jgen.writeFieldName("instances");
            writeNodeInstances(xNode.getInstanceNames(),jgen);

            jgen.writeEndObject();
        }

        jgen.writeEndArray();
    }

    private void writeNodeInstances(List<AgentInfoDigest> agentInfoDigests, JsonGenerator jgen) throws IOException{
        jgen.writeStartArray();
        if (!agentInfoDigests.isEmpty()) {
            for(AgentInfoDigest agentInfoDigest : agentInfoDigests) {
                jgen.writeStartObject();
                jgen.writeStringField("id", agentInfoDigest.getAgentId());
                jgen.writeStringField("value", agentInfoDigest.getAgentId());
                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();

    }


    private void writeLinks(List<XLink> xLinks, JsonGenerator jgen) throws IOException{
        jgen.writeStartArray();

        for (XLink xLink : xLinks) {
            jgen.writeStartObject();

            jgen.writeStringField("from", xLink.getFrom());
            jgen.writeStringField("to", xLink.getTo());
            jgen.writeStringField("respondTime", xLink.getMetrics());

            jgen.writeEndObject();
        }

        jgen.writeEndArray();
    }
}
