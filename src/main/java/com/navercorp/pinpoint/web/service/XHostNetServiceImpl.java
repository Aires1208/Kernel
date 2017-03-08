package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryCond;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.dao.elasticsearch.NetsDao;
import com.navercorp.pinpoint.web.view.XNetDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Created by root on 17-2-16.
 */
@Service
public class XHostNetServiceImpl implements XHostNetService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;

    @Override
    public XNetDetail getNetDetail(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "agent not found");

        NetsDao netsDao = new NetsDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult netStatics = netsDao.getNetStatics(esQueryCond);
            ESQueryResult netIO = netsDao.getNetsByTime(esQueryCond);

            if (!CollectionUtils.isEmpty(netStatics.getEsMetricses()) &&
                    !CollectionUtils.isEmpty(netIO.getEsMetricses())) {
                return new XNetDetail(range, netStatics, netIO);
            }
        }
        return null;
    }
}
