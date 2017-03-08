package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XFSDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Set;

/**
 * Created by root on 17-2-16.
 */
public interface XHostFSService {
    XFSDetail getFSDetail(Set<AgentInfo> agents, Range range);
}
