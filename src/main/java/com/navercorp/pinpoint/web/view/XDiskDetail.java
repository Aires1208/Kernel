package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-15.
 */
@JsonSerialize(using = XDiskDetailSerializer.class)
public class XDiskDetail {
    private Range range;
    private ESQueryResult metrics;
    private ESQueryResult diskInfo;

    public XDiskDetail(Range range, ESQueryResult metrics, ESQueryResult diskInfo) {
        this.range = range;
        this.metrics = metrics;
        this.diskInfo = diskInfo;
    }

    public Range getRange() {
        return range;
    }

    public ESQueryResult getMetrics() {
        return metrics;
    }

    public ESQueryResult getDiskInfo() {
        return diskInfo;
    }
}
