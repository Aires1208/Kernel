package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by root on 2/13/17.
 */
public class MemInfoDaoTest {

    @Test
    public void testGetMemInfos() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1487211566079L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult = new MemInfoDao().getMemInfos(esQueryCond);

        System.out.println(esQueryResult);
    }

    @Test
    public void testGetAggMemInfos() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1487302082458L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult = new MemInfoDao().getAggMemInfos(esQueryCond);

        System.out.println(esQueryResult);
    }
    @Test
    public void testTimeParse() throws Exception {
        long acturalTime = new MemInfoDao().timeParse("2017-02-14T01:00:00.000Z");
        System.out.println(acturalTime);



    }
}