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

public class XApplicationsDashBoardSerializer extends JsonSerializer<XApplicationsDashBoard> {

    @Override
    public void serialize(XApplicationsDashBoard XApplicationsDashBoard, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeFieldName("summary");
        writeApplications(XApplicationsDashBoard.getxApplications(),jgen);


        jgen.writeEndObject();
    }

    private void writeApplications(List<XApplication> xApplications, JsonGenerator jgen) throws IOException, JsonProcessingException{
        jgen.writeStartArray();

        for(XApplication xApplication : xApplications) {
            jgen.writeObject(xApplication);
        }

        jgen.writeEndArray();

    }
}
