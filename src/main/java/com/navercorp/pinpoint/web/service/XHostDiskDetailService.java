package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XDiskDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Set;

/**
 * Created by root on 17-2-15.
 */
public interface XHostDiskDetailService {
    XDiskDetail getDiskDetail(Set<AgentInfo> agents, Range range);
}
