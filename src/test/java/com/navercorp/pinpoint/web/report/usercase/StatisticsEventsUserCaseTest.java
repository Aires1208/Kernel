package com.navercorp.pinpoint.web.report.usercase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.events.ResultEvent;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by root on 16-9-27.
 */
public class StatisticsEventsUserCaseTest {
    private List<ResultEvent> fakeEvents() {
        return ImmutableList.of(new ResultEvent("app=EMS, service=FM", 10011, 12234556, 0, "detail"), new ResultEvent("app=EMS, service=PM", 10072, 1246434, 0, "detail"));
    }

    @Test
    public void getScore() throws Exception {
        List<ResultEvent> eventList = fakeEvents();

        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventList, 5);

        assertThat(userCase.getScore(), is(60.00));
    }

    @Test
    public void getNormal() throws Exception {
        List<ResultEvent> eventList = fakeEvents();

        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventList, 5);

        assertThat(userCase.getNormal(), is(3));
    }

    @Test
    public void getWarning() throws Exception {
        List<ResultEvent> eventList = fakeEvents();

        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventList, 5);

        assertThat(userCase.getWarning(), is(1));
    }

    @Test
    public void getCritical() throws Exception {
        List<ResultEvent> eventList = fakeEvents();

        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventList, 5);

        assertThat(userCase.getCritical(), is(1));
    }

    @Test
    public void getLevel() throws Exception {
        List<ResultEvent> eventList = fakeEvents();

        StatisticsEventsUserCase userCase = new StatisticsEventsUserCase(eventList, 5);

        assertThat(userCase.getLevel(), is(HealthLevel.CRITICAL));
    }

}