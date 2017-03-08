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

public class NetsDao extends ESBaseDao{

    public ESQueryResult getNetTopN(ESQueryCond esQueryCond) {
        List<ESMetrics> esMetricsesLast = getNetStatics(esQueryCond,10,SortOrder.DESC);
        Map<String, ESMetrics> esMetricsLastMap = new HashMap<>();
        for(ESMetrics esMetrics : esMetricsesLast) {
            esMetricsLastMap.put((String)esMetrics.getValue(ESConst.NET_NAME),esMetrics);
        }



        Map<String, ESMetrics> esMetricsFirstMap = new HashMap<>();
        List<ESMetrics> esMetricsesFirst = getNetStatics(esQueryCond,10,SortOrder.ASC);
        for(ESMetrics esMetrics : esMetricsesFirst) {
            esMetricsFirstMap.put((String)esMetrics.getValue(ESConst.NET_NAME),esMetrics);
        }


        List<ESMetrics> esMetricses = new ArrayList<>();
        Set<String> remainSet = esMetricsLastMap.keySet();
        remainSet.retainAll(esMetricsFirstMap.keySet());
        for(String key : remainSet) {
            ESMetrics last = esMetricsLastMap.get(key);
            ESMetrics first = esMetricsFirstMap.get(key);
            esMetricses.add(calMetircs(first,last));
        }



        return new ESQueryResult(ESConst.NET_TYPE,esMetricses);
    }

    public ESQueryResult getNetStatics(ESQueryCond esQueryCond) {
        List<ESMetrics> esMetricsesLast = getNetStatics(esQueryCond,10,SortOrder.DESC);
        Map<String, ESMetrics> esMetricsLastMap = new HashMap<>();
        for(ESMetrics esMetrics : esMetricsesLast) {
            esMetricsLastMap.put((String)esMetrics.getValue(ESConst.NET_NAME),esMetrics);
        }
        List<ESMetrics> esMetricses = new ArrayList<>();
        esMetricses.addAll(esMetricsLastMap.values());

        return new ESQueryResult(ESConst.NET_TYPE,esMetricses);
    }


    public ESQueryResult getNetsByTime(ESQueryCond esQueryCond) {

        List<ESMetrics> esMetricses = getMeticsByTime(esQueryCond);

        List<ESMetrics> calEsMetricses = new ArrayList<>();
        ESMetrics esMetricsBefore = null;
        for(ESMetrics esMetrics : esMetricses) {
            if(esMetricsBefore == null) {
                esMetricsBefore = esMetrics;
            }

            Map<String ,Object> metricsMap = new HashMap<>();

            calEsMetricses.add(calMetircs(esMetricsBefore,esMetrics));
        }

        return new ESQueryResult(ESConst.NET_TYPE,calEsMetricses);
    }
    public List<ESMetrics> getMeticsByTime(ESQueryCond esQueryCond) {

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
            ESQueryResult curEsQueryResult = getNetStatics(curEsQueryCond);

            ESMetrics esMetrics = aggESQueryResult(curEsQueryResult);
            long curCollectTime = XLong(esMetrics.getValue(ESConst.COLLECT_TIME));
            if(curCollectTime > 0) {
                esMetricses.add(esMetrics);
            }
        }

