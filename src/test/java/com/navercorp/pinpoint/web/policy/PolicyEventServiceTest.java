package com.navercorp.pinpoint.web.policy;

import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.service.XApplicationsService;
import com.navercorp.pinpoint.web.service.XBusinessTransactions;
import com.navercorp.pinpoint.web.service.XHostsService;
import com.navercorp.pinpoint.web.service.XTransactionServiceImpl;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by root on 17-1-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PolicyEventServiceTest {

    @Mock
    private XHostsService serverStatService;

    @Mock
    private XApplicationsService xApplicationsService;

    @Mock
    private XTransactionServiceImpl transactionService;

    @Mock
    private AgentStatDao agentStatDao;

    @Mock
    private ApplicationIndexDao applicationIndexDao;

    @InjectMocks
    private PolicyEventService policyEventService = new PolicyEventService();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private XTransactions getTransaction() {
        XTransactionName transactionName = new XTransactionName("fm_active", "fm-active", "/getTimestamp");
        Map<XTransactionName, XBusinessTransactions> transactionsMap = newHashMap();
        XBusinessTransactions xBusinessTransactions = new XBusinessTransactions(transactionName);
        XBusinessTransaction xBusinessTransaction = new XBusinessTransaction(new TransactionId("fm-active", 123L, 456L), getSpans());
        xBusinessTransactions.add(xBusinessTransaction);


        transactionsMap.put(transactionName, xBusinessTransactions);
        return new XTransactions(transactionsMap, new Range(0, 1000L), newArrayList(new XApplication("fm", getXService())));
    }

    private List<SpanBo> getSpans() {
        SpanBo spanBo = new SpanBo("fm-agent", 123L, 456L, 125L, 6, 1L);
        spanBo.setRpc("/getTimestamp");
        spanBo.setErrCode(1);
        spanBo.setApplicationId("fm_active");

        return newArrayList(spanBo);
    }

    @Test
    public void buildMessage() throws Exception {
        //given
        long timestamp = 122L;

        when(xApplicationsService.getXApplications()).thenReturn(newArrayList(new XApplication("fm", getXService())));
        when(transactionService.getXAppTransactions(anyString(), any(Range.class))).thenReturn(getTransaction());
        when(transactionService.getXServiceTransactions(anyString(), any(Range.class))).thenReturn(getTransaction());
        when(applicationIndexDao.selectAgentIds(anyString())).thenReturn(newArrayList("fm-active"));
        when(transactionService.getInstanceTransactions(anyString(), any(Range.class))).thenReturn(getTransaction());
        when(serverStatService.getXHosts()).thenReturn(newHashSet(buildXHost()));
        when(agentStatDao.agentStatExists(anyString(), any(Range.class))).thenReturn(true);
        when(agentStatDao.getAgentStatList(anyString(), any(Range.class))).thenReturn(getAgentStat());

        List<String> policyEvent = policyEventService.buildMessage(timestamp);

        //then
        assertEquals(policyEvent.size(), 37);
    }

    private List<AgentStat> getAgentStat() {
        AgentStat stat1 = new AgentStat("fm-active", 150L);
        stat1.setSystemCpuUsage(0.2154);
        stat1.setMemUsed(100000L);
        stat1.setMemTotal(200000L);
        stat1.setDiskUsage(0.4512);
        stat1.setInSpeed(12 * 1024L);
        stat1.setOutSpeed(9 * 1024L);
        stat1.setSpeed(100L * 1024 * 1024);
        stat1.setHeapMax(1000L);
        stat1.setHeapUsed(400L);
        stat1.setGcOldTime(34L);
        return newArrayList(stat1);
    }

    private XHost buildXHost() {
        XHost.Builder builder = XHost.Builder();
        builder.agents(newHashSet(new AgentInfo()));
        builder.hostname("localhost");
        builder.mac("unknow-mac");
        return builder.build();
    }

    private List<XService> getXService() {
        return newArrayList(new XService("fm_active", ServiceType.SPRING));
    }

}