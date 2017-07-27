package com.navercorp.pinpoint.web.view;

/**
 * Created by aires on 7/25/16.
 */

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.common.topo.domain.CpuUsage;
import com.navercorp.pinpoint.common.topo.domain.HeapInfo;
import com.navercorp.pinpoint.common.topo.domain.PermGen;
import com.navercorp.pinpoint.common.topo.domain.TransactionsPerSecond;

import java.io.IOException;
import java.util.List;

public class XJVMDashBoardSerializer extends JsonSerializer<XJVMDashBoard> {
    @Override
    public void serialize(XJVMDashBoard xJVMDashBoard, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("heapInfo");
        writeHeapInfo(xJVMDashBoard.getHeapInfo(), jsonGenerator);

        jsonGenerator.writeFieldName("permGen");
        writeGermGen(xJVMDashBoard.getPermGen(), jsonGenerator);


        jsonGenerator.writeFieldName("jvmSys");
        writeJvmSys(xJVMDashBoard.getCpuUsage(), jsonGenerator);

        jsonGenerator.writeFieldName("tps");
        writeTransactionsPerSecond(xJVMDashBoard.getTransactionsPerSecond(), jsonGenerator);
        jsonGenerator.writeFieldName("vmargs");
        writeJVMArgs(xJVMDashBoard.getJvmArgs(), jsonGenerator);

        jsonGenerator.writeFieldName("jvmVersion");
        writeJVMString(xJVMDashBoard.getJvmVersion(), jsonGenerator);

        jsonGenerator.writeFieldName("gcTypeName");
        writeJVMString(xJVMDashBoard.getGcTypeName(), jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void writeHeapInfo(HeapInfo heapInfo, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("info", heapInfo.getInfo());

        jsonGenerator.writeFieldName("time");
        jsonGenerator.writeStartArray();
        for (String item : heapInfo.getTimes()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("max");
        jsonGenerator.writeStartArray();
        for (Long item : heapInfo.getMaxs()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("used");
        jsonGenerator.writeStartArray();
        for (Long item : heapInfo.getUseds()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("fgc");
        jsonGenerator.writeStartArray();
        for (Long item : heapInfo.getFgcs()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void writeGermGen(PermGen permGen, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("info", permGen.getInfo());

        jsonGenerator.writeFieldName("time");
        jsonGenerator.writeStartArray();
        for (String item : permGen.getTimes()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("max");
        jsonGenerator.writeStartArray();
        for (Long item : permGen.getMaxs()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("used");
        jsonGenerator.writeStartArray();
        for (Long item : permGen.getUseds()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("fgc");
        jsonGenerator.writeStartArray();
        for (Long item : permGen.getFgcs()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void writeJvmSys(CpuUsage cpuUsage, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("info", cpuUsage.getInfo());

        jsonGenerator.writeFieldName("time");
        jsonGenerator.writeStartArray();
        for (String item : cpuUsage.getTimes()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("jvm");
        jsonGenerator.writeStartArray();
        for (Double item : cpuUsage.getJvmCpuUsage()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("sys");
        jsonGenerator.writeStartArray();
        for (Double item : cpuUsage.getSystemCpuUsage()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void writeTransactionsPerSecond(TransactionsPerSecond transactionsPerSecond, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("info", transactionsPerSecond.getInfo());

        jsonGenerator.writeFieldName("time");
        jsonGenerator.writeStartArray();
        for (String item : transactionsPerSecond.getTimes()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("sc");
        jsonGenerator.writeStartArray();
        for (Long item : transactionsPerSecond.getSampledNewCount()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();


        jsonGenerator.writeFieldName("sn");
        jsonGenerator.writeStartArray();
        for (Long item : transactionsPerSecond.getSampledContinuationCount()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("uc");
        jsonGenerator.writeStartArray();
        for (Long item : transactionsPerSecond.getUnsampledNewCount()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("un");
        jsonGenerator.writeStartArray();
        for (Long item : transactionsPerSecond.getUnsampledContinuationCount()) {
            jsonGenerator.writeObject(item);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }

    private void writeJVMArgs(List<String> jvmargs, JsonGenerator jsonGenerator) throws IOException {
        if (null == jvmargs || 0 == jvmargs.size()) {
            jsonGenerator.writeString("");
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String arg : jvmargs) {
            stringBuffer.append(arg).append(";");
        }
        jsonGenerator.writeString(stringBuffer.substring(0, stringBuffer.length() - 1));

    }

    private void writeJVMString(String args, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeString(args);
    }

}
