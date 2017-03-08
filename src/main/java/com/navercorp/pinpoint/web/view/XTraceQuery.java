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

import com.navercorp.pinpoint.web.vo.Range;

public class XTraceQuery {

    private String application;
    private String service;
    private String instance;
    private String command;
    private long from;
    private long to;
    private long maxResponse;
    private long minResponse;


    private XTraceQuery(String application, String service, String instance,
                        String command, long from, long to, long maxResponse, long minResponse) {
        this.application = application;
        this.service = service;
        this.instance = instance;
        this.command = command;

        this.from = from;
        this.to = to;
        this.maxResponse = maxResponse;
        this.minResponse = minResponse;
    }

    public String getApplication() {
        return application;
    }

    public Range getRange() {
        return new Range(from,to);
    }

    public String getCommand() {
        return command;
    }

    public String getService() {
        return service;
    }

    public String getInstance() {
        return instance;
    }

    public long getMin() {
        return minResponse;
    }

    public long getMax() {
        return maxResponse;
    }

    public static class Builder{
        private String application;
        private String service;
        private String instance;
        private String command;
        private long from;
        private long to;
        private long maxResponse = -1L;
        private long minResponse = -1L;
        public Builder() {

        }

        public Builder Application(String application) {
            this.application = application;
            return  this;
        }

        public Builder Service(String service) {
            this.service = service;
            return  this;
        }

        public Builder Instance(String instance) {
            this.instance = instance;
            return  this;
        }
        public Builder Command(String command) {
            this.command = command;
            return  this;
        }

        public Builder From(long from) {
            this.from = from;
            return  this;
        }


        public Builder To(long to) {
            this.to = to;
            return  this;
        }

        public Builder Max(Long maxResponse) {
            if(maxResponse != null) {
                this.maxResponse = maxResponse;
            }

            return  this;
        }

        public Builder Min(Long minResponse) {
            if(minResponse != null) {
                this.minResponse = minResponse;
            }

            return  this;
        }

        public XTraceQuery Build() {
            return new XTraceQuery(application, service,instance,
                    command, from, to, maxResponse,minResponse) ;
        }


    }

}
