package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XCpuDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Set;

/**
 * Created by root on 17-2-10.
 */
public interface XHostCpuDetailService {
    XCpuDetail getCpuDetail(Set<AgentInfo> agents, Range range);
}
