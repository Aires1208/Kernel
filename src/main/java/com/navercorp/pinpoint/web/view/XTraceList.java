package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.vo.XTransactionName;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-18.
 */
@JsonSerialize(using = XTraceListSerializer.class)
public class XTraceList {
    List<XTransactionName> transactionNames = newArrayList();

    public List<XTransactionName> getTransactionNames() {
        return transactionNames;
    }

    public XTraceList(List<XTransactionName> transactionNames) {

        this.transactionNames = transactionNames;
    }
}
