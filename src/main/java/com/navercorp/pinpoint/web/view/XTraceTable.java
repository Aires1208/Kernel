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
import com.navercorp.pinpoint.web.filter.TransactionType;

import java.util.List;

@JsonSerialize(using = XTraceTableSerializer.class)
public class XTraceTable {

    private final List<XType> xTypes;

    private final List<XTrace> xTraces;


    public XTraceTable(List<XTrace> xTraces, List<XType> xTypes) {
        if (xTraces == null ) {
            throw new NullPointerException("xTraces must not be null");
        }
        this.xTraces = xTraces;
        this.xTypes = xTypes;
    }

    public List<XTrace> getxTraces() {
        return xTraces;
    }

    public List<XType> getxTypes() {
        return xTypes;
    }

}
