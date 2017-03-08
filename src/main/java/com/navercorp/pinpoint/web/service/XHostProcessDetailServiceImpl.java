package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESConst;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryCond;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.dao.elasticsearch.ProcessesDao;
import com.navercorp.pinpoint.web.view.XProcessDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 17-2-17.
 */
@Service
public class XHostProcessDetailServiceImpl implements XHostProcessDetailService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;
    private static final long DEFAULT_RANGE_FROM_5_MIN = 5 * 60 * 1000L;

    @Override
    public XProcessDetail getProcessDetail(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "agent not found.");

        ProcessesDao processesDao = new ProcessesDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            List<ESQueryResult> esQueryResults = processesDao.getProcessesByTime(esQueryCond);

            if (!CollectionUtils.isEmpty(esQueryResults)) {
                return XProcessDetail.Builder()
                        .Range(range)
                        .TopNUsage(getTable(esQueryResults, ESConst.PROCESS_CPU_USAGE))
                        .TopNTime(getTable(esQueryResults, ESConst.PROCESS_CPU_TIME))
                        .TopNVirt(getTable(esQueryResults, ESConst.PROCESS_VIRT)).build();
            }
        }

        return null;
    }

    @Override
    public XProcessDetail getTimedProcesses(Set<AgentInfo> agents, long timestamp) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "agent not found.");

        ProcessesDao processesDao = new ProcessesDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(timestamp - DEFAULT_RANGE_FROM_5_MIN)
                    .to(timestamp).build();

            List<ESQueryResult> esQueryResults = processesDao.getTimedProcesses(esQueryCond);

            if (!CollectionUtils.isEmpty(esQueryResults)) {
                return XProcessDetail.Builder()
                        .TopNUsage(getTable(esQueryResults, ESConst.PROCESS_CPU_USAGE))
                        .TopNTime(getTable(esQueryResults, ESConst.PROCESS_CPU_TIME))
                        .TopNVirt(getTable(esQueryResults, ESConst.PROCESS_VIRT)).build();
            }
        }

        return null;
    }

    private ESQueryResult getTable(List<ESQueryResult> esQueryResults, String tableName) {
        for (ESQueryResult esQueryResult : esQueryResults) {
            if (tableName.equals(esQueryResult.getName())) {
                return esQueryResult;
            }
        }
        return new ESQueryResult(tableName, newArrayList());
    }
}
