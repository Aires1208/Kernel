package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XHostOverView;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.Set;

/**
 * Created by root on 17-2-16.
 */
public interface XHostOverViewService {
    XHostOverView getOverView(Set<AgentInfo> agents, Range range);
}
