package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.vo.XInstance;
import com.navercorp.pinpoint.web.vo.XLoadInfo;
import com.navercorp.pinpoint.web.vo.XServiceTopo;

@JsonSerialize(using = XInstanceDashBoardSerializer.class)
public class XInstanceDashBoard {

    private final XInstance xInstance;
    private final XServiceTopo xServiceTopo;
    private XLoadInfo xLoadInfo;

    public XInstanceDashBoard(XInstance xInstance, XServiceTopo xServiceTopo,
                              XLoadInfo xLoadInfo) {
        Preconditions.checkArgument(xInstance != null && xServiceTopo != null,
                new NullPointerException("instance or application topology  must not be null"));

        this.xInstance = xInstance;
        this.xServiceTopo = xServiceTopo;
        this.xLoadInfo = xLoadInfo;
    }


    public XServiceTopo getTopo() {
        return xServiceTopo;
    }

    public XInstance getxService() {
        return xInstance;
    }

    public XLoadInfo getxLoadInfo() {
        return xLoadInfo;
    }
}
