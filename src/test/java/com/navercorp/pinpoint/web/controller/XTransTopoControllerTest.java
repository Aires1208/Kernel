package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XTranxTopoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;

/**
 * Created by root on 17-1-20.
 */
public class XTransTopoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private XTranxTopoServiceImpl tranxTopoService;

    @InjectMocks
    private XTransTopoController controller = new XTransTopoController();

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAppTranxTopo() throws Exception {

    }

    @Test
    public void getServiceTranxTopo() throws Exception {

    }

    @Test
    public void getInstanceTranxTopo() throws Exception {

    }

}