        return esMetricses;
    }

    private ESMetrics calMetircs(ESMetrics beforeEsMetrics ,ESMetrics curEsMetrics) {

        long colls = 0;
        long transmitErrs = 0;
        long transmitBytes = 0;
        long receiveErrs = 0;
        long receiveBytes = 0;
        long collectTime = 0;

        long BeforeCollectTime = XLong(curEsMetrics.getValue(ESConst.COLLECT_TIME));
        long curCollectTime = XLong(curEsMetrics.getValue(ESConst.COLLECT_TIME));

        long beforeColls = XLong(curEsMetrics.getValue(ESConst.NET_COLLS));
        long curColls = XLong(curEsMetrics.getValue(ESConst.NET_COLLS));
        colls = curColls - beforeColls;

        long beforeReceiveErrs = XLong(curEsMetrics.getValue(ESConst.NET_RECEIVE_ERRORS));
        long curReceiveErrs = XLong(curEsMetrics.getValue(ESConst.NET_RECEIVE_ERRORS));
        receiveErrs = curReceiveErrs - beforeReceiveErrs;

        long beforeTansmitErrs = XLong(curEsMetrics.getValue(ESConst.NET_TRANSMIT_ERRORS));
        long curTansmitErrs = XLong(curEsMetrics.getValue(ESConst.NET_TRANSMIT_ERRORS));
        transmitErrs = curTansmitErrs - beforeTansmitErrs;

        long beforeReceiveBytes = XLong(curEsMetrics.getValue(ESConst.NET_RECEIVE_BYTES));
        long curReceiveBytes = XLong(curEsMetrics.getValue(ESConst.NET_RECEIVE_BYTES));

        if(curCollectTime - BeforeCollectTime > 0) {
            receiveBytes = (curReceiveBytes - beforeReceiveBytes)/((curCollectTime - BeforeCollectTime)/1000);
        }


        long beforeTransmitBytes = XLong(curEsMetrics.getValue(ESConst.NET_TRANSMIT_BYTES));
        long curTransmitBytes = XLong(curEsMetrics.getValue(ESConst.NET_TRANSMIT_BYTES));
        if(curCollectTime - BeforeCollectTime > 0) {
            receiveBytes = (curTransmitBytes - beforeTransmitBytes)/((curCollectTime - BeforeCollectTime)/1000);
        }


        Map<String ,Object> metricMap = new HashMap<>();
        metricMap.put(ESConst.COLLECT_TIME,curCollectTime);
        metricMap.put(ESConst.NET_COLLS,colls);
        metricMap.put(ESConst.NET_RECEIVE_BYTES,receiveBytes);
        metricMap.put(ESConst.NET_RECEIVE_ERRORS,receiveErrs);
        metricMap.put(ESConst.NET_TRANSMIT_BYTES,transmitBytes);
        metricMap.put(ESConst.NET_TRANSMIT_ERRORS,transmitErrs);

        if(curEsMetrics.getValue(ESConst.NET_NAME) != null) {
            metricMap.put(ESConst.NET_NAME,curEsMetrics.getValue(ESConst.NET_NAME));

        }


        return new ESMetrics(metricMap);
    }


    private ESMetrics aggESQueryResult(ESQueryResult curEsQueryResult) {

        long colls = 0;
        long transmitErrs = 0;
        long transmitBytes = 0;
        long receiveErrs = 0;
        long receiveBytes = 0;
        long collectTime = 0;

        for(ESMetrics esMetrics : curEsQueryResult.getEsMetricses()) {

            long curCollectTime = XLong(esMetrics.getValue(ESConst.COLLECT_TIME));
            collectTime = curCollectTime > 0 ? curCollectTime : collectTime;

            long curColls = XLong(esMetrics.getValue(ESConst.NET_COLLS));
            colls += curColls;

            long curReceiveErrs = XLong(esMetrics.getValue(ESConst.NET_RECEIVE_ERRORS));
            receiveErrs += curReceiveErrs;
            long curReceiveBytes = XLong(esMetrics.getValue(ESConst.NET_RECEIVE_BYTES));
            receiveBytes += curReceiveBytes;

            long curTransmitBytes = XLong(esMetrics.getValue(ESConst.NET_TRANSMIT_BYTES));
            transmitBytes += curTransmitBytes;
            long curTansmitErrs = XLong(esMetrics.getValue(ESConst.NET_TRANSMIT_ERRORS));
            transmitErrs += curTansmitErrs;
        }

        Map<String ,Object> metricMap = new HashMap<>();
        metricMap.put(ESConst.COLLECT_TIME,collectTime);
        metricMap.put(ESConst.NET_COLLS,colls);
        metricMap.put(ESConst.NET_RECEIVE_BYTES,receiveBytes);
        metricMap.put(ESConst.NET_RECEIVE_ERRORS,receiveErrs);
        metricMap.put(ESConst.NET_TRANSMIT_BYTES,transmitBytes);
        metricMap.put(ESConst.NET_TRANSMIT_ERRORS,transmitErrs);

        return new ESMetrics(metricMap);
    }


    private List<ESMetrics> getNetStatics(ESQueryCond esQueryCond, int topN, SortOrder order) {
        List<ESMetrics> esMetricses = new ArrayList<>();
        Client client = null;
        try {
            client = getClient();

            SearchResponse sp=client.prepareSearch(ESConst.INDEX)
                    .setTypes(ESConst.NET_TYPE)
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
