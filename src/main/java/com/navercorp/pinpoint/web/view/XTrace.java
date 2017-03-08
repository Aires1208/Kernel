package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.common.bo.SpanBo;
import com.navercorp.pinpoint.web.filter.CmdRanger;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 6/12/16.
 */
@JsonSerialize(using = XTraceSerializer.class)
public class XTrace {
    private SpanBo rootSpan;
    private List<SpanBo> traceSpanList = newArrayList();

    public XTrace(List<SpanBo> spanBoList) {
        traceSpanList = spanBoList;
        spanBoList.stream().filter(SpanBo::isRoot).forEach(spanBo -> {
            rootSpan = spanBo;
        });
    }

    public long getStartTime() {
        return rootSpan.getStartTime();
    }

    public String getPath() {
        return rootSpan.getRpc();
    }

    public long getResponseTime() {
        return rootSpan.getElapsed();
    }

    public String getException() {
        //??
        return rootSpan.getExceptionMessage();
    }

    public String getAgentId() {
        return rootSpan.getAgentId();
    }

    public String getClientIp() {
        return rootSpan.getRemoteAddr();
    }

    public String getTransactionId() {
        return rootSpan.getTransactionId();
    }

    public long getErrors() {
        long errors = 0;
        for (SpanBo spanBo : traceSpanList) {
            errors += spanBo.getErrCode();
        }
        return errors;
    }

    public String getType() {
        int cmdCode = StringUtils.isNumeric(getPath()) ? Integer.parseInt(getPath()) : -1;

        return new CmdRanger().getType(cmdCode);
    }

}
