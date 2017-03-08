package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.XInstanceEvent;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-9-5.
 */
@JsonSerialize(using = XServiceCallsEventSerializer.class)
public class XServiceCallsEvent {
    private String svcName;
    private double callsPermin;
    private List<XInstanceEvent> events = newArrayList();

    public XServiceCallsEvent(String svcName, double callsPermin, List<XInstanceEvent> events) {
        this.svcName = svcName;
        this.callsPermin = callsPermin;
        this.events = events;
    }

    public String getSvcName() {
        return svcName;
    }

    public double getCallsPermin() {
        return callsPermin;
    }

    public List<XInstanceEvent> getEvents() {
        return events;
    }
}
