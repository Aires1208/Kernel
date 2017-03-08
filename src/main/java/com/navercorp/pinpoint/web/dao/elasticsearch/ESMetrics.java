package com.navercorp.pinpoint.web.dao.elasticsearch;

import java.util.*;

/**
 * Created by root on 2/13/17.
 */
public class ESMetrics {
    private Map<String ,Object> values = new HashMap<String,Object>();
    private long collectTime;

    public ESMetrics(long collectTime, Map<String ,Object> values) {
        this.values = values;
        this.collectTime = collectTime;
    }

    public ESMetrics(Map<String ,Object> values) {
        this.values = values;
    }

    public Object getValue(String key) {

//        Optional<Object> optional = Optional.ofNullable(values.get(key));
//        return optional.orElse(new Object());
        return values.get(key);
    }

    public long getCollectTime() {
        return collectTime;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n");


        for(Map.Entry<String,Object> entry : values.entrySet()) {
            if(entry.getKey().equals(ESConst.COLLECT_TIME)) {
                long time = (long)entry.getValue();
                stringBuilder.append("collectTime=").append(new Date(time)).append("\n");
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            } else {

                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
