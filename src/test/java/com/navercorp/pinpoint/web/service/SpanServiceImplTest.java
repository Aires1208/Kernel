package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.TransactionId;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})

public class SpanServiceImplTest {

    @Autowired
    private SpanService spanService;

    @Ignore
    @Test
    public void testSelectSpan() throws Exception {
        System.out.println("beginning");
        TransactionId traceId = new TransactionId("test-agent^1464073841789^1");
        SpanResult spanResult = spanService.selectSpan(traceId,1464579338326L);
        System.out.println("ended");

    }
}