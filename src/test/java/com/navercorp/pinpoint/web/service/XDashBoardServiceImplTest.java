package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.view.XApplicationDashBoard;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})
public class XDashBoardServiceImplTest {

    @Autowired
    private XServiceDashBoardServiceImpl xApplicationDashBoardService;

    @Autowired
    private XServiceDetailServiceImpl xServiceDetailService;

    @Ignore
    @Test
    public void should_return_applicationsDashBoard_when_input_is_Nothing() throws Exception {

    }

    @Ignore
    @Test
    public void should_return_applicationDashBoard_when_input_is_EMS() throws Exception {

        //givenfrom=&to=
        Range range = new Range(1482397213453L, 1484902813455L);

        //when
        XApplicationDashBoard xApplicationDashBoard = xApplicationDashBoardService.getXApplicationDashBoard("smartsight", range);


        String dashboardJson = new ObjectMapper().writeValueAsString(xApplicationDashBoard);

        //then
        System.out.println(dashboardJson);
    }

    @Ignore
    @Test
    public void should_return_serviceDashBoard_when_input_service_is_EMS_minos() throws Exception {

        //given
        String expectedServiceDashBoard = "{\"summary\":{\"appName\":\"EMS_minos\",\"healthRuleViolations\":0,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":1},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":704},\"calls\":\"710\",\"callsPerMin\":\"5.92\",\"responseTime\":\"492.31\",\"errorsPercent\":\"0.00\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\"},\"topo\":{\"nodes\":[{\"key\":\"USER\",\"name\":\"USER\",\"type\":\"Java\",\"count\":0,\"instances\":[]},{\"key\":\"EMS_uca\",\"name\":\"EMS_uca\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_uca\",\"value\":\"EMS_uca\"}]},{\"key\":\"minos1\",\"name\":\"minos1\",\"type\":\"Java\",\"count\":0,\"instances\":[]},{\"key\":\"EMS_uca\",\"name\":\"EMS_uca\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_uca\",\"value\":\"EMS_uca\"}]},{\"key\":\"EMS_minos\",\"name\":\"EMS_minos\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_minos\",\"value\":\"EMS_minos\"}]}],\"links\":[{\"from\":\"USER\",\"to\":\"EMS_minos\",\"respondTime\":\"709calls/0errors/0ms\"},{\"from\":\"EMS_uca\",\"to\":\"EMS_minos\",\"respondTime\":\"1calls/0errors/1011ms\"},{\"from\":\"EMS_minos\",\"to\":\"EMS_uca\",\"respondTime\":\"346calls/0errors/1008ms\"},{\"from\":\"EMS_minos\",\"to\":\"minos1\",\"respondTime\":\"726calls/0errors/0ms\"}]},\"loadInfo\":{\"info\":\"todo\",\"time\":[],\"data\":[]},\"respondInfo\":{\"info\":\"todo\",\"time\":[],\"data\":[]},\"errorInfo\":{\"info\":\"todo\",\"time\":[],\"data\":[]}}";
        Range range = new Range(1471244572780L, 1471245172780L);

        //when

        XServiceDashBoard xServiceDashBoard = xServiceDetailService.getXServiceDashBoard("EMS", "EMS_minos", range);
        String dashboardJson = new ObjectMapper().writeValueAsString(xServiceDashBoard);

        //then
        assertThat(dashboardJson, is(expectedServiceDashBoard));

    }

    @Ignore
    @Test
    public void should_return_instanceDashBoard_when_input_instance_is_EMS_minos() throws Exception {

        //given
        String expectedServiceDashBoard = "{\"summary\":{\"appName\":\"EMS_minos\",\"healthRuleViolations\":0,\"nodeHealth\":{\"critical\":1,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":704},\"calls\":\"710\",\"callsPerMin\":\"5.92\",\"responseTime\":\"492.31\",\"errorsPercent\":\"0.00\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\"},\"topo\":{\"nodes\":[{\"key\":\"USER\",\"name\":\"USER\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_minos\",\"value\":\"EMS_minos\"}]},{\"key\":\"EMS_uca\",\"name\":\"EMS_uca\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_minos\",\"value\":\"EMS_minos\"}]},{\"key\":\"minos1\",\"name\":\"minos1\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_minos\",\"value\":\"EMS_minos\"}]},{\"key\":\"EMS_uca\",\"name\":\"EMS_uca\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_minos\",\"value\":\"EMS_minos\"}]},{\"key\":\"EMS_minos\",\"name\":\"EMS_minos\",\"type\":\"Java\",\"count\":1,\"instances\":[{\"id\":\"EMS_minos\",\"value\":\"EMS_minos\"}]}],\"links\":[{\"from\":\"USER\",\"to\":\"EMS_minos\",\"respondTime\":\"709calls/0errors/0ms\"},{\"from\":\"EMS_uca\",\"to\":\"EMS_minos\",\"respondTime\":\"1calls/0errors/1011ms\"},{\"from\":\"EMS_minos\",\"to\":\"EMS_uca\",\"respondTime\":\"346calls/0errors/1008ms\"},{\"from\":\"EMS_minos\",\"to\":\"minos1\",\"respondTime\":\"726calls/0errors/0ms\"}]},\"loadInfo\":{\"info\":\"todo\",\"time\":[],\"data\":[]},\"respondInfo\":{\"info\":\"todo\",\"time\":[],\"data\":[]},\"errorInfo\":{\"info\":\"todo\",\"time\":[],\"data\":[]}}";
        Range range = new Range(1471244572780L, 1471245172780L);

        //when
        XInstanceDashBoard xInstanceDashBoard = xServiceDetailService.getXInstanceDetail("EMS", "EMS_minos", "EMS_minos", range);

        String dashboardJson = new ObjectMapper().writeValueAsString(xInstanceDashBoard);

        //then
        assertThat(dashboardJson, is(expectedServiceDashBoard));

    }
}