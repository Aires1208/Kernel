package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XTransScatterServiceImpl;
import com.navercorp.pinpoint.web.view.XTransScatters;
import com.navercorp.pinpoint.web.vo.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transScatter")
public class XTransScatterController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private XTransScatterServiceImpl xTransScatterService;

    @RequestMapping(value = "/applications/{application}", method = RequestMethod.GET)
    @ResponseBody
    public XTransScatters getApplication(@PathVariable String application,
                                         @RequestParam("from") long from,
                                         @RequestParam("to") long to) {

        XTransScatters xTransScatters = xTransScatterService.getXAppTransScatters(application, new Range(from, to));

        logger.debug("/getApplication, ", application, new Range(from, to));

        return xTransScatters;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}", method = RequestMethod.GET)
    @ResponseBody
    public XTransScatters getService(@PathVariable String application,
                                     @PathVariable String service,
                                     @RequestParam("from") long from,
                                     @RequestParam("to") long to) {
        XTransScatters xTransScatters = xTransScatterService.getXServiceScatters(service, new Range(from, to));

        logger.debug("/getService, ", application, new Range(from, to));

        return xTransScatters;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}", method = RequestMethod.GET)
    @ResponseBody
    public XTransScatters getInstance(@PathVariable String application,
                                      @PathVariable String service,
                                      @PathVariable String instance,
                                      @RequestParam("from") long from,
                                      @RequestParam("to") long to) {
        XTransScatters xTransScatters = xTransScatterService.getInstanceScatters(instance, new Range(from, to));

        logger.debug("/getInstance, ", instance, new Range(from, to));

        return xTransScatters;
    }


}
