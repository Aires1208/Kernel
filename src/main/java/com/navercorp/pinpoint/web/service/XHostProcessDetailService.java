package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XProcessDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Set;

/**
 * Created by root on 17-2-17.
 */
public interface XHostProcessDetailService {
    XProcessDetail getProcessDetail(Set<AgentInfo> agents, Range range);

    XProcessDetail getTimedProcesses(Set<AgentInfo> agents, long timestamp);
}
