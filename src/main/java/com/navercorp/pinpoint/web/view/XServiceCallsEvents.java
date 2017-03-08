package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-9-5.
 */
@JsonSerialize(using = XServiceCallsEventsSerializer.class)
public class XServiceCallsEvents {
    private String appName;
    private List<String > appList = newArrayList();
    private List<XServiceCallsEvent> events = newArrayList();

    public XServiceCallsEvents(String appName, List<String> appList, List<XServiceCallsEvent> events) {
        this.appName = appName;
        this.appList = appList;
        this.events = events;
    }

    public String getAppName() {
        return appName;
    }

    public List<String> getAppList() {
        return appList;
    }

    public List<XServiceCallsEvent> getEvents() {
        return events;
    }
}
