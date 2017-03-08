package com.navercorp.pinpoint.web.dao.elasticsearch;

/**
 * Created by root on 2/20/17.
 */
public class ESDataFactory {
    public static ESQueryCond createWeekCond() {
        return new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1487302082458L)
                .from(1487120166066L)
                .to(1487638566066L)
                .gp(432)
                .build();
    }

    public static ESQueryCond createHourCond() {
        return new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1487302082458L)
                .from(1487638566066L - 10*60*60*1000)
                .to(1487638566066L + 13*60*60*1000 )
                .gp(60)
                .build();
    }
}
