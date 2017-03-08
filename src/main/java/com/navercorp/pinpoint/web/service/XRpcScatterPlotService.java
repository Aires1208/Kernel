package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XTransScatters;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;


/**
 * Created by ${10183966} on 11/24/16.
 */
public interface XRpcScatterPlotService {

    XTransScatters getXRpcScatterPlots(String applicationName, String rpc, Range range);

    List<XRpcScatterPlotTables> getXRpcScatterPlotTavlesList(String applicationName, String rpc, Range range, Long resMin, Long resMax);
}
