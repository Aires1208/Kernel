package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.web.view.XMemoryDetail;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by root on 17-2-9.
 */
@RunWith(MockitoJUnitRunner.class)
public class XHostMemServiceImplTest {

    @InjectMocks
    private XHostMemoryService memService = new XHostMemoryServiceImpl();

    @Test
    public void getMemInfo() throws Exception {
//        XMemoryDetail memInfo = memService.getMemoryDetail("test", new Range(1484097841886L, 1486603441886L));
//
//        String result = new ObjectMapper().writeValueAsString(memInfo);
//        System.out.println(result);
    }

}