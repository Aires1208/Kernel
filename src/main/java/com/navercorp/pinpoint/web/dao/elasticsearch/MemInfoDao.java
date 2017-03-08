package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
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

public class MemInfoDao extends ESBaseDao{

    public ESQueryResult getAggMemInfos(ESQueryCond esQueryCond) {
        ESQueryResult esQueryResult = null;
        Client client = null;
        try {
            client = getClient();

            SearchRequestBuilder requestBuilder = getAggSearchRequestBuilder(esQueryCond, client);

            SearchResponse response = requestBuilder.execute().actionGet();

            esQueryResult = getAggEsQueryResult(response);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return esQueryResult;
    }

    private ESQueryResult getAggEsQueryResult(SearchResponse sr) {

            Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

            StringTerms gradeTerms = (StringTerms) aggMap.get(ESConst.AGENT_ID);

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

                metricsValue.put(ESConst.AGENT_ID,gradeBucket.getKey());

                ESMetrics esMetrics = new ESMetrics( metricsValue);
                esMetricses.add(esMetrics);
            }

            return new ESQueryResult(ESConst.MEMORY_TYPE,esMetricses);

    }

    public ESQueryResult getMemInfos(ESQueryCond esQueryCond) {
        ESQueryResult esQueryResult = null;
        Client client = null;
        try {
            client = getClient();

            SearchRequestBuilder requestBuilder = getSearchRequestBuilder(esQueryCond, client);

            SearchResponse response = requestBuilder.execute().actionGet();

            esQueryResult = getEsQueryResult(response);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return esQueryResult;
    }


    private SearchRequestBuilder getAggSearchRequestBuilder(ESQueryCond esQueryCond, Client client) {
        SearchRequestBuilder srb = client.prepareSearch(ESConst.INDEX);
        srb.setTypes(ESConst.MEMORY_TYPE);
        srb.setSearchType(SearchType.COUNT);


        TermsBuilder gradeTermsBuilder = AggregationBuilders
                .terms(ESConst.AGENT_ID)
                .field(ESConst.AGENT_ID)
                .order(Terms.Order.term(false));
        gradeTermsBuilder
                .subAggregation(AggregationBuilders.avg(ESConst.VM_TOTAL).field(ESConst.VM_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.VM_FREE).field(ESConst.VM_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.VM_USED).field(ESConst.VM_USED))
                .subAggregation(AggregationBuilders.avg(ESConst.PHY_TOTAL).field(ESConst.PHY_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.PHY_FREE).field(ESConst.PHY_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.PHY_USED).field(ESConst.PHY_USED))
                .subAggregation(AggregationBuilders.avg(ESConst.SWAP_TOTAL).field(ESConst.SWAP_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.SWAP_FREE).field(ESConst.SWAP_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.SWAP_USED).field(ESConst.SWAP_USED));

        srb.addAggregation(gradeTermsBuilder);


        srb.setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())));
        return srb;
    }

    private SearchRequestBuilder getSearchRequestBuilder(ESQueryCond esQueryCond, Client client) {
        SearchRequestBuilder srb = client.prepareSearch(ESConst.INDEX);
        srb.setTypes(ESConst.MEMORY_TYPE);
        srb.setSearchType(SearchType.COUNT);

        DateHistogramBuilder gradeTermsBuilder = AggregationBuilders
                .dateHistogram("By_Date")
                .field("collectTime")
                .format("yyyy-MM-dd HH:mm:ss" )
                .interval(DateHistogramInterval.minutes(esQueryCond.getGp()))
                .interval(DateHistogramInterval.minutes(esQueryCond.getGp()))
                .subAggregation(AggregationBuilders.avg(ESConst.VM_TOTAL).field(ESConst.VM_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.VM_FREE).field(ESConst.VM_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.VM_USED).field(ESConst.VM_USED))
                .subAggregation(AggregationBuilders.avg(ESConst.PHY_TOTAL).field(ESConst.PHY_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.PHY_FREE).field(ESConst.PHY_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.PHY_USED).field(ESConst.PHY_USED))
                .subAggregation(AggregationBuilders.avg(ESConst.SWAP_TOTAL).field(ESConst.SWAP_TOTAL))
                .subAggregation(AggregationBuilders.avg(ESConst.SWAP_FREE).field(ESConst.SWAP_FREE))
                .subAggregation(AggregationBuilders.avg(ESConst.SWAP_USED).field(ESConst.SWAP_USED));

        srb.addAggregation(gradeTermsBuilder);

        srb.setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_ID, esQueryCond.getAgentId()))
                .must(QueryBuilders.matchPhraseQuery(ESConst.AGENT_STARTTIME, esQueryCond.getAgentStartTime()))
                .must(QueryBuilders.rangeQuery(ESConst.COLLECT_TIME).from(esQueryCond.getFrom()).to(esQueryCond.getTo())))
                .addSort(ESConst.COLLECT_TIME, SortOrder.ASC);
        return srb;
    }

    private ESQueryResult getEsQueryResult(SearchResponse sr) {
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

        esQueryResult = new ESQueryResult(ESConst.MEMORY_TYPE, esMetricses);
        return esQueryResult;
    }

}
