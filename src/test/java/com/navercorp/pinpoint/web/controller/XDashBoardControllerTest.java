package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.common.topo.domain.*;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.service.XApplicationsService;
import com.navercorp.pinpoint.web.service.XJVMDashBoardServiceImpl;
import com.navercorp.pinpoint.web.service.XServiceDashBoardServiceImpl;
import com.navercorp.pinpoint.web.service.XServiceDetailServiceImpl;
import com.navercorp.pinpoint.web.view.*;
import com.navercorp.pinpoint.web.vo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by root on 17-1-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class XDashBoardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private XServiceDashBoardServiceImpl xServiceDashBoardService;

    @Mock
    private XApplicationsService xApplicationsService;

    @Mock
    private XServiceDetailServiceImpl xServiceDetailService;

    @Mock
    private XJVMDashBoardServiceImpl xJvmDashBoardService;

    @InjectMocks
    private XDashBoardController controller = new XDashBoardController();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getApplications() throws Exception {
        Mockito.when(xApplicationsService.getXApplicationsDashBoard(any(Range.class))).thenReturn(buildApplications());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/applications")
                .param("from", "100")
                .param("to", "200")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"summary\":[{\"appName\":\"fm\",\"healthRuleViolations\":0,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"calls\":\"0\",\"callsPerMin\":\"0.00\",\"responseTime\":\"0.00\",\"errorsPercent\":\"0.00%\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\"}]}"));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/applications")
                .param("from", "100L")
                .param("to", "200L"))
                .andExpect(status().isBadRequest());
    }

    private XApplicationsDashBoard buildApplications() {
        XService xService = new XService("fm-active", ServiceType.SPRING);
        XApplication xApplication = new XApplication("fm", newArrayList(xService));

        return new XApplicationsDashBoard(newArrayList(xApplication));
    }

    @Test
    public void getApplication() throws Exception {
        Mockito.when(xServiceDashBoardService.getXApplicationDashBoard(anyString(), any(Range.class))).thenReturn(buildApplication());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/applications/{application}", "fm-active")
                .param("from", "10")
                .param("to", "5000")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"summary\":{\"appName\":\"fm\",\"healthRuleViolations\":0,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"calls\":\"32\",\"callsPerMin\":\"0.00\",\"responseTime\":\"93.75\",\"errorsPercent\":\"0.00%\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\"},\"topo\":{\"nodes\":[{\"key\":\"user\",\"name\":\"user\",\"type\":\"USER\",\"tracked\":\"false\",\"instances\":[]},{\"key\":\"fm-active\",\"name\":\"fm-active\",\"type\":\"JAVA\",\"tracked\":\"false\",\"instances\":[]}],\"links\":[{\"from\":\"user\",\"to\":\"fm-active\",\"respondTime\":\"32calls/0errors/93ms\"}]},\"loadInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[23,23,23]},\"respondInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[10.17,10.17,10.17]},\"errorInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[1,1,1]}}"));
    }

    private XApplicationDashBoard buildApplication() {
        XService xService = new XService("fm-active", ServiceType.SPRING);
        XApplication xApplication = new XApplication("fm", newArrayList(xService));

        XServiceTopo xServiceTopo = getxServiceTopo();

        XApplicationDashBoard dashBoard = new XApplicationDashBoard(xApplication, xServiceTopo, new Range(150L, 3000L));

        XDot xDot = new XDot(223L);
        xDot.setxMetric(new XMetric(234L, 23L, 1L));
        dashBoard.setLoadInfo(new XLoadInfo(newArrayList(xDot), new Range(150L, 3000L)));

        return dashBoard;
    }

    private XServiceTopo getxServiceTopo() {
        return new XServiceTopo(newArrayList(
                new XNode("user", ServiceType.USER.getCode(), 0, 0, 0),
                new XNode("fm-active", ServiceType.STAND_ALONE.getCode(), 3000, 0, 32)),
                newArrayList(new XLink("user", "fm-active", 3000, 0, 32)));
    }

    @Test
    public void getService() throws Exception {
        Mockito.when(xServiceDetailService.getXServiceDashBoard(anyString(), anyString(), any(Range.class))).thenReturn(buildService());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/applications/{application}/services/{service}", "fm", "fm-active")
                .accept(MediaType.APPLICATION_JSON)
                .param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"summary\":{\"appName\":\"fm-active\",\"healthRuleViolations\":0,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"calls\":\"0\",\"callsPerMin\":\"0.00\",\"responseTime\":\"0.00\",\"errorsPercent\":\"0.00\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\"},\"topo\":{\"nodes\":[{\"key\":\"user\",\"name\":\"user\",\"type\":\"USER\",\"tracked\":\"false\",\"instances\":[]},{\"key\":\"fm-active\",\"name\":\"fm-active\",\"type\":\"JAVA\",\"tracked\":\"false\",\"instances\":[]}],\"links\":[{\"from\":\"user\",\"to\":\"fm-active\",\"respondTime\":\"32calls/0errors/93ms\"}]},\"loadInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[23,23,23]},\"respondInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[10.17,10.17,10.17]},\"errorInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[1,1,1]}}"));
    }

    private XServiceDashBoard buildService() {
        XService xService = new XService("fm-active", ServiceType.SPRING);
        XDot xDot = new XDot(223L);
        xDot.setxMetric(new XMetric(234L, 23L, 1L));
        return new XServiceDashBoard(xService, getxServiceTopo(), new XLoadInfo(newArrayList(xDot), new Range(150L, 3000L)));
    }

    @Test
    public void getInstance() throws Exception {
        Mockito.when(xServiceDetailService.getXInstanceDetail(anyString(), anyString(), anyString(), any(Range.class))).thenReturn(buildInstance());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/applications/{application}/services/{service}/instances/{instance}", "fm", "fm_active", "fm-agent")
                .accept(MediaType.APPLICATION_JSON)
                .param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"summary\":{\"appName\":\"fm-agent\",\"healthRuleViolations\":0,\"nodeHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"transactionHealth\":{\"critical\":0,\"warning\":0,\"normal\":0},\"calls\":\"0\",\"callsPerMin\":\"0.00\",\"responseTime\":\"0.00\",\"errorsPercent\":\"0.00\",\"errors\":\"0\",\"errorsPerMin\":\"0.00\",\"serverIp\":null,\"hostId\":\"null@null\",\"pid\":\"0\",\"serviceType\":\"UNKNOWN\",\"runIn\":\"Server\",\"agentId\":null,\"agentVersion\":null,\"startTime\":\"70-01-01 08:00\",\"status\":\"Unknown\"},\"topo\":{\"nodes\":[{\"key\":\"user\",\"name\":\"user\",\"type\":\"USER\",\"tracked\":\"false\",\"instances\":[]},{\"key\":\"fm-active\",\"name\":\"fm-active\",\"type\":\"JAVA\",\"tracked\":\"false\",\"instances\":[]}],\"links\":[{\"from\":\"user\",\"to\":\"fm-active\",\"respondTime\":\"32calls/0errors/93ms\"}]},\"loadInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[23,23,23]},\"respondInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[10.17,10.17,10.17]},\"errorInfo\":{\"info\":\"\",\"time\":[\"70-01-01 08:00\",\"70-01-01 08:00\",\"70-01-01 08:00\"],\"data\":[1,1,1]}}"));
    }

    private XInstanceDashBoard buildInstance() {
        XInstance instance = new XInstance("fm-agent", ServiceType.SPRING);
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setStatus(new AgentStatus("fm-active"));
        instance.setAgentInfo(agentInfo);
        XDot xDot = new XDot(223L);
        xDot.setxMetric(new XMetric(234L, 23L, 1L));
        return new XInstanceDashBoard(instance, getxServiceTopo(), new XLoadInfo(newArrayList(xDot), new Range(150L, 3000L)));
    }

    @Test
    public void getInstanceJVM() throws Exception {
        Mockito.when(xJvmDashBoardService.getXJVMDashBoard(anyString(), any(Range.class))).thenReturn(buildJvm());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/applications/{application}/services/{service}/instances/{instance}/agentids/{agentid}", "fm", "fm_active", "fm-agent", "fm-agent")
                .accept(MediaType.APPLICATION_JSON)
                .param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"heapInfo\":{\"info\":null,\"time\":[\"17-01-18 15:54\"],\"max\":[1],\"used\":[2],\"fgc\":[3]},\"permGen\":{\"info\":null,\"time\":[\"17-01-18 15:54\"],\"max\":[1],\"used\":[2],\"fgc\":[3]},\"jvmSys\":{\"info\":null,\"time\":[\"17-01-18 15:54\"],\"jvm\":[1.0],\"sys\":[2.0]},\"tps\":{\"info\":null,\"time\":[\"17-01-18 15:54\"],\"sc\":[1],\"sn\":[2],\"uc\":[3],\"un\":[4]},\"vmargs\":\"\",\"jvmVersion\":null,\"gcTypeName\":null}"));
    }

    private XJVMDashBoard buildJvm() {
        HeapInfo heapInfo = new HeapInfo(new String[]{"17-01-18 15:54"}, new long[]{1}, new long[]{2}, new long[]{3});
        PermGen permGen = new PermGen(new String[]{"17-01-18 15:54"}, new long[]{1}, new long[]{2}, new long[]{3});
        CpuUsage cpuUsage = new CpuUsage(new String[]{"17-01-18 15:54"}, new double[]{1.0}, new double[]{2.0});
        TransactionsPerSecond transactionsPerSecond = new TransactionsPerSecond(new String[]{"17-01-18 15:54"}, new long[]{1}, new long[]{2}, new long[]{3}, new long[]{4});
        return new XJVMDashBoard(heapInfo, permGen, cpuUsage, transactionsPerSecond);
    }

    @Test
    public void getApplicationsTest() throws Exception {
        Mockito.when(xApplicationsService.getFullAppList()).thenReturn(buildApps());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/dashBoard/fullASI")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"apps\":[{\"name\":\"fm-active\",\"level\":\"application\",\"services\":[{\"name\":\"fm-active\",\"appName\":\"fm-active\",\"level\":\"service\",\"instances\":[{\"name\":\"fm-agent\",\"appName\":\"fm-active\",\"serviceName\":\"fm-active\",\"level\":\"instance\"}]}]}]}"));
    }

    private List<XApplication> buildApps() {
        XService xService = new XService("fm-active", ServiceType.SPRING);
        xService.setAgentIds(newArrayList("fm-agent"));
        XApplication xApplication = new XApplication("fm-active", newArrayList(xService));
        return newArrayList(xApplication);
    }

}