package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.dao.TransactionListDao;
import com.navercorp.pinpoint.web.service.XTransactionServiceImpl;
import com.navercorp.pinpoint.web.view.XTraceList;
import com.navercorp.pinpoint.web.view.XTraceQuery;
import com.navercorp.pinpoint.web.view.XTransactions;
import com.navercorp.pinpoint.web.vo.XTransactionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;


@Controller
@RequestMapping("/transactions")
public class XTransactionsController {

    private static final long DEFAULT_TIME_OBSERVED = 60000 * 60 * 24 * 60;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XTransactionServiceImpl xTransactionService;

    @Autowired
    private TransactionListDao transactionListDao;

    @RequestMapping(value = "/applications/{application}", method = RequestMethod.GET)
    @ResponseBody
    public XTransactions getAppTranscation(@PathVariable String application,
                                           @RequestParam("from") long from,
                                           @RequestParam("to") long to) {
        XTraceQuery query = new XTraceQuery.Builder().Application(application).From(from).To(to).Build();

        XTransactions xTransactions = xTransactionService.getTransactions("application", query);

        logger.debug("/getService, appName:{}", application);

        return xTransactions;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}", method = RequestMethod.GET)
    @ResponseBody
    public XTransactions getSvcTransactions(@PathVariable String application,
                                            @PathVariable String service,
                                            @RequestParam("from") long from,
                                            @RequestParam("to") long to) {
        XTraceQuery query = new XTraceQuery.Builder().Application(application).Service(service).From(from).To(to).Build();

        XTransactions xTransactions = xTransactionService.getTransactions("service", query);

        logger.debug("/getSvcTransactions, appName:{}, serviceName:{}", application, service);

        return xTransactions;
    }

    @RequestMapping(value = "/applications/{application}/services/{service}/instances/{instance}", method = RequestMethod.GET)
    @ResponseBody
    public XTransactions getInstTransaction(@PathVariable String application,
                                            @PathVariable String service,
                                            @PathVariable String instance,
                                            @RequestParam("from") long from,
                                            @RequestParam("to") long to) {
        XTraceQuery query = new XTraceQuery.Builder().Application(application).Service(service).Instance(instance).From(from).To(to).Build();

        XTransactions xTransactions = xTransactionService.getTransactions("instance", query);

        logger.debug("/getInstTransaction, appName:{}, serviceName:{}, instanceName:{}", application, service, instance);

        return xTransactions;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public XTraceList getServerList() {
        List<XTransactionName> transactionNames = transactionListDao.scan();

        List<XTransactionName> traces = newArrayList();
        traces.addAll(transactionNames.stream().filter(name -> System.currentTimeMillis() - name.getLastTime() < DEFAULT_TIME_OBSERVED).collect(Collectors.toList()));
        return new XTraceList(traces);
    }

}
