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
import com.navercorp.pinpoint.web.vo.XLoadInfo;
import com.navercorp.pinpoint.web.vo.XService;
import com.navercorp.pinpoint.web.vo.XMetrics;
import com.navercorp.pinpoint.web.vo.XServiceTopo;

@JsonSerialize(using = XServiceDashBoardSerializer.class)
public class XServiceDashBoard {

    private final XService xService;
    private final XServiceTopo xServiceTopo;
    private XLoadInfo xLoadInfo;

    public XServiceDashBoard(XService xService, XServiceTopo xServiceTopo,
                             XLoadInfo xLoadInfo) {
        if (xService == null || xServiceTopo == null) {
            throw new NullPointerException("xService orxApplicationTopo  must not be null");
        }
        this.xService = xService;
        this.xServiceTopo = xServiceTopo;
        this.xLoadInfo = xLoadInfo;
    }


    public XServiceTopo getTopo() {
        return xServiceTopo;
    }

    public XService getxService() {
        return xService;
    }

    public XLoadInfo getxLoadInfo() {
        return xLoadInfo;
    }
}
