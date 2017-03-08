package com.navercorp.pinpoint.web.service;


import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.dao.AgentStatDao;
import com.navercorp.pinpoint.web.report.usercase.StatisticsEventsUserCase;
import com.navercorp.pinpoint.web.vo.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.view.StringWrapper.DefaultDateStr;

@Service
public class XHostServiceImpl implements XHostService {
    private static final int DEFAULT_NUMBER_SEGMENTS = 10;
    private static final long DEFAULT_RANGE_SEGMENT_ONE_HOUR = 3600 * 1000;
    private static final long DEFAULT_SAMPLE_RANGE_THRESHOLD = 10 * 60 * 1000;
    private static final int DEFAULT_SAMPLE_COUNT = 2;
    private static final long DEFAULT_SAMPLE_INTERVAL = 30 * 1000L;

    @Autowired
    private XHostsService xHostsService;

    @Autowired
    private AgentStatDao agentStatDao;

    @Autowired
    private XEventService eventService;
//
//    public XHostDetail getHostDetailbyAggregation(String hostId, Range range) {
//        Preconditions.checkArgument(hostId != null, new NullPointerException("hostId must not be null."));
//
//        Set<XHost> hosts = xHostsService.getXHosts();
//
//        XHost host = findHost(hosts, hostId);
//        Preconditions.checkNotNull(host != null, "host not found");
//
//        AggregationClient aggregationClient = new AggregationClient(new Configuration());
//
//
//        aggregationClient.
//
//        return null;
//    }

    @Override
    public XHostDetail getHostDetail(String hostId, Range range) {
        Preconditions.checkArgument(hostId != null, new NullPointerException("hostId must not be null."));

        Set<XHost> hosts = xHostsService.getXHosts();

        XHost host = findHost(hosts, hostId);
        Preconditions.checkNotNull(host != null, new IllegalStateException("host not found"));

        List<XHostDetailDot> dots = newArrayList();
        dots.addAll(range.splitRange(DEFAULT_NUMBER_SEGMENTS, 0).stream()
                .map(splitCountRange -> buildDot(host.getAgentIds(), splitCountRange)).collect(Collectors.toList()));

        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventService.getHostEvents(hostId, range), 0);

        return buildHostDetail(dots, host, userCase.getLevel().getDesc());
    }

    private XHostDetail buildHostDetail(List<XHostDetailDot> dots, XHost host, String health) {
        Preconditions.checkArgument(dots.size() > 0, new NullPointerException("Dots count error"));

        List<String> timestamp = newArrayList();
        List<Double> cpuUsage = newArrayList();
        List<Double> memUsage = newArrayList();
        List<Double> diskUsage = newArrayList();
        List<Double> netDLUsage = newArrayList();
        List<Double> netULUsage = newArrayList();

        for (XHostDetailDot dot : dots) {
            timestamp.add(dot.getTimestamp());
            cpuUsage.add(dot.getCpuUsage());
            memUsage.add(dot.getMemUsage());
            diskUsage.add(dot.getDiskUsage());
            netDLUsage.add(dot.getNetDLUsage());
            netULUsage.add(dot.getNetULUsage());
        }

        XMetricsDouble1 cpu = new XMetricsDouble1("", timestamp, cpuUsage);
        XMetricsDouble1 mem = new XMetricsDouble1("", timestamp, memUsage);
        XMetricsDouble1 disk = new XMetricsDouble1("", timestamp, diskUsage);
        XMetricsDouble2 net = new XMetricsDouble2("", timestamp, netDLUsage, netULUsage);
        return XHostDetail.Builder().hostId(host.getHostId()).ipAddr(host.getIpAddr()).osType(host.getOsType()).health(health).
                cpuMetrics(cpu).memMetrics(mem).diskMetrics(disk).netMetrics(net).build();
    }

    private XHostDetailDot buildDot(Set<String> agentIds, Range range) {
        List<XHostDetailDot> dots = newArrayList();

        if (range.getRange() < DEFAULT_SAMPLE_RANGE_THRESHOLD) {
            buildSampledDot(agentIds, range, dots);
        } else {
            List<Range> ranges = range.splitRange(DEFAULT_SAMPLE_COUNT, 0);
            for (Range splitRange : ranges) {
                Range sampledRange = splitRange.sample(splitRange.getAvr(), DEFAULT_SAMPLE_INTERVAL);
                buildSampledDot(agentIds, sampledRange, dots);
            }
        }

        for (Range splitNumRange : range.splitRange(0, DEFAULT_RANGE_SEGMENT_ONE_HOUR)) {
            dots.addAll(agentIds.stream().filter(agentId -> agentStatDao.agentStatExists(agentId, splitNumRange))
                    .map(agentId -> createDot(agentStatDao.getAgentStatList(agentId, splitNumRange))).collect(Collectors.toList()));
        }

        return buildDot0(range.getAvr(), dots);
    }

    private void buildSampledDot(Set<String> agentIds, Range range, List<XHostDetailDot> dots) {
        Iterator<String> agentIdIter = agentIds.iterator();
        while (agentIdIter.hasNext()) {
            String agentId = agentIdIter.next();
            if (agentStatDao.agentStatExists(agentId, range)) {
                dots.add(createDot(agentStatDao.getAgentStatList(agentId, range)));
                break;
            }
        }
    }

    private XHostDetailDot buildDot0(long focusTime, List<XHostDetailDot> dots) {
        String timestamp = DefaultDateStr(focusTime);
        if (dots.size() == 0) {
            return XHostDetailDot.Builder().timestamp(timestamp).build();
        }
        double cpu = 0.00;
        double mem = 0.00;
        double disk = 0.00;
        double netDL = 0.00;
        double netUL = 0.00;
        for (XHostDetailDot detailDot : dots) {
            cpu += detailDot.getCpuUsage();
            mem += detailDot.getMemUsage();
            disk += detailDot.getDiskUsage();
            netDL += detailDot.getNetDLUsage();
            netUL += detailDot.getNetULUsage();
        }
        return XHostDetailDot.Builder().timestamp(timestamp).cpuUsage(cpu / dots.size()).memUsage(mem / dots.size())
                .diskUsage(disk / dots.size()).netDLUsage(netDL / dots.size()).netULUsage(netUL / dots.size()).build();
    }

    private XHostDetailDot createDot(List<AgentStat> agentStats) {
        double dataSize = (double) agentStats.size();
        if (dataSize == 0) {
            return XHostDetailDot.Builder().build();
        }
        double cpu = 0.00;
        double mem = 0.00;
        double disk = 0.00;
        double netDL = 0.00;
        double netUL = 0.00;
        for (AgentStat agentStat : agentStats) {
            cpu += agentStat.getSystemCpuUsage();
            mem += agentStat.getMemUsage();
            disk += agentStat.getDiskUsage();
            netDL += agentStat.getInSpeed();
            netUL += agentStat.getOutSpeed();
        }

        XHostDetailDot.Builder builder = XHostDetailDot.Builder();
        return builder.cpuUsage(cpu / dataSize).memUsage(mem / dataSize).diskUsage(disk / dataSize)
                .netDLUsage(netDL / dataSize).netULUsage(netUL / dataSize).build();
    }

    private XHost findHost(Set<XHost> hosts, String hostId) {
        for (XHost host : hosts) {
            if (host.getHostId().equals(hostId) && !host.getAgentIds().isEmpty()) {
                return host;
            }
        }
        return null;
    }
}
