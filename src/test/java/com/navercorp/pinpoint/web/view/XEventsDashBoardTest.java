package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.web.vo.*;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 9/25/16.
 */
public class XEventsDashBoardTest {

    @Test
    public void testGetxApplications() throws Exception {

        //given
        List<XApplication> xApplicationList = newArrayList();

        XService xService = new XService("service1", new FakeServiceType());
        xService.setAgentIds(newArrayList("agent1", "agent2"));

        XApplication xApplication = new XApplication("TestAp", newArrayList(xService));
        xApplicationList.add(xApplication);

        //when
        XEventsDashBoard xEventsDashBoard = new XEventsDashBoard(xApplicationList);
        ResultEvent resultEvent = new ResultEvent("appName=ems", 0, 123, 234, "details");
        List<ResultEvent> xEvents = newArrayList(resultEvent);
        xEventsDashBoard.addResultEvents(xEvents);

        //then
        String scattersJson = new ObjectMapper().writeValueAsString(xEventsDashBoard);
        System.out.println(scattersJson);
        System.out.println("end");
    }


}