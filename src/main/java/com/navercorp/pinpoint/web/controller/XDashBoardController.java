package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.XApplicationsService;
import com.navercorp.pinpoint.web.service.XJVMDashBoardServiceImpl;
import com.navercorp.pinpoint.web.service.XServiceDashBoardServiceImpl;
import com.navercorp.pinpoint.web.service.XServiceDetailServiceImpl;
import com.navercorp.pinpoint.web.view.*;
import com.navercorp.pinpoint.web.vo.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashBoard")
public class XDashBoardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XServiceDashBoardServiceImpl xServiceDashBoardService;

    @Autowired
    private XServiceDetailServiceImpl xServiceDetailService;

    @Autowired
    private XApplicationsService applicationsService;

    @Autowired
    private XJVMDashBoardServiceImpl xjvmDashBoardService;

    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    @ResponseBody
    public XApplicationsDashBoard getApplications(@RequestParam("from") long from,
                                                  @RequestParam("to") long to) {

        XApplicationsDashBoard XApplicationsDashBoard = applicationsService.getXApplicationsDashBoard(new Range(from, to));

        logger.debug("/getApplications {}", XApplicationsDashBoard);

        return XApplicationsDashBoard;
    }

    @RequestMapping(value = "/applications/{application}", method = RequestMethod.GET)
    @ResponseBody
    public XApplicationDashBoard getApplication(@PathVariable String application,
                                                @RequestParam("from") long from,
                                                @RequestParam("to") long to) {

        XApplicationDashBoard xApplicationDashBoard = xServiceDashBoardService.getXApplicationDashBoard(application, new Range(from, to));

        logger.debug("/getApplication {}", xApplicationDashBoard);

        return xApplicationDashBoard;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}", method = RequestMethod.GET)
    @ResponseBody
    public XServiceDashBoard getService(@PathVariable String application,
                                        @PathVariable String service,
                                        @RequestParam("from") long from,
                                        @RequestParam("to") long to) {

        XServiceDashBoard xServiceDashBoard = xServiceDetailService.getXServiceDashBoard(application, service, new Range(from, to));

        logger.debug("/getService {}", xServiceDashBoard);

        return xServiceDashBoard;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}", method = RequestMethod.GET)
    @ResponseBody
    public XInstanceDashBoard getInstance(@PathVariable String application,
                                          @PathVariable String service,
                                          @PathVariable String instance,
                                          @RequestParam("from") long from,
                                          @RequestParam("to") long to) {

        XInstanceDashBoard xInstanceDashBoard = xServiceDetailService.getXInstanceDetail(application, service, instance, new Range(from, to));

        logger.debug("/getInstance {}", xInstanceDashBoard);

        return xInstanceDashBoard;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}/agentids/{agentid}", method = RequestMethod.GET)
    @ResponseBody
    public XJVMDashBoard getInstanceJVM(@PathVariable String application,
                                        @PathVariable String service,
                                        @PathVariable String instance,
                                        @PathVariable String agentid,
                                        @RequestParam("from") long from,
                                        @RequestParam("to") long to) {

        XJVMDashBoard xjvmDashBoard = xjvmDashBoardService.getXJVMDashBoard(agentid, new Range(from, to));

        logger.debug("/getInstanceJVM {}", xjvmDashBoard);

        return xjvmDashBoard;
    }

    @RequestMapping(value = "/fullASI", method = RequestMethod.GET)
    @ResponseBody
    public XASIList getApplications() {

        List<XApplication> appList = applicationsService.getFullAppList();

        logger.debug("get full app list, app: {}", appList);

        return new XASIList(appList);
    }

}
