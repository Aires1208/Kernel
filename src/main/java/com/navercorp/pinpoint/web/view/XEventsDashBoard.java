package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.common.events.ResultEvent;

import java.util.List;

/**
 * Created by root on 9/25/16.
 */
@JsonSerialize(using = XEventsDashBoardSerializer.class)
public class XEventsDashBoard {
    private List<XApplication> xApplications;
    private List<ResultEvent> resultEvents;

    public XEventsDashBoard(List<XApplication> xApplications) {
        this.xApplications = xApplications;
    }

    public XEventsDashBoard(List<XApplication> xApplications,List<ResultEvent> resultEvents) {
        this.xApplications = xApplications;
        this.resultEvents = resultEvents;
    }

    public List<XApplication> getxApplications() {
        return xApplications;
    }

    public void addResultEvents(List<ResultEvent> resultEvents) {
        this.resultEvents = resultEvents;
    }

    public List<ResultEvent> getResultEvents() {
        return resultEvents;
    }
}
