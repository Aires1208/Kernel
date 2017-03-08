package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-17.
 */
@JsonSerialize(using = XProcessDetailSerializer.class)
public class XProcessDetail {
    private Range range;
    private ESQueryResult topNUsage;
    private ESQueryResult topNTime;
    private ESQueryResult topNVirt;

    private XProcessDetail(Range range, ESQueryResult topNUsage, ESQueryResult topNTime, ESQueryResult topNVirt) {
        this.range = range;
        this.topNUsage = topNUsage;
        this.topNTime = topNTime;
        this.topNVirt = topNVirt;
    }

    public Range getRange() {
        return range;
    }

    public ESQueryResult getTopNUsage() {
        return topNUsage;
    }

    public ESQueryResult getTopNTime() {
        return topNTime;
    }

    public ESQueryResult getTopNVirt() {
        return topNVirt;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private Range range;
        private ESQueryResult topNUsage;
        private ESQueryResult topNTime;
        private ESQueryResult topNVirt;

        public Builder Range(Range range) {
            this.range = range;
            return this;
        }

        public Builder TopNUsage(ESQueryResult topNUsage) {
            this.topNUsage = topNUsage;
            return this;
        }

        public Builder TopNTime(ESQueryResult topNTime) {
            this.topNTime = topNTime;
            return this;
        }

        public Builder TopNVirt(ESQueryResult topNVirt) {
            this.topNVirt = topNVirt;
            return this;
        }

        public XProcessDetail build() {
            return new XProcessDetail(range, topNUsage, topNTime, topNVirt);
        }
    }
}
