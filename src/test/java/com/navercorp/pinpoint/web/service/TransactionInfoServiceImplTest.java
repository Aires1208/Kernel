package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.calltree.span.CallTreeIterator;
import com.navercorp.pinpoint.web.vo.TransactionId;
import com.navercorp.pinpoint.web.vo.callstacks.RecordSet;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})

public class TransactionInfoServiceImplTest {

    @Autowired
    private SpanService spanService;

    @Autowired
    private TransactionInfoService transactionInfoService;

    @Ignore
    @Test
    public void testSelectBusinessTransactions() throws Exception {

    }

    @Ignore
    @Test
    public void testCreateRecordSet() throws Exception {
        //given
        TransactionId traceId = new TransactionId("test-agent^1464073841789^11");
        long focusTimestamp = 1464917892535L;

        //when
        // select spans
        final SpanResult spanResult = this.spanService.selectSpan(traceId, focusTimestamp);
        final CallTreeIterator callTreeIterator = spanResult.getCallTree();

        RecordSet recordSet = this.transactionInfoService.createRecordSet(callTreeIterator, focusTimestamp);


        //then
        System.out.println("ended");

    }
}