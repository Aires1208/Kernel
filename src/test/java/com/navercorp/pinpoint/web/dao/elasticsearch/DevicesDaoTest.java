package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.junit.Test;

/**
 * Created by root on 2/13/17.
 */
public class DevicesDaoTest {

    @Test
    public void testGetDevices_by_name() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-active")
                .agentStartTime(1483300200000L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult = new DevicesDao().getDevicesByName(esQueryCond);

        System.out.println("*****************************************");
        System.out.println(esQueryResult);
    }

    @Test
    public void testGetMemInfos_by_time() throws Exception {
        ESQueryCond esQueryCond = new ESQueryCond.ESQueryCondBuild()
                .agentId("fm-active")
                .agentStartTime(1483300200000L)
                .from(487044800000L)
                .to(2487048400000L)
                .gp(5)
                .build();

        ESQueryResult esQueryResult = new DevicesDao().getDevicesByTime(esQueryCond);

        System.out.println(esQueryResult);
    }

    @Test
    public void testTimeParse() throws Exception {
        long acturalTime = new MemInfoDao().timeParse("2017-02-14T01:00:00.000Z");
        System.out.println(acturalTime);



    }
}