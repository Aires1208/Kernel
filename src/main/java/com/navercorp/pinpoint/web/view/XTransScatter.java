package com.navercorp.pinpoint.web.view;

/**
 * Created by root on 8/2/16.
 */
public class XTransScatter {
    private long startTime;
    private long response;

    public XTransScatter(long startTime, long response) {
        this.startTime = startTime;
        this.response = response;
    }

    public long getResponse() {
        return response;
    }

    public long getStartTime() {
        return startTime;
    }
}
