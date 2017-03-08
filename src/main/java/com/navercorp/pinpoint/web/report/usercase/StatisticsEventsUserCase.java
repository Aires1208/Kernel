package com.navercorp.pinpoint.web.report.usercase;

import com.navercorp.pinpoint.common.events.ResultEvent;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.navercorp.pinpoint.web.report.usercase.HealthLevel.*;

/**
 * Created by root on 16-9-21.
 */
public class StatisticsEventsUserCase {
    private List<ResultEvent> events = newArrayList();
    private int dnCount;
    private Map<String, List<ResultEvent>> map = buildDNEventsMap();

    public StatisticsEventsUserCase(List<ResultEvent> events, int dnCount) {
        this.events = events;
        this.dnCount = dnCount;
        this.map = buildDNEventsMap();
    }

    public double getScore() {
        double score = dnCount != 0 ? (getNormal() / (double) dnCount) : 0;
        return score * 100;
    }

    public Integer getNormal() {
        return dnCount != 0 ? (dnCount - getWarning() - getCritical()) : 0;
    }

    public Integer getWarning() {
        Integer warning = 0;

        if (map.isEmpty()) {
            return warning;
        }

        for (Map.Entry<String, List<ResultEvent>> entry : map.entrySet()) {
            boolean isWarning = isWarning(entry);
            warning += isWarning ? 1 : 0;
        }
        return warning;
    }

    private boolean isWarning(Map.Entry<String, List<ResultEvent>> entry) {
        boolean isWarning = false;
        for (ResultEvent event : entry.getValue()) {
            int level = event.getEventType() % 10;
            isWarning = !(level == CRITICAL.getCode() || level == NORMAL.getCode());
        }
        return isWarning;
    }

    public Integer getCritical() {
        Integer critical = 0;

        if (map.isEmpty())
            return critical;

        for (Map.Entry<String, List<ResultEvent>> entry : map.entrySet()) {
            boolean isCritical = isCritical(entry);
            critical += isCritical ? 1 : 0;
        }
        return critical;
    }

    private boolean isCritical(Map.Entry<String, List<ResultEvent>> entry) {
        boolean isCritical = false;
        for (ResultEvent event : entry.getValue()) {
            isCritical = event.getEventType() % 10 == CRITICAL.getCode();
        }
        return isCritical;
    }

    private Map<String, List<ResultEvent>> buildDNEventsMap() {
        if (CollectionUtils.isEmpty(events)) {
            return newHashMap();
        }

        Map<String, List<ResultEvent>> dnEventsMap = newHashMap();
        for (ResultEvent event : events) {
            putToMap(dnEventsMap, event);
        }

        return dnEventsMap;
    }

    private void putToMap(Map<String, List<ResultEvent>> dnEventsMap, ResultEvent event) {
        String objDN = event.getObjDN();
        if (dnEventsMap.containsKey(objDN)) {
            dnEventsMap.get(objDN).add(event);
        } else {
            dnEventsMap.put(objDN, newArrayList(event));
        }
    }

    public HealthLevel getLevel() {
        if (CollectionUtils.isEmpty(events)) {
            return NORMAL;
        }

        int flag = 0;
        for (ResultEvent event : events) {
            int level = event.getEventType() % 10;
            flag = (flag < level) ? level : flag;
        }

        return flag == CRITICAL.getCode() ? CRITICAL : (flag == WARNING.getCode() ? WARNING : NORMAL);
    }

}
