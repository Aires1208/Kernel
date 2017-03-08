package com.navercorp.pinpoint.web.vo;

/**
 * Created by root on 16-8-29.
 */
public enum ErrorsType {
    CALLS_HEAVY(1, "calls heavy"),
    CALLS_OVER_BASELINE(2, "calls over baseline"),
    ERRORS_HEAVY(3, "errors heavy"),
    ERRORS_OVER_BASELINE(4, "errors over baseline"),
    ERROR_RATIO_TOO_HIGH(5, "error ratio too high"),
    ERROR_RATIO_OVER_BASELINE(6, "error ratio over baseline"),
    RESPONSE_TIME_TOO_LONG(7, "response time too long"),
    RESPONSE_TIME_OVER_BASELINE(8, "responseTime over baseline"),
    JVM_HEAP_USAGE_EXCEPTION(9, "jvm heap usage exception"),
    GC_TIME_EXCEPTION(10, "gc time exception"),
    GC_FREQUENCY_EXCEPTION(11, "jvm heap usage exception"),
    CPU_USAGE_EXCEPTION(12, "cpu usage exception"),
    MEMORY_USAGE_EXCEPTION(13, "memory usage exception"),
    DISK_IO_EXCEPTION(14, "disk IO exception"),
    NETWORK_IO_EXCEPTION(15, "host network IO exception"),
    DB_OVERLOAD_WARNING(16, "db over load"),
    DB_TOO_MANY_DB_CONNECTIONS(17, "too many DB connections"),
    DB_TIME_SPENT_IN_DB_EXCEPTION(18, "time spent in DB exception");

    private final int errorCode;
    private final String errorName;

    ErrorsType(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorName = errorDesc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorName() {
        return errorName;
    }
}