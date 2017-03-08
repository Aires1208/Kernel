package com.navercorp.pinpoint.web.service;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.TransactionListDao;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XService;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XTracesListServiceImplTest {

    @Mock
    private TransactionListDao transactionListDao;

    @Mock
    private XApplicationsService applicationsService;

    @InjectMocks
    private XTracesListService tracesListService = new XTracesListServiceImpl();


    private XTransactionName trace1 = new XTransactionName("EMS_minos", "8502", 1477115974000L, "minos-agent");
    private XTransactionName trace2 = new XTransactionName("EMS_uca", "getAlarm", 1477115923232L, "uca-agent");
    private List<XService> serviceList = ImmutableList.of(new XService("EMS_minos", ServiceType.TEST), new XService("EMS_uca", ServiceType.TEST));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAppTracesList() throws Exception {
        //given
        String appName = "EMS";
        Range range = new Range(1477115972000L, 1477115974000L);
        List<XTransactionName> tracesList = ImmutableList.of(trace1, trace2);

        when(applicationsService.getXServices("EMS")).thenReturn(serviceList);
        when(transactionListDao.getServiceTracesList(eq("EMS_minos"), anyObject())).thenReturn(newArrayList(trace1));
        when(transactionListDao.getServiceTracesList(eq("EMS_uca"), anyObject())).thenReturn(newArrayList(trace2));

        //when
        List<XTransactionName> AppTraceList = tracesListService.getAppTracesList(appName, range);

        //then
        assertThat(AppTraceList, is(tracesList));
    }

    @Test
    public void getServiceTracesList() throws Exception {
        //given
        String serviceName = "EMS_minos";
        Range range = new Range(1477115972000L, 1477115974000L);

        //when
        when(transactionListDao.getServiceTracesList(eq("EMS_minos"), anyObject())).thenReturn(newArrayList(trace1));
        List<XTransactionName> servTracesList = tracesListService.getServiceTracesList(serviceName, range);

        //then
        assertThat(servTracesList, is(newArrayList(new XTransactionName("EMS_minos", "8502", 1477115974000L, "minos-agent"))));
    }

    @Test
    public void getInstTracesList() throws Exception {
        //given
        String serviceName = "EMS_uca";
        String agentId = "uca-agent";
        Range range = new Range(1477115972000L, 1477115974000L);

        //when
        when(transactionListDao.getServiceTracesList(eq("EMS_uca"), anyObject())).thenReturn(newArrayList(trace2));
        List<XTransactionName> instTracesList = tracesListService.getInstTracesList(serviceName, agentId, range);

        //then
        assertThat(instTracesList, is(newArrayList(new XTransactionName("EMS_uca", "getAlarm", 1477115923232L, "uca-agent"))));
    }

}