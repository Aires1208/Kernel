package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.report.usercase.HealthLevel;

/**
 * Created by root on 16-8-29.
 */
public class XEvent {
    private String eventName;
    private String eventTime;
    private HealthLevel level;
    private String eventDetail;

    public XEvent(String eventName, String eventTime, HealthLevel level, String eventDetail) {
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.level = level;
        this.eventDetail = eventDetail;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public HealthLevel getLevel() {
        return level;
    }

    public String getEventDetail() {
        return eventDetail;
    }
}
