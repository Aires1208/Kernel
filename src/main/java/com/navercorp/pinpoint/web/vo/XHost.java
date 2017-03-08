package com.navercorp.pinpoint.web.vo;


import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

public class XHost {
    private static final String HOSTID_SEPERATOR = "@";
    private static final String NOT_AVAILABLE = "N/A";

    private String ipAddr;
    private String mac;
    private String hostname;
    private String osType;
    private Set<AgentInfo> agents = newHashSet();

    private XHost(String ipAddr, String mac, String hostname, String osType, Set<AgentInfo> agentIds) {
        this.ipAddr = ipAddr;
        this.mac = mac;
        this.hostname = hostname;
        this.osType = osType;
        this.agents = agentIds;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getMac() {
        return mac;
    }

    public String getHostname() {
        return hostname;
    }

    public String getOsType() {
        return osType;
    }

    public Set<String> getAgentIds() {
        Set<String> agentIds = newHashSet();
        agentIds.addAll(agents.stream().map(AgentInfo::getAgentId).collect(Collectors.toList()));
        return agentIds;
    }

    public Set<AgentInfo> getAgents() {
        return null != agents ? agents : newHashSet();
    }

    public Set<String> getServices() {
        Set<String> services = newHashSet();
        services.addAll(agents.stream().map(AgentInfo::getApplicationName).collect(Collectors.toList()));
        return services;
    }

    public String getHostId() {
        Optional<String> tmpMac = Optional.of(mac);
        return tmpMac.orElse(NOT_AVAILABLE) + HOSTID_SEPERATOR + hostname;
    }

    public String parseMac(String hostId) {
        String[] parts = hostId.split(HOSTID_SEPERATOR);
        return parts.length == 2 ? parts[0] : NOT_AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XHost)) {
            return false;
        }

        XHost host = (XHost) o;

        return getMac().equals(host.getMac());
    }

    @Override
    public int hashCode() {
        return getMac().hashCode();
    }

    public static class Builder {
        private String ipAddr;
        private String mac;
        private String hostname;
        private String osType;
        private Set<AgentInfo> agents;

        public Builder() {
        }

        public Builder ipAddr(String ipAddr) {
            this.ipAddr = null != ipAddr ? ipAddr : "";
            return this;
        }

        public Builder mac(String mac) {
            this.mac = null != mac ? mac : "";
            return this;
        }

        public Builder hostname(String hostname) {
            this.hostname = null != hostname ? hostname : "";
            return this;
        }

        public Builder osType(String osType) {
            this.osType = null != osType ? osType : "";
            return this;
        }

        public Builder agents(Set<AgentInfo> agentIds) {
            this.agents = CollectionUtils.isEmpty(agentIds) ? newHashSet() : agentIds;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Builder builder = (Builder) o;

            return mac.equals(builder.mac);

        }

        @Override
        public int hashCode() {
            return mac.hashCode();
        }

        public XHost build() {
            return new XHost(ipAddr, mac, hostname, osType, agents);
        }
    }
}
