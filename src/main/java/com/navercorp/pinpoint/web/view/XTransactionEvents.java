package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.XTransactionEvent;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-8-29.
 */
@JsonSerialize(using = XTransactionEventsSerializer.class)
public class XTransactionEvents {
    private String appName;
    private List<String> appList = newArrayList();
    private List<XTransactionEvent> eventList = newArrayList();

    public XTransactionEvents(String appName, List<String> appList, List<XTransactionEvent> eventList) {
        this.appName = appName;
        this.appList = appList;
        this.eventList = eventList;
    }

    public String getAppName() {
        return appName;
    }

    public List<String> getAppList() {
        return appList;
    }

    public List<XTransactionEvent> getEventList() {
        return eventList;
    }
}
