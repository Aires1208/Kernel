package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryCond;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.dao.elasticsearch.FilesDao;
import com.navercorp.pinpoint.web.view.XFSDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Created by root on 17-2-16.
 */
@Service
public class XHostFSServiceImpl implements XHostFSService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;

    @Override
    public XFSDetail getFSDetail(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "no agent find.");

        FilesDao filesDao = new FilesDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult fsStatics = filesDao.getFileStatics(esQueryCond);
            ESQueryResult fsUsage = filesDao.getFilesByTime(esQueryCond);

            if (!CollectionUtils.isEmpty(fsStatics.getEsMetricses()) &&
                    !CollectionUtils.isEmpty(fsUsage.getEsMetricses())) {
                return new XFSDetail(range, fsStatics, fsUsage);
            }
        }

        return null;
    }
}
