package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-9.
 */
@JsonSerialize(using = XMemoryDetailSerializer.class)
public class XMemoryDetail {
    private Range range;
    private ESQueryResult memMetaData;

    public XMemoryDetail(Range range, ESQueryResult memMetaData) {
        this.range = range;
        this.memMetaData = memMetaData;
    }

    public Range getRange() {
        return range;
    }

    public ESQueryResult getMemMetaData() {
        return memMetaData;
    }
}
