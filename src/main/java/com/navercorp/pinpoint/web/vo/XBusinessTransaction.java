package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.bo.SpanBo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


/**
 * transactionId
 * startTime
 * hasExcetpion
 * elapsed
 */
public class XBusinessTransaction {

    private TransactionId transactionId;
    private long startTime = 0L;

    private List<SpanBo> spanBos = newArrayList();

    private boolean hasExcetpion = false;
    private long elapsed;
    private int errors;

    private String traceName;
    private String agentId;
    private String service;

    public XBusinessTransaction(TransactionId transactionId, List<SpanBo> spanBos) {

        this.transactionId = transactionId;
        this.spanBos = spanBos;
    }

    public XBusinessTransaction build() {
        elapsed = 0L;
        errors = 0;

        for (SpanBo spanBo : spanBos) {
            buildException(spanBo.getErrCode());
            errors += spanBo.getErrCode();
            startTime = spanBo.getStartTime() < startTime ? spanBo.getStartTime() : startTime;
            if (spanBo.isRoot()) {
                elapsed = spanBo.getElapsed();
                startTime = spanBo.getStartTime();

                traceName = spanBo.getRpc();
                agentId = spanBo.getAgentId();
                service = spanBo.getApplicationId();
            }
        }

        return this;
    }

    private void buildException(int errCode) {
        if (hasExcetpion) return;

        if (errCode > 0) {
            hasExcetpion = true;
        }
    }

    public String getTraceName() {
        return traceName;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getService() {
        return service;
    }

    public Integer getErrors() {
        return errors;
    }

    public Integer getCalls() {
        return spanBos.size();
    }

    public boolean hasExcetpion() {
        return hasExcetpion;
    }

    public Long getElapsed() {
        return elapsed;
    }

    public long getStartTime() {
        return startTime;
    }
}
