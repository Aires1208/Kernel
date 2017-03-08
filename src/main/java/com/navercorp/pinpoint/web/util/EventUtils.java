package com.navercorp.pinpoint.web.util;

import com.navercorp.pinpoint.web.report.usercase.HealthLevel;
import com.navercorp.pinpoint.web.vo.ErrorsType;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.navercorp.pinpoint.web.vo.ErrorsType.*;

public class EventUtils {
    private static Map<Integer, ErrorsType> errorMap = newHashMap();

    static {
        errorMap.put(CALLS_HEAVY.getErrorCode(), CALLS_HEAVY);
        errorMap.put(CALLS_OVER_BASELINE.getErrorCode(), CALLS_OVER_BASELINE);
        errorMap.put(ERRORS_HEAVY.getErrorCode(), ERRORS_HEAVY);
        errorMap.put(ERRORS_OVER_BASELINE.getErrorCode(), ERRORS_OVER_BASELINE);
        errorMap.put(ERROR_RATIO_TOO_HIGH.getErrorCode(), ERROR_RATIO_TOO_HIGH);
        errorMap.put(ERROR_RATIO_OVER_BASELINE.getErrorCode(), ERROR_RATIO_OVER_BASELINE);
        errorMap.put(RESPONSE_TIME_TOO_LONG.getErrorCode(), RESPONSE_TIME_TOO_LONG);
        errorMap.put(RESPONSE_TIME_OVER_BASELINE.getErrorCode(), RESPONSE_TIME_OVER_BASELINE);
        errorMap.put(JVM_HEAP_USAGE_EXCEPTION.getErrorCode(), JVM_HEAP_USAGE_EXCEPTION);
        errorMap.put(GC_TIME_EXCEPTION.getErrorCode(), GC_TIME_EXCEPTION);
        errorMap.put(GC_FREQUENCY_EXCEPTION.getErrorCode(), GC_FREQUENCY_EXCEPTION);
        errorMap.put(CPU_USAGE_EXCEPTION.getErrorCode(), CPU_USAGE_EXCEPTION);
        errorMap.put(MEMORY_USAGE_EXCEPTION.getErrorCode(), MEMORY_USAGE_EXCEPTION);
        errorMap.put(DISK_IO_EXCEPTION.getErrorCode(), DISK_IO_EXCEPTION);
        errorMap.put(NETWORK_IO_EXCEPTION.getErrorCode(), NETWORK_IO_EXCEPTION);
        errorMap.put(DB_OVERLOAD_WARNING.getErrorCode(), DB_OVERLOAD_WARNING);
        errorMap.put(DB_TOO_MANY_DB_CONNECTIONS.getErrorCode(), DB_TOO_MANY_DB_CONNECTIONS);
        errorMap.put(DB_TIME_SPENT_IN_DB_EXCEPTION.getErrorCode(), DB_TIME_SPENT_IN_DB_EXCEPTION);
    }

    public static String getLevel(int eventType) {
        if (eventType % 10 == 1) {
            return HealthLevel.WARNING.getDesc();
        } else if (eventType % 10 == 2) {
            return HealthLevel.CRITICAL.getDesc();
        } else {
            return HealthLevel.NORMAL.getDesc();
        }
    }

    public static String getDescription(int eventType) {
        int errorCode = eventType / 10 % 1000;
        return errorMap.containsKey(errorCode) ? errorMap.get(errorCode).getErrorName() : "errors not defined";
    }

    public static String getObjType(int eventType) {
        int objType = eventType / 10000;
        switch (objType) {
            case 1:
                return "app";
            case 2:
                return "service";
            case 3:
                return "instance";
            case 4:
                return "transaction";
            case 5:
                return "server";
            case 6:
                return "DB";
            default:
                return "";
        }
    }
}