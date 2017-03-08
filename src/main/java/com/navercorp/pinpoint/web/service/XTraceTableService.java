package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XTraceTable;
import com.navercorp.pinpoint.web.view.XTraceQuery;

/**
 * Created by root on 16-11-17.
 */
public interface XTraceTableService {
    XTraceTable getApplicationTraceTable(XTraceQuery query);

    XTraceTable getServiceTraceTable(XTraceQuery query);

    XTraceTable getInstanceTraceTable(XTraceQuery query);
}
