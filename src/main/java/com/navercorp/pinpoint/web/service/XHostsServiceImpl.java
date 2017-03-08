package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.view.HostStat;
import com.navercorp.pinpoint.web.view.StatLine;
import com.navercorp.pinpoint.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Service
public class XHostsServiceImpl implements XHostsService {
    private static final long SHOULD_SAMPLE_AGENT_STAT_THRESHOLD = 10 * 60 * 1000;
    private static final long TWO_MONTHS = 3600 * 1000 * 24 * 60;
    private static final long DEFAULT_SAMPLE_INTERVAL = 60 * 1000;
    private static final int DEFAULT_SAMPLE_COUNT = 10;

    @Autowired
    private AgentStatDao agentStatDao;

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private AgentInfoService agentInfoService;

    @Autowired
    private XEventService eventService;

    @Override
    public List<HostStat> getHostsDashBoard(Range range) {
        List<String> agentIds = getAgentIds();
        List<XHost> xHosts = buildXHosts(agentIds, range);

        List<HostStat> hostStats = newArrayList();

        hostStats.addAll(xHosts.stream().map(xHost ->
                buildServerStat(xHost, range)).collect(Collectors.toList()));

        return hostStats;
    }

    private HostStat buildServerStat(XHost xHost, Range range) {
        Set<StatLine> statLines = buildStatLines(xHost.getAgentIds(), range);

        StatisticsEventsUserCase eventsUserCase =
                new StatisticsEventsUserCase(eventService.getHostEvents(xHost.getHostId(), range), 0);

        boolean isDocker = false;
        for (AgentInfo agentInfo : xHost.getAgents()) {
            isDocker = isDocker || agentInfo.isDocker();
        }

        return HostStat.Builder().hostId(xHost.getHostId())
                .ipAddr(xHost.getIpAddr())
                .osType(xHost.getOsType())
                .isDocker(isDocker)
                .health(eventsUserCase.getLevel().getDesc())
                .services(xHost.getServices())
                .statLines(statLines).build();
    }

    private Set<StatLine> buildStatLines(Set<String> agentIds, Range range) {
        Set<StatLine> statLines = newHashSet();

        if (range.getRange() <= SHOULD_SAMPLE_AGENT_STAT_THRESHOLD) {
            statLines.addAll(agentIds.stream().filter(agentId ->
                    agentStatDao.agentStatExists(agentId, range)).map(agentId ->
                    buildStatLine(agentStatDao.getAgentStatList(agentId, range))).collect(Collectors.toList()));
        } else {
            getSampledStatLines(agentIds, range.splitRange(DEFAULT_SAMPLE_COUNT, 0), statLines);
        }

        return statLines;
    }

    private void getSampledStatLines(Set<String> agentIds, List<Range> ranges, Set<StatLine> statLines) {
        for (Range splitRange : ranges) {
            Iterator<String> iterator = agentIds.iterator();
            while (iterator.hasNext()) {
                Range sampleRange = splitRange.sample(splitRange.getAvr(), DEFAULT_SAMPLE_INTERVAL);
                String agentId = iterator.next();
                if (agentStatDao.agentStatExists(agentId, sampleRange)) {
                    statLines.add(buildStatLine(agentStatDao.getAgentStatList(agentId, sampleRange)));
                    break;
                }
            }
        }
    }


    private List<XHost> buildXHosts(List<String> agentIds, Range range) {
        Map<XHost.Builder, Set<AgentInfo>> map = newHashMap();
        agentIds.stream().filter(agentId -> agentStatDao.agentStatExists(agentId, range)).forEach(agentId -> {
            AgentInfo agentInfo = agentInfoService.getAgentInfo(agentId, range.getTo());
            if (null != agentInfo) {
                XHost.Builder builder = XHost.Builder().hostname(agentInfo.getHostName())
                        .mac(agentInfo.getMac()).ipAddr(agentInfo.getIp()).osType(agentInfo.getOs());
                if (map.containsKey(builder)) {
                    map.get(builder).add(agentInfo);
                } else {
                    map.put(builder, newHashSet(agentInfo));
                }
            }
        });
        List<XHost> xHosts = newArrayList();
        for (Map.Entry<XHost.Builder, Set<AgentInfo>> entry : map.entrySet()) {
            XHost xHost = entry.getKey().agents(entry.getValue()).build();
            xHosts.add(xHost);
        }
        return xHosts;
    }

    private List<String> getAgentIds() {
        List<Application> applications = applicationIndexDao.selectAllApplicationNames();

        List<String> agents = newArrayList();
        for (Application app : applications) {
            List<String> agentIds = applicationIndexDao.selectAgentIds(app.getName());
            agents.addAll(agentIds);
        }

        return agents;
    }

    private StatLine buildStatLine(List<AgentStat> statList) {
        double cpuUsage = 0;
        double diskUsage = 0;
        double memUsage = 0;
        double netUsage = 0;
        for (AgentStat agentStat : statList) {
            cpuUsage += agentStat.getSystemCpuUsage();
            memUsage += agentStat.getMemUsage();
            diskUsage += agentStat.getDiskUsage();
            netUsage += agentStat.getNetUsage();
        }
        return new StatLine(cpuUsage / (double) statList.size(),
                memUsage / (double) statList.size(),
                diskUsage / (double) statList.size(),
                netUsage / (double) statList.size());
    }

    @Override
    public Set<XHost> getXHosts() {
        List<String> agentIds = getAgentIds();

        long timestamp = System.currentTimeMillis();
        Range defaultRange = new Range(timestamp - TWO_MONTHS, timestamp);

        return newHashSet(buildXHosts(agentIds, defaultRange));
    }
}
