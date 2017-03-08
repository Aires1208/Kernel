package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.view.XInstanceDashBoard;
import com.navercorp.pinpoint.web.view.XServiceDashBoard;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

/**
 * Created by root on 17-2-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})
public class XServiceDetailServiceImplTest {

    @Autowired
    private XServiceDetailServiceImpl serviceDetailService;


//    http://10.62.100.151:8084/dashBoard/applications/ranoss/services/ranoss_common-nimbus/instances/common-nimbus.pinpoint?from=1484791553892&to=1487297153892
    @Ignore
    @Test
    public void getXInstanceDetail() throws Exception {
        XInstanceDashBoard instanceDashBoard = serviceDetailService.getXInstanceDetail("ranoss", "ranoss_common-nimbus", "common-nimbus", new Range(1484791553892L, 1487297153892L));

        System.out.println("");
    }

//    http://10.62.100.151:8084/dashBoard/applications/ranoss/services/192.168.100.8:10081.pinpoint?from=1485071573532&to=1487577173532
    @Ignore
    @Test
    public void getService() throws Exception {

        XServiceDashBoard serviceDashBoard = serviceDetailService.getXServiceDashBoard("ranoss", "192.168.100.8:10081", new Range(1485071573532L, 1487577173532L));

        System.out.println("");
    }

//    http://10.62.100.164:8084/dashBoard/applications/TEST/services/TEST_APP-test-appb.pinpoint?from=1487745577380&to=1487752777380
    @Ignore
    @Test
    public void mqtest() throws Exception {

        XServiceDashBoard serviceDashBoard = serviceDetailService.getXServiceDashBoard("TEST", "TEST_APP-test-appb", new Range(1487745577380L, 1487752777380L));

        String service = new ObjectMapper().writeValueAsString(serviceDashBoard);

        System.out.println(service);
    }
}