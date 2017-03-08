package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 5/26/16.
 */
public class TransactionHealth {
    private int critical;
    private int warning;
    private int normal;

    public TransactionHealth() {

    }

    public TransactionHealth(int critical, int warning, int normal) {
        this.critical = critical;
        this.warning = warning;
        this.normal = normal;
    }


    public int getCritical() {
        return critical;
    }

    public int getWarning() {
        return warning;
    }

    public int getNormal() {
        return normal;
    }
}
