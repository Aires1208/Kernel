package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-16.
 */
@JsonSerialize(using = XNetDetailSerializer.class)
public class XNetDetail {
    private Range range;
    private ESQueryResult netStatics;
    private ESQueryResult netUsages;

    public XNetDetail(Range range, ESQueryResult netStatics, ESQueryResult netUsages) {
        this.range = range;
        this.netStatics = netStatics;
        this.netUsages = netUsages;
    }

    public Range getRange() {
        return range;
    }

    public ESQueryResult getNetStatics() {
        return netStatics;
    }

    public ESQueryResult getNetUsages() {
        return netUsages;
    }
}
