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

package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.XServiceTopo;
import com.navercorp.pinpoint.web.vo.XMetrics;
import com.navercorp.pinpoint.web.vo.callstacks.RecordSet;

@JsonSerialize(using = XTraceDetailSerializer.class)
public class XTraceDetail {

    private final XServiceTopo xServiceTopo;
    private final XMetrics cpuMetrics;
    private final XMetrics memoryMetrics;
    private final XMetrics storeMetrics;
    private final XMetrics netMetrics;
    private final RecordSet recordSet;

    public XTraceDetail(XServiceTopo xServiceTopo, XMetrics cpuMetrics,
                        XMetrics memoryMetrics, XMetrics storeMetrics, XMetrics netMetrics, RecordSet recordSet) {

        this.xServiceTopo = xServiceTopo;
        this.cpuMetrics = cpuMetrics;
        this.memoryMetrics = memoryMetrics;
        this.storeMetrics = storeMetrics;
        this.netMetrics = netMetrics;
        this.recordSet = recordSet;
    }

    public XServiceTopo getTopo() {
        return xServiceTopo;
    }

    public XMetrics getCpuMetrics() {
        return cpuMetrics;
    }

    public XMetrics getMemoryMetrics() {
        return memoryMetrics;
    }

    public XMetrics getStoreMetrics() {
        return storeMetrics;
    }

    public XMetrics getNetMetrics() {
        return netMetrics;
    }

    public RecordSet getRecordSet() {
        return recordSet;
    }
}
