package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.events.ResultEvent;

import java.util.List;

/**
 * Created by root on 8/30/16.
 */
public class XServiceHealthEvent {
    private String serviceName;
    private double serviceScores;
    private List<ResultEvent> eventList;

    public XServiceHealthEvent(String serviceName, double serviceScores, List<ResultEvent> eventInfoList) {
        this.serviceName = serviceName;
        this.serviceScores = serviceScores;
        this.eventList = eventInfoList;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getServiceScores() {
        return serviceScores;
    }

    public int getEventCount() {
        return eventList.size();
    }

    public List<ResultEvent> getEventList() {
        return eventList;
    }

}
