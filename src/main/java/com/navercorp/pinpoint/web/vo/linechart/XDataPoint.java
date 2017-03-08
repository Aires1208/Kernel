package com.navercorp.pinpoint.web.vo.linechart;

public class XDataPoint <Y extends Number> {
    private final String x;
    private final Y y;
    public XDataPoint(String x, Y y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public Y getY() {
        return y;
    }
}
