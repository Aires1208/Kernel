package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.elasticsearch.DevicesDao;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryCond;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.view.XDiskDetail;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.util.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by root on 17-2-15.
 */
@Service
public class XHostDiskDetailServiceImpl implements XHostDiskDetailService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;

    @Override
    public XDiskDetail getDiskDetail(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "agent not find.");

        DevicesDao devicesDao = new DevicesDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();
            ESQueryResult diskInfo = devicesDao.getDevicesByName(esQueryCond);
            ESQueryResult metrics = devicesDao.getDevicesByTime(esQueryCond);

            if (null != diskInfo && null != metrics) {
                return new XDiskDetail(range, metrics, diskInfo);
            }
        }

        return null;
    }
}
