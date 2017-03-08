package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 16-10-17.
 */
public class XTransactionName {
    private String serviceName;
    private String transactionName;
    private long lastTime;
    private String agentId;

    public XTransactionName(String serviceName, String transactionName, long lastTime, String agentId) {
        this.serviceName = serviceName;
        this.transactionName = transactionName;
        this.lastTime = lastTime;
        this.agentId = agentId;
    }

    public XTransactionName(String serviceName, String agentId, String transactionName) {
        this.serviceName = serviceName;
        this.transactionName = transactionName;
        this.agentId = agentId;
        this.lastTime = 0L;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public long getLastTime() {
        return lastTime;
    }

    public String getAgentId() {
        return agentId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XTransactionName name = (XTransactionName) o;

        if (lastTime != name.lastTime) return false;
        if (serviceName != null ? !serviceName.equals(name.serviceName) : name.serviceName != null) return false;
        if (transactionName != null ? !transactionName.equals(name.transactionName) : name.transactionName != null)
            return false;
        return agentId != null ? agentId.equals(name.agentId) : name.agentId == null;

    }

    @Override
    public final int hashCode() {
        int result = serviceName != null ? serviceName.hashCode() : 0;
        result = 31 * result + (transactionName != null ? transactionName.hashCode() : 0);
        result = 31 * result + (int) (lastTime ^ (lastTime >>> 32));
        result = 31 * result + (agentId != null ? agentId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "XTransactionName{" +
                "serviceName='" + serviceName + '\'' +
                ", transactionName='" + transactionName + '\'' +
                ", lastTime=" + lastTime +
                ", agentId='" + agentId + '\'' +
                '}';
    }
}
