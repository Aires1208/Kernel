package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.view.XTransScatters;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})

public class XTransScatterServiceImplTest {
    @Autowired
    private XTransScatterServiceImpl xTransScatterService;

    @Test
    public void testGetXAppTransScatters() throws Exception {
        //given
        String appName = "EMS";
        Range range = new Range(0L, System.currentTimeMillis());

        //when
        XTransScatters xTransScatters = xTransScatterService.getXAppTransScatters(appName, range);

        //then
        String scattersJson = new ObjectMapper().writeValueAsString(xTransScatters);
        System.out.println("end");
    }

    @Test
    public void testGetXServiceScatters() throws Exception {
        //given
        String service = "EMS_minos";
        Range range = new Range(0L, System.currentTimeMillis());

        //when
        XTransScatters xTransScatters = xTransScatterService.getXServiceScatters(service, range);

        //then
        String scattersJson = new ObjectMapper().writeValueAsString(xTransScatters);
        System.out.println("end");
    }

    @Test
    public void testGetInstanceScatters() throws Exception {
        //given
        String instance = "EMS_minos";
        Range range = new Range(0L, System.currentTimeMillis());

        //when
        XTransScatters xTransScatters = xTransScatterService.getInstanceScatters(instance, range);

        //then
        String scattersJson = new ObjectMapper().writeValueAsString(xTransScatters);
        System.out.println("end");
    }
}