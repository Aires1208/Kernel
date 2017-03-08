package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 16-9-5.
 */
public class XInstanceEvent {
    private String instanceName;
    private double callsPermin;
    private int totalcalls;
    private double cpuUsage;
    private double memUsage;
    private double heapUsage;
    private long gcTimePerMin;

    public String getInstanceName() {
        return instanceName;
    }

    public double getCallsPermin() {
        return callsPermin;
    }

    public int getTotalcalls() {
        return totalcalls;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getMemUsage() {
        return memUsage;
    }

    public double getHeapUsage() {
        return heapUsage;
    }

    public long getGcTimePerMin() {
        return gcTimePerMin;
    }

    public XInstanceEvent(String instanceName, double callsPermin, int totalcalls, double cpuUsage, double memUsage, double heapUsage, long gcTimePerMin) {

        this.instanceName = instanceName;
        this.callsPermin = callsPermin;
        this.totalcalls = totalcalls;
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.heapUsage = heapUsage;
        this.gcTimePerMin = gcTimePerMin;
    }
}
