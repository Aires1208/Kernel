package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

/**
 * Created by ${10183966} on 11/24/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})

public class XRpcStatisticServiceImplTest {
    @Autowired
    private XRpcStatisticService xRpcStatisticService;

    @Ignore
    @Test
    public void getXRpcStatisticList() throws Exception {

        System.out.println(xRpcStatisticService.getXRpcStatisticList("FM1", new Range(System.currentTimeMillis() - 1000 * 60 * 60 * 24, System.currentTimeMillis())));

    }

}