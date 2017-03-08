/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.trace.HistogramSchema;
import com.navercorp.pinpoint.common.trace.SlotType;

import java.util.Map;

/**
 * @author HyunGil Jeong
 */
public class AgentStat {

    public static final int NOT_COLLECTED = -1;

    private final String agentId;
    private final long timestamp;

    private long collectInterval;

    private String gcType;
    private long gcOldCount = NOT_COLLECTED;
    private long gcOldTime = NOT_COLLECTED;
    private long heapUsed = NOT_COLLECTED;
    private long heapMax = NOT_COLLECTED;
    private long nonHeapUsed = NOT_COLLECTED;
    private long nonHeapMax = NOT_COLLECTED;
    private long gcNewCount = NOT_COLLECTED;
    private long gcNewTime = NOT_COLLECTED;
    private double jvmPoolCodeCacheUsed = NOT_COLLECTED;
    private double jvmPoolNewGenUsed = NOT_COLLECTED;
    private double jvmPoolOldGenUsed = NOT_COLLECTED;
    private double jvmPoolSurvivorSpaceUsed = NOT_COLLECTED;
    private double jvmPoolPermGenUsed = NOT_COLLECTED;
    private double jvmPoolMetaspaceUsed = NOT_COLLECTED;

    private long memTotal = NOT_COLLECTED;
    private long memFree = NOT_COLLECTED;
    private long memUsed = NOT_COLLECTED;

    private long diskTotal = NOT_COLLECTED;
    private long diskFree = NOT_COLLECTED;
    private long diskUsed = NOT_COLLECTED;
    private double diskUsage = NOT_COLLECTED;

    private double inSpeed = NOT_COLLECTED;
    private double outSpeed = NOT_COLLECTED;
    private long speed = NOT_COLLECTED;

    private double jvmCpuUsage = NOT_COLLECTED;
    private double systemCpuUsage = NOT_COLLECTED;

    private long sampledNewCount = NOT_COLLECTED;
    private long sampledContinuationCount = NOT_COLLECTED;
    private long unsampledNewCount = NOT_COLLECTED;
    private long unsampledContinuationCount = NOT_COLLECTED;

    private HistogramSchema histogramSchema;
    private Map<SlotType, Integer> activeTraceCounts;

    public AgentStat(String agentId, long timestamp) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (timestamp < 0) {
            throw new NullPointerException("timestamp must not be negative");
        }
        this.agentId = agentId;
        this.timestamp = timestamp;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public long getCollectInterval() {
        return this.collectInterval;
    }

    public void setCollectInterval(long collectInterval) {
        this.collectInterval = collectInterval;
    }

    public String getGcType() {
        return gcType;
    }

    public void setGcType(String gcType) {
        this.gcType = gcType;
    }

    public long getGcOldCount() {
        return gcOldCount;
    }

    public void setGcOldCount(long gcOldCount) {
        this.gcOldCount = gcOldCount;
    }

    public long getGcOldTime() {
        return gcOldTime;
    }

