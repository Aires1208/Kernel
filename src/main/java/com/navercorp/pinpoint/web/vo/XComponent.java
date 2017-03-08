package com.navercorp.pinpoint.web.vo;


import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.common.topo.domain.XNode;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 7/23/16.
 */
public abstract class XComponent {
    private List<XNode> xNodes = newArrayList();
    private Range range = new Range(0, 0);

    public void setXNodes(List<XNode> xNodes) {
        this.xNodes = xNodes;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public long getCalls() {
        long calls = 0;
        for (XNode xNode : xNodes) {
            calls += xNode.getCalls();
        }
        return calls;
    }

    public double getCallsPerMin() {
        double tmpCalls = getCalls();

        double mins = range.getRange() / (1000 * 60);
        return mins > 0 ? tmpCalls / mins : 0.00;
    }

    public double getErrorsPerMin() {
        double mins = range.getRange() / (1000 * 60);
        return mins > 0 ? getErrors() / mins : 0.00;
    }

    public double getErrorsPercent() {
        double tmpCalls = getCalls();
        return tmpCalls > 0 ? getErrors() / tmpCalls : 0.00;
    }

    public long getErrors() {
        long errors = 0;
        for (XNode xNode : xNodes) {
            errors += xNode.getErrors();
        }
        return errors;
    }

    public double getResponse() {
        double curResponse = 0.00;
        for (XNode xNode : xNodes) {
            curResponse += xNode.getResponseTime();
        }

        double calls = getCalls();
        return calls > 0 ? curResponse / calls : 0.00;
    }

//    public NodeHealth getNodeHealth() {
//        long critical = 0;
//        long warning = 0;
//        long normal = 0;
//
//        for (XNode xNode : xNodes) {
//            NodeHealth nodeHealth = xNode.getNodeHealth();
//            critical += nodeHealth.getCritical();
//            warning += nodeHealth.getWarning();
//            normal += nodeHealth.getNormal();
//        }
//
//        return new NodeHealth(critical, warning, normal);
//    }

}
