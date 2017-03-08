package com.navercorp.pinpoint.web.vo;

import java.util.List;

/**
 * Created by root on 16-8-3.
 */
public class XMetricsDouble1 {
    private String info;
    private List<String> timestamps;
    private List<Double> datas;

    public XMetricsDouble1(String info, List<String> timestamps, List<Double> datas) {
        this.info = info;
        this.timestamps = timestamps;
        this.datas = datas;
    }

    public String getInfo() {
        return info;
    }

    public List<String> getTimestamps() {
        return timestamps;
    }

    public List<Double> getDatas() {
        return datas;
    }
}
