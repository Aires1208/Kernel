package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XTranxTopoServiceImpl;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XServiceTopo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by root on 16-9-20.
 */
@Controller
@RequestMapping("/tranxTopo")
public class XTransTopoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XTranxTopoServiceImpl tranxTopoService;

    @RequestMapping(value = "/applications/{application}", method = RequestMethod.GET)
    @ResponseBody
    public XServiceTopo getAppTranxTopo(@PathVariable("application") String application,
                                        @RequestParam("command") String command,
                                        @RequestParam("from") long from,
                                        @RequestParam("to") long to) {
        XServiceTopo serviceTopo = tranxTopoService.getAppTranxTopo(application, command, new Range(from, to));
        logger.debug("/getAppTranxTopo", serviceTopo, new Range(from, to));

        return serviceTopo;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}", method = RequestMethod.GET)
    @ResponseBody
    public XServiceTopo getServiceTranxTopo(@PathVariable("application") String application,
                                            @PathVariable("service") String service,
                                            @RequestParam("command") String command,
                                            @RequestParam("from") long from,
                                            @RequestParam("to") long to) {

        return tranxTopoService.getServiceTranxTopo(application, service, command, new Range(from, to));
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}", method = RequestMethod.GET)
    @ResponseBody
    public XServiceTopo getInstanceTranxTopo(@PathVariable("application") String application,
                                             @PathVariable("service") String service,
                                             @PathVariable("instance") String instance,
                                             @RequestParam("command") String command,
                                             @RequestParam("from") long from,
                                             @RequestParam("to") long to) {

        return tranxTopoService.getInstanceTranxTopo(application, service, instance, command, new Range(from, to));
    }
}
