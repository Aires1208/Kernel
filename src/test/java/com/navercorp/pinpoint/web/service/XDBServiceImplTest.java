package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.view.XDBRiskEvents;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XDBsRisk;
import org.apache.htrace.fasterxml.jackson.core.JsonProcessingException;
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
public class XDBServiceImplTest {

    @Autowired
    private XDBServiceImpl xdbService;

    @Test
    public void getDbInfoTest() {
        //given
        String appName = "fmnf";
        Range range = new Range(System.currentTimeMillis() - 20 * 24 * 60 * 60000, System.currentTimeMillis());

        //when
        XDBsRisk xdBsRisk = xdbService.getXDBsRisk(appName, 5, range);

        //then
        System.out.println("end");
    }

    @Test
    public void getDBRiskEventsTest() throws JsonProcessingException {
        //given
        String appName = "fmnf";
        String dbName="uep4x_caf_fm";
        Range range = new Range(System.currentTimeMillis() - 20 * 24 * 60 * 60000, System.currentTimeMillis());

        //when
        XDBRiskEvents xdbRiskEvents = xdbService.getDBRiskEvents(appName, dbName, range);

        //then
        String jsonStr = null;
        try {
            jsonStr = new ObjectMapper().writeValueAsString(xdbRiskEvents);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonStr);
    }

}