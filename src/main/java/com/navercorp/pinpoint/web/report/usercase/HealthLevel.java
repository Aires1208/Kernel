package com.navercorp.pinpoint.web.report.usercase;

public enum HealthLevel {
    NORMAL(0, "Normal"),
    WARNING(1, "Warning"),
    CRITICAL(2, "Critical");

    private final int code;
    private final String desc;

    HealthLevel(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
