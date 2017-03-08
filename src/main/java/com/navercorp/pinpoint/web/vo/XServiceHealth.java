package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 16-8-24.
 */
public class XServiceHealth {
    private String serviceName;
    private Integer calls;
    private double healthScore;

    public XServiceHealth(String serviceName, Integer calls, double healthScore) {
        this.serviceName = serviceName;
        this.calls = calls;
        this.healthScore = healthScore;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getCalls() {
        return calls;
    }

    public double getHealthScore() {
        return healthScore;
    }
}
