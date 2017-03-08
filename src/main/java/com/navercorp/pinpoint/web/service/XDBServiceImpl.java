package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.common.bo.*;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.SqlMetaDataDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.view.XDBRiskEvents;
import com.navercorp.pinpoint.web.vo.*;
import com.navercorp.pinpoint.web.vo.linechart.XDataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Component
public class XDBServiceImpl {
    @Autowired
    private TraceDao traceDao;

    @Autowired
    private XApplicationsService applicationsService;

    @Autowired
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Autowired
    private SqlMetaDataDao sqlMetaDataDao;

    public XDBsRisk getXDBsRisk(String appName, int topN, Range range) {
        Preconditions.checkArgument(appName != null, "appName must not be null.");

        List<TransactionId> transactionIds = getTransactionIds(appName, range);

        List<List<SpanBo>> spansList = newArrayList();
        spansList.addAll(transactionIds.stream().map(transactionId -> traceDao.selectSpanAndAnnotation(transactionId)).collect(Collectors.toList()));

        Map<String, List<SqlInfo>> dbSqlMap = buildSqlMap(spansList);

        if (dbSqlMap.isEmpty()) {
            return new XDBsRisk(newArrayList(), newArrayList());
        }

        List<String> dbList = newArrayList(dbSqlMap.keySet());

        List<XDBRisk> dbRisks = buildDBRisks(dbSqlMap, topN);

        return new XDBsRisk(dbList, dbRisks);
    }

    private List<XDBRisk> buildDBRisks(Map<String, List<SqlInfo>> dbSqlMap, int topN) {
        Preconditions.checkArgument(!dbSqlMap.isEmpty(), "dbSqlMap must not be empty.");

        List<XDBRisk> dbRiskList = newArrayList();
        for (Map.Entry<String, List<SqlInfo>> entry : dbSqlMap.entrySet()) {
            List<SqlInfo> tmpList = entry.getValue();
            Collections.sort(tmpList, (sql1, sql2) ->
                    sql1.getCalls() == sql2.getCalls() ? 0 : (sql1.getCalls() - sql2.getCalls() > 0 ? -1 : 1));
            List<XDataPoint<Integer>> topNCallsSql = createTopNCalls(tmpList, topN);

            Collections.sort(tmpList, (sql1, sql2) ->
                    sql1.getAvgElapsed() == sql2.getAvgElapsed() ? 0 : (sql1.getAvgElapsed() - sql2.getAvgElapsed() > 0 ? -1 : 1));
            List<XDataPoint<Double>> topNElapsedSql = createTopNElasped(tmpList, topN);

            dbRiskList.add(new XDBRisk(entry.getKey(), topNElapsedSql, topNCallsSql));
        }

        return dbRiskList;
    }

    private List<XDataPoint<Double>> createTopNElasped(List<SqlInfo> sqlInfos, int topN) {
        Preconditions.checkArgument(!sqlInfos.isEmpty(), "sqlInfo list must not be empty.");
        List<XDataPoint<Double>> topElapsed = newArrayList();

        for (int i = 0; i < (topN > sqlInfos.size() ? sqlInfos.size() : topN); i++) {
            topElapsed.add(new XDataPoint<>(sqlInfos.get(i).getSqlInfo(), sqlInfos.get(i).getAvgElapsed()));
        }
        return topElapsed;
    }

    private List<XDataPoint<Integer>> createTopNCalls(List<SqlInfo> sqlInfos, int topN) {
        Preconditions.checkArgument(!sqlInfos.isEmpty(), "sqlInfo list must not be empty.");
        List<XDataPoint<Integer>> topCalls = newArrayList();

        for (int i = 0; i < (topN > sqlInfos.size() ? sqlInfos.size() : topN); i++) {
            topCalls.add(new XDataPoint<>(sqlInfos.get(i).getSqlInfo(), sqlInfos.get(i).getCalls()));
        }
        return topCalls;
    }

    private Map<String, List<SqlInfo>> buildSqlMap(List<List<SpanBo>> spansList) {
        Map<String, List<SqlInfo>> sqlInfoMap = newHashMap();
        for (List<SpanBo> trace : spansList) {
            buildTracesSqlMap(sqlInfoMap, trace);
        }
        return sqlInfoMap;
    }

    private void buildTracesSqlMap(Map<String, List<SqlInfo>> sqlInfoMap, List<SpanBo> trace) {
        for (SpanBo spanBo : trace) {
            buildEventsSqlMap(sqlInfoMap, spanBo);
        }
    }

    private void buildEventsSqlMap(Map<String, List<SqlInfo>> sqlInfoMap, SpanBo spanBo) {
        if (spanBo.getSpanEventBoList().isEmpty()) {
            return;
        }

        for (SpanEventBo spanEventBo : spanBo.getSpanEventBoList()) {
            AnnotationBo sqlAnnotation = getSqlAnnotation(spanEventBo);
            if (sqlAnnotation != null) {
                String sql = getSql(spanBo.getAgentId(), spanBo.getAgentStartTime(), sqlAnnotation);
                long startTime = spanBo.getStartTime() + spanEventBo.getStartElapsed();
                Sql sqlExec = new Sql(sql, startTime, spanEventBo.getEndElapsed(), spanBo.getRpc(), spanBo.getTransactionId(), spanEventBo.getDestinationId());
                put2Map(sqlInfoMap, sql, spanEventBo, getSqlId(sqlAnnotation), sqlExec);
            }
        }
    }

