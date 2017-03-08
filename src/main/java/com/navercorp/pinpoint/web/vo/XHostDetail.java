package com.navercorp.pinpoint.web.vo;

public class XHostDetail {
    private static final String DEFAULT_STRING = "";
    private String hostId;
    private String ipAddr;
    private String osType;
    private String health;

    private XMetricsDouble1 cpuMetrics;
    private XMetricsDouble1 memMetrics;
    private XMetricsDouble1 diskMetrics;
    private XMetricsDouble2 netMetrics;

    private XHostDetail(String hostId, String ipAddr, String osType, String health, XMetricsDouble1 cpuMetrics, XMetricsDouble1 memMetrics, XMetricsDouble1 diskMetrics, XMetricsDouble2 netMetrics) {
        this.hostId = hostId;
        this.ipAddr = ipAddr;
        this.osType = osType;
        this.health = health;
        this.cpuMetrics = cpuMetrics;
        this.memMetrics = memMetrics;
        this.diskMetrics = diskMetrics;
        this.netMetrics = netMetrics;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public String getHostId() {
        return hostId;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getOsType() {
        return osType;
    }

    public String getHealth() {
        return health;
    }

    public XMetricsDouble1 getCpuMetrics() {
        return cpuMetrics;
    }

    public XMetricsDouble1 getMemMetrics() {
        return memMetrics;
    }

    public XMetricsDouble1 getDiskMetrics() {
        return diskMetrics;
    }

    public XMetricsDouble2 getNetMetrics() {
        return netMetrics;
    }

    public static class Builder {
        private String hostId;
        private String ipAddr;
        private String osType;
        private String health;

        private XMetricsDouble1 cpuMetrics;
        private XMetricsDouble1 memMetrics;
        private XMetricsDouble1 diskMetrics;
        private XMetricsDouble2 netMetrics;

        public Builder() {
        }

        public Builder hostId(String hostId) {
            this.hostId = hostId == null ? DEFAULT_STRING : hostId;
            return this;
        }

        public Builder ipAddr(String ipAddr) {
            this.ipAddr = ipAddr == null ? DEFAULT_STRING : ipAddr;
            return this;
        }

        public Builder osType(String osType) {
            this.osType = osType == null ? DEFAULT_STRING : osType;
            return this;
        }

        public Builder health(String health) {
            this.health = health != null ? health : "NORMAL";
            return this;
        }

        public Builder cpuMetrics(XMetricsDouble1 cpuMetrics) {
            this.cpuMetrics = cpuMetrics;
            return this;
        }

        public Builder memMetrics(XMetricsDouble1 memMetrics) {
            this.memMetrics = memMetrics;
            return this;
        }

        public Builder diskMetrics(XMetricsDouble1 diskMetrics) {
            this.diskMetrics = diskMetrics;
            return this;
        }

        public Builder netMetrics(XMetricsDouble2 netMetrics) {
            this.netMetrics = netMetrics;
            return this;
        }

        public XHostDetail build() {
            return new XHostDetail(hostId, ipAddr, osType, health, cpuMetrics, memMetrics, diskMetrics, netMetrics);
        }
    }
}
