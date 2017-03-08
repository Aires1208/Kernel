package com.navercorp.pinpoint.web.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by root on 16-9-26.
 */
public class EventUtilsTest {
    @Test
    public void getDesTest() {
        int eventType = 10041;

        String errorName = EventUtils.getDescription(eventType);

        assertThat(errorName, is("errors over baseline"));
    }
}
