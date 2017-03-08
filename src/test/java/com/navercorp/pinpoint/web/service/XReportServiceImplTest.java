package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.view.XAppReport;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by root on 16-9-23.
 */
//@RunWith(MockitoJUnitRunner.class)
public class XReportServiceImplTest {
    private XEventService eventService;

    @Autowired
    private XReportServiceImpl reportService;

//    @Before
    public void setUp() {
        this.eventService = mock(XEventService.class);

        List<ResultEvent> serviceEventList = ImmutableList.of(
                new ResultEvent("app=fmnf, service=fmnf_active", 10071, System.currentTimeMillis() - 60000, System.currentTimeMillis(), "APP_RESPONSETIMETOOLONG_WARNING"),
                new ResultEvent("app=fmnf, service=fmnf_active", 10062, System.currentTimeMillis() - 60000, System.currentTimeMillis(), "APP_ERRORRATIOOVERBASELINE_CRITICAL"));

        List<ResultEvent> transactionEventList = ImmutableList.of(
                new ResultEvent("app=fmnf, transactionName=/favicon.ico", 40011, System.currentTimeMillis() - 60000, System.currentTimeMillis(), "TRANSACTION_CALLHEAVY_WARNING"),
                new ResultEvent("app=fmnf, transactionName=/favicon.ico", 40022, System.currentTimeMillis() - 60000, System.currentTimeMillis(), "TRANSACTION_CALLOVERBASELINE_CRITICAL")
        );

        List<ResultEvent> instanceEventList = ImmutableList.of(
                new ResultEvent("app=fmnf, service=fmnf_active, instance=fmactive-agent", 10041, System.currentTimeMillis() - 60000, System.currentTimeMillis(), "APP_RESPONSETIMETOOLONG_WARNING"),
                new ResultEvent("app=fmnf, service=fmnf_active, instance=fmactive-agent", 10082, System.currentTimeMillis() - 60000, System.currentTimeMillis(), "APP_ERRORRATIOOVERBASELINE_CRITICAL"));

        when(eventService.getAppEvents(anyString(), anyObject())).thenReturn(serviceEventList);
        when(eventService.getAppTransactionEvents(anyString(), anyObject())).thenReturn(transactionEventList);
        when(eventService.getServiceEvents(anyString(), anyString(), anyObject())).thenReturn(instanceEventList);
        reportService.setEventService(eventService);
    }

//    @Test
    public void getAppReport() throws Exception {
        //given
        String app = "fmnf";
        int topN = 5;
        Range range = new Range(System.currentTimeMillis() -14 * 24 * 60 * 60000, System.currentTimeMillis());

        //when
        XAppReport appReport = reportService.getAppReport(app, topN, range);

        //then
        String appReportStr= new ObjectMapper().writeValueAsString(appReport);
        System.out.println(appReportStr);
    }

}