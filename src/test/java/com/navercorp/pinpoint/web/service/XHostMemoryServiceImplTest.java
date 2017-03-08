package com.navercorp.pinpoint.web.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by root on 17-2-13.
 */

@RunWith(MockitoJUnitRunner.class)
public class XHostMemoryServiceImplTest {

    @InjectMocks
    private XHostMemoryService memoryService = new XHostMemoryServiceImpl();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMemoryDetail() throws Exception {

    }

}