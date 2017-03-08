package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XHostService;
import com.navercorp.pinpoint.web.service.XHostsService;
import com.navercorp.pinpoint.web.view.HostStat;
import com.navercorp.pinpoint.web.view.XHostDashBoard;
import com.navercorp.pinpoint.web.view.XHostList;
import com.navercorp.pinpoint.web.view.XHostsDashBoard;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XHost;
import com.navercorp.pinpoint.web.vo.XHostDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class XHostsController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XHostsService xHostsService;

    @Autowired
    private XHostService xHostService;

    @RequestMapping(value = "/serverstats", method = RequestMethod.GET)
    @ResponseBody
    public XHostsDashBoard getXHostDashBoard(@RequestParam("from") long from,
                                             @RequestParam("to") long to) {
        List<HostStat> hostStats = xHostsService.getHostsDashBoard(new Range(from, to));


        logger.debug("/serverstats, ", new Range(from, to));
        return new XHostsDashBoard(hostStats);
    }

    @RequestMapping(value = "/serverstats/{hostid}", method = RequestMethod.GET)
    @ResponseBody
    public XHostDashBoard getXHostDashBoard(@PathVariable("hostid") String hostId,
                                            @RequestParam("from") long from,
                                            @RequestParam("to") long to) {

        XHostDetail xHostDetail = xHostService.getHostDetail(hostId, new Range(from, to));

        logger.debug("/serverstats, ", new Range(from, to));
        return new XHostDashBoard(xHostDetail);

    }

    @RequestMapping(value = "/serverlist", method = RequestMethod.GET)
    @ResponseBody
    public XHostList getHostList() {
        Set<XHost> serverIds = xHostsService.getXHosts();
        return new XHostList(serverIds);
    }
}
