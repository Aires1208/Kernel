package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.Result;


/**
 * Created by ${10183966} on 11/24/16.
 */
public interface XRpcStatisticService {
    Result getXRpcStatisticList(String applicationName, Range range);

}
