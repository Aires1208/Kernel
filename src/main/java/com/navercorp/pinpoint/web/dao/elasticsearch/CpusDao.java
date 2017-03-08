package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.rounding.Rounding;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CpusDao extends ESBaseDao{

    public ESQueryResult getCpuStatics(ESQueryCond esQueryCond) {
        List<ESMetrics> esMetricsesLast = getCpusDystatics(esQueryCond,100,SortOrder.DESC);
        Map<String, ESMetrics> esMetricsLastMap = new HashMap<>();
        for(ESMetrics esMetrics : esMetricsesLast) {
            esMetricsLastMap.put((String)esMetrics.getValue(ESConst.CPU_ID),esMetrics);
        }
        List<ESMetrics> esMetricses = new ArrayList<>();
        esMetricses.addAll(esMetricsLastMap.values());

        return new ESQueryResult(ESConst.CPU_TYPE,esMetricses);
    }

    public ESQueryResult getCpuRatioByTime(ESQueryCond esQueryCond) {
        System.out.println("ESQueryCond = " + esQueryCond);
        ESQueryResult esQueryResult = null;
        Client client = null;
        try {
            client = getClient();

            SearchRequestBuilder requestBuilder = getSearchRequestBuilder(esQueryCond, client);

            SearchResponse response = requestBuilder.execute().actionGet();

            esQueryResult = getEsQueryResultByTime(response);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return esQueryResult;
    }

    private SearchRequestBuilder getSearchRequestBuilder(ESQueryCond esQueryCond, Client client) {
        SearchRequestBuilder srb = client.prepareSearch(ESConst.INDEX);
        srb.setTypes(ESConst.CPU_TYPE);
        srb.setSearchType(SearchType.QUERY_THEN_FETCH);

        DateHistogramBuilder gradeTermsBuilder = AggregationBuilders
                .dateHistogram("By_Date")
                .field("collectTime")
                .format("yyyy-MM-dd HH:mm:ss")
                .subAggregation(AggregationBuilders.max(ESConst.CPU_USER).field(ESConst.CPU_USER))
                .subAggregation(AggregationBuilders.max(ESConst.CPU_NICE).field(ESConst.CPU_NICE))
                .subAggregation(AggregationBuilders.max(ESConst.CPU_SYSTEM).field(ESConst.CPU_SYSTEM))
                .subAggregation(AggregationBuilders.max(ESConst.CPU_IDEL).field(ESConst.CPU_IDEL))
                .subAggregation(AggregationBuilders.max(ESConst.CPU_IOWAIT).field(ESConst.CPU_IOWAIT))
                .subAggregation(AggregationBuilders.max(ESConst.CPU_IRQ).field(ESConst.CPU_IRQ))
                .subAggregation(AggregationBuilders.max(ESConst.CPU_SOFTIRQ).field(ESConst.CPU_SOFTIRQ))
                .interval(DateHistogramInterval.minutes(esQueryCond.getGp()))
                ;

        srb.addAggregation(gradeTermsBuilder);

        srb.setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())))
                .addSort(ESConst.COLLECT_TIME, SortOrder.ASC);
        return srb;
    }

    private ESQueryResult getEsQueryResultByTime(SearchResponse sr) {
        ESQueryResult esQueryResult = null;

        Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

        InternalHistogram gradeTerms = (InternalHistogram) aggMap.get("By_Date");

        Iterator<Histogram.Bucket> gradeBucketIt = gradeTerms.getBuckets().iterator();

        List<ESMetrics> esMetricses = new ArrayList<ESMetrics>();

        while (gradeBucketIt.hasNext()) {
            Histogram.Bucket gradeBucket = gradeBucketIt.next();
            System.out.println(gradeBucket.getKey() + ",");
            Iterator<Aggregation> metricsIt = gradeBucket.getAggregations().iterator();

            Map<String, Object> metricsValue = new HashMap<String, Object>();
            while (metricsIt.hasNext()) {
                Aggregation aggregation = metricsIt.next();
                Long value = -1L;
                System.out.print("name= " + aggregation.getName());
                if (aggregation instanceof InternalMax) {
                    value = (long) ((InternalMax) aggregation).getValue();
                    System.out.print(((InternalMax) aggregation).getValue());
                } else if (aggregation instanceof InternalAvg) {
                    value = (long) ((InternalAvg) aggregation).getValue();
                    System.out.print(((InternalAvg) aggregation).getValue());
                } else if (aggregation instanceof InternalSum) {
                    value = (long) ((InternalSum) aggregation).getValue();
                    System.out.print(((InternalSum) aggregation).getValue());
                } else if (aggregation instanceof InternalMin) {
                    value = (long) ((InternalMin) aggregation).getValue();
                    System.out.print(((InternalMin) aggregation).getValue());
                }

                metricsValue.put(aggregation.getName(), value);

                System.out.println("");
            }

            String collectTime = gradeBucket.getKey().toString();

            metricsValue.put(ESConst.COLLECT_TIME,timeParse(collectTime));
            System.out.println("collectTime=" + collectTime + "," + timeParse(collectTime));
            ESMetrics esMetrics = new ESMetrics(metricsValue);
            esMetricses.add(esMetrics);
        }

        esQueryResult = new ESQueryResult(ESConst.CPU_TYPE, esMetricses);
        return esQueryResult;
    }


    public ESQueryResult getCpuRatioTopN(ESQueryCond esQueryCond) {
        List<ESMetrics> esMetricsesLast = getCpusDystatics(esQueryCond,100,SortOrder.DESC);
        Map<String, ESMetrics> esMetricsLastMap = new HashMap<>();
        for(ESMetrics esMetrics : esMetricsesLast) {
            esMetricsLastMap.put((String)esMetrics.getValue(ESConst.CPU_ID),esMetrics);
        }

        Map<String, ESMetrics> esMetricsFirstMap = new HashMap<>();
        List<ESMetrics> esMetricsesFirst = getCpusDystatics(esQueryCond,100,SortOrder.ASC);
        for(ESMetrics esMetrics : esMetricsesFirst) {
            esMetricsFirstMap.put((String)esMetrics.getValue(ESConst.CPU_ID),esMetrics);
        }

        List<ESMetrics> esMetricses = new ArrayList<>();
        Set<String> remainSet = esMetricsLastMap.keySet();
        remainSet.retainAll(esMetricsFirstMap.keySet());
        for(String key : remainSet) {
            ESMetrics last = esMetricsLastMap.get(key);
            ESMetrics first = esMetricsFirstMap.get(key);

            Map<String,Object> metricMap = new HashMap<>();
            metricMap.put(ESConst.CPU_ID,last.getValue(ESConst.CPU_ID));
            metricMap.put(ESConst.CPU_USER,XLong(last.getValue(ESConst.CPU_USER))-XLong(first.getValue(ESConst.CPU_USER)));
            metricMap.put(ESConst.CPU_NICE,XLong(last.getValue(ESConst.CPU_NICE))-XLong(first.getValue(ESConst.CPU_NICE)));
            metricMap.put(ESConst.CPU_SYSTEM,XLong(last.getValue(ESConst.CPU_SYSTEM))-XLong(first.getValue(ESConst.CPU_SYSTEM)));
            metricMap.put(ESConst.CPU_IDEL,XLong(last.getValue(ESConst.CPU_IDEL))-XLong(first.getValue(ESConst.CPU_IDEL)));
            metricMap.put(ESConst.CPU_IOWAIT,XLong(last.getValue(ESConst.CPU_IOWAIT))-XLong(first.getValue(ESConst.CPU_IOWAIT)));
            metricMap.put(ESConst.CPU_IRQ,XLong(last.getValue(ESConst.CPU_IRQ))-XLong(first.getValue(ESConst.CPU_IRQ)));
            metricMap.put(ESConst.CPU_SOFTIRQ,XLong(last.getValue(ESConst.CPU_SOFTIRQ))-XLong(first.getValue(ESConst.CPU_SOFTIRQ)));


            ESMetrics esMetrics = new ESMetrics(metricMap);
            esMetricses.add(esMetrics);

        }

        sort(esMetricses);

        int lastIndex = esMetricses.size() < 5 ? esMetricses.size() : 5;
        List<ESMetrics> esMetricsesTopN =  esMetricses.subList(0,lastIndex);
        return new ESQueryResult(ESConst.CPU_TYPE,esMetricsesTopN);


    }


    private void sort(List<ESMetrics> esMetricses) {
        esMetricses.sort(new Comparator<ESMetrics>() {
            @Override
            public int compare(ESMetrics o1, ESMetrics o2) {
                long used1 = (long)o1.getValue(ESConst.CPU_USER) + (long)o1.getValue(ESConst.CPU_NICE)
                        + (long)o1.getValue(ESConst.CPU_SYSTEM);
                long total1 = used1 + (long)o1.getValue(ESConst.CPU_IDEL)
                        + (long)o1.getValue(ESConst.CPU_IOWAIT) + (long)o1.getValue(ESConst.CPU_IRQ)
                        + (long)o1.getValue(ESConst.CPU_SOFTIRQ);

                long used2 = (long)o2.getValue(ESConst.CPU_USER) + (long)o2.getValue(ESConst.CPU_NICE)
                        + (long)o2.getValue(ESConst.CPU_SYSTEM);
                long total2 = used2 + (long)o2.getValue(ESConst.CPU_IDEL)
                        + (long)o2.getValue(ESConst.CPU_IOWAIT) + (long)o2.getValue(ESConst.CPU_IRQ)
                        + (long)o2.getValue(ESConst.CPU_SOFTIRQ);

                int cpuRatio1 = 0;
                int cpuRatio2 = 0;
                if(total1 < 0) {
                    cpuRatio1 = 0;
                }

                if(total2 < 0) {
                    cpuRatio2 = 0;
                }

                cpuRatio1 = (int)(used1 / total1);
                cpuRatio2 = (int)(used2 / total2);

                return cpuRatio1 - cpuRatio2;
            }
        });
    }

    private List<ESMetrics> getCpusDystatics(ESQueryCond esQueryCond,int topN,SortOrder order) {
        List<ESMetrics> esMetricses = new ArrayList<>();
        Client client = null;
        try {
            client = getClient();

            SearchResponse sp=client.prepareSearch(ESConst.INDEX)
                    .setTypes(ESConst.CPU_TYPE)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
//                            .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                            .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())))

//                    .addSort(ESConst.COLLECT_TIME,SortOrder.DESC)
                    .addSort(ESConst.COLLECT_TIME,order)
                    .setFrom(0).setSize(topN).setExplain(true)
                    .execute().actionGet();
            System.out.println("hits numbers: "+sp.getHits().getTotalHits());
            long collectTime = -1L;
            for(SearchHit hits:sp.getHits().getHits()){


                Map<String, Object> sourceAsMap = hits.sourceAsMap();
                for(Map.Entry<String, Object> k : sourceAsMap.entrySet()){
                    System.out.println("name :  "+k.getKey()+"     value : "+k.getValue());
                }

                if(collectTime < 0) {
                    collectTime = (long)sourceAsMap.get(ESConst.COLLECT_TIME);
                    System.out.println("first collectTime: " + collectTime);
                }

                if(collectTime == (long)sourceAsMap.get(ESConst.COLLECT_TIME)) {
                    esMetricses.add(new ESMetrics(sourceAsMap));
                    System.out.println("first collectTime: " + collectTime);
                    System.out.println("current collectTime: " + (long)sourceAsMap.get(ESConst.COLLECT_TIME));
                    System.out.println("matched");
                } else {
                    System.out.println("not matched");
                }


                System.out.println("=============================================");

            }
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return esMetricses;
    }

}
