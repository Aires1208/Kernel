package com.navercorp.pinpoint.web.view;

/**
 * Created by 10183966 on 7/25/16.
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.common.topo.domain.CpuUsage;
import com.navercorp.pinpoint.common.topo.domain.HeapInfo;
import com.navercorp.pinpoint.common.topo.domain.PermGen;
import com.navercorp.pinpoint.common.topo.domain.TransactionsPerSecond;

import java.util.List;

@JsonSerialize(using = XJVMDashBoardSerializer.class)
public class XJVMDashBoard {
    private final HeapInfo heapInfo;
    private final PermGen permGen;
    private final CpuUsage cpuUsage;
    private final TransactionsPerSecond transactionsPerSecond;
    private List<String> jvmArgs;
    private String jvmVersion;
    private String gcTypeName;



    public XJVMDashBoard(HeapInfo heapInfo, PermGen permGen, CpuUsage cpuUsage, TransactionsPerSecond transactionsPerSecond) {
        this.heapInfo = heapInfo;
        this.permGen = permGen;
        this.cpuUsage = cpuUsage;
        this.transactionsPerSecond = transactionsPerSecond;
    }

    public HeapInfo getHeapInfo() {
        return heapInfo;
    }

    public PermGen getPermGen() {
        return permGen;
    }

    public CpuUsage getCpuUsage() {
        return cpuUsage;
    }

    public TransactionsPerSecond getTransactionsPerSecond() {
        return transactionsPerSecond;
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public String getGcTypeName() {
        return gcTypeName;
    }

    public void setGcTypeName(String gcTypeName) {
        this.gcTypeName = gcTypeName;
    }
}
