/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.web.view.XTransScatter;
import com.navercorp.pinpoint.web.view.XTransScatters;
import com.navercorp.pinpoint.web.dao.ApplicationTraceIndexDao;
import com.navercorp.pinpoint.web.dao.TraceDao;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.stream.Collectors.toList;

/**
 * @author sinwaj
 */
@Service
public class XTransScatterServiceImpl {

    @Autowired
    private TraceDao traceDao;

    @Autowired
    private ApplicationTraceIndexDao applicationTraceIndexDao;

    @Autowired
    private XApplicationsServiceImpl xApplicationsService;

    @Autowired
    private XInstanceServiceImpl xInstanceService;

    public XTransScatters getXAppTransScatters(String appName, Range range) {
        if (appName == null)
        {
            throw new NullPointerException("appName must not be null!");
        }

        List<XService> xServiceList = xApplicationsService.getXServices(appName);

        List<XBusinessTransaction> transactions = newArrayList();

        for (XService xService : xServiceList)
        {
            Set<TransactionId> transactionIds = getTransactionIds(xService.getName(), range);
            transactions.addAll(getTransactions(transactionIds));
        }


        return getScatters(transactions);
    }



    public XTransScatters getScattersFilterBySpanBo(List<SpanBo> SpanBos) {
        List<XTransScatter> normals = SpanBos.stream()
                .filter(item->item.getElapsed() <= 1000 && !item.hasException())
                .map(item -> new XTransScatter(item.getStartTime(), item.getElapsed()))
                .collect(toList());
        List<XTransScatter> warnings = SpanBos.stream()
                .filter(item->item.getElapsed() > 1000 && !item.hasException())
                .map(item -> new XTransScatter(item.getStartTime(), item.getElapsed()))
                .collect(toList());

        List<XTransScatter> criticals = SpanBos.stream()
                .filter(item-> item.hasException())
                .map(item -> new XTransScatter(item.getStartTime(), item.getElapsed()))
                .collect(toList());

        return new XTransScatters.Builder()
                .Normals(normals)
                .Warings(warnings)
                .Criticals(criticals)
                .build();
    }

    private XTransScatters getScatters(List<XBusinessTransaction> transactions) {
        List<XTransScatter> normals = transactions.stream()
                .filter(item->item.getElapsed() <= 1000 && !item.hasExcetpion())
                .map(item -> new XTransScatter(item.getStartTime(), item.getElapsed()))
                .collect(toList());
        List<XTransScatter> warnings = transactions.stream()
                .filter(item->item.getElapsed() > 1000 && !item.hasExcetpion())
                .map(item -> new XTransScatter(item.getStartTime(), item.getElapsed()))
                .collect(toList());

        List<XTransScatter> criticals = transactions.stream()
                .filter(item-> item.hasExcetpion())
                .map(item -> new XTransScatter(item.getStartTime(), item.getElapsed()))
                .collect(toList());

        return new XTransScatters.Builder()
                .Normals(normals)
                .Warings(warnings)
                .Criticals(criticals)
                .build();
    }


    public XTransScatters getXServiceScatters(String serviceName, Range range) {
        if (serviceName == null) {
            throw new NullPointerException("serviceName must not be null!");
        }

        List<XBusinessTransaction> xBusinessTransactions = getTransactions(getTransactionIds(serviceName, range));

        return getScatters(xBusinessTransactions);
    }

    public XTransScatters getInstanceScatters(String instanceName, Range range) {
        if (instanceName == null) {
            throw new NullPointerException("instanceName must not be null!");
        }

        Set<TransactionId> transactionIds = getTransactionIds(xInstanceService.getXServiceName(instanceName), range);
        List<XBusinessTransaction> xBusinessTransactionList = newArrayList();
        for(TransactionId transactionId : transactionIds) {
            List<SpanBo> spanBos = traceDao.selectSpan(transactionId);
            if (isTraced(instanceName, spanBos)) {
                xBusinessTransactionList.add(new XBusinessTransaction(transactionId, spanBos).build());
            }
        }

        return getScatters(xBusinessTransactionList);
    }




    private List<XBusinessTransaction> getTransactions(Set<TransactionId> transactionIds) {
        List<XBusinessTransaction> xBusinessTransactions = newArrayList();

        for(TransactionId transactionId : transactionIds) {
            List<SpanBo> spanBos = traceDao.selectSpan(transactionId);
            xBusinessTransactions.add(new XBusinessTransaction(transactionId,spanBos).build());
        }

        return xBusinessTransactions;

    }

    private Set<TransactionId>  getTransactionIds(String applicationName,Range range) {
        LimitedScanResult<List<TransactionId>> limitedScanResult =  applicationTraceIndexDao.scanTraceIndex(applicationName,range,5000,false);
        List<TransactionId> transactionIds = limitedScanResult.getScanData();

        return new HashSet<TransactionId>(transactionIds);
    }



    private boolean isTraced(String name, List<SpanBo> spanBos) {
        for (SpanBo spanBo : spanBos) {
            if (spanBo.getAgentId().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
