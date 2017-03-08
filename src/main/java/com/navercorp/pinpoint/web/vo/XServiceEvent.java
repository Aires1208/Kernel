package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.report.usercase.HealthLevel;

/**
 * Created by root on 8/29/16.
 */
public class XServiceEvent extends XEvent{
    private String instance;

    public XServiceEvent(String eventTime, String eventName, HealthLevel eventLevel, String eventDetails, String instanceName) {
        super(eventName, eventTime, eventLevel, eventDetails);
        this.instance = instanceName;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
