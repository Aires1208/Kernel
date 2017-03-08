package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.view.SqlInfoSerialize;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-9-14.
 */
@JsonSerialize(using = SqlInfoSerialize.class)
public class SqlInfo {
    private String sqlInfo;
    private String dbName;
    private int sqlId;
    private double avgElapsed;
    private int calls;
    private long maxElapsed;
    List<Sql> sqlList = newArrayList();

    public SqlInfo(String sqlInfo, String dbName, int sqlId, long avgElapsed, long maxElapsed, int calls, List<Sql> sqlList) {
        this.sqlInfo = sqlInfo;
        this.dbName = dbName;
        this.sqlId = sqlId;
        this.avgElapsed = avgElapsed;
        this.calls = calls;
        this.maxElapsed = maxElapsed;
        this.sqlList = sqlList;
    }

    public String getSqlInfo() {
        return sqlInfo;
    }

    public String getDbName() {
        return dbName;
    }

    public int getSqlId() {
        return sqlId;
    }

    public double getAvgElapsed() {
        return avgElapsed;
    }

    public int getCalls() {
        return calls;
    }

    public long getMaxElapsed() {
        return maxElapsed;
    }

    public List<Sql> getSqlList() {
        return sqlList;
    }

    public void setAvgElapsed(long avgElapsed) {
        this.avgElapsed = (this.avgElapsed + avgElapsed) / 2.00;
    }

    public void addCalls() {
        this.calls += 1;
    }

    public void setMaxElapsed(long maxElapsed) {
        this.maxElapsed = this.maxElapsed > maxElapsed ? this.maxElapsed : maxElapsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlInfo sqlInfo1 = (SqlInfo) o;

        if (sqlInfo != null ? !sqlInfo.equals(sqlInfo1.sqlInfo) : sqlInfo1.sqlInfo != null) return false;
        return dbName != null ? dbName.equals(sqlInfo1.dbName) : sqlInfo1.dbName == null;

    }

    @Override
    public int hashCode() {
        int result = sqlInfo != null ? sqlInfo.hashCode() : 0;
        result = 31 * result + (dbName != null ? dbName.hashCode() : 0);
        return result;
    }
}
