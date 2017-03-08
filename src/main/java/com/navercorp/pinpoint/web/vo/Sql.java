package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 16-9-30.
 */
public class Sql {
    private String sqlInfo;
    private long startTime;
    private long elapsed;
    private String transactionName;
    private String transactionId;
    private String DBName;

    public Sql(String sqlInfo, long startTime, long elapsed, String transactionName, String transactionId, String DBName) {
        this.sqlInfo = sqlInfo;
        this.startTime = startTime;
        this.elapsed = elapsed;
        this.transactionName = transactionName;
        this.transactionId = transactionId;
        this.DBName = DBName;
    }

    public String getSqlInfo() {
        return sqlInfo;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsed() {
        return elapsed;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDBName() {
        return DBName;
    }
}
