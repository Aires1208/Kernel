package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
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

public class FilesDao extends ESBaseDao{

    public ESQueryResult getFileUsedTopN(ESQueryCond esQueryCond) {
        ESQueryResult esQueryResult = null;
        Client client = null;
        try {
            client = getClient();

            SearchRequestBuilder requestBuilder = getTotalUsedSearchRequestBuilder(esQueryCond, client);
            SearchResponse response = requestBuilder.execute().actionGet();
            List<ESMetrics> esMetricses2  = getTotalUsedEsQueryResult(response);


            esQueryResult = new ESQueryResult(ESConst.FILE_TYPE,top5(esMetricses2));
            System.out.println(esMetricses2);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return esQueryResult;
    }

    private List<ESMetrics>  top5(List<ESMetrics> esMetricses) {
        esMetricses.sort(new Comparator<ESMetrics>() {
            @Override
            public int compare(ESMetrics o1, ESMetrics o2) {
                double used1 = (double)o1.getValue(ESConst.FILE_USED);
                double total1 = (double)o1.getValue(ESConst.FILE_TOTAL);

                double used2 = (double)o1.getValue(ESConst.FILE_USED);
                double total2 = (double)o1.getValue(ESConst.FILE_TOTAL);

                if(used1/total1 > used2/total2) {
                    return -1;
                } if(used1/total1 < used2/total2) {
                    return 1;
                } else {
                    return 0;
                }

            }
        });

        List<ESMetrics> retEsMetricses = new ArrayList<>();
        for(int i = 0; i < esMetricses.size() && i < 5; i++) {
            retEsMetricses.add(esMetricses.get(i));
        }

        return retEsMetricses;

    }

    private SearchRequestBuilder getTotalUsedSearchRequestBuilder(ESQueryCond esQueryCond, Client client) {
        SearchRequestBuilder srb = client.prepareSearch(ESConst.INDEX);
        srb.setTypes(ESConst.FILE_TYPE);
        srb.setSearchType(SearchType.COUNT);

        /**********************************************************/

        TermsBuilder gradeTermsBuilder = AggregationBuilders
                .terms(ESConst.FILE_MOUNTON)
                .field(ESConst.FILE_MOUNTON)
                .order(Terms.Order.term(false));
        gradeTermsBuilder
                .subAggregation(AggregationBuilders.avg(ESConst.FILE_TOTAL).field(ESConst.FILE_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.FILE_FREE).field(ESConst.FILE_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.FILE_USED).field(ESConst.FILE_USED));
        srb.addAggregation(gradeTermsBuilder);

//

        srb.setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())));
        return srb;
    }

    private List<ESMetrics> getTotalUsedEsQueryResult(SearchResponse sr) {
        ESQueryResult esQueryResult = null;

        Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

        StringTerms gradeTerms = (StringTerms) aggMap.get(ESConst.FILE_MOUNTON);

        Iterator<Terms.Bucket> gradeBucketIt = gradeTerms.getBuckets().iterator();

        List<ESMetrics> esMetricses = new ArrayList<ESMetrics>();

        while (gradeBucketIt.hasNext()) {
            Terms.Bucket gradeBucket = gradeBucketIt.next();
            System.out.println(gradeBucket.getKey() + ",");
            Iterator<Aggregation> metricsIt = gradeBucket.getAggregations().iterator();

            Map<String, Object> metricsValue = new HashMap<String, Object>();
            while (metricsIt.hasNext()) {
                Aggregation aggregation = metricsIt.next();
                Double value = 0.0;
                System.out.print("name= " + aggregation.getName());
                if (aggregation instanceof InternalMax) {
                    value = ((InternalMax) aggregation).getValue();
                    System.out.print(((InternalMax) aggregation).getValue());
                } else if (aggregation instanceof InternalAvg) {
                    value = ((InternalAvg) aggregation).getValue();
                    System.out.print(((InternalAvg) aggregation).getValue());
                } else if (aggregation instanceof InternalSum) {
                    value =  ((InternalSum) aggregation).getValue();
                    System.out.print(((InternalSum) aggregation).getValue());
                } else if (aggregation instanceof InternalMin) {
                    value =  ((InternalMin) aggregation).getValue();
                    System.out.print(((InternalMin) aggregation).getValue());
                }

                metricsValue.put(aggregation.getName(), value);

                System.out.println("");
            }

            metricsValue.put(ESConst.FILE_MOUNTON,gradeBucket.getKey());

            ESMetrics esMetrics = new ESMetrics( metricsValue);
            esMetricses.add(esMetrics);
        }

        return esMetricses;
    }

    public ESQueryResult getFileStatics(ESQueryCond esQueryCond) {
        List<ESMetrics> esMetricsesLast = getFileStatics(esQueryCond,100,SortOrder.DESC);
        Map<String, ESMetrics> esMetricsLastMap = new HashMap<>();
        for(ESMetrics esMetrics : esMetricsesLast) {
            esMetricsLastMap.put((String)esMetrics.getValue(ESConst.FILE_SYSTEM),esMetrics);
        }
        List<ESMetrics> esMetricses = new ArrayList<>();
        esMetricses.addAll(esMetricsLastMap.values());

        return new ESQueryResult(ESConst.FILE_TYPE,esMetricses);
    }


    public ESQueryResult getFilesByTime(ESQueryCond esQueryCond) {
        System.out.println(esQueryCond);
        List<ESMetrics> esMetricses = new ArrayList<>();

        long gpMiSeconds = esQueryCond.getGp() * 60 * 1000;

        long range = esQueryCond.getTo() - esQueryCond.getFrom();

        int number = (int)(range / gpMiSeconds)  + 1;

        for (int i = 0; i < number && i <= 20; i++) {
            long from = esQueryCond.getFrom() + i*gpMiSeconds;
            long to = from + gpMiSeconds;
            ESQueryCond curEsQueryCond = new ESQueryCond.ESQueryCondBuild(esQueryCond)
                    .from(from)
                    .to(to)
                    .build();
            System.out.println(curEsQueryCond);
            ESQueryResult curEsQueryResult = getFileStatics(curEsQueryCond);

            ESMetrics esMetrics = aggESQueryResult(curEsQueryResult);
            long curCollectTime = XLong(esMetrics.getValue(ESConst.COLLECT_TIME));
            if(curCollectTime > 0) {
                esMetricses.add(esMetrics);
            }
        }

        return new ESQueryResult(ESConst.FILE_TYPE,esMetricses);
    }

    private ESMetrics aggESQueryResult(ESQueryResult curEsQueryResult) {

        long total = 0;
        long used = 0;
        long free = 0;
        long collectTime = 0;

        for(ESMetrics esMetrics : curEsQueryResult.getEsMetricses()) {

            long curCollectTime = XLong(esMetrics.getValue(ESConst.COLLECT_TIME));
            collectTime = curCollectTime > 0 ? curCollectTime : collectTime;

            long curTotal = XLong(esMetrics.getValue(ESConst.FILE_TOTAL));
            total += curTotal;

            long curFree = XLong(esMetrics.getValue(ESConst.FILE_FREE));
            free += curFree;

            long curUsed = XLong(esMetrics.getValue(ESConst.FILE_USED));
            used += curUsed;
        }

        Map<String ,Object> metricMap = new HashMap<>();
        metricMap.put(ESConst.COLLECT_TIME,collectTime);
        metricMap.put(ESConst.FILE_TOTAL,total);
        metricMap.put(ESConst.FILE_FREE,free);
        metricMap.put(ESConst.FILE_USED,used);

        return new ESMetrics(metricMap);
    }

    private List<ESMetrics> getFileStatics(ESQueryCond esQueryCond, int topN, SortOrder order) {
        List<ESMetrics> esMetricses = new ArrayList<>();
        Client client = null;
        try {
            client = getClient();

            SearchResponse sp=client.prepareSearch(ESConst.INDEX)
                    .setTypes(ESConst.FILE_TYPE)
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


    

    private SearchRequestBuilder getTotalSearchRequestBuilder(ESQueryCond esQueryCond, Client client) {
        SearchRequestBuilder srb = client.prepareSearch(ESConst.INDEX);
        srb.setTypes(ESConst.DEVICE_TYPE);
        srb.setSearchType(SearchType.COUNT);
        
        /**********************************************************/

            TermsBuilder gradeTermsBuilder = AggregationBuilders
                    .terms(ESConst.DEVICE_NAME)
                    .field(ESConst.DEVICE_NAME)
                    .order(Terms.Order.term(false));
            gradeTermsBuilder
                    .subAggregation(AggregationBuilders.max(ESConst.DEVICE_READ).field(ESConst.DEVICE_READ))
                    .subAggregation(AggregationBuilders.max(ESConst.DEVICE_WRITE).field(ESConst.DEVICE_WRITE));
            srb.addAggregation(gradeTermsBuilder);

//

        srb.setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())));
        return srb;
    }

    private List<ESMetrics> getTotalEsQueryResult(SearchResponse sr) {
        ESQueryResult esQueryResult = null;

        Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

        StringTerms gradeTerms = (StringTerms) aggMap.get(ESConst.DEVICE_NAME);

        Iterator<Terms.Bucket> gradeBucketIt = gradeTerms.getBuckets().iterator();

        List<ESMetrics> esMetricses = new ArrayList<ESMetrics>();

        while (gradeBucketIt.hasNext()) {
            Terms.Bucket gradeBucket = gradeBucketIt.next();
            System.out.println(gradeBucket.getKey() + ",");
            Iterator<Aggregation> metricsIt = gradeBucket.getAggregations().iterator();

            Map<String, Object> metricsValue = new HashMap<String, Object>();
            while (metricsIt.hasNext()) {
                Aggregation aggregation = metricsIt.next();
                Double value = 0.0;
                System.out.print("name= " + aggregation.getName());
                if (aggregation instanceof InternalMax) {
                    value = ((InternalMax) aggregation).getValue();
                    System.out.print(((InternalMax) aggregation).getValue());
                } else if (aggregation instanceof InternalAvg) {
                    value = ((InternalAvg) aggregation).getValue();
                    System.out.print(((InternalAvg) aggregation).getValue());
                } else if (aggregation instanceof InternalSum) {
                    value =  ((InternalSum) aggregation).getValue();
                    System.out.print(((InternalSum) aggregation).getValue());
                } else if (aggregation instanceof InternalMin) {
                    value =  ((InternalMin) aggregation).getValue();
                    System.out.print(((InternalMin) aggregation).getValue());
                }

                metricsValue.put(aggregation.getName(), value);

                System.out.println("");
            }

            metricsValue.put(ESConst.DEVICE_NAME,gradeBucket.getKey());

            ESMetrics esMetrics = new ESMetrics( metricsValue);
            esMetricses.add(esMetrics);
        }

        return esMetricses;
    }

    private List<ESMetrics> getEsQueryResult(SearchResponse sr) {

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
                Double value = 0.0;
                System.out.print("name= " + aggregation.getName());
                if (aggregation instanceof InternalMax) {
                    value = ((InternalMax) aggregation).getValue();
                    System.out.print(((InternalMax) aggregation).getValue());
                } else if (aggregation instanceof InternalAvg) {
                    value = ((InternalAvg) aggregation).getValue();
                    System.out.print(((InternalAvg) aggregation).getValue());
                } else if (aggregation instanceof InternalSum) {
                    value =  ((InternalSum) aggregation).getValue();
                    System.out.print(((InternalSum) aggregation).getValue());
                } else if (aggregation instanceof InternalMin) {
                    value =  ((InternalMin) aggregation).getValue();
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

        return esMetricses;
    }

}
