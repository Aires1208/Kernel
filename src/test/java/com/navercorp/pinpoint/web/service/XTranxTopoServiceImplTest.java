package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XServiceTopo;
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
public class XTranxTopoServiceImplTest {
    @Autowired
    private XTranxTopoServiceImpl tranxTopoService;

    @Ignore
    @Test
    public void getAppTranxTopo() throws Exception {
        //given
        String appName = "IaasOps";
        String command = "";
        Range range = new Range(System.currentTimeMillis() - 20 * 60000, System.currentTimeMillis());

        //when
        XServiceTopo serviceTopo = tranxTopoService.getAppTranxTopo(appName, command, range);

        //then
        String scattersJson = new ObjectMapper().writeValueAsString(serviceTopo);
        System.out.println("end");

    }

}