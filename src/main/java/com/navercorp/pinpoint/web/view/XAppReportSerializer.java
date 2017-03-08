package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.*;
import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeArray;

public class XAppReportSerializer extends JsonSerializer<XAppReport> {
    @Override
    public void serialize(XAppReport xAppReport, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("appNames");
        writeArray(jgen, xAppReport.getApplist());

        jgen.writeStringField("appName", xAppReport.getAppName());

        jgen.writeFieldName("healthInfo");
        jgen.writeStartObject();
        jgen.writeFieldName("appHealth");
        writeHealth(jgen, xAppReport.getApplicationHealth());
        jgen.writeFieldName("transactionHealth");
        writeHealth(jgen, xAppReport.getTransactionHealth());
        jgen.writeFieldName("serviceHealth");
        writeHealth(jgen, xAppReport.getServiceHealth());
        jgen.writeEndObject();

        jgen.writeFieldName("riskInfo");
        jgen.writeStartObject();
        jgen.writeFieldName("transactionRisk");
        writeTransactionRisk(jgen, xAppReport.getTransactionRisk());
        jgen.writeFieldName("serviceRisk");
        writeServiceRisk(jgen, xAppReport.getServiceRisk());
        jgen.writeFieldName("dbRisk");
        writeDBsRisk(jgen, xAppReport.getDBsRisk());
        jgen.writeEndObject();

        jgen.writeEndObject();
    }

    private void writeHealth(JsonGenerator jgen, XHealth health) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("info", health.getInfo());
        jgen.writeNumberField("score", health.getScore());

        if (health.isSetCriticals()) {
            jgen.writeNumberField("criticals", health.getCritical());
        }

        if (health.isSetWarnnings()) {
            jgen.writeNumberField("warnings", health.getWarning());
        }

        jgen.writeFieldName("time");
        writeArray(jgen, health.getTimestamps());

        jgen.writeFieldName("data");
        writeArray(jgen, health.getHealths());

        jgen.writeEndObject();
    }

    private void writeTransactionRisk(JsonGenerator jgen, XTransactionsRisk transactionRisk) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("calls");
        writeNameIntegerObject(jgen, transactionRisk.getTopCalls());

        jgen.writeFieldName("errors");
        writeNameIntegerObject(jgen, transactionRisk.getTopErrors());

        jgen.writeFieldName("response");
        writeNameLongObject(jgen, transactionRisk.getTopResponse());

        jgen.writeEndObject();
    }

    private void writeServiceRisk(JsonGenerator jgen, XServicesRisk serviceRisk) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("busyService");
        writeNameIntegerObject(jgen, serviceRisk.getTopBusyServices());

        jgen.writeFieldName("riskService");
        writeNameDoubleObject(jgen, serviceRisk.getTopRiskServices());

        jgen.writeEndObject();
    }

    private void writeDBsRisk(JsonGenerator jgen, XDBsRisk dBsRisk) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("dbNames");
        writeArray(jgen, dBsRisk.getDbList());

        jgen.writeFieldName("dbData");
        jgen.writeStartArray();
        for (XDBRisk xdbRisk : dBsRisk.getDbRisks()){
            writeOneDBDataObject(jgen, xdbRisk);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeOneDBDataObject(JsonGenerator jgen, XDBRisk xdbRisk) throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("dbName", xdbRisk.getDbName());

        jgen.writeFieldName("slowSql");
        writeNameDoubleObject(jgen, xdbRisk.getTopSlowSql());

        jgen.writeFieldName("frequeryCalls");
        writeNameIntegerObject(jgen, xdbRisk.getTopFrequencySql());

        jgen.writeEndObject();
    }


    private void writeNameIntegerObject(JsonGenerator jgen, List<XDataPoint<Integer>> xDataPoints) throws IOException {
        jgen.writeStartObject();

        List<String> names = newArrayList();
        List<Integer> datas = newArrayList();
        for (XDataPoint<Integer> xDataPoint : xDataPoints) {
            names.add(xDataPoint.getX());
            datas.add(xDataPoint.getY());
        }
        jgen.writeFieldName("name");
        writeArray(jgen, names);

        jgen.writeFieldName("data");
        writeArray(jgen, datas);

        jgen.writeEndObject();
    }

    private void writeNameLongObject(JsonGenerator jgen, List<XDataPoint<Long>> xDataPoints) throws IOException {
        jgen.writeStartObject();

        List<String> names = newArrayList();
        List<Long> datas = newArrayList();
        for (XDataPoint<Long> xDataPoint : xDataPoints) {
            names.add(xDataPoint.getX());
            datas.add(xDataPoint.getY());
        }
        jgen.writeFieldName("name");
        writeArray(jgen, names);
        jgen.writeFieldName("data");
        writeArray(jgen, datas);

        jgen.writeEndObject();
    }

    private void writeNameDoubleObject(JsonGenerator jgen, List<XDataPoint<Double>> xDataPoints) throws IOException {
        jgen.writeStartObject();

        List<String> names = newArrayList();
        List<Double> datas = newArrayList();
        for (XDataPoint<Double> xDataPoint : xDataPoints) {
            names.add(xDataPoint.getX());
            datas.add(xDataPoint.getY());
        }
        jgen.writeFieldName("name");
        writeArray(jgen, names);
        jgen.writeFieldName("data");
        writeArray(jgen, datas);

        jgen.writeEndObject();
    }
}
