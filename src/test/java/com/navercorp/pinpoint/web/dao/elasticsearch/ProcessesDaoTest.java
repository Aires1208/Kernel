package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.junit.Test;

import java.util.List;

/**
 * Created by root on 2/16/17.
 */
public class ProcessesDaoTest {

    @Test
    public void testGetProcess() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1483300200000L)
                .from(482044800000L)
                .to(2487048400000L)
                .gp(60*24*365)
                .build();

        List<ESQueryResult> esQueryResults =
                new ProcessesDao().getProcessesByTime(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResults);
    }

    @Test
    public void testGetTimedProcess() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-agent80")
                .agentStartTime(1483300200000L)
                .from(482044800000L)
                .to(1487302231624L)
                .gp(60*24*365)
                .build();

        List<ESQueryResult> esQueryResults =
                new ProcessesDao().getTimedProcesses(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResults);
    }

}