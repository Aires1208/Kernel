package com.navercorp.pinpoint.web.vo;

public class XMetrics {
    private String info;
    private String[] timestamps;
    private Integer[] dataPoints;

    public XMetrics(String info, String[] timestamps, Integer[] data) {
        this.info = info;
        this.timestamps = timestamps;
        this.dataPoints = data;
    }


    public String getInfo() {
        return info;
    }

    public String[] getTimestamps() {
        return timestamps;
    }


    public Integer[] getDataPoints() {
        return dataPoints;
    }
}
