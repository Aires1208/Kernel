package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryCond;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.dao.elasticsearch.MemInfoDao;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Created by root on 17-2-9.
 */
@Service
public class XHostMemoryServiceImpl implements XHostMemoryService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;

    @Override
    public ESQueryResult getMemoryDetail(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "no agent find.");

        MemInfoDao memInfoDao = new MemInfoDao();
        ESQueryResult queryResult = null;
        for (AgentInfo agent : agents) {
            ESQueryCond queryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();
            queryResult = memInfoDao.getMemInfos(queryCond);

            if (null != queryResult) {
                break;
            }
        }

        return queryResult;
    }
}
