package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.XServiceHealthEvent;

import java.util.List;


@JsonSerialize(using = XServiceHealthEventsSerializer.class)
public class XServiceHealthEvents {
    private List<String> appList;
    private String appName;
    private List<XServiceHealthEvent> serviceInfoList;

    public XServiceHealthEvents(List<String> appList, String appName, List<XServiceHealthEvent> serviceInfoList) {
        this.appList = appList;
        this.appName = appName;
        this.serviceInfoList = serviceInfoList;
    }

    public List<String> getAppList() {
        return appList;
    }

    public String getAppName() {
        return appName;
    }

    public List<XServiceHealthEvent> getServiceInfoList() {
        return serviceInfoList;
    }

}
