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

public class ProcessesDao extends ESBaseDao{

    public List<ESQueryResult>  getTimedProcesses(ESQueryCond esQueryCond) {
        List<ESQueryResult>  esQueryResults = new ArrayList<>();

        List<ESMetrics> esMetricsesLast = getTimedProcesses(esQueryCond,100,SortOrder.DESC);
        Map<String, ESMetrics> esMetricsLastMap = new HashMap<>();
        for(ESMetrics esMetrics : esMetricsesLast) {
            esMetricsLastMap.put((String)esMetrics.getValue(ESConst.PROCESS_PID),esMetrics);
        }
        List<ESMetrics> esMetricses = new ArrayList<>();
        esMetricses.addAll(esMetricsLastMap.values());

        esQueryResults = TopN(esMetricses);

        return esQueryResults;
    }

    private List<ESMetrics> getTimedProcesses(ESQueryCond esQueryCond, int topN, SortOrder order) {
        List<ESMetrics> esMetricses = new ArrayList<>();
        Client client = null;
        try {
            client = getClient();

            SearchResponse sp=client.prepareSearch(ESConst.INDEX)
                    .setTypes(ESConst.PROCESS_TYPE)
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


    private List<ESMetrics> copy(List<ESMetrics> esMetricses) {
        List<ESMetrics> retEsMetricses = new ArrayList<>();
        int i = 0;
        for(ESMetrics esMetrics : esMetricses) {
            retEsMetricses.add(esMetrics);
            i++;
            if(i > 5) break;
        }
        return retEsMetricses;
    }

    public List<ESQueryResult> getProcessesByTime(ESQueryCond esQueryCond) {
        List<ESQueryResult>  esQueryResults = new ArrayList<>();
        Client client = null;
        try {
            client = getClient();

            List<ESMetrics> esMetricses = testAggregation(client,esQueryCond);

            esQueryResults = TopN(esMetricses);

            System.out.println(esMetricses);

        } finally {
            if (client != null) {
                client.close();
            }
        }

        return esQueryResults;
    }

    private List<ESQueryResult> TopN( List<ESMetrics> esMetricses) {
        List<ESQueryResult> esQueryResults = new ArrayList<>();
        esMetricses.sort(new Comparator<ESMetrics>() {
            @Override
            public int compare(ESMetrics o1, ESMetrics o2) {
                return xcompare(o1.getValue(ESConst.PROCESS_VIRT),o2.getValue(ESConst.PROCESS_VIRT));

            }
        });


        esQueryResults.add(new ESQueryResult(ESConst.PROCESS_VIRT,copy(esMetricses)));


        esMetricses.sort(new Comparator<ESMetrics>() {
            @Override
            public int compare(ESMetrics o1, ESMetrics o2) {
                return xcompare(o1.getValue(ESConst.PROCESS_CPU_USAGE),o2.getValue(ESConst.PROCESS_CPU_USAGE));

            }
        });
        esQueryResults.add(new ESQueryResult(ESConst.PROCESS_CPU_USAGE,copy(esMetricses)));

        esMetricses.sort(new Comparator<ESMetrics>() {
            @Override
            public int compare(ESMetrics o1, ESMetrics o2) {

                return xcompare(o1.getValue(ESConst.PROCESS_CPU_TIME),o2.getValue(ESConst.PROCESS_CPU_TIME));
            }
        });
        esQueryResults.add(new ESQueryResult(ESConst.PROCESS_CPU_TIME,copy(esMetricses)));

        return esQueryResults;
    }

    private int xcompare(Object o1 ,Object o2) {
        int ret = 0;
        if(o1 instanceof Double) {
            double  virt1 = (double)o1;
            double  virt2 = (double)o2;
            double diff = virt1 - virt2;

            if(diff > 0) ret = -1;
            if(diff == 0) ret = 0;
            if(diff < 0) ret = 1;
        } else if(o1 instanceof Integer) {
            int  virt1 = (int)o1;
            int  virt2 = (int)o2;
            int diff = virt1 - virt2;
            if(diff > 0) ret = -1;
            if(diff == 0) ret = 0;
            if(diff < 0) ret = 1;
        } else if(o1 instanceof Long) {
            long  virt1 = (long)o1;
            long  virt2 = (long)o2;
            long diff = virt1 - virt2;
            if(diff > 0) ret = -1;
            if(diff == 0) ret = 0;
            if(diff < 0) ret = 1;
        }

        return ret;

    }


    private List<ESMetrics> testAggregation(Client client,ESQueryCond esQueryCond) {
    List<ESMetrics> esMetricses = new ArrayList<ESMetrics>();
        try {
            SearchRequestBuilder srb = client.prepareSearch(ESConst.INDEX);
            srb.setTypes(ESConst.PROCESS_TYPE);
            srb.setSearchType(SearchType.COUNT);

            TermsBuilder pidTermsBuilder = AggregationBuilders
                    .terms(ESConst.PROCESS_PID)
                    .field(ESConst.PROCESS_PID);
            TermsBuilder nameTermsBuilder = AggregationBuilders
                    .terms(ESConst.PROCESS_NAME)
                    .field(ESConst.PROCESS_NAME);

            TermsBuilder commandTermsBuilder = AggregationBuilders
                    .terms(ESConst.PROCESS_COMMAND)
                    .field(ESConst.PROCESS_COMMAND);

            commandTermsBuilder
                    .subAggregation(AggregationBuilders.avg(ESConst.PROCESS_CPU_USAGE).field(ESConst.PROCESS_CPU_USAGE))
                    .subAggregation(AggregationBuilders.avg(ESConst.PROCESS_VIRT).field(ESConst.PROCESS_VIRT))
                    .subAggregation(AggregationBuilders.max(ESConst.PROCESS_CPU_TIME).field(ESConst.PROCESS_CPU_TIME));

            nameTermsBuilder.subAggregation(commandTermsBuilder);
            pidTermsBuilder.subAggregation(nameTermsBuilder);
            srb.addAggregation(pidTermsBuilder);

            srb.setQuery(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
                    .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                    .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())));

            SearchResponse sr = srb.execute().actionGet();



            Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

            StringTerms pidTerms = (StringTerms) aggMap.get(ESConst.PROCESS_PID);

            Iterator<Terms.Bucket> pidBucketIt = pidTerms.getBuckets().iterator();

            while (pidBucketIt.hasNext()) {
                Terms.Bucket pidBucket = pidBucketIt.next();
                System.out.println(pidBucket.getKey() + " : " + pidBucket.getDocCount() + " ");

                StringTerms nameTerms = (StringTerms) pidBucket.getAggregations().asMap().get(ESConst.PROCESS_NAME);
                Iterator<Terms.Bucket> nameBucketIt = nameTerms.getBuckets().iterator();

                while (nameBucketIt.hasNext()) {
                    Terms.Bucket nameBucket = nameBucketIt.next();
                    System.out.println(pidBucket.getKey() + " : " + nameBucket.getKey() + " : " + nameBucket.getDocCount() + " ");

                    StringTerms commandTerms = (StringTerms) nameBucket.getAggregations().asMap().get(ESConst.PROCESS_COMMAND);
                    Iterator<Terms.Bucket> commandBucketIt = commandTerms.getBuckets().iterator();
                    while(commandBucketIt.hasNext()) {
                        Terms.Bucket commandBucket = commandBucketIt.next();
                        System.out.println(pidBucket.getKey() + " : " + nameBucket.getKey() + " : " + commandBucket.getKey());

                        Iterator<Aggregation> metricsIt = commandBucket.getAggregations().iterator();

                        Map<String, Object> metricsValue = new HashMap<String, Object>();
                        while (metricsIt.hasNext()) {
                            Aggregation aggregation = metricsIt.next();
                            Object value = -1L;
                            System.out.print("name= " + aggregation.getName());
                            if (aggregation instanceof InternalMax) {
                                value = ((InternalMax) aggregation).getValue();
                                System.out.print(((InternalMax) aggregation).getValue());
                            } else if (aggregation instanceof InternalAvg) {
                                value =  ((InternalAvg) aggregation).getValue();
                                System.out.print(((InternalAvg) aggregation).getValue());
                            } else if (aggregation instanceof InternalSum) {
                                value = ((InternalSum) aggregation).getValue();
                                System.out.print(((InternalSum) aggregation).getValue());
                            } else if (aggregation instanceof InternalMin) {
                                value = ((InternalMin) aggregation).getValue();
                                System.out.print(((InternalMin) aggregation).getValue());
                            }

                            metricsValue.put(aggregation.getName(), value);

                            System.out.println("");
                        }

                        metricsValue.put(ESConst.PROCESS_PID,pidBucket.getKey());
                        metricsValue.put(ESConst.PROCESS_NAME,nameBucket.getKey());
                        metricsValue.put(ESConst.PROCESS_COMMAND,commandBucket.getKey());
                        ESMetrics esMetrics = new ESMetrics(metricsValue);
                        esMetricses.add(esMetrics);
                    }
                }
                System.out.println();
            }
        } catch (Exception ex) {

        }

    return esMetricses;
}




}
