package com.navercorp.pinpoint.web.controller;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.service.*;
import com.navercorp.pinpoint.web.view.*;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static smartsight.elasticsearch.common.collect.Sets.newHashSet;

/**
 * Created by root on 17-2-9.
 */
@Controller
@RequestMapping("/hosts")
public class HostsController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XHostsService xHostsService;

    @Autowired
    private XHostOverViewService hostOverViewService;

    @Autowired
    private XHostMemoryService hostMemoryService;

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

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    @ResponseBody
    public XHostsDashBoard getXHostDashBoard(@RequestParam("from") long from,
                                             @RequestParam("to") long to) {
        List<HostStat> hostStats = xHostsService.getHostsDashBoard(new Range(from, to));


        logger.debug("/host/dashboard, range:{}", new Range(from, to));
        return new XHostsDashBoard(hostStats);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public XHostList getHostList() {
        Set<XHost> serverIds = xHostsService.getXHosts();
        return new XHostList(serverIds);
    }

    @RequestMapping(value = "/host/{hostid}/overview")
    @ResponseBody
    public ResponseEntity<?> getOverview(@PathVariable("hostid") String hostid,
                                         @RequestParam("from") long from,
                                         @RequestParam("to") long to) {

        try {
            XHostOverView hostOverView = hostOverViewService.getOverView(getAgents(hostid), new Range(from, to));

            return ResponseEntity.ok(hostOverView);
        } catch (Exception e) {
            return new ResponseEntity<Object>("get data error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/host/{hostid}/memoryUsage", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getMemoryDetail(@PathVariable("hostid") String hostid,
                                             @RequestParam("from") long from,
                                             @RequestParam("to") long to) {
        try {
            Range range = new Range(from, to);

            ESQueryResult result = hostMemoryService.getMemoryDetail(getAgents(hostid), range);

            XMemoryDetail memoryDetail = new XMemoryDetail(range, result);

            return null != result ? ResponseEntity.ok(memoryDetail) : new ResponseEntity<Object>("no data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("get memory detail error: {}", e);
            return new ResponseEntity<Object>("get memory detail error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/host/{hostid}/aggregateCPUUsage", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCpuDetail(@PathVariable("hostid") String hostid,
                                          @RequestParam("from") long from,
                                          @RequestParam("to") long to) {
        try {
            XCpuDetail cpuDetail = hostCpuDetailService.getCpuDetail(getAgents(hostid), new Range(from, to));
            return null != cpuDetail ? ResponseEntity.ok(cpuDetail) : new ResponseEntity<Object>("no data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("get cpu detail error: {}", e);
            return new ResponseEntity<Object>("get cpu detail error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/host/{hostid}/fileSystem", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getFileSystem(@PathVariable("hostid") String hostid,
                                           @RequestParam("from") long from,
                                           @RequestParam("to") long to) {

        try {
            XFSDetail xfsDetail = hostFSService.getFSDetail(getAgents(hostid), new Range(from, to));
            return null != xfsDetail ? ResponseEntity.ok(xfsDetail) : new ResponseEntity<Object>("no data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("get fileSystem detail error: {}", e);
            return new ResponseEntity<Object>("get fileSystem detail error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/host/{hostid}/disk", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDiskDetail(@PathVariable("hostid") String hostid,
                                           @RequestParam("from") long from,
                                           @RequestParam("to") long to) {

        try {
            XDiskDetail diskDetail = hostDiskDetailService.getDiskDetail(getAgents(hostid), new Range(from, to));

            return null != diskDetail ? ResponseEntity.ok(diskDetail) : new ResponseEntity<Object>("no data find.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("get disk detail error: {}", e);
            return new ResponseEntity<Object>("get disk detail error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/host/{hostid}/networkInterface", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getNetDetail(@PathVariable("hostid") String hostid,
                                          @RequestParam("from") long from,
                                          @RequestParam("to") long to) {

        try {
            XNetDetail netDetail = hostNetService.getNetDetail(getAgents(hostid), new Range(from, to));

            return null != netDetail ? ResponseEntity.ok(netDetail) : new ResponseEntity<Object>("no data find.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("get network detail error: {}", e);
            return new ResponseEntity<Object>("get network detail error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/host/{hostid}/processInformation", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getProcesses(@PathVariable("hostid") String hostid,
                                          @RequestParam(value = "from", required = false) Long from,
                                          @RequestParam("to") long to) {

        try {
            Set<AgentInfo> agents = getAgents(hostid);

            XProcessDetail xProcessDetail = null == from ? hostProcessDetailService.getTimedProcesses(agents, to) :
                    hostProcessDetailService.getProcessDetail(agents, new Range(from, to));

            return null != xProcessDetail ? ResponseEntity.ok(xProcessDetail) : new ResponseEntity<Object>("no data found.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("get processes detail error: {}", e);
            return new ResponseEntity<Object>("get processes detail error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Set<AgentInfo> getAgents(String hostId) {
        Set<XHost> hosts = xHostsService.getXHosts();
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
