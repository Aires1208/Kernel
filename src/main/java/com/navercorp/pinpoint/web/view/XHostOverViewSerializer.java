package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESConst;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESMetrics;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import org.apache.hadoop.hbase.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

import static com.navercorp.pinpoint.web.util.JsonFormatUtils.writeTimes;
import static com.navercorp.pinpoint.web.view.StringWrapper.wrapDouble;

/**
 * Created by root on 17-2-17.
 */
public class XHostOverViewSerializer extends JsonSerializer<XHostOverView> {
    @Override
    public void serialize(XHostOverView xHostOverView, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeFieldName("overview");
        jgen.writeStartObject();

        writeCPUs(jgen, xHostOverView.getCpus().getEsMetricses());

        writeMems(jgen, xHostOverView.getMems().getEsMetricses());

        writeFiles(jgen, xHostOverView.getFileSystems().getEsMetricses());

        writeDevices(jgen, xHostOverView.getDisks().getEsMetricses());

        writeNets(jgen, xHostOverView.getNets().getEsMetricses());

        writeOSInfo(jgen, xHostOverView.getAgentInfo());

        jgen.writeEndObject();
        jgen.writeEndObject();
    }

    private void writeOSInfo(JsonGenerator jgen, AgentInfo agentInfo) throws IOException {
        jgen.writeFieldName("systemMessage");
        jgen.writeStartObject();

        String[] osInfo = agentInfo.getOs().split(":");

        jgen.writeStringField("operatingSystem", osInfo[0]);
        jgen.writeStringField("theKernelVersion", osInfo[2]);
        jgen.writeStringField("FQDName", "FQDName");
        jgen.writeStringField("hostAddress", agentInfo.getIp());
        jgen.writeStringField("minutesOfTheSystem", "minutesOfTheSystem");
        jgen.writeStringField("systemUptime", "systemUptime");

        jgen.writeEndObject();
    }

