package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XHostService;
import com.navercorp.pinpoint.web.service.XHostsService;
import com.navercorp.pinpoint.web.view.HostStat;
import com.navercorp.pinpoint.web.view.StatLine;
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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by root on 17-1-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class XServerStatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private XHostsService xHostsService;

    @Mock
    private XHostService xHostService;

    @InjectMocks
    private XHostsController controller = new XHostsController();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getHostsDashboard() throws Exception {
        StatLine statLine = new StatLine(0.8, 0.7, 0.6, 0.5);
        Mockito.when(xHostsService.getHostsDashBoard(any(Range.class)))
                .thenReturn(newArrayList(getHostStat(statLine)));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/serverstats")
                .param("from", "1")
                .param("to", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"servers\":[{\"application\":\"fm\",\"service\":\"active\",\"hostid\":\"testHost\",\"ip\":\"127.0.0.1\",\"os\":\"linux\",\"runIn\":\"Server\",\"health\":\"WARNING\",\"cpu\":\"80.00%\",\"mem\":\"70.00%\",\"disk\":\"60.00%\",\"network\":\"50.00%\"},{\"application\":\"fakeservice\",\"service\":\"fakeservice\",\"hostid\":\"testHost\",\"ip\":\"127.0.0.1\",\"os\":\"linux\",\"runIn\":\"Server\",\"health\":\"WARNING\",\"cpu\":\"80.00%\",\"mem\":\"70.00%\",\"disk\":\"60.00%\",\"network\":\"50.00%\"}]}"));
    }

    private HostStat getHostStat(StatLine statLine) {
        HostStat.Builder builder = HostStat.Builder();

        return builder.hostId("testHost")
                .ipAddr("127.0.0.1")
                .osType("linux")
                .health("WARNING")
                .services(newHashSet("fm_active", "fakeservice"))
                .statLines(newHashSet(statLine)).build();
    }

    @Test
    public void getHostDashboard() throws Exception {
        Mockito.when(xHostService.getHostDetail(anyString(), any(Range.class))).thenReturn(getHostDetail());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/serverstats/{hostid}", "testHost")
                .param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"summary\":{\"hostId\":\"fakeHost\",\"ip\":\"127.0.0.1\",\"os\":\"linux\",\"health\":\"NORMAL\"},\"cpuInfo\":{\"info\":\"\",\"time\":[\"17-01-18\"],\"data\":[\"70.00\"]},\"memInfo\":{\"info\":\"\",\"time\":[\"17-01-18\"],\"data\":[\"50.00\"]},\"diskInfo\":{\"info\":\"\",\"time\":[\"17-01-18\"],\"data\":[\"80.00\"]},\"netInfo\":{\"info\":\"\",\"time\":[\"17-01-18\"],\"Dl\":[\"0.10\"],\"Ul\":[\"0.10\"]}}"));
    }

    private XHostDetail getHostDetail() {
        return XHostDetail.Builder()
                .hostId("fakeHost")
                .ipAddr("127.0.0.1")
                .osType("linux")
                .health("NORMAL")
                .cpuMetrics(new XMetricsDouble1("", newArrayList("17-01-18"), newArrayList(0.7)))
                .diskMetrics(new XMetricsDouble1("", newArrayList("17-01-18"), newArrayList(0.8)))
                .memMetrics(new XMetricsDouble1("", newArrayList("17-01-18"), newArrayList(0.5)))
                .netMetrics(new XMetricsDouble2("", newArrayList("17-01-18"), newArrayList(0.1), newArrayList(0.1)))
                .build();
    }

    @Test
    public void getHostList() throws Exception {
        Mockito.when(xHostsService.getXHosts()).thenReturn(newHashSet(XHost.Builder().hostname("testHost").mac("unknow-mac").build()));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/serverlist")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"serverlist\":[{\"fullname\":\"unknow-mac@testHost\",\"simplifiedname\":\"unknow-mac@testHost\"}]}"));
    }

}