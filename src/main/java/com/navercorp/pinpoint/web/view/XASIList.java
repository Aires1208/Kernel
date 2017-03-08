package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-10.
 */
@JsonSerialize(using = XASIListSerialize.class)
public class XASIList {
    List<XApplication>  xApplications = newArrayList();

    public List<XApplication> getxApplications() {
        return xApplications;
    }

    public XASIList(List<XApplication> xApplications) {

        this.xApplications = xApplications;
    }
}
