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

import java.util.List;

@JsonSerialize(using = XApplicationsDashBoardSerializer.class)
public class XApplicationsDashBoard {

    private final List<XApplication> xApplications;

    public XApplicationsDashBoard(List<XApplication> xApplications) {
        if (xApplications == null) {
            throw new NullPointerException("xApplications must not be null");
        }
        this.xApplications = xApplications;
    }

    public List<XApplication> getxApplications() {
        return xApplications;
    }
}
