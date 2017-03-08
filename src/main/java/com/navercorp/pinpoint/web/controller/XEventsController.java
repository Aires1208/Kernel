package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XEventServiceImpl;
import com.navercorp.pinpoint.web.view.XEventsDashBoard;
import com.navercorp.pinpoint.web.vo.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/events")
public class XEventsController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private XEventServiceImpl xEventService;

    @RequestMapping(value = "/applications/{application}", method = RequestMethod.GET)
    @ResponseBody
    public XEventsDashBoard getAppEvents(@PathVariable String application,
                                         @RequestParam("from") long from,
                                         @RequestParam("to") long to) {
        XEventsDashBoard xEventsDashBoard = xEventService.getAppEventsDashBoard(application, new Range(from, to));
        logger.debug("/getAppEvents, ", xEventsDashBoard);

        return xEventsDashBoard;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}", method = RequestMethod.GET)
    @ResponseBody
    public XEventsDashBoard getServiceEvents(@PathVariable String application,
                                             @PathVariable String service,
                                             @RequestParam("from") long from,
                                             @RequestParam("to") long to) {
        XEventsDashBoard xEventsDashBoard = xEventService.getServiceEventsDashBoard(application, service, new Range(from, to));
        logger.debug("/getServiceEvents, ", xEventsDashBoard);
        return xEventsDashBoard;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}", method = RequestMethod.GET)
    @ResponseBody
    public XEventsDashBoard getInstanceEvents(@PathVariable String application,
                                              @PathVariable String service,
                                              @PathVariable String instance,
                                              @RequestParam("from") long from,
                                              @RequestParam("to") long to) {
        XEventsDashBoard xEventsDashBoard = xEventService.getInstanceEventsDashBoard(application, service, instance, new Range(from, to));
        logger.debug("/getInstanceEvents, ", xEventsDashBoard);

        return xEventsDashBoard;
    }

}