    public void setGcOldTime(long gcOldTime) {
        this.gcOldTime = gcOldTime;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(long heapMax) {
        this.heapMax = heapMax;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public long getNonHeapMax() {
        return nonHeapMax;
    }

    public void setNonHeapMax(long nonHeapMax) {
        this.nonHeapMax = nonHeapMax;
    }

    public long getGcNewCount() {
        return gcNewCount;
    }

    public void setGcNewCount(long gcNewCount) {
        this.gcNewCount = gcNewCount;
    }

    public long getGcNewTime() {
        return gcNewTime;
    }

    public void setGcNewTime(long gcNewTime) {
        this.gcNewTime = gcNewTime;
    }

    public double getJvmPoolCodeCacheUsed() {
        return jvmPoolCodeCacheUsed;
    }

    public void setJvmPoolCodeCacheUsed(double jvmPoolCodeCacheUsed) {
        this.jvmPoolCodeCacheUsed = jvmPoolCodeCacheUsed;
    }

    public double getJvmPoolNewGenUsed() {
        return jvmPoolNewGenUsed;
    }

    public void setJvmPoolNewGenUsed(double jvmPoolNewGenUsed) {
        this.jvmPoolNewGenUsed = jvmPoolNewGenUsed;
    }

    public double getJvmPoolOldGenUsed() {
        return jvmPoolOldGenUsed;
    }

    public void setJvmPoolOldGenUsed(double jvmPoolOldGenUsed) {
        this.jvmPoolOldGenUsed = jvmPoolOldGenUsed;
    }

    public double getJvmPoolSurvivorSpaceUsed() {
        return jvmPoolSurvivorSpaceUsed;
    }

    public void setJvmPoolSurvivorSpaceUsed(double jvmPoolSurvivorSpaceUsed) {
        this.jvmPoolSurvivorSpaceUsed = jvmPoolSurvivorSpaceUsed;
    }

    public double getJvmPoolPermGenUsed() {
        return jvmPoolPermGenUsed;
    }

    public void setJvmPoolPermGenUsed(double jvmPoolPermGenUsed) {
        this.jvmPoolPermGenUsed = jvmPoolPermGenUsed;
    }

    public double getJvmPoolMetaspaceUsed() {
        return jvmPoolMetaspaceUsed;
    }

    public void setJvmPoolMetaspaceUsed(double jvmPoolMetaspaceUsed) {
        this.jvmPoolMetaspaceUsed = jvmPoolMetaspaceUsed;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public long getMemFree() {
        return memFree;
    }

    public void setMemFree(long memFree) {
        this.memFree = memFree;
    }

    public long getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(long memUsed) {
        this.memUsed = memUsed;
    }

    public double getJvmCpuUsage() {
        return jvmCpuUsage;
    }

    public void setJvmCpuUsage(double jvmCpuUsage) {
        this.jvmCpuUsage = jvmCpuUsage;
    }

    public double getSystemCpuUsage() {
        return systemCpuUsage;
    }

    public void setSystemCpuUsage(double systemCpuUsage) {
        this.systemCpuUsage = systemCpuUsage;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }

    public long getDiskFree() {
        return diskFree;
    }

    public void setDiskFree(long diskFree) {
        this.diskFree = diskFree;
    }

    public long getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(long diskUsed) {
        this.diskUsed = diskUsed;
    }

    public double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public double getInSpeed() {
        return inSpeed;
    }

    public void setInSpeed(double inSpeed) {
        this.inSpeed = inSpeed;
    }

    public double getOutSpeed() {
        return outSpeed;
    }

    public void setOutSpeed(double outSpeed) {
        this.outSpeed = outSpeed;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getSampledNewCount() {
        return sampledNewCount;
    }

    public void setSampledNewCount(long sampledNewCount) {
        this.sampledNewCount = sampledNewCount;
    }

    public long getSampledContinuationCount() {
        return sampledContinuationCount;
    }

    public void setSampledContinuationCount(long sampledContinuationCount) {
        this.sampledContinuationCount = sampledContinuationCount;
    }

    public long getUnsampledNewCount() {
        return unsampledNewCount;
    }

    public void setUnsampledNewCount(long unsampledNewCount) {
        this.unsampledNewCount = unsampledNewCount;
    }

    public long getUnsampledContinuationCount() {
        return unsampledContinuationCount;
    }

    public void setUnsampledContinuationCount(long unsampledContinuationCount) {
        this.unsampledContinuationCount = unsampledContinuationCount;
    }

    public HistogramSchema getHistogramSchema() {
        return histogramSchema;
    }

    public void setHistogramSchema(HistogramSchema histogramSchema) {
        this.histogramSchema = histogramSchema;
    }

    public Map<SlotType, Integer> getActiveTraceCounts() {
        return activeTraceCounts;
    }

    public void setActiveTraceCounts(Map<SlotType, Integer> activeTraceCounts) {
        this.activeTraceCounts = activeTraceCounts;
    }

    public double getMemUsage() {
        return getMemTotal() > 0 ? this.getMemUsed()/(double)this.getMemTotal() : 0.00;
    }

    public double getNetUsage() {
        return getSpeed() > 0 ? (getInSpeed() + getOutSpeed()) * 1024 / (double)getSpeed() : 0.00;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AgentStat [");
        sb.append("agentId=" + agentId + ", timestamp=" + timestamp + ", collectInterval=" + collectInterval);
        sb.append(", gcType=" + gcType + ", gcOldCount=" + gcOldCount + ", gcOldTime=" + gcOldTime);
        sb.append(", gcNewCount=" + gcNewCount + ", gcNewTime=" + gcNewTime + ", jvmPoolCodeCacheUsed=" + jvmPoolCodeCacheUsed + ", jvmPoolNewGenUsed=" + jvmPoolNewGenUsed);
        sb.append(", jvmPoolOldGenUsed=" + jvmPoolOldGenUsed + ", jvmPoolSurvivorSpaceUsed=" + jvmPoolSurvivorSpaceUsed);
        sb.append(",jvmPoolPermGenUsed=" + jvmPoolPermGenUsed + ", jvmPoolMetaspaceUsed=" + jvmPoolMetaspaceUsed);
        sb.append(", heapUsed=" + heapUsed + ", heapMax=" + heapMax + ", nonHeapUsed=" + nonHeapUsed);
        sb.append(", nonHeapMax=" + nonHeapMax + ", jvmCpuUsage=" + jvmCpuUsage + ", systemCpuUsage=" + systemCpuUsage);
        sb.append(", memTotal=" + memTotal + ", memFree=" + memFree + ", memUsed=" + memUsed);
        sb.append(", diskTotal=" + diskTotal + ", diskFree=" + diskFree + ", diskUsed=" + diskUsed + ", diskUsage=" + diskUsage);
        sb.append(", inSpeed=" + inSpeed + ", outSpeed" + outSpeed + ", speed=" + speed);
        sb.append(", sampledNewCount=" + sampledNewCount + ", sampledContinuationCount=" + sampledContinuationCount);
        sb.append(", unsampledNewCount=" + unsampledNewCount + ", unsampledContinuationCount=" + unsampledContinuationCount);
        if (histogramSchema != null) {
            sb.append(", histogramSchemaTypeCode=" + histogramSchema.getTypeCode());
        }
        if (activeTraceCounts != null) {
            sb.append(", activeTraceCounts=" + activeTraceCounts);
        }
        sb.append("]");
        return sb.toString();
    }

}
