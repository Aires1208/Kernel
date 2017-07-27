package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.Result;


/**
 * Created by ${aires} on 11/24/16.
 */
public interface XRpcStatisticService {
    Result getXRpcStatisticList(String applicationName, Range range);

}
