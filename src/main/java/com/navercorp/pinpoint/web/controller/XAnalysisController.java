package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.service.*;
import com.navercorp.pinpoint.web.view.*;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class XAnalysisController {

    @Autowired
    private XReportServiceImpl reportService;

    @Autowired
    private XTransactionHealthServiceImpl xTransactionHealthService;

    @Autowired
    private XServicesRiskEventServiceImpl xServicesRiskEventService;

    @Autowired
    private XDBServiceImpl dbService;

    @Autowired
    private XRpcStatisticService xRpcStatisticService;

    @Autowired
    private XRpcScatterPlotServiceImpl xRpcScatterPlotService;

    @RequestMapping(value = "/analystics/OperationReport/ScatterPlot", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getRpcStatisticReportScatterPlot(@RequestParam("appName") String appName,
                                                              @RequestParam("rpc") String rpc,
                                                              @RequestParam("from") long from,
                                                              @RequestParam("to") long to) {
        XTransScatters xTransScatters = xRpcScatterPlotService.getXRpcScatterPlots(appName, rpc, new Range(from, to));
        if (null == xTransScatters) {
            return new ResponseEntity<>("get scatter plot fail!", HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(xTransScatters);
        }
    }

    @RequestMapping(value = "/analystics/OperationReport/ScatterPlotTables", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getRpcStatisticReportScatterPlotTables(@RequestParam("appName") String appName,
                                                                    @RequestParam("rpc") String rpc,
                                                                    @RequestParam("from") long from,
                                                                    @RequestParam("to") long to,
                                                                    @RequestParam("resMin") Long resMin,
                                                                    @RequestParam("resMax") Long resMax) {
        List<XRpcScatterPlotTables> xRpcScatterPlotTables = xRpcScatterPlotService.getXRpcScatterPlotTavlesList(appName, rpc, new Range(from, to), resMin, resMax);
        if (null == xRpcScatterPlotTables) {
            return new ResponseEntity<>("get scatter plot tables fail!", HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(xRpcScatterPlotTables);
        }
    }

    @RequestMapping(value = "/analystics/OperationReport", method = RequestMethod.GET)
    @ResponseBody
    public Result getRpcStatisticReport(@RequestParam(value = "appName", required = false) String appName,
                                        @RequestParam("from") long from,
                                        @RequestParam("to") long to) {

        return xRpcStatisticService.getXRpcStatisticList(appName, new Range(from, to));
    }

    @RequestMapping(value = "/analystics", method = RequestMethod.GET)
    @ResponseBody
    public XAppReport getAppReport(@RequestParam(value = "appName", required = false) String appName,
                                   @RequestParam(value = "topN", required = false) Integer topN,
                                   @RequestParam("from") long from,
                                   @RequestParam("to") long to) {

        return reportService.getAppReport(appName, topN, new Range(from, to));
    }

    @RequestMapping(value = "/transactionHealthEvents", method = RequestMethod.GET)
    @ResponseBody
    public XTransactionEvents getEventList(@RequestParam("appName") String appName,
                                           @RequestParam("from") long from,
                                           @RequestParam("to") long to) {
        XTraceQuery query = new XTraceQuery.Builder().Application(appName).From(from).To(to).Build();

        return xTransactionHealthService.calcTransactionHealthEvents(query);
    }

    @RequestMapping(value = "/serviceCallsEvents", method = RequestMethod.GET)
    @ResponseBody
    public XServiceCallsEvents getServiceCallsInfo(@RequestParam("appName") String appName,
                                                   @RequestParam("from") long from,
                                                   @RequestParam("to") long to) {
        return xServicesRiskEventService.getServiceCallsEvents(appName, new Range(from, to));
    }

    @RequestMapping(value = "/serviceHealthEvents", method = RequestMethod.GET)
    @ResponseBody
    public XServiceHealthEvents getServiceRiskInfo(@RequestParam("appName") String appName,
                                                   @RequestParam("from") long from,
                                                   @RequestParam("to") long to) {
        return xServicesRiskEventService.calcServiceHealthEvents(appName, new Range(from, to));
    }

    @RequestMapping(value = "/dbRiskEvents", method = RequestMethod.GET)
    @ResponseBody
    public XDBRiskEvents getDBRiskInfo(@RequestParam("appName") String appName,
                                       @RequestParam("dbName") String dbName,
                                       @RequestParam("from") long from,
                                       @RequestParam("to") long to) {

        return dbService.getDBRiskEvents(appName, dbName, new Range(from, to));
    }
}
