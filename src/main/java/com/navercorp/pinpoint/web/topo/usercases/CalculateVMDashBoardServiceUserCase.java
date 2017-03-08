package com.navercorp.pinpoint.web.topo.usercases;

import com.navercorp.pinpoint.common.topo.domain.CpuUsage;
import com.navercorp.pinpoint.common.topo.domain.HeapInfo;
import com.navercorp.pinpoint.common.topo.domain.PermGen;
import com.navercorp.pinpoint.common.topo.domain.TransactionsPerSecond;
import com.navercorp.pinpoint.web.view.XJVMDashBoard;
import com.navercorp.pinpoint.web.vo.AgentStat;
import com.navercorp.pinpoint.web.vo.Range;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by 10183966 on 7/26/16.
 */
public class CalculateVMDashBoardServiceUserCase {
    private long interval;
    private final int DEFAULT_NUMBER_SEGMENTS = 49;
    private List<AgentStat> agentStats;
    private Range range;

    public CalculateVMDashBoardServiceUserCase(List<AgentStat> agentStats, Range range) {
        this.agentStats = agentStats;
        this.range = range;
    }

    public XJVMDashBoard getVMDashBoardServiceUserCase() {
        if (null == agentStats || agentStats.size() == 0) {
            return null;
        }
        List<AgentStat> agentStatList = packetAgentStatListByRange(agentStats, segmentationRang());
        XJVMDashBoard XJVMDashBoard = packageObjectByAgentStat(agentStatList);
        return XJVMDashBoard;
    }

    private XJVMDashBoard packageObjectByAgentStat(List<AgentStat> agentStats) {
        int sizeOfAgentStatList = agentStats.size();
        String times[] = new String[sizeOfAgentStatList];
        //HeapInfo
        long heapMaxs[] = new long[sizeOfAgentStatList];
        long heapUseds[] = new long[sizeOfAgentStatList];
        long heapfgcs[] = new long[sizeOfAgentStatList];

        //PermGen
        long permGenMaxs[] = new long[sizeOfAgentStatList];
        long permGenUseds[] = new long[sizeOfAgentStatList];
        long permGenfgcs[] = new long[sizeOfAgentStatList];

        //CPUUsage
        double[] jvmCpuUsage = new double[sizeOfAgentStatList];
        double[] systemCpuUsage = new double[sizeOfAgentStatList];

        //TPS
        long[] sampledNewCount = new long[sizeOfAgentStatList];
        long[] sampledContinuationCount = new long[sizeOfAgentStatList];
        long[] unsampledNewCount = new long[sizeOfAgentStatList];
        long[] unsampledContinuationCount = new long[sizeOfAgentStatList];
        putDataToArrays(agentStats, times, heapMaxs, heapUseds, heapfgcs, permGenMaxs, permGenUseds, permGenfgcs, jvmCpuUsage, systemCpuUsage, sampledNewCount, sampledContinuationCount, unsampledNewCount, unsampledContinuationCount);

        return getXvmDashBoardService(times, heapMaxs, heapUseds, heapfgcs, permGenMaxs, permGenUseds, permGenfgcs, jvmCpuUsage, systemCpuUsage, sampledNewCount, sampledContinuationCount, unsampledNewCount, unsampledContinuationCount);
    }

    private XJVMDashBoard getXvmDashBoardService(String[] times, long[] heapMaxs, long[] heapUseds, long[] heapfgcs, long[] permGenMaxs, long[] permGenUseds, long[] permGenfgcs, double[] jvmCpuUsage, double[] systemCpuUsage, long[] sampledNewCount, long[] sampledContinuationCount, long[] unsampledNewCount, long[] unsampledContinuationCount) {
        HeapInfo heapInfo = new HeapInfo(times, heapMaxs, heapUseds, heapfgcs);
        heapInfo.setInfo(" ");
        PermGen permGen = new PermGen(times, permGenMaxs, permGenUseds, permGenfgcs);
        permGen.setInfo(" ");
        CpuUsage cpuUsage = new CpuUsage(times, jvmCpuUsage, systemCpuUsage);
        cpuUsage.setInfo(" ");
        TransactionsPerSecond transactionsPerSecond = new TransactionsPerSecond(times, sampledNewCount, sampledContinuationCount, unsampledNewCount, unsampledContinuationCount);
        transactionsPerSecond.setInfo(" ");
        return new XJVMDashBoard(heapInfo, permGen, cpuUsage, transactionsPerSecond);
    }

