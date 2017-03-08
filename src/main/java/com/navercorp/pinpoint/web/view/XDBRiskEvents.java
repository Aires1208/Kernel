package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.SqlInfo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-8.
 */
@JsonSerialize(using = XDBRiskEventsSerialize.class)
public class XDBRiskEvents {
    List<SqlInfo> xdbRiskEvents = newArrayList();

    public XDBRiskEvents(List<SqlInfo> xdbRiskEvents) {
        this.xdbRiskEvents = xdbRiskEvents;
    }

    public List<SqlInfo> getXdbRiskEvents() {
        return xdbRiskEvents;
    }
}
