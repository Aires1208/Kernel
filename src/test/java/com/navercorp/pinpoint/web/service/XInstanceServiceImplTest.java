package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XServiceTopo;
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

public class XInstanceServiceImplTest {

    @Autowired
    private XInstanceServiceImpl xInstanceService;

    @Ignore
    @Test
    public void testGetXServiceName() throws Exception {


        String serviceName = xInstanceService.getXServiceName("test-agent");

        System.out.println(serviceName);

    }

    @Ignore
    @Test
    public void testGetInstanceTopo() {
        //given
        String appName = "EMS";
        String instanceName = "main_agent1";
        Range range = new Range(0, System.currentTimeMillis());

        //when
        XServiceTopo serviceTopo = xInstanceService.getInstanceTopo(appName, range, instanceName);

        //then
        System.out.println(serviceTopo);
    }
}