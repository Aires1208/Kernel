package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.CpusDao;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryCond;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.view.XCpuDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Created by root on 17-2-10.
 */
@Service
public class XHostCpuDetailServiceImpl implements XHostCpuDetailService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;

    @Override
    public XCpuDetail getCpuDetail(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "agent not found.");
        CpusDao cpusDao = new CpusDao();

        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();


            ESQueryResult cpuStatics = cpusDao.getCpuStatics(esQueryCond);
            ESQueryResult topNCpus = cpusDao.getCpuRatioTopN(esQueryCond);
            ESQueryResult metrics = cpusDao.getCpuRatioByTime(esQueryCond);

            if (!CollectionUtils.isEmpty(cpuStatics.getEsMetricses()) &&
                    !CollectionUtils.isEmpty(topNCpus.getEsMetricses()) &&
                    !CollectionUtils.isEmpty(metrics.getEsMetricses())) {
                return XCpuDetail.Builder()
                        .Range(range)
                        .CpuStatics(cpuStatics)
                        .TopNCpu(topNCpus)
                        .Metrics(metrics).build();
            }
        }

        return null;
    }
}
