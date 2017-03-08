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
import com.navercorp.pinpoint.web.vo.XMetricsDouble2;
import com.navercorp.pinpoint.web.vo.XMetricsDouble1;

import java.io.IOException;
import java.text.DecimalFormat;

import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

public class XHostDashBoardSerializer extends JsonSerializer<XHostDashBoard> {

    @Override
    public void serialize(XHostDashBoard xHostDashBoard, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {


        jgen.writeStartObject();

        jgen.writeFieldName("summary");

        jgen.writeStartObject();
        jgen.writeStringField("hostId", xHostDashBoard.getxHostDetail().getHostId());
        jgen.writeStringField("ip", xHostDashBoard.getxHostDetail().getIpAddr());
        jgen.writeStringField("os", xHostDashBoard.getxHostDetail().getOsType());
        jgen.writeStringField("health", xHostDashBoard.getxHostDetail().getHealth());
        jgen.writeEndObject();

        jgen.writeFieldName("cpuInfo");
        writeMetrics(xHostDashBoard.getxHostDetail().getCpuMetrics(), jgen);

        jgen.writeFieldName("memInfo");
        writeMetrics(xHostDashBoard.getxHostDetail().getMemMetrics(), jgen);

        jgen.writeFieldName("diskInfo");
        writeMetrics(xHostDashBoard.getxHostDetail().getDiskMetrics(), jgen);

        jgen.writeFieldName("netInfo");
        writeNetMetrics(xHostDashBoard.getxHostDetail().getNetMetrics(), jgen);

        jgen.writeEndObject();
    }

    private void writeNetMetrics(XMetricsDouble2 metrics, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("info", metrics.getInfo());

        jgen.writeFieldName("time");
        jgen.writeStartArray();
        for (String time : metrics.getTimestamps()) {
            jgen.writeObject(time);
        }
        jgen.writeEndArray();

        jgen.writeFieldName("Dl");
        jgen.writeStartArray();
        for (Double data : metrics.getIn()) {
            jgen.writeObject(wrapDouble(data));
        }
        jgen.writeEndArray();

        jgen.writeFieldName("Ul");
        jgen.writeStartArray();
        for (Double data : metrics.getOut()) {
            jgen.writeObject(wrapDouble(data));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeMetrics(XMetricsDouble1 metricses, JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("info", metricses.getInfo());

        jgen.writeFieldName("time");
        jgen.writeStartArray();
        for (String time : metricses.getTimestamps()) {
            jgen.writeObject(time);
        }
        jgen.writeEndArray();

        jgen.writeFieldName("data");
        jgen.writeStartArray();
        for (Double data : metricses.getDatas()) {
            jgen.writeObject(wrapPerCent(data));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private String wrapPerCent(Double input) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(input*100);
    }

}
