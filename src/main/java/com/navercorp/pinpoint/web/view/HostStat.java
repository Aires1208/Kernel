package com.navercorp.pinpoint.web.view;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class HostStat {
    private String hostId;
    private String ipAddr;
    private String osType;
    private String health;
    private boolean isDocker;

    private Set<String> services = newHashSet();
    private Set<StatLine> statLines = newHashSet();

    private HostStat(String hostId, String ipAddr, String osType, String health, Set<String> services, Set<StatLine> statLines, boolean isDocker) {
        this.hostId = hostId;
        this.ipAddr = ipAddr;
        this.osType = osType;
        this.health = health;
        this.services = services;
        this.statLines = statLines;
        this.isDocker = isDocker;
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

    public Set<String> getServices() {
        return services;
    }

    public boolean isDocker() {
        return isDocker;
    }

    public Set<StatLine> getStatLines() {
        return statLines;
    }

    public boolean hasStat() {
        return statLines.isEmpty();
    }

    public double getCpu() {
        double cpuUsage = 0.00;
        for (StatLine statLine : statLines) {
            cpuUsage += statLine.getCpuUsage();
        }

        return statLines.isEmpty() ? 0.00 : cpuUsage / (double) statLines.size();
    }

    public double getMem() {
        double memUsage = 0.00;
        for (StatLine statLine : statLines) {
            memUsage += statLine.getMemUsage();
        }

        return statLines.isEmpty() ? 0.00 : memUsage / (double) statLines.size();
    }

    public double getDisk() {
        double diskUsage = 0.00;
        for (StatLine statLine : statLines) {
            diskUsage += statLine.getDiskUsage();
        }

        return statLines.isEmpty() ? 0.00 : diskUsage / (double) statLines.size();
    }

    public double getNet() {
        double netUsage = 0.00;
        for (StatLine statLine : statLines) {
            netUsage += statLine.getNetUsage();
        }

        return statLines.isEmpty() ? 0.00 : netUsage / (double) statLines.size();
    }

    @Override
    public String toString() {
        return "HostStat{" +
                "hostId='" + hostId + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", osType='" + osType + '\'' +
                ", health='" + health + '\'' +
                ", services=" + services +
                ", statLines=" + statLines +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HostStat that = (HostStat) o;

        return hostId.equals(that.hostId) && ipAddr.equals(that.ipAddr) && osType.equals(that.osType);

    }

    @Override
    public int hashCode() {
        int result = hostId.hashCode();
        result = 31 * result + ipAddr.hashCode();
        result = 31 * result + osType.hashCode();
        return result;
    }

    public static class Builder {
        private String hostId;
        private String ipAddr;
        private String osType;
        private String health;
        private boolean isDocker;

        private Set<String> services = newHashSet();
        private Set<StatLine> statLines = newHashSet();

        public Builder() {
        }

        public Builder hostId(String hostName) {
            this.hostId = hostName != null ? hostName : "";
            return this;
        }

        public Builder ipAddr(String ipAddr) {
            this.ipAddr = ipAddr != null ? ipAddr : "127.0.0.1";
            return this;
        }

        public Builder osType(String osType) {
            this.osType = osType != null ? osType : "";
            return this;
        }

        public Builder isDocker(boolean isDocker) {
            this.isDocker = isDocker;
            return this;
        }

        public Builder health(String health) {
            this.health = health != null ? health : "NORMAL";
            return this;
        }

        public Builder statLines(Set<StatLine> statLines) {
            this.statLines = statLines != null ? statLines : newHashSet();
            return this;
        }

        public Builder services(Set<String> services) {
            this.services = services != null ? services : newHashSet();
            return this;
        }

        public HostStat build() {
            return new HostStat(hostId, ipAddr, osType, health, services, statLines, isDocker);
        }
    }
}
