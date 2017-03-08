package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.junit.Test;

/**
 * Created by root on 2/15/17.
 */
public class FilesDaoTest {

    @Test
    public void testGetFiles_Static() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1483300200000L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult =
                new FilesDao().getFileStatics(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetFiles_topN() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1487302082458L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult =
                new FilesDao().getFileUsedTopN(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetFiles_By_Time() throws Exception {
        ESQueryCond esQueryCond = ESDataFactory.createHourCond();

        ESQueryResult esQueryResult =
                new FilesDao().getFilesByTime(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }
}