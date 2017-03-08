package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapPercent;

public class XHostsDashBoardSerializer extends JsonSerializer<XHostsDashBoard> {

    private static final String DEFAULT_SERVICE_NAME_SEPARATER = "_";

    @Override
    public void serialize(XHostsDashBoard xHostsDashBoard, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        jgen.writeFieldName("servers");

        jgen.writeStartArray();
        for (HostStat hostStat : xHostsDashBoard.getHostStats()) {
            if (!hostStat.hasStat()) {
                writeServerStat(hostStat, jgen);
            }
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeServerStat(HostStat hostStat, JsonGenerator jgen) throws IOException {
        for (String service : hostStat.getServices()) {
            List<String> dn = parseServiceName(service);
            jgen.writeStartObject();
            jgen.writeStringField("application", dn.get(0));
            jgen.writeStringField("service", dn.get(1));
            jgen.writeStringField("hostid", hostStat.getHostId());
            jgen.writeStringField("ip", hostStat.getIpAddr());
            jgen.writeStringField("os", hostStat.getOsType());
            jgen.writeStringField("runIn", hostStat.isDocker() ? "Docker" : "Server");
            jgen.writeStringField("health", hostStat.getHealth());

            jgen.writeStringField("cpu", wrapPercent(hostStat.getCpu()));
            jgen.writeStringField("mem", wrapPercent(hostStat.getMem()));
            jgen.writeStringField("disk", wrapPercent(hostStat.getDisk()));
            jgen.writeStringField("network", wrapPercent(hostStat.getNet()));

            jgen.writeEndObject();
        }
    }

    private List<String> parseServiceName(String service) {
        String[] services = service.split(DEFAULT_SERVICE_NAME_SEPARATER);
        switch (services.length) {
            case 3:
            case 2:
                return newArrayList(services[0], services[1]);
            default:
                return newArrayList(service, service);
        }

    }

}
