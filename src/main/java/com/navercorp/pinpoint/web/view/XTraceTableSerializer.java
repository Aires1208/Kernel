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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author emeroad
 */
public class XTraceTableSerializer extends JsonSerializer<XTraceTable> {

    @Override
    public void serialize(XTraceTable xTraceTable, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeFieldName("typeList");
        writeXTypes(xTraceTable.getxTypes(),jgen);

        jgen.writeFieldName("traceTable");
        writeXTransactions(xTraceTable.getxTraces(),jgen);

        jgen.writeEndObject();
    }

    private void writeXTypes(List<XType> xTypes, JsonGenerator jgen) throws  IOException{
        jgen.writeStartArray();

        for(XType xType : xTypes) {
            jgen.writeStartObject();

            jgen.writeStringField("id",xType.getId());
            jgen.writeStringField("value",xType.getValue());

            jgen.writeEndObject();
        }

        jgen.writeEndArray();
    }

    public void writeXTransactions(List<XTrace> xTraces, JsonGenerator jgen) throws IOException{
        jgen.writeStartArray();

        for(XTrace xTrace : xTraces) {
            jgen.writeObject(xTrace);
        }

        jgen.writeEndArray();
    }

}
