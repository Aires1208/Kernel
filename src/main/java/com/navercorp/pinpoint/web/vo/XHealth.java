package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XHealth {
    private String info = "";
    private double score;
    private Integer critical;
    private boolean isSetCriticals = false;
    private Integer warning;
    private boolean isSetWarnnings = false;
    private List<String> timestamps = newArrayList();
    private List<Double> subHealths = newArrayList();

    public XHealth(double score) {
        this.score = score;
    }

    public String getInfo() {
        return info;
    }

    public double getScore() {
        return score;
    }

    public boolean isSetCriticals() {
        return isSetCriticals;
    }

    public boolean isSetWarnnings() {
        return isSetWarnnings;
    }

    public Integer getCritical() {
        return critical;
    }

    public void setCritical(Integer critical) {
        this.isSetCriticals = true;
        this.critical = critical;
    }

    public Integer getWarning() {
        return warning;
    }

    public void setWarning(Integer warning) {
        this.isSetWarnnings = true;
        this.warning = warning;
    }

    public List<String> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<String> timestamps) {
        this.timestamps = timestamps;
    }

    public List<Double> getHealths() {
        return subHealths;
    }

    public void setHealths(List<Double> healths) {
        this.subHealths = healths;
    }
}
