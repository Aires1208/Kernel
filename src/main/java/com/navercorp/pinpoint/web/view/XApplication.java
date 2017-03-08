package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.web.vo.TransactionHealth;
import com.navercorp.pinpoint.web.vo.XComponent;
import com.navercorp.pinpoint.web.vo.XService;

import java.util.List;

@JsonSerialize(using = XApplicationSerializer.class)
public class XApplication extends XComponent {
    private String name;
    private List<XService> xServices;
    private TransactionHealth transactionHealth = new TransactionHealth();
    private NodeHealth serviceHealth = new NodeHealth();
    private int eventCount;

    public XApplication(String name, List<XService> xServices) {
        this.name = name;
        this.xServices = xServices;
    }

    public String getName() {
        return name;
    }

    public List<XService> getXServices() {
        return xServices;
    }

    public NodeHealth getServiceHealth() {
        return serviceHealth;
    }

    public void setServiceHealth(NodeHealth serviceHealth) {
        this.serviceHealth = serviceHealth;
    }

    public TransactionHealth getTransactionHealth() {
        return transactionHealth;
    }

    public void setTransactionHealth(TransactionHealth transactionHealth) {
        this.transactionHealth = transactionHealth;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    @Override
    public String toString() {
        return "XApplication{" +
                "name='" + name + '\'' +
                ", xServices=" + xServices +
                ", transactionHealth=" + transactionHealth +
                '}';
    }
}
