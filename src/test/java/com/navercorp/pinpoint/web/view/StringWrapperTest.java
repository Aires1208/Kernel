package com.navercorp.pinpoint.web.view;

import org.junit.Test;

import static com.navercorp.pinpoint.web.view.StringWrapper.wrapNumberDouble;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by root on 16-10-8.
 */
public class StringWrapperTest {
    @Test
    public void wrapNumberDoubleTest() throws Exception {
        //given
        double input = 1234453.141592535897;
        double expect = 1234453.14;

        //when
        double output = wrapNumberDouble(input);

        //then
        assertThat(output, is(expect));
        System.out.println(output);
    }

}