    private void writeNets(JsonGenerator jgen, List<ESMetrics> esMetricses) throws IOException {
        jgen.writeFieldName("toptennetworkinterface");
        jgen.writeStartObject();

        jgen.writeFieldName("networkinterface");
        writeStringValues(jgen, esMetricses, ESConst.NET_NAME);

        jgen.writeFieldName("packetsTransferredPerSecond");
        jgen.writeStartArray();
        for (ESMetrics esMetricse : esMetricses) {
            long tps = (long) esMetricse.getValue(ESConst.NET_TRANSMIT_BYTES)
                    + (long) esMetricse.getValue(ESConst.NET_RECEIVE_BYTES);

            jgen.writeNumber(tps);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeFiles(JsonGenerator jgen, List<ESMetrics> esMetricses) throws IOException {
        jgen.writeFieldName("topfivefileststem");
        jgen.writeStartObject();

        jgen.writeFieldName("installationPoint");
        writeStringValues(jgen, esMetricses, ESConst.FILE_MOUNTON);

        jgen.writeFieldName("percentageUsed");
        jgen.writeStartArray();
        for (ESMetrics esMetricse : esMetricses) {
            double fileTotal = (double) esMetricse.getValue(ESConst.FILE_TOTAL);
            double fileUsed = fileTotal != 0 ? (double) esMetricse.getValue(ESConst.FILE_USED) / fileTotal : 0.00;
            jgen.writeString(wrapDouble(100 * fileUsed));
        }
        jgen.writeEndArray();

        jgen.writeFieldName("availablePercentage");
        jgen.writeStartArray();
        for (ESMetrics esMetricse : esMetricses) {
            double fileTotal = (double) esMetricse.getValue(ESConst.FILE_TOTAL);
            double fileIdle = fileTotal != 0 ? (double) esMetricse.getValue(ESConst.FILE_FREE) / fileTotal : 0.00;
            jgen.writeString(wrapDouble(100 * fileIdle));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeDevices(JsonGenerator jgen, List<ESMetrics> esMetricses) throws IOException {
        jgen.writeFieldName("topfivedisk");
        jgen.writeStartObject();
        jgen.writeFieldName("disk");
        writeStringValues(jgen, esMetricses, ESConst.DEVICE_NAME);

        jgen.writeFieldName("dataTransferredPerSecond");
        jgen.writeStartArray();
        for (ESMetrics esMetricse : esMetricses) {
            double tps = null != esMetricse.getValue(ESConst.DEVICE_TPS) ? (double) esMetricse.getValue(ESConst.DEVICE_TPS) : 0.00;
            jgen.writeString(wrapDouble(tps));
//            jgen.writeString(wrapDouble((double) esMetricse.getValue(ESConst.DEVICE_READ)
//                    + (double) esMetricse.getValue(ESConst.DEVICE_WRITE)));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private void writeStringValues(JsonGenerator jgen, List<ESMetrics> esMetricses, String valueType) throws IOException {
        jgen.writeStartArray();
        for (ESMetrics esMetrics : esMetricses) {
            jgen.writeString((String) esMetrics.getValue(valueType));
        }
        jgen.writeEndArray();
    }

    private void writeMems(JsonGenerator jgen, List<ESMetrics> esMetrics) throws IOException {
        jgen.writeFieldName("smemory");
        jgen.writeStartObject();

        if (!CollectionUtils.isEmpty(esMetrics)) {
            writeMemArray(jgen, esMetrics.get(0));
        }

        jgen.writeEndObject();
    }

    private void writeMemArray(JsonGenerator jgen, ESMetrics esMetric) throws IOException {
        double swapTotal = (double) esMetric.getValue(ESConst.SWAP_TOTAL);
        double swapUsed = (double) esMetric.getValue(ESConst.SWAP_USED);
        double swapIdle = (double) esMetric.getValue(ESConst.SWAP_FREE);
        double vmTotal = (double) esMetric.getValue(ESConst.VM_TOTAL);
        double vmUsed = (double) esMetric.getValue(ESConst.VM_USED);
        double vmIdle = (double) esMetric.getValue(ESConst.VM_FREE);
        double phyTotal = (double) esMetric.getValue(ESConst.PHY_TOTAL);
        double phyUsed = (double) esMetric.getValue(ESConst.PHY_USED);
        double phyIdle = (double) esMetric.getValue(ESConst.PHY_FREE);

        jgen.writeFieldName("percentageUsed");
        jgen.writeStartArray();
        jgen.writeString(wrapDouble(swapTotal != 0 ? 100 * swapUsed / swapTotal : 0.00));
        jgen.writeString(wrapDouble(vmTotal != 0 ? 100 * vmUsed / vmTotal : 0.00));
        jgen.writeString(wrapDouble(phyTotal != 0 ? 100 * phyUsed / phyTotal : 0.00));
        jgen.writeEndArray();

        jgen.writeFieldName("availablePercentage");
        jgen.writeStartArray();
        jgen.writeString(wrapDouble(swapTotal != 0 ? 100 * swapIdle / swapTotal : 0.00));
        jgen.writeString(wrapDouble(vmTotal != 0 ? 100 * vmIdle / vmTotal : 0.00));
        jgen.writeString(wrapDouble(phyTotal != 0 ? 100 * phyIdle / phyTotal : 0.00));
        jgen.writeEndArray();
    }

    private void writeCPUs(JsonGenerator jgen, List<ESMetrics> esMetrics) throws IOException {
        jgen.writeFieldName("aggregateCPUUsage");
        jgen.writeStartObject();

        writeTimes(jgen, esMetrics, ESConst.COLLECT_TIME);

        writeUsedArray(jgen, esMetrics);

        writeIdledArray(jgen, esMetrics);

        jgen.writeEndObject();
    }

    private void writeIdledArray(JsonGenerator jgen, List<ESMetrics> esMetrics) throws IOException {
        jgen.writeFieldName("idlePercentage");
        jgen.writeStartArray();
        for (ESMetrics esMetric : esMetrics) {
            long idle = (long) esMetric.getValue(ESConst.CPU_IDEL);

            jgen.writeString(wrapDouble(100 * idle / (double) getTotal(esMetric)));
        }
        jgen.writeEndArray();
    }

    private void writeUsedArray(JsonGenerator jgen, List<ESMetrics> esMetrics) throws IOException {
        jgen.writeFieldName("busyPercentage");
        jgen.writeStartArray();
        for (ESMetrics esMetric : esMetrics) {
            long used = (long) esMetric.getValue(ESConst.CPU_NICE)
                    + (long) esMetric.getValue(ESConst.CPU_SYSTEM)
                    + (long) esMetric.getValue(ESConst.CPU_USER)
                    + (long) esMetric.getValue(ESConst.CPU_IOWAIT)
                    + (long) esMetric.getValue(ESConst.CPU_IRQ)
                    + (long) esMetric.getValue(ESConst.CPU_SOFTIRQ);

            jgen.writeString(wrapDouble(100 * used / (double) getTotal(esMetric)));
        }
        jgen.writeEndArray();
    }

    private long getTotal(ESMetrics metrics) {
        return (long) metrics.getValue(ESConst.CPU_IDEL)
                + (long) metrics.getValue(ESConst.CPU_NICE)
                + (long) metrics.getValue(ESConst.CPU_SYSTEM)
                + (long) metrics.getValue(ESConst.CPU_USER)
                + (long) metrics.getValue(ESConst.CPU_SOFTIRQ)
                + (long) metrics.getValue(ESConst.CPU_IRQ)
                + (long) metrics.getValue(ESConst.CPU_IOWAIT);
    }
}
