package com.navercorp.pinpoint.web.view;

public class StatLine {
    private double cpuUsage;
    private double memUsage;
    private double diskUsage;
    private double netUsage;

    public StatLine(double cpuUsage, double memUsage, double diskUsage, double netUsage) {
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.diskUsage = diskUsage;
        this.netUsage = netUsage;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getMemUsage() {
        return memUsage;
    }

    public double getDiskUsage() {
        return diskUsage;
    }

    public double getNetUsage() {
        return netUsage;
    }
}
