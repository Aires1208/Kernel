package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by root on 2/15/17.
 */
public class NetsDaoTest {

    @Test
    public void testGetNetTopN() throws Exception {
        ESQueryCond esQueryCond = ESDataFactory.createHourCond();

        ESQueryResult esQueryResult =
                new NetsDao().getNetTopN(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetNetStatics() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1487211566079L)
//                .agentStartTime(1483300200000L)
                .from(487044800000L)
                .to(1497048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult =
                new NetsDao().getNetStatics(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetNet_by_time() throws Exception {
        ESQueryCond esQueryCond = ESDataFactory.createHourCond();

        ESQueryResult esQueryResult =
                new NetsDao().getNetsByTime(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }
}