package com.navercorp.pinpoint.web.vo;

/**
 * Created by ${aires} on 11/29/16.
 */
public class VoXRpc {
    private String method;
    private int count;
    private String success;
    private long min_time;
    private long max_time;
    private long duration;
    private long avg_time;
    private String rpc;

    public VoXRpc(String method, int count, String success, long min_time, long max_time, long duration, long avg_time, String rpc) {
        this.method = method;
        this.count = count;
        this.success = success;
        this.min_time = min_time;
        this.max_time = max_time;
        this.duration = duration;
        this.avg_time = avg_time;
        this.rpc = rpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public long getMin_time() {
        return min_time;
    }

    public void setMin_time(long min_time) {
        this.min_time = min_time;
    }

    public long getMax_time() {
        return max_time;
    }

    public void setMax_time(long max_time) {
        this.max_time = max_time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAvg_time() {
        return avg_time;
    }

    public void setAvg_time(long avg_time) {
        this.avg_time = avg_time;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    @Override
    public String toString() {
        return "VoXRpc{" +
                "method='" + method + '\'' +
                ", count=" + count +
                ", success='" + success + '\'' +
                ", min_time=" + min_time +
                ", max_time=" + max_time +
                ", duration=" + duration +
                ", avg_time=" + avg_time +
                ", rpc='" + rpc + '\'' +
                '}';
    }
}
