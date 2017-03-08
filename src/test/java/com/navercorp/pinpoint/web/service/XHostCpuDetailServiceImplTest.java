package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XCpuDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by root on 17-2-13.
 */

public class XHostCpuDetailServiceImplTest {

    @Test
    public void getCpuDtail() throws Exception {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setAgentId("fm-agent80");
        agentInfo.setStartTimestamp(1487302082458L);
        Set<AgentInfo> agentInfos = new HashSet<>();
        agentInfos.add(agentInfo);

        Range range = new Range(1487120166066L,1487638566066L);

        XHostCpuDetailServiceImpl xHostCpuDetailService = new XHostCpuDetailServiceImpl();
        XCpuDetail xCpuDetail = xHostCpuDetailService.getCpuDetail(agentInfos,range);

        System.out.println("1. CpuStatics");
//        System.out.println(xCpuDetail.getCpuStatics());
        System.out.println("2. getMetrics");
//        System.out.println(xCpuDetail.getMetrics());
        System.out.println("3. getTop5Cpu");
//        System.out.println(xCpuDetail.getTop5Cpu());

    }

}