package com.navercorp.pinpoint.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.view.*;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XHost;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

import static smartsight.elasticsearch.common.collect.Sets.newHashSet;

/**
 * Created by root on 17-2-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})
public class XHostDiskDetailServiceImplTest {

    @Autowired
    private XHostOverViewService hostOverViewService;

    @Autowired
    private XHostsService hostsService;

    @Autowired
    private XHostDiskDetailService hostDiskDetailService;

    @Autowired
    private XHostCpuDetailService hostCpuDetailService;

    @Autowired
    private XHostFSService hostFSService;

    @Autowired
    private XHostNetService hostNetService;

    @Autowired
    private XHostProcessDetailService hostProcessDetailService;

    @Ignore
    @Test
    public void getOverView() throws Exception {
//        http://10.62.100.241:8084/hots/host/52-54-00-48-1E-5B@Esight-80/processInformation.pinpoint?from=1487606400000&to=1487642359263
        Range range = new Range(1487606400000L, 1487642359263L);

        XHostOverView hostOverView = hostOverViewService.getOverView(getAgents("52-54-00-48-1E-5B@Esight-80"), range);

        String overview = new ObjectMapper().writeValueAsString(hostOverView);

        System.out.println(overview);

    }

    @Ignore
    @Test
    public void getProcessDetail() throws Exception {
//        from=1487295031624&to=1487302231624
        Range range = new Range(1487295031624L, 1487302231624L);

//        XProcessDetail processDetail = hostProcessDetailService.getProcessDetail(getAgents("52-54-00-48-1E-5B@Esight-80"), range);

        XProcessDetail processDetail = hostProcessDetailService.getTimedProcesses(getAgents("52-54-00-48-1E-5B@Esight-80"), 1487302231624L);


        String process = new ObjectMapper().writeValueAsString(processDetail);

        System.out.println(process);

    }

    @Ignore
    @Test
    public void getNetDetail() throws Exception {
//        from=1487225806250&to=1487233006250
        Range range = new Range(1487225806250L, 1487233006250L);

        XNetDetail netDetail = hostNetService.getNetDetail(getAgents("52-54-00-48-1E-5B@Esight-80"), range);

        String net = new ObjectMapper().writeValueAsString(netDetail);

        System.out.println(net);
    }

    @Ignore
    @Test
    public void getFSDetail() throws Exception {

        Range range = new Range(1487220595613L, 1487227795613L);

        XFSDetail xfsDetail = hostFSService.getFSDetail(getAgents("52-54-00-48-1E-5B@Esight-80"), range);

        String fsdetail = new ObjectMapper().writeValueAsString(xfsDetail);

        System.out.println(fsdetail);
    }

    @Ignore
    @Test
    public void getDiskDetail() throws Exception {
        Range range = new Range(1487001600000L, 1487174400000L);

        XDiskDetail diskDetail = hostDiskDetailService.getDiskDetail(getAgents("52-54-00-48-1E-5B@Esight-80"), range);

        System.out.println(diskDetail);
    }

    @Ignore
    @Test
    public void getCpuDetail() throws Exception {
        //52-54-00-48-1E-5B@Esight-80&from=1487209638658&to=1487216838658
        XCpuDetail cpuDetail = hostCpuDetailService.getCpuDetail(getAgents("52-54-00-48-1E-5B@Esight-80"), new Range(1487209638658L, 1487216838658L));

        String cpu = new ObjectMapper().writeValueAsString(cpuDetail);

        System.out.println(cpu);
    }

    private Set<AgentInfo> getAgents(String hostId) {
        Set<XHost> hosts = hostsService.getXHosts();
        Preconditions.checkNotNull(!CollectionUtils.isEmpty(hosts), "host list must not be empty.");

        XHost host = findHost(hosts, hostId);

        return null != host && !StringUtils.isEmpty(host.getAgents()) ? host.getAgents() : newHashSet();
    }

    private XHost findHost(Set<XHost> hosts, String hostId) {
        for (XHost host : hosts) {
            if (host.getHostId().equals(hostId) && !host.getAgentIds().isEmpty()) {
                return host;
            }
        }
        return null;
    }
}