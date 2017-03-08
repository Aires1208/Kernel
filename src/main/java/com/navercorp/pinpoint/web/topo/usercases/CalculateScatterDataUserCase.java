package com.navercorp.pinpoint.web.topo.usercases;

import com.navercorp.pinpoint.common.topo.domain.*;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by 10183966 on 7/21/16.
 */
public class CalculateScatterDataUserCase {

    private final int DEFAULT_DOT_NUMBER = 10;
    private long dividingFactor;
    private List<TopoLine> topoLines;
    private Range range;

    public CalculateScatterDataUserCase(List<TopoLine> topoLines, Range range) {
        this.topoLines = topoLines;
        this.range = range;
    }

    public List<Range> segmentationRang() {
        long from = range.getFrom();
        long to = range.getTo();
        List<Range> rangs = newArrayList();
        long xGroupUnitMillis;
        if (getDividingFactor() <= 0) {
            xGroupUnitMillis = (to - from) / DEFAULT_DOT_NUMBER;
        } else {
            xGroupUnitMillis = getDividingFactor();
        }
        while (from < to) {
            rangs.add(new Range(from, from + xGroupUnitMillis));
            from += xGroupUnitMillis;
        }
        return rangs;
    }


    public long getDividingFactor() {
        return dividingFactor;
    }

    public void setDividingFactor(long dividingFactor) {
        this.dividingFactor = dividingFactor;
    }

    public List<XDot> mergeTopoLinesGenerateXDots() {
        List<XDot> XDots = newArrayList();
        List<Range> ranges = segmentationRang();
        sortTopoLineList(topoLines);
        for (Range range : ranges) {
            generateXDots(XDots, range);
        }
        return XDots;
    }

    private void generateXDots(List<XDot> XDots, Range range) {
        long calls = 0;
        long errors = 0;
        long responseTime = 0;

        long begin = range.getFrom();
        long end = range.getTo();

        for (TopoLine topoLine : topoLines) {
            long timestamp = topoLine.getTimestamp();
            if (timestamp <= end && timestamp >= begin) {
                if (timestamp > end) {
                    continue;
                }
                List<XNode> xNodesPerTopoLine = topoLine.getXNodes();
                for (XNode xNode : xNodesPerTopoLine) {
                    calls += xNode.getCalls();
                    errors += xNode.getErrors();
                    responseTime += xNode.getResponseTime();
                }
            }
        }
        long acceptedTimeDiff = (begin + end) / 2;
        XMetric xMetric = new XMetricBuilder().Calls(calls).Errors(errors).Response(responseTime).build();
        XDot xDot = new XDot(acceptedTimeDiff);
        xDot.setxMetric(xMetric);
        XDots.add(xDot);
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    private void sortTopoLineList(List<TopoLine> topoLines) {
        XTopoLineComparable sort = new XTopoLineComparable();
        XTopoLineComparable.sortASC = true;
        Collections.sort(topoLines, sort);
    }
}
