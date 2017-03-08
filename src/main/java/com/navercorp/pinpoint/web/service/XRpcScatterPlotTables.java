package com.navercorp.pinpoint.web.service;

/**
 * Created by root on 16-12-7.
 */
public class XRpcScatterPlotTables {

    private Long start_time;
    private String rpc;
    private String method;
    private int elapsed;
    private Long operationID;

    public XRpcScatterPlotTables(Long start_time, String rpc, String method, int elapsed, Long operationID) {
        this.start_time = start_time;
        this.rpc = rpc;
        this.method = method;
        this.elapsed = elapsed;
        this.operationID = operationID;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public Long getOperationID() {
        return operationID;
    }

    public void setOperationID(Long operationID) {
        this.operationID = operationID;
    }

    @Override
    public String toString() {
        return "XRpcScatterPlotTables{" +
                "start_time=" + start_time +
                ", rpc='" + rpc + '\'' +
                ", method='" + method + '\'' +
                ", elapsed=" + elapsed +
                ", operationID=" + operationID +
                '}';
    }
}
