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
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XLoadInfo;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.DefaultDateStr;

public class XInstanceDashBoardSerializer extends JsonSerializer<XInstanceDashBoard> {

    @Override
    public void serialize(XInstanceDashBoard xInstanceDashBoard, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeFieldName("summary");
        jgen.writeObject(xInstanceDashBoard.getxService());

        jgen.writeFieldName("topo");
        jgen.writeObject(xInstanceDashBoard.getTopo());

        XLoadInfo xLoadInfo = xInstanceDashBoard.getxLoadInfo();

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

}
