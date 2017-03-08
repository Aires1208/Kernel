package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.report.usercase.HealthLevel;

/**
 * Created by root on 16-8-29.
 */
public class XTransactionEvent extends XEvent {
    private String traceName;

    public XTransactionEvent(String eventName, String eventTime, HealthLevel level, String eventDetail, String traceName) {
        super(eventName, eventTime, level, eventDetail);
        this.traceName = traceName;
    }

    public String getTraceName() {
        return traceName;
    }
}
