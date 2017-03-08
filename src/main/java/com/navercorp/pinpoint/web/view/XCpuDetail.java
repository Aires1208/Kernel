package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-10.
 */
@JsonSerialize(using = XCpuDetailSerializer.class)
public class XCpuDetail {
    private Range range;
    private ESQueryResult cpuStatics;
    private ESQueryResult top5Cpu;
    private ESQueryResult metrics;

    private XCpuDetail(Range range, ESQueryResult cpuStatics, ESQueryResult top5Cpu, ESQueryResult metrics) {
        this.range = range;
        this.cpuStatics = cpuStatics;
        this.top5Cpu = top5Cpu;
        this.metrics = metrics;
    }

    public Range getRange() {
        return range;
    }

    public ESQueryResult getCpuStatics() {
        return cpuStatics;
    }

    public ESQueryResult getTop5Cpu() {
        return top5Cpu;
    }

    public ESQueryResult getMetrics() {
        return metrics;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private Range range;
        private ESQueryResult cpuStatics;
        private ESQueryResult topNCpu;
        private ESQueryResult metrics;

        public Builder Range(Range range) {
            this.range = range;
            return this;
        }

        public Builder CpuStatics(ESQueryResult cpuStatics) {
            this.cpuStatics = cpuStatics;
            return this;
        }

        public Builder TopNCpu(ESQueryResult topNCpu) {
            this.topNCpu = topNCpu;
            return this;
        }

        public Builder Metrics(ESQueryResult metrics) {
            this.metrics = metrics;
            return this;
        }

        public XCpuDetail build() {
            return new XCpuDetail(range, cpuStatics, topNCpu, metrics);
        }
    }
}
