package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.web.view.XServiceTopoSerializer;

import java.util.List;

@JsonSerialize(using = XServiceTopoSerializer.class)
public class XServiceTopo {
    private List<XNode> xNodes;
    private List<XLink> xLinks;

    public XServiceTopo(List<XNode> xNodes, List<XLink> xLinks) {
        if (xNodes == null || xLinks == null) {
            throw new NullPointerException("xNodes or xLinks must not be null");
        }
        this.xNodes = xNodes;
        this.xLinks = xLinks;
    }

    public List<XNode> getXNodes() {
        return xNodes;
    }

    public List<XLink> getXLinks() {
        return xLinks;
    }

    public void setNodes(List<XNode> nodes) {
        this.xNodes = nodes;
    }

    public void setLinks(List<XLink> links) {
        this.xLinks = links;
    }
}

