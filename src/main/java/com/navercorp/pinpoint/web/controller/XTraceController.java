package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XTraceDetailServiceImpl;
import com.navercorp.pinpoint.web.view.XTraceDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class XTraceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XTraceDetailServiceImpl xTraceDetailService;


    @RequestMapping(value = "/serviceTraceDetail", method = RequestMethod.GET)
    @ResponseBody
    public XTraceDetail getTraceDetail(@RequestParam("traceId") String traceId,
                                       @RequestParam("startTime") long startTime) {

        XTraceDetail xTraceDetail = xTraceDetailService.getXTraceDetail(traceId, startTime);

        logger.debug("/serviceTraceDetail {}", xTraceDetail);

        return xTraceDetail;
    }
}
