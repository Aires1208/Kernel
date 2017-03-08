package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XTraceTableService;
import com.navercorp.pinpoint.web.view.XTraceTable;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/traceTable")
public class XTracesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XTraceTableService xTraceTableService;

    @RequestMapping(value = "/applications/{application}", method = RequestMethod.GET)
    @ResponseBody
    public XTraceTable getApplication(@PathVariable String application,
                                      @RequestParam("from") long from,
                                      @RequestParam("to") long to,
                                      @RequestParam(value = "command", required = false) String command,
                                      @RequestParam(value = "max", required = false) Long max,
                                      @RequestParam(value = "min", required = false) Long min) {

        XTraceQuery query = new XTraceQuery.Builder()
                .Application(application)
                .From(from)
                .To(to)
                .Command(command)
                .Max(max)
                .Min(min).Build();
        XTraceTable xTraceTable = xTraceTableService.getApplicationTraceTable(query);

        logger.debug("/traceTable, {}, {}", "application", application);

        return xTraceTable;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}", method = RequestMethod.GET)
    @ResponseBody
    public XTraceTable getService(@PathVariable String application,
                                  @PathVariable String service,
                                  @RequestParam("from") long from,
                                  @RequestParam("to") long to,
                                  @RequestParam(value = "command", required = false) String command,
                                  @RequestParam(value = "max", required = false) Long max,
                                  @RequestParam(value = "min", required = false) Long min) {
        XTraceQuery query = new XTraceQuery.Builder()
                .Application(application)
                .Service(service)
                .From(from)
                .To(to)
                .Command(command)
                .Max(max)
                .Min(min).Build();
        XTraceTable xTraceTable = xTraceTableService.getServiceTraceTable(query);

        logger.debug("/traceTable, {}, {}", "service", application);

        return xTraceTable;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}", method = RequestMethod.GET)
    @ResponseBody
    public XTraceTable getInstance(@PathVariable String application,
                                   @PathVariable String service,
                                   @PathVariable String instance,
                                   @RequestParam("from") long from,
                                   @RequestParam("to") long to,
                                   @RequestParam(value = "command", required = false) String command,
                                   @RequestParam(value = "max", required = false) Long max,
                                   @RequestParam(value = "min", required = false) Long min) {
        XTraceQuery query = new XTraceQuery.Builder()
                .Application(application)
                .Service(service)
                .Instance(instance)
                .From(from)
                .To(to)
                .Command(command)
                .Max(max)
                .Min(min).Build();
        XTraceTable xTraceTable = xTraceTableService.getInstanceTraceTable(query);

        logger.debug("/traceTable, {}, {}", "instance", instance);

        return xTraceTable;
    }


}
