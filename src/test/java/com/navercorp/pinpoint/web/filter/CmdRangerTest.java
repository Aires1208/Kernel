package com.navercorp.pinpoint.web.filter;

import org.junit.Test;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class CmdRangerTest {


    @Test
    public void should_return_false_when_check_cmdCode_126102_in_TRANSACTION_CM()
    {
        //given
        List<CodeRange> codeRangeList = newArrayList(TransactionType.CM_CBC_RANGE,
                TransactionType.CM_MOVESITE_RANGE,TransactionType.CM_OTHER_RANGE);
        int cmdCode = 126102;

        //when
        CmdRange cmdRange = new CmdRange(TransactionType.TRANSACTION_CM, codeRangeList);
        boolean include = cmdRange.include(cmdCode);

        //then
        assertThat(include,is(false));
    }

    @Test
    public void should_return_CM_when_cmdCode_is_461979()
    {
        //given
        int cmdCode = 461979;
        String expectType = "CM";

        //when
        String type = new CmdRanger().getType(cmdCode);

        //then
        assertThat(type,is(expectType) );
    }

    @Test
    public void should_return_expectedRanges_when_type_is_CM()
    {
        //given
        List<CodeRange> expectRanges = newArrayList(TransactionType.CM_CBC_RANGE,
                TransactionType.CM_MOVESITE_RANGE,TransactionType.CM_EMB_RANGE,
                TransactionType.CM_OTHER_RANGE);

        //when
        List<CodeRange> codeRanges = new CmdRanger().getRange("CM");

        //then
        assertThat(codeRanges, is(expectRanges));
    }
}
