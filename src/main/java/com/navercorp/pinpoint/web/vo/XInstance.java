package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.view.XInstanceSerializer;

import static com.google.common.collect.Lists.newArrayList;

@JsonSerialize(using = XInstanceSerializer.class)
public final class XInstance extends XComponent {
    private final String name;
    private final ServiceType serviceType;
    private final short code;
    private AgentInfo agentInfo;
    private int eventCount;

    private TransactionHealth transactionHealth = new TransactionHealth();
    private NodeHealth nodeHealth = new NodeHealth();

    public XInstance(String name, ServiceType serviceType) {
        if (name == null) {
            throw new NullPointerException("name must not be null. serviceType=" + serviceType);
        }
        if (serviceType == null) {
            throw new NullPointerException("serviceType must not be null. name=" + name);
        }
        this.name = name;
        this.serviceType = serviceType;
        this.code = serviceType.getCode();
        this.eventCount = 0;
    }

    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public String getName() {
        return name;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public short getServiceTypeCode() {
        return serviceType.getCode();
    }

    public short getCode() {
        return code;
    }

    public TransactionHealth getTransactionHealth() {

        return transactionHealth;
    }

    public void setTransactionHealth(TransactionHealth transactionHealth) {
        this.transactionHealth = transactionHealth;
    }

    public void setXNode(XNode xNode) {
        setXNodes(newArrayList(xNode));
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public NodeHealth getNodeHealth() {
        return nodeHealth;
    }

    public void setNodeHealth(NodeHealth nodeHealth) {
        this.nodeHealth = nodeHealth;
    }

    public boolean equals(String thatName, ServiceType thatServiceType) {
        if (thatName == null) {
            throw new NullPointerException("thatName must not be null");
        }
        if (thatServiceType == null) {
            throw new NullPointerException("thatServiceType must not be null");
        }
        if (serviceType != thatServiceType) return false;
        return name.equals(thatName);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XInstance that = (XInstance) o;

        if (serviceType != that.serviceType) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + serviceType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name + "(" + serviceType + ":" + code + ")";
    }
}
