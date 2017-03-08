package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-16.
 */
@JsonSerialize(using = XFSDetailSerializer.class)
public class XFSDetail {
    private Range range;
    private ESQueryResult fileStatics;
    private ESQueryResult fileUsages;

    public XFSDetail(Range range, ESQueryResult fileStatics, ESQueryResult fileUsages) {
        this.range = range;
        this.fileStatics = fileStatics;
        this.fileUsages = fileUsages;
    }

    public Range getRange() {
        return range;
    }

    public ESQueryResult getFileStatics() {
        return fileStatics;
    }

    public ESQueryResult getFileUsages() {
        return fileUsages;
    }
}
