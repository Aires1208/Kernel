package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.junit.Test;

/**
 * Created by root on 2/15/17.
 */
public class CpusDaoTest {

    @Test
    public void testGetCpusTopN() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1483300200000L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult =
                new CpusDao().getCpuRatioTopN(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetCpusByTime() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1483300200000L)
//                from=1487575020779&to=1487582220779
//                .from(487044800000L)
//                .to(2487048400000L)
                .from(1487575020779L)
                .to(1487582220779L)
                .gp(30)
                .build();

        ESQueryResult esQueryResult =
                new CpusDao().getCpuRatioByTime(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetCpuStatics() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1483300200000L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult =
                new CpusDao().getCpuStatics(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }
}