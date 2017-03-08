package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 2016/11/25.
 */
public class XHostDetailDot {
    private String timestamp;
    private double cpuUsage;
    private double memUsage;
    private double diskUsage;
    private double netDLUsage;
    private double netULUsage;

    private XHostDetailDot(String timestamp, double cpuUsage, double memUsage, double diskUsage, double netDLUsage, double netULUsage) {
        this.timestamp = timestamp;
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.diskUsage = diskUsage;
        this.netDLUsage = netDLUsage;
        this.netULUsage = netULUsage;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public String getTimestamp() {
        return timestamp;
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

    public double getNetDLUsage() {
        return netDLUsage;
    }

    public double getNetULUsage() {
        return netULUsage;
    }

    public static final class Builder {
        private String timestamp;
        private double cpuUsage;
        private double memUsage;
        private double diskUsage;
        private double netDLUsage;
        private double netULUsage;

        public Builder() {
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp == null ? "" : timestamp;
            return this;
        }

        public Builder cpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
            return this;
        }

        public Builder memUsage(double memUsage) {
            this.memUsage = memUsage;
            return this;
        }

        public Builder diskUsage(double diskUsage) {
            this.diskUsage = diskUsage;
            return this;
        }

        public Builder netDLUsage(double netDLUsage) {
            this.netDLUsage = netDLUsage;
            return this;
        }

        public Builder netULUsage(double netULUsage) {
            this.netULUsage = netULUsage;
            return this;
        }

        public XHostDetailDot build() {
            return new XHostDetailDot(timestamp, cpuUsage, memUsage, diskUsage, netDLUsage, netULUsage);
        }
    }
}