    private void putDataToArrays(List<AgentStat> agentStats, String[] times, long[] heapMaxs, long[] heapUseds, long[] heapfgcs, long[] permGenMaxs, long[] permGenUseds, long[] permGenfgcs, double[] jvmCpuUsage, double[] systemCpuUsage, long[] sampledNewCount, long[] sampledContinuationCount, long[] unsampledNewCount, long[] unsampledContinuationCount) {
        for (int indexOfAgentStats = 0; indexOfAgentStats < agentStats.size(); indexOfAgentStats++) {
            String timeSt = transLongToData(agentStats.get(indexOfAgentStats).getTimestamp());
            times[indexOfAgentStats] = isNotContains(times, timeSt) ? timeSt : transLongToData(agentStats.get(indexOfAgentStats).getTimestamp() + 1000 * 60);

            heapMaxs[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getHeapMax() / (1024 * 1024);
            heapUseds[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getHeapUsed() / (1024 * 1024);
            heapfgcs[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getGcOldTime();

            permGenMaxs[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getNonHeapMax() / (1024 * 1024);
            permGenUseds[indexOfAgentStats] = (long) (agentStats.get(indexOfAgentStats).getJvmPoolPermGenUsed() / (1024 * 1024));
            permGenfgcs[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getGcOldTime();

            jvmCpuUsage[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getJvmCpuUsage() * 100;
            systemCpuUsage[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getSystemCpuUsage() * 100;

            sampledNewCount[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getSampledNewCount();
            sampledContinuationCount[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getSampledContinuationCount();
            unsampledNewCount[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getUnsampledNewCount();
            unsampledContinuationCount[indexOfAgentStats] = agentStats.get(indexOfAgentStats).getUnsampledContinuationCount();
        }
    }

    public static List<AgentStat> packetAgentStatListByRange(List<AgentStat> agentStats, List<Range> ranges) {
        if (agentStats.size() <= ranges.size()) {
            return agentStats;
        }
        List<AgentStat> agentStatLists = newArrayList();
        for (Range range : ranges) {
            List<AgentStat> agentS = newArrayList();
            long begin = range.getFrom();
            long end = range.getTo();
            for (AgentStat agentStat : agentStats) {
                long timestamp = agentStat.getTimestamp();
                if (timestamp <= end || timestamp >= begin) {
                    agentS.add(agentStat);
                }
            }
            AgentStat agentStat = getIntermediateAgentStatByRange(agentS, range);
            agentStatLists.add(agentStat);
        }
        return agentStatLists;
    }

    public static AgentStat getIntermediateAgentStatByRange(List<AgentStat> agentStatList, Range range) {
        if (null == agentStatList || 0 == agentStatList.size()) {
            return null;
        }
        long intermediateTimestamp = (range.getFrom() + range.getTo()) / 2;
        Map<Long, AgentStat> longAgentStatMap = new HashMap<Long, AgentStat>();
        for (AgentStat agentStat : agentStatList) {
            long key = (agentStat.getTimestamp() - intermediateTimestamp) >= 0 ? agentStat.getTimestamp() - intermediateTimestamp : -(agentStat.getTimestamp() - intermediateTimestamp);
            if (null == longAgentStatMap.get(key)) {
                longAgentStatMap.put(key, agentStat);
            } else {
                longAgentStatMap.put(key - 1, agentStat);
            }
        }
        long minKey = Collections.min(longAgentStatMap.keySet());
        return longAgentStatMap.get(minKey);
    }

    public List<Range> segmentationRang() {
        long from = this.range.getFrom();

        long to = this.range.getTo();
        List<Range> rangs = newArrayList();
        long xGroupUnitMillis;
        if (getInterval() <= 0) {
            xGroupUnitMillis = (to - from) / DEFAULT_NUMBER_SEGMENTS;
        } else {
            xGroupUnitMillis = getInterval();
        }
        while (from < to) {
            rangs.add(new Range(from, from + xGroupUnitMillis));
            from += xGroupUnitMillis;
        }
        return rangs;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }


    public Range getRange() {
        return range;
    }

    public String transLongToData(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    private boolean isNotContains(String[] timeS, String time) {
        for (String str : timeS) {
            if (str == time) {
                return false;
            }
        }
        return true;
    }

}
