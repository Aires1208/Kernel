package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.service.XEventServiceImpl;
import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XEventsDashBoard;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by root on 17-1-18.
 */
public class XEventsControllerTest {
    private MockMvc mockMvc;

    @Mock
    private XEventServiceImpl xEventService;

    @InjectMocks
    private XEventsController eventsController = new XEventsController();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(eventsController).build();
    }

    @Test
    public void getAppEvents() throws Exception {
        Mockito.when(xEventService.getAppEventsDashBoard(anyString(), any(Range.class))).thenReturn(buildXEventDashBoard());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/events/applications/{application}", "fm")
                .accept(MediaType.APPLICATION_JSON)
                .param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"apps\":[{\"name\":\"fm\",\"level\":\"application\",\"services\":[{\"name\":\"fm-active\",\"appName\":\"fm\",\"level\":\"service\",\"instances\":[{\"name\":\"fm-agent\",\"appName\":\"fm\",\"serviceName\":\"fm-active\",\"level\":\"instance\"}]}]}],\"tables\":[{\"objecttype\":\"app\",\"objectdn\":\"dn\",\"starttime\":\"1970-01-01 08:00:00\",\"endtime\":\"1970-01-01 08:00:00\",\"eventname\":\"errors not defined\",\"level\":\"Critical\",\"detail\":\"detail\"}]}"));
    }

    private XEventsDashBoard buildXEventDashBoard() {
        XService xService = new XService("fm-active", ServiceType.SPRING);
        xService.setAgentIds(newArrayList("fm-agent"));
        return new XEventsDashBoard(newArrayList(new XApplication("fm", newArrayList(xService))), newArrayList(new ResultEvent("dn", 11202, 133L, 166L, "detail")));
    }

    @Test
    public void getServiceEvents() throws Exception {
        Mockito.when(xEventService.getServiceEventsDashBoard(anyString(), anyString(), any(Range.class))).thenReturn(buildXEventDashBoard());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/events/applications/{application}/services/{service}", "fm", "fm-active")
                .accept(MediaType.APPLICATION_JSON).param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"apps\":[{\"name\":\"fm\",\"level\":\"application\",\"services\":[{\"name\":\"fm-active\",\"appName\":\"fm\",\"level\":\"service\",\"instances\":[{\"name\":\"fm-agent\",\"appName\":\"fm\",\"serviceName\":\"fm-active\",\"level\":\"instance\"}]}]}],\"tables\":[{\"objecttype\":\"app\",\"objectdn\":\"dn\",\"starttime\":\"1970-01-01 08:00:00\",\"endtime\":\"1970-01-01 08:00:00\",\"eventname\":\"errors not defined\",\"level\":\"Critical\",\"detail\":\"detail\"}]}"));
    }

    @Test
    public void getInstanceEvents() throws Exception {
        Mockito.when(xEventService.getInstanceEventsDashBoard(anyString(), anyString(), anyString(), any(Range.class))).thenReturn(buildXEventDashBoard());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/events/applications/{application}/services/{service}/instances/{instance}", "fm", "fm-active", "fm-agent")
                .accept(MediaType.APPLICATION_JSON)
                .param("from", "1")
                .param("to", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"apps\":[{\"name\":\"fm\",\"level\":\"application\",\"services\":[{\"name\":\"fm-active\",\"appName\":\"fm\",\"level\":\"service\",\"instances\":[{\"name\":\"fm-agent\",\"appName\":\"fm\",\"serviceName\":\"fm-active\",\"level\":\"instance\"}]}]}],\"tables\":[{\"objecttype\":\"app\",\"objectdn\":\"dn\",\"starttime\":\"1970-01-01 08:00:00\",\"endtime\":\"1970-01-01 08:00:00\",\"eventname\":\"errors not defined\",\"level\":\"Critical\",\"detail\":\"detail\"}]}"));
    }

}