    private void put2Map(Map<String, List<SqlInfo>> sqlInfoMap, String sql, SpanEventBo spanEventBo, int sqlId, Sql sqlExec) {
        String dbName = spanEventBo.getDestinationId();
        if (sqlInfoMap.keySet().contains(dbName)) {
            addSqlInfo(sqlInfoMap, sql, spanEventBo, sqlId, sqlExec);
        } else {
            SqlInfo sqlInfo = new SqlInfo(sql, dbName, sqlId, spanEventBo.getEndElapsed(), spanEventBo.getEndElapsed(), 1, newArrayList(sqlExec));
            sqlInfoMap.put(dbName, newArrayList(sqlInfo));
        }
    }

    private void addSqlInfo(Map<String, List<SqlInfo>> sqlInfoMap, String sql, SpanEventBo spanEventBo, int sqlId, Sql sqlExec) {
        String dbName = spanEventBo.getDestinationId();

        List<SqlInfo> sqlInfoList = sqlInfoMap.get(dbName);
        SqlInfo sqlInfos = new SqlInfo(sql, dbName, sqlId, spanEventBo.getEndElapsed(), spanEventBo.getEndElapsed(), 1, newArrayList(sqlExec));

        if (sqlInfoList.contains(sqlInfos)) {
            int index = sqlInfoList.indexOf(sqlInfos);
            SqlInfo sqlInfo = sqlInfoList.get(index);
            sqlInfo.setAvgElapsed(spanEventBo.getEndElapsed());
            sqlInfo.addCalls();
            sqlInfo.setMaxElapsed(spanEventBo.getEndElapsed());
            sqlInfo.getSqlList().add(sqlExec);
        } else {
            sqlInfoList.add(sqlInfos);
        }
    }

    private String getSql(String agentId, long agentStartTime, AnnotationBo annotationBo) {
        final int hashCode = getSqlId(annotationBo);
        List<SqlMetaDataBo> sqlMetaDataBos = sqlMetaDataDao.getSqlMetaData(agentId, agentStartTime, hashCode);

        return sqlMetaDataBos.size() == 0 ? "SQL-ID not found hashCode:" + hashCode : sqlMetaDataBos.get(0).getSql();
    }

    private int getSqlId(AnnotationBo annotationBo) {
        IntStringStringValue sqlValue = (IntStringStringValue) annotationBo.getValue();
        return sqlValue.getIntValue();
    }

    private AnnotationBo getSqlAnnotation(SpanEventBo spanEventBo) {
        if (!isSqlEvent(spanEventBo)) {
            return null;
        }

        for (AnnotationBo annotationBo : spanEventBo.getAnnotationBoList()) {
            if (annotationBo.getKey() == AnnotationKey.SQL_ID.getCode())
                return annotationBo;
        }

        return null;
    }

    private boolean isSqlEvent(SpanEventBo spanEventBo) {
        if (spanEventBo.getAnnotationBoList().isEmpty() || spanEventBo.getDestinationId() == null) {
            return false;
        }

        for (AnnotationBo annotationBo : spanEventBo.getAnnotationBoList()) {
            if (annotationBo.getKey() == AnnotationKey.SQL_ID.getCode()) {
                return true;
            }
        }

        return false;
    }

    private List<TransactionId> getTransactionIds(String appName, Range range) {
        List<TransactionId> transactionIds = newArrayList();
        for (XService xService : applicationsService.getXServices(appName)) {
            LimitedScanResult<List<TransactionId>> listLimitedScanResult = applicationTraceIndexDao.scanTraceIndex(xService.getName(), range, 5000, false);
            transactionIds.addAll(listLimitedScanResult.getScanData());
        }
        return transactionIds;
    }

    public XDBRiskEvents getDBRiskEvents(String appName, String dbName, Range range) {
        Preconditions.checkArgument(appName != null, new NullPointerException("appName"));

        List<TransactionId> transactionIds = getTransactionIds(appName, range);

        List<List<SpanBo>> spansList = newArrayList();
        spansList.addAll(transactionIds.stream().map(transactionId -> traceDao.selectSpanAndAnnotation(transactionId)).collect(Collectors.toList()));

        Map<String, List<SqlInfo>> dbSqlMap = buildSqlMap(spansList);

        Preconditions.checkArgument(dbSqlMap.keySet().contains(dbName), new IllegalArgumentException("illegal dbName."));

        List<SqlInfo> sqlInfos = dbSqlMap.get(dbName);

        return new XDBRiskEvents(sqlInfos);
    }
}
