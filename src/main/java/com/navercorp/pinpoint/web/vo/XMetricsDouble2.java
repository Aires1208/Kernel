package com.navercorp.pinpoint.web.vo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XMetricsDouble2 {
    private String info;
    private List<String> timestamps = newArrayList();
    private List<Double> in = newArrayList();
    private List<Double> out = newArrayList();

    public XMetricsDouble2(String info, List<String> timestamps, List<Double> in, List<Double> out) {
        this.info = info;
        this.timestamps = timestamps;
        this.in = in;
        this.out = out;
    }

    public String getInfo() {
        return info;
    }

    public List<String> getTimestamps() {
        return timestamps;
    }

    public List<Double> getIn() {
        return in;
    }

    public List<Double> getOut() {
        return out;
    }
}
