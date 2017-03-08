package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.view.XMemoryDetail;

import java.util.Set;

/**
 * Created by root on 17-2-9.
 */
public interface XHostMemoryService {
    ESQueryResult getMemoryDetail(Set<AgentInfo> agents, Range range);
}
