package com.navercorp.pinpoint.web.dao.elasticsearch;

import java.util.Date;

public class ESQueryCond {
    private String agentId;
    private long agentStartTime;
    private long from;
    private long to;
    private int gp;
    private String type;
    private String subType;

    private ESQueryCond(String agentId, long agentStartTime, long from, long to, int gp) {
        this.agentId = agentId;

        this.agentStartTime = agentStartTime;
        this.from = from;
        this.to = to;
        this.gp = gp;
    }


    public String getAgentId() {
        return agentId;
    }

    public long getAgentStartTime() {
        return agentStartTime;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public int getGp() {
        return gp;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("agentId=").append(agentId).append("\n");
        stringBuilder.append("date agentStartTime=").append(new Date(agentStartTime)).append("\n");
        stringBuilder.append("long agentStartTime=").append(agentStartTime).append("\n");
        stringBuilder.append("date from=").append(new Date(from)).append("\n");
        stringBuilder.append("long from=").append(from).append("\n");
        stringBuilder.append("date to=").append(new Date(to)).append("\n");
        stringBuilder.append("long to=").append(to).append("\n");
        stringBuilder.append("gp=").append(gp).append("\n");

        return stringBuilder.toString();

    }

    public static class ESQueryCondBuild {
        private String agentId;
        private long agentStartTime;
        private long from;
        private long to;
        private int gp;


        public ESQueryCondBuild(ESQueryCond esQueryCond) {
            this.agentId = esQueryCond.getAgentId();
            this.agentStartTime = esQueryCond.getAgentStartTime();
            this.from = esQueryCond.getFrom();
            this.to = esQueryCond.getTo();
            this.gp = esQueryCond.getGp();
        }

        public ESQueryCondBuild() {

        }

        public ESQueryCondBuild agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }

        public ESQueryCondBuild agentStartTime(long agentStartTime) {
            this.agentStartTime = agentStartTime;
            return this;
        }

        public ESQueryCondBuild from(long from) {
            this.from = from;
            return this;
        }

        public ESQueryCondBuild to(long to) {
            this.to = to;
            return this;
        }

        public ESQueryCondBuild gp(int gp) {
            this.gp = gp;
            return  this;
        }

        public ESQueryCond build() {
            return new ESQueryCond(agentId,agentStartTime,from,to,gp);
        }
    }
}
