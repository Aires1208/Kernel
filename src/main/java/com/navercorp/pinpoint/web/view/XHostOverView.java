package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

/**
 * Created by root on 17-2-16.
 */
@JsonSerialize(using = XHostOverViewSerializer.class)
public class XHostOverView {
    private Range range;
    private AgentInfo agentInfo;
    private ESQueryResult cpus;
    private ESQueryResult mems;
    private ESQueryResult fileSystems;
    private ESQueryResult disks;
    private ESQueryResult nets;

    private XHostOverView(Range range, AgentInfo agentInfo, ESQueryResult cpus, ESQueryResult mems, ESQueryResult fileSystems, ESQueryResult disks, ESQueryResult nets) {
        this.range = range;
        this.agentInfo = agentInfo;
        this.cpus = cpus;
        this.mems = mems;
        this.fileSystems = fileSystems;
        this.disks = disks;
        this.nets = nets;
    }

    public Range getRange() {
        return range;
    }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public ESQueryResult getCpus() {
        return cpus;
    }

    public ESQueryResult getMems() {
        return mems;
    }

    public ESQueryResult getFileSystems() {
        return fileSystems;
    }

    public ESQueryResult getDisks() {
        return disks;
    }

    public ESQueryResult getNets() {
        return nets;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder{
        private Range range;
        private AgentInfo agentInfo;
        private ESQueryResult cpus;
        private ESQueryResult mems;
        private ESQueryResult fileSystems;
        private ESQueryResult disks;
        private ESQueryResult nets;

        public Builder Range(Range range) {
            this.range = range;
            return this;
        }

        public Builder AgentInfo(AgentInfo agentInfo) {
            this.agentInfo = agentInfo;
            return this;
        }

        public Builder Cpus(ESQueryResult cpus) {
            this.cpus = cpus;
            return this;
        }

        public Builder Mems(ESQueryResult mems) {
            this.mems = mems;
            return this;
        }

        public Builder FileSystems(ESQueryResult fileSystems) {
            this.fileSystems = fileSystems;
            return this;
        }

        public Builder Disks(ESQueryResult disks) {
            this.disks = disks;
            return this;
        }

        public Builder Nets(ESQueryResult nets) {
            this.nets = nets;
            return this;
        }

        public XHostOverView build() {
            return new XHostOverView(range, agentInfo, cpus, mems, fileSystems, disks, nets);
        }
    }
}
