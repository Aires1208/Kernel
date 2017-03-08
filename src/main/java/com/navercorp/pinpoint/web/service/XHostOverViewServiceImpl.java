package com.navercorp.pinpoint.web.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.navercorp.pinpoint.web.dao.elasticsearch.*;
import com.navercorp.pinpoint.web.view.XHostOverView;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 17-2-16.
 */
@Service
public class XHostOverViewServiceImpl implements XHostOverViewService {
    private static final int DEFAULT_SAMPLE_COUNT = 20;

    @Override
    public XHostOverView getOverView(Set<AgentInfo> agents, Range range) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(agents), "agent not found.");

        ESQueryResult cpus = getCpus(agents, range);
        ESQueryResult mems = getMems(agents, range);
        ESQueryResult files = getFiles(agents, range);
        ESQueryResult disks = getDisks(agents, range);
        ESQueryResult nets = getNets(agents, range);

        AgentInfo agentInfo = agents.iterator().next();

        return XHostOverView.Builder().Cpus(null != cpus ? cpus : new ESQueryResult("cpu", newArrayList()))
                .Mems(null != mems ? mems : new ESQueryResult("mem", newArrayList()))
                .FileSystems(null != files ? files : new ESQueryResult("files", newArrayList()))
                .Disks(null != disks ? disks : new ESQueryResult("devices", newArrayList()))
                .Nets(null != nets ? nets : new ESQueryResult("nets", newArrayList()))
                .AgentInfo(agentInfo).build();
    }

    private ESQueryResult getNets(Set<AgentInfo> agents, Range range) {
        NetsDao netsDao = new NetsDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult nets = netsDao.getNetTopN(esQueryCond);
            if (!CollectionUtils.isEmpty(nets.getEsMetricses())) {
                return nets;
            }
        }
        return null;
    }

    private ESQueryResult getDisks(Set<AgentInfo> agents, Range range) {
        DevicesDao devicesDao = new DevicesDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult disks = devicesDao.getDevicesByName(esQueryCond);
            if (!CollectionUtils.isEmpty(disks.getEsMetricses())) {
                return disks;
            }
        }
        return null;
    }

    private ESQueryResult getFiles(Set<AgentInfo> agents, Range range) {
        FilesDao filesDao = new FilesDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult files = filesDao.getFileUsedTopN(esQueryCond);
            if (!CollectionUtils.isEmpty(files.getEsMetricses())) {
                return files;
            }
        }
        return null;
    }

    private ESQueryResult getMems(Set<AgentInfo> agents, Range range) {
        MemInfoDao memInfoDao = new MemInfoDao();
        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult mems = memInfoDao.getAggMemInfos(esQueryCond);

            if (!CollectionUtils.isEmpty(mems.getEsMetricses())) {
                return mems;
            }
        }

        return null;
    }

    private ESQueryResult getCpus(Set<AgentInfo> agents, Range range) {
        CpusDao cpusDao = new CpusDao();

        for (AgentInfo agent : agents) {
            ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                    .agentId(agent.getAgentId())
                    .agentStartTime(agent.getStartTimestamp())
                    .from(range.getFrom())
                    .to(range.getTo())
                    .gp(range.getGp(DEFAULT_SAMPLE_COUNT)).build();

            ESQueryResult cpus = cpusDao.getCpuRatioByTime(esQueryCond);
            if (!CollectionUtils.isEmpty(cpus.getEsMetricses())) {
                return cpus;
            }
        }

        return null;
    }
}
