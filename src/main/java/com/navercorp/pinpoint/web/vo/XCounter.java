package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 6/1/16.
 */
public class XCounter {
    private  int calls;
    private long responseTime;
    private int errors;

    public XCounter() {
    }


    public void increase(long l) {
        calls++;
        responseTime += l;
    }

    public long getAvg(){
        return calls == 0 ? 0 : responseTime/ calls;
    }

    public void increaseErrors() {
        errors++;
    }


    public int getCalls() {
        return calls;
    }

    public long getErrors() {
        return errors;
    }
}
