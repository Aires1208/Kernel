package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XTraceDetailServiceImpl;
import com.navercorp.pinpoint.web.view.XTraceDetail;
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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by root on 17-1-18.
 */
public class XTraceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private XTraceDetailServiceImpl xTraceDetailService;

    @InjectMocks
    private XTraceController controller = new XTraceController();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getTraceDetail() throws Exception {
        Mockito.when(xTraceDetailService.getXTraceDetail(anyString(), anyLong())).thenReturn(buildTraceDetail());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/serviceTraceDetail")
                .param("traceId", "test-agent^1357^2")
                .param("startTime", "2345")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private XTraceDetail buildTraceDetail() {
        return null;
    }

}