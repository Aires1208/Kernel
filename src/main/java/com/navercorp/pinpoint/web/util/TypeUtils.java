package com.navercorp.pinpoint.web.util;

import com.navercorp.pinpoint.common.topo.domain.XNodeType;

/**
 * Created by root on 16-9-6.
 */
public class TypeUtils {
    public static String getType(short serviceType) {
        switch (serviceType / 1000) {
            case 0:
                return getInnerUseType(serviceType);
            case 1:
                return getServerType(serviceType);
            case 2:
                return getDBType(serviceType);
            case 8:
                return getCacheClientType(serviceType);
            case 10:
                return XNodeType.PYTHON.getDesc();
            default:
                return XNodeType.JAVA.getDesc();

        }

    }

    private static String getCacheClientType(short serviceType) {
        switch (serviceType / 10) {
            case 830:
                return XNodeType.RABBITMQ.getDesc();
            case 831:
                return XNodeType.ACTIVEMQ.getDesc();
            case 835:
                return XNodeType.KAFKA.getDesc();
            default:
                return "CACHE_CLIENT";
        }
    }

    private static String getDBType(short serviceType) {
        switch (serviceType / 100) {
            case 21:
                return XNodeType.MYSQL.getDesc();
            case 22:
                return XNodeType.MSSQL.getDesc();
            case 23:
                return XNodeType.ORACLE.getDesc();
            case 24:
                return XNodeType.CUBRID.getDesc();
            case 25:
                return XNodeType.POSTGRESQL.getDesc();
            case 26:
                return XNodeType.CASSANDRA.getDesc();
            default:
                return XNodeType.UNKNOWN_DB.getDesc();
        }
    }

    private static String getServerType(short serviceType) {
        if (serviceType == (short) 1905) {
            return XNodeType.SCALA.getDesc();
        }
        return XNodeType.JAVA.getDesc();
    }

    private static String getInnerUseType(short serviceType) {
        switch (serviceType) {
            case -1:
                return XNodeType.UNDEFINED.getDesc();
            case 2:
                return XNodeType.USER.getDesc();
            default:
                return XNodeType.UNKNOWN.getDesc();
        }
    }
}
