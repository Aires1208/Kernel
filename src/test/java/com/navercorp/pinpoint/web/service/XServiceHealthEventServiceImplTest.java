package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.view.XServiceCallsEvents;
import com.navercorp.pinpoint.web.view.XServiceHealthEvents;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by root on 8/30/16.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})
public class XServiceHealthEventServiceImplTest {
    @Autowired
    XServicesRiskEventServiceImpl xServiceHealthEventInfoService;


    @Test
    public void testCalcServiceHealthEventInfo() throws Exception {
        //given
        String appName = "fmnf";
        Range range = new Range(System.currentTimeMillis() - 20 * 24 * 60 *60000, System.currentTimeMillis());

        //when
        XServiceHealthEvents xServiceHealthEventInfo = xServiceHealthEventInfoService.calcServiceHealthEvents(appName, range);

        //then
        String jsonStr = new ObjectMapper().writeValueAsString(xServiceHealthEventInfo);
        System.out.println("end");
    }

    @Test
    public void testCalcServiceCallsEvents() throws JsonProcessingException {
        //given
        String appName = "fm";
        Range range = new Range(System.currentTimeMillis() - 200 * 60 *60000, System.currentTimeMillis());

        //when
        XServiceCallsEvents xServiceCallsEvents = xServiceHealthEventInfoService.getServiceCallsEvents(appName, range);

        //then
        String jsonStr = new ObjectMapper().writeValueAsString(xServiceCallsEvents);
        System.out.println("end");
    }
}