package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.vo.XHealth;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XAppReportSerializerTest {

    @Test
    public void testSerialize() throws Exception {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = new ObjectMapper().getFactory().createGenerator(stringWriter);
        XAppReport xAppreport = mockXReport();
        new XAppReportSerializer().serialize(xAppreport, jsonGenerator, null);
        jsonGenerator.flush();
        jsonGenerator.close();
        System.out.println(stringWriter.toString());
    }

    private XAppReport mockXReport() {
        XAppReport report = new XAppReport("test");
        report.setApplist(mockAppList());
        report.setApplicationHealth(mockAppHealth());
        report.setServiceHealth(mockServiceHealth());
        report.setTransactionHealth(mockTransHealth());
        return report;
    }

    private XHealth mockServiceHealth() {
        XHealth xHealth = new XHealth(80);
        xHealth.setCritical(10);
        xHealth.setWarning(20);
        xHealth.setHealths(newArrayList(90.00, 80.00, 70.00, 80.00));
        xHealth.setTimestamps(newArrayList("11", "22", "33", "44"));
        return xHealth;
    }

    private XHealth mockTransHealth() {
        XHealth xHealth = new XHealth(60);
        xHealth.setCritical(5);
        xHealth.setWarning(15);
        xHealth.setHealths(newArrayList(50.00, 60.00, 70.00, 80.00));
        xHealth.setTimestamps(newArrayList("1", "2", "3", "4"));
        return xHealth;
    }

    private XHealth mockAppHealth() {
        XHealth xHealth = new XHealth(70);
        xHealth.setCritical(11);
        xHealth.setWarning(22);
        xHealth.setHealths(newArrayList(70.00, 80.00, 70.00, 60.00));
        xHealth.setTimestamps(newArrayList("111", "222", "333", "444"));
        return xHealth;
    }

    private List<String> mockAppList() {
        return newArrayList("test", "test1", "test2");
    }
}