package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XNetDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Set;

/**
 * Created by root on 17-2-16.
 */
public interface XHostNetService {
    XNetDetail getNetDetail(Set<AgentInfo> agents, Range range);
}
