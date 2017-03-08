package com.navercorp.pinpoint.web.controller;

import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.web.service.XHostService;
import com.navercorp.pinpoint.web.service.XHostsService;
import com.navercorp.pinpoint.web.view.HostStat;
import com.navercorp.pinpoint.web.view.StatLine;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XHost;
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

import java.util.Iterator;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by root on 17-2-15.
 */
@RunWith(MockitoJUnitRunner.class)
public class HostsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private XHostsService xHostsService;

    @Mock
    private XHostService xHostService;

    @InjectMocks
    private HostsController controller = new HostsController();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getXHostDashBoard() throws Exception {
        StatLine statLine = new StatLine(0.8, 0.7, 0.6, 0.5);
        Mockito.when(xHostsService.getHostsDashBoard(any(Range.class)))
                .thenReturn(newArrayList(getHostStat(statLine)));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/hosts/dashboard")
                .param("from", "1")
                .param("to", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"servers\":[{\"application\":\"fm\",\"service\":\"active\",\"hostid\":\"testHost\",\"ip\":\"127.0.0.1\",\"os\":\"linux\",\"runIn\":\"Server\",\"health\":\"WARNING\",\"cpu\":\"80.00%\",\"mem\":\"70.00%\",\"disk\":\"60.00%\",\"network\":\"50.00%\"},{\"application\":\"fakeservice\",\"service\":\"fakeservice\",\"hostid\":\"testHost\",\"ip\":\"127.0.0.1\",\"os\":\"linux\",\"runIn\":\"Server\",\"health\":\"WARNING\",\"cpu\":\"80.00%\",\"mem\":\"70.00%\",\"disk\":\"60.00%\",\"network\":\"50.00%\"}]}"));
    }

    @Test
    public void getHostList() throws Exception {
        Mockito.when(xHostsService.getXHosts()).thenReturn(newHashSet(XHost.Builder().hostname("testHost").mac("unknow-mac").build()));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/hosts/list")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"serverlist\":[{\"fullname\":\"unknow-mac@testHost\",\"simplifiedname\":\"unknow-mac@testHost\"}]}"));
    }

    @Test
    public void getOverview() throws Exception {

    }

    @Test
    public void getMemoryDetail() throws Exception {
        Mockito.when(xHostsService.getXHosts()).thenReturn(ImmutableSet.of());
    }

    @Test
    public void getCpuDetail() throws Exception {

    }

    @Test
    public void getDiskDetail() throws Exception {